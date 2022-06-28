package org.nervousync.cache;

public interface CacheManager {

	boolean register(final String cacheName, final Object cacheConfig);

	CacheClient generateClient(final String cacheName);

	void destroyCache(final String cacheName);
	void destroy();
}
