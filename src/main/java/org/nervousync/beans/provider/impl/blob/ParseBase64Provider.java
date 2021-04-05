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

package org.nervousync.beans.provider.impl.blob;

import org.nervousync.beans.provider.ConvertProvider;
import org.nervousync.utils.StringUtils;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 8/25/2020 2:56 PM $
 */
public final class ParseBase64Provider implements ConvertProvider {

	@Override
	public boolean checkType(Class<?> dataType) {
		return String.class.equals(dataType);
	}

	@Override
	public <T> T convert(Object origObj, Class<T> targetClass) {
		Object object = StringUtils.base64Decode((String)origObj);
		if (targetClass.isInstance(object)) {
			return targetClass.cast(object);
		}
		return null;
	}
}
