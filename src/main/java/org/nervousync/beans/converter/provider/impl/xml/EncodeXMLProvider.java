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

package org.nervousync.beans.converter.provider.impl.xml;

import org.nervousync.beans.converter.provider.ConvertProvider;
import org.nervousync.beans.core.BeanObject;

/**
 * The type Encode xml provider.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 8/15/2020 4:34 PM $
 */
public final class EncodeXMLProvider implements ConvertProvider {

	/**
	 * Instantiates a new Encode xml provider.
	 */
	public EncodeXMLProvider() {
	}

	@Override
	public boolean checkType(Class<?> dataType) {
		return BeanObject.class.isAssignableFrom(dataType);
	}

	@Override
	public <T> T convert(Object origObj, Class<T> targetClass) {
		if (origObj != null && BeanObject.class.isAssignableFrom(origObj.getClass())
				&& String.class.equals(targetClass)) {
			return targetClass.cast(origObj.toString());
		}
		return null;
	}
}
