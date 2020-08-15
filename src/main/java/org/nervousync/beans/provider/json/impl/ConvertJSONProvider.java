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

package org.nervousync.beans.provider.json.impl;

import org.nervousync.beans.provider.ConvertProvider;
import org.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 8/15/2020 4:41 PM $
 */
public final class ConvertJSONProvider implements ConvertProvider {

	public ConvertJSONProvider() {
	}

	@Override
	public boolean checkType(Class<?> dataType) {
		return Object.class.isAssignableFrom(dataType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(Object origObj, Class<T> targetClass) {
		if (origObj != null && String.class.equals(targetClass)) {
			return (T) StringUtils.convertObjectToJSONString(origObj);
		}
		return null;
	}
}
