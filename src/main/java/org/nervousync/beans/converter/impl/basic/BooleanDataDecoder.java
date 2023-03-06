package org.nervousync.beans.converter.impl.basic;

import org.nervousync.beans.converter.DataConverter;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BooleanDataDecoder extends DataConverter {

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object object, final Class<T> targetClass) {
		if (object instanceof String) {
			Boolean boolValue = Boolean.valueOf((String) object);
			try {
				if (targetClass.isPrimitive()) {
					String className = targetClass.getName();
					String methodName = className + "Value";
					Method convertMethod = ReflectionUtils.findMethod(ClassUtils.primitiveWrapper(targetClass),
							methodName, new Class[]{});
					if (convertMethod != null) {
						return (T) convertMethod.invoke(boolValue);
					}
				}
				return targetClass.cast(boolValue);
			} catch (IllegalAccessException | InvocationTargetException ignored) {
			}
		}
		return targetClass.cast(object);
	}
}
