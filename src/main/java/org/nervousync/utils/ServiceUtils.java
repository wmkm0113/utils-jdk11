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

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.handler.HandlerResolver;
import org.nervousync.commons.core.Globals;
import org.nervousync.enumerations.web.HttpMethodOption;
import org.nervousync.restful.converter.ParameterConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * The type Service utils.
 */
public final class ServiceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUtils.class);

    private static final List<ParameterConverter> REGISTERED_CONVERTERS;

    private static Client CLIENT = ClientBuilder.newClient();

    static {
        REGISTERED_CONVERTERS = new ArrayList<>();
        ServiceLoader.load(ParameterConverter.class).forEach(ServiceUtils::registerConverter);
    }

    private ServiceUtils() {
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
    public static <T> T SOAPClient(final Class<T> serviceInterface, final HandlerResolver handlerResolver)
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
     * Generate Restful service client instance
     *
     * @param <T>           Client interface
     * @param targetAddress the target address
     * @param serviceClient Client interface class
     * @return Generated instance
     */
    public static <T> T RestfulClient(final String targetAddress, final Class<T> serviceClient) {
        return RestfulClient(targetAddress, serviceClient, null);
    }

    /**
     * Generate Restful service client instance
     *
     * @param <T>           Client interface
     * @param targetAddress the target address
     * @param serviceClient Client interface class
     * @param headerMap     the header map
     * @return Generated instance
     */
    public static <T> T RestfulClient(final String targetAddress, final Class<T> serviceClient,
                                      final Map<String, String> headerMap) {
        if (StringUtils.isEmpty(targetAddress)) {
            return null;
        }
        String servicePath = targetAddress.toLowerCase().startsWith("http")
                ? targetAddress
                : Globals.HTTP_PROTOCOL + targetAddress;
        if (serviceClient.isAnnotationPresent(Path.class)) {
            servicePath += serviceClient.getAnnotation(Path.class).value();
        }
        return ObjectUtils.createProxyInstance(serviceClient, new RestfulInterceptor(servicePath, headerMap));
    }

    /**
     * Init client.
     *
     * @param configuration the configuration
     */
    public static void initClient(final Configuration configuration) {
        CLIENT = (configuration == null) ? ClientBuilder.newClient() : ClientBuilder.newClient(configuration);
    }

    /**
     * Register converter.
     *
     * @param parameterConverter the parameter converter
     */
    public static void registerConverter(final ParameterConverter parameterConverter) {
        if (REGISTERED_CONVERTERS.contains(parameterConverter)) {
            LOGGER.warn("Exists converter: {}", parameterConverter.getClass().getName());
        }
        REGISTERED_CONVERTERS.add(parameterConverter);
    }

    /**
     * Init converter optional.
     *
     * @param targetClass the target class
     * @return the optional
     */
    public static Optional<ParameterConverter> initConverter(final Class<?> targetClass) {
        return REGISTERED_CONVERTERS
                .stream()
                .filter(parameterConverter -> parameterConverter.match(targetClass))
                .findFirst();
    }

    private static final class RestfulInterceptor implements InvocationHandler {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final String requestPath;
        private final Map<String, String> headerMap;

        /**
         * Instantiates a new Restful interceptor.
         *
         * @param requestPath the request path
         * @param headerMap   the header map
         */
        RestfulInterceptor(final String requestPath, final Map<String, String> headerMap) {
            this.requestPath = requestPath;
            this.headerMap = new HashMap<>();
            if (headerMap != null) {
                this.headerMap.putAll(headerMap);
            }
        }

        @Override
        public Object invoke(final Object o, final Method method, final Object[] objects) throws Throwable {
            HttpMethodOption methodOption = RequestUtils.httpMethodOption(method);
            if (HttpMethodOption.UNKNOWN.equals(methodOption) || !method.isAnnotationPresent(Path.class)) {
                throw new Exception("Unknown method! ");
            }

            if (!void.class.equals(method.getReturnType()) && !method.isAnnotationPresent(Produces.class)) {
                throw new Exception("Unknown response data type! ");
            }

            String methodName = method.getAnnotation(Path.class).value();
            if (methodName.length() == 0) {
                methodName = method.getName();
            } else if (methodName.startsWith("/")) {
                methodName = methodName.substring(1);
            }

            String servicePath = this.requestPath + "/" + methodName;

            Annotation[][] annotations = method.getParameterAnnotations();
            Class<?>[] parameterClasses = method.getParameterTypes();

            if (objects.length != parameterClasses.length) {
                throw new Exception("Mismatch arguments");
            }

            Map<String, String> formParameters = new HashMap<>();
            Map<String, String> queryParameters = new HashMap<>();
            Map<String, String[]> matrixParameters = new HashMap<>();

            String[] mediaTypes = method.isAnnotationPresent(Consumes.class)
                    ? method.getAnnotation(Consumes.class).value()
                    : new String[0];

            for (int i = 0 ; i < objects.length ; i++) {
                Object paramObj = objects[i];
                if (paramObj == null) {
                    continue;
                }
                if (Arrays.stream(annotations[i])
                        .anyMatch(annotation -> annotation.annotationType().equals(BeanParam.class))) {
                    BeanParameter beanParameter = new BeanParameter(paramObj, mediaTypes);
                    this.headerMap.putAll(beanParameter.getHeaders());
                    for (Map.Entry<String, String> entry : beanParameter.getPaths().entrySet()) {
                        if (StringUtils.isEmpty(entry.getKey()) || entry.getValue() == null) {
                            throw new ServiceException("Unknown parameter name or path parameter value is null! ");
                        }
                        String pathKey = "{" + entry.getKey() + "}";
                        if (servicePath.indexOf(pathKey) > 0) {
                            servicePath = StringUtils.replace(servicePath, pathKey,
                                    URLEncoder.encode(entry.getValue(), Globals.DEFAULT_ENCODING));
                        }
                    }
                    formParameters.putAll(beanParameter.getFormParameters());
                    queryParameters.putAll(beanParameter.getQueryParameters());
                    matrixParameters.putAll(beanParameter.getMatrixParameters());
                } else if (Arrays.stream(annotations[i])
                        .anyMatch(annotation -> annotation.annotationType().equals(MatrixParam.class))) {
                    Arrays.stream(annotations[i])
                            .filter(annotation -> annotation.annotationType().equals(MatrixParam.class))
                            .findFirst()
                            .map(annotation -> ((MatrixParam) annotation).value())
                            .ifPresent(paramName -> {
                                if (paramObj.getClass().isArray()) {
                                    Arrays.asList((Object[]) paramObj).forEach(itemValue -> {
                                        String paramValue = initConverter(itemValue.getClass())
                                                .map(parameterConverter ->
                                                        parameterConverter.toString(itemValue, mediaTypes))
                                                .orElse(itemValue.toString());
                                        matrixParameters.put(paramName,
                                                appendValue(matrixParameters.getOrDefault(paramName, new String[0]),
                                                        paramValue));
                                    });
                                } else if (List.class.isAssignableFrom(paramObj.getClass())) {
                                    ((List<?>) paramObj).forEach(itemValue -> {
                                        String paramValue = initConverter(itemValue.getClass())
                                                .map(parameterConverter ->
                                                        parameterConverter.toString(itemValue, mediaTypes))
                                                .orElse(itemValue.toString());
                                        matrixParameters.put(paramName,
                                                appendValue(matrixParameters.getOrDefault(paramName, new String[0]),
                                                        paramValue));
                                    });
                                } else {
                                    String paramValue = initConverter(paramObj.getClass())
                                            .map(parameterConverter ->
                                                    parameterConverter.toString(paramObj, mediaTypes))
                                            .orElse(paramObj.toString());
                                    matrixParameters.put(paramName,
                                            appendValue(matrixParameters.getOrDefault(paramName, new String[0]),
                                                    paramValue));
                                }
                            });
                } else {
                    String paramValue = initConverter(paramObj.getClass())
                            .map(parameterConverter -> parameterConverter.toString(paramObj, mediaTypes))
                            .orElse(paramObj.toString());
                    if (Arrays.stream(annotations[i])
                            .anyMatch(annotation -> annotation.annotationType().equals(QueryParam.class))) {
                        String paramName =
                                Arrays.stream(annotations[i]).filter(annotation ->
                                                annotation.annotationType().equals(QueryParam.class))
                                        .findFirst()
                                        .map(annotation -> ((QueryParam) annotation).value())
                                        .orElse(Globals.DEFAULT_VALUE_STRING);
                        if (StringUtils.notBlank(paramName)) {
                            queryParameters.put(paramName, paramValue);
                        }
                    }

                    if (Arrays.stream(annotations[i])
                            .anyMatch(annotation -> annotation.annotationType().equals(FormParam.class))) {
                        String paramName =
                                Arrays.stream(annotations[i]).filter(annotation ->
                                                annotation.annotationType().equals(FormParam.class))
                                        .findFirst()
                                        .map(annotation -> ((FormParam) annotation).value())
                                        .orElse(Globals.DEFAULT_VALUE_STRING);
                        if (StringUtils.notBlank(paramName)) {
                            queryParameters.put(paramName, paramValue);
                        }
                    }

                    if (Arrays.stream(annotations[i])
                            .anyMatch(annotation -> annotation.annotationType().equals(PathParam.class))) {
                        String paramName =
                                Arrays.stream(annotations[i]).filter(annotation ->
                                                annotation.annotationType().equals(PathParam.class))
                                        .findFirst()
                                        .map(annotation -> ((PathParam) annotation).value())
                                        .orElse(Globals.DEFAULT_VALUE_STRING);
                        if (StringUtils.notBlank(paramName)) {
                            if (StringUtils.isEmpty(paramValue)) {
                                throw new ServiceException("Unknown parameter name or path parameter value is null! ");
                            }
                            String pathKey = "{" + paramName + "}";
                            if (servicePath.indexOf(pathKey) > 0) {
                                servicePath = StringUtils.replace(servicePath, pathKey,
                                        URLEncoder.encode(paramValue, Globals.DEFAULT_ENCODING));
                            }
                        }
                    }

                    if (Arrays.stream(annotations[i])
                            .anyMatch(annotation -> annotation.annotationType().equals(HeaderParam.class))) {
                        String paramName =
                                Arrays.stream(annotations[i]).filter(annotation ->
                                                annotation.annotationType().equals(HeaderParam.class))
                                        .findFirst()
                                        .map(annotation -> ((HeaderParam) annotation).value())
                                        .orElse(Globals.DEFAULT_VALUE_STRING);
                        if (StringUtils.notBlank(paramName)) {
                            this.headerMap.put(paramName, paramValue);
                        }
                    }
                }
            }

            WebTarget webTarget = CLIENT.target(servicePath);
            queryParameters.forEach(webTarget::queryParam);
            matrixParameters.forEach(webTarget::matrixParam);
            Invocation.Builder builder = webTarget.request(method.getAnnotation(Produces.class).value());
            if (method.isAnnotationPresent(Consumes.class)) {
                builder.accept(method.getAnnotation(Consumes.class).value());
            }
            this.headerMap.forEach(builder::header);

            Form form = null;
            if (HttpMethodOption.POST.equals(methodOption)
                    || HttpMethodOption.PUT.equals(methodOption)
                    || HttpMethodOption.PATCH.equals(methodOption)) {
                form = new Form();
                formParameters.forEach(form::param);
            }

            Response response = null;

            try {
                switch (methodOption) {
                    case GET:
                        response = builder.get();
                        break;
                    case PATCH:
                        response = builder.method("PATCH",
                                Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
                        break;
                    case PUT:
                        response = builder.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
                        break;
                    case POST:
                        response = builder.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
                        break;
                    case DELETE:
                        response = builder.delete();
                        break;
                    case HEAD:
                        response = builder.head();
                        break;
                    default:
                        throw new ServiceException("Method not supported! ");
                }

                boolean operateResult;

                switch (methodOption) {
                    case PUT:
                        operateResult = (response.getStatus() == HttpServletResponse.SC_CREATED
                                || response.getStatus() == HttpServletResponse.SC_NO_CONTENT
                                || response.getStatus() == HttpServletResponse.SC_OK);
                        break;
                    case POST:
                        operateResult = (response.getStatus() == HttpServletResponse.SC_CREATED
                                || response.getStatus() == HttpServletResponse.SC_OK);
                        break;
                    case PATCH:
                    case DELETE:
                        operateResult = (response.getStatus() == HttpServletResponse.SC_NO_CONTENT);
                        break;
                    default:
                        operateResult = (response.getStatus() == HttpServletResponse.SC_OK);
                        break;
                }

                if (operateResult) {
                    if (response.getStatus() == HttpServletResponse.SC_NO_CONTENT) {
                        return null;
                    }

                    Class<?> returnType = method.getReturnType();
                    if (void.class.equals(returnType)) {
                        return null;
                    }

                    Class<?> paramClass = ReflectionUtils.parseComponentType(method);

                    String responseData = response.readEntity(String.class);
                    if (responseData.endsWith(FileUtils.CRLF)) {
                        responseData = responseData.substring(0, responseData.length() - FileUtils.CRLF.length());
                    }
                    if (responseData.endsWith(Character.toString(FileUtils.CR))) {
                        responseData = responseData.substring(0, responseData.length() - Character.toString(FileUtils.CR).length());
                    }
                    if (responseData.endsWith(Character.toString(FileUtils.LF))) {
                        responseData = responseData.substring(0, responseData.length() - Character.toString(FileUtils.LF).length());
                    }

                    switch (response.getHeaderString(HttpHeaders.CONTENT_TYPE)) {
                        case FileUtils.MIME_TYPE_JSON:
                            if (returnType.isArray()) {
                                return parseToList(responseData, paramClass).toArray();
                            } else if (List.class.isAssignableFrom(returnType)) {
                                return parseToList(responseData, paramClass);
                            }
                            return parseToObject(responseData, returnType);
                        case FileUtils.MIME_TYPE_TEXT_XML:
                        case FileUtils.MIME_TYPE_XML:
                        case FileUtils.MIME_TYPE_TEXT_YAML:
                        case FileUtils.MIME_TYPE_YAML:
                            return parseToObject(responseData, returnType);
                        case FileUtils.MIME_TYPE_TEXT:
                            return responseData;
                        default:
                            final String value = responseData;
                            return initConverter(returnType)
                                    .map(parameterConverter -> parameterConverter.fromString(returnType, value))
                                    .orElse(null);
                    }
                } else {
                    String errorMsg = response.readEntity(String.class);
                    if (this.logger.isDebugEnabled()) {
                        if (response.getStatus() == HttpServletResponse.SC_BAD_REQUEST) {
                            errorMsg += "Send request data error!";
                        } else if (HttpMethodOption.GET.equals(methodOption)
                                && response.getStatus() == HttpServletResponse.SC_NOT_FOUND) {
                            errorMsg += "Not found data! ";
                        } else if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED) {
                            errorMsg += "Unauthenticated error! ";
                        } else if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {
                            errorMsg += "Request forbidden! ";
                        } else if (response.getStatus() == HttpServletResponse.SC_BAD_GATEWAY
                                || response.getStatus() == HttpServletResponse.SC_SERVICE_UNAVAILABLE
                                || response.getStatus() == HttpServletResponse.SC_GATEWAY_TIMEOUT) {
                            errorMsg += "Request forbidden! ";
                        } else {
                            errorMsg += Globals.DEFAULT_VALUE_STRING;
                        }
                        this.logger.debug("Response code: {}, error message: {}", response.getStatus(), errorMsg);
                    }
                    throw new ServiceException(errorMsg);
                }
            } catch (Exception e) {
                if (e instanceof ServiceException) {
                    throw e;
                }
                throw new ServiceException(e);
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }
    }

    private static final class BeanParameter {

        /**
         * The Form parameters.
         */
        final Map<String, String> formParameters = new HashMap<>();
        /**
         * The Query parameters.
         */
        final Map<String, String> queryParameters = new HashMap<>();
        /**
         * The Parameters.
         */
        final Map<String, String[]> matrixParameters = new HashMap<>();
        /**
         * The Headers.
         */
        final Map<String, String> headers = new HashMap<>();
        /**
         * The Paths.
         */
        final Map<String, String> paths = new HashMap<>();

        /**
         * Instantiates a new Bean parameter.
         *
         * @param beanObject the bean object
         */
        BeanParameter(final Object beanObject, final String[] mediaTypes) {
            ReflectionUtils.getAllDeclaredFields(beanObject.getClass()).forEach(field -> {
                Object fieldValue = ReflectionUtils.getFieldValue(field, beanObject);
                if (field.isAnnotationPresent(BeanParam.class)) {
                    BeanParameter beanParameter = new BeanParameter(fieldValue, mediaTypes);
                    this.formParameters.putAll(beanParameter.getFormParameters());
                    this.queryParameters.putAll(beanParameter.getQueryParameters());
                    this.matrixParameters.putAll(beanParameter.getMatrixParameters());
                    this.headers.putAll(beanParameter.getHeaders());
                    this.paths.putAll(beanParameter.getPaths());
                } else {
                    String stringValue = initConverter(fieldValue.getClass())
                            .map(parameterConverter -> parameterConverter.toString(fieldValue, mediaTypes))
                            .orElse(fieldValue.toString());

                    if (field.isAnnotationPresent(QueryParam.class)) {
                        this.queryParameters.put(field.getAnnotation(QueryParam.class).value(), stringValue);
                    } else if (field.isAnnotationPresent(FormParam.class)) {
                        this.formParameters.put(field.getAnnotation(FormParam.class).value(), stringValue);
                    } else if (field.isAnnotationPresent(MatrixParam.class)) {
                        String paramName = field.getAnnotation(MatrixParam.class).value();
                        String[] paramValues = this.matrixParameters.getOrDefault(paramName, new String[0]);
                        this.matrixParameters.put(paramName, appendValue(paramValues, stringValue));
                    } else if (field.isAnnotationPresent(HeaderParam.class)) {
                        this.headers.put(field.getAnnotation(HeaderParam.class).value(), stringValue);
                    } else if (field.isAnnotationPresent(PathParam.class)) {
                        this.paths.put(field.getAnnotation(HeaderParam.class).value(), stringValue);
                    }
                }
            });
        }

        /**
         * Gets form parameters.
         *
         * @return the form parameters
         */
        public Map<String, String> getFormParameters() {
            return formParameters;
        }

        /**
         * Gets query parameters.
         *
         * @return the query parameters
         */
        public Map<String, String> getQueryParameters() {
            return queryParameters;
        }

        /**
         * Gets matrix parameters.
         *
         * @return the matrix parameters
         */
        public Map<String, String[]> getMatrixParameters() {
            return matrixParameters;
        }

        /**
         * Gets headers.
         *
         * @return the headers
         */
        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * Gets paths.
         *
         * @return the paths
         */
        public Map<String, String> getPaths() {
            return paths;
        }
    }

    private static <T> T parseToObject(String string, Class<T> targetClass) {
        return StringUtils.stringToObject(string, Globals.DEFAULT_ENCODING, targetClass);
    }

    private static <T> List<T> parseToList(String string, Class<T> targetClass) {
        return StringUtils.stringToList(string, Globals.DEFAULT_ENCODING, targetClass);
    }

    private static String[] appendValue(String[] paramValues, String appendValue) {
        String[] newValues = Arrays.copyOf(paramValues, paramValues.length + 1);
        newValues[paramValues.length] = Objects.requireNonNullElse(appendValue, Globals.DEFAULT_VALUE_STRING);
        return newValues;
    }
}
