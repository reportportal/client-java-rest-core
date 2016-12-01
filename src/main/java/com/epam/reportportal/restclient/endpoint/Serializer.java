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

import java.io.InputStream;
import java.lang.reflect.Type;

import com.epam.reportportal.restclient.endpoint.exception.SerializerException;

/**
 * HTTP Message Serializer. Converts messages to/from byte array
 * 
 * @author Andrei Varabyeu
 * 
 */
public interface Serializer {

	/**
	 * Serializes Message into byte array
	 * 
	 * @param t
	 * @return
	 * @throws SerializerException
	 */
	<T> InputStream serialize(T t) throws SerializerException;

	/**
	 * Deserializes message from byte array
	 * 
	 * @param content
	 * @param clazz
	 * @return
	 * @throws SerializerException
	 */
	<T> T deserialize(byte[] content, Class<T> clazz) throws SerializerException;

	/**
	 * Deserializes message from byte array
	 * 
	 * @param content
	 * @param type
	 *            - Representation of type of response. For generic types (e.g.
	 *            collections) {@link java.lang.reflect.ParameterizedType} may
	 *            be used
	 * @return
	 * @throws SerializerException
	 */
	<T> T deserialize(byte[] content, Type type) throws SerializerException;

	/**
	 * Returns MIME type of serialized messages
	 * 
	 * @return
	 */
	String getMimeType();

	/**
	 * Checks whether mime types is supported by this serializer implementation
	 * 
	 * @see http://en.wikipedia.org/wiki/Internet_media_type
	 * 
	 * @param mimeType
	 *            - MIME Type
	 * 
	 * @return TRUE if specified type is supported
	 */
	boolean canRead(String mimeType);

	/**
	 * Check whether object can be serializer via this serializer implementation
	 * 
	 * @param o
	 *            - Object to be serialized
	 * @return
	 */
	boolean canWrite(Object o);
}
