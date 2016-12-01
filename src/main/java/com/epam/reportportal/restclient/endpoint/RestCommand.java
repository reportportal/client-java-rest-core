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

import java.lang.reflect.Type;

/**
 * Base Rest request representation
 * 
 * @author Andrei Varabyeu
 * 
 * @param <RQ>
 *            - type of request
 * @param <RS>
 *            - type of response
 */
public class RestCommand<RQ, RS> {

	private HttpMethod httpMethod;
	private RQ request;
	private String uri;
	private ParameterizedTypeReference<RS> typeReference;

	public RestCommand(String uri, HttpMethod method, RQ request) {
		this.httpMethod = method;
		this.request = request;
		this.uri = uri;
		this.typeReference = new ParameterizedTypeReference<RS>() {
		};
		validate();
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public RQ getRequest() {
		return request;
	}

	public String getUri() {
		return uri;
	}

	public Type getType() {
		return typeReference.getType();
	}

	private void validate() {
		if (HttpMethod.GET.equals(this.httpMethod) && null != this.request) {
			throw new RuntimeException("'GET' request cannot contain body");
		}
	}
}
