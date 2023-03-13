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
import org.nervousync.utils.StringUtils;

public class Base32Decoder extends DataConverter {

	@Override
	public <T> T convert(final Object object, Class<T> targetClass) {
		if (object instanceof String) {
			byte[] byteArray = StringUtils.base32Decode((String) object);
			if (targetClass.isInstance(byteArray)) {
				return targetClass.cast(byteArray);
			}
		}
		return null;
	}
}
