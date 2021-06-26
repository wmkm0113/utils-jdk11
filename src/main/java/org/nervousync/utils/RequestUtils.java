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
package org.nervousync.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nervousync.annotations.service.RestfulClient;
import org.nervousync.commons.beans.ip.IPRange;
import org.nervousync.commons.beans.servlet.request.RequestAttribute;
import org.nervousync.commons.beans.servlet.request.RequestInfo;
import org.nervousync.commons.beans.servlet.response.HttpResponseContent;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.core.RegexGlobals;
import org.nervousync.commons.http.cookie.CookieEntity;
import org.nervousync.commons.http.entity.HttpEntity;
import org.nervousync.commons.http.proxy.ProxyInfo;
import org.nervousync.commons.http.security.GeneX509TrustManager;
import org.nervousync.enumerations.ip.IPType;
import org.nervousync.enumerations.web.HttpMethodOption;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.handler.HandlerResolver;

import org.nervousync.interceptor.beans.HandlerInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Request Utils
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jan 13, 2010 11:23:13 AM $
 */
public final class RequestUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

	private static final String STOWED_REQUEST_ATTRIBS = "ssl.redirect.attrib.stowed";
	private static final int DEFAULT_TIME_OUT = 5;

	/**
	 * The constant HTTP_METHOD_GET.
	 */
	public static final String HTTP_METHOD_GET = "GET";
	/**
	 * The constant HTTP_METHOD_POST.
	 */
	public static final String HTTP_METHOD_POST = "POST";
	/**
	 * The constant HTTP_METHOD_PUT.
	 */
	public static final String HTTP_METHOD_PUT = "PUT";
	/**
	 * The constant HTTP_METHOD_TRACE.
	 */
	public static final String HTTP_METHOD_TRACE = "TRACE";
	/**
	 * The constant HTTP_METHOD_HEAD.
	 */
	public static final String HTTP_METHOD_HEAD = "HEAD";
	/**
	 * The constant HTTP_METHOD_DELETE.
	 */
	public static final String HTTP_METHOD_DELETE = "DELETE";
	/**
	 * The constant HTTP_METHOD_OPTIONS.
	 */
	public static final String HTTP_METHOD_OPTIONS = "OPTIONS";

	private RequestUtils() {
	}

	/**
	 * Parse HTTP method from string to com.nervousync.enumerations.web.HttpMethodOption
	 *
	 * @param method string of HTTP method
	 * @return com.nervousync.enumerations.web.HttpMethodOption http method option
	 */
	public static HttpMethodOption httpMethodOption(String method) {
		if (method == null) {
			return HttpMethodOption.UNKNOWN;
		}

		switch (method.toUpperCase()) {
			case HTTP_METHOD_GET:
				return HttpMethodOption.GET;
			case HTTP_METHOD_HEAD:
				return HttpMethodOption.HEAD;
			case HTTP_METHOD_PUT:
				return HttpMethodOption.PUT;
			case HTTP_METHOD_POST:
				return HttpMethodOption.POST;
			case HTTP_METHOD_TRACE:
				return HttpMethodOption.TRACE;
			case HTTP_METHOD_DELETE:
				return HttpMethodOption.DELETE;
			case HTTP_METHOD_OPTIONS:
				return HttpMethodOption.OPTIONS;
			default:
				return HttpMethodOption.UNKNOWN;
		}
	}

	/**
	 * Resolve domain name to IP address
	 *
	 * @param domainName domain name
	 * @return IP address
	 */
	public static String resolveDomain(String domainName) {
		try {
			return InetAddress.getByName(domainName).getHostAddress();
		} catch (UnknownHostException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Resolve domain error! ", e);
			}
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	/**
	 * Calculate IP range address
	 *
	 * @param ipAddress IP address in range
	 * @param cidr      CIDR
	 * @return IPRange object
	 */
	public static IPRange calcRange(String ipAddress, int cidr) {
		IPRange ipRange = new IPRange();
		String beginAddress;
		String endAddress;
		if (ipAddress.contains(":")) {
			ipRange.setIpType(IPType.IPv6);
			beginAddress = RequestUtils.beginIPv6(ipAddress, cidr);
			endAddress = RequestUtils.endIPv6(beginAddress, cidr);
		} else {
			ipRange.setIpType(IPType.IPv4);
			beginAddress = RequestUtils.beginIPv4(ipAddress, convertCIDRToNetmask(cidr));
			endAddress = RequestUtils.endIPv4(beginAddress, convertCIDRToNetmask(cidr));
		}

		ipRange.setBeginAddress(beginAddress);
		ipRange.setEndAddress(endAddress);

		return ipRange;
	}

	/**
	 * Convert net mask to CIDR
	 *
	 * @param netmask Net mask address
	 * @return CIDR int
	 */
	public static int convertNetmaskToCIDR(String netmask) {
		int result = 0;

		String[] splitItems = StringUtils.tokenizeToStringArray(netmask, ".");

		for (String splitItem : splitItems) {
			int number = Integer.parseInt(splitItem);
			while (number > 0) {
				if ((number % 2) == 1) {
					result++;
				}
				number /= 2;
			}
		}

		return result;
	}

	/**
	 * Convert CIDR to net mask address
	 *
	 * @param cidr CIDR
	 * @return Net mask address
	 */
	public static String convertCIDRToNetmask(int cidr) {
		if (cidr >= 0 && cidr <= 32) {
			int[] arrays = new int[]{0, 0, 0, 0};
			int index = 0;
			while (index < 4 && cidr >= 0) {
				arrays[index] = RequestUtils.fillBitsFromLeft(cidr);
				cidr -= 8;
				index++;
			}

			StringBuilder stringBuilder = new StringBuilder();

			for (int mask : arrays) {
				stringBuilder.append(".").append(mask);
			}
			return stringBuilder.substring(1);
		}
		return null;
	}

	/**
	 * Convert IPv4 address to compatible IPv6 address
	 *
	 * @param ipAddress IPv4 address
	 * @return Compatible IPv6 address
	 */
	public static String convertIPv4ToCompatibleIPv6(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return "::" + ipAddress;
		}
		return null;
	}

	/**
	 * Convert IPv4 address to IPv6 address and collapse
	 *
	 * @param ipAddress IPv4 address
	 * @return Collapse IPv6 address
	 */
	public static String convertIPv4ToIPv6(String ipAddress) {
		return convertIPv4ToIPv6(ipAddress, true);
	}

	/**
	 * Convert IPv4 address to IPv6 address
	 *
	 * @param ipAddress IPv4 address
	 * @param collapse  Collapse IPv6 address
	 * @return IPv6 address
	 */
	public static String convertIPv4ToIPv6(String ipAddress, boolean collapse) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, ".");
			StringBuilder stringBuilder;
			if (collapse) {
				stringBuilder = new StringBuilder(":");
			} else {
				stringBuilder = new StringBuilder("0000:0000:0000:0000:0000:0000");
			}
			int index = 0;
			for (String addressItem : splitAddress) {
				if (index % 2 == 0) {
					stringBuilder.append(":");
				}
				stringBuilder.append(Integer.toHexString(Integer.parseInt(addressItem)));
				index++;
			}

			return stringBuilder.toString().toUpperCase();
		}
		return null;
	}

	/**
	 * Convert IPv4 address to byte array
	 *
	 * @param ipAddress IPv4 address
	 * @return byte array
	 */
	public static byte[] convertIPv4ToBytes(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, ".");
			byte[] addressBytes = new byte[4];

			addressBytes[0] = (byte) Integer.parseInt(splitAddress[0]);
			addressBytes[1] = (byte) Integer.parseInt(splitAddress[1]);
			addressBytes[2] = (byte) Integer.parseInt(splitAddress[2]);
			addressBytes[3] = (byte) Integer.parseInt(splitAddress[3]);

			return addressBytes;
		}
		return null;
	}

	/**
	 * Convert IP address to BigInteger(supported IPv4 and IPv6)
	 *
	 * @param ipAddress IP address
	 * @return BigInteger big integer
	 */
	public static BigInteger convertIPtoBigInteger(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return RequestUtils.convertIPv4ToBigInteger(ipAddress);
		} else {
			return RequestUtils.convertIPv6ToBigInteger(ipAddress);
		}
	}

	/**
	 * Convert IPv4 address to BigInteger
	 *
	 * @param ipAddress IPv4 address
	 * @return BigInteger big integer
	 */
	public static BigInteger convertIPv4ToBigInteger(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, ".");
			if (splitAddress.length == 4) {
				long result = 0L;
				for (int i = 0; i < splitAddress.length; i++) {
					result += (Long.parseLong(splitAddress[i]) << 8 * (3 - i));
				}
				return BigInteger.valueOf(result);
			}
		}
		return BigInteger.ZERO;
	}

	/**
	 * Convert IPv6 address to BigInteger
	 *
	 * @param ipAddress IPv6 address
	 * @return BigInteger big integer
	 */
	public static BigInteger convertIPv6ToBigInteger(String ipAddress) {
		String fullAddress = expandIgnore(ipAddress);
		if (StringUtils.matches(fullAddress, RegexGlobals.IPV6_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(fullAddress, ":");
			BigInteger bigInteger = BigInteger.ZERO;
			int index = 0;
			for (String split : splitAddress) {
				BigInteger currentInteger;
				if (StringUtils.matches(split, RegexGlobals.IPV4_REGEX)) {
					currentInteger = convertIPv4ToBigInteger(split);
				} else {
					currentInteger = BigInteger.valueOf(Long.valueOf(split, 16));
				}
				if (currentInteger == null) {
					return BigInteger.ZERO;
				}
				bigInteger = bigInteger.add(currentInteger.shiftLeft(16 * (splitAddress.length - index - 1)));
				index++;
			}
			return bigInteger;
		}
		return BigInteger.ZERO;
	}

	/**
	 * Convert BigInteger value to IPv4 address(x.x.x.x)
	 *
	 * @param bigInteger BigInteger value
	 * @return IPv4 address
	 */
	public static String convertBigIntegerToIPv4(BigInteger bigInteger) {
		StringBuilder ipv4Address = new StringBuilder();
		BigInteger ff = BigInteger.valueOf(0xFFL);

		for (int i = 0; i < 4; i++) {
			ipv4Address.insert(0, "." + bigInteger.and(ff));
			bigInteger = bigInteger.shiftRight(8);
		}

		return ipv4Address.substring(1);
	}

	/**
	 * Convert BigInteger value to IPv6 address(xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx or :xxxx:xxxx::xxxx)
	 *
	 * @param bigInteger BigInteger value
	 * @return IPv6 address
	 */
	public static String convertBigIntegerToIPv6Address(BigInteger bigInteger) {
		StringBuilder ipv6Address = new StringBuilder();
		BigInteger ff = BigInteger.valueOf(0xFFFFL);

		for (int i = 0; i < 8; i++) {
			ipv6Address.insert(0, ":" + bigInteger.and(ff).toString(16));
			bigInteger = bigInteger.shiftRight(16);
		}

		return ipv6Address.substring(1).replaceFirst(RegexGlobals.IPV6_COMPRESS_REGEX, "::");
	}

	/**
	 * Generate Restful service client instance
	 *
	 * @param <T>                Client interface
	 * @param serviceClient      Client interface class
	 * @param handlerInterceptor Handler interceptor
	 * @return Generated instance
	 */
	public static <T> T RestfulClient(Class<T> serviceClient, HandlerInterceptor handlerInterceptor) {
		if (!serviceClient.isAnnotationPresent(RestfulClient.class)) {
			return null;
		}
		return ObjectUtils.createProxyInstance(serviceClient, handlerInterceptor);
	}

	/**
	 * Generate SOAP client instance
	 *
	 * @param <T>              End point interface
	 * @param serviceInterface End point interface
	 * @param handlerResolver  Handler resolver
	 * @return Generated instance
	 * @throws MalformedURLException if no protocol is specified, or an unknown protocol is found, or spec is null.
	 */
	public static <T> T SOAPClient(Class<T> serviceInterface, HandlerResolver handlerResolver)
			throws MalformedURLException {
		if (!serviceInterface.isAnnotationPresent(WebServiceClient.class)) {
			return null;
		}

		WebServiceClient serviceClient = serviceInterface.getAnnotation(WebServiceClient.class);

		String namespaceURI = serviceClient.targetNamespace();
		String serviceName = serviceClient.name();
		URL wsdlLocation = new URL(serviceClient.wsdlLocation());

		if (namespaceURI.length() == 0) {
			String packageName = serviceInterface.getPackage().getName();
			String[] packageNames = StringUtils.tokenizeToStringArray(packageName, ".");
			StringBuilder stringBuilder = new StringBuilder(wsdlLocation.getProtocol() + "://");
			for (int i = packageNames.length - 1; i >= 0; i--) {
				stringBuilder.append(packageNames[i]).append(".");
			}

			namespaceURI = stringBuilder.substring(0, stringBuilder.length() - 1) + "/";
		}

		if (StringUtils.isEmpty(serviceName)) {
			serviceName = serviceInterface.getSimpleName() + "Service";
		}

		Service service = Service.create(wsdlLocation, new QName(namespaceURI, serviceName));
		if (handlerResolver != null) {
			service.setHandlerResolver(handlerResolver);
		}

		return service.getPort(new QName(namespaceURI, serviceName), serviceInterface);
	}

	/**
	 * Retrieve response content length
	 *
	 * @param requestUrl URL address
	 * @return Response content length
	 */
	public int retrieveContentLength(String requestUrl) {
		return sendRequest(RequestInfo.builder(HttpMethodOption.HEAD).requestUrl(requestUrl).build())
				.filter(httpResponseContent -> httpResponseContent.getStatusCode() == HttpURLConnection.HTTP_OK)
				.map(HttpResponseContent::getContentLength)
				.orElse(Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * Send request parse object optional.
	 *
	 * @param <T>         the type parameter
	 * @param requestInfo the request info
	 * @param targetClass the target class
	 * @return the optional
	 */
	public static <T> Optional<T> sendRequestParseSimple(RequestInfo requestInfo, Class<T> targetClass) {
		return sendRequest(requestInfo, new ObjectBodyHandler<>(targetClass, StringUtils.StringType.SIMPLE));
	}

	/**
	 * Send request parse object optional.
	 *
	 * @param <T>         the type parameter
	 * @param requestInfo the request info
	 * @param targetClass the target class
	 * @return the optional
	 */
	public static <T> Optional<T> sendRequestParseXML(RequestInfo requestInfo, Class<T> targetClass) {
		return sendRequest(requestInfo, new ObjectBodyHandler<>(targetClass, StringUtils.StringType.XML));
	}

	/**
	 * Send request parse object optional.
	 *
	 * @param <T>         the type parameter
	 * @param requestInfo the request info
	 * @param targetClass the target class
	 * @return the optional
	 */
	public static <T> Optional<T> sendRequestParseJSON(RequestInfo requestInfo, Class<T> targetClass) {
		return sendRequest(requestInfo, new ObjectBodyHandler<>(targetClass, StringUtils.StringType.JSON));
	}

	/**
	 * Send request parse object optional.
	 *
	 * @param <T>         the type parameter
	 * @param requestInfo the request info
	 * @param targetClass the target class
	 * @return the optional
	 */
	public static <T> Optional<T> sendRequestParseYAML(RequestInfo requestInfo, Class<T> targetClass) {
		return sendRequest(requestInfo, new ObjectBodyHandler<>(targetClass, StringUtils.StringType.YAML));
	}

	/**
	 * Send request parse list optional.
	 *
	 * @param <T>         the type parameter
	 * @param requestInfo the request info
	 * @param targetClass the target class
	 * @return the optional
	 */
	public static <T> Optional<List<T>> sendRequestParseJSONList(RequestInfo requestInfo, Class<T> targetClass) {
		return sendRequest(requestInfo, new ArrayBodyHandler<>(targetClass, StringUtils.StringType.JSON));
	}

	/**
	 * Send request parse list optional.
	 *
	 * @param <T>         the type parameter
	 * @param requestInfo the request info
	 * @param targetClass the target class
	 * @return the optional
	 */
	public static <T> Optional<List<T>> sendRequestParseYamlList(RequestInfo requestInfo, Class<T> targetClass) {
		return sendRequest(requestInfo, new ArrayBodyHandler<>(targetClass, StringUtils.StringType.YAML));
	}

	/**
	 * Send request optional.
	 *
	 * @param requestInfo the request info
	 * @return the optional
	 */
	public static Optional<HttpResponseContent> sendRequest(RequestInfo requestInfo) {
		return sendRequest(requestInfo, new ResponseContentHandler());
	}

	/**
	 * Send request and receive response
	 *
	 * @param <T>                 the type parameter
	 * @param requestInfo         Request info
	 * @param supplierBodyHandler the supplier body handler
	 * @return HttpResponseContent http response content
	 */
	public static <T> Optional<T> sendRequest(RequestInfo requestInfo,
											  HttpResponse.BodyHandler<Supplier<T>> supplierBodyHandler) {
		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
		HttpEntity httpEntity = null;
		switch (requestInfo.getHttpMethodOption()) {
			case GET:
				requestBuilder.GET();
				httpEntity = generateEntity(requestInfo.getParameters(), null);
				break;
			case POST:
				if (requestInfo.getPostDatas() != null) {
					requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(requestInfo.getPostDatas()));
				} else {
					try {
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						httpEntity = generateEntity(requestInfo.getParameters(), requestInfo.getUploadParam());
						httpEntity.writeData(requestInfo.getCharset(), byteArrayOutputStream);
						requestBuilder.POST(HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray()));
					} catch (IOException ignored) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Process data error! ");
						}
						return Optional.empty();
					}
				}
				break;
			case PUT:
				if (requestInfo.getPostDatas() != null) {
					requestBuilder.PUT(HttpRequest.BodyPublishers.ofByteArray(requestInfo.getPostDatas()));
				} else {
					try {
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						httpEntity = generateEntity(requestInfo.getParameters(), requestInfo.getUploadParam());
						httpEntity.writeData(requestInfo.getCharset(), byteArrayOutputStream);
						requestBuilder.PUT(HttpRequest.BodyPublishers.ofByteArray(byteArrayOutputStream.toByteArray()));
					} catch (IOException ignored) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("Process data error! ");
						}
						return Optional.empty();
					}
				}
				break;
			case DELETE:
				requestBuilder.DELETE();
				httpEntity = generateEntity(requestInfo.getParameters(), null);
				break;
			default:
				requestBuilder.method(requestInfo.getHttpMethodOption().toString(), null);
				httpEntity = generateEntity(requestInfo.getParameters(), null);
				break;
		}

		String uri = requestInfo.getRequestUrl();
		if (httpEntity != null) {
			try {
				requestBuilder.header("Content-Type",
						httpEntity.generateContentType(requestInfo.getCharset(), requestInfo.getHttpMethodOption()));
			} catch (UnsupportedEncodingException e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Process content type error! ", e);
				}
			}
			if (!HttpMethodOption.POST.equals(requestInfo.getHttpMethodOption())
					&& !HttpMethodOption.PUT.equals(requestInfo.getHttpMethodOption())) {
				uri = RequestUtils.appendParams(uri, requestInfo.getParameters());
			}
		} else {
			requestBuilder.header("Content-Type",
					requestInfo.getContentType() + ";charset=" + requestInfo.getCharset());
		}

		requestInfo.getHeaders()
				.forEach(simpleHeader ->
						requestBuilder.setHeader(simpleHeader.getHeaderName(), simpleHeader.getHeaderValue()));
		requestBuilder.uri(URI.create(uri));

		requestBuilder.setHeader("Accept", "text/html,text/javascript,text/xml");
		requestBuilder.setHeader("Accept-Encoding", "gzip, deflate");
		if (StringUtils.isEmpty(requestInfo.getUserAgent())) {
			requestBuilder.setHeader("User-Agent", "NervousyncBot");
		} else {
			requestBuilder.setHeader("User-Agent", requestInfo.getUserAgent());
		}
		String cookie = RequestUtils.generateCookie(requestInfo.getRequestUrl(), requestInfo.getCookieList());
		if (StringUtils.notBlank(cookie)) {
			requestBuilder.setHeader("Cookie", cookie);
		}

		HttpClient.Builder clientBuilder = HttpClient.newBuilder()
				.version(HttpClient.Version.HTTP_2)
				.followRedirects(HttpClient.Redirect.NORMAL);
		if (requestInfo.getTimeOut() > 0) {
			clientBuilder.connectTimeout(Duration.ofSeconds(requestInfo.getTimeOut()));
		}

		if (requestInfo.getProxyInfo() != null) {
			ProxyInfo proxyInfo = requestInfo.getProxyInfo();
			clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyInfo.getProxyAddress(), proxyInfo.getProxyPort())));
			if (StringUtils.notBlank(proxyInfo.getUserName())) {
				String authentication = proxyInfo.getUserName() + ":";
				if (StringUtils.notBlank(proxyInfo.getPassword())) {
					authentication += proxyInfo.getPassword();
				}

				requestBuilder.setHeader("Proxy-Authorization",
						StringUtils.base64Encode(authentication.getBytes(Charset.forName(Globals.DEFAULT_ENCODING))));
			}
		}

		if (requestInfo.getTrustCertInfos() != null && requestInfo.getTrustCertInfos().size() > 0) {
			try {
				System.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());
				SSLContext sslContext = SSLContext.getInstance("TLS");
				GeneX509TrustManager x509TrustManager =
						GeneX509TrustManager.init(requestInfo.getPassPhrase(), requestInfo.getTrustCertInfos());
				sslContext.init(new KeyManager[0], new TrustManager[]{x509TrustManager}, new SecureRandom());
				clientBuilder.sslContext(sslContext);
			} catch (Exception e) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Process ssl certificate error! ");
				}
			}
		}

		try {
			return Optional.ofNullable(clientBuilder.build()
					.send(requestBuilder.build(), supplierBodyHandler)
					.body()
					.get());
		} catch (IOException | InterruptedException e) {
			if (RequestUtils.LOGGER.isDebugEnabled()) {
				RequestUtils.LOGGER.debug("Send Request ERROR: ", e);
			}
			return Optional.empty();
		} finally {
			System.clearProperty("jdk.internal.httpclient.disableHostnameVerification");
		}
	}

	/**
	 * Creates query String from request body parameters
	 *
	 * @param request The request that will supply parameters
	 * @return Query string corresponding to that request parameters
	 */
	public static String getRequestParameters(HttpServletRequest request) {
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
	 * @param queryString  Query string to get parameters from
	 * @param decodeValues Whether to decode values (which are URL-encoded)
	 * @return Map with request parameters mapped to their values
	 */
	public static Map<String, String[]> getRequestParametersFromString(String queryString,
	                                                                   boolean decodeValues) {
		HashMap<String, String[]> parameterMap = new HashMap<>();

		if (queryString == null) {
			return parameterMap;
		}

		for (int k = 0; k < queryString.length(); ) {
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
			return new HashMap<>();
		}

		int qSignPos = uri.indexOf('?');
		if (qSignPos == -1) {
			return new HashMap<>();
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
	public static StringBuilder createQueryStringFromMap(Map<String, String[]> m, String ampersand, boolean encode) {
		StringBuilder result = new StringBuilder();
		Set<Entry<String, String[]>> entrySet = m.entrySet();

		for (Entry<String, String[]> entry : entrySet) {
			String[] values = entry.getValue();

			if (values == null) {
				append(entry.getKey(), "", result, ampersand, encode);
			} else {
				for (String value : values) {
					append(entry.getKey(), value, result, ampersand, encode);
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
	public static StringBuilder createQueryStringFromMap(Map<String, String[]> m, String ampersand) {
		return createQueryStringFromMap(m, ampersand, true);
	}

	/**
	 * Append parameters to base URI.
	 *
	 * @param uri    An address that is base for adding params
	 * @param params A map of parameters
	 * @return resulting URI
	 */
	public static String appendParams(String uri, Map<String, String[]> params) {
		String delimiter = (uri.indexOf('?') == -1) ? "?" : "&";
		return uri + delimiter + RequestUtils.createQueryStringFromMap(params, "&");
	}

	/**
	 * Stores request attributes in session
	 *
	 * @param request the current request
	 */
	public static void stowRequestAttributes(HttpServletRequest request) {
		if (request.getSession().getAttribute(STOWED_REQUEST_ATTRIBS) != null) {
			return;
		}

		request.getSession().setAttribute(STOWED_REQUEST_ATTRIBS, RequestAttribute.newInstance(request));
	}

	/**
	 * Returns request attributes from session to request
	 *
	 * @param request a request to which saved in session parameters will be                assigned
	 */
	public static void reclaimRequestAttributes(HttpServletRequest request) {
		RequestAttribute requestAttribute =
				(RequestAttribute) request.getSession().getAttribute(STOWED_REQUEST_ATTRIBS);

		if (requestAttribute == null) {
			return;
		}

		Map<String, Object> attributeMap = requestAttribute.getAttributeMap();
		attributeMap.forEach(request::setAttribute);

		request.getSession().removeAttribute(STOWED_REQUEST_ATTRIBS);
	}

	/**
	 * Convenience method to get the application's URL based on request
	 * variables.
	 *
	 * @param request the request from which the URL is calculated
	 * @return Application URL
	 */
	public static String getAppURL(HttpServletRequest request) {
		StringBuilder requestUrl = new StringBuilder();
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

	/**
	 * Is user in role boolean.
	 *
	 * @param rolesIterator the roles iterator
	 * @param request       the request
	 * @return the boolean
	 */
	public static boolean isUserInRole(Iterator<Object> rolesIterator, HttpServletRequest request) {
		return isUserInRole(rolesIterator, null, request);
	}

	/**
	 * Is user in role boolean.
	 *
	 * @param rolesIterator the roles iterator
	 * @param property      the property
	 * @param request       the request
	 * @return the boolean
	 */
	public static boolean isUserInRole(Iterator<Object> rolesIterator, String property, HttpServletRequest request) {
		if (rolesIterator != null) {
			while (rolesIterator.hasNext()) {
				Object bean = rolesIterator.next();
				if (bean == null) {
					continue;
				}
				String roleName;
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
	 * @param request HttpServletRequest Object
	 * @return client IP address
	 */
	public static String getClientIP(HttpServletRequest request) {
		request.getHeaderNames();
		//	如果使用了反向代理服务器，则需要从Header中获取转发的客户端IP地址
		String clientIP = request.getHeader("X-Forwarded-For");

		if (StringUtils.isEmpty(clientIP) || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("Proxy-Client-IP");
		}

		if (StringUtils.isEmpty(clientIP) || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("WL-Proxy-Client-IP");
		}

		if (StringUtils.isEmpty(clientIP) || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("HTTP_CLIENT_IP");
		}

		if (StringUtils.isEmpty(clientIP) || "unknown".equalsIgnoreCase(clientIP)) {
			clientIP = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if (StringUtils.isEmpty(clientIP) || "unknown".equalsIgnoreCase(clientIP)) {
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

	/**
	 * Gets request uri.
	 *
	 * @param request the request
	 * @return the request uri
	 */
	public static String getRequestURI(HttpServletRequest request) {
		String requestUrl = RequestUtils.getRequestPath(request);

		if (requestUrl.lastIndexOf('.') != -1) {
			return requestUrl.substring(0, requestUrl.lastIndexOf("."));
		}
		return requestUrl;
	}

	/**
	 * Gets request path.
	 *
	 * @param request the request
	 * @return the request path
	 */
	public static String getRequestPath(HttpServletRequest request) {
		if (request == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		String requestUrl = request.getRequestURI();

		if (StringUtils.notBlank(request.getContextPath())) {
			requestUrl = requestUrl.substring(request.getContextPath().length());
		}
		return requestUrl;
	}

	/**
	 * Gets request url.
	 *
	 * @param request the request
	 * @return the request url
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		return getRequestUrl(request, true);
	}

	/**
	 * Gets request url.
	 *
	 * @param request       the request
	 * @param includeDomain the include domain
	 * @return the request url
	 */
	public static String getRequestUrl(HttpServletRequest request, boolean includeDomain) {
		if (request == null) {
			return Globals.DEFAULT_VALUE_STRING;
		}

		StringBuilder requestUrl = new StringBuilder();

		if (includeDomain) {
			requestUrl.append(RequestUtils.getAppURL(request));
		}

		requestUrl.append(getRequestURI(request));

		if (request.getQueryString() != null && request.getQueryString().length() > 0) {
			requestUrl.append("?").append(request.getQueryString());
		}

		return requestUrl.toString();
	}

	/**
	 * Format path string.
	 *
	 * @param filePath the file path
	 * @return the string
	 */
	public static String formatPath(String filePath) {
		return StringUtils.replace(filePath, Globals.DEFAULT_PAGE_SEPARATOR, "/");
	}

	/**
	 * Process rewrite string.
	 *
	 * @param request the request
	 * @param regex   the regex
	 * @param toPath  the to path
	 * @return the string
	 */
	public static String processRewrite(HttpServletRequest request, String regex, String toPath) {
		String requestPath = RequestUtils.getRequestUrl(request, false);

		if (StringUtils.matches(requestPath, regex)) {
			String redirectPath = toPath;
			Matcher matcher = Pattern.compile(regex).matcher(requestPath);
			if (matcher.find()) {
				for (int i = 0; i < matcher.groupCount(); i++) {
					int index = i + 1;
					redirectPath = StringUtils.replace(redirectPath, "$" + index, matcher.group(index));
				}

				return redirectPath;
			}
		}

		return null;
	}

	/**
	 * Expand IPv6 address ignore data
	 *
	 * @param ipv6Address IPv6 address
	 * @return Expand IPv6 address
	 */
	public static String expandIgnore(String ipv6Address) {
		if (ipv6Address.contains("::")) {
			int count = StringUtils.countOccurrencesOf(ipv6Address, ":");
			ipv6Address = StringUtils.replace(ipv6Address, "::",
					":0000".repeat(Math.max(0, 8 - count)) + ":");
			if (ipv6Address.startsWith(":")) {
				ipv6Address = "0000" + ipv6Address;
			}
			if (ipv6Address.endsWith(":")) {
				ipv6Address += "0000";
			}
		}

		String[] addressItems = StringUtils.delimitedListToStringArray(ipv6Address, ":");
		StringBuilder stringBuilder = new StringBuilder();
		for (String addressItem : addressItems) {
			StringBuilder addressItemBuilder = new StringBuilder(addressItem);
			while (addressItemBuilder.length() < 4) {
				addressItemBuilder.insert(0, "0");
			}
			addressItem = addressItemBuilder.toString();
			stringBuilder.append(":").append(addressItem);
		}
		return stringBuilder.substring(1);
	}

	/**
	 * Calculate IP range begin address
	 *
	 * @param ipAddress IP address in range
	 * @param netmask   Net mask address
	 * @return Begin IP address
	 */
	private static String beginIPv4(String ipAddress, String netmask) {
		String[] addressItems = StringUtils.tokenizeToStringArray(ipAddress, ".");
		String[] maskItems = StringUtils.tokenizeToStringArray(netmask, ".");

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			int itemValue = i < addressItems.length ? Integer.parseInt(addressItems[i])
					: Globals.INITIALIZE_INT_VALUE;
			int beginItem = itemValue & Integer.parseInt(maskItems[i]);
			if (itemValue == 0 && i == 3) {
				beginItem++;
			}
			stringBuilder.append(".").append(beginItem);
		}

		return stringBuilder.substring(1);
	}

	/**
	 * Calculate IP range end address
	 *
	 * @param beginIP Begin address of range
	 * @param netmask Net mask address
	 * @return End IP address
	 */
	private static String endIPv4(String beginIP, String netmask) {
		String[] addressItems = StringUtils.tokenizeToStringArray(beginIP, ".");
		String[] maskItems = StringUtils.tokenizeToStringArray(netmask, ".");

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < 4; i++) {
			int endItem = 255 - Integer.parseInt(addressItems[i]) ^ Integer.parseInt(maskItems[i]);
			stringBuilder.append(".").append(endItem);
		}

		return stringBuilder.substring(1);
	}

	/**
	 * Calculate IPv6 range begin address
	 *
	 * @param ipAddress IPv6 address
	 * @param cidr      CIDR
	 * @return Begin address
	 */
	private static String beginIPv6(String ipAddress, int cidr) {
		if (cidr >= 0 && cidr <= 128) {
			String hexAddress = StringUtils.replace(RequestUtils.expandIgnore(ipAddress), ":", "");
			StringBuilder baseIP = new StringBuilder(hexToBin(hexAddress).substring(0, cidr));

			while (baseIP.length() < 128) {
				baseIP.append("0");
			}

			return binToHex(baseIP.toString());
		}
		return null;
	}

	/**
	 * Calculate IPv6 range end address
	 *
	 * @param ipAddress IPv6 address
	 * @param cidr      CIDR
	 * @return End address
	 */
	private static String endIPv6(String ipAddress, int cidr) {
		if (cidr >= 0 && cidr <= 128) {
			String hexAddress = StringUtils.replace(RequestUtils.expandIgnore(ipAddress), ":", "");
			StringBuilder baseIP = new StringBuilder(hexToBin(hexAddress).substring(0, cidr));

			while (baseIP.length() < 128) {
				baseIP.append("1");
			}

			return binToHex(baseIP.toString());
		}
		return null;
	}

	/**
	 * Convert IPv6 address from hex data to binary data
	 *
	 * @param hexAddress hex data address
	 * @return binary data address
	 */
	private static String hexToBin(String hexAddress) {
		StringBuilder binBuilder = new StringBuilder();
		int index = 0;
		while (index < hexAddress.length()) {
			int hexInt = Integer.parseInt(hexAddress.substring(index, index + 1), 16);
			StringBuilder binItem = new StringBuilder(Integer.toString(hexInt, 2));
			while (binItem.length() < 4) {
				binItem.insert(0, "0");
			}
			binBuilder.append(binItem);
			index++;
		}
		return binBuilder.toString();
	}

	/**
	 * Convert IPv6 address from binary data to hex data
	 *
	 * @param binAddress binary data address
	 * @return hex data address
	 */
	private static String binToHex(String binAddress) {
		StringBuilder binBuilder = new StringBuilder();
		int index = 0;
		while (index < binAddress.length()) {
			if (index % 16 == 0) {
				binBuilder.append(":");
			}
			int binInt = Integer.parseInt(binAddress.substring(index, index + 4), 2);
			binBuilder.append(Integer.toString(binInt, 16).toUpperCase());
			index += 4;
		}
		return binBuilder.substring(1);
	}

	private static int fillBitsFromLeft(int value) {
		if (value >= 8) {
			return 255;
		} else {
			return 256 - Double.valueOf(Math.pow(2, (8 - value))).intValue();
		}
	}

	private static String generateCookie(String requestUrl, List<CookieEntity> cookieList) {
		if (cookieList == null || cookieList.size() == 0) {
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();

		for (CookieEntity cookieInfo : cookieList) {
			if ((!requestUrl.startsWith(Globals.SECURE_HTTP_PROTOCOL)
					&& cookieInfo.isSecure()) || (cookieInfo.getExpires() > DateTimeUtils.currentUTCTimeMillis())
					|| cookieInfo.getMaxAge() == Globals.DEFAULT_VALUE_LONG || cookieInfo.getMaxAge() == 0L) {
				continue;
			}

			String domain;
			String requestPath;

			if (requestUrl.startsWith(Globals.SECURE_HTTP_PROTOCOL)) {
				domain = requestUrl.substring(Globals.SECURE_HTTP_PROTOCOL.length());
			} else if (requestUrl.startsWith(Globals.HTTP_PROTOCOL)) {
				domain = requestUrl.substring(Globals.HTTP_PROTOCOL.length());
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

			stringBuilder.append("; ").append(cookieInfo.getName()).append("=").append(cookieInfo.getValue());
		}

		if (stringBuilder.length() == 0) {
			return null;
		}
		return stringBuilder.substring(2);
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
		String[] values1;
		String[] values2;

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
			values2 = new String[]{v2};
		}

		// merge arrays
		return StringUtils.concatenateStringArrays(values1, values2);
	}

	/**
	 * Creates page URL from page requestUrl and request parameters specified in parent grid tag
	 *
	 * @param parameters    The parameter map
	 * @param uploadFileMap The upload file map
	 * @return URL for this grid tag
	 */

	private static HttpEntity generateEntity(Map<String, String[]> parameters,
	                                         Map<String, File> uploadFileMap) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}

		HttpEntity httpEntity = HttpEntity.newInstance();
		parameters.forEach((key, values) -> {
			for (String value : values) {
				httpEntity.addTextEntity(key, value);
			}
		});

		if (uploadFileMap != null && !uploadFileMap.isEmpty()) {
			uploadFileMap.forEach((key, uploadFile) -> {
				if (uploadFile != null) {
					httpEntity.addBinaryEntity(key, uploadFile.getAbsolutePath());
				}
			});
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
	 */
	private static void append(Object key, Object value,
	                           StringBuilder queryString,
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
	}

	private static final class ArrayBodyHandler<T> implements HttpResponse.BodyHandler<Supplier<List<T>>> {

		private final Class<T> targetClass;
		private final StringUtils.StringType dataType;

		/**
		 * Instantiates a new Array body handler.
		 *
		 * @param targetClass the target class
		 * @param dataType    the data type
		 */
		public ArrayBodyHandler(Class<T> targetClass, StringUtils.StringType dataType) {
			this.targetClass = targetClass;
			this.dataType = dataType;
		}

		public HttpResponse.BodySubscriber<Supplier<List<T>>> apply(HttpResponse.ResponseInfo responseInfo) {
			HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
			return HttpResponse.BodySubscribers.mapping(upstream, inputStream -> () -> {
				switch (this.dataType) {
					case JSON:
					case YAML:
						try {
							ObjectMapper objectMapper = new ObjectMapper();
							JavaType javaType =
									objectMapper.getTypeFactory().constructParametricType(ArrayList.class, targetClass);
							return objectMapper.readValue(inputStream, javaType);
						} catch (IOException e) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Convert json string to object bean error! ", e);
							}
							throw new UncheckedIOException(e);
						}
				}
				return null;
			});
		}
	}

	private static final class ResponseContentHandler implements HttpResponse.BodyHandler<Supplier<HttpResponseContent>> {

		/**
		 * Instantiates a new Response content handler.
		 */
		public ResponseContentHandler() {
		}

		public HttpResponse.BodySubscriber<Supplier<HttpResponseContent>> apply(HttpResponse.ResponseInfo responseInfo) {
			HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
			return HttpResponse.BodySubscribers.mapping(upstream,
					inputStream -> () -> new HttpResponseContent(responseInfo, inputStream));
		}
	}

	private static final class ObjectBodyHandler<T> implements HttpResponse.BodyHandler<Supplier<T>> {

		private final Class<T> targetClass;
		private final StringUtils.StringType dataType;

		/**
		 * Instantiates a new Object body handler.
		 *
		 * @param targetClass the target class
		 * @param dataType    the data type
		 */
		public ObjectBodyHandler(Class<T> targetClass, StringUtils.StringType dataType) {
			this.targetClass = targetClass;
			this.dataType = dataType;
		}

		public HttpResponse.BodySubscriber<Supplier<T>> apply(HttpResponse.ResponseInfo responseInfo) {
			HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
			return HttpResponse.BodySubscribers.mapping(upstream, inputStream -> () -> {
				switch (this.dataType) {
					case JSON:
					case YAML:
						try {
							ObjectMapper objectMapper = new ObjectMapper();
							return objectMapper.readValue(inputStream, targetClass);
						} catch (IOException e) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Convert json string to object bean error! ", e);
							}
							throw new UncheckedIOException(e);
						}
					case XML:
						return BeanUtils.parseXml(IOUtils.readContent(inputStream), this.targetClass);
					case SIMPLE:
						try {
							return StringUtils.parseSimpleData(IOUtils.readContent(inputStream), this.targetClass);
						} catch (ParseException e) {
							if (LOGGER.isDebugEnabled()) {
								LOGGER.debug("Convert string to simple object error! ", e);
							}
							return null;
						}
				}
				return null;
			});
		}
	}
}
