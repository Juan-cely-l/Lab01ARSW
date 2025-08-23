/*
 * Simple test to verify parallel BlackList search functionality
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

public class SimpleTest {
    
    public static void main(String[] args) {
        HostBlackListsValidator validator = new HostBlackListsValidator();
        
        System.out.println("=== Testing Parallel BlackList Search ===");
        
        // Test with 200.24.34.55 (should be found quickly in first few servers)
        System.out.println("\nTesting IP: 200.24.34.55 (expected: found in first few servers)");
        
        System.out.println("Single thread:");
        long start = System.currentTimeMillis();
        List<Integer> result1 = validator.checkHost("200.24.34.55");
        long time1 = System.currentTimeMillis() - start;
        System.out.println("Time: " + time1 + "ms, Found: " + result1.size() + " occurrences");
        System.out.println("Blacklists: " + result1);
        
        System.out.println("\nWith 4 threads:");
        start = System.currentTimeMillis();
        List<Integer> result2 = validator.checkHost("200.24.34.55", 4);
        long time2 = System.currentTimeMillis() - start;
        System.out.println("Time: " + time2 + "ms, Found: " + result2.size() + " occurrences");
        System.out.println("Blacklists: " + result2);
        
        // Test with 202.24.34.55 (dispersed IP - should check more servers)
        System.out.println("\n\nTesting IP: 202.24.34.55 (expected: dispersed across servers)");
        
        System.out.println("With 1 thread:");
        start = System.currentTimeMillis();
        List<Integer> result3 = validator.checkHost("202.24.34.55", 1);
        long time3 = System.currentTimeMillis() - start;
        System.out.println("Time: " + time3 + "ms, Found: " + result3.size() + " occurrences");
        System.out.println("Blacklists: " + result3);
        
        System.out.println("\nWith 4 threads:");
        start = System.currentTimeMillis();
        List<Integer> result4 = validator.checkHost("202.24.34.55", 4);
        long time4 = System.currentTimeMillis() - start;
        System.out.println("Time: " + time4 + "ms, Found: " + result4.size() + " occurrences");
        System.out.println("Blacklists: " + result4);
        
        System.out.println("\nPerformance improvement: " + 
            String.format("%.2fx", (double)time3 / time4));
    }
}