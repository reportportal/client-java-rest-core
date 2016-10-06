/*
 * Copyright 2016 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/epam/ReportPortal
 *
 * Report Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Report Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.epam.reportportal.restclient.serializer;

import com.epam.reportportal.restclient.serializer.Jackson2Serializer;
import com.google.common.io.CharStreams;
import org.junit.Assert;
import org.junit.Test;

import com.epam.reportportal.restclient.endpoint.Serializer;
import com.epam.reportportal.restclient.endpoint.exception.SerializerException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Serializer/Deserializer tests
 * 
 * @author Andrei Varabyeu
 * 
 */
public class Jackson2SerializerTest {

	private static final String SERIALIZED_STRING = "{\"intField\":100,\"stringField\":\"test string\"}";

	private static final String INCORRECT_SERIALIZED_STRING = "{intField:100,\"stringField\":\"test string\"}";

	private static class TestObject {

		private int intField;
		private String stringField;

		@SuppressWarnings("unused")
		public TestObject() {

		}

		public TestObject(int intField, String stringField) {
			this.intField = intField;
			this.stringField = stringField;
		}

		public int getIntField() {
			return intField;
		}

		public String getStringField() {
			return stringField;
		}

	}

	private Serializer serializer = new Jackson2Serializer(new ObjectMapper());

	@Test
	public void testSerialization() throws IOException {
		TestObject to = new TestObject(100, "test string");
		String seriazationResult = CharStreams.toString(new InputStreamReader(serializer.serialize(to)));
		Assert.assertEquals("Incorrect Serialization", SERIALIZED_STRING, seriazationResult);
	}

	@Test
	public void testDerialization() throws SerializerException {
		TestObject to = serializer.deserialize(SERIALIZED_STRING.getBytes(), TestObject.class);
		Assert.assertEquals("Incorrect String field Serialization", "test string", to.getStringField());
		Assert.assertEquals("Incorrect Int field Serialization", 100, to.getIntField());
	}

	@Test(expected = SerializerException.class)
	public void testSerializationError() throws SerializerException {
		serializer.serialize(new ObjectMapper());
	}

	@Test(expected = SerializerException.class)
	public void testDeserializationError() throws SerializerException {
		serializer.deserialize(INCORRECT_SERIALIZED_STRING.getBytes(), TestObject.class);
	}
}
