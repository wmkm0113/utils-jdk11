package org.nervousync.beans.converter.impl.basic;

import org.nervousync.beans.converter.DataConverter;

public final class BooleanDataEncoder extends DataConverter {

	@Override
	public <T> T convert(final Object object, final Class<T> targetClass) {
		if (object instanceof Boolean) {
			return targetClass.cast(object.toString());
		}
		return null;
	}
}
