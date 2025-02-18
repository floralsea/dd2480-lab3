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
| readObject(JsonIterator iter)                 | src/main/java/com/jsoniter/IterImplObject.java       | 9           | 32            |                         |                         |
| readObjectCB(JsonIterator iter, JsonIterator.ReadObjectCallback cb, Object attachment)                   | src/main/java/com/jsoniter/IterImplObject.java | 10          | 35                      |                       |            |
| findStringEnd(JsonIterator iter)              | src/main/java/com/jsoniter/IterImplSkip.java         | 10          | 26                      |                         |            |
| skipString(JsonIterator iter)                              | src/main/java/com/jsoniter/IterImplForStreaming.java                                  | 9          | 27                      |                       |            |


   * Did all methods (tools vs. manual count) get the same result?
   * Are the results clear?
2. Are the functions just complex, or also long?
3. What is the purpose of the functions?
4. Are exceptions taken into account in the given measurements?
5. Is the documentation clear w.r.t. all the possible outcomes?

## Refactoring

Plan for refactoring complex code:

Estimated impact of refactoring (lower CC, but other drawbacks?).

Carried out refactoring (optional, P+):

git diff ...

## Coverage

### Tools

Document your experience in using a "new"/different coverage tool.

How well was the tool documented? Was it possible/easy/difficult to
integrate it with your build environment?

### Your own coverage tool

Show a patch (or link to a branch) that shows the instrumented code to
gather coverage measurements.

The patch is probably too long to be copied here, so please add
the git command that is used to obtain the patch instead:

git diff ...

What kinds of constructs does your tool support, and how accurate is
its output?

### Evaluation

1. How detailed is your coverage measurement?

2. What are the limitations of your own tool?

3. Are the results of your tool consistent with existing coverage tools?

## Coverage improvement

Show the comments that describe the requirements for the coverage.

Report of old coverage: [link]

Report of new coverage: [link]

Test cases added:

git diff ...

Number of test cases added: two per team member (P) or at least four (P+).

## Self-assessment: Way of working

Current state according to the Essence standard: ...

Was the self-assessment unanimous? Any doubts about certain items?

How have you improved so far?

Where is potential for improvement?

## Overall experience

What are your main take-aways from this project? What did you learn?

Is there something special you want to mention here?
