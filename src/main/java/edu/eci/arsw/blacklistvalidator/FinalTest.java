/*
 * Final test to verify all cases work correctly including non-existing IPs
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

public class FinalTest {
    
    public static void main(String[] args) {
        HostBlackListsValidator validator = new HostBlackListsValidator();
        int cores = Runtime.getRuntime().availableProcessors();
        
        System.out.println("=== Final BlackList Search Test ===");
        System.out.println("Available CPU cores: " + cores);
        System.out.println();
        
        // Test case 1: IP found early (200.24.34.55)
        System.out.println("1. IP 200.24.34.55 (found in first servers):");
        testIP(validator, "200.24.34.55", cores);
        
        // Test case 2: IP found dispersed (202.24.34.55)  
        System.out.println("\n2. IP 202.24.34.55 (dispersed across servers):");
        testIP(validator, "202.24.34.55", cores);
        
        // Test case 3: IP not found (212.24.24.55)
        System.out.println("\n3. IP 212.24.24.55 (not in any blacklist):");
        testIP(validator, "212.24.24.55", cores);
    }
    
    private static void testIP(HostBlackListsValidator validator, String ip, int cores) {
        // Test with 1, cores, and 2*cores threads
        int[] threadCounts = {1, cores, cores * 2};
        
        System.out.println("Threads | Time (ms) | Occurrences | Servers Checked");
        System.out.println("--------|-----------|-------------|----------------");
        
        for (int threads : threadCounts) {
            long start = System.currentTimeMillis();
            List<Integer> result = validator.checkHost(ip, threads);
            long time = System.currentTimeMillis() - start;
            
            System.out.printf("%7d | %9d | %11d | %s%n", 
                threads, time, result.size(), 
                result.size() > 0 ? "Early stop" : "All checked");
        }
    }
}