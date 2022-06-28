package org.nervousync.launcher.core;

import org.nervousync.commons.core.Globals;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractStartupLauncher {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected String parsePath(final String basePath) {
		if (StringUtils.isEmpty(basePath)) {
			return Globals.DEFAULT_VALUE_STRING;
		}
		return basePath.endsWith(Globals.DEFAULT_PAGE_SEPARATOR)
				? basePath.substring(0, basePath.length() - 1)
				: basePath;
	}
}
