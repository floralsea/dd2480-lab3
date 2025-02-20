package com.jsoniter;

import java.io.IOException;

public class TestExperimentFunctions {
    public static void main(String[] args) throws IOException {
        // Ensure valid JSON
        String json = "{\"key\":\"value\"}";
        JsonIterator iter1 = JsonIterator.parse(json);
        ExperimentFunctions.readObject(iter1);

        JsonIterator iter2 = JsonIterator.parse(json);
        ExperimentFunctions.readObjectCB(iter2, new JsonIterator.ReadObjectCallback() {
            @Override
            public boolean handle(JsonIterator iter, String field, Object attachment) {
                System.out.println("Handling field: " + field);
                // Continue parsing
                return true;
            }
        }, null);

        JsonIterator iter3 = JsonIterator.parse(json);
        ExperimentFunctions.findStringEnd(iter3);

        JsonIterator iter4 = JsonIterator.parse(json);
        ExperimentFunctions.skipString(iter4);

        // Output coverage results
        // ExperimentFunctions.printCoverageReport();
    }
}


