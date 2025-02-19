package com.jsoniter;

import java.util.*;

/**
 * The CoverageTool class is responsible for managing and executing test cases
 * for instrumented functions to measure branch coverage. It keeps track of
 * the test cases assigned to each function and executes them while collecting
 * coverage data.
 */
public class CoverageTool {
    // A map storing test cases for each function, indexed by function name.
    private final Map<String, List<Runnable>> functionTests;
    // A map storing the total number of branches for each function.
    private final Map<String, Integer> functionBranchCounts;

    /**
     * Constructor initializes the CoverageTool with empty maps for tracking
     * function tests and branch counts.
     */
    public CoverageTool() {
        this.functionTests = new HashMap<>();
        this.functionBranchCounts = new HashMap<>();
    }

    /**
     * Adds a test case for a specific function and records the total number of branches.
     *
     * @param functionName The name of the function being tested.
     * @param test         A Runnable test case that exercises the function.
     * @param branchCount  The total number of branches in the function.
     */
    public void addTest(String functionName, Runnable test, int branchCount) {
        functionTests.computeIfAbsent(functionName, k -> new ArrayList<>()).add(test);
        functionBranchCounts.put(functionName, branchCount);
    }

    /**
     * Executes all registered test cases while tracking branch coverage.
     * For each function, its associated tests are executed, and coverage data is recorded.
     */
    public void run() {
        for (Map.Entry<String, List<Runnable>> entry : functionTests.entrySet()) {
            String functionName = entry.getKey();
            // Retrieve or create coverage tracking instance
            CoverageData coverage = CoverageData.getInstance(functionName, functionBranchCounts.get(functionName));

            // Execute all registered test cases for the function
            for (Runnable test : entry.getValue()) {
                test.run();
            }
        }
    }

    /**
     * Prints the coverage report for all instrumented functions.
     */
    public void printResults() {
        CoverageData.printAllResults();
    }
}

