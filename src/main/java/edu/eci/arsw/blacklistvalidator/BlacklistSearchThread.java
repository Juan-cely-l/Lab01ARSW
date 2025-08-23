/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread class that searches for an IP address in a segment of blacklist servers
 * with support for early termination when enough occurrences are found
 * @author hcadavid
 */
public class BlacklistSearchThread extends Thread {
    
    private final int startIndex;
    private final int endIndex;
    private final String ipAddress;
    private final HostBlacklistsDataSourceFacade dataSourceFacade;
    private final AtomicInteger globalOccurrences;
    private final AtomicBoolean shouldStop;
    private final int alarmCount;
    
    private List<Integer> blackListOccurrences;
    private int occurrencesCount;
    private int checkedListsCount;
    
    /**
     * Constructor for BlacklistSearchThread
     * @param startIndex Starting index of the server range to search
     * @param endIndex Ending index of the server range to search (exclusive)
     * @param ipAddress IP address to search for
     * @param globalOccurrences Shared counter for total occurrences found
     * @param shouldStop Shared flag to signal when to stop searching
     * @param alarmCount Number of occurrences needed to trigger alarm
     */
    public BlacklistSearchThread(int startIndex, int endIndex, String ipAddress, 
            AtomicInteger globalOccurrences, AtomicBoolean shouldStop, int alarmCount) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.ipAddress = ipAddress;
        this.dataSourceFacade = HostBlacklistsDataSourceFacade.getInstance();
        this.globalOccurrences = globalOccurrences;
        this.shouldStop = shouldStop;
        this.alarmCount = alarmCount;
        this.blackListOccurrences = new LinkedList<>();
        this.occurrencesCount = 0;
        this.checkedListsCount = 0;
    }
    
    @Override
    public void run() {
        for (int i = startIndex; i < endIndex && !shouldStop.get(); i++) {
            checkedListsCount++;
            
            if (dataSourceFacade.isInBlackListServer(i, ipAddress)) {
                blackListOccurrences.add(i);
                occurrencesCount++;
                
                // Update global counter and check if we should stop
                int currentGlobal = globalOccurrences.incrementAndGet();
                if (currentGlobal >= alarmCount) {
                    shouldStop.set(true);
                    break;
                }
            }
        }
    }
    
    /**
     * Get the number of blacklist occurrences found by this thread
     * @return Number of occurrences found
     */
    public int getOccurrencesCount() {
        return occurrencesCount;
    }
    
    /**
     * Get the list of blacklist server numbers where the IP was found
     * @return List of server numbers
     */
    public List<Integer> getBlackListOccurrences() {
        return blackListOccurrences;
    }
    
    /**
     * Get the number of blacklists checked by this thread
     * @return Number of blacklists checked
     */
    public int getCheckedListsCount() {
        return checkedListsCount;
    }
}