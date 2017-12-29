/*
 * Copyright © 2003 - 2009 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.beans.servlet.request.RequestAttribute;
import com.nervousync.commons.beans.servlet.request.RequestInfo;
import com.nervousync.commons.beans.servlet.response.HttpResponseContent;
import com.nervousync.commons.core.Globals;
import com.nervousync.enumeration.web.HttpMethodOption;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.handler.HandlerResolver;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
	private static final String IPV4_REGEX = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
	private static final String IPV6_REGEX = "^([\\da-fA-F]{1,4}(:[\\da-fA-F]{1,4}){7}|([\\da-fA-F]{1,4}){0,1}:(:[\\da-fA-F]{1,4}){1,7}|[\\da-fA-F]{1,4}::|::)$";
	private static final String IPV6_COMPRESS_REGEX = "(^|:)(0+(:|$)){2,8}";
	
	private RequestUtils() {
		
	}
	
	public static long convertIPv4ToNum(String ipAddress) {
		if (StringUtils.matches(ipAddress, IPV4_REGEX)) {
			String[] splitAddr = StringUtils.tokenizeToStringArray(ipAddress, ".");
			if (splitAddr.length == 4) {
				long result = 0L;
				result += Long.parseLong(splitAddr[3]);
				result += Long.parseLong(splitAddr[2]) << 8;
				result += Long.parseLong(splitAddr[1]) << 16;
				result += Long.parseLong(splitAddr[0]) << 24;
				return result;
			}
		}
		return Globals.DEFAULT_VALUE_LONG;
	}
	
	public static BigInteger convertIPv6ToNum(String ipAddress) {
		if (StringUtils.matches(ipAddress, IPV6_REGEX)) {
			ipAddress = appendIgnore(ipAddress);
			String[] splitAddr = StringUtils.tokenizeToStringArray(ipAddress, ":");
			if (splitAddr.length == 8) {
				BigInteger bigInteger = BigInteger.ZERO;
				int index = 0;
				for (String split : splitAddr) {
					if (StringUtils.matches(split, IPV4_REGEX)) {
						bigInteger = bigInteger.add(BigInteger.valueOf(convertIPv4ToNum(split))
								.shiftLeft(16 * (splitAddr.length - index - 1)));
					} else {
						bigInteger = bigInteger.add(BigInteger.valueOf(Long.valueOf(split, 16))
								.shiftLeft(16 * (splitAddr.length - index - 1)));
					}
					index++;
				}
				return bigInteger;
			}
		}
		return null;
	}
	
	public static String convertNumToIPv4(long num) {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(String.valueOf(num >>> 24));
		stringBuilder.append(".");
		stringBuilder.append(String.valueOf((num & 0x00FFFFFF) >>> 16));
		stringBuilder.append(".");
		stringBuilder.append(String.valueOf((num & 0x0000FFFF) >>> 8));
		stringBuilder.append(".");
		stringBuilder.append(String.valueOf(num & 0x000000FF));
		
		return stringBuilder.toString();
	}
	
	public static String convertNumToIPv6Addr(BigInteger bigInteger) {
		String ipv6Addr = "";
		BigInteger ff = BigInteger.valueOf(0xFFFFL);
		
		for (int i = 0 ; i < 8 ; i++) {
			ipv6Addr = bigInteger.and(ff).toString(16) + ":" + ipv6Addr;
			bigInteger = bigInteger.shiftRight(16);
		}
		
		return ipv6Addr.substring(0, ipv6Addr.length() - 1).replaceFirst(IPV6_COMPRESS_REGEX, "::");
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
		if (httpResponseContent.getStatusCode() == HttpStatus.SC_OK) {
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

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers, int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers, int timeOut) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers, 
			int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers, 
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

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers, 
			HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers, 
			HttpMethodOption httpMethodOption, int timeOut) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, List<Header> headers, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition, int timeOut) 
			throws UnsupportedEncodingException {
		
		return sendRequest(new RequestInfo(HttpMethodOption.GET, requestUrl, timeOut, 
				beginPosition, endPosition, headers, null, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers, 
			HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers, 
			HttpMethodOption httpMethodOption, int timeOut) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers, 
			HttpMethodOption httpMethodOption, int beginPosition, int endPosition) 
			throws UnsupportedEncodingException {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				beginPosition, endPosition, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, String data, List<Header> headers, 
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
			List<Header> headers, HttpMethodOption httpMethodOption) 
			throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			List<Header> headers, HttpMethodOption httpMethodOption, int timeOut) 
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
			Map<String, File> uploadParam, List<Header> headers, HttpMethodOption httpMethodOption) 
					throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, uploadParam));
	}

	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, 
			Map<String, File> uploadParam, List<Header> headers, 
			HttpMethodOption httpMethodOption, int timeOut) 
					throws UnsupportedEncodingException {
		return sendRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, 
				Globals.DEFAULT_VALUE_INT, Globals.DEFAULT_VALUE_INT, headers, parameters, uploadParam));
	}
	
	public static HttpResponseContent sendRequest(RequestInfo requestInfo) 
					throws UnsupportedEncodingException {
		HttpRequestBase method = null;
		HttpMethodOption httpMethodOption = requestInfo.getHttpMethodOption();
		if (httpMethodOption == null) {
			httpMethodOption = HttpMethodOption.DEFAULT;
		}
		
		String requestUrl = requestInfo.getRequestUrl();
		
		if (requestUrl.indexOf("://") == Globals.DEFAULT_VALUE_INT) {
			requestUrl = Globals.DEFAULT_PROTOCOL_PREFIX_HTTP + requestUrl;
		}
		
		String uri = null;
		String data = null;
		
		if (HttpMethodOption.POST.equals(httpMethodOption) 
				|| HttpMethodOption.PUT.equals(httpMethodOption)) {
			if (requestUrl.indexOf('?') != -1) {
				uri = requestUrl.substring(0, requestUrl.indexOf('?'));
				data = requestUrl.substring(requestUrl.indexOf("?") + 1);
			} else {
				uri = requestUrl;
				data = "";
			}
		} else if (HttpMethodOption.DEFAULT.equals(httpMethodOption)) {
			if (requestInfo.getParameters() == null) {
				if ((requestUrl.indexOf("?") != -1 && requestUrl.length() > 255)) {
					uri = requestUrl.substring(0, requestUrl.indexOf('?'));
					data = requestUrl.substring(requestUrl.indexOf("?") + 1);
					httpMethodOption = HttpMethodOption.POST;
				} else {
					uri = requestUrl;
					httpMethodOption = HttpMethodOption.GET;
				}
			} else {
				String generateUrl = appendParams(requestUrl, requestInfo.getParameters());
				if (generateUrl.length() > 255) {
					uri = generateUrl.substring(0, generateUrl.indexOf('?'));
					data = generateUrl.substring(generateUrl.indexOf("?") + 1);
					httpMethodOption = HttpMethodOption.POST;
				} else {
					uri = generateUrl;
					httpMethodOption = HttpMethodOption.GET;
				}
			}
		} else {
			if (requestInfo.getParameters() != null) {
				uri = appendParams(requestUrl, requestInfo.getParameters());
			} else {
				uri = requestUrl;
			}
		}
		
		switch (httpMethodOption) {
		case GET:
			method = new HttpGet(uri);
			break;
		case POST:
			method = new HttpPost(uri);
			((HttpPost)method).setEntity(generateEntity(data, requestInfo.getParameters(), requestInfo.getUploadParam()));
			break;
		case PUT:
			method = new HttpPut(uri);
			((HttpPut)method).setEntity(generateEntity(data, requestInfo.getParameters(), requestInfo.getUploadParam()));
			break;
		case TRACE:
			method = new HttpTrace(uri);
			break;
		case HEAD:
			method = new HttpHead(uri);
			break;
		case DELETE:
			method = new HttpDelete(uri);
			break;
		case OPTIONS:
			method = new HttpOptions(uri);
			break;
			default:
				throw new UnsupportedEncodingException("Unknown Request Method");
		}
		
		//	Add gzip supported
		method.addHeader("Accept-Encoding", "gzip, deflate");
		
		if (requestInfo.getBeginPosition() != Globals.DEFAULT_VALUE_INT 
				&& requestInfo.getBeginPosition() >= 0 
				&& requestInfo.getEndPosition() != Globals.DEFAULT_VALUE_INT 
				&& requestInfo.getEndPosition() >= 0 
				&& requestInfo.getBeginPosition() < requestInfo.getEndPosition()) {
			method.addHeader("Range", "bytes=" + requestInfo.getBeginPosition() + "-" + requestInfo.getEndPosition());
		}
		
		if (requestInfo.getHeaders() != null) {
			for (Header header : requestInfo.getHeaders()) {
				method.addHeader(header);
			}
		}
		
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			
			Builder builder = RequestConfig.custom();
			
			builder.setCookieSpec(CookieSpecs.DEFAULT);
			
			int timeout = requestInfo.getTimeOut() == Globals.DEFAULT_VALUE_INT ? DEFAULT_TIME_OUT : requestInfo.getTimeOut();
			builder.setSocketTimeout(timeout * 1000);
			builder.setConnectionRequestTimeout(timeout * 1000);
			
			RequestConfig requestConfig = builder.build();
			method.setConfig(requestConfig);
			
			HttpClientContext context = HttpClientContext.create();
			httpResponse = httpClient.execute(method, context);
			List<URI> redirectLocations = context.getRedirectLocations();
			if (redirectLocations != null) {
				URI location = URIUtils.resolve(method.getURI(), context.getTargetHost(), redirectLocations);
				return sendRequest(new RequestInfo(location.toASCIIString(), requestInfo));
			} else {
				HttpResponseContent responseContent = new HttpResponseContent(httpResponse);
				
				if (responseContent.getStatusCode() == HttpStatus.SC_OK) {
					if (RequestUtils.LOGGER.isDebugEnabled()) {
						RequestUtils.LOGGER.debug("Read entity length: " + responseContent.getContentLength());
					}
				}
				return responseContent;
			}
		} catch (Exception e) {
			if (RequestUtils.LOGGER.isDebugEnabled()) {
				RequestUtils.LOGGER.debug("Send Request ERROR: ", e);
			}
			method.abort();
		} finally {
			if (method != null) {
				method.reset();
			}
			
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
					if (RequestUtils.LOGGER.isDebugEnabled()) {
						RequestUtils.LOGGER.debug("Close response error! ", e);
					}
				}
			}
			
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					if (RequestUtils.LOGGER.isDebugEnabled()) {
						RequestUtils.LOGGER.debug("Close client error! ", e);
					}
				}
			}
		}
		
		return null;
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
	 * @param ampersand String to use for ampersands (e.g. "&" or "&amp;")
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
	 * @param ampersand String to use for ampersands (e.g. "&" or "&amp;")
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
		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
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
	 */

	private static HttpEntity generateEntity(String data, Map<String, String[]> parameters, 
			Map<String, File> uploadFileMap) throws UnsupportedEncodingException {
		if (parameters == null) {
			parameters = new HashMap<String, String[]>();
		}
		
		Map<String, String[]> parameter = getRequestParametersFromString(data);
		
		Iterator<String> iterator = parameter.keySet().iterator();
		
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (parameters.containsKey(key) && RequestUtils.LOGGER.isDebugEnabled()) {
				RequestUtils.LOGGER.debug("Override parameter : {}", key);
			}
			parameters.put(key, parameter.get(key));
		}
		
		if (uploadFileMap == null || uploadFileMap.isEmpty()) {
			iterator = parameters.keySet().iterator();
			
			List<NameValuePair> requestParams = new ArrayList<NameValuePair>();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				String[] values = parameters.get(key);

				for (String value : values) {
					requestParams.add(new BasicNameValuePair(key, value));
				}
			}
			
			return new UrlEncodedFormEntity(requestParams, Globals.DEFAULT_ENCODING);
		} else {
			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			
			iterator = parameters.keySet().iterator();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				String[] values = parameters.get(key);

				for (String value : values) {
					entityBuilder = entityBuilder.addPart(key, new StringBody(value, ContentType.TEXT_PLAIN));
				}
			}

			iterator = uploadFileMap.keySet().iterator();
			
			while (iterator.hasNext()) {
				String key = iterator.next();
				File uploadFile = uploadFileMap.get(key);

				entityBuilder = entityBuilder.addPart(key, new FileBody(uploadFile));
			}
			
			return entityBuilder.build();
		}
	}
	
	/**
	 * Appends new key and value pair to query string
	 *
	 * @param key         parameter name
	 * @param value       value of parameter
	 * @param queryString existing query string
	 * @param ampersand   string to use for ampersand (e.g. "&" or "&amp;")
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
}
