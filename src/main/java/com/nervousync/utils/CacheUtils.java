/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nervousync.utils;

import com.nervousync.cache.annotation.CacheProvider;
import com.nervousync.commons.beans.xml.cache.CacheConfig;
import com.nervousync.cache.core.NervousyncCache;
import com.nervousync.exceptions.cache.CacheException;
import com.nervousync.cache.provider.AbstractCacheProvider;
import com.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 11/21/2019 11:20 AM $
 */
public final class CacheUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtils.class);

	private static CacheUtils INSTANCE = null;

	private final Hashtable<String, Class<? extends AbstractCacheProvider>> registeredProviders;

	private Hashtable<String, NervousyncCache> registeredCache;

	private CacheUtils() {
		this.registeredProviders = new Hashtable<>();
		this.registeredCache = new Hashtable<>();
	}

	public static CacheUtils getInstance() {
		if (CacheUtils.INSTANCE == null) {
			CacheUtils.INSTANCE = new CacheUtils();
		}
		return CacheUtils.INSTANCE;
	}

	/**
	 * Check given cache name was initialized
	 * @param cacheName     cache name
	 * @return              initialize result
	 */
	public static boolean isInitialized(String cacheName) {
		return CacheUtils.INSTANCE == null ? Globals.DEFAULT_VALUE_BOOLEAN
				: CacheUtils.INSTANCE.registeredCache.containsKey(cacheName);
	}

	/**
	 * Read registered provider name list
	 * @return      provider name list
	 */
	public List<String> registeredProviderNames() {
		return new ArrayList<>(this.registeredProviders.keySet());
	}

	/**
	 * Register cache provider
	 * @param providerClass     cache provider class
	 * @return                  provider name
	 */
	public Optional<String> registerProvider(Class<? extends AbstractCacheProvider> providerClass) {
		String providerName = null;
		if (providerClass != null && providerClass.isAnnotationPresent(CacheProvider.class)) {
			CacheProvider cacheProvider = providerClass.getAnnotation(CacheProvider.class);
			providerName = cacheProvider.name();
			this.registeredProviders.put(providerName, providerClass);
		}
		return Optional.ofNullable(providerName);
	}

	/**
	 * Remove registered cache provider
	 * @param providerName      cache provider name
	 */
	public void removeProvider(String providerName) {
		this.registeredProviders.remove(providerName);
	}

	/**
	 * Initialize cache by given configure
	 * @param cacheName         cache name
	 * @param cacheConfig       cache config
	 */
	public void registerCache(String cacheName, CacheConfig cacheConfig) {
		if (cacheName == null || cacheConfig == null
				|| !this.registeredProviders.containsKey(cacheConfig.getProviderName())) {
			return;
		}
		if (this.registeredCache.containsKey(cacheName)) {
			LOGGER.warn("Override cache config, cache name: {}", cacheName);
		}

		try {
			this.registeredCache.put(cacheName,
					new NervousyncCache(cacheConfig, this.registeredProviders.get(cacheConfig.getProviderName())));
		} catch (CacheException e) {
			LOGGER.error("Generate nervousync cache instance error! ");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Stack message: ", e);
			}
		}
	}

	/**
	 * Destroy cache instance
	 * @param cacheName     cache name
	 */
	public void destroyCache(String cacheName) {
		this.registeredCache.remove(cacheName);
	}

	/**
	 * Set key-value to cache server by default expire time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public void set(String cacheName, String key, String value) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).set(key, value);
		}
	}

	/**
	 * Set key-value to cache server and set expire time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	public void set(String cacheName, String key, String value, int expire) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).set(key, value, expire);
		}
	}

	/**
	 * Add a new key-value to cache server by default expire time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public void add(String cacheName, String key, String value) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).add(key, value);
		}
	}

	/**
	 * Add a new key-value to cache server and set expire time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	public void add(String cacheName, String key, String value, int expire) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).add(key, value, expire);
		}
	}

	/**
	 * Replace exists value of given key by given value by default expire time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public void replace(String cacheName, String key, String value) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).replace(key, value);
		}
	}

	/**
	 * Replace exists value of given key by given value and given expire time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	public void replace(String cacheName, String key, String value, int expire) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).replace(key, value, expire);
		}
	}

	/**
	 * Set expire time to new given expire value which cache key was given
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param expire	New expire time
	 */
	public void touch(String cacheName, String key, int expire) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).touch(key, expire);
		}
	}

	/**
	 * Remove cache key-value from cache server
	 * @param cacheName Cache name
	 * @param key		Cache key
	 */
	public void delete(String cacheName, String key) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).delete(key);
		}
	}

	/**
	 * Read cache value from cache key which cache key was given
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @return			Cache value or null if cache key was not exists or it was expired
	 */
	public Object get(String cacheName, String key) {
		if (key != null && this.registeredCache.containsKey(cacheName)) {
			return this.registeredCache.get(cacheName).get(key);
		}
		return null;
	}

	/**
	 * Destroy all registered cache instance
	 */
	public void destroy() {
		this.registeredCache.values().forEach(NervousyncCache::destroy);
		this.registeredCache = null;
		CacheUtils.INSTANCE = null;
	}
}
