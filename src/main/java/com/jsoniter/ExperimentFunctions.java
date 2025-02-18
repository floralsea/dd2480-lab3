package com.jsoniter;

import java.io.IOException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.jsoniter.IterImplForStreaming.loadMore;

public class ExperimentFunctions {
    // Record the executed branches
    private static final Set<Integer> executedBranches = new HashSet<>();

    // Record branch execution
    private static void logBranch(int id) {
        executedBranches.add(id);
    }

    // Output coverage report
    public static void printCoverageReport() {
        System.out.println("Executed branches: " + executedBranches);
    }

    /*
     * Function 1: readObject()
     * Location: src/main/java/com/jsoniter/IterImplObject.java
     */
    public static final String readObject(JsonIterator iter) throws IOException {
        // Record function is called
        logBranch(1);
        byte c = IterImpl.nextToken(iter);
        switch (c) {
            case 'n':
                logBranch(2);
                IterImpl.skipFixedBytes(iter, 3);
                return null;
            case '{':
                logBranch(3);
                c = IterImpl.nextToken(iter);
                if (c == '"') {
                    logBranch(4);
                    iter.unreadByte();
                    String field = iter.readString();
                    if (IterImpl.nextToken(iter) != ':') {
                        logBranch(5);
                        throw iter.reportError("readObject", "expect :");
                    }
                    return field;
                }
                if (c == '}') {
                    logBranch(6);
                    return null; // end of object
                }
                logBranch(7);
                throw iter.reportError("readObject", "expect \" after {");
            case ',':
                logBranch(8);
                String field = iter.readString();
                if (IterImpl.nextToken(iter) != ':') {
                    logBranch(9);
                    throw iter.reportError("readObject", "expect :");
                }
                return field;
            case '}':
                logBranch(10);
                return null; // end of object
            default:
                logBranch(11);
                throw iter.reportError("readObject", "expect { or , or } or n, but found: " + (char) c);
        }
    }

    /*
     * Function 2: readObjectCB()
     * Location: src/main/java/com/jsoniter/IterImplObject.java
     */
    public static final boolean readObjectCB(JsonIterator iter, JsonIterator.ReadObjectCallback cb, Object attachment)
            throws IOException {
        logBranch(12);
        byte c = IterImpl.nextToken(iter);
        if ('{' == c) {
            logBranch(13);
            c = IterImpl.nextToken(iter);
            if ('"' == c) {
                logBranch(14);
                iter.unreadByte();
                String field = iter.readString();
                if (IterImpl.nextToken(iter) != ':') {
                    logBranch(15);
                    throw iter.reportError("readObject", "expect :");
                }
                if (!cb.handle(iter, field, attachment)) {
                    logBranch(16);
                    return false;
                }
                while (IterImpl.nextToken(iter) == ',') {
                    logBranch(17);
                    field = iter.readString();
                    if (IterImpl.nextToken(iter) != ':') {
                        logBranch(18);
                        throw iter.reportError("readObject", "expect :");
                    }
                    if (!cb.handle(iter, field, attachment)) {
                        logBranch(19);
                        return false;
                    }
                }
                return true;
            }
            if ('}' == c) {
                logBranch(20);
                return true;
            }
            logBranch(21);
            throw iter.reportError("readObjectCB", "expect \" after {");
        }
        if ('n' == c) {
            logBranch(22);
            IterImpl.skipFixedBytes(iter, 3);
            return true;
        }
        logBranch(23);
        throw iter.reportError("readObjectCB", "expect { or n");
    }

    /*
     * Function 3: findStringEnd()
     * Location: src/main/java/com/jsoniter/IterImplSkip.java
     */
    final static int findStringEnd(JsonIterator iter) {
        logBranch(24);
        boolean escaped = false;
        for (int i = iter.head; i < iter.tail; i++) {
            byte c = iter.buf[i];
            if (c == '"') {
                logBranch(25);
                if (!escaped) {
                    logBranch(26);
                    return i + 1;
                } else {
                    logBranch(27);
                    int j = i - 1;
                    for (;;) {
                        if (j < iter.head || iter.buf[j] != '\\') {
                            logBranch(28);
                            // even number of backslashes
                            // either end of buffer, or " found
                            return i + 1;
                        }
                        j--;
                        if (j < iter.head || iter.buf[j] != '\\') {
                            logBranch(29);
                            // odd number of backslashes
                            // it is \" or \\\"
                            break;
                        }
                        j--;
                    }
                }
            } else if (c == '\\') {
                logBranch(30);
                escaped = true;
            }
        }
        logBranch(31);
        return -1;
    }

    /*
     * Function 4: skipString()
     * Location: src/main/java/com/jsoniter/IterImplForStreaming.java
     */
    final static void skipString(JsonIterator iter) throws IOException {
        logBranch(32);
        for (;;) {
            int end = IterImplSkip.findStringEnd(iter);
            if (end == -1) {
                logBranch(33);
                int j = iter.tail - 1;
                boolean escaped = true;
                // can not just look the last byte is \
                // because it could be \\ or \\\
                for (;;) {
                    // walk backward until head
                    if (j < iter.head || iter.buf[j] != '\\') {
                        logBranch(34);
                        // even number of backslashes
                        // either end of buffer, or " found
                        escaped = false;
                        break;
                    }
                    j--;
                    if (j < iter.head || iter.buf[j] != '\\') {
                        logBranch(35);
                        // odd number of backslashes
                        // it is \" or \\\"
                        break;
                    }
                    j--;

                }
                if (!loadMore(iter)) {
                    logBranch(36);
                    throw iter.reportError("skipString", "incomplete string");
                }
                if (escaped) {
                    logBranch(37);
                    // TODO add unit test to prove/verify bug
                    iter.head += 1; // skip the first char as last char is \
                }
            } else {
                logBranch(38);
                iter.head = end;
                return;
            }
        }
    }
}
