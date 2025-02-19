package com.jsoniter;

import org.junit.jupiter.api.*;
import java.io.IOException;
import com.jsoniter.JsonIterator;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExperimentFunctionsTest {
    private final CoverageTool coverageTool = new CoverageTool();

    @BeforeAll
    public void setup() {
        coverageTool.addTest("readObject", this::testReadObject_ValidJson, 11);
        coverageTool.addTest("readObject", this::testReadObject_Null, 11);
        coverageTool.addTest("readObjectCB", this::testReadObjectCB_EmptyObject, 8);
    }

    // Test example from Xu Zuo
    @Test
    public void testReadObject_ValidJson() {
        assertDoesNotThrow(() -> {
            JsonIterator iter = JsonIterator.parse("{\"key\":\"value\"}");
            String result = ExperimentFunctions.readObject(iter);
            assertEquals("key", result);
        });
    }

    // Test example from Xu Zuo
    @Test
    public void testReadObject_Null() {
        assertDoesNotThrow(() -> {
            JsonIterator iter = JsonIterator.parse("null");
            String result = ExperimentFunctions.readObject(iter);
            assertNull(result);
        });
    }

    // Test example from Xu Zuo
    @Test
    public void testReadObjectCB_EmptyObject() {
        assertDoesNotThrow(() -> {
            JsonIterator iter = JsonIterator.parse("{}");
            boolean result = ExperimentFunctions.readObjectCB(iter, (i, field, att) -> true, null);
            assertTrue(result);
        });
    }

    @AfterAll
    public void printCoverage() {
        coverageTool.run();
        coverageTool.printResults();
    }
}

