/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.servlet.request;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 19, 2015 8:56:50 AM $
 */
public final class RequestAttribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 485757973915022360L;
	
	private String sessionId;
	private Map<String, Object> attributeMap;

	private RequestAttribute(HttpServletRequest request) {
		this.sessionId = request.getSession().getId();
		this.attributeMap = new HashMap<String, Object>();

		Enumeration<String> e = request.getAttributeNames();

		while (e.hasMoreElements()) {
			String name = e.nextElement();
			this.attributeMap.put(name, request.getAttribute(name));
		}
	}
	
	public static RequestAttribute newInstance(HttpServletRequest request) {
		return new RequestAttribute(request);
	}
	
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @return the attributeMap
	 */
	public Map<String, Object> getAttributeMap() {
		return attributeMap;
	}
}
