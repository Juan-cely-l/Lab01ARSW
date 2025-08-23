package edu.eci.arsw.blacklistvalidator;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        HostBlackListsValidator hblv = new HostBlackListsValidator();
        String ipAddress = "202.24.34.55";

        int numCores = Runtime.getRuntime().availableProcessors();
        System.out.println("System has " + numCores + " cores available");

        System.out.println("\n===== BLACKLIST VALIDATION PERFORMANCE TESTS =====");
        System.out.println("IP to test: " + ipAddress);
        System.out.println("Please make sure jVisualVM is running to monitor resources");
        System.out.println("Press Enter before each test to continue...");

        Scanner scanner = new Scanner(System.in);

        waitForEnter(scanner, "Running test with 1 thread");
        runTest(hblv, ipAddress, 1);

        waitForEnter(scanner, "Running test with " + numCores + " threads (# of cores)");
        runTest(hblv, ipAddress, numCores);

        waitForEnter(scanner, "Running test with " + (numCores * 2) + " threads (2x # of cores)");
        runTest(hblv, ipAddress, numCores * 2);

        waitForEnter(scanner, "Running test with 50 threads");
        runTest(hblv, ipAddress, 50);

        waitForEnter(scanner, "Running test with 100 threads");
        runTest(hblv, ipAddress, 100);

        scanner.close();
        System.out.println("\nAll tests completed. Please analyze jVisualVM results.");
    }

    private static void runTest(HostBlackListsValidator validator, String ipAddress, int threadCount) {
        System.out.println("\n----- Test with " + threadCount + " threads -----");
        System.out.println("Starting test...");

        long startTime = System.currentTimeMillis();

        // Recibir objeto CheckResult en vez de List<Integer>
        HostBlackListsValidator.CheckResult result = validator.checkHost(ipAddress, threadCount);

        long executionTime = System.currentTimeMillis() - startTime;

        System.out.println("Execution time: " + executionTime + " ms");
        System.out.println("The host was found in " + result.getOccurrences().size() + " blacklists");
        System.out.println("Blacklists: " + result.getOccurrences());
        System.out.println("Checked lists: " + result.getCheckedListsCount());
    }

    private static void waitForEnter(Scanner scanner, String message) {
        System.out.println("\n>> " + message);
        System.out.print(">> Press Enter to continue...");
        scanner.nextLine();
    }
}
