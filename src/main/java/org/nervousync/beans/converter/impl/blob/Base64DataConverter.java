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

package org.nervousync.beans.converter.impl.blob;

import org.nervousync.beans.converter.DataConverter;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.StringUtils;

/**
 * The type Parse base 64 provider.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 8/25/2020 2:56 PM $
 */
public final class Base64DataConverter extends DataConverter {

	@Override
	public String encode(Object object) {
		return StringUtils.base64Encode(ConvertUtils.convertToByteArray(object));
	}

	@Override
	public <T> T decode(final String string, Class<T> targetClass) {
		byte[] object = StringUtils.base64Decode(string);
		if (targetClass.isInstance(object)) {
			return targetClass.cast(object);
		}
		return null;
	}
}
