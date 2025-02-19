package com.jsoniter;

import java.util.*;

/**
 * CoverageData class is responsible for tracking branch execution
 * in instrumented functions. It maintains coverage statistics for each
 * function separately, logs branch execution, and generates coverage reports.
 */
public class CoverageData {
    // A map storing coverage data for each function, indexed by function name.
    private static final Map<String, CoverageData> functionCoverageMap = new HashMap<>();

    private final String functionName;// Name of the function being tracked.
    private final List<Integer> accessSequence = new ArrayList<>();// Ordered list of executed branches.
    private final Map<Integer, Integer> branchCounts = new HashMap<>();// Count of executions per branch.
    private final int totalBranches;// Total number of branches in the function.

    /**
     * Constructor to initialize a CoverageData object for a function.
     *
     * @param functionName  The name of the function being tracked.
     * @param totalBranches The total number of branches in the function.
     */
    public CoverageData(String functionName, int totalBranches) {
        this.functionName = functionName;
        this.totalBranches = totalBranches;
    }

    /**
     * Retrieves the CoverageData instance for a specific function.
     * If it does not exist, a new instance is created.
     *
     * @param functionName  The name of the function.
     * @param totalBranches The total number of branches in the function.
     * @return The CoverageData instance for the function.
     */
    public static CoverageData getInstance(String functionName, int totalBranches) {
        return functionCoverageMap.computeIfAbsent(functionName, k -> new CoverageData(functionName, totalBranches));
    }

    /**
     * Logs the execution of a branch by storing its ID and updating its count.
     *
     * @param type The type of coverage being recorded (e.g., "branch" or "exit").
     * @param id   The unique identifier of the branch being executed.
     * @throws IndexOutOfBoundsException if the branch ID exceeds the defined total.
     */
    public void logBranch(String type, int id) {
        if (id > totalBranches) throw new IndexOutOfBoundsException("Branch ID exceeds limit");
        accessSequence.add(id);
        branchCounts.put(id, branchCounts.getOrDefault(id, 0) + 1);
    }

    /**
     * Prints the coverage report for all instrumented functions.
     */
    public static void printAllResults() {
        System.out.println("==================================================");
        for (CoverageData coverage : functionCoverageMap.values()) {
            coverage.printResults();
        }
        System.out.println("==================================================");
    }

    /**
     * Prints the coverage report for the current function, including:
     * - Execution sequence of branches
     * - Number of executions per branch
     * - Overall branch coverage percentage
     */
    public void printResults() {
        System.out.printf("Coverage Report for Function: %s\n", functionName);
        System.out.println("--------------------------------------------------");
        System.out.println("ACCESS SEQUENCE:");
        accessSequence.forEach(id -> System.out.print(id + " -> "));
        System.out.println("\n--------------------------------------------------");

        System.out.println("TOTAL ACCESSES:");
        int uncoveredBranches = 0;
        for (int i = 1; i <= totalBranches; i++) {
            int count = branchCounts.getOrDefault(i, 0);
            System.out.printf("Branch %d: %d accesses\n", i, count);
            if (count == 0) uncoveredBranches++;
        }

        double coverage = 100.0 * (1 - (double) uncoveredBranches / totalBranches);
        System.out.printf("\nBranch Coverage: %.2f%%\n", coverage);
        System.out.println("--------------------------------------------------");
    }
}

