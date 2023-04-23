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
package org.nervousync.restful.converter.core;

import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

/**
 * The type Bean object converter.
 */
public final class BeanObjectConverter implements ParameterConverter {

	@Override
	public boolean match(Class<?> targetClass) {
		return targetClass != null && BeanObject.class.isAssignableFrom(targetClass);
	}

	@Override
	public String toString(Object object, String[] mediaTypes) {
		if (object instanceof BeanObject) {
			for (String mediaType : mediaTypes) {
				switch (mediaType) {
					case FileUtils.MIME_TYPE_JSON:
						return ((BeanObject) object).toJson();
					case FileUtils.MIME_TYPE_TEXT_XML:
					case FileUtils.MIME_TYPE_XML:
						return ((BeanObject) object).toXML();
					case FileUtils.MIME_TYPE_TEXT_YAML:
					case FileUtils.MIME_TYPE_YAML:
						return ((BeanObject) object).toYaml();
				}
			}
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	public Object fromString(Class<?> clazz, String value) {
		if (BeanObject.class.isAssignableFrom(clazz)) {
			return StringUtils.stringToObject(value, clazz);
		}
		return null;
	}
}
