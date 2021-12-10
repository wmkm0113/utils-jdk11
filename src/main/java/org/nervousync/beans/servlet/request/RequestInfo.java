/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.beans.servlet.request;

import java.io.File;
import java.io.Serializable;
import java.net.Proxy;
import java.util.*;

import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.web.HttpMethodOption;
import org.nervousync.commons.http.cert.TrustCert;
import org.nervousync.commons.http.cookie.CookieEntity;
import org.nervousync.commons.http.header.SimpleHeader;
import org.nervousync.commons.http.proxy.ProxyInfo;

/**
 * Request information for sending by com.nervousync.utils.RequestUtils
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Aug 25, 2017 11:04:17 AM $
 */
public final class RequestInfo implements Serializable {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 7350946565537957232L;

	/**
	 * Http method define
	 * @see HttpMethodOption
	 */
	private final HttpMethodOption httpMethodOption;
	/**
	 * Proxy server configuration
	 */
	private final ProxyInfo proxyInfo;
	/**
	 * Custom certificate info
	 */
	private final List<TrustCert> trustTrustCerts;
	/**
	 * Default pass phrase
	 */
	private final String passPhrase;
	/**
	 * User agent
	 */
	private final String userAgent;
	/**
	 * Request URL
	 */
	private final String requestUrl;
	/**
	 * Charset encoding
	 */
	private final String charset;
	/**
	 * Content type
	 */
	private final String contentType;
	/**
	 * Request timeout
	 */
	private final int timeOut;
	/**
	 * Post ata arrays
	 */
	private final byte[] postDatas;
	/**
	 * Send header values of request
	 */
	private final List<SimpleHeader> headers;
	/**
	 * Send form fields of request
	 */
	private final Map<String, String[]> parameters;
	/**
	 * Send multipart fields of request
	 */
	private final Map<String, File> uploadParams;
	/**
	 * Cookie list
	 */
	private final List<CookieEntity> cookieList;

	/**
	 * Constructor for given http method, request URL, and many options
	 *
	 * @param httpMethodOption Request http method
	 * @param requestUrl       Target request url
	 * @param charset          Charset encoding
	 * @param timeOut          Request timeout setting
	 * @param headers          Send header information of request
	 * @param parameters       Send parameter information of request
	 * @param uploadParams      Send multipart files of request
	 */
	private RequestInfo(HttpMethodOption httpMethodOption, ProxyInfo proxyInfo, List<TrustCert> trustTrustCerts,
	                    String passPhrase, String userAgent, String requestUrl, String charset,
	                    String contentType, int timeOut, byte[] postDatas, List<SimpleHeader> headers,
	                    List<CookieEntity> cookieList, Map<String, String[]> parameters,
	                    Map<String, File> uploadParams) {
		this.httpMethodOption = httpMethodOption;
		this.proxyInfo = proxyInfo;
		this.trustTrustCerts = trustTrustCerts;
		this.passPhrase = passPhrase;
		this.userAgent = userAgent;
		this.requestUrl = requestUrl;
		this.charset = charset;
		this.contentType = contentType;
		this.timeOut = timeOut;
		this.postDatas = postDatas;
		this.headers = headers;
		this.parameters = parameters;
		this.uploadParams = uploadParams;
		this.cookieList = cookieList;
	}

	/**
	 * Builder request builder.
	 *
	 * @param httpMethodOption the http method option
	 * @return the request builder
	 */
	public static RequestBuilder builder(HttpMethodOption httpMethodOption) {
		return new RequestBuilder(httpMethodOption);
	}

	/**
	 * Gets serial version uid.
	 *
	 * @return the serial version uid
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * Gets http method option.
	 *
	 * @return the http method option
	 */
	public HttpMethodOption getHttpMethodOption() {
		return httpMethodOption;
	}

	/**
	 * Gets upload params.
	 *
	 * @return the upload params
	 */
	public Map<String, File> getUploadParams() {
		return uploadParams;
	}

	/**
	 * Gets cookie list.
	 *
	 * @return the cookie list
	 */
	public List<CookieEntity> getCookieList() {
		return cookieList;
	}

	/**
	 * Gets proxy info.
	 *
	 * @return the proxy info
	 */
	public ProxyInfo getProxyInfo() {
		return proxyInfo;
	}

	/**
	 * Gets trust cert infos.
	 *
	 * @return the trust cert infos
	 */
	public List<TrustCert> getTrustCertInfos() {
		return trustTrustCerts;
	}

	/**
	 * Gets pass phrase.
	 *
	 * @return the pass phrase
	 */
	public String getPassPhrase() {
		return passPhrase;
	}

	/**
	 * Gets user agent.
	 *
	 * @return the user agent
	 */
	public String getUserAgent() {
		return userAgent;
	}

	/**
	 * Gets request url.
	 *
	 * @return the request url
	 */
	public String getRequestUrl() {
		return requestUrl;
	}

	/**
	 * Gets charset.
	 *
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Gets content type.
	 *
	 * @return the content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Gets time out.
	 *
	 * @return the time out
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * Get post datas byte [ ].
	 *
	 * @return the byte [ ]
	 */
	public byte[] getPostDatas() {
		return postDatas;
	}

	/**
	 * Gets headers.
	 *
	 * @return the headers
	 */
	public List<SimpleHeader> getHeaders() {
		return headers;
	}

	/**
	 * Gets parameters.
	 *
	 * @return the parameters
	 */
	public Map<String, String[]> getParameters() {
		return parameters;
	}

	/**
	 * Gets upload param.
	 *
	 * @return the upload param
	 */
	public Map<String, File> getUploadParam() {
		return uploadParams;
	}

	/**
	 * The type Request builder.
	 */
	public static final class RequestBuilder implements Serializable {

		private static final long serialVersionUID = -2600183788360275158L;

		/**
		 * Http method define
		 * @see HttpMethodOption
		 */
		private final HttpMethodOption httpMethodOption;
		/**
		 * Proxy server configuration
		 */
		private ProxyInfo proxyInfo;
		/**
		 * Custom certificate info
		 */
		private final List<TrustCert> trustTrustCerts = new ArrayList<>();
		/**
		 * Default pass phrase
		 */
		private String passPhrase;
		/**
		 * User agent
		 */
		private String userAgent;
		/**
		 * Request URL
		 */
		private String requestUrl;
		/**
		 * Charset encoding
		 */
		private String charset;
		/**
		 * Content type
		 */
		private String contentType;
		/**
		 * Request timeout
		 */
		private int timeOut = Globals.DEFAULT_TIME_OUT;
		/**
		 * Post ata arrays
		 */
		private byte[] postDatas;
		/**
		 * Send header values of request
		 */
		private final List<SimpleHeader> headers = new ArrayList<>();
		/**
		 * Send form fields of request
		 */
		private final Map<String, String[]> parameters = new HashMap<>();
		/**
		 * Send multipart fields of request
		 */
		private final Map<String, File> uploadParams = new HashMap<>();
		/**
		 * Cookie list
		 */
		private final List<CookieEntity> cookieList = new ArrayList<>();

		private RequestBuilder(HttpMethodOption httpMethodOption) {
			this.httpMethodOption = httpMethodOption;
		}

		/**
		 * Build request info.
		 *
		 * @return the request info
		 */
		public RequestInfo build() {
			return new RequestInfo(this.httpMethodOption, this.proxyInfo, this.trustTrustCerts, this.passPhrase,
					this.userAgent, this.requestUrl, this.charset, this.contentType, this.timeOut,
					this.postDatas, this.headers, this.cookieList, this.parameters, this.uploadParams);
		}

		/**
		 * Add ProxyInfo
		 *
		 * @param proxyType    Proxy type
		 * @param proxyAddress Proxy server address
		 * @return Proxy info instance
		 */
		public RequestBuilder configProxyInfo(Proxy.Type proxyType, String proxyAddress) {
			this.proxyInfo = ProxyInfo.newInstance(proxyType, proxyAddress);
			return this;
		}

		/**
		 * Add ProxyInfo
		 *
		 * @param proxyType    Proxy type
		 * @param proxyAddress Proxy server address
		 * @param proxyPort    Proxy server port
		 * @return Proxy info instance
		 */
		public RequestBuilder configProxyInfo(Proxy.Type proxyType, String proxyAddress, int proxyPort) {
			this.proxyInfo = ProxyInfo.newInstance(proxyType, proxyAddress, proxyPort);
			return this;
		}

		/**
		 * Add ProxyInfo
		 *
		 * @param proxyType    Proxy type
		 * @param proxyAddress Proxy server address
		 * @param proxyPort    Proxy server port
		 * @param userName     Proxy server user name
		 * @param password     Proxy server password
		 * @return Proxy info instance
		 */
		public RequestBuilder configProxyInfo(Proxy.Type proxyType, String proxyAddress,
		                                      int proxyPort, String userName, String password) {
			this.proxyInfo = ProxyInfo.newInstance(proxyType, proxyAddress, proxyPort, userName, password);
			return this;
		}

		/**
		 * Add cert info.
		 *
		 * @param certPath     the cert path
		 * @param certPassword the cert password
		 * @return the cert info
		 */
		public RequestBuilder addTrustCertificate(String certPath, String certPassword) {
			this.trustTrustCerts.add(TrustCert.newInstance(certPath, certPassword));
			return this;
		}

		/**
		 * Add cert info.
		 *
		 * @param certContent  the cert content
		 * @param certPassword the cert password
		 * @return the cert info
		 */
		public RequestBuilder addTrustCertificate(byte[] certContent, String certPassword) {
			this.trustTrustCerts.add(TrustCert.newInstance(certContent, certPassword));
			return this;
		}

		/**
		 * Pass phrase request builder.
		 *
		 * @param passPhrase the pass phrase
		 * @return the request builder
		 */
		public RequestBuilder passPhrase(String passPhrase) {
			this.passPhrase = passPhrase;
			return this;
		}

		/**
		 * User agent request builder.
		 *
		 * @param userAgent the user agent
		 * @return the request builder
		 */
		public RequestBuilder userAgent(String userAgent) {
			this.userAgent = userAgent;
			return this;
		}

		/**
		 * Request url request builder.
		 *
		 * @param requestUrl the request url
		 * @return the request builder
		 */
		public RequestBuilder requestUrl(String requestUrl) {
			this.requestUrl = requestUrl;
			return this;
		}

		/**
		 * Charset request builder.
		 *
		 * @param charset the charset
		 * @return the request builder
		 */
		public RequestBuilder charset(String charset) {
			this.passPhrase = charset;
			return this;
		}

		/**
		 * Content type request builder.
		 *
		 * @param contentType the content type
		 * @return the request builder
		 */
		public RequestBuilder contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		/**
		 * Time out request builder.
		 *
		 * @param timeOut the time out
		 * @return the request builder
		 */
		public RequestBuilder timeOut(int timeOut) {
			this.timeOut = timeOut;
			return this;
		}

		/**
		 * Post datas request builder.
		 *
		 * @param postDatas the post datas
		 * @return the request builder
		 */
		public RequestBuilder postDatas(byte[] postDatas) {
			this.postDatas = postDatas;
			return this;
		}

		/**
		 * Add header request builder.
		 *
		 * @param headerName  the header name
		 * @param headerValue the header value
		 * @return the request builder
		 */
		public RequestBuilder addHeader(String headerName, String headerValue) {
			this.headers.add(new SimpleHeader(headerName, headerValue));
			return this;
		}

		/**
		 * Add parameter request builder.
		 *
		 * @param parameterName   the parameter name
		 * @param parameterValues the parameter values
		 * @return the request builder
		 */
		public RequestBuilder addParameter(String parameterName, String[] parameterValues) {
			this.parameters.put(parameterName, parameterValues);
			return this;
		}

		/**
		 * Add upload param request builder.
		 *
		 * @param parameterName  the parameter name
		 * @param parameterValue the parameter value
		 * @return the request builder
		 */
		public RequestBuilder addUploadParam(String parameterName, File parameterValue) {
			this.uploadParams.put(parameterName, parameterValue);
			return this;
		}

		/**
		 * Add cookies request builder.
		 *
		 * @param cookieEntity the cookie entity
		 * @return the request builder
		 */
		public RequestBuilder addCookies(CookieEntity cookieEntity) {
			this.cookieList.add(cookieEntity);
			return this;
		}

		/**
		 * Add cookies request builder.
		 *
		 * @param responseCookieValue the response cookie value
		 * @return the request builder
		 */
		public RequestBuilder addCookies(String responseCookieValue) {
			this.cookieList.add(new CookieEntity(responseCookieValue));
			return this;
		}
	}
}
