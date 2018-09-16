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
package com.nervousync.commons.beans.servlet.request;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nervousync.commons.core.Globals;
import com.nervousync.commons.http.header.SimpleHeader;
import com.nervousync.commons.http.proxy.ProxyInfo;
import com.nervousync.enumerations.web.HttpMethodOption;
import com.nervousync.utils.RequestUtils;

/**
 * Request information for sending by com.nervousync.utils.RequestUtils
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Aug 25, 2017 11:04:17 AM $
 */
public final class RequestInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7350946565537957232L;

	/**
	 * Http method define
	 * @see com.nervousync.enumerations.web.HttpMethodOption
	 */
	private HttpMethodOption httpMethodOption = HttpMethodOption.DEFAULT;
	/**
	 * Proxy server configuration
	 */
	private ProxyInfo proxyInfo = null;
	/**
	 * Request URL
	 */
	private final String requestUrl;
	/**
	 * Charset encoding
	 */
	private String charset = Globals.DEFAULT_ENCODING;
	/**
	 * Request timeout
	 */
	private int timeOut = Globals.DEFAULT_VALUE_INT;
	/**
	 * Reading position of begin
	 */
	private int beginPosition = Globals.DEFAULT_VALUE_INT;
	/**
	 * Reading position of end
	 */
	private int endPosition = Globals.DEFAULT_VALUE_INT;
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
	private final Map<String, File> uploadParam;
	
	/**
	 * General constructor
	 * @param requestUrl	Target request URL
	 */
	public RequestInfo(String requestUrl) {
		this.requestUrl = requestUrl;
		this.headers = new ArrayList<>();
		this.parameters = new HashMap<>();
		this.uploadParam = new HashMap<>();
	}
	
	/**
	 * Redirect request constructor, maybe using when receive response code of 301/302
	 * @param requestUrl		Redirect URL
	 * @param originalInfo		Original request information
	 */
	public RequestInfo(String requestUrl, RequestInfo originalInfo) {
		this.requestUrl = requestUrl;
		this.httpMethodOption = originalInfo.getHttpMethodOption();
		this.timeOut = originalInfo.getTimeOut();
		this.beginPosition = originalInfo.getBeginPosition();
		this.endPosition = originalInfo.getEndPosition();
		this.headers = originalInfo.getHeaders();
		this.parameters = originalInfo.getParameters();
		this.uploadParam = originalInfo.getUploadParam();
	}

	/**
	 * Constructor for given http method, request URL and send data
	 * @param httpMethodOption		Request http method
	 * @param requestUrl			Target request url
	 * @param data					Send data
	 */
	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, String data) {
		this.httpMethodOption = httpMethodOption;
		this.requestUrl = requestUrl;
		this.parameters = RequestUtils.getRequestParametersFromString(data);
		this.headers = new ArrayList<>();
		this.uploadParam = new HashMap<>();
	}

	/**
	 * Constructor for given http method, request URL and send parameters data
	 * @param httpMethodOption		Request http method
	 * @param requestUrl			Target request url
	 * @param parameters			Send parameters data
	 */
	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, Map<String, String[]> parameters) {
		this.httpMethodOption = httpMethodOption;
		this.requestUrl = requestUrl;
		this.parameters = parameters != null ? parameters : new HashMap<String, String[]>();
		this.headers = new ArrayList<>();
		this.uploadParam = new HashMap<>();
	}
	
	/**
	 * Constructor for given http method, request URL, and many options
	 * @param httpMethodOption		Request http method
	 * @param requestUrl			Target request url
	 * @param timeOut				Request timeout setting
	 * @param beginPosition			Reading position of begin
	 * @param endPosition			Reading position of end
	 * @param headers				Send header information of request
	 * @param parameters			Send parameter information of request
	 * @param uploadParam			Send multipart files of request
	 */
	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, 
			int timeOut, int beginPosition, int endPosition, List<SimpleHeader> headers, 
			Map<String, String[]> parameters, Map<String, File> uploadParam) {
		this.httpMethodOption = httpMethodOption;
		this.timeOut = timeOut;
		this.beginPosition = beginPosition;
		this.endPosition = endPosition;
		this.headers = headers != null ? headers : new ArrayList<SimpleHeader>();
		this.parameters = parameters != null ? parameters : new HashMap<String, String[]>();
		this.uploadParam = uploadParam != null ? uploadParam : new HashMap<String, File>();
		if (requestUrl.contains("?")) {
			this.parameters.putAll(RequestUtils.getRequestParametersFromString(requestUrl.substring(requestUrl.indexOf("?") + 1)));
			requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
		}
		this.requestUrl = requestUrl;
	}
	
	/**
	 * Constructor for given http method, request URL, and many options
	 * @param httpMethodOption		Request http method
	 * @param requestUrl			Target request url
	 * @param charset				Charset encoding
	 * @param timeOut				Request timeout setting
	 * @param beginPosition			Reading position of begin
	 * @param endPosition			Reading position of end
	 * @param headers				Send header information of request
	 * @param parameters			Send parameter information of request
	 * @param uploadParam			Send multipart files of request
	 */
	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, String charset, 
			int timeOut, int beginPosition, int endPosition, List<SimpleHeader> headers, 
			Map<String, String[]> parameters, Map<String, File> uploadParam) {
		this.httpMethodOption = httpMethodOption;
		this.charset = charset;
		this.timeOut = timeOut;
		this.beginPosition = beginPosition;
		this.endPosition = endPosition;
		this.headers = headers != null ? headers : new ArrayList<SimpleHeader>();
		this.parameters = parameters != null ? parameters : new HashMap<String, String[]>();
		this.uploadParam = uploadParam != null ? uploadParam : new HashMap<String, File>();
		if (requestUrl.contains("?")) {
			this.parameters.putAll(RequestUtils.getRequestParametersFromString(requestUrl.substring(requestUrl.indexOf("?") + 1)));
			requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
		}
		this.requestUrl = requestUrl;
	}

	/**
	 * @return the httpMethodOption
	 */
	public HttpMethodOption getHttpMethodOption() {
		if (HttpMethodOption.DEFAULT.equals(this.httpMethodOption)) {
			if (!this.uploadParam.isEmpty()) {
				this.httpMethodOption = HttpMethodOption.POST;
			} else {
				if (RequestUtils.appendParams(this.requestUrl, this.parameters).length() > 1024) {
					this.httpMethodOption = HttpMethodOption.POST;
				} else {
					this.httpMethodOption = HttpMethodOption.GET;
				}
			}
		}
		return httpMethodOption;
	}

	/**
	 * @return the proxyInfo
	 */
	public ProxyInfo getProxyInfo() {
		return proxyInfo;
	}

	/**
	 * @param proxyInfo the proxyInfo to set
	 */
	public void setProxyInfo(ProxyInfo proxyInfo) {
		this.proxyInfo = proxyInfo;
	}

	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the timeOut
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * @return the beginPosition
	 */
	public int getBeginPosition() {
		return beginPosition;
	}

	/**
	 * @return the endPosition
	 */
	public int getEndPosition() {
		return endPosition;
	}

	/**
	 * @return the headers
	 */
	public List<SimpleHeader> getHeaders() {
		return headers;
	}

	/**
	 * @return the parameters
	 */
	public Map<String, String[]> getParameters() {
		return parameters;
	}

	/**
	 * @return the uploadParam
	 */
	public Map<String, File> getUploadParam() {
		return uploadParam;
	}
}
