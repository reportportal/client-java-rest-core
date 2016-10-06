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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import com.epam.reportportal.restclient.endpoint.IOUtils;
import com.epam.reportportal.restclient.endpoint.Serializer;
import com.epam.reportportal.restclient.endpoint.exception.SerializerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.net.MediaType;

/**
 * Serializer uses Jackson2 {@link https://github.com/FasterXML} API to
 * converting to/from POJO/JSON
 * 
 * @author Andrei Varabyeu
 * 
 */
public class Jackson2Serializer implements Serializer {

	/** Default Object Mapper */
	private ObjectMapper objectMapper;

	public Jackson2Serializer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Serializes POJO marked with <br>
	 * {@link https://github.com/FasterXML/jackson-annotations} to byte array
	 */
	@Override
	public <T> InputStream serialize(T t) throws SerializerException {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			objectMapper.getFactory().createGenerator(baos).writeObject(t);
			return new ByteArrayInputStream(baos.toByteArray());
		} catch (IOException e) {
			throw new SerializerException("Unable to serialize object '" + t + "'", e);
		} finally {
			IOUtils.closeQuietly(baos);
		}
	}

	/**
	 * Deserializes byte array to POJO marked with <br>
	 * {@link https://github.com/FasterXML/jackson-annotations}
	 */
	@Override
	public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
		try {
			return objectMapper.getFactory().createParser(content).readValueAs(clazz);
		} catch (IOException e) {
			throw new SerializerException("Unable to deserialize content '" + new String(content) + "' to type '" + clazz.getName() + "'",
					e);
		}
	}

	/**
	 * Deserializes byte array to POJO marked with <br>
	 * {@link https://github.com/FasterXML/jackson-annotations}<br>
	 * Uses {@link java.lang.reflect.Type} to understand object to be
	 * deserialized into
	 */
	@Override
	public <T> T deserialize(byte[] content, Type type) throws SerializerException {
		try {
			return objectMapper.readValue(content, TypeFactory.defaultInstance().constructType(type));
		} catch (IOException e) {
			throw new SerializerException("Unable to deserialize content '" + new String(content) + "' to type '" + type.toString() + "'",
					e);
		}
	}

	/**
	 * Returns default MIME type
	 */
	@Override
	public String getMimeType() {
		return MediaType.JSON_UTF_8.toString();
	}

	/**
	 * Checks whether mime types is supported by this serializer implementation
	 */
	@Override
	public boolean canRead(String mimeType) {
		return MediaType.JSON_UTF_8.withoutParameters().is(MediaType.parse(mimeType).withoutParameters());
	}

	@Override
	public boolean canWrite(Object o) {
		return objectMapper.canSerialize(o.getClass());
	}

}