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
import java.net.ServerSocket;

import com.epam.reportportal.restclient.endpoint.*;
import com.epam.reportportal.apache.http.HttpResponse;
import com.epam.reportportal.apache.http.auth.UsernamePasswordCredentials;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.mockwebserver.MockWebServer;

/**
 * Google Guice Module for testing purposes
 * 
 * @author Andrei Varabyeu
 * 
 */
public class GuiceTestModule implements Module {

	/**
	 * Default PORT for Mock Web Server
	 */
	public static int MOCK_PORT = findFreePort();

	@Override
	public void configure(Binder binder) {

		/** Error Handler binding */
		binder.bind(new Key<ErrorHandler<HttpResponse>>() {
		}).to(DefaultErrorHandler.class).in(Scopes.SINGLETON);

		/** MockWebServir binding */
		binder.bind(MockWebServer.class).toInstance(new MockWebServer());

	}

	/**
	 * Default {@link Serializer} binding
	 * 
	 * @return
	 */
	@Provides
	public Serializer provideSeriazer() {
		return new StringSerializer();
	}

	/**
	 * Default {@link RestEndpoint} binding
	 * 
	 * @param serializer
	 * @return
	 */
	@Provides
	public RestEndpoint provideRestEndpoint(Serializer serializer) {
		return new HttpClientRestEndpoint(new AuthClientFactory(new UsernamePasswordCredentials("")).createHttpClient(),
				Lists.<Serializer> newArrayList(new StringSerializer(), new ByteArraySerializer()), new DefaultErrorHandler(),
				"http://localhost:" + MOCK_PORT);
	}

	private static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
			throw new RuntimeException("Unable to find free port", e);
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// do nothing
				}
			}
		}
	}

}
