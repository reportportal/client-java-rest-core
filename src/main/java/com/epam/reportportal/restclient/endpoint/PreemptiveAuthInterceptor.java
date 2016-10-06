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

import com.epam.reportportal.apache.http.HttpException;
import com.epam.reportportal.apache.http.HttpHost;
import com.epam.reportportal.apache.http.HttpRequest;
import com.epam.reportportal.apache.http.HttpRequestInterceptor;
import com.epam.reportportal.apache.http.auth.AuthScheme;
import com.epam.reportportal.apache.http.auth.AuthState;
import com.epam.reportportal.apache.http.client.AuthCache;
import com.epam.reportportal.apache.http.client.HttpClient;
import com.epam.reportportal.apache.http.client.protocol.HttpClientContext;
import com.epam.reportportal.apache.http.impl.auth.BasicScheme;
import com.epam.reportportal.apache.http.impl.client.BasicAuthCache;
import com.epam.reportportal.apache.http.protocol.HttpContext;
import com.epam.reportportal.apache.http.protocol.HttpCoreContext;

/**
 * Adds {@link AuthScheme} to all requests as {@link AuthCache} object. This way
 * we are able to force {@link HttpClient} to use auth preemptively
 * 
 */
public class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

	/**
	 * Adds provided auth scheme to the client if there are no another provided
	 * auth schemes
	 */
	@Override
	public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {

		AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
		if (authState.getAuthScheme() == null) {

			HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
			AuthCache authCache = new BasicAuthCache();
			authCache.put(targetHost, new BasicScheme());
			context.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
		}
	}
}
