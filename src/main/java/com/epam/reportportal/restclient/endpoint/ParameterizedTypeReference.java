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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Parameterized type reference. Implementation grabbed from SpringSource
 * 
 * @author Andrei Varabyeu
 * @see <a href=
 *      "https://github.com/SpringSource/spring-framework/blob/master/spring-core/src/main/java/org/springframework/core/ParameterizedTypeReference.java"
 *      >Source</>
 * 
 * @param <T>
 */
abstract public class ParameterizedTypeReference<T> {

	private final Type type;

	protected ParameterizedTypeReference() {
		Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(getClass());
		Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();

		if (!ParameterizedType.class.isInstance(type)) {
			throw new IllegalArgumentException("");
		}
		// Assert.isInstanceOf(ParameterizedType.class, type);
		ParameterizedType parameterizedType = (ParameterizedType) type;
		// Assert.isTrue(parameterizedType.getActualTypeArguments().length ==
		// 1);
		this.type = parameterizedType.getActualTypeArguments()[0];
	}

	public Type getType() {
		return this.type;
	}

	@Override
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof ParameterizedTypeReference && this.type.equals(((ParameterizedTypeReference<?>) obj).type)));
	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	@Override
	public String toString() {
		return "ParameterizedTypeReference<" + this.type + ">";
	}

	private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
		Class<?> parent = child.getSuperclass();
		if (Object.class.equals(parent)) {
			throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
		} else if (ParameterizedTypeReference.class.equals(parent)) {
			return child;
		} else {
			return findParameterizedTypeReferenceSubclass(parent);
		}
	}

}
