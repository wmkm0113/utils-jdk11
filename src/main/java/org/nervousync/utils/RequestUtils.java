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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import org.nervousync.annotations.beans.OutputConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.*;
import java.time.Duration;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import org.nervousync.beans.servlet.request.RequestAttribute;
import org.nervousync.beans.servlet.request.RequestInfo;
import org.nervousync.beans.servlet.response.ResponseInfo;
import org.nervousync.commons.core.Globals;
import org.nervousync.commons.http.cookie.CookieEntity;
import org.nervousync.commons.http.entity.HttpEntity;
import org.nervousync.commons.proxy.ProxyConfig;
import org.nervousync.commons.http.security.GeneX509TrustManager;
import org.nervousync.enumerations.web.HttpMethodOption;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.net.ssl.*;

public final class RequestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestUtils.class);

    private static final String STOWED_REQUEST_ATTRIBS = "ssl.redirect.attrib.stowed";

    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_TRACE = "TRACE";
    public static final String HTTP_METHOD_HEAD = "HEAD";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String HTTP_METHOD_OPTIONS = "OPTIONS";

    private RequestUtils() {
    }

    public static HttpMethodOption httpMethodOption(final String method) {
        if (StringUtils.isEmpty(method)) {
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

    public static HttpMethodOption httpMethodOption(final Method method) {
        if (method == null) {
            return HttpMethodOption.UNKNOWN;
        }

        if (method.isAnnotationPresent(GET.class)) {
            return HttpMethodOption.GET;
        }

        if (method.isAnnotationPresent(HEAD.class)) {
            return HttpMethodOption.HEAD;
        }

        if (method.isAnnotationPresent(PUT.class)) {
            return HttpMethodOption.PUT;
        }

        if (method.isAnnotationPresent(POST.class)) {
            return HttpMethodOption.POST;
        }

        if (method.isAnnotationPresent(DELETE.class)) {
            return HttpMethodOption.DELETE;
        }

        if (method.isAnnotationPresent(PATCH.class)) {
            return HttpMethodOption.PATCH;
        }

        return HttpMethodOption.UNKNOWN;
    }

    public static String resolveDomain(final String domainName) {
        try {
            InetAddress[] inetAddresses = InetAddress.getAllByName(domainName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Resolve domain {} receive address count {}", domainName, inetAddresses.length);
            }
            StringBuilder stringBuilder = new StringBuilder();
            List.of(InetAddress.getAllByName(domainName))
                    .forEach(inetAddress -> stringBuilder.append(",").append(inetAddress.getHostAddress()));
            return stringBuilder.substring(1);
        } catch (UnknownHostException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Resolve domain error! ", e);
            }
        }
        return Globals.DEFAULT_VALUE_STRING;
    }

    public static Certificate serverCertificate(final String urlAddress) {
        if (StringUtils.notBlank(urlAddress)) {
            try {
                int beginIndex, endIndex;
                if (urlAddress.toLowerCase().startsWith(Globals.SECURE_HTTP_PROTOCOL)) {
                    beginIndex = Globals.SECURE_HTTP_PROTOCOL.length();
                } else if (urlAddress.toLowerCase().startsWith(Globals.HTTP_PROTOCOL)) {
                    beginIndex = Globals.HTTP_PROTOCOL.length();
                } else {
                    beginIndex = Globals.INITIALIZE_INT_VALUE;
                }
                endIndex = urlAddress.indexOf("/", beginIndex);
                if (endIndex < 0) {
                    endIndex = urlAddress.length();
                }
                final String domainName = urlAddress.substring(beginIndex, endIndex);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(urlAddress).openConnection();
                httpsURLConnection.connect();
                return Arrays.stream(httpsURLConnection.getServerCertificates())
                        .filter(serverCertificate -> verifyCertificate((X509Certificate) serverCertificate, domainName))
                        .findFirst()
                        .orElse(null);
            } catch (IOException e) {
                LOGGER.error("Read server certificate error! ");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Stack message: ", e);
                }
            }
        }
        return null;
    }

    private static boolean verifyCertificate(final X509Certificate x509Certificate, final String domainName) {
        if (x509Certificate == null || StringUtils.isEmpty(domainName)) {
            return Boolean.FALSE;
        }
        try {
            x509Certificate.checkValidity();
            if (x509Certificate.getVersion() == 3) {
                Collection<List<?>> collection = x509Certificate.getSubjectAlternativeNames();
                if (collection != null) {
                    boolean matchResult;
                    if (IPUtils.isIPv4Address(domainName) || IPUtils.isIPv6Address(domainName)) {
                        matchResult = collection.stream()
                                //  See RFC 5280 section 4.2.1.6
                                .filter(dataList -> ((Integer) dataList.get(0)) == 7)
                                .map(dataList -> (String) dataList.get(1))
                                .anyMatch(domainName::equalsIgnoreCase);
                    } else {
                        matchResult = collection.stream()
                                //  See RFC 5280 section 4.2.1.6
                                .filter(dataList -> ((Integer) dataList.get(0)) == 2)
                                .map(dataList -> (String) dataList.get(1))
                                .anyMatch(matchDomain -> matchDomain(matchDomain, domainName));
                    }
                    if (matchResult) {
                        return Boolean.TRUE;
                    }
                }
            }
            LdapName ldapName = new LdapName(x509Certificate.getSubjectX500Principal().getName());
            String matchDomain = ldapName.getRdns().stream()
                    .filter(rdn -> rdn.getType().equalsIgnoreCase("CN"))
                    .findFirst()
                    .map(rdn -> (String) rdn.getValue())
                    .orElse(Globals.DEFAULT_VALUE_STRING);
            return matchDomain(matchDomain, domainName);
        } catch (CertificateNotYetValidException | CertificateExpiredException | InvalidNameException |
                 CertificateParsingException e) {
            return Boolean.FALSE;
        }
    }

    private static boolean matchDomain(final String matchDomain, final String domainName) {
        return matchDomain.startsWith("*")
                ? domainName.toLowerCase().endsWith(matchDomain.substring(1).toLowerCase())
                : domainName.equalsIgnoreCase(matchDomain);
    }

    public static int contentLength(final String requestUrl) {
        return sendRequest(RequestInfo.builder(HttpMethodOption.GET).requestUrl(requestUrl).build(), ResponseInfo.class)
                .filter(responseInfo -> responseInfo.getStatusCode() == HttpURLConnection.HTTP_OK)
                .map(ResponseInfo::getContentLength)
                .orElse(Globals.DEFAULT_VALUE_INT);
    }

    public static <T> Optional<T> sendRequest(final RequestInfo requestInfo, final Class<T> targetClass) {
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
                requestBuilder.method(requestInfo.getHttpMethodOption().toString(), HttpRequest.BodyPublishers.noBody());
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
                uri = appendParams(uri, requestInfo.getParameters());
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
        String cookie = generateCookie(requestInfo.getRequestUrl(), requestInfo.getCookieList());
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
            ProxyConfig proxyConfig = requestInfo.getProxyInfo();
            clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(proxyConfig.getProxyAddress(), proxyConfig.getProxyPort())));
            if (StringUtils.notBlank(proxyConfig.getUserName())) {
                String authentication = proxyConfig.getUserName() + ":";
                if (StringUtils.notBlank(proxyConfig.getPassword())) {
                    authentication += proxyConfig.getPassword();
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
                            .send(requestBuilder.build(), new ResponseContentHandler())
                            .body()
                            .get())
                    .map(responseInfo -> {
                        if (ResponseInfo.class.equals(targetClass)) {
                            return targetClass.cast(responseInfo);
                        }
                        Class<?> entityClass = ClassUtils.componentType(targetClass);
                        OutputConfig outputConfig = entityClass.getAnnotation(OutputConfig.class);
                        if (outputConfig == null) {
                            return responseInfo.parseObject(targetClass);
                        }
                        boolean isArray = targetClass.isArray() || ClassUtils.isAssignable(targetClass, Collection.class);
                        try {
                            switch (outputConfig.type()) {
                                case JSON:
                                    return isArray ? targetClass.cast(responseInfo.parseList(entityClass)) : responseInfo.parseJson(targetClass);
                                case XML:
                                    return responseInfo.parseXml(targetClass);
                                case YAML:
                                    return isArray ? targetClass.cast(responseInfo.parseList(entityClass)) : responseInfo.parseYaml(targetClass);
                                default:
                                    return responseInfo.parseObject(targetClass);
                            }
                        } catch (UnsupportedEncodingException e) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Parse response content error! ", e);
                            }
                            return null;
                        }
                    });
        } catch (IOException | InterruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Send Request ERROR: ", e);
            }
            return Optional.empty();
        } finally {
            System.clearProperty("jdk.internal.httpclient.disableHostnameVerification");
        }
    }

    public static String createQueryString(final HttpServletRequest request) {
        return createQueryStringFromMap(request.getParameterMap(), "&").toString();
    }

    public static Map<String, String[]> parseQueryString(final String queryString) {
        return parseQueryString(queryString, Boolean.TRUE, Boolean.FALSE);
    }

    public static Map<String, String[]> parseQueryString(final String queryString, final boolean parseMatrix) {
        return parseQueryString(queryString, Boolean.TRUE, parseMatrix);
    }

    public static Map<String, String[]> parseQueryString(final String queryString, final boolean decodeValues,
                                                         final boolean parseMatrix) {
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
                    if (parseMatrix) {
                        int splitIndex = value.indexOf(";");
                        if (splitIndex == Globals.DEFAULT_VALUE_INT) {
                            parameterMap.put(key, mergeValues(parameterMap.get(key), value));
                        } else {
                            String paramValue = value.substring(0, splitIndex);
                            parameterMap.put(key, mergeValues(parameterMap.get(key), paramValue));

                            Arrays.asList(StringUtils.tokenizeToStringArray(value.substring(splitIndex), ";"))
                                    .forEach(matrixString -> parseMatrixString(parameterMap, matrixString));
                        }
                    } else {
                        parameterMap.put(key, mergeValues(parameterMap.get(key), value));
                    }
                } catch (UnsupportedEncodingException e) {
                    // do nothing
                }
            }
            k = ampPos + 1;
        }

        return parameterMap;
    }

    private static void parseMatrixString(final Map<String, String[]> parameterMap, final String matrixString) {
        if (StringUtils.isEmpty(matrixString)) {
            return;
        }
        int equalIndex = matrixString.indexOf("=");
        if (equalIndex != Globals.DEFAULT_VALUE_INT) {
            String matrixKey = matrixString.substring(0, equalIndex);
            String matrixValue = matrixString.substring(equalIndex + 1);

            parameterMap.put(matrixKey, mergeValues(parameterMap.get(matrixKey), matrixValue));
        }
    }

    public static Map<String, String[]> parseParametersFromUri(final String uri) {
        return parseParametersFromUri(uri, Boolean.FALSE);
    }

    public static Map<String, String[]> parseParametersFromUri(final String uri, final boolean parseMatrix) {
        if (ObjectUtils.isNull(uri) || uri.trim().length() == 0) {
            return new HashMap<>();
        }

        int qSignPos = uri.indexOf('?');
        if (qSignPos == -1) {
            return new HashMap<>();
        }

        return parseQueryString(uri.substring(qSignPos + 1), parseMatrix);
    }


    public static String getBaseFromUri(final String uri) {
        if (ObjectUtils.isNull(uri) || uri.trim().length() == 0) {
            return "";
        }

        int qSignPos = uri.indexOf('?');
        if (qSignPos == -1) {
            return uri;
        }

        return uri.substring(0, qSignPos);
    }


    public static StringBuilder createQueryStringFromMap(final Map<String, String[]> m, final String ampersand,
                                                         final boolean encode) {
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

    public static StringBuilder createQueryStringFromMap(final Map<String, String[]> m, final String ampersand) {
        return createQueryStringFromMap(m, ampersand, true);
    }

    public static String appendParams(final String uri, final Map<String, String[]> params) {
        String delimiter = (uri.indexOf('?') == -1) ? "?" : "&";
        return uri + delimiter + createQueryStringFromMap(params, "&");
    }

    public static void stowRequestAttributes(final HttpServletRequest request) {
        if (request.getSession().getAttribute(STOWED_REQUEST_ATTRIBS) != null) {
            return;
        }

        request.getSession().setAttribute(STOWED_REQUEST_ATTRIBS, RequestAttribute.newInstance(request));
    }

    public static void reclaimRequestAttributes(final HttpServletRequest request) {
        RequestAttribute requestAttribute =
                (RequestAttribute) request.getSession().getAttribute(STOWED_REQUEST_ATTRIBS);

        if (requestAttribute == null) {
            return;
        }

        requestAttribute.getAttributeMap().forEach(request::setAttribute);

        request.getSession().removeAttribute(STOWED_REQUEST_ATTRIBS);
    }

    public static String getAppURL(final HttpServletRequest request) {
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

    public static boolean isUserInRole(final Iterator<Object> rolesIterator, final HttpServletRequest request) {
        return isUserInRole(rolesIterator, null, request);
    }

    public static boolean isUserInRole(final Iterator<Object> rolesIterator, final String property,
                                       final HttpServletRequest request) {
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
                    return Boolean.TRUE;
                }
            }
        } else {
            // if no role is specified, grant access
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static String getClientIP(final HttpServletRequest request) {
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

    public static String getRequestURI(final HttpServletRequest request) {
        String requestUrl = getRequestPath(request);

        if (requestUrl.lastIndexOf('.') != -1) {
            return requestUrl.substring(0, requestUrl.lastIndexOf("."));
        }
        return requestUrl;
    }

    public static String getRequestPath(final HttpServletRequest request) {
        if (request == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }
        String requestUrl = request.getRequestURI();

        if (StringUtils.notBlank(request.getContextPath())) {
            requestUrl = requestUrl.substring(request.getContextPath().length());
        }
        return requestUrl;
    }

    public static String getRequestUrl(final HttpServletRequest request) {
        return getRequestUrl(request, Boolean.TRUE);
    }

    public static String getRequestUrl(final HttpServletRequest request, final boolean includeDomain) {
        if (request == null) {
            return Globals.DEFAULT_VALUE_STRING;
        }

        StringBuilder requestUrl = new StringBuilder();

        if (includeDomain) {
            requestUrl.append(getAppURL(request));
        }

        requestUrl.append(getRequestURI(request));

        if (request.getQueryString() != null && request.getQueryString().length() > 0) {
            requestUrl.append("?").append(request.getQueryString());
        }

        return requestUrl.toString();
    }

    public static String processRewrite(final HttpServletRequest request, final String regex, final String toPath) {
        String rewriteUrl = StringUtils.replaceWithRegex(getRequestUrl(request, Boolean.FALSE), regex, toPath, "/");
        if (StringUtils.notBlank(rewriteUrl)) {
            String queryString = createQueryString(request);
            if (StringUtils.notBlank(queryString)) {
                return rewriteUrl + (rewriteUrl.contains("?") ? "&" : "?") + queryString;
            }
        }
        return rewriteUrl;
    }

    private static String generateCookie(final String requestUrl, final List<CookieEntity> cookieList) {
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

    private static String[] mergeValues(final String[] v1, final String... v2) {
        String[] values1 = Objects.requireNonNullElseGet(v1, () -> new String[0]);
        // get the second array of values
        if (v2 == null) {
            return values1;
        } else {
            // merge arrays
            return StringUtils.concatenateStringArrays(values1, v2);
        }
    }

    private static HttpEntity generateEntity(Map<String, String[]> parameters, Map<String, File> uploadFileMap) {
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

    private static void append(final Object key, final Object value, final StringBuilder queryString,
                               final String ampersand, final boolean encode) {
        if (queryString.length() > 0) {
            queryString.append(ampersand);
        }

        try {
            queryString.append(encode ? URLEncoder.encode(key.toString(), Globals.DEFAULT_ENCODING) : key);
            queryString.append("=");
            queryString.append(encode ? URLEncoder.encode(value.toString(), Globals.DEFAULT_ENCODING) : value);
        } catch (UnsupportedEncodingException e) {
            // do nothing
        }
    }

    private static final class ResponseContentHandler implements HttpResponse.BodyHandler<Supplier<ResponseInfo>> {

        public ResponseContentHandler() {
        }

        public HttpResponse.BodySubscriber<Supplier<ResponseInfo>> apply(HttpResponse.ResponseInfo responseInfo) {
            HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream,
                    inputStream -> () -> new ResponseInfo(responseInfo, inputStream));
        }
    }
}
