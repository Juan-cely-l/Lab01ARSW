package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

    // Por defecto: 1 hilo
    public CheckResult checkHost(String ipAddress) {
        return checkHost(ipAddress, 1);
    }

    // Versión con N hilos que devuelve CheckResult
    public CheckResult checkHost(String ipAddress, int numThreads) {
        List<Integer> blackListOccurrences = new LinkedList<>();
        int occurrencesCount = 0;

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        int totalServers = skds.getRegisteredServersCount();

        if (numThreads <= 0) numThreads = 1;
        if (numThreads > totalServers) numThreads = totalServers;

        int segmentSize = totalServers / numThreads;
        int remainder = totalServers % numThreads;

        List<BlacklistSearchThread> threads = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < numThreads; i++) {
            int end = start + segmentSize + (i < remainder ? 1 : 0);
            threads.add(new BlacklistSearchThread(ipAddress, start, end, skds));
            start = end;
        }

        LOG.log(Level.INFO, "Starting parallel check for IP: {0} with {1} threads",
                new Object[]{ipAddress, numThreads});

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, "Thread interrupted", e);
                Thread.currentThread().interrupt();
            }
        });

        int reviewedServers = 0;
        for (BlacklistSearchThread t : threads) {
            blackListOccurrences.addAll(t.getBlackListOccurrences());
            occurrencesCount += t.getOccurrencesCount();
            reviewedServers += (t.getEndIndex() - t.getStartIndex());
        }

        if (occurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipAddress);
            LOG.log(Level.SEVERE, "IP {0} is NOT trustworthy. Found in {1} blacklists.",
                    new Object[]{ipAddress, occurrencesCount});
        } else {
            skds.reportAsTrustworthy(ipAddress);
            LOG.log(Level.INFO, "IP {0} is trustworthy. Found in {1} blacklists.",
                    new Object[]{ipAddress, occurrencesCount});
        }

        LOG.log(Level.INFO, "Blacklist check completed. Reviewed: {0}/{1} servers.",
                new Object[]{reviewedServers, totalServers});

        return new CheckResult(blackListOccurrences, reviewedServers);
    }

    // Clase para encapsular resultados
    public static class CheckResult {
        private final List<Integer> occurrences;
        private final int checkedListsCount;

        public CheckResult(List<Integer> occurrences, int checkedListsCount) {
            this.occurrences = occurrences;
            this.checkedListsCount = checkedListsCount;
        }

        public List<Integer> getOccurrences() { return occurrences; }
        public int getCheckedListsCount() { return checkedListsCount; }
    }
}
