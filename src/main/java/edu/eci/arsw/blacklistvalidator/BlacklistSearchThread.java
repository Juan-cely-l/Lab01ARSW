package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlacklistSearchThread extends Thread {

    private static final Logger LOG = Logger.getLogger(BlacklistSearchThread.class.getName());

    private final String ipAddress;
    private final int startIndex;
    private final int endIndex;
    private final HostBlacklistsDataSourceFacade datasource;

    private int occurrencesCount = 0;
    private final LinkedList<Integer> blackListOccurrences = new LinkedList<>();

    public BlacklistSearchThread(String ipAddress,
                                 int startIndex,
                                 int endIndex,
                                 HostBlacklistsDataSourceFacade datasource) {
        this.ipAddress = ipAddress;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.datasource = datasource;
    }

    @Override
    public void run() {
        LOG.log(Level.FINE, "Thread scanning range [{0}, {1}) for IP {2}",
                new Object[]{startIndex, endIndex, ipAddress});

        for (int i = startIndex; i < endIndex; i++) {
            if (datasource.isInBlackListServer(i, ipAddress)) {
                occurrencesCount++;
                blackListOccurrences.add(i);
                LOG.log(Level.FINER, "IP {0} found in server {1}",
                        new Object[]{ipAddress, i});
            }
        }

        LOG.log(Level.FINE, "Thread finished range [{0}, {1}), occurrences: {2}",
                new Object[]{startIndex, endIndex, occurrencesCount});
    }

    public int getOccurrencesCount() {
        return occurrencesCount;
    }

    public List<Integer> getBlackListOccurrences() {
        return new LinkedList<>(blackListOccurrences);
    }

    public int getStartIndex() { return startIndex; }
    public int getEndIndex() { return endIndex; }
}
