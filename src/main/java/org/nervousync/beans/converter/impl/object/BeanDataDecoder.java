package org.nervousync.beans.converter.impl.object;

import org.nervousync.beans.converter.DataConverter;
import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;

public final class BeanDataDecoder extends DataConverter {

	@Override
	public <T> T convert(final Object object, Class<T> targetClass) {
		if ((object instanceof String) && targetClass != null && BeanObject.class.isAssignableFrom(targetClass)) {
			return StringUtils.stringToObject((String) object, Globals.DEFAULT_ENCODING, targetClass);
		}
		return null;
	}
}
