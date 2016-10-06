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

import com.epam.reportportal.apache.http.HttpException;
import com.epam.reportportal.apache.http.HttpRequest;
import com.epam.reportportal.apache.http.HttpRequestInterceptor;
import com.epam.reportportal.apache.http.protocol.HttpContext;
import com.google.common.base.Preconditions;
import com.google.common.net.HttpHeaders;

import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Adds bearer token to requests
 *
 * @author Andrei Varabyeu
 */
public class BearerAuthorizationInterceptor implements HttpRequestInterceptor {

	private final String authorization;

	public BearerAuthorizationInterceptor(String token) {
		Preconditions.checkArgument(!isNullOrEmpty(token));
		this.authorization = "Bearer " + token;
	}

	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		/* remove already existing auth headers if they are present */
		request.removeHeaders(HttpHeaders.AUTHORIZATION);

		request.setHeader(HttpHeaders.AUTHORIZATION, authorization);
	}
}
