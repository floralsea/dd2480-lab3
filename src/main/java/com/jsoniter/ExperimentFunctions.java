package com.jsoniter;

import java.io.IOException;
import com.jsoniter.JsonIterator;

import static com.jsoniter.IterImplForStreaming.loadMore;

public class ExperimentFunctions {

    public static final String readObject(JsonIterator iter) throws IOException {
        CoverageData coverage = CoverageData.getInstance("readObject", 11);
        coverage.logBranch("branch", 1);

        byte c = IterImpl.nextToken(iter);
        switch (c) {
            case 'n':
                coverage.logBranch("branch", 2);
                IterImpl.skipFixedBytes(iter, 3);
                coverage.logBranch("exit", 1);
                return null;
            case '{':
                coverage.logBranch("branch", 3);
                c = IterImpl.nextToken(iter);
                if (c == '"') {
                    coverage.logBranch("branch", 4);
                    iter.unreadByte();
                    String field = iter.readString();
                    if (IterImpl.nextToken(iter) != ':') {
                        coverage.logBranch("branch", 5);
                        throw iter.reportError("readObject", "expect :");
                    }
                    coverage.logBranch("exit", 2);
                    return field;
                }
                if (c == '}') {
                    coverage.logBranch("branch", 6);
                    coverage.logBranch("exit", 3);
                    return null;
                }
                coverage.logBranch("branch", 7);
                throw iter.reportError("readObject", "expect \" after {");
            case ',':
                coverage.logBranch("branch", 8);
                String field = iter.readString();
                if (IterImpl.nextToken(iter) != ':') {
                    coverage.logBranch("branch", 9);
                    throw iter.reportError("readObject", "expect :");
                }
                coverage.logBranch("exit", 4);
                return field;
            case '}':
                coverage.logBranch("branch", 10);
                coverage.logBranch("exit", 5);
                return null;
            default:
                coverage.logBranch("branch", 11);
                throw iter.reportError("readObject", "unexpected character");
        }
    }

    public static boolean readObjectCB(JsonIterator iter, JsonIterator.ReadObjectCallback cb, Object attachment) throws IOException {
        CoverageData coverage = CoverageData.getInstance("readObjectCB", 8);
        coverage.logBranch("branch", 1);

        byte c = IterImpl.nextToken(iter);
        if (c != '{') {
            coverage.logBranch("branch", 2);
            coverage.logBranch("exit", 1);
            return false;
        }

        coverage.logBranch("branch", 3);
        while (true) {
            c = IterImpl.nextToken(iter);
            if (c == '}') {
                coverage.logBranch("branch", 4);
                coverage.logBranch("exit", 2);
                return true;
            }

            coverage.logBranch("branch", 5);
            iter.unreadByte();
            String field = iter.readString();

            if (IterImpl.nextToken(iter) != ':') {
                coverage.logBranch("branch", 6);
                coverage.logBranch("exit", 3);
                throw iter.reportError("readObjectCB", "expect :");
            }

            coverage.logBranch("branch", 7);
            if (!cb.handle(iter, field, attachment)) {
                coverage.logBranch("branch", 8);
                coverage.logBranch("exit", 4);
                return false;
            }
        }
    }



    /*
     * Function 3: findStringEnd()
     * Location: src/main/java/com/jsoniter/IterImplSkip.java
     */
    final static int findStringEnd(JsonIterator iter) {
        CoverageData coverage = CoverageData.getInstance("findStringEnd", 8);
        coverage.logBranch("branch", 1); // record function call

        boolean escaped = false;
        for (int i = iter.head; i < iter.tail; i++) {
            byte c = iter.buf[i];

            if (c == '"') {
                coverage.logBranch("branch", 2);
                if (!escaped) {
                    coverage.logBranch("branch", 3);
                    coverage.logBranch("exit", 1);
                    return i + 1;  // exit point 1
                } else {
                    coverage.logBranch("branch", 4);
                    int j = i - 1;
                    for (;;) {
                        if (j < iter.head || iter.buf[j] != '\\') {
                            coverage.logBranch("branch", 5);
                            // even number of backslashes
                            // either end of buffer, or " found
                            coverage.logBranch("exit", 2);
                            return i + 1;  // exit point 2
                        }
                        j--;
                        if (j < iter.head || iter.buf[j] != '\\') {
                            coverage.logBranch("branch", 6);
                            // odd number of backslashes
                            // it is \" or \\\"
                            break;
                        }
                        j--;
                    }
                }
            } else if (c == '\\') {
                coverage.logBranch("branch", 7);
                escaped = true;
            }
        }
        coverage.logBranch("branch", 8);
        coverage.logBranch("exit", 3);
        return -1; // exit point 3
    }


    /*
     * Function 4: skipString()
     * Location: src/main/java/com/jsoniter/IterImplForStreaming.java
     */
    final static void skipString(JsonIterator iter) throws IOException {
        CoverageData coverage = CoverageData.getInstance("skipString", 7);
        coverage.logBranch("branch", 1);

        for (;;) {
            int end = IterImplSkip.findStringEnd(iter);
            if (end == -1) {
                coverage.logBranch("branch", 2);
                int j = iter.tail - 1;
                boolean escaped = true;
                // can not just look the last byte is \
                // because it could be \\ or \\\
                for (;;) {
                    // walk backward until head
                    if (j < iter.head || iter.buf[j] != '\\') {
                        coverage.logBranch("branch", 3);
                        // even number of backslashes
                        // either end of buffer, or " found
                        escaped = false;
                        break;
                    }
                    j--;
                    if (j < iter.head || iter.buf[j] != '\\') {
                        coverage.logBranch("branch", 4);
                        // odd number of backslashes
                        // it is \" or \\\"
                        break;
                    }
                    j--;
                }
                if (!loadMore(iter)) {
                    coverage.logBranch("branch", 5);
                    coverage.logBranch("exit", 1);
                    throw iter.reportError("skipString", "incomplete string");
                }
                if (escaped) {
                    coverage.logBranch("branch", 6);
                    // TODO add unit test to prove/verify bug
                    iter.head += 1; // skip the first char as last char is \
                }
            } else {
                coverage.logBranch("branch", 7);
                iter.head = end;
                coverage.logBranch("exit", 2);
                return;
            }
        }
    }

}

