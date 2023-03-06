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
