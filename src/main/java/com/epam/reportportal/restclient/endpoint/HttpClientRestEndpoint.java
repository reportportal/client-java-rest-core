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
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.epam.reportportal.apache.http.HttpEntity;
import com.epam.reportportal.apache.http.HttpResponse;
import com.epam.reportportal.apache.http.client.HttpClient;
import com.epam.reportportal.apache.http.client.methods.HttpDelete;
import com.epam.reportportal.apache.http.client.methods.HttpGet;
import com.epam.reportportal.apache.http.client.methods.HttpPatch;
import com.epam.reportportal.apache.http.client.methods.HttpPost;
import com.epam.reportportal.apache.http.client.methods.HttpPut;
import com.epam.reportportal.apache.http.client.methods.HttpUriRequest;
import com.epam.reportportal.apache.http.client.utils.URIBuilder;
import com.epam.reportportal.apache.http.entity.ContentType;
import com.epam.reportportal.apache.http.entity.InputStreamEntity;
import com.epam.reportportal.apache.http.entity.mime.MultipartEntityBuilder;
import com.epam.reportportal.apache.http.entity.mime.content.InputStreamBody;
import com.epam.reportportal.apache.http.util.EntityUtils;
import com.epam.reportportal.restclient.endpoint.MultiPartRequest.MultiPartBinary;
import com.epam.reportportal.restclient.endpoint.exception.RestEndpointIOException;
import com.epam.reportportal.restclient.endpoint.exception.SerializerException;
import com.google.common.base.Preconditions;

/**
 * {@link RestEndpoint} implementation. Uses
 * Apache HTTP Components {@link org.apache.http.client.HttpClient} as default
 * http client implementation
 * 
 * @author Andrei Varabyeu
 * 
 */
public class HttpClientRestEndpoint implements RestEndpoint {

	/** Serializer for converting HTTP messages */
	private List<Serializer> serializers;

	/** Base Endpoint URL */
	private String baseUrl;

	/** Error Handler for HttpResponses */
	private ErrorHandler<HttpResponse> errorHandler;

	/** HTTP Client */
	private HttpClient httpClient;

	/**
	 * Default constructor.
	 * 
	 * @param serializer
	 *            - Serializer for converting HTTP messages. Shouldn't be null
	 * @param errorHandler
	 *            - Error handler for HTTP messages
	 * @param credentials
	 *            - Credentials for HTTP client
	 * @param baseUrl
	 *            - REST WebService Base URL
	 */
	public HttpClientRestEndpoint(HttpClient httpClient, List<Serializer> serializers, ErrorHandler<HttpResponse> errorHandler,
			String baseUrl) {
		this.serializers = Preconditions.checkNotNull(serializers, "Serializer should'be be null");
		this.baseUrl = Preconditions.checkNotNull(baseUrl, "Base URL shouldn't be null");

		this.errorHandler = errorHandler == null ? new DefaultErrorHandler() : errorHandler;
		this.httpClient = httpClient;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RestEndpoint#post(java.lang .String,
	 * java.lang.Object, java.lang.Class)
	 */
	@Override
	public <RQ, RS> RS post(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
		HttpPost post = new HttpPost(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		InputStreamEntity httpEntity = new InputStreamEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
		post.setEntity(httpEntity);
		return executeInternal(post, new ClassConverterCallback<RS>(serializers, clazz));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RestEndpoint#post(java.lang.String,
	 * java.lang.Object, java.lang.reflect.Type)
	 */
	@Override
	public <RQ, RS> RS post(String resource, RQ rq, Type type) throws RestEndpointIOException {
		HttpPost post = new HttpPost(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		InputStreamEntity httpEntity = new InputStreamEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
		post.setEntity(httpEntity);
		return executeInternal(post, new TypeConverterCallback<RS>(serializers, type));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RestEndpoint#post(java.lang .String,
	 * MultiPartRequest, java.lang.Class)
	 */
	@Override
	public <RQ, RS> RS post(String resource, MultiPartRequest<RQ> request, Class<RS> clazz) throws RestEndpointIOException {
		HttpPost post = new HttpPost(spliceUrl(resource));

		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			for (MultiPartRequest.MultiPartSerialized<RQ> serializedPart : request.getSerializedRQs()) {
				Serializer serializer = getSupportedSerializer(serializedPart);
				builder.addPart(serializedPart.getPartName(), new InputStreamBody(serializer.serialize(serializedPart.getRequest()),
						ContentType.parse(serializer.getMimeType())));
			}

			for (MultiPartBinary partBinaty : request.getBinaryRQs()) {
				builder.addPart(
						partBinaty.getPartName(),
						new InputStreamBody(partBinaty.getData().openBufferedStream(), ContentType.parse(partBinaty.getContentType()), partBinaty
								.getFilename()));
			}

			post.setEntity(builder.build());
			
		} catch (Exception e) {
			throw new RestEndpointIOException("Unable to build post multipart request", e);
		}
		return executeInternal(post, new ClassConverterCallback<RS>(serializers, clazz));
	}

	@Override
	public <RQ, RS> RS post(String resource, Map<String, String> parameters, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
		HttpPost post = new HttpPost(spliceUrl(resource, parameters));
		Serializer serializer = getSupportedSerializer(rq);
		InputStreamEntity httpEntity = new InputStreamEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
		post.setEntity(httpEntity);
		return executeInternal(post, new ClassConverterCallback<RS>(serializers, clazz));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RestEndpoint#put(java.lang .String,
	 * java.lang.Object, java.lang.Class)
	 */
	@Override
	public <RQ, RS> RS put(String resource, RQ rq, Class<RS> clazz) throws RestEndpointIOException {
		HttpPut put = new HttpPut(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		InputStreamEntity httpEntity = new InputStreamEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
		put.setEntity(httpEntity);
		return executeInternal(put, new ClassConverterCallback<RS>(serializers, clazz));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RestEndpoint#put(java.lang.String,
	 * java.lang.Object, java.lang.reflect.Type)
	 */
	@Override
	public <RQ, RS> RS put(String resource, RQ rq, Type type) throws RestEndpointIOException {
		HttpPut put = new HttpPut(spliceUrl(resource));
		Serializer serializer = getSupportedSerializer(rq);
		InputStreamEntity httpEntity = new InputStreamEntity(serializer.serialize(rq), ContentType.create(serializer.getMimeType()));
		put.setEntity(httpEntity);
		return executeInternal(put, new TypeConverterCallback<RS>(serializers, type));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RestEndpoint#delete(java.
	 * lang.String, java.lang.Class)
	 */
	@Override
	public <RS> RS delete(String resource, Class<RS> clazz) throws RestEndpointIOException {
		HttpDelete delete = new HttpDelete(spliceUrl(resource));
		return executeInternal(delete, new ClassConverterCallback<RS>(serializers, clazz));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see RestEndpoint#get(java.lang .String,
	 * java.lang.Class)
	 */
	@Override
	public <RS> RS get(String resource, Class<RS> clazz) throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource));
		return executeInternal(get, new ClassConverterCallback<RS>(serializers, clazz));
	}

	@Override
	public <RS> RS get(String resource, Type type) throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource));
		return executeInternal(get, new TypeConverterCallback<RS>(serializers, type));
	}

	@Override
	public <RS> RS get(String resource, Map<String, String> parameters, Class<RS> clazz) throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource, parameters));
		return executeInternal(get, new ClassConverterCallback<RS>(serializers, clazz));
	}

	@Override
	public <RS> RS get(String resource, Map<String, String> parameters, Type type) throws RestEndpointIOException {
		HttpGet get = new HttpGet(spliceUrl(resource, parameters));
		return executeInternal(get, new TypeConverterCallback<RS>(serializers, type));
	}

	/**
	 * Executes request command
	 * 
	 * @param command
	 * @return
	 * @throws RestEndpointIOException
	 * @throws RestClientException
	 * @throws URISyntaxException
	 */
	@Override
	public <RQ, RS> RS executeRequest(RestCommand<RQ, RS> command) throws RestEndpointIOException {
		URI uri = spliceUrl(command.getUri());
		HttpUriRequest rq = null;
		Serializer serializer = null;
		switch (command.getHttpMethod()) {
		case GET:
			rq = new HttpGet(uri);
			break;
		case POST:
			serializer = getSupportedSerializer(command.getRequest());
			rq = new HttpPost(uri);
			((HttpPost) rq).setEntity(new InputStreamEntity(serializer.serialize(command.getRequest()), ContentType.create(
					serializer.getMimeType())));
			break;
		case PUT:
			serializer = getSupportedSerializer(command.getRequest());
			rq = new HttpPut(uri);
			((HttpPut) rq).setEntity(new InputStreamEntity(serializer.serialize(command.getRequest()), ContentType.create(
					serializer.getMimeType())));
			break;
		case DELETE:
			rq = new HttpDelete(uri);
			break;
		case PATCH:
			serializer = getSupportedSerializer(command.getRequest());
			rq = new HttpPatch(uri);
			((HttpPatch) rq).setEntity(new InputStreamEntity(serializer.serialize(command.getRequest()), ContentType.create(
					serializer.getMimeType())));
			break;
		default:
			throw new IllegalArgumentException("Method '" + command.getHttpMethod() + "' is unsupported");
		}

		return executeInternal(rq, new TypeConverterCallback<RS>(serializers, command.getType()));
	}

	/**
	 * Splice base URL and URL of resource
	 * 
	 * @param resource
	 * @return
	 * @throws RestEndpointIOException
	 */
	private URI spliceUrl(String resource) throws RestEndpointIOException {
		try {
			return new URIBuilder(baseUrl).setPath(resource).build();
		} catch (URISyntaxException e) {
			throw new RestEndpointIOException("Unable to builder URL with base url '" + baseUrl + "' and resouce '" + resource + "'", e);
		}
	}

	/**
	 * Splice base URL and URL of resource
	 * 
	 * @param resource
	 * @return
	 * @throws RestEndpointIOException
	 */
	private URI spliceUrl(String resource, Map<String, String> parameters) throws RestEndpointIOException {
		try {
			URIBuilder builder = new URIBuilder(baseUrl).setPath(resource);
			for (Entry<String, String> parameter : parameters.entrySet()) {
				builder.addParameter(parameter.getKey(), parameter.getValue());
			}
			return builder.build();
		} catch (URISyntaxException e) {
			throw new RestEndpointIOException("Unable to builder URL with base url '" + baseUrl + "' and resouce '" + resource + "'", e);
		}
	}

	private Serializer getSupportedSerializer(Object o) throws SerializerException {
		for (Serializer s : serializers) {
			if (s.canWrite(o)) {
				return s;
			}
		}
		throw new SerializerException("Unable to find serializer for object with type '" + o.getClass() + "'");
	}

	/**
	 * Executes {@link org.apache.http.client.methods.HttpUriRequest}
	 * 
	 * @param rq
	 *            - Request
	 * @param clazz
	 *            - Response Body Type
	 * @return - Serialized Response Body
	 * @throws RestEndpointIOException
	 */
	private <RS> RS executeInternal(HttpUriRequest rq, HttpEntityCallback<RS> callback) throws RestEndpointIOException {
		try {
			HttpResponse response = httpClient.execute(rq);
			if (errorHandler.hasError(response)) {
				errorHandler.handle(response);
			}

			HttpEntity entity = response.getEntity();
			return callback.callback(entity);

		} catch (SerializerException e) {
			throw e;
		} catch (IOException e) {
			throw new RestEndpointIOException("Unable to execute request", e);
		} finally {
			rq.abort();
		}
	}

	private static abstract class HttpEntityCallback<RS> {

		protected List<Serializer> serializers;

		public HttpEntityCallback(List<Serializer> serializers) {
			this.serializers = serializers;
		}

		protected Serializer getSupported(String contentType) throws SerializerException {
			for (Serializer s : serializers) {
				if (s.canRead(contentType)) {
					return s;
				}
			}
			throw new SerializerException("Unsupported media type '" + contentType);
		}

		abstract public RS callback(HttpEntity entity) throws SerializerException, IOException;
	}

	private static class TypeConverterCallback<RS> extends HttpEntityCallback<RS> {

		private Type type;

		public TypeConverterCallback(List<Serializer> serializers, Type type) {
			super(serializers);
			this.type = type;
		}

		@Override
		public RS callback(HttpEntity entity) throws SerializerException, IOException {
			return getSupported(entity.getContentType().getValue()).deserialize(EntityUtils.toByteArray(entity), type);
		}

	}

	private static class ClassConverterCallback<RS> extends HttpEntityCallback<RS> {

		private Class<RS> clazz;

		public ClassConverterCallback(List<Serializer> serializers, Class<RS> clazz) {
			super(serializers);
			this.clazz = clazz;
		}

		@Override
		public RS callback(HttpEntity entity) throws SerializerException, IOException {
			return getSupported(entity.getContentType().getValue()).deserialize(EntityUtils.toByteArray(entity), clazz);
		}

	}
}