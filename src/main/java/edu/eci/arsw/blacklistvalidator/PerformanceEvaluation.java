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
        
        // Different thread configurations as required
        int cores = Runtime.getRuntime().availableProcessors();
        int[] threadCounts = {1, cores, cores * 2, 50, 100};
        
        System.out.println("=== BlackList Search Performance Evaluation ===");
        System.out.println("Available CPU cores: " + cores);
        System.out.println("Thread configurations: 1, " + cores + " (cores), " + 
            (cores * 2) + " (2*cores), 50, 100");
        System.out.println();
        
        // Test with dispersed IP that will show performance differences
        String testIP = "202.24.34.55";
        System.out.println("Testing with IP: " + testIP + " (dispersed across blacklists)");
        System.out.println();
        
        System.out.println("Thread Count | Time (ms) | Speedup | Efficiency | Occurrences");
        System.out.println("-------------|-----------|---------|------------|------------");
        
        long baselineTime = 0;
        
        for (int i = 0; i < threadCounts.length; i++) {
            int threadCount = threadCounts[i];
            
            // Warm up
            if (i == 0) {
                validator.checkHost(testIP, threadCount);
            }
            
            long startTime = System.currentTimeMillis();
            List<Integer> results = validator.checkHost(testIP, threadCount);
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            if (i == 0) {
                baselineTime = executionTime;
            }
            
            double speedup = (double) baselineTime / executionTime;
            double efficiency = speedup / threadCount * 100;
            
            System.out.printf("%12d | %9d | %6.2fx | %9.1f%% | %11d%n", 
                threadCount, executionTime, speedup, efficiency, results.size());
        }
        
        System.out.println();
        System.out.println("Analysis:");
        System.out.println("- Speedup: How many times faster compared to 1 thread");
        System.out.println("- Efficiency: Speedup / Thread count * 100% (ideal = 100%)");
        System.out.println("- Higher efficiency = better utilization of threads");
    }
}