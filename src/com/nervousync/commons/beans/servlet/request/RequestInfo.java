/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervous Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervous Studio.
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
import com.nervousync.enumeration.web.HttpMethodOption;
import com.nervousync.utils.RequestUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Aug 25, 2017 11:04:17 AM $
 */
public final class RequestInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7350946565537957232L;

	private HttpMethodOption httpMethodOption = HttpMethodOption.DEFAULT;
	private ProxyInfo proxyInfo = null;
	private String requestUrl = null;
	private String charset = Globals.DEFAULT_ENCODING;
	private int timeOut = Globals.DEFAULT_VALUE_INT;
	private int beginPosition = Globals.DEFAULT_VALUE_INT;
	private int endPosition = Globals.DEFAULT_VALUE_INT;
	private List<SimpleHeader> headers = null;
	private Map<String, String[]> parameters = null;
	private Map<String, File> uploadParam = null;
	
	public RequestInfo(String requestUrl) {
		this.requestUrl = requestUrl;
		this.headers = new ArrayList<SimpleHeader>();
		this.parameters = new HashMap<String, String[]>();
		this.uploadParam = new HashMap<String, File>();
	}
	
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

	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, String data) {
		this.httpMethodOption = httpMethodOption;
		this.requestUrl = requestUrl;
		this.parameters = RequestUtils.getRequestParametersFromString(data);
		this.headers = new ArrayList<SimpleHeader>();
		this.uploadParam = new HashMap<String, File>();
	}
	
	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, Map<String, String[]> parameters) {
		this.httpMethodOption = httpMethodOption;
		this.requestUrl = requestUrl;
		this.parameters = parameters != null ? parameters : new HashMap<String, String[]>();
		this.headers = new ArrayList<SimpleHeader>();
		this.uploadParam = new HashMap<String, File>();
	}
	
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
		if (requestUrl.indexOf("?") != Globals.DEFAULT_VALUE_INT) {
			this.parameters.putAll(RequestUtils.getRequestParametersFromString(requestUrl.substring(requestUrl.indexOf("?") + 1)));
			requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
		}
		this.requestUrl = requestUrl;
	}
	
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
		if (requestUrl.indexOf("?") != Globals.DEFAULT_VALUE_INT) {
			this.parameters.putAll(RequestUtils.getRequestParametersFromString(requestUrl.substring(requestUrl.indexOf("?") + 1)));
			requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
		}
		this.requestUrl = requestUrl;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
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
