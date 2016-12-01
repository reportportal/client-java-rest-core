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
import java.util.Map;

import com.epam.reportportal.restclient.endpoint.exception.RestEndpointIOException;

/**
 * Interface of endpoint of REST web service
 * 
 * @author Andrei Varabyeu
 * 
 */
public interface RestEndpoint {

	/**
	 * HTTP POST method
	 * 
	 * @param resource
	 *            - REST resource
	 * @param rq
	 *            - Request body
	 * @param clazz
	 *            - Type of returned response
	 * @return - Response body
	 * 
	 * @throws RestEndpointIOException
	 */
	<RQ, RS> RS post(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

	/**
	 * HTTP POST method
	 * 
	 * @param resource
	 *            - REST resource
	 * @param rq
	 *            - Request body
	 * @param type
	 *            - Type of returned response
	 * @return - Response body
	 * 
	 * @throws RestEndpointIOException
	 */
	<RQ, RS> RS post(String resource, RQ rq, Type type) throws RestEndpointIOException;

	/**
	 * HTTP MultiPart POST. May contain whether serialized and binary parts
	 * 
	 * @param resource
	 *            - REST resource
	 * @param request
	 *            - MultiPart request
	 * @param clazz
	 *            - Type of returned response
	 * @return - Response Body
	 * @throws RestEndpointIOException
	 */
	<RQ, RS> RS post(String resource, MultiPartRequest<RQ> request, Class<RS> clazz) throws RestEndpointIOException;

	/**
	 * HTTP POST method with parameters
	 * 
	 * @param resource
	 *            - REST resource
	 * @param parameters
	 *            - Parameters
	 * @param rq
	 *            - Request body
	 * @param clazz
	 *            - Type of returned response
	 * @return
	 * @throws RestEndpointIOException
	 */
	<RQ, RS> RS post(String resource, Map<String, String> parameters, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

	/**
	 * HTTP PUT
	 * 
	 * @param resource
	 *            - REST resource
	 * @param rq
	 *            - Request body
	 * @param clazz
	 *            - Type of Response
	 * @return - Response body
	 * @throws RestEndpointIOException
	 */
	<RQ, RS> RS put(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException;

	/**
	 * HTTP PUT
	 * 
	 * @param resource
	 *            - REST resource
	 * @param rq
	 *            - Request body
	 * @param clazz
	 *            - {@link Type} of Response
	 * @return - Response body
	 * @throws RestEndpointIOException
	 */
	<RQ, RS> RS put(String resource, RQ rq, Type type) throws RestEndpointIOException;

	/**
	 * HTTP DELETE
	 * 
	 * @param resource
	 *            - REST Resource
	 * @param clazz
	 *            - Response Body Type
	 * @return - Response Body
	 * @throws RestEndpointIOException
	 */
	<RS> RS delete(String resource, Class<RS> clazz) throws RestEndpointIOException;

	/**
	 * HTTP GET
	 * 
	 * @param resource
	 *            - REST Resource
	 * @param clazz
	 *            - Response Body Type
	 * @return - Response Body
	 * @throws RestEndpointIOException
	 */
	<RS> RS get(String resource, Class<RS> clazz) throws RestEndpointIOException;

	/**
	 * HTTP GET
	 * 
	 * @param resource
	 *            - REST Resource
	 * @param clazz
	 *            - Response Body Type
	 * @return - Response Body
	 * @throws RestEndpointIOException
	 */
	<RS> RS get(String resource, Type type) throws RestEndpointIOException;

	/**
	 * HTTP GET with parameters
	 * 
	 * @param resource
	 *            - REST Resource
	 * @param parameters
	 *            - Map of query parameters
	 * @param clazz
	 *            - Response body type
	 * @return - Response Body
	 * @throws RestEndpointIOException
	 */
	<RS> RS get(String resource, Map<String, String> parameters, Class<RS> clazz) throws RestEndpointIOException;

	/**
	 * HTTP GET with parameters
	 * 
	 * @param resource
	 *            - REST Resource
	 * @param parameters
	 *            - Map of query parameters
	 * @param type
	 *            - Response body type. For generic types (e.g. collections)
	 *            {@link java.lang.reflect.ParameterizedType} may be used
	 * @return - Response Body
	 * @throws RestEndpointIOException
	 */
	<RS> RS get(String resource, Map<String, String> parameters, Type type) throws RestEndpointIOException;

	<RQ, RS> RS executeRequest(RestCommand<RQ, RS> command) throws RestEndpointIOException;
}
