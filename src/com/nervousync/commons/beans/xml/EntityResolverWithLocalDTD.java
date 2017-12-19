/*
 * Copyright © 2003 - 2009 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.beans.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2009/09/28 14:32:00 $
 */
public class EntityResolverWithLocalDTD implements EntityResolver {
	private String DTDFile = null;
	
	public EntityResolverWithLocalDTD( String dtdFile ) {
		this.DTDFile = dtdFile;
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		// TODO Auto-generated method stub
		InputStream inputStream = new FileInputStream(this.DTDFile);
		InputSource inputSource = new InputSource(inputStream);
		inputSource.setPublicId(publicId);
		inputSource.setSystemId(systemId);
		return inputSource;
	}
}
