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
package org.nervousync.beans.converter.provider.impl.basic;

import org.nervousync.beans.converter.provider.ConvertProvider;
import org.nervousync.enumerations.xml.DataType;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 8/29/2020 10:55 PM $
 */
public final class ParseNumberProvider implements ConvertProvider {

	public ParseNumberProvider() {
	}

	@Override
	public boolean checkType(Class<?> dataType) {
		DataType currentType = ObjectUtils.retrieveSimpleDataType(dataType);
		return DataType.NUMBER.equals(currentType) || DataType.STRING.equals(currentType)
				|| BigInteger.class.equals(dataType) || BigDecimal.class.equals(dataType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(Object origObj, Class<T> targetClass) {
		if (origObj != null && DataType.NUMBER.equals(ObjectUtils.retrieveSimpleDataType(targetClass))) {
			if (targetClass.equals(BigInteger.class)) {
				return targetClass.cast(new BigInteger(origObj.toString()));
			}
			if (targetClass.equals(BigDecimal.class)) {
				return targetClass.cast(new BigDecimal(origObj.toString()));
			}
			String string = origObj.toString();
			if (targetClass.equals(Integer.class) || targetClass.equals(int.class)
					|| targetClass.equals(Short.class) || targetClass.equals(short.class)
					|| targetClass.equals(Long.class) || targetClass.equals(long.class)) {
				if (string.contains(".")) {
					string = string.substring(0, string.indexOf("."));
				}
			}

			Method method = ReflectionUtils.findMethod(targetClass, "valueOf", new Class[]{String.class});
			if (method != null) {
				try {
					Object object = method.invoke(null, string);
					if (targetClass.isPrimitive()) {
						String className = targetClass.getName();
						String methodName = className + "Value";
						Method convertMethod = ReflectionUtils.findMethod(ClassUtils.primitiveWrapper(targetClass),
								methodName, new Class[]{});
						if (convertMethod != null) {
							return (T) convertMethod.invoke(object, new Object[0]);
						}
					} else {
						return targetClass.cast(object);
					}
				} catch (IllegalAccessException | InvocationTargetException ignored) {
				}
			}
		}
		return null;
	}
}
