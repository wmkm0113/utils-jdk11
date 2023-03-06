package org.nervousync.beans.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataConverter {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract <T> T convert(final Object object, final Class<T> targetClass);

}
