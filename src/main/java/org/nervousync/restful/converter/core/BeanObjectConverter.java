package org.nervousync.restful.converter.core;

import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.core.Globals;
import org.nervousync.restful.converter.ParameterConverter;
import org.nervousync.utils.FileUtils;
import org.nervousync.utils.StringUtils;

/**
 * The type Bean object converter.
 */
public final class BeanObjectConverter implements ParameterConverter {

	@Override
	public boolean match(Class<?> targetClass) {
		return targetClass != null && BeanObject.class.isAssignableFrom(targetClass);
	}

	@Override
	public String toString(Object object, String[] mediaTypes) {
		if (object instanceof BeanObject) {
			for (String mediaType : mediaTypes) {
				switch (mediaType) {
					case FileUtils.MIME_TYPE_JSON:
						return ((BeanObject) object).toJson();
					case FileUtils.MIME_TYPE_TEXT_XML:
					case FileUtils.MIME_TYPE_XML:
						return ((BeanObject) object).toXML();
					case FileUtils.MIME_TYPE_TEXT_YAML:
					case FileUtils.MIME_TYPE_YAML:
						return ((BeanObject) object).toYaml();
				}
			}
		}
		return Globals.DEFAULT_VALUE_STRING;
	}

	@Override
	public Object fromString(Class<?> clazz, String value) {
		if (BeanObject.class.isAssignableFrom(clazz)) {
			return StringUtils.stringToObject(value, Globals.DEFAULT_ENCODING, clazz);
		}
		return null;
	}
}
