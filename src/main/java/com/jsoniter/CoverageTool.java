package com.jsoniter;

import java.util.*;

public class CoverageTool {
    private final Map<String, List<Runnable>> functionTests;
    private final Map<String, Integer> functionBranchCounts;

    public CoverageTool() {
        this.functionTests = new HashMap<>();
        this.functionBranchCounts = new HashMap<>();
    }

    public void addTest(String functionName, Runnable test, int branchCount) {
        functionTests.computeIfAbsent(functionName, k -> new ArrayList<>()).add(test);
        functionBranchCounts.put(functionName, branchCount);
    }

    public void run() {
        for (Map.Entry<String, List<Runnable>> entry : functionTests.entrySet()) {
            String functionName = entry.getKey();
            CoverageData coverage = CoverageData.getInstance(functionName, functionBranchCounts.get(functionName));

            for (Runnable test : entry.getValue()) {
                test.run(); // 运行测试
            }
        }
    }

    public void printResults() {
        CoverageData.printAllResults();
    }
}

