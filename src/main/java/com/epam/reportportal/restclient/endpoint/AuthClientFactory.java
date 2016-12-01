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

import java.net.ProxySelector;
import java.util.List;

import com.epam.reportportal.apache.http.HttpRequestInterceptor;
import com.epam.reportportal.apache.http.auth.AuthScope;
import com.epam.reportportal.apache.http.auth.Credentials;
import com.epam.reportportal.apache.http.client.CredentialsProvider;
import com.epam.reportportal.apache.http.client.HttpClient;
import com.epam.reportportal.apache.http.impl.client.BasicCredentialsProvider;
import com.epam.reportportal.apache.http.impl.client.HttpClientBuilder;
import com.epam.reportportal.apache.http.impl.conn.SystemDefaultRoutePlanner;

/**
 * Basic Auth HTTP Client Factory
 * 
 * @author Andrei Varabyeu
 * 
 */
public class AuthClientFactory implements HttpClientFactory {

	/** HTTP Credentials */
	private Credentials credentials;

	/*
	 * Http client interceptor
	 */
	private List<HttpRequestInterceptor> interceptors;

	public AuthClientFactory(Credentials credentials) {
		this.credentials = credentials;
	}

	public AuthClientFactory(Credentials credentials, List<HttpRequestInterceptor> interceptors) {
		this.credentials = credentials;
		this.interceptors = interceptors;
	}

	@Override
	public HttpClient createHttpClient() {
		HttpClientBuilder builder = initDefaultBuilder();
		return builder.build();
	}

	/**
	 * Initializes default http client builder instance
	 * 
	 * @return
	 */
	protected HttpClientBuilder initDefaultBuilder() {
		HttpClientBuilder builder = HttpClientBuilder.create();

		if (null != credentials) {
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, credentials);
			builder.setDefaultCredentialsProvider(credentialsProvider);
		}

		builder.setMaxConnPerRoute(5);
		builder.setMaxConnTotal(20);
		// dirty hack to avoid npe in soapui client, soapui client sets default
		// proxy selector to null
		ProxySelector proxySelector = ProxySelector.getDefault();
		if (proxySelector != null)
			builder.setRoutePlanner(new SystemDefaultRoutePlanner(proxySelector));

		if (null != interceptors && !interceptors.isEmpty()) {
			for (HttpRequestInterceptor interceptor : interceptors) {
				builder.addInterceptorFirst(interceptor);
			}
		}

		return builder;
	}

}
