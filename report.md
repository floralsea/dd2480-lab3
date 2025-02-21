# Report for assignment 3

This is a template for your report. You are free to modify it as needed.
It is not required to use markdown for your report either, but the report
has to be delivered in a standard, cross-platform format.

## Project

Name: jsoniter (json-iterator)

URL: https://github.com/json-iterator/java

jsoniter (json-iterator) is fast and flexible JSON parser available in Java and Go. Base on the developers' test, its Java version could be 3x times faster than jackson/gson/fastjson and Golang version could be more than 6x times faster than standard lib (encoding/json). 

## Onboarding experience

**1. Did it build and run as documented?**

Yes, it build and run as [documented](http://jsoniter.com/java-features.html).

**(a) Did you have to install a lot of additional tools to build the software?**

This tool (jsoniter) has two different versions: Java and Golang, 
since we focus on Java programming language for both the lab and the course, 
we chose to build and run its Java version locally. As documented, we don't have
to install a lot of additional tools to build the software. However, though we just
need to add **_dependency_** to pom.xml in **_Maven_** project then we could use the software,
the developers don't provide very clear instructions for other types of Java projects 
(e.g. Gradle or non Mavan/Gradle project).

**(b) Were those tools well documented?**

Generally, for different **_APIs_** in jsoniter, it's clear and well documented, as described in the 
[document](http://jsoniter.com/java-features.html), they have instructions and examples for how 
to use different types of APIs. And the developers provide both English and Chinese documentation, it's 
especially benefit for our group.

However, for the project itself, it's not clearly documented. 
Specifically, we encountered complication error when we ran tests locally. And it was updated several 
years ago, though we updated dependencies, we still couldn't pass all tests in the project. In addition, 
we have to use a lot external links to checkout their full benchmark.

**(c) Were other components installed automatically by the build script?**

Yes, after updating a few necessary dependencies to match the correct JDK version, running mvn clean install 
automatically downloaded jsoniter and its dependencies. Maven handled the dependency resolution and 
build process without additional manual intervention.

**(d) Did the build conclude automatically without errors?**

The build process completed successfully, as indicated by the **_BUILD SUCCESS_** message. 
However, the test phase encountered failures, with 2 test failures and 148 errors in 705 Tests.

**(e) How well do examples and tests run on your system(s)?**

The tests did not run smoothly, as 148 errors occurred during execution.
The failures may be due to Java version compatibility or internal issues 
in jsoniter. Therefore, further investigation may be needed.

**_2. Do you plan to continue or choose another project?_**

We plan to continue with this project despite the test failures. 
The lab requires us to analyze code complexity, measure coverage, 
and improve testing rather than fixing all existing issues. 
Since we can still run JaCoCo and Lizard to gather relevant data, 
the project remains suitable for our analysis. We will document the test failures 
and consider them as part of our evaluation.

## Complexity

1. What are your results for five complex functions?

Since there are only four active members in our group, we chose four functions, which are presented in the table below. We found that most of the classes integrated with small functions, while functions with high CCNs basically use "switch-case-default" clause, so we chose functions with over 9 CCNs based on ["Code complexity and clean code"](https://www.brandonsavage.net/code-complexity-and-clean-code/), which says functions with CCNs between 8 to 10 are considered as high complexity.

| Function                                      | Location                                             | CCN, lizard | NLOC, lizard  | CCN, manual (Student 1) | CCN, manual (Student 2) |
| --------------------------------------------- | ---------------------------------------------------- | ----------- | ------------- | ----------------------- | ----------------------- |
| readObject(JsonIterator iter)                 | src/main/java/com/jsoniter/IterImplObject.java       | 9           | 32            |    9                   |          9               |
| readObjectCB(JsonIterator iter, JsonIterator.ReadObjectCallback cb, Object attachment)                   | src/main/java/com/jsoniter/IterImplObject.java | 10          | 35                      |     10               |    10        |
| findStringEnd(JsonIterator iter)              | src/main/java/com/jsoniter/IterImplSkip.java         | 10          | 26                      |         10              |  10          |
| skipString(JsonIterator iter)                              | src/main/java/com/jsoniter/IterImplForStreaming.java                                  | 9          | 27                      |      9              |  9          |


   * Did all methods (tools vs. manual count) get the same result?
    Since there are different ways to count cyclomatic complexity, there can be slight discrepancies in counting. If counting by the common and simple way of ```#decisionPoints - 1```, you get the same as the count from Lizard. However, if you take into account early returns in the function, and use a formula such as ```#decisionPoints - #exitPoints + 2```, the CC will be smaller and this could potentially reflect reality better. There are also instances of code using infinite for loops (```for(; ; )```), which could be argued wheither or not they add compexity. The same for a default case in a switch statement. 

   * Are the results clear?
    The CC results are generally clear given that the functions are quite long and complex. There could be an argument made about which method of counting is better in this case, and also how to handle the questions like infinite for loops or default cases.

2. Are the functions just complex, or also long?

    The functions we selected, such as `readObject()` and `readObjectCB()`, exhibit high cyclomatic complexity due to multiple branching conditions (e.g., `switch-case`, `if-else` structures) rather than excessive length. While they are not particularly long in terms of lines of code, their complexity arises from multiple possible execution paths, error handling, and nested conditions. Other functions like `findStringEnd()` and `skipString()` also demonstrate high complexity due to loops and conditional checks, but they are relatively short in size.

3. What is the purpose of the functions?

- [`readObject(JsonIterator iter)`](./src/main/java/com/jsoniter/IterImplObject.java)

    The `readObject()` function is responsible for parsing the next JSON object key from a JsonIterator stream. It processes different token cases to ensure correct JSON syntax, **handling null values**, **extracting field names**, and **verifying that keys** are **followed by a colon** `(:)`. If an **empty object** `{}` is encountered, it returns `null`. The function also detects unexpected tokens and **throws an error** if the input does not conform to valid JSON formatting. 

- [`readObjectCB((JsonIterator iter, JsonIterator.ReadObjectCallback cb, Object attachment) throws IOException )`](./src/main/java/com/jsoniter/IterImplObject.java)
    
    The `readObjectCB` method is similar to `readObject`, but it introduces the use of a callback (cb) to handle the parsed fields.

- [`final static int findStringEnd(JsonIterator iter)`](./src/main/java/com/jsoniter/iterImplSkip.java)

    The `findStringEnd` method returns is used in parsing to find the position in the iterator where the next string ends. It is mostly used in the code in for skipping/jumping over a newly encountered string in the iterator.

- [`skipString(JsonIterator iter)`](./src/main/java/com/jsoniter/IterImplForStreaming.java)
    The `skipString()` method skips over a string in the iterator. It handles escape sequences and processes the string, even if it's loaded incrementally from a stream.

4. Are exceptions taken into account in the given measurements?

    We think JaCoCo does not seem to fully account for `exception-throwing branches` in its branch coverage measurement. Even though exceptions are explicitly thrown in different branches, it appears that JaCoCo treats them differently than regular conditionals (if or switch-case). This is consistent with our later observation where our test cases successfully executed exception paths (verified via debugging), yet JaCoCo still marked them as missed branches. Thus, when using JaCoCo, exception handling paths might need to be manually verified to ensure they are covered, as the tool may not always reflect this in its reports.
   
6. Is the documentation clear w.r.t. all the possible outcomes?

   The documentation of the original functions provides some indication of expected outcomes. However, it does not explicitly detail all possible paths, including edge cases where certain JSON structures could trigger unexpected errors. Additionally, while some assumptions about input structure are implicit in the code, a more explicit explanation of expected inputs, error scenarios, and return values would improve clarity.


## Refactoring

Plan for refactoring complex code:
1. Extract Methods:

    Break down large functions into smaller helper methods with single responsibilities.
    This reduces the number of independent paths in a single function.
    
2. Replace Nested Conditionals with Early Returns:

    If possible, return early from functions to avoid deep nesting. This reduces unnecessary branches.

3. Refactor switch Statements:

    Convert deeply nested switch-case structures into dedicated handler methods. This improves readability and modularity.

Taking `readObject()` as an example:

Before refactoring (CC=9):
```Java
public static final String readObject(JsonIterator iter) throws IOException {
    byte c = IterImpl.nextToken(iter);
    switch (c) {
        case 'n': IterImpl.skipFixedBytes(iter, 3);
            return null;
        case '{':  c = IterImpl.nextToken(iter);
            if (c == '"') {
                iter.unreadByte();
                String field = iter.readString();
                if (IterImpl.nextToken(iter) != ':') {
                    throw iter.reportError("readObject", "expect :");
                }
                return field;
            }
            if (c == '}') { return null;}
            throw iter.reportError("readObject", "expect \" after {");
        case ',':  String field = iter.readString();
            if (IterImpl.nextToken(iter) != ':') {
                throw iter.reportError("readObject", "expect :");
            }
            return field;
        case '}': return null;
        default: throw iter.reportError("readObject", "expect { or , or } or n, but found: " + (char) c);
    }
}
```
After refactoring (CC = 5):
```Java
public static final String readObject(JsonIterator iter) throws IOException {
    byte c = IterImpl.nextToken(iter);
    if (c == 'n') return handleNull(iter);
    if (c == '{') return handleObject(iter);
    if (c == ',') return handleCommaSeparated(iter);
    if (c == '}') return null;
    throw iter.reportError("readObject", "unexpected token: " + (char) c);
}
// Helper functions
private static String handleNull(JsonIterator iter) throws IOException {...}

private static String handleObject(JsonIterator iter) throws IOException {...}

private static String parseKeyValue(JsonIterator iter) throws IOException {...}

private static String handleCommaSeparated(JsonIterator iter) throws IOException {...}
```

Estimated impact of refactoring (lower CC, but other drawbacks?).

Potential drawbacks could be that the refactored code introduces additional function calls, which slightly increases stack usage.
However, this trade-off improves readability and maintainability. Its smaller methods enhance reusability and modularity. Also, small methods
could be easier to test. While it does increase code length.


Carried out refactoring (optional, P+):

The table below shows the original CC counted by JaCoCo and new CC after refactoring:

| Functions       | Refactored by | Old CC | New CC | Reduced by |
|-----------------|---------------|--------|--------|------------|
| readObject()    | Xu Zuo        | 9      | 5      | 44.4%      |
| readObjectCB()  | Wen Biming    | 10     | 3      |  70%       |
| findStringEnd() | Gustav Wallin | 10     | 6      | 40%        |
| skipString()    |Gustav Nordström | 9    | 4      | 55.5%      |

For more implementation details about each function refactoring, please click the link in the following paragraph.

For `readObject()` function, **Xu Zuo** refactored it and reduced the complexity by **44.4%**, which can be found in this 
[commit](https://github.com/floralsea/dd2480-lab3/commit/0551e795428d2a9afeee0460dc757579c558d507) and in issue [#20](https://github.com/floralsea/dd2480-lab3/issues/20). 
Also, we used the same unit test cases (both the original project and new test cases we added) to test whether the refactored code worked, and the 
refactored code worked well and passed all test cases. The original `readObject()` CC is `9`, and the refactored `readObject()` 
by **Xu Zuo** is `5`, since we can't avoid the `switch` clause, we could only reduce it by 4, while this also meets the requirement.

For `readObjectCB` function, **Wen Biming** refactored it and reduced the complexity by **70%**, which can be found in this [commit](https://github.com/floralsea/dd2480-lab3/commit/056f79a31ca6b025fe412cf70b826ee55ffcccfd)

For `findStringEnd()`, **Gustav Wallin** refactored it and reduced complexity by **40%** found in this [commit](https://github.com/floralsea/dd2480-lab3/commit/7ad1ed8a170fcad35a3d660f1e4b8171558a8c43) or by ```git diff 7ad1ed8 67ed156```. The refactored function was also unit tested. The original function's CC is `10`, and the refactored function has CC `6`, as measured by Lizard. The function could be further refactored, but given that its now within a lower and more acceptable CC and the most complex part has been refactored, it was considered sufficient. 

For `skipString()`, **Gustav Nordström** refactored it and reduced the complexity by **55.5%**. It passes the unit tests and the changes can be found in this [commit](https://github.com/floralsea/dd2480-lab3/commit/7be37087f8a196af3c78ebda9c899f11cc43546e). Two new methods were added, `handleEscapedBackslashes()` and `isEscaped()`. The latter method is where the major improvement lies, replacing two if-statements within a loop with a single loop.

## Coverage

### Tools

We used both **_Lizard_** and **_JaCoCo_** for code complexity analysis, and both of them
are well-documented for users, so it's easy to use it following the instructions.

As for Lizard, it's very easy to integrate because it runs as a standalone command-line tool. 
We simply executed it on our project files without modifying the code or build process. 
However, its lack of filtering and fine-grained output control meant that we had to manually 
extract relevant information.

JaCoCo, though the front-end page is simple, it's more powerful, required more effort to 
integrate. Adding it as a dependency in the Maven pom.xml file is straightforward, but 
every change in test code required recompilation and regeneration of the coverage report. 
Running mvn clean test jacoco:report is necessary each time, adding some overhead to our workflow.

### Your own coverage tool

Our custom coverage tool instruments selected functions to track executed branches and exits. 
We introduced manual logging using CoverageData.logBranch(type, id), where type differentiates 
between branch and exit points, and id uniquely identifies each path. The tool collects this 
data during execution and provides a report summarizing which branches were executed.

🔗patch link: https://github.com/floralsea/dd2480-lab3/commit/9415c356d3728cd8a6c47718d1d228f82e4db197

[Here](https://github.com/floralsea/dd2480-lab3/commit/9415c356d3728cd8a6c47718d1d228f82e4db197)
 you can see how our DIY Coverage Tool works. Our custom DIY coverage tool is designed to manually instrument 
functions and track their branch execution during runtime. Unlike JaCoCo, which operates at the bytecode level, 
our tool requires explicit function modifications to log coverage data. Below is a step-by-step explanation of 
how our coverage tool works, based on the CoverageData and CoverageTool classes.

1. Structure of the Coverage Tool

- Our tool consists of two primary components:

  - `CoverageData` (Tracking Execution Data)

    This class is responsible for recording and reporting branch execution details for instrumented functions.
    
    It maintains execution logs per function and computes branch coverage.
   
  - `CoverageTool` (Managing and Running Tests)

    This class manages test cases associated with each instrumented function.

    It executes test cases and collects branch execution data.

2. How Our Coverage Tool Works

- step 1: Instrumenting the Target Functions
  
    Before running tests, we manually insert calls to CoverageData.logBranch(type, id) inside the target functions. 
These log statements record branch execution paths by assigning unique IDs to different branches.
  
    For example, in an instrumented function:
    ```Java
    CoverageData coverage = CoverageData.getInstance("readObject", 9);
    coverage.logBranch("branch", 1);
    ```
  Each function is assigned a name (`"readObject"`) and the total number of `branches (9)`. During execution, the corresponding `branch ID (1, 2, etc.)` is logged whenever a specific branch is executed.

- Step 2: Initializing and Running Tests
  
    We register test cases for the instrumented functions inside CoverageTool. For each function, we specify: `function name`, `A test method` and `The total number of branches`.
      
    For example:
    ```Java
    coverageTool.addTest("readObject", ExperimentFunctionsTest::testReadObject_ValidJson, 11);
    ```
    This ensures that when `testReadObject_ValidJson()` is executed, the coverage data for `readObject` is recorded.
        
- Step 3: Executing Tests and Capturing Coverage
  
    When we invoke:
    ```Java
    coverageTool.run();
    ```
  The tool retrieves all registered test functions. Each test function is executed, causing the instrumented function to run.
  During execution, `CoverageData.logBranch()` records which branches were reached. Then the collected data is stored in memory for reporting.

- Step 4: Generating a Coverage Report

  After running all test cases, we generate a report using: `coverageTool.printResults();` This internally calls CoverageData.printAllResults(), 
  which prints the sequence of executed branches, displays how many times each branch was accessed and calculates and prints the overall branch coverage percentage.
  Example could be found in this [commit](https://github.com/floralsea/dd2480-lab3/commit/9415c356d3728cd8a6c47718d1d228f82e4db197)

  
What kinds of constructs does your tool support, and how accurate is
its output?

Our coverage tool supports branch coverage by manually instrumenting functions. 
Specifically, it logs execution paths inside conditionals (`if`, `switch`, `while`, `for`) by inserting logBranch() 
calls at key decision points. However, Our DIY coverage tool currently does not explicitly account for `ternary operators` 
`(condition ? yes : no)` and partially tracks `exceptions`. Our tool allows us to track several key points, such as `Branch execution`, 
`Exit paths` and `Execution sequences`.

Considering the **accuracy**, since we manually insert logBranch() calls, every branch execution is explicitly recorded. 
While if a branch is missed due to incorrect instrumentation, it may not be detected. Another drawback is unlike JaCoCo, 
which analyzes all bytecode paths, our tool only tracks the branches where we manually insert logging.

### Evaluation

1. How detailed is your coverage measurement?

    Our tool provides detailed insights into branch execution within a function. It tracks which branches were executed and how many times.
    It stores an execution sequence for debugging and computes branch coverage percentage (executed branches vs. total branches).
    Also, it provides function-level reports, allowing fine-grained analysis. However, it couldn't track statement or line coverage like JaCoCo and 
    automatically detect missing branches without explicit logging.

2. What are the limitations of your own tool?

    Potential limitations could be: manual instrumentation required, only tracks functions that are explicitly instrumented,
    no graphical reports/front-end page, measure branch coverage but not statement or method coverage, and potential for human error.

3. Are the results of your tool consistent with existing coverage tools?

    Compared with JaCoCo, our results are basically consistent with JaCoCo in terms of branch execution tracking.
    While JaCoCo has a drawback, which can be found in this [issue](https://github.com/jacoco/jacoco/issues/1003), for `throw exception` clause,
    JaCoCo can't detect/count it unless we add additional code like `System.out.println()`. This is a benefit of our tool, since we can track
    `Exceptions`. Also, our tool provides a sequence of executed branches, while JaCoCo does not. However, JaCoCo sometimes detects uncovered 
    branches that our tool misses due to human error in instrumentation.

    Lizard is simpler than JaCoCo based on our experience. It calculates cyclomatic complexity (CC) but does not track execution paths.
    Our tool is execution-based, whereas Lizard is static analysis-based.

## Coverage improvement

Since our chosen project used a lot `throw Exceptions` clauses, we have to state this JaCoCo related [issue](https://github.com/jacoco/jacoco/issues/1003) here.
In this [commit](https://github.com/floralsea/dd2480-lab3/commit/64ab32ebaeccc1d79ed3ac0ad5552b297d32173d#diff-64812f7fd59160730dd32c525c0b8313f6797df093247450a8df678509007dd4),
we have to add additional `System.out.println()` before each `throw` clause so that we can observe the `coverage improvement` changes.

Show the comments that describe the requirements for the coverage.

Report of old coverage: find in [`old-coverage branch`](https://github.com/floralsea/dd2480-lab3/tree/old-coverage)

Report of new coverage: find in [`coverage-improvemrnt branch`](https://github.com/floralsea/dd2480-lab3/tree/coverage-improvement)

You can find `screenshots` in [`old-coverage branch`](https://github.com/floralsea/dd2480-lab3/tree/old-coverage), the location is 
[`src/main/resources`](https://github.com/floralsea/dd2480-lab3/tree/coverage-improvement/src/main/resources), or click this [link](https://github.com/floralsea/dd2480-lab3/tree/old-coverage/src/main/resources) directly to it. Also, you can 
find the `report` by `JaCoCo` under [site/](https://github.com/floralsea/dd2480-lab3/blob/old-coverage/site/jacoco/index.html).

Similarly, we present our improvements in [`coverage-improvemrnt branch`](https://github.com/floralsea/dd2480-lab3/tree/coverage-improvement), you can directly checkout `main` branch and see `sec/main/resources` for screenshots.

The table below is the comparison of `old coverage` and `new coverage`:

| Function        | old coverage | new coverage |
|-----------------|--------------|--------------|
| readObject()    | 61%          | 92%          |
| readObjectCB()  | 55%          | 77%          |
| findStringEnd() | 31%          | 93%          |
| skipString()    | 0%           | 83%          |


Test cases added: see the following or click the `links` in the following paragraphs for more new test cases details 

Number of test cases added: two per team member (P) or at least four (P+).

For `readObject()` function, **Xu Zuo** added four unit test cases and improved the branch coverage, from 61% to 92%, which 
can be found in this [commit](https://github.com/floralsea/dd2480-lab3/commit/64ab32ebaeccc1d79ed3ac0ad5552b297d32173d). We track the changes 
by using `issue` in our repo, and it relates to this issue [#12](https://github.com/floralsea/dd2480-lab3/issues/12).

For `readObjectCB` function, **Wen Biming** added 4 unit test cases and improved the branch coverage, from 61% to 77%, which can be found in this [commit](https://github.com/floralsea/dd2480-lab3/commit/d0c52ef1951f5d8d9cf63d6dfd35159c8452a31b)

for `findStringEnd()`, four new test cases were added that that specifically targeted uncovered branches. The low coverage before is likely explained by this function only being indirectly tested by other tests. Therefore the new tests directly tested this function which improved coverage greatly. The tests can be seen in this [commit](https://github.com/floralsea/dd2480-lab3/commit/fcc600c096190b22714f91092a0b903403fd435f) or using ```git diff 67ed156 18c987f```. 

For `skipString()` function, **Gustav Nordström** added four unit tests and improved branch coverage, from 0% to 83%. The changes can be found in this [commit](https://github.com/floralsea/dd2480-lab3/commit/16823829fbd3fbd7fab49f7a9091837a670aaafa), and it relates to issue [#15](https://github.com/floralsea/dd2480-lab3/issues/15).

## Self-assessment: Way of working

Current state according to the Essence standard: Collaborating

We're currently working as one cohesive unit, and are still improving. We've improved our communication since the start of the course, and we're collaborating well to reach our common goals.

There are no real doubts about our potential areas for improvement. We acknowledge that there are opportunities to enhance or communication and further improve effectiveness. By focusing on more efficient collaboration and addressing minor inefficiencies, we can optimize our performance even further.

## Overall experience

What are your main take-aways from this project? What did you learn?

Is there something special you want to mention here?

We've mentioned above that JaCoCo does not register exception-based branches as covered unless add additional debug code, 
even when unit tests execute them. This issue was observed in our debugging process, where test cases successfully triggered 
exception paths, but JaCoCo’s report still marked them as uncovered. While our DIY tool explicitly logs branch execution, 
including those leading to throw statements, it ensures complete branch tracking, making it more accurate for measuring 
exception-handling coverage. We found limitations of industry-standard tools in our practice and we could improve it based on 
the current issues. Also, through this process, we have gained a deeper understanding of coverage limitations in existing tools 
and have enhanced our ability to design and implement a more customized and effective testing framework.
