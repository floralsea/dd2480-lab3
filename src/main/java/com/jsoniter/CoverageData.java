package com.jsoniter;

import java.util.*;

public class CoverageData {
    private static final Map<String, CoverageData> functionCoverageMap = new HashMap<>();

    private final String functionName;
    private final List<Integer> accessSequence = new ArrayList<>();
    private final Map<Integer, Integer> branchCounts = new HashMap<>();
    private final int totalBranches;

    public CoverageData(String functionName, int totalBranches) {
        this.functionName = functionName;
        this.totalBranches = totalBranches;
    }

    public static CoverageData getInstance(String functionName, int totalBranches) {
        return functionCoverageMap.computeIfAbsent(functionName, k -> new CoverageData(functionName, totalBranches));
    }

    public void logBranch(String type, int id) {
        if (id > totalBranches) throw new IndexOutOfBoundsException("Branch ID exceeds limit");
        accessSequence.add(id);
        branchCounts.put(id, branchCounts.getOrDefault(id, 0) + 1);
    }

    public static void printAllResults() {
        System.out.println("==================================================");
        for (CoverageData coverage : functionCoverageMap.values()) {
            coverage.printResults();
        }
        System.out.println("==================================================");
    }

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

