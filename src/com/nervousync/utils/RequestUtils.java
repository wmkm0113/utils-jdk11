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
package com.nervousync.utils;

import com.nervousync.commons.beans.servlet.request.RequestAttribute;
import com.nervousync.commons.beans.servlet.request.RequestInfo;
import com.nervousync.commons.beans.servlet.response.HttpResponseContent;
import com.nervousync.commons.core.Globals;
import com.nervousync.commons.core.RegexGlobals;
import com.nervousync.commons.http.cookie.CookieInfo;
import com.nervousync.commons.http.entity.HttpEntity;
import com.nervousync.commons.http.header.SimpleHeader;
import com.nervousync.commons.http.proxy.ProxyInfo;
import com.nervousync.enumerations.web.HttpMethodOption;

import javax.jws.WebService;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.handler.HandlerResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request Utils
 * 
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 11:23:13 AM $
 */
public final class RequestUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);
	
	private static final String STOWED_REQUEST_ATTRIBS = "ssl.redirect.attrib.stowed";
	private static final int DEFAULT_TIME_OUT = 5;
	
	private static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_POST = "POST";
	private static final String HTTP_METHOD_PUT = "PUT";
	private static final String HTTP_METHOD_TRACE = "TRACE";
	private static final String HTTP_METHOD_HEAD = "HEAD";
	private static final String HTTP_METHOD_DELETE = "DELETE";
	private static final String HTTP_METHOD_OPTIONS = "OPTIONS";

	static {
		try {
			RequestUtils.initTrustManager("changeit");
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Initialize trust manager error! ", e);
			}
		}
	}
	
	private RequestUtils() {
		
	}
	
	public static String resolveDomain(String domainName) {
		try {
			return InetAddress.getByName(domainName).getHostAddress();
		} catch (UnknownHostException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Resolve domain error! ", e);
			}
		}
		return null;
	}
	
	public static String convertIPv4ToCompatibleIPv6(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return "::" + ipAddress;
		}
		return null;
	}
	
	public static String convertIPv4ToIPv6(String ipAddress) {
		return convertIPv4ToIPv6(ipAddress, true);
	}
	
	public static String convertIPv4ToIPv6(String ipAddress, boolean collapse) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddr = StringUtils.tokenizeToStringArray(ipAddress, ".");
			StringBuilder stringBuilder = null;
			if (collapse) {
				stringBuilder = new StringBuilder(":");
			} else {
				stringBuilder = new StringBuilder("0000:0000:0000:0000:0000:0000");
			}
			int index = 0;
			for (String addrItem : splitAddr) {
				if (index % 2 == 0) {
					stringBuilder.append(":");
				}
				stringBuilder.append(Integer.toHexString(Integer.parseInt(addrItem)));
				index++;
			}
			
			return stringBuilder.toString().toUpperCase();
		}
		return null;
	}
	
	public static byte[] convertIPv4ToBytes(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddr = StringUtils.tokenizeToStringArray(ipAddress, ".");
			byte[] addrBytes = new byte[4];
			
			addrBytes[0] = (byte)Integer.parseInt(splitAddr[0]);
			addrBytes[1] = (byte)Integer.parseInt(splitAddr[1]);
			addrBytes[2] = (byte)Integer.parseInt(splitAddr[2]);
			addrBytes[3] = (byte)Integer.parseInt(splitAddr[3]);
			
			return addrBytes;
		}
		return null;
	}
	
	public static BigInteger convertIPtoBigInteger(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return RequestUtils.convertIPv4ToBigInteger(ipAddress);
		} else {
			return RequestUtils.convertIPv6ToBigInteger(ipAddress);
		}
	}

	public static BigInteger convertIPv4ToBigInteger(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddr = StringUtils.tokenizeToStringArray(ipAddress, ".");
			if (splitAddr.length == 4) {
				BigInteger bigInteger = BigInteger.ZERO;
				
				for (int i = 0 ; i < splitAddr.length ; i++) {
					BigInteger currentInteger = BigInteger.valueOf(Long.parseLong(splitAddr[3 - i])).shiftLeft(i * 8);
					bigInteger = bigInteger.add(currentInteger);
				}
				return bigInteger;
			}
		}
		return null;
	}
	
	public static BigInteger convertIPv6ToBigInteger(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV6_REGEX)) {
			ipAddress = appendIgnore(ipAddress);
			String[] splitAddr = StringUtils.tokenizeToStringArray(ipAddress, ":");
			if (splitAddr.length == 8) {
				BigInteger bigInteger = BigInteger.ZERO;
				int index = 0;
				for (String split : splitAddr) {
					BigInteger currentInteger = null;
					if (StringUtils.matches(split, RegexGlobals.IPV4_REGEX)) {
						currentInteger = convertIPv4ToBigInteger(split);
					} else {
						currentInteger = BigInteger.valueOf(Long.valueOf(split, 16));
					}
					bigInteger = bigInteger.add(currentInteger.shiftLeft(16 * (splitAddr.length - index - 1)));
					index++;
				}
				return bigInteger;
			}
		}
		return null;
	}
	
	public static String convertBigIntegerToIPv4(BigInteger bigInteger) {
		String ipv4Addr = "";
		BigInteger ff = BigInteger.valueOf(0xFFL);
		
		for (int i = 0 ; i < 4 ; i++) {
			ipv4Addr = bigInteger.and(ff).toString() + "." + ipv4Addr;
			bigInteger = bigInteger.shiftRight(8);
		}
		
		return ipv4Addr.substring(0, ipv4Addr.length() - 1);
	}
	
	public static String convertBigIntegerToIPv6Addr(BigInteger bigInteger) {
		String ipv6Addr = "";
		BigInteger ff = BigInteger.valueOf(0xFFFFL);
		
		for (int i = 0 ; i < 8 ; i++) {
			ipv6Addr = bigInteger.and(ff).toString(16) + ":" + ipv6Addr;
			bigInteger = bigInteger.shiftRight(16);
		}
		
		return ipv6Addr.substring(0, ipv6Addr.length() - 1).replaceFirst(RegexGlobals.IPV6_COMPRESS_REGEX, "::");
	}
	
	public static <T> T generateSOAPClient(String endPointUrl, Class<T> serviceEndpointInterface, HandlerResolver handlerResolver) 
			throws MalformedURLException {
		if (endPointUrl == null || endPointUrl.length() == 0 || serviceEndpointInterface == null 
				|| !serviceEndpointInterface.isAnnotationPresent(WebService.class)) {
			return null;
		}
		
		URL wsdlDocumentLocation = new URL(endPointUrl);
		
		WebService webService = serviceEndpointInterface.getAnnotation(WebService.class);
		
		String namespaceURI = webService.targetNamespace();
		String serviceName = webService.serviceName();
		String portName = webService.portName();
		
		if (namespaceURI == null || namespaceURI.length() == 0) {
			String packageName = serviceEndpointInterface.getPackage().getName();
			String[] packageNames = StringUtils.tokenizeToStringArray(packageName, ".");
			namespaceURI = wsdlDocumentLocation.getProtocol() + "://";
			
			for (int i = packageNames.length - 1 ; i >= 0 ; i--) {
				namespaceURI += (packageNames[i] + ".");
			}
			
			namespaceURI = namespaceURI.substring(0, namespaceURI.length() - 1) + "/";
		}
		
		if (serviceName == null || serviceName.length() == 0) {
			serviceName = serviceEndpointInterface.getSimpleName() + "Service";
		}
		
		if (portName == null || portName.length() == 0) {
			portName = serviceEndpointInterface.getSimpleName() + "Port";
		}
		
		Service service = Service.create(wsdlDocumentLocation, new QName(namespaceURI, serviceName));
		if (handlerResolver != null) {
			service.setHandlerResolver(handlerResolver);
		}
		
		return service.getPort(new QName(namespaceURI, portName), serviceEndpointInterface);
	}
	
	public static long retrieveContentLength(String requestUrl) throws UnsupportedEncodingException {
		HttpResponseContent httpResponseContent = sendRequest(requestUrl, HttpMethodOption.HEAD);
		long contentLength = Globals.DEFAULT_VALUE_LONG;
		if (httpResponseContent.getStatusCode() == HttpURLConnection.HTTP_OK) {
			contentLength = httpResponseContent.getContentLength();
		}
		return contentLength;
	}

	public static HttpResponseContent sendRequest(String requestUrl) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				beginPosition, endPosition, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data) 
			throws UnsupportedEncodingException {
		return sendRequest(requestUrl, data, null, HttpMethodOption.DEFAULT, Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT);
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(requestUrl, data, null, HttpMethodOption.DEFAULT, beginPosition, endPosition);
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, int timeOut) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, 
			int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, 
			int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				beginPosition, endPosition, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				beginPosition, endPosition, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, HttpMethodOption httpMethodOption, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, HttpMethodOption httpMethodOption, 
			int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, HttpMethodOption httpMethodOption, 
			int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				beginPosition, endPosition, null, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(requestUrl, data, null, httpMethodOption, beginPosition, endPosition);
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		
		return sendRequest(new RequestInfo(HttpMethodOption.GET, requestUrl, timeOut, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption, int timeOut) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				beginPosition, endPosition, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			HttpMethodOption httpMethodOption, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			List<SimpleHeader> headers, HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			List<SimpleHeader> headers, HttpMethodOption httpMethodOption, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, null, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, null, parameters, null));
	}
	
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			Map<String, File> uploadParam, List<SimpleHeader> headers, HttpMethodOption httpMethodOption) 
					throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, uploadParam));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			Map<String, File> uploadParam, List<SimpleHeader> headers, 
			HttpMethodOption httpMethodOption, int timeOut) 
					throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, uploadParam));
	}
	
	public static HttpResponseContent sendRequest(RequestInfo requestInfo) {
		return RequestUtils.sendRequest(requestInfo, null);
	}
	
	public static HttpResponseContent sendRequest(RequestInfo requestInfo, List<CookieInfo> cookieInfos) {
		HttpURLConnection urlConnection = null;
		OutputStream outputStream = null;
		
		try {
			urlConnection = openConnection(requestInfo, cookieInfos);
			int timeout = requestInfo.getTimeOut() == Globals.DEFAULT_VALUE_INT ? DEFAULT_TIME_OUT : requestInfo.getTimeOut();
			urlConnection.setConnectTimeout(timeout * 1000);
			urlConnection.setReadTimeout(timeout * 1000);
			
			HttpEntity httpEntity = generateEntity(requestInfo.getParameters(), requestInfo.getUploadParam());
			
			urlConnection.setRequestProperty("Content-Type", 
					httpEntity.generateContentType(requestInfo.getCharset(), requestInfo.getHttpMethodOption()));
			
			if (HttpMethodOption.POST.equals(requestInfo.getHttpMethodOption()) 
					|| HttpMethodOption.PUT.equals(requestInfo.getHttpMethodOption())) {
				outputStream = urlConnection.getOutputStream();
				httpEntity.writeData(requestInfo.getCharset(), outputStream);
			}
			
			String redirectUrl = urlConnection.getHeaderField("Location");
			if (redirectUrl != null) {
				if (cookieInfos == null) {
					cookieInfos = new ArrayList<CookieInfo>();
				}
				
				Iterator<Entry<String, List<String>>> iterator = urlConnection.getHeaderFields().entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, List<String>> entry = iterator.next();
					if ("Set-Cookie".equals(entry.getKey())) {
						for (String cookieValue : entry.getValue()) {
							cookieInfos.add(new CookieInfo(cookieValue));
						}
					}
				}
				
				return RequestUtils.sendRequest(new RequestInfo(redirectUrl, requestInfo), cookieInfos);
			}
			return new HttpResponseContent(urlConnection);
		} catch (Exception e) {
			if (RequestUtils.LOGGER.isDebugEnabled()) {
				RequestUtils.LOGGER.debug("Send Request ERROR: ", e);
			}
			return null;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					
				}
			}
			
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}
	
	public static void initTrustManager(String passPhrase) throws Exception {
		NervousyncX509TrustManager.init(passPhrase);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(new KeyManager[0], new TrustManager[]{NervousyncX509TrustManager.getInstance()}, new SecureRandom());
		SSLContext.setDefault(sslContext);
	}
	
	public static void addCustomCert(String certPath, String passPhrase) throws Exception {
		NervousyncX509TrustManager.getInstance().addCustomCert(new TrustedCert(certPath, passPhrase));
	}

	public static void removeCustomCert(String certPath, String passPhrase) throws Exception {
		NervousyncX509TrustManager.getInstance().removeCustomCert(certPath);
	}
	
	/**
	 * Creates query String from request body parameters
     *
     * @param request The request that will supply parameters
     * @return Query string corresponding to that request parameters
	 */
	public static String getRequestParameters(HttpServletRequest request) {
		// set the ALGORIGTHM as defined for the application
		//ALGORITHM = (String) aRequest.getAttribute(Constants.ENC_ALGORITHM);
		Map<String, String[]> m = request.getParameterMap();
		return createQueryStringFromMap(m, "&").toString();
	}

	/**
	 * Creates map of request parameters from given query string. Values are
     * decoded.
	 *
	 * @param queryString Query string to get parameters from
	 * @return Map with request parameters mapped to their values
	 */
	public static Map<String, String[]> getRequestParametersFromString(String queryString) {
        return getRequestParametersFromString(queryString, true);
    }

	/**
	 * Creates map of request parameters from given query string
	 *
	 * @param queryString Query string to get parameters from
     * @param decodeValues Whether to decode values (which are URL-encoded)
	 * @return Map with request parameters mapped to their values
	 */
	public static Map<String, String[]> getRequestParametersFromString(String queryString,
                                                     boolean decodeValues) {
		HashMap<String, String[]> parameterMap = new HashMap<String, String[]>();
		
		if (queryString == null) {
			return parameterMap;
		}
		
		for (int k = 0; k < queryString.length();) {
			int ampPos = queryString.indexOf('&', k);
			if (ampPos == -1) {
				ampPos = queryString.length();
			}
			String parameter = queryString.substring(k, ampPos);
			int equalsSignPos = parameter.indexOf('=');
            if (equalsSignPos != -1) {
                String key = parameter.substring(0, equalsSignPos);
                String value = parameter.substring(equalsSignPos + 1).trim();
                try {
                    key = URLDecoder.decode(key, Globals.DEFAULT_ENCODING);
                    if (decodeValues) {
                        value = URLDecoder.decode(value, Globals.DEFAULT_ENCODING);
                    }
                    parameterMap.put(key, mergeValues(parameterMap.get(key), value));
                } catch (UnsupportedEncodingException e) {
                    // do nothing
                }
            }
			k = ampPos + 1;
		}

		return parameterMap;
	}


    /**
     * Creates a map of request parameters from URI.
     *
     * @param uri An address to extract request parameters from
     * @return map of request parameters
     */
	public static Map<String, String[]> getRequestParametersFromUri(String uri) {
        if (ObjectUtils.isNull(uri) || uri.trim().length() == 0) {
            return new HashMap<String, String[]>();
        }

        int qSignPos = uri.indexOf('?');
        if (qSignPos == -1) {
            return new HashMap<String, String[]>();
        }

        return RequestUtils.getRequestParametersFromString(uri.substring(qSignPos + 1));
    }


    /**
     * Extracts a base address from URI (that is, part of address before '?')
     *
     * @param uri An address to extract base address from
     * @return base address
     */
    public static String getBaseFromUri(String uri) {
        if (ObjectUtils.isNull(uri) || uri.trim().length() == 0) {
            return "";
        }

        int qSignPos = uri.indexOf('?');
        if (qSignPos == -1) {
            return uri;
        }

        return uri.substring(0, qSignPos);
    }


	/**
	 * Builds a query string from a given map of parameters
	 *
	 * @param m         A map of parameters
	 * @param ampersand String to use for ampersands (e.g. "&amp;" or "&amp;amp;")
	 * @param encode    Whether or not to encode non-ASCII characters
	 * @return query string (with no leading "?")
	 */
	public static StringBuffer createQueryStringFromMap(Map<String, String[]> m, String ampersand, boolean encode) {
		StringBuffer result = new StringBuffer("");
		Set<Entry<String, String[]>> entrySet = m.entrySet();
		Iterator<Entry<String, String[]>> entrySetIterator = entrySet.iterator();

		while (entrySetIterator.hasNext()) {
			Entry<String, String[]> entry = entrySetIterator.next();
			String[] values = entry.getValue();

			if (values == null) {
				append(entry.getKey(), "", result, ampersand, encode);
			} else {
				for (int i = 0; i < values.length; i++) {
					append(entry.getKey(), values[i], result, ampersand, encode);
				}
			}
		}

		return result;
	}

	/**
	 * Builds a query string from a given map of parameters
	 *
	 * @param m         A map of parameters
	 * @param ampersand String to use for ampersands (e.g. "&amp;" or "&amp;amp;")
	 * @return query string (with no leading "?")
	 */
	public static StringBuffer createQueryStringFromMap(Map<String, String[]> m, String ampersand) {
		return createQueryStringFromMap(m, ampersand, true);
	}

    /**
     * Append parameters to base URI.
     *
     * @param uri An address that is base for adding params
     * @param params A map of parameters
     * @return resulting URI
     */
    public static String appendParams(String uri, Map<String, String[]> params) {
        String delim = (uri.indexOf('?') == -1) ? "?" : "&";
        return uri + delim + RequestUtils.createQueryStringFromMap(params, "&").toString();
    }

	/**
	 * Stores request attributes in session
	 *
	 * @param aRequest the current request
	 */
	public static void stowRequestAttributes(HttpServletRequest aRequest) {
		if (aRequest.getSession().getAttribute(STOWED_REQUEST_ATTRIBS) != null) {
			return;
		}
		
		aRequest.getSession().setAttribute(STOWED_REQUEST_ATTRIBS, RequestAttribute.newInstance(aRequest));
	}

	/**
	 * Returns request attributes from session to request
	 *
	 * @param aRequest a request to which saved in session parameters will be
     * assigned
	 */
	public static void reclaimRequestAttributes(HttpServletRequest aRequest) {
		RequestAttribute requestAttribute =
		        (RequestAttribute)aRequest.getSession().getAttribute(STOWED_REQUEST_ATTRIBS);

		if (requestAttribute == null) {
			return;
		}
		
		Map<String, Object> attributeMap = requestAttribute.getAttributeMap();

		Iterator<String> itr = attributeMap.keySet().iterator();
		
		while (itr.hasNext()) {
			String name = (String) itr.next();
			aRequest.setAttribute(name, attributeMap.get(name));
		}

		aRequest.getSession().removeAttribute(STOWED_REQUEST_ATTRIBS);
	}

	/**
	 * Convenience method to get the application's URL based on request
	 * variables.
     *
     * @param request the request from which the URL is calculated
     * @return Application URL
	 */
	public static String getAppURL(HttpServletRequest request) {
		StringBuffer requestUrl = new StringBuffer();
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}
		String scheme = request.getScheme();
		requestUrl.append(scheme);
		requestUrl.append("://");
		requestUrl.append(request.getServerName());
		if ((scheme.equalsIgnoreCase("http") && (port != 80)) 
				|| (scheme.equalsIgnoreCase("https") && (port != 443))) {
			requestUrl.append(':');
			requestUrl.append(port);
		}
		requestUrl.append(request.getContextPath());
		return requestUrl.toString();
	}
    
    public static boolean isUserInRole(Iterator<Object> rolesIterator, HttpServletRequest request)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    	return isUserInRole(rolesIterator, null, request);
    }
    
	public static boolean isUserInRole(Iterator<Object> rolesIterator, String property, HttpServletRequest request)
		throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		if (rolesIterator != null) {
			while (rolesIterator.hasNext()) {
				Object bean = rolesIterator.next();
				if (bean == null) {
					continue;
				}
				String roleName = null;
				if (bean instanceof String) {
					roleName = (String) bean;
				} else if (property != null && property.trim().length() > 0) {
					roleName = String.valueOf(ReflectionUtils.getFieldValue(property, bean));
				} else {
					roleName = String.valueOf(bean);
				}
				
				if (request.isUserInRole(roleName.trim())) {
					return true;
				}
			}
		} else {
			// if no role is specified, grant access
			return true;
		}
		return false;
	}

	/**
	 * Get client IP address
	 * 
	 * @param request		HttpServletRequest Object
	 * @return client IP address
	 */
	public static String getClientIP(HttpServletRequest request) {
		
		//	如果使用了反向代理服务器，则需要从Header中获取转发的客户端IP地址
		String clientIP = request.getHeader("X-Forwarded-For");
		
		if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("Proxy-Client-IP");
		}

		if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("WL-Proxy-Client-IP");
		}

		if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("HTTP_CLIENT_IP");
		}

		if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if (clientIP == null || clientIP.length() == 0 || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getRemoteAddr();
		}
		
		int index = clientIP.indexOf(',');
		
		if (index == -1) {
			return clientIP;
		} else {
			//	使用了多级反向代理服务器
			return clientIP.substring(0, index);
		}
	}

	public static String getRequestURI(HttpServletRequest request) throws ServletException {
		if (request == null) {
			throw new ServletException("Request object must not be null");
		}
		String requestUrl = request.getRequestURI();
		
		if (request.getContextPath().length() > 0) {
			requestUrl = requestUrl.substring(request.getContextPath().length());
		}
		if (requestUrl.lastIndexOf('.') != -1) {
			return requestUrl.substring(0, requestUrl.lastIndexOf("."));
		}
		return requestUrl;
	}

	public static String getRequestPath(HttpServletRequest request) throws ServletException {
		if (request == null) {
			throw new ServletException("Request object must not be null");
		}
		String requestUrl = request.getRequestURI();
		
		if (request.getContextPath().length() > 0) {
			requestUrl = requestUrl.substring(request.getContextPath().length());
		}
		return requestUrl;
	}
	
	public static String getRequestUrl(HttpServletRequest request) throws Exception {
		return getRequestUrl(request, true);
	}
	
	public static String getRequestUrl(HttpServletRequest request, boolean includeDomain) throws Exception {
		if (request == null) {
			throw new Exception("Request object must not be null");
		}
		
		StringBuffer requestUrl = new StringBuffer();
		
		if (includeDomain) {
			requestUrl.append(RequestUtils.getAppURL(request));
		}
		
		requestUrl.append(request.getRequestURI());
		
		if (request.getQueryString() != null && request.getQueryString().length() > 0) {
			requestUrl.append("?" + request.getQueryString());
		}
		
		return requestUrl.toString();
	}
	
	public static String formatPath(String filePath) {
		return StringUtils.replace(filePath, Globals.DEFAULT_PAGE_SEPARATOR, "/");
	}
	
	public static String processRewrite(HttpServletRequest request, String regex, String toPath) throws Exception {
		String requestPath = RequestUtils.getRequestUrl(request, false);
		
		if (StringUtils.matches(requestPath, regex)) {
			String redirectPath = toPath;
			Matcher matcher = Pattern.compile(regex).matcher(requestPath);
			matcher.find();
			
			for (int i = 0 ; i < matcher.groupCount() ; i++) {
				int index = i + 1;
				redirectPath = StringUtils.replace(redirectPath, "$" + index, matcher.group(index));
			}
			
			return redirectPath;
		}
		
		return null;
	}

	private static String generateCookie(String requestUrl, List<CookieInfo> cookieInfos) {
		if (cookieInfos == null || cookieInfos.size() == 0) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		
		for (CookieInfo cookieInfo : cookieInfos) {
			if ((!requestUrl.startsWith(Globals.DEFAULT_PROTOCOL_PREFIX_HTTPS)
					&& cookieInfo.isSecure()) || (cookieInfo.getExpires() > DateTimeUtils.currentGMTTimeMillis())
					|| cookieInfo.getMaxAge() == Globals.DEFAULT_VALUE_LONG || cookieInfo.getMaxAge() == 0L) {
				continue;
			}
			
			String domain = null;
			String requestPath = null;
			
			if (requestUrl.startsWith(Globals.DEFAULT_PROTOCOL_PREFIX_HTTPS)) {
				domain = requestUrl.substring(Globals.DEFAULT_PROTOCOL_PREFIX_HTTPS.length());
			} else if (requestUrl.startsWith(Globals.DEFAULT_PROTOCOL_PREFIX_HTTP)) {
				domain = requestUrl.substring(Globals.DEFAULT_PROTOCOL_PREFIX_HTTP.length());
			} else {
				return null;
			}
			
			if (domain.indexOf("/") > 0) {
				requestPath = domain.substring(domain.indexOf("/") + 1);
				domain = domain.substring(0, domain.indexOf("/"));
			} else {
				requestPath = "/";
			}
			
			if (!domain.toLowerCase().endsWith(cookieInfo.getDomain().toLowerCase())
					|| !requestPath.startsWith(cookieInfo.getPath())) {
				continue;
			}
			
			stringBuilder.append("; " + cookieInfo.getName() + "=" + cookieInfo.getValue());
		}
		
		if (stringBuilder.length() == 0) {
			return null;
		}
		return stringBuilder.substring(2);
	}

	private static HttpURLConnection openConnection(RequestInfo requestInfo, List<CookieInfo> cookieInfos, 
			TrustedCert... trustedCerts) throws UnsupportedEncodingException {
		String method = null;
		switch (requestInfo.getHttpMethodOption()) {
		case GET:
			method = HTTP_METHOD_GET;
			break;
		case POST:
			method = HTTP_METHOD_POST;
			break;
		case PUT:
			method = HTTP_METHOD_PUT;
			break;
		case TRACE:
			method = HTTP_METHOD_TRACE;
			break;
		case HEAD:
			method = HTTP_METHOD_HEAD;
			break;
		case DELETE:
			method = HTTP_METHOD_DELETE;
			break;
		case OPTIONS:
			method = HTTP_METHOD_OPTIONS;
			break;
			default:
				throw new UnsupportedEncodingException("Unknown Request Method");
		}
		
		String urlAddress = null;

		if (HttpMethodOption.POST.equals(requestInfo.getHttpMethodOption()) 
				|| HttpMethodOption.PUT.equals(requestInfo.getHttpMethodOption())) {
			urlAddress = requestInfo.getRequestUrl();
		} else {
			urlAddress = appendParams(requestInfo.getRequestUrl(), requestInfo.getParameters());
		}
		
		HttpURLConnection connection = null;
		try {
			URL url = new URL(urlAddress);
			if (requestInfo.getProxyInfo() != null) {
				ProxyInfo proxyInfo = requestInfo.getProxyInfo();
				connection = (HttpURLConnection)url.openConnection(new Proxy(proxyInfo.getProxyType(), 
						new InetSocketAddress(proxyInfo.getProxyAddress(), proxyInfo.getProxyPort())));
				if (proxyInfo.getUserName() != null && proxyInfo.getUserName().length() > 0) {
					String authentication = proxyInfo.getUserName() + ":";
					if (proxyInfo.getPassword() != null && proxyInfo.getPassword().length() > 0) {
						authentication += proxyInfo.getPassword();
					}
					
					connection.setRequestProperty("Proxy-Authorization", 
							StringUtils.base64Encode(authentication.getBytes()));
				}
			} else {
				connection = (HttpURLConnection)url.openConnection();
			}
			connection.setRequestMethod(method);
			connection.setDoInput(true);
			if (HttpMethodOption.POST.equals(requestInfo.getHttpMethodOption()) 
					|| HttpMethodOption.PUT.equals(requestInfo.getHttpMethodOption())) {
				connection.setDoOutput(true);
			}
			connection.setRequestProperty("Accept", "text/html,text/javascript,text/xml");
			connection.addRequestProperty("Accept-Encoding", "gzip, deflate");
			connection.setRequestProperty("User-Agent", "NervousyncBot");
			String cookie = RequestUtils.generateCookie(requestInfo.getRequestUrl(), cookieInfos);
			if (cookie != null) {
				connection.setRequestProperty("Cookie", cookie);
			}
			
			if (urlAddress.startsWith(Globals.DEFAULT_PROTOCOL_PREFIX_HTTPS)) {
				((HttpsURLConnection)connection).setHostnameVerifier(new NervousyncHostnameVerifier());
			}
		} catch (Exception e) {
			connection = null;
		}
		return connection;
	}
	
	/**
	 * Merges values into one array. Instances of <code>java.lang.String</code>,
	 * <code>java.lang.String[]</code> and <code>java.util.Collection</code> are supported
	 * both in <code>v1</code> and <code>v2</code> arguments.
	 *
	 * @param v1 First object to merge
	 * @param v2 Second object to merge
	 * @return Array contains merged objects
	 */
	private static String[] mergeValues(String[] v1, String v2) {
		String[] values1 = null;
		String[] values2 = null;

		// get first array of values
		if (v1 == null) {
			values1 = new String[0];
		} else {
			values1 = v1;
		}

		// get second array of values
		if (v2 == null) {
			values2 = new String[0];
		} else {
			values2 = new String[]{String.valueOf(v2)};
		}

		// merge arrays
		String[] result = new String[values1.length + values2.length];
		System.arraycopy(values1, 0, result, 0, values1.length);
		System.arraycopy(values2, 0, result, values1.length, values2.length);

		return result;
	}

	/**
	 * Creates page URL from page requestUrl and request parameters specified in parent grid tag
	 *
	 * @param parentGridTag The grid tag
	 * @return URL for this grid tag
	 * @throws FileNotFoundException 
	 */

	private static HttpEntity generateEntity(Map<String, String[]> parameters, 
			Map<String, File> uploadFileMap) throws FileNotFoundException {
		if (parameters == null) {
			parameters = new HashMap<String, String[]>();
		}
		
		HttpEntity httpEntity = HttpEntity.newInstance();
		
		Iterator<String> iterator = parameters.keySet().iterator();
		
		while (iterator.hasNext()) {
			String key = iterator.next();
			String[] values = parameters.get(key);

			for (String value : values) {
				httpEntity.addTextEntity(key, value);
			}
		}
		
		if (uploadFileMap != null && !uploadFileMap.isEmpty()) {
			iterator = uploadFileMap.keySet().iterator();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				File uploadFile = uploadFileMap.get(key);
				
				if (uploadFile != null) {
					httpEntity.addBinaryEntity(key, uploadFile.getAbsolutePath());
				}
			}
		}
		
		return httpEntity;
	}
	
	/**
	 * Appends new key and value pair to query string
	 *
	 * @param key         parameter name
	 * @param value       value of parameter
	 * @param queryString existing query string
	 * @param ampersand   string to use for ampersand (e.g. "&amp;" or "&amp;amp;")
     * @param encode      whether to encode value
	 * @return query string (with no leading "?")
	 */
	private static StringBuffer append(Object key, Object value,
	                                   StringBuffer queryString,
	                                   String ampersand, boolean encode) {
		if (queryString.length() > 0) {
			queryString.append(ampersand);
		}

		try {
			if (encode) {
				key = URLEncoder.encode(key.toString(), Globals.DEFAULT_ENCODING);
				value = URLEncoder.encode(value.toString(), Globals.DEFAULT_ENCODING);
			}
			queryString.append(key);
			queryString.append("=");
			queryString.append(value);
		} catch (UnsupportedEncodingException e) {
			// do nothing
		}
		return queryString;
	}

	private static String appendIgnore(String ipv6Address) {
		if (ipv6Address.indexOf("::") != Globals.DEFAULT_VALUE_INT) {
			int count = StringUtils.countOccurrencesOf(ipv6Address, ":");
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = count ; i < 8 ; i++) {
				stringBuilder.append(":0");
			}
			ipv6Address = StringUtils.replace(ipv6Address, "::", stringBuilder.toString() + ":");
			if (ipv6Address.startsWith(":")) {
				ipv6Address = "0" + ipv6Address;
			}
			if (ipv6Address.endsWith(":")) {
				ipv6Address += "0";
			}
		}
		
		return ipv6Address;
	}
	
	private static final class TrustedCert {
		
		private String certPath = null;
		private String certPassphrase = null;
		
		public TrustedCert(String certPath, String certPassphrase) {
			this.certPath = certPath;
			this.certPassphrase = certPassphrase;
		}
		
		/**
		 * @return the certPath
		 */
		public String getCertPath() {
			return certPath;
		}

		/**
		 * @return the certPassphrase
		 */
		public String getCertPassphrase() {
			return certPassphrase;
		}
	}
	
	private static final class NervousyncX509TrustManager implements X509TrustManager {

		private static final String JAVA_CERT_PATH = Globals.DEFAULT_PAGE_SEPARATOR + "lib" 
					+ Globals.DEFAULT_PAGE_SEPARATOR + "security" + Globals.DEFAULT_PAGE_SEPARATOR + "cacerts";
		
		private static NervousyncX509TrustManager INSTANCE = null;
		
		private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
		private String passPhrase = null;
		private List<TrustedCert> customCerts = null;
		private X509TrustManager trustManager = null;
		
		private NervousyncX509TrustManager(String passPhrase) throws Exception {
			if (passPhrase == null) {
				this.passPhrase = "changeit";
			} else {
				this.passPhrase = passPhrase;
			}
			
			this.customCerts = new ArrayList<TrustedCert>();
			this.initManager();
		}
		
		public static void init(String passPhrase) throws Exception {
			if (INSTANCE == null) {
				INSTANCE = new NervousyncX509TrustManager(passPhrase);
			}
		}
		
		public static NervousyncX509TrustManager getInstance() {
			return INSTANCE;
		}
		
		public void addCustomCert(TrustedCert customCert) throws Exception {
			if (this.certIndex(customCert.getCertPath()) == Globals.DEFAULT_VALUE_INT) {
				this.customCerts.add(customCert);
				this.initManager();
			}
		}

		public void removeCustomCert(String certPath) throws Exception {
			int certIndex = this.certIndex(certPath);
			if (certIndex != Globals.DEFAULT_VALUE_INT) {
				this.customCerts.remove(certIndex);
				this.initManager();
			}
		}
		
		@Override
		public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			this.trustManager.checkClientTrusted(ax509certificate, s);
		}
		
		@Override
		public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			this.trustManager.checkServerTrusted(ax509certificate, s);
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return this.trustManager.getAcceptedIssuers();
		}
		
		private int certIndex(String certPath) {
			int i = 0;
			for (TrustedCert trustedCert : this.customCerts) {
				if (trustedCert.getCertPath().equalsIgnoreCase(certPath)) {
					return i;
				}
				i++;
			}
			return Globals.DEFAULT_VALUE_INT;
		}
		
		private void initManager() throws Exception {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			String sysCertPath = SystemUtils.JAVA_HOME + JAVA_CERT_PATH;
			if (!FileUtils.isExists(sysCertPath)) {
				this.logger.warn("System cert file not found!");
			} else {
				try {
					keyStore.load(FileUtils.loadFile(sysCertPath), this.passPhrase.toCharArray());
				} catch (Exception e) {
					this.logger.warn("Load system cert file using default passphrase error! ");
				}
			}
			
			if (!this.customCerts.isEmpty()) {
				for (TrustedCert trustedCert : this.customCerts) {
					keyStore.load(FileUtils.loadFile(trustedCert.getCertPath()), 
							trustedCert.getCertPassphrase().toCharArray());
				}
			}
			
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			trustManagerFactory.init(keyStore);
			
			for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
				if (trustManager instanceof X509TrustManager) {
					this.trustManager = (X509TrustManager)trustManager;
					return;
				}
			}
			
			throw new Exception("Can't found X509TrustManager");
		}
	}

	private static final class NervousyncHostnameVerifier implements HostnameVerifier {

		private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
		@Override
		public boolean verify(String s, SSLSession sslSession) {
			try {
				String hostName = sslSession.getPeerHost();
				for (X509Certificate certificate : (X509Certificate[])sslSession.getPeerCertificates()) {
					String certDomainNames = certificate.getSubjectX500Principal().getName();
					for (String certDomain : StringUtils.delimitedListToStringArray(certDomainNames, ",")) {
						if (certDomain.startsWith("CN")) {
							if (certDomain.contains(s) && certDomain.contains(hostName)) {
								return true;
							}
						}
					}
				}
			} catch (SSLPeerUnverifiedException e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Verify host name error! ", e);
				}
			}
			return Globals.DEFAULT_VALUE_BOOLEAN;
		}
	}
}
