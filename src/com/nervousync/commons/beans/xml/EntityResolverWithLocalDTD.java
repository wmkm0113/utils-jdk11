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
package com.nervousync.commons.beans.xml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Local DTD resolver implement org.xml.sax.EntityResolver
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 2009/09/28 14:32:00 $
 */
public final class EntityResolverWithLocalDTD implements EntityResolver {
	private final String DTDFile;
	
	public EntityResolverWithLocalDTD(String dtdFile) {
		this.DTDFile = dtdFile;
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException {
		InputStream inputStream = new FileInputStream(this.DTDFile);
		InputSource inputSource = new InputSource(inputStream);
		inputSource.setPublicId(publicId);
		inputSource.setSystemId(systemId);
		return inputSource;
	}
}
