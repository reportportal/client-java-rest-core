/*
 * Copyright 2016 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/reportportal/client-java-rest-core
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
package com.epam.reportportal.restclient.endpoint;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;

import com.epam.reportportal.restclient.endpoint.exception.SerializerException;
import com.google.common.net.MediaType;

public class StringSerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * Serializer#serialize(java.lang.Object)
	 */
	@Override
	public <T> InputStream serialize(T t) throws SerializerException {
		return new ByteArrayInputStream(t.toString().getBytes());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#deserialize(byte[],
	 * java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException {
		validateString(clazz);
		return (T) new String(content);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] content, Type type) throws SerializerException {
		validateString(type);
		return (T) new String(content);
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
		return String.class.isAssignableFrom(o.getClass());
	}

	/**
	 * Validates that provided class is assignable from java.lang.String
	 * 
	 * @param clazz
	 * @throws SerializerException
	 */
	private void validateString(Class<?> clazz) throws SerializerException {
		if (null != clazz && !clazz.isAssignableFrom(String.class)) {
			throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
		}
	}

	/**
	 * Validates that provided type is assignable from java.lang.String
	 * 
	 * @param type
	 * @throws SerializerException
	 */
	private void validateString(Type type) throws SerializerException {
		if (null != type && String.class.equals(type)) {
			throw new SerializerException("String serializer is able to work only with data types assignable from java.lang.String");
		}
	}

}