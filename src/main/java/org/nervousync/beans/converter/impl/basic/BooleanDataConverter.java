package org.nervousync.beans.converter.impl.basic;

import org.nervousync.beans.converter.DataConverter;
import org.nervousync.utils.ClassUtils;
import org.nervousync.utils.ReflectionUtils;
import org.nervousync.utils.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BooleanDataConverter extends DataConverter {

	@Override
	public String encode(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof Boolean) {
			return ((Boolean)object).toString();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T decode(String string, Class<T> targetClass) {
		if (StringUtils.isEmpty(string)) {
			return null;
		}
		Boolean boolValue = Boolean.valueOf(string);
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
		return null;
	}
}
