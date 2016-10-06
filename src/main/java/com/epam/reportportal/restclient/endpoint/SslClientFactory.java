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

import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.SSLContext;

import com.epam.reportportal.apache.http.HttpRequestInterceptor;
import com.epam.reportportal.apache.http.auth.Credentials;
import com.epam.reportportal.apache.http.client.HttpClient;
import com.epam.reportportal.apache.http.conn.ssl.AllowAllHostnameVerifier;
import com.epam.reportportal.apache.http.conn.ssl.SSLConnectionSocketFactory;
import com.epam.reportportal.apache.http.conn.ssl.SSLContexts;
import com.epam.reportportal.apache.http.impl.client.HttpClientBuilder;

/**
 * SSL Client Factory. Can be customized via {@link HttpRequestInterceptor}
 * 
 * @author Andrei Varabyeu
 * 
 */
public class SslClientFactory extends AuthClientFactory implements HttpClientFactory {

	private KeyStore keyStore;

	public SslClientFactory(Credentials credentials, KeyStore keyStore) {
		super(credentials);
		this.keyStore = keyStore;
	}

	public SslClientFactory(Credentials credentials, InputStream keyStore, String keyStorePass) {
		super(credentials);
		this.keyStore = loadKeyStore(keyStore, keyStorePass);
	}

	public SslClientFactory(Credentials credentials, String keyStoreResource, String keyStorePass, List<HttpRequestInterceptor> interceptors) {
		super(credentials, interceptors);
		this.keyStore = loadKeyStore(this.getClass().getClassLoader().getResourceAsStream(keyStoreResource), keyStorePass);

	}

	public SslClientFactory(Credentials credentials, InputStream keyStore, String keyStorePass, List<HttpRequestInterceptor> interceptors) {
		super(credentials, interceptors);
		this.keyStore = loadKeyStore(keyStore, keyStorePass);
	}

	@Override
	public HttpClient createHttpClient() {
		try {

			SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keyStore).build();

			/*
			 * Unreal magic, but we can't use
			 * org.apache.http.conn.ssl.SSLConnectionSocketFactory
			 * .BROWSER_COMPATIBLE_HOSTNAME_VERIFIER here due to some problems
			 * related to classloaders. Initialize host name verifier explicitly
			 */
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new AllowAllHostnameVerifier());
			HttpClientBuilder builder = initDefaultBuilder();

			builder.setSSLSocketFactory(sslsf);
			return builder.build();
		} catch (Exception e) {
			throw new RuntimeException("Unable to create http client", e);
		}
	}

	/**
	 * Loads keystore
	 * 
	 * @param keyStore
	 * @param password
	 * @return
	 */
	private KeyStore loadKeyStore(InputStream keyStore, String password) {
		try {
			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStore.load(keyStore, password.toCharArray());
			return trustStore;
		} catch (Exception e) {
			throw new RuntimeException("Unable to load trust store", e);
		} finally {
			IOUtils.closeQuietly(keyStore);
		}
	}

}
