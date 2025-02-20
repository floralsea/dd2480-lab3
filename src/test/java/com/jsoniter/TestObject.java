package com.jsoniter;

import com.jsoniter.annotation.JsonProperty;
import com.jsoniter.any.Any;
import com.jsoniter.fuzzy.MaybeEmptyArrayDecoder;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.EmptyExtension;
import com.jsoniter.spi.JsonException;
import com.jsoniter.spi.JsoniterSpi;
import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TestObject extends TestCase {

    static {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public static class EmptyClass {
    }

    public void test_empty_class() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNotNull(iter.read(EmptyClass.class));
    }

    public void test_empty_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{}");
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj.field1);
        iter.reset(iter.buf);
        Object obj = iter.read(Object.class);
        assertEquals(0, ((Map) obj).size());
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(0, any.size());
    }

    public void test_one_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1'\r:\n\t'hello' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertNull(simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals("hello", any.toString("field1"));
        assertEquals(ValueType.INVALID, any.get("field2").valueType());
        iter.reset(iter.buf);
        assertEquals("hello", ((Map) iter.read()).get("field1"));
    }

    public void test_two_fields() throws IOException {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 'hello' , 'field2': 'world' }".replace('\'', '"'));
        assertEquals("field1", iter.readObject());
        assertEquals("hello", iter.readString());
        assertEquals("field2", iter.readObject());
        assertEquals("world", iter.readString());
        assertNull(iter.readObject());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertEquals("hello", simpleObj.field1);
        assertEquals("world", simpleObj.field2);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals("hello", any.toString("field1"));
        assertEquals("world", any.toString("field2"));
        iter.reset(iter.buf);
        final ArrayList<String> fields = new ArrayList<String>();
        iter.readObjectCB(new JsonIterator.ReadObjectCallback() {
            @Override
            public boolean handle(JsonIterator iter, String field, Object attachment) throws IOException {
                fields.add(field);
                iter.skip();
                return true;
            }
        }, null);
        assertEquals(Arrays.asList("field1", "field2"), fields);
    }

    public void test_read_null() throws IOException {
        JsonIterator iter = JsonIterator.parse("null".replace('\'', '"'));
        assertTrue(iter.readNull());
        iter.reset(iter.buf);
        SimpleObject simpleObj = iter.read(SimpleObject.class);
        assertNull(simpleObj);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(ValueType.NULL, any.get().valueType());
    }

    public void test_native_field() throws IOException {
        JsonIterator iter = JsonIterator.parse("{ 'field1' : 100 }".replace('\'', '"'));
        ComplexObject complexObject = iter.read(ComplexObject.class);
        assertEquals(100, complexObject.field1);
        iter.reset(iter.buf);
        Any any = iter.readAny();
        assertEquals(100, any.toInt("field1"));
    }

    public static class InheritedObject extends SimpleObject {
        public String inheritedField;
    }

    public void test_inheritance() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'inheritedField': 'hello'}".replace('\'', '"'));
        InheritedObject inheritedObject = iter.read(InheritedObject.class);
        assertEquals("hello", inheritedObject.inheritedField);
    }

    public void test_incomplete_field_name() throws IOException {
        try {
            JsonIterator.parse("{\"abc").read(InheritedObject.class);
            fail();
        } catch (JsonException e) {
        }
    }

    public static interface IDependenceInjectedObject {
        String getSomeService();
    }

    public static class DependenceInjectedObject implements IDependenceInjectedObject {

        private String someService;

        public DependenceInjectedObject(String someService) {
            this.someService = someService;
        }

        public String getSomeService() {
            return someService;
        }
    }

    public void test_object_creation() throws IOException {
        JsoniterSpi.registerExtension(new EmptyExtension() {
            @Override
            public boolean canCreate(Class clazz) {
                return clazz.equals(DependenceInjectedObject.class) || clazz.equals(IDependenceInjectedObject.class);
            }

            @Override
            public Object create(Class clazz) {
                return new DependenceInjectedObject("hello");
            }
        });
        IDependenceInjectedObject obj = JsonIterator.deserialize("{}", IDependenceInjectedObject.class);
        assertEquals("hello", obj.getSomeService());
    }

    public static class TestObject5 {

        public enum MyEnum {
            HELLO,
            WORLD,
            WOW
        }

        public MyEnum field1;
    }

    public void test_enum() throws IOException {
//        JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
        TestObject5 obj = JsonIterator.deserialize("{\"field1\":\"HELLO\"}", TestObject5.class);
        assertEquals(TestObject5.MyEnum.HELLO, obj.field1);
        try {
            JsonIterator.deserialize("{\"field1\":\"HELLO1\"}", TestObject5.class);
            fail();
        } catch (JsonException e) {
        }
        obj = JsonIterator.deserialize("{\"field1\":null}", TestObject5.class);
        assertNull(obj.field1);
        obj = JsonIterator.deserialize("{\"field1\":\"WOW\"}", TestObject5.class);
        assertEquals(TestObject5.MyEnum.WOW, obj.field1);
    }

    public static class TestObject6_field1 {
        public int a;
    }

    public static class TestObject6 {
        @JsonProperty(decoder = MaybeEmptyArrayDecoder.class)
        public TestObject6_field1 field1;
    }

    public void test_maybe_empty_array_field() {
        TestObject6 obj = JsonIterator.deserialize("{\"field1\":[]}", TestObject6.class);
        assertNull(obj.field1);
        obj = JsonIterator.deserialize("{\"field1\":{\"a\":1}}", TestObject6.class);
        assertEquals(1, obj.field1.a);
    }

    public void test_iterator() {
        Any any = JsonIterator.deserialize("{\"field1\":1,\"field2\":2,\"field3\":3}");
        Any.EntryIterator iter = any.entries();
        assertTrue(iter.next());
        assertEquals("field1", iter.key());
        assertEquals(1, iter.value().toInt());
        iter = any.entries();
        assertTrue(iter.next());
        assertEquals("field1", iter.key());
        assertEquals(1, iter.value().toInt());
        assertTrue(iter.next());
        assertEquals("field2", iter.key());
        assertEquals(2, iter.value().toInt());
        assertTrue(iter.next());
        assertEquals("field3", iter.key());
        assertEquals(3, iter.value().toInt());
        assertFalse(iter.next());
    }

    public static class PublicSuper {
        public String field1;
    }

    private static class PrivateSub extends PublicSuper {
    }

    public static class TestObject7 {
        public PrivateSub field1;

        public void setFieldXXX(PrivateSub obj) {
        }
    }

    public void test_private_ref() throws IOException {
        TestObject7 obj = JsonIterator.deserialize("{}", TestObject7.class);
        assertNull(obj.field1);
    }

    public static class TestObject8 {
        public String field1;

        @JsonProperty(from = {"field-1"})
        public void setField1(String obj) {
            field1 = "!!!" + obj;
        }
    }

    public void test_setter_is_preferred() throws IOException {
        TestObject8 obj = JsonIterator.deserialize("{\"field-1\":\"hello\"}", TestObject8.class);
        assertEquals("!!!hello", obj.field1);
    }

    public void skip_object_lazy_any_to_string() {
        Any any = JsonIterator.deserialize("{\"field1\":1,\"field2\":2,\"field3\":3}");
        any.asMap().put("field4", Any.wrap(4));
        assertEquals("{\"field1\":1,\"field3\":3,\"field2\":2,\"field4\":4}", any.toString());
    }

    public static class TestObject9 {
        public int 字段;
    }

    public void test_non_ascii_field() {
        TestObject9 obj = JsonIterator.deserialize("{\"字段\":100}", TestObject9.class);
        assertEquals(100, obj.字段);
    }




    // Test for valid JSON object with fields
    @Test
    public void testReadObjectCB_validJson() throws Exception {
        String json = "{\"field1\":1, \"field2\":2, \"field3\":3}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a callback to handle the fields
        JsonIterator.ReadObjectCallback cb = (jsonIter, field, attachment) -> {
            switch (field) {
                case "field1":
                    assertEquals(1, jsonIter.readInt());
                    break;
                case "field2":
                    assertEquals(2, jsonIter.readInt());
                    break;
                case "field3":
                    assertEquals(3, jsonIter.readInt());
                    break;
                default:
                    fail("Unexpected field: " + field);
            }
            return true; // Return true to continue processing
        };

        // Call the method and assert the result
        boolean result = IterImplObject.readObjectCB(iter, cb, null);
        assertTrue(result);
    }

    // Test for empty JSON object
    @Test
    public void testReadObjectCB_emptyObject() throws Exception {
        String json = "{}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a mock callback
        JsonIterator.ReadObjectCallback cb = Mockito.mock(JsonIterator.ReadObjectCallback.class);

        // Call the method and expect it to return true for an empty object
        boolean result = IterImplObject.readObjectCB(iter, cb, null);

        // Assert the result and verify the callback is not invoked (since it's an empty object)
        assertTrue(result);
        Mockito.verify(cb, Mockito.never()).handle(Mockito.any(), Mockito.any(), Mockito.any());
    }

    // Test for JSON object with a null value
    @Test
    public void testReadObjectCB_nullValue() throws Exception {
        String json = "{\"field1\":null}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a callback
        JsonIterator.ReadObjectCallback cb = (jsonIter, field, attachment) -> {
            if ("field1".equals(field)) {
                assertNull(jsonIter.read()); // Read the null value
            }
            return true;
        };

        // Call the method and expect it to handle the field correctly
        boolean result = IterImplObject.readObjectCB(iter, cb, null);

        // Assert the result
        assertTrue(result);
    }

    // Test for JSON object with invalid syntax (missing colon)
    @Test
    public void testReadObjectCB_invalidJson_missingColon() throws Exception {
        String json = "{\"field1\":1, \"field2\"}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a callback
        JsonIterator.ReadObjectCallback cb = Mockito.mock(JsonIterator.ReadObjectCallback.class);

        // Call the method and expect an exception due to the invalid JSON
        Exception exception = assertThrows(Exception.class, () -> {
            IterImplObject.readObjectCB(iter, cb, null);
        });

        // Check that the exception contains the expected error message
        assertTrue(exception.getMessage().contains("expect :"));
    }

    // Test for JSON object with no fields (empty object)
    @Test
    public void testReadObjectCB_noFields() throws Exception {
        String json = "{}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a callback
        JsonIterator.ReadObjectCallback cb = Mockito.mock(JsonIterator.ReadObjectCallback.class);

        // Call the method and assert it returns true
        boolean result = IterImplObject.readObjectCB(iter, cb, null);

        // Assert that the result is true and callback was never called
        assertTrue(result);
        Mockito.verify(cb, Mockito.never()).handle(Mockito.any(), Mockito.any(), Mockito.any());
    }

    // Test for JSON object where the first field is valid but the second field is missing a colon
    @Test
    public void testReadObjectCB_missingColonAfterField() throws Exception {
        String json = "{\"field1\":1, \"field2\"}";
        JsonIterator iter = JsonIterator.parse(json);

        JsonIterator.ReadObjectCallback cb = (jsonIter, field, attachment) -> {
            assertEquals("field1", field);
            return true;
        };

        // Call the method and expect an exception
        Exception exception = assertThrows(Exception.class, () -> {
            IterImplObject.readObjectCB(iter, cb, null);
        });

        // Assert the exception message
        assertTrue(exception.getMessage().contains("expect :"));
    }

    // Test for a JSON object with a single field
    @Test
    public void testReadObjectCB_singleField() throws Exception {
        String json = "{\"field1\": 10}";
        JsonIterator iter = JsonIterator.parse(json);

        JsonIterator.ReadObjectCallback cb = (jsonIter, field, attachment) -> {
            assertEquals("field1", field);
            assertEquals(10, jsonIter.readInt());
            return true;
        };

        // Call the method and assert the result
        boolean result = IterImplObject.readObjectCB(iter, cb, null);
        assertTrue(result);
    }

    // Test for JSON object with multiple fields and commas
    @Test
    public void testReadObjectCB_multipleFields() throws Exception {
        String json = "{\"field1\":1, \"field2\":2, \"field3\":3}";
        JsonIterator iter = JsonIterator.parse(json);

        JsonIterator.ReadObjectCallback cb = (jsonIter, field, attachment) -> {
            switch (field) {
                case "field1":
                    assertEquals(1, jsonIter.readInt());
                    break;
                case "field2":
                    assertEquals(2, jsonIter.readInt());
                    break;
                case "field3":
                    assertEquals(3, jsonIter.readInt());
                    break;
                default:
                    fail("Unexpected field: " + field);
            }
            return true; // Return true to continue processing
        };

        // Call the method and assert the result
        boolean result = IterImplObject.readObjectCB(iter, cb, null);
        assertTrue(result);
    }

    // Test for JSON object with no opening brace, expecting error
    @Test
    public void testReadObjectCB_missingOpeningBrace() throws Exception {
        String json = "\"field1\":1, \"field2\":2}";
        JsonIterator iter = JsonIterator.parse(json);

        // Expecting the method to throw an exception due to the missing opening brace
        Exception exception = assertThrows(Exception.class, () -> {
            IterImplObject.readObjectCB(iter, (jsonIter, field, attachment) -> true, null);
        });

        assertTrue(exception.getMessage().contains("expect {"));
    }

    // Test for JSON object with "null" literal (not an object)
    @Test
    public void testReadObjectCB_nullLiteral() throws Exception {
        String json = "null";
        JsonIterator iter = JsonIterator.parse(json);

        // Call the method and expect it to handle the "null" value correctly
        boolean result = IterImplObject.readObjectCB(iter, (jsonIter, field, attachment) -> true, null);

        // Assert the result
        assertTrue(result);
    }


    // Tests for refactored readObjectCB() method

    @Test
    public void testrefactoredReadObjectCB_validJson() throws Exception {
        String json = "{\"field1\":1, \"field2\":2, \"field3\":3}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a callback to handle the fields
        JsonIterator.ReadObjectCallback cb = (jsonIter, field, attachment) -> {
            switch (field) {
                case "field1":
                    assertEquals(1, jsonIter.readInt());
                    break;
                case "field2":
                    assertEquals(2, jsonIter.readInt());
                    break;
                case "field3":
                    assertEquals(3, jsonIter.readInt());
                    break;
                default:
                    fail("Unexpected field: " + field);
            }
            return true; // Return true to continue processing
        };

        // Call the method and assert the result
        boolean result = IterImplObject.readObjectCB(iter, cb, null);
        assertTrue(result);
    }

    // Test for empty JSON object
    @Test
    public void testrefactoredReadObjectCB_emptyObject() throws Exception {
        String json = "{}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a mock callback
        JsonIterator.ReadObjectCallback cb = Mockito.mock(JsonIterator.ReadObjectCallback.class);

        // Call the method and expect it to return true for an empty object
        boolean result = IterImplObject.readObjectCB(iter, cb, null);

        // Assert the result and verify the callback is not invoked (since it's an empty object)
        assertTrue(result);
        Mockito.verify(cb, Mockito.never()).handle(Mockito.any(), Mockito.any(), Mockito.any());
    }

    // Test for JSON object with a null value
    @Test
    public void testrefactoredReadObjectCB_nullValue() throws Exception {
        String json = "{\"field1\":null}";
        JsonIterator iter = JsonIterator.parse(json);

        // Create a callback
        JsonIterator.ReadObjectCallback cb = (jsonIter, field, attachment) -> {
            if ("field1".equals(field)) {
                assertNull(jsonIter.read()); // Read the null value
            }
            return true;
        };

        // Call the method and expect it to handle the field correctly
        boolean result = IterImplObject.readObjectCB(iter, cb, null);

        // Assert the result
        assertTrue(result);
    }

}
