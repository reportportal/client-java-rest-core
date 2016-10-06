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

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.epam.reportportal.apache.http.Consts;
import com.epam.reportportal.apache.http.HttpResponse;
import com.epam.reportportal.apache.http.HttpVersion;
import com.epam.reportportal.apache.http.StatusLine;
import com.epam.reportportal.apache.http.entity.StringEntity;
import com.epam.reportportal.apache.http.impl.EnglishReasonPhraseCatalog;
import com.epam.reportportal.apache.http.message.BasicHttpResponse;
import com.epam.reportportal.apache.http.message.BasicStatusLine;
import com.epam.reportportal.restclient.endpoint.exception.RestEndpointClientException;
import com.epam.reportportal.restclient.endpoint.exception.RestEndpointIOException;
import com.epam.reportportal.restclient.endpoint.exception.RestEndpointServerException;
import com.google.inject.Key;

/**
 * Default Error Handler Unit Tests
 * 
 * @author Andrei Varabyeu
 * 
 */
public class DefaultErrorHandlerTest {

	private ErrorHandler<HttpResponse> handler = Injector.getInstance().getBean(new Key<ErrorHandler<HttpResponse>>() {
	});

	@Test
	public void errorHandlerCheckClientError() {
		HttpResponse response = getHttpResponse(404, "Not Found");
		Assert.assertTrue("Client Error is not handled", handler.hasError(response));
	}

	@Test
	public void errorHandlerCheckServerError() {
		HttpResponse response = getHttpResponse(500, "Internal Server Error");
		Assert.assertTrue("Server Error is not handled", handler.hasError(response));
	}

	@Test
	public void errorHandlerCheckInformationalResponse() {
		HttpResponse response = getHttpResponse(100, "Continue");
		Assert.assertFalse("Infromation response is handled", handler.hasError(response));
	}

	@Test
	public void errorHandlerCheckSuccessResponse() {
		HttpResponse response = getHttpResponse(200, "Success");
		Assert.assertFalse("Success response is handled", handler.hasError(response));
	}

	@Test
	public void errorHandlerCheckRedirectionResponse() {
		HttpResponse response = getHttpResponse(302, "Found");
		Assert.assertFalse("Redirection response is handled", handler.hasError(response));
	}

	@Test(expected = RestEndpointClientException.class)
	public void testErrorHandlerClientError() throws RestEndpointIOException {
		HttpResponse response = getHttpResponse(404, "Not Found");
		handler.handle(response);
	}

	@Test(expected = RestEndpointServerException.class)
	public void testErrorHandlerServerError() throws RestEndpointIOException {
		HttpResponse response = getHttpResponse(500, "Internal Server Error");
		handler.handle(response);
	}

	@Test
	public void testHandlerInformationalResponse() throws RestEndpointIOException {
		HttpResponse response = getHttpResponse(100, "Continue");
		handler.handle(response);
	}

	@Test
	public void testErrorHandlerSuccessResponse() throws RestEndpointIOException {
		HttpResponse response = getHttpResponse(200, "Success");
		handler.handle(response);
	}

	@Test
	public void testHandlerRedirectionResponse() throws RestEndpointIOException {
		HttpResponse response = getHttpResponse(302, "Found");
		handler.handle(response);
	}

	private HttpResponse getHttpResponse(int statusCode, String message) {
		StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, statusCode, message);
		BasicHttpResponse response = new BasicHttpResponse(statusLine, EnglishReasonPhraseCatalog.INSTANCE, Locale.US);
		response.setEntity(new StringEntity("test string response body", Consts.UTF_8));
		return response;
	}
}
