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

package org.nervousync.commons.beans.json;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.StringUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * The type Json object.
 */
public class JsonObject implements Serializable {

	private static final long serialVersionUID = 4862718779056901888L;

	/**
	 * Parse json t.
	 *
	 * @param <T>       the type parameter
	 * @param string    the string
	 * @param beanClass the bean class
	 * @return the t
	 */
	public static <T> T parseJSON(@Nonnull String string, @Nonnull Class<T> beanClass) {
		return JsonObject.parseJSON(string, Globals.DEFAULT_ENCODING, beanClass);
	}

	/**
	 * Parse json t.
	 *
	 * @param <T>       the type parameter
	 * @param string    the string
	 * @param encoding  the encoding
	 * @param beanClass the bean class
	 * @return the t
	 */
	public static <T> T parseJSON(@Nonnull String string, @Nonnull String encoding,
	                              @Nonnull Class<T> beanClass) {
		Map<String, Object> jsonMap = StringUtils.convertJSONStringToMap(string);
		if (jsonMap.isEmpty()) {
			return null;
		}
		return ConvertUtils.convertMapToObject(jsonMap, beanClass);
	}

	/**
	 * To json string string.
	 *
	 * @return the string
	 */
	public String toJsonString() {
		return StringUtils.convertObjectToJSONString(this);
	}
}
