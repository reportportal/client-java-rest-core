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

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

/**
 * Unit tests for {@link RestEndpoint}
 * 
 * @author Andrei Varabyeu
 * 
 */
public class RestEndointTest {

	private static final String SERIALIZED_STRING = "{\"intField\":100,\"stringField\":\"test string\"}";

	private static final String SERIALIZED_STRING_PATTERN = "{\"intField\":%d,\"stringField\":\"%s\"}";

	private RestEndpoint endpoint = Injector.getInstance().getBean(RestEndpoint.class);

	private static MockWebServer server = Injector.getInstance().getBean(MockWebServer.class);

	@BeforeClass
	public static void before() throws IOException {
		server.play(GuiceTestModule.MOCK_PORT);
	}

	@AfterClass
	public static void after() throws IOException {
		server.shutdown();
	}

	@Test
	public void testGet() throws IOException, InterruptedException {
		server.enqueue(prepareResponse(SERIALIZED_STRING));
		String to = endpoint.get("/", String.class);
		Assert.assertNotNull("Recieved Object is null", to);

		RecordedRequest request = server.takeRequest();
		Assert.assertEquals("Incorrect Request Line", "GET / HTTP/1.1", request.getRequestLine());
	}

	@Test
	public void testPost() throws IOException, InterruptedException {
		server.enqueue(prepareResponse(SERIALIZED_STRING));
		String to = endpoint.post("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class);
		Assert.assertNotNull("Recieved Object is null", to);

		RecordedRequest request = server.takeRequest();
		Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
		validateHeader(request);
		Assert.assertEquals("Incorrect body", SERIALIZED_STRING, new String(request.getBody()));
	}

	@Test
	public void testPut() throws IOException, InterruptedException {
		server.enqueue(prepareResponse(SERIALIZED_STRING));
		String to = endpoint.put("/", String.format(SERIALIZED_STRING_PATTERN, 100, "test string"), String.class);
		Assert.assertNotNull("Recieved Object is null", to);

		RecordedRequest request = server.takeRequest();
		Assert.assertEquals("Incorrect Request Line", "PUT / HTTP/1.1", request.getRequestLine());
		validateHeader(request);
		Assert.assertEquals("Incorrect body", SERIALIZED_STRING, new String(request.getBody()));
	}

	@Test
	public void testDelete() throws IOException, InterruptedException {
		server.enqueue(prepareResponse(SERIALIZED_STRING));
		String to = endpoint.delete("/", String.class);
		Assert.assertNotNull("Recieved Object is null", to);

		RecordedRequest request = server.takeRequest();
		Assert.assertEquals("Incorrect Request Line", "DELETE / HTTP/1.1", request.getRequestLine());
	}

	@Test
	public void testCommand() throws IOException, InterruptedException {
		server.enqueue(prepareResponse(SERIALIZED_STRING));

		RestCommand<String, String> command = new RestCommand<String, String>("/", HttpMethod.POST, SERIALIZED_STRING);

		String to = endpoint.executeRequest(command);

		Assert.assertNotNull("Recieved Object is null", to);

		RecordedRequest request = server.takeRequest();
		Assert.assertEquals("Incorrect Request Line", "POST / HTTP/1.1", request.getRequestLine());
	}

	private void validateHeader(RecordedRequest request) {
		Assert.assertTrue(request.getHeaders().contains("Content-Type: application/json; charset=utf-8"));
	}

	private MockResponse prepareResponse(String body) {
		return new MockResponse().setBody(body).setHeader("Content-Type", "application/json");
	}
}
