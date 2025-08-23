/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        int ocurrencesCount=0;
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int checkedListsCount=0;
        
        for (int i=0;i<skds.getRegisteredServersCount() && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            
            if (skds.isInBlackListServer(i, ipaddress)){
                
                blackListOcurrences.add(i);
                
                ocurrencesCount++;
            }
        }
        
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    /**
     * Check the given host's IP address in all the available black lists using N threads,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * @param ipaddress suspicious host's IP address.
     * @param N number of threads to use for the search.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        int totalServers = skds.getRegisteredServersCount();
        
        // Shared variables for thread coordination
        java.util.concurrent.atomic.AtomicInteger globalOccurrences = 
            new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicBoolean shouldStop = 
            new java.util.concurrent.atomic.AtomicBoolean(false);
        
        // Create N threads and divide the search space
        BlacklistSearchThread[] threads = new BlacklistSearchThread[N];
        int serversPerThread = totalServers / N;
        int remainingServers = totalServers % N;
        
        int currentStart = 0;
        for (int i = 0; i < N; i++) {
            int currentEnd = currentStart + serversPerThread;
            // Distribute remaining servers among first threads
            if (i < remainingServers) {
                currentEnd++;
            }
            
            threads[i] = new BlacklistSearchThread(currentStart, currentEnd, ipaddress, 
                globalOccurrences, shouldStop, BLACK_LIST_ALARM_COUNT);
            threads[i].start();
            
            currentStart = currentEnd;
        }
        
        // Wait for all threads to complete and collect results
        int totalOccurrences = 0;
        int totalCheckedLists = 0;
        
        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
                totalOccurrences += threads[i].getOccurrencesCount();
                totalCheckedLists += threads[i].getCheckedListsCount();
                blackListOcurrences.addAll(threads[i].getBlackListOccurrences());
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, "Thread interrupted", e);
            }
        }
        
        // Report host based on total occurrences found
        if (totalOccurrences >= BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{totalCheckedLists, totalServers});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
