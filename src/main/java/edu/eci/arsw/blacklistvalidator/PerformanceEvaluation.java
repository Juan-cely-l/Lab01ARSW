/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import java.util.List;

/**
 * Performance evaluation class for BlackList Search with different thread counts
 * @author hcadavid
 */
public class PerformanceEvaluation {
    
    public static void main(String[] args) {
        HostBlackListsValidator validator = new HostBlackListsValidator();
        
        // Test IPs: 202.24.34.55 (dispersed), 212.24.24.55 (not found), 200.24.34.55 (early found)
        String[] testIPs = {"202.24.34.55", "212.24.24.55", "200.24.34.55"};
        
        // Different thread configurations
        int cores = Runtime.getRuntime().availableProcessors();
        int[] threadCounts = {1, cores, cores * 2, 50, 100};
        
        System.out.println("=== BlackList Search Performance Evaluation ===");
        System.out.println("Available CPU cores: " + cores);
        System.out.println();
        
        for (String ip : testIPs) {
            System.out.println("Testing IP: " + ip);
            System.out.println("Thread Count | Time (ms) | Occurrences Found");
            System.out.println("-------------|-----------|------------------");
            
            for (int threadCount : threadCounts) {
                long startTime = System.currentTimeMillis();
                List<Integer> results = validator.checkHost(ip, threadCount);
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;
                
                System.out.printf("%12d | %9d | %17d%n", 
                    threadCount, executionTime, results.size());
            }
            
            // Also test single threaded original method for comparison
            long startTime = System.currentTimeMillis();
            List<Integer> results = validator.checkHost(ip);
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            System.out.printf("%12s | %9d | %17d%n", 
                "Original", executionTime, results.size());
            
            System.out.println();
        }
    }
}