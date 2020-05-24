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

import com.nervousync.commons.beans.ip.IPRange;
import com.nervousync.commons.beans.servlet.request.RequestAttribute;
import com.nervousync.commons.beans.servlet.request.RequestInfo;
import com.nervousync.commons.beans.servlet.response.HttpResponseContent;
import com.nervousync.commons.core.Globals;
import com.nervousync.commons.core.RegexGlobals;
import com.nervousync.commons.http.cert.CertInfo;
import com.nervousync.commons.http.cookie.CookieEntity;
import com.nervousync.commons.http.entity.HttpEntity;
import com.nervousync.commons.http.header.SimpleHeader;
import com.nervousync.commons.http.proxy.ProxyInfo;
import com.nervousync.enumerations.ip.IPType;
import com.nervousync.enumerations.web.HttpMethodOption;

import javax.jws.WebService;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.handler.HandlerResolver;

import com.nervousync.exceptions.servlet.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
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
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
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

	private static final String DEFAULT_PASSPHRASE = "changeit";
	
	private static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_METHOD_POST = "POST";
	private static final String HTTP_METHOD_PUT = "PUT";
	private static final String HTTP_METHOD_TRACE = "TRACE";
	private static final String HTTP_METHOD_HEAD = "HEAD";
	private static final String HTTP_METHOD_DELETE = "DELETE";
	private static final String HTTP_METHOD_OPTIONS = "OPTIONS";

	static {
		try {
			RequestUtils.initTrustManager(null);
		} catch (Exception e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Initialize trust manager error! ", e);
			}
		}
	}

	private RequestUtils() {
		
	}

	/**
	 * Retrieve default value of target class
	 * @param clazz	target class
	 * @return	default value or null if not defined
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		if (clazz.isArray()) {
			return Array.newInstance(clazz.getComponentType(), 0);
		} else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
			return Globals.DEFAULT_VALUE_INT;
		} else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
			return Globals.DEFAULT_VALUE_LONG;
		} else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
			return Globals.DEFAULT_VALUE_SHORT;
		} else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
			return Globals.DEFAULT_VALUE_DOUBLE;
		} else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
			return Globals.DEFAULT_VALUE_FLOAT;
		} else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
			return Globals.DEFAULT_VALUE_BOOLEAN;
		} else if (clazz.equals(String.class)) {
			return Globals.DEFAULT_VALUE_STRING;
		} else {
			return null;
		}
	}

	/**
	 * Parse HTTP method from string to com.nervousync.enumerations.web.HttpMethodOption
	 * @param method	string of HTTP method
	 * @return	com.nervousync.enumerations.web.HttpMethodOption
	 */
	public static HttpMethodOption httpMethodOption(String method) {
		if (method == null) {
			return HttpMethodOption.DEFAULT;
		}
		
		if (HTTP_METHOD_GET.equalsIgnoreCase(method)) {
			return HttpMethodOption.GET;
		} else if (HTTP_METHOD_HEAD.equalsIgnoreCase(method)) {
			return HttpMethodOption.HEAD;
		} else if (HTTP_METHOD_PUT.equalsIgnoreCase(method)) {
			return HttpMethodOption.PUT;
		} else if (HTTP_METHOD_POST.equalsIgnoreCase(method)) {
			return HttpMethodOption.POST;
		} else if (HTTP_METHOD_TRACE.equalsIgnoreCase(method)) {
			return HttpMethodOption.TRACE;
		} else if (HTTP_METHOD_DELETE.equalsIgnoreCase(method)) {
			return HttpMethodOption.DELETE;
		} else if (HTTP_METHOD_OPTIONS.equalsIgnoreCase(method)) {
			return HttpMethodOption.OPTIONS;
		} else {
			return HttpMethodOption.DEFAULT;
		}
	}
	
	/**
	 * Resolve domain name to IP address
	 * @param domainName	domain name
	 * @return	IP address
	 */
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

	/**
	 * Calculate IP range address
	 * @param ipAddress		IP address in range
	 * @param cidr			CIDR
	 * @return				IPRange object
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
	 * @param netmask	Net mask address
	 * @return			CIDR
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
	 * @param cidr		CIDR
	 * @return			Net mask address
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
			return stringBuilder.toString().substring(1);
		}
		return null;
	}
	
	/**
	 * Convert IPv4 address to compatible IPv6 address
	 * @param ipAddress		IPv4 address
	 * @return				Compatible IPv6 address
	 */
	public static String convertIPv4ToCompatibleIPv6(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			return "::" + ipAddress;
		}
		return null;
	}
	
	/**
	 * Convert IPv4 address to IPv6 address and collapse
	 * @param ipAddress		IPv4 address
	 * @return				Collapse IPv6 address
	 */
	public static String convertIPv4ToIPv6(String ipAddress) {
		return convertIPv4ToIPv6(ipAddress, true);
	}
	
	/**
	 * Convert IPv4 address to IPv6 address
	 * @param ipAddress		IPv4 address
	 * @param collapse		Collapse IPv6 address
	 * @return				IPv6 address
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
	 * @param ipAddress		IPv4 address
	 * @return				byte array
	 */
	public static byte[] convertIPv4ToBytes(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, ".");
			byte[] addressBytes = new byte[4];

			addressBytes[0] = (byte)Integer.parseInt(splitAddress[0]);
			addressBytes[1] = (byte)Integer.parseInt(splitAddress[1]);
			addressBytes[2] = (byte)Integer.parseInt(splitAddress[2]);
			addressBytes[3] = (byte)Integer.parseInt(splitAddress[3]);
			
			return addressBytes;
		}
		return null;
	}
	
	/**
	 * Convert IP address to BigInteger(supported IPv4 and IPv6)
	 * @param ipAddress		IP address
	 * @return				BigInteger
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
	 * @param ipAddress		IPv4 address
	 * @return				BigInteger
	 */
	public static BigInteger convertIPv4ToBigInteger(String ipAddress) {
		if (StringUtils.matches(ipAddress, RegexGlobals.IPV4_REGEX)) {
			String[] splitAddress = StringUtils.tokenizeToStringArray(ipAddress, ".");
			if (splitAddress.length == 4) {
				BigInteger bigInteger = BigInteger.ZERO;
				
				for (int i = 0 ; i < splitAddress.length ; i++) {
					BigInteger currentInteger = BigInteger.valueOf(Long.parseLong(splitAddress[3 - i])).shiftLeft(i * 8);
					bigInteger = bigInteger.add(currentInteger);
				}
				return bigInteger;
			}
		}
		return null;
	}
	
	/**
	 * Convert IPv6 address to BigInteger
	 * @param ipAddress		IPv6 address
	 * @return				BigInteger
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
					return null;
				}
				bigInteger = bigInteger.add(currentInteger.shiftLeft(16 * (splitAddress.length - index - 1)));
				index++;
			}
			return bigInteger;
		}
		return null;
	}
	
	/**
	 * Convert BigInteger value to IPv4 address(x.x.x.x)
	 * @param bigInteger		BigInteger value
	 * @return					IPv4 address
	 */
	public static String convertBigIntegerToIPv4(BigInteger bigInteger) {
		StringBuilder ipv4Address = new StringBuilder();
		BigInteger ff = BigInteger.valueOf(0xFFL);
		
		for (int i = 0 ; i < 4 ; i++) {
			ipv4Address.insert(0, "." + bigInteger.and(ff).toString());
			bigInteger = bigInteger.shiftRight(8);
		}
		
		return ipv4Address.substring(1);
	}

	/**
	 * Convert BigInteger value to IPv6 address(xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx or :xxxx:xxxx::xxxx)
	 * @param bigInteger		BigInteger value
	 * @return					IPv6 address
	 */
	public static String convertBigIntegerToIPv6Address(BigInteger bigInteger) {
		StringBuilder ipv6Address = new StringBuilder();
		BigInteger ff = BigInteger.valueOf(0xFFFFL);
		
		for (int i = 0 ; i < 8 ; i++) {
			ipv6Address.insert(0, ":" + bigInteger.and(ff).toString(16));
			bigInteger = bigInteger.shiftRight(16);
		}
		
		return ipv6Address.substring(1).replaceFirst(RegexGlobals.IPV6_COMPRESS_REGEX, "::");
	}
	
	/**
	 * Generate SOAP client instance
	 * @param <T>                           End point interface
	 * @param endPointUrl					End point URL
	 * @param serviceEndpointInterface		End point interface
	 * @param handlerResolver				Handler resolver
	 * @return								Generated instance
	 * @throws MalformedURLException		if no protocol is specified, or an unknown protocol is found, or spec is null.
	 */
	public static <T> T generateSOAPClient(String endPointUrl, 
			Class<T> serviceEndpointInterface, HandlerResolver handlerResolver) 
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
		
		if (namespaceURI.length() == 0) {
			String packageName = serviceEndpointInterface.getPackage().getName();
			String[] packageNames = StringUtils.tokenizeToStringArray(packageName, ".");
			StringBuilder stringBuilder = new StringBuilder(wsdlDocumentLocation.getProtocol() + "://");
			for (int i = packageNames.length - 1 ; i >= 0 ; i--) {
				stringBuilder.append(packageNames[i]).append(".");
			}
			
			namespaceURI = stringBuilder.substring(0, stringBuilder.length() - 1) + "/";
		}
		
		if (serviceName.length() == 0) {
			serviceName = serviceEndpointInterface.getSimpleName() + "Service";
		}
		
		if (portName.length() == 0) {
			portName = serviceEndpointInterface.getSimpleName() + "Port";
		}
		
		Service service = Service.create(wsdlDocumentLocation, new QName(namespaceURI, serviceName));
		if (handlerResolver != null) {
			service.setHandlerResolver(handlerResolver);
		}
		
		return service.getPort(new QName(namespaceURI, portName), serviceEndpointInterface);
	}
	
	/**
	 * Retrieve response content length
	 * @param requestUrl		URL address
	 * @return					Response content length
	 */
	public static long retrieveContentLength(String requestUrl) {
		HttpResponseContent httpResponseContent = sendRequest(requestUrl, HttpMethodOption.HEAD);
		long contentLength = Globals.DEFAULT_VALUE_LONG;
		if (httpResponseContent.getStatusCode() == HttpURLConnection.HTTP_OK) {
			contentLength = httpResponseContent.getContentLength();
		}
		return contentLength;
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl) {
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT,
				null, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, int timeOut) {
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut,
				null, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, String data) {
		return sendRequest(requestUrl, data, null, HttpMethodOption.DEFAULT);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers) {
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT,
				headers, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers, int timeOut) {
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, headers, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers) {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, headers, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers, int timeOut) {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, headers, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters) {
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, null, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters, int timeOut) {
		return processRequest(new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, null, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param httpMethodOption	HTTP method
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, HttpMethodOption httpMethodOption) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, null, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, HttpMethodOption httpMethodOption, int timeOut) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, null, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param httpMethodOption	HTTP method
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, String data, HttpMethodOption httpMethodOption) {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, null, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, headers, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption, int timeOut) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, headers, null, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption) {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT,
				headers, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, String data, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption, int timeOut) {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut,
				headers, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param httpMethodOption	HTTP method
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters,
	                                              HttpMethodOption httpMethodOption) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT,
				null, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters,
	                                              HttpMethodOption httpMethodOption, int timeOut) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut,
				null, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters,
	                                              List<SimpleHeader> headers, HttpMethodOption httpMethodOption) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT,
				headers, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters,
	                                              List<SimpleHeader> headers, HttpMethodOption httpMethodOption, int timeOut) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut, headers, parameters, null));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param uploadParam		Upload resource parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters,
	                                              Map<String, File> uploadParam, List<SimpleHeader> headers, HttpMethodOption httpMethodOption) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT,
				headers, parameters, uploadParam));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param uploadParam		Upload resource parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, Map<String, String[]> parameters,
	                                              Map<String, File> uploadParam, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption, int timeOut) {
		return processRequest(new RequestInfo(httpMethodOption, requestUrl, timeOut,
				headers, parameters, uploadParam));
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, byte[] sendDatas, String contentType) {
		return processRequest(new RequestInfo(HttpMethodOption.POST, requestUrl, Globals.DEFAULT_VALUE_INT, null, sendDatas, contentType, Globals.DEFAULT_ENCODING));
	}
	
	/**
	 * Send request and receive response
	 * @param httpMethodOption	HTTP method
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(HttpMethodOption httpMethodOption, String requestUrl, byte[] sendDatas, String contentType) {
		if (HttpMethodOption.POST.equals(httpMethodOption) || HttpMethodOption.PUT.equals(httpMethodOption)) {
			return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, null, sendDatas, contentType, Globals.DEFAULT_ENCODING));
		} else {
			return null;
		}
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @param charset           Character encoding
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(String requestUrl, byte[] sendDatas, String contentType, String charset) {
		return processRequest(new RequestInfo(HttpMethodOption.POST, requestUrl, Globals.DEFAULT_VALUE_INT, null, sendDatas, contentType, charset));
	}
	
	/**
	 * Send request and receive response
	 * @param httpMethodOption	HTTP method
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @param charset           Character encoding
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendRequest(HttpMethodOption httpMethodOption, String requestUrl, byte[] sendDatas, String contentType, String charset) {
		if (HttpMethodOption.POST.equals(httpMethodOption) || HttpMethodOption.PUT.equals(httpMethodOption)) {
			return processRequest(new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, null, sendDatas, contentType, charset));
		} else {
			return null;
		}
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT,
				null, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, int timeOut, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut,
				null, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, String data, String passPhrase, CertInfo certInfo) {
		return sendSecureRequest(requestUrl, data, null, HttpMethodOption.DEFAULT, passPhrase, certInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, List<SimpleHeader> headers, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT,
				headers, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, List<SimpleHeader> headers, int timeOut, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, headers, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, String data, List<SimpleHeader> headers, String passPhrase, CertInfo certInfo) {
		return sendSecureRequest(requestUrl, data, headers, Globals.DEFAULT_VALUE_INT, passPhrase, certInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, String data, List<SimpleHeader> headers, int timeOut, String passPhrase, CertInfo certInfo) {
		Map<String, String[]> parameters = (data == null ? null : getRequestParametersFromString(data));
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, headers, parameters, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, Globals.DEFAULT_VALUE_INT, null, parameters, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters, int timeOut, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.DEFAULT, requestUrl, timeOut, null, parameters, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param httpMethodOption	HTTP method
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, HttpMethodOption httpMethodOption, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, null, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, HttpMethodOption httpMethodOption, int timeOut, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, timeOut, null, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param httpMethodOption	HTTP method
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, String data, HttpMethodOption httpMethodOption, String passPhrase, CertInfo certInfo) {
		return sendSecureRequest(requestUrl, (data == null ? null : getRequestParametersFromString(data)), null, null, httpMethodOption, passPhrase, certInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT, headers, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption, int timeOut, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, timeOut, headers, null, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, String data, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption, String passPhrase, CertInfo certInfo) {
		return sendSecureRequest(requestUrl, (data == null ? null : getRequestParametersFromString(data)), null, headers, httpMethodOption, passPhrase, certInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param data				Request datas
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, String data, List<SimpleHeader> headers,
	                                              HttpMethodOption httpMethodOption, int timeOut, String passPhrase, CertInfo certInfo) {
		return sendSecureRequest(requestUrl, (data == null ? null : getRequestParametersFromString(data)), null, headers, httpMethodOption, timeOut, passPhrase, certInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param httpMethodOption	HTTP method
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters,
	                                              HttpMethodOption httpMethodOption, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT,
				null, parameters, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters,
	                                                    HttpMethodOption httpMethodOption, int timeOut,
	                                                    String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, timeOut,
				null, parameters, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters,
	                                                    List<SimpleHeader> headers, HttpMethodOption httpMethodOption,
	                                                    String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT,
				headers, parameters, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters,
	                                                    List<SimpleHeader> headers, HttpMethodOption httpMethodOption,
	                                                    int timeOut, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, timeOut, headers, parameters, null);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param uploadParam		Upload resource parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters,
	                                                    Map<String, File> uploadParam, List<SimpleHeader> headers,
	                                                    HttpMethodOption httpMethodOption, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, Globals.DEFAULT_VALUE_INT,
				headers, parameters, uploadParam);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param parameters		Request parameters
	 * @param uploadParam		Upload resource parameters
	 * @param headers			Request header values
	 * @param httpMethodOption	HTTP method
	 * @param timeOut			value of time out
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, Map<String, String[]> parameters,
	                                                    Map<String, File> uploadParam, List<SimpleHeader> headers,
	                                                    HttpMethodOption httpMethodOption, int timeOut, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl, timeOut,
				headers, parameters, uploadParam);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, byte[] sendDatas, String contentType,
	                                                    String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.POST, requestUrl, Globals.DEFAULT_VALUE_INT,
				null, sendDatas, contentType, Globals.DEFAULT_ENCODING);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param httpMethodOption	HTTP method
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(HttpMethodOption httpMethodOption, String requestUrl,
	                                                    byte[] sendDatas, String contentType, String passPhrase, CertInfo certInfo) {
		if (HttpMethodOption.POST.equals(httpMethodOption) || HttpMethodOption.PUT.equals(httpMethodOption)) {
			RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl,
					Globals.DEFAULT_VALUE_INT, null, sendDatas, contentType, Globals.DEFAULT_ENCODING);
			requestInfo.setCertInfo(certInfo);
			requestInfo.setPassPhrase(passPhrase);
			return processRequest(requestInfo);
		} else {
			return null;
		}
	}
	
	/**
	 * Send request and receive response
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @param charset           Character encoding
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(String requestUrl, byte[] sendDatas, String contentType,
	                                                    String charset, String passPhrase, CertInfo certInfo) {
		RequestInfo requestInfo = new RequestInfo(HttpMethodOption.POST, requestUrl, Globals.DEFAULT_VALUE_INT,
				null, sendDatas, contentType, charset);
		requestInfo.setCertInfo(certInfo);
		requestInfo.setPassPhrase(passPhrase);
		return processRequest(requestInfo);
	}
	
	/**
	 * Send request and receive response
	 * @param httpMethodOption	HTTP method
	 * @param requestUrl		URL address
	 * @param sendDatas         Send data arrays
	 * @param contentType       Content type
	 * @param charset           Character encoding
	 * @param passPhrase        Default pass phrase
	 * @param certInfo          Certificate information
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent sendSecureRequest(HttpMethodOption httpMethodOption, String requestUrl,
	                                                    byte[] sendDatas, String contentType, String charset,
	                                                    String passPhrase, CertInfo certInfo) {
		if (HttpMethodOption.POST.equals(httpMethodOption) || HttpMethodOption.PUT.equals(httpMethodOption)) {
			RequestInfo requestInfo = new RequestInfo(httpMethodOption, requestUrl,
					Globals.DEFAULT_VALUE_INT, null, sendDatas, contentType, charset);
			requestInfo.setCertInfo(certInfo);
			requestInfo.setPassPhrase(passPhrase);
			return processRequest(requestInfo);
		} else {
			return null;
		}
	}
	
	/**
	 * Send request and receive response
	 * @param requestInfo		Request info
	 * @return					HttpResponseContent
	 */
	private static HttpResponseContent processRequest(RequestInfo requestInfo) {
		return RequestUtils.processRequest(requestInfo, null);
	}
	
	/**
	 * Send request and receive response
	 * @param requestInfo		Request info
	 * @param cookieList		Cookie  list
	 * @return					HttpResponseContent
	 */
	public static HttpResponseContent processRequest(RequestInfo requestInfo, List<CookieEntity> cookieList) {
		HttpURLConnection urlConnection = null;
		OutputStream outputStream = null;
		
		try {
			urlConnection = openConnection(requestInfo, cookieList);
			int timeout = requestInfo.getTimeOut() == Globals.DEFAULT_VALUE_INT ? DEFAULT_TIME_OUT : requestInfo.getTimeOut();
			urlConnection.setConnectTimeout(timeout * 1000);
			urlConnection.setReadTimeout(timeout * 1000);
			
			if (requestInfo.getHeaders() != null) {
				for (SimpleHeader simpleHeader : requestInfo.getHeaders()) {
					urlConnection.setRequestProperty(simpleHeader.getHeaderName(), simpleHeader.getHeaderValue());
				}
			}
			
			if ((HttpMethodOption.POST.equals(requestInfo.getHttpMethodOption()) || HttpMethodOption.PUT.equals(requestInfo.getHttpMethodOption()))
					&& requestInfo.getPostDatas() != null && requestInfo.getPostDatas().length > 0) {
				
				urlConnection.setRequestProperty("Content-Type", requestInfo.getContentType() + ";charset=" + requestInfo.getCharset());
				urlConnection.setRequestProperty("Content-Length", String.valueOf(requestInfo.getPostDatas().length));
				outputStream = urlConnection.getOutputStream();
				outputStream.write(requestInfo.getPostDatas());
			} else {
				HttpEntity httpEntity = generateEntity(requestInfo.getParameters(), requestInfo.getUploadParam());
				
				urlConnection.setRequestProperty("Content-Type",
						httpEntity.generateContentType(requestInfo.getCharset(), requestInfo.getHttpMethodOption()));
				
				if (HttpMethodOption.POST.equals(requestInfo.getHttpMethodOption())
						|| HttpMethodOption.PUT.equals(requestInfo.getHttpMethodOption())) {
					outputStream = urlConnection.getOutputStream();
					httpEntity.writeData(requestInfo.getCharset(), outputStream);
				}
			}
			
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
			
			String redirectUrl = urlConnection.getHeaderField("Location");
			if (redirectUrl != null) {
				if (cookieList == null) {
					cookieList = new ArrayList<>();
				}

				for (Entry<String, List<String>> entry : urlConnection.getHeaderFields().entrySet()) {
					if ("Set-Cookie".equals(entry.getKey())) {
						for (String cookieValue : entry.getValue()) {
							cookieList.add(new CookieEntity(cookieValue));
						}
					}
				}
				
				return RequestUtils.processRequest(new RequestInfo(redirectUrl, requestInfo), cookieList);
			}
			return new HttpResponseContent(urlConnection);
		} catch (IOException e) {
			if (RequestUtils.LOGGER.isDebugEnabled()) {
				RequestUtils.LOGGER.debug("Send Request ERROR: ", e);
			}
			return null;
		} finally {
			IOUtils.closeStream(outputStream);
			
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}
	
	/**
	 * Initialize certificate trust manager
	 * @param passPhrase		pass phrase
	 * @throws Exception		if initialize failed
	 */
	public static void initTrustManager(String passPhrase) throws Exception {
		NervousyncX509TrustManager.init(passPhrase);

		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(new KeyManager[0], new TrustManager[]{NervousyncX509TrustManager.getInstance()}, new SecureRandom());
		SSLContext.setDefault(sslContext);
	}
	
	/**
	 * Add custom certificate
	 * @param certPath			certificate path
	 * @param passPhrase		certificate pass phrase
	 * @throws Exception		if certificate was not found
	 */
	public static void addCustomCert(String certPath, String passPhrase) throws Exception {
		NervousyncX509TrustManager.getInstance().addCustomCert(new TrustedCert(certPath, passPhrase));
	}

	/**
	 * Remove custom certificate
	 * @param certPath			certificate path
	 * @param passPhrase		certificate pass phrase
	 * @throws Exception		if certificate was not found
	 */
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
		HashMap<String, String[]> parameterMap = new HashMap<>();
		
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
     * @param uri An address that is base for adding params
     * @param params A map of parameters
     * @return resulting URI
     */
    public static String appendParams(String uri, Map<String, String[]> params) {
        String delimiter = (uri.indexOf('?') == -1) ? "?" : "&";
        return uri + delimiter + RequestUtils.createQueryStringFromMap(params, "&").toString();
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
	 * @param request a request to which saved in session parameters will be
     * assigned
	 */
	public static void reclaimRequestAttributes(HttpServletRequest request) {
		RequestAttribute requestAttribute =
		        (RequestAttribute)request.getSession().getAttribute(STOWED_REQUEST_ATTRIBS);

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
    
    public static boolean isUserInRole(Iterator<Object> rolesIterator, HttpServletRequest request) {
    	return isUserInRole(rolesIterator, null, request);
    }
    
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
	 * @param request		HttpServletRequest Object
	 * @return client IP address
	 */
	public static String getClientIP(HttpServletRequest request) {
		request.getHeaderNames();
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

	public static String getRequestURI(HttpServletRequest request) throws RequestException {
		String requestUrl = RequestUtils.getRequestPath(request);

		if (requestUrl.lastIndexOf('.') != -1) {
			return requestUrl.substring(0, requestUrl.lastIndexOf("."));
		}
		return requestUrl;
	}

	public static String getRequestPath(HttpServletRequest request) throws RequestException {
		if (request == null) {
			throw new RequestException("Request object must not be null");
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
		
		StringBuilder requestUrl = new StringBuilder();
		
		if (includeDomain) {
			requestUrl.append(RequestUtils.getAppURL(request));
		}
		
		requestUrl.append(request.getRequestURI());
		
		if (request.getQueryString() != null && request.getQueryString().length() > 0) {
			requestUrl.append("?").append(request.getQueryString());
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
			if (matcher.find()) {
				for (int i = 0 ; i < matcher.groupCount() ; i++) {
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
	 * @param ipv6Address      IPv6 address
	 * @return                 Expand IPv6 address
	 */
	public static String expandIgnore(String ipv6Address) {
		if (ipv6Address.contains("::")) {
			int count = StringUtils.countOccurrencesOf(ipv6Address, ":");
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = count ; i < 8 ; i++) {
				stringBuilder.append(":0000");
			}
			ipv6Address = StringUtils.replace(ipv6Address, "::", stringBuilder.toString() + ":");
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
	 * @param ipAddress		IP address in range
	 * @param netmask		Net mask address
	 * @return				Begin IP address
	 */
	private static String beginIPv4(String ipAddress, String netmask) {
		String[] addressItems = StringUtils.tokenizeToStringArray(ipAddress, ".");
		String[] maskItems = StringUtils.tokenizeToStringArray(netmask, ".");

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0 ; i < 4 ; i++) {
			int itemValue = i < addressItems.length ? Integer.parseInt(addressItems[i])
					: Globals.INITIALIZE_INT_VALUE;
			int beginItem = itemValue & Integer.parseInt(maskItems[i]);
			if (itemValue == 0 && i == 3) {
				beginItem++;
			}
			stringBuilder.append(".").append(beginItem);
		}

		return stringBuilder.toString().substring(1);
	}

	/**
	 * Calculate IP range end address
	 * @param beginIP		Begin address of range
	 * @param netmask		Net mask address
	 * @return				End IP address
	 */
	private static String endIPv4(String beginIP, String netmask) {
		String[] addressItems = StringUtils.tokenizeToStringArray(beginIP, ".");
		String[] maskItems = StringUtils.tokenizeToStringArray(netmask, ".");

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0 ; i < 4 ; i++) {
			int endItem = 255 - Integer.parseInt(addressItems[i]) ^ Integer.parseInt(maskItems[i]);
			stringBuilder.append(".").append(endItem);
		}

		return stringBuilder.toString().substring(1);
	}

	/**
	 * Calculate IPv6 range begin address
	 * @param ipAddress     IPv6 address
	 * @param cidr          CIDR
	 * @return              Begin address
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
	 * @param ipAddress     IPv6 address
	 * @param cidr          CIDR
	 * @return              End address
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
	 * @param hexAddress    hex data address
	 * @return              binary data address
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
			binBuilder.append(binItem.toString());
			index++;
		}
		return binBuilder.toString();
	}

	/**
	 * Convert IPv6 address from binary data to hex data
	 * @param binAddress    binary data address
	 * @return              hex data address
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

	private static HttpURLConnection openConnection(RequestInfo requestInfo, List<CookieEntity> cookieList,
			TrustedCert... trustedCerts) throws UnsupportedEncodingException {
		String method;
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
		
		String urlAddress;

		if (HttpMethodOption.POST.equals(requestInfo.getHttpMethodOption()) 
				|| HttpMethodOption.PUT.equals(requestInfo.getHttpMethodOption())) {
			urlAddress = requestInfo.getRequestUrl();
		} else {
			urlAddress = appendParams(requestInfo.getRequestUrl(), requestInfo.getParameters());
		}
		
		HttpURLConnection connection;
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
							StringUtils.base64Encode(authentication.getBytes(Charset.forName(Globals.DEFAULT_ENCODING))));
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
			String cookie = RequestUtils.generateCookie(requestInfo.getRequestUrl(), cookieList);
			if (cookie != null) {
				connection.setRequestProperty("Cookie", cookie);
			}
			
			if (urlAddress.startsWith(Globals.SECURE_HTTP_PROTOCOL)) {
				((HttpsURLConnection)connection).setHostnameVerifier(new NervousyncHostnameVerifier());
				if (requestInfo.getCertInfo() != null) {
					SSLContext sslContext = SSLContext.getInstance("TLS");
					sslContext.init(requestInfo.getCertInfo().generateKeyManagers(),
							retrieveDefaultTrustManagers(requestInfo.getPassPhrase()), new SecureRandom());
					((HttpsURLConnection)connection).setSSLSocketFactory(sslContext.getSocketFactory());
				}
			}
		} catch (IOException | NoSuchAlgorithmException e) {
			LOGGER.error("Build connection error! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
			connection = null;
		} catch (com.nervousync.exceptions.http.CertificateException | CertificateException | KeyManagementException e) {
			LOGGER.error("Process security certificate error! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
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
	 * @param parameters The parameter map
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
	
	private static TrustManager[] retrieveDefaultTrustManagers(String passPhrase) throws CertificateException {
		try {
			if (passPhrase == null || passPhrase.length() == 0) {
				passPhrase = DEFAULT_PASSPHRASE;
			}
			
			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStore.load(FileUtils.loadFile(SystemUtils.systemCertPath()), passPhrase.toCharArray());
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(trustStore);
			return trustManagerFactory.getTrustManagers();
		} catch (Exception e) {
			throw new CertificateException("Read default trust manager error! ", e);
		}
	}

	private static final class TrustedCert {
		
		private final String certPath;
		private final String certPassphrase;
		
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

		private static NervousyncX509TrustManager INSTANCE = null;
		
		private final Logger logger = LoggerFactory.getLogger(this.getClass());
		
		private final String passPhrase;
		private final List<TrustedCert> customCerts;
		private X509TrustManager trustManager = null;
		
		private NervousyncX509TrustManager(String passPhrase) throws Exception {
			this.passPhrase = passPhrase != null ? passPhrase : DEFAULT_PASSPHRASE;
			this.customCerts = new ArrayList<>();
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
			if (!FileUtils.isExists(SystemUtils.systemCertPath())) {
				this.logger.warn("System cert file not found!");
			} else {
				try {
					keyStore.load(FileUtils.loadFile(SystemUtils.systemCertPath()), this.passPhrase.toCharArray());
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
