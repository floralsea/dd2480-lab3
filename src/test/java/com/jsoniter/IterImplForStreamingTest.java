package com.jsoniter;

import com.jsoniter.any.Any;
import com.jsoniter.spi.JsonException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jdk.jshell.spi.ExecutionControl;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.experimental.categories.Category;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class IterImplForStreamingTest extends TestCase {

	public void testReadMaxDouble() throws Exception {
		String maxDouble = "1.7976931348623157e+308";
		JsonIterator iter = JsonIterator.parse("1.7976931348623157e+308");
		IterImplForStreaming.numberChars numberChars = IterImplForStreaming.readNumber(iter);
		String number = new String(numberChars.chars, 0, numberChars.charsLength);
		assertEquals(maxDouble, number);
	}

	@Category(StreamingCategory.class)
	public void testLoadMore() throws IOException {
		final String originalContent = "1234567890";
		final byte[] src = ("{\"a\":\"" + originalContent + "\"}").getBytes();

		int initialBufferSize;
		Any parsedString;
		// Case #1: Data fits into initial buffer, autoresizing on
		// Input must definitely fit into such large buffer
		initialBufferSize = src.length * 2;
		JsonIterator jsonIterator = JsonIterator.parse(getSluggishInputStream(src), initialBufferSize, 512);
		jsonIterator.readObject();
		parsedString = jsonIterator.readAny();
		assertEquals(originalContent, parsedString.toString());
		// Check buffer was not expanded
		assertEquals(initialBufferSize, jsonIterator.buf.length);

		// Case #2: Data does not fit into initial buffer, autoresizing off
		initialBufferSize = originalContent.length() / 2;
		jsonIterator = JsonIterator.parse(getSluggishInputStream(src), initialBufferSize, 0);
		jsonIterator.readObject();
		try {
			jsonIterator.readAny();
			fail("Expect to fail because buffer is too small.");
		} catch (JsonException e) {
			if (!e.getMessage().startsWith("loadMore")) {
				throw e;
			}
		}
		// Check buffer was not expanded
		assertEquals(initialBufferSize, jsonIterator.buf.length);

		// Case #3: Data does fit into initial buffer, autoresizing on
		initialBufferSize = originalContent.length() / 2;
		int autoExpandBufferStep = initialBufferSize * 3;
		jsonIterator = JsonIterator.parse(getSluggishInputStream(src), initialBufferSize, autoExpandBufferStep);
		jsonIterator.readObject();
		parsedString = jsonIterator.readAny();
		assertEquals(originalContent, parsedString.toString());
		// Check buffer was expanded exactly once
		assertEquals(initialBufferSize + autoExpandBufferStep, jsonIterator.buf.length);

		// Case #4: Data does not fit (but largest string does) into initial buffer, autoresizing on
		initialBufferSize = originalContent.length() + 2;
		jsonIterator = JsonIterator.parse(new ByteArrayInputStream(src), initialBufferSize, 0);
		jsonIterator.readObject();
		parsedString = jsonIterator.readAny();
		assertEquals(originalContent, parsedString.toString());
		// Check buffer was expanded exactly once
		assertEquals(initialBufferSize, jsonIterator.buf.length);
	}

	private static InputStream getSluggishInputStream(final byte[] src) {
		return new InputStream() {
			int position = 0;

			@Override
			public int read() throws IOException {
                try {
                    throw new ExecutionControl.NotImplementedException("xzuo");
                } catch (ExecutionControl.NotImplementedException e) {
                    throw new RuntimeException(e);
                }
            }

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				if (position < src.length) {
					b[off] = src[position++];
					return 1;
				}
				return -1;
			}
		};
	}

	@Test
    public void testSkipString_Simple() throws IOException {
        JsonIterator iter = new JsonIterator();
		iter.buf = "test\\\\\"".getBytes();
		iter.head = 0;
        iter.tail = iter.buf.length;

        IterImplForStreaming.skipString(iter);
        assertEquals(iter.head, iter.tail);
    }


	@Test
    public void testSkipString_Complex() throws IOException {
        JsonIterator iter = new JsonIterator();
		iter.buf = "hej\\".getBytes();
		iter.head = 0;
        iter.tail = iter.buf.length;
		iter.in = new ByteArrayInputStream("\\\"".getBytes());

        IterImplForStreaming.skipString(iter);
        assertEquals(iter.head, iter.tail);
    }

	@Test
	public void testSkipString_IncompleteString() throws IOException {
		JsonIterator iter = new JsonIterator();
		iter.buf = "".getBytes();
		iter.head = 0;
        iter.tail = iter.buf.length;

		Exception exception = assertThrows(JsonException.class, () -> {
            IterImplForStreaming.skipString(iter);
        });

        assertTrue(exception.getMessage().contains("incomplete string"));
         
	}

	@Test
	public void testSkipString_EscapedBackslash() throws IOException {
		JsonIterator iter = new JsonIterator();
		iter.buf = "test\\".getBytes();
		iter.head = 0;
        iter.tail = iter.buf.length;

		Exception exception = assertThrows(JsonException.class, () -> {
            IterImplForStreaming.skipString(iter);
        });

        assertTrue(exception.getMessage().contains("incomplete string"));
         
	}
}
