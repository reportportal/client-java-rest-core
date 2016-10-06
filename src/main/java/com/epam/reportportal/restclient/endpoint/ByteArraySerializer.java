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
package com.epam.reportportal.restclient.endpoint;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;

import com.epam.reportportal.restclient.endpoint.exception.SerializerException;
import com.google.common.net.MediaType;

/**
 * Byte array message converter. Actually, just placeholder to be able to work
 * with byte arrays through {@link Serializer} interface
 * 
 * @author Andrei Varabyeu
 * 
 */
public class ByteArraySerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * Serializer#serialize(java.lang.Object)
	 */
	@Override
	public <T> InputStream serialize(T t) throws SerializerException {
		return new ByteArrayInputStream((byte[]) t);
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
		if (byte[].class.equals(clazz)) {
			return (T) content;
		}
		throw new SerializerException("Unable to deserialize to type '" + clazz.getName() + "'");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#deserialize(byte[],
	 * java.lang.reflect.Type)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] content, Type type) throws SerializerException {
		if (byte[].class.equals(type)) {
			return (T) content;
		}
		throw new SerializerException("Unable to deserialize to type '" + type + "'");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#getMimeType()
	 */
	@Override
	public String getMimeType() {
		return MediaType.OCTET_STREAM.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Serializer#canRead(java.lang.String)
	 */
	@Override
	public boolean canRead(String mimeType) {
		//Following return invalid .canRead() result so just return TRUE anywhere
		//MediaType.ANY_TYPE.is(MediaType.parse(mimeType).withoutParameters());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * Serializer#canWrite(java.lang.Object)
	 */
	@Override
	public boolean canWrite(Object o) {
		return byte[].class.equals(o.getClass());
	}

}
