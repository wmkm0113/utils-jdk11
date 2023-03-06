package org.nervousync.beans.converter.impl.blob;

import org.nervousync.beans.converter.DataConverter;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.StringUtils;

public final class Base64Encoder extends DataConverter {

    @Override
    public <T> T convert(final Object object, final Class<T> targetClass) {
        if (String.class.equals(targetClass)) {
            return targetClass.cast(StringUtils.base64Encode(ConvertUtils.convertToByteArray(object)));
        }
        return null;
    }
}
