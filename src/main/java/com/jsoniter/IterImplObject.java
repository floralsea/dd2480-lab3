package com.jsoniter;

import java.io.IOException;

class IterImplObject {

    public static final String readObject(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        switch (c) {
            case 'n':
                IterImpl.skipFixedBytes(iter, 3);
                return null;
            case '{':
                c = IterImpl.nextToken(iter);
                if (c == '"') {
                    iter.unreadByte();
                    String field = iter.readString();
                    if (IterImpl.nextToken(iter) != ':') {
                        // for resolving jacoco can not report throw
                        System.out.println("Invalid field: " + field);
                        throw iter.reportError("readObject", "expect :");
                    }
                    return field;
                }
                if (c == '}') {
                    return null; // end of object
                }
                // for resolving jacoco can not report throw
                System.out.println("Invalid field: " + c);
                throw iter.reportError("readObject", "expect \" after {");
            case ',':
                String field = iter.readString();
                if (IterImpl.nextToken(iter) != ':') {
                    // for resolving jacoco can not report throw
                    System.out.println("expected comma separated field: " + field);
                    throw iter.reportError("readObject", "expect :");
                }
                return field;
            case '}':
                return null; // end of object
            default:
                // for resolving jacoco can not report throw
                System.out.println("unexpected token: " + c);
                throw iter.reportError("readObject", "expect { or , or } or n, but found: " + (char)c);
        }
    }

    public static final boolean readObjectCB(JsonIterator iter, JsonIterator.ReadObjectCallback cb, Object attachment) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if ('{' == c) {
            c = IterImpl.nextToken(iter);
            if ('"' == c) {
                iter.unreadByte();
                String field = iter.readString();
                if (IterImpl.nextToken(iter) != ':') {
                    throw iter.reportError("readObject", "expect :");
                }
                if (!cb.handle(iter, field, attachment)) {
                    return false;
                }
                while (IterImpl.nextToken(iter) == ',') {
                    field = iter.readString();
                    if (IterImpl.nextToken(iter) != ':') {
                        throw iter.reportError("readObject", "expect :");
                    }
                    if (!cb.handle(iter, field, attachment)) {
                        return false;
                    }
                }
                return true;
            }
            if ('}' == c) {
                return true;
            }
            throw iter.reportError("readObjectCB", "expect \" after {");
        }
        if ('n' == c) {
            IterImpl.skipFixedBytes(iter, 3);
            return true;
        }
        throw iter.reportError("readObjectCB", "expect { or n");
    }

    // Refactor work from Xu Zuo
    public static final String refactoredReadObject(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);

        if (c == 'n') {
            return handleNull(iter);
        }
        if (c == '{') {
            return processObject(iter);
        }
        if (c == ',') {
            return processCommaField(iter);
        }
        if (c == '}') {
            return null; // end of object
        }

        handleUnexpectedToken(iter, c);
        return null; // Unreachable but needed for compilation
    }

    // Handles the case when 'n' (null) is encountered
    private static String handleNull(JsonIterator iter) throws IOException {
        IterImpl.skipFixedBytes(iter, 3);
        return null;
    }

    // Handles objects { "key": "value" }
    private static String processObject(JsonIterator iter) throws IOException {
        byte c = IterImpl.nextToken(iter);
        if (c == '"') {
            iter.unreadByte();
            String field = iter.readString();
            if (IterImpl.nextToken(iter) != ':') {
                throw iter.reportError("readObject", "expect :");
            }
            return field;
        }
        if (c == '}') {
            return null; // End of object
        }
        throw iter.reportError("readObject", "expect \" after {");
    }

    // Handles the case when a comma is encountered
    private static String processCommaField(JsonIterator iter) throws IOException {
        String field = iter.readString();
        if (IterImpl.nextToken(iter) != ':') {
            throw iter.reportError("readObject", "expect :");
        }
        return field;
    }

    // Handles unexpected tokens
    private static void handleUnexpectedToken(JsonIterator iter, byte c) throws IOException {
        throw iter.reportError("readObject", "expect { or , or } or n, but found: " + (char)c);
//        throw Error("readObject", "expect { or , or } or n, but found: " + (char) c, c);
    }

    // Centralized error handling for Jacoco visibility
    private static void throwError(String function, String message, Object value) throws IOException {
        System.out.println("Error in " + function + ": " + message + " - " + value);
        throw new IOException(message);
    }

}
