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

import java.io.IOException;

import com.epam.reportportal.restclient.endpoint.exception.RestEndpointIOException;
import com.epam.reportportal.restclient.endpoint.exception.RestEndpointServerException;
import com.epam.reportportal.apache.http.HttpEntity;
import com.epam.reportportal.apache.http.HttpResponse;
import com.epam.reportportal.apache.http.util.EntityUtils;
import com.epam.reportportal.restclient.endpoint.exception.RestEndpointClientException;
import com.epam.reportportal.restclient.endpoint.exception.RestEndpointException;

/**
 * Default implementation of
 * {@link ErrorHandler}
 * 
 * @author Andrei Varabyeu
 * 
 */
public class DefaultErrorHandler implements ErrorHandler<HttpResponse> {

	/**
	 * Returns TRUE in case status code of response starts with 4 or 5
	 */
	@Override
	public boolean hasError(HttpResponse rs) {
		StatusType statusType = StatusType.valueOf(rs.getStatusLine().getStatusCode());
		return (statusType == StatusType.CLIENT_ERROR || statusType == StatusType.SERVER_ERROR);
	}

	/**
	 * Default implementation. May be overridden in subclasses<br>
	 * Throws
	 * {@link RestEndpointClientException}
	 * for client exceptions and
	 * {@link RestEndpointServerException}
	 * for server exceptions<br>
	 * 
	 * Throwed exceptions may be overridden in handle* methods
	 */
	@Override
	public void handle(HttpResponse rs) throws RestEndpointIOException {
		if (!hasError(rs)) {
			return;
		}
		StatusType statusType = StatusType.valueOf(rs.getStatusLine().getStatusCode());
		int statusCode = rs.getStatusLine().getStatusCode();
		String statusMessage = rs.getStatusLine().getReasonPhrase();
		byte[] errorBody = getErrorBody(rs);

		switch (statusType) {
		case CLIENT_ERROR:
			handleClientError(statusCode, statusMessage, errorBody);
			break;
		case SERVER_ERROR:
			handleServerError(statusCode, statusMessage, errorBody);
			break;
		default:
			handleDefaultError(statusCode, statusMessage, errorBody);
			break;
		}
	}

	/**
	 * Handler methods for HTTP client errors
	 * 
	 * @param statusCode
	 *            - HTTP status code
	 * @param statusMessage
	 *            - HTTP status message
	 * @param errorBody
	 *            - HTTP response body
	 * @throws RestEndpointIOException
	 */
	protected void handleClientError(int statusCode, String statusMessage, byte[] errorBody) throws RestEndpointIOException {
		throw new RestEndpointClientException(statusCode, statusMessage, errorBody);
	}

	/**
	 * Handler methods for HTTP server errors
	 * 
	 * @param statusCode
	 *            - HTTP status code
	 * @param statusMessage
	 *            - HTTP status message
	 * @param errorBody
	 *            - HTTP response body
	 * @throws RestEndpointIOException
	 */
	protected void handleServerError(int statusCode, String statusMessage, byte[] errorBody) throws RestEndpointIOException {
		throw new RestEndpointServerException(statusCode, statusMessage, errorBody);
	}

	/**
	 * Handler methods for unclassified errors
	 * 
	 * @param statusCode
	 *            - HTTP status code
	 * @param statusMessage
	 *            - HTTP status message
	 * @param errorBody
	 *            - HTTP response body
	 * @throws RestEndpointIOException
	 */
	protected void handleDefaultError(int statusCode, String statusMessage, byte[] errorBody) throws RestEndpointIOException {
		throw new RestEndpointException(statusCode, statusMessage, errorBody);
	}

	/**
	 * Parses byte from entity
	 * 
	 * @param rs
	 * @return
	 * @throws RestEndpointIOException
	 */
	private byte[] getErrorBody(HttpResponse rs) throws RestEndpointIOException {
		HttpEntity entity = null;
		try {
			entity = rs.getEntity();
			return EntityUtils.toByteArray(rs.getEntity());
		} catch (IOException e) {
			throw new RestEndpointIOException("Unable to read body from error", e);
		} finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				throw new RestEndpointIOException("Unable to consume response entity", e);
			}
		}
	}
}
