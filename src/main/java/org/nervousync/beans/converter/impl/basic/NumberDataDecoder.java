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
package org.nervousync.beans.converter.impl.basic;

import org.nervousync.beans.converter.DataConverter;
import org.nervousync.enumerations.xml.DataType;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * The type Parse number provider.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: 8/29/2020 10:55 PM $
 */
public final class NumberDataDecoder extends DataConverter {

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object object, final Class<T> targetClass) {
		if ((object instanceof String) && DataType.NUMBER.equals(ObjectUtils.retrieveSimpleDataType(targetClass))) {
			if (targetClass.equals(BigInteger.class)) {
				return targetClass.cast(new BigInteger((String) object));
			}
			if (targetClass.equals(BigDecimal.class)) {
				return targetClass.cast(new BigDecimal((String) object));
			}
			String stringValue = (String) object;
			if (targetClass.equals(Integer.class) || targetClass.equals(int.class)
					|| targetClass.equals(Short.class) || targetClass.equals(short.class)
					|| targetClass.equals(Long.class) || targetClass.equals(long.class)) {
				if (stringValue.contains(".")) {
					stringValue = stringValue.substring(0, stringValue.indexOf("."));
				}
			}

			Method method = ClassUtils.findMethod(ClassUtils.primitiveWrapper(targetClass),
					"valueOf", new Class[]{String.class});
			if (method != null) {
				try {
					Object targetObject = method.invoke(null, stringValue);
					if (targetClass.isPrimitive()) {
						String className = targetClass.getName();
						String methodName = className + "Value";
						Method convertMethod = ClassUtils.findMethod(ClassUtils.primitiveWrapper(targetClass),
								methodName, new Class[]{});
						if (convertMethod != null) {
							return (T) convertMethod.invoke(targetObject);
						}
					} else {
						return targetClass.cast(targetObject);
					}
				} catch (IllegalAccessException | InvocationTargetException ignored) {
				}
			}
		}
		return null;
	}
}
