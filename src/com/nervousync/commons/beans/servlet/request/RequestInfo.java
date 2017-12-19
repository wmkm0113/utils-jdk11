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

import org.apache.http.Header;

import com.nervousync.commons.core.Globals;
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
	private String requestUrl = null;
	private int timeOut = Globals.DEFAULT_VALUE_INT;
	private int beginPosition = Globals.DEFAULT_VALUE_INT;
	private int endPosition = Globals.DEFAULT_VALUE_INT;
	private List<Header> headers = null;
	private Map<String, String[]> parameters = null;
	private Map<String, File> uploadParam = null;
	
	public RequestInfo(String requestUrl) {
		this.requestUrl = requestUrl;
		this.headers = new ArrayList<Header>();
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
		this.headers = new ArrayList<Header>();
		this.uploadParam = new HashMap<String, File>();
	}
	
	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, Map<String, String[]> parameters) {
		this.httpMethodOption = httpMethodOption;
		this.requestUrl = requestUrl;
		this.parameters = parameters;
		this.headers = new ArrayList<Header>();
		this.uploadParam = new HashMap<String, File>();
	}
	
	public RequestInfo(HttpMethodOption httpMethodOption, String requestUrl, 
			int timeOut, int beginPosition, int endPosition, List<Header> headers, 
			Map<String, String[]> parameters, Map<String, File> uploadParam) {
		this.httpMethodOption = httpMethodOption;
		this.requestUrl = requestUrl;
		this.timeOut = timeOut;
		this.beginPosition = beginPosition;
		this.endPosition = endPosition;
		this.headers = headers;
		this.parameters = parameters;
		this.uploadParam = uploadParam;
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
		return httpMethodOption;
	}

	/**
	 * @return the requestUrl
	 */
	public String getRequestUrl() {
		return requestUrl;
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
	public List<Header> getHeaders() {
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
