package org.nervousync.beans.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataConverter {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public abstract String encode(Object object);

	public abstract <T> T decode(String string, Class<T> targetClass);

}
