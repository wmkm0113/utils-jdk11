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
package org.nervousync.utils;

import org.nervousync.cache.annotation.CacheProviderImpl;
import org.nervousync.cache.provider.CacheProvider;
import org.nervousync.commons.beans.xml.cache.CacheConfig;
import org.nervousync.cache.core.CacheInstance;
import org.nervousync.exceptions.cache.CacheException;
import org.nervousync.commons.core.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 11/21/2019 11:20 AM $
 */
public final class CacheUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheUtils.class);

	//  Single Instance Mode
	private static volatile CacheUtils INSTANCE = null;

	//  Registered cache provider implements
	private static final Hashtable<String, Class<?>> REGISTERED_PROVIDERS = new Hashtable<>();

	//  Registered cache instance
	private Hashtable<String, CacheInstance> registeredCache;

	static {
		ServiceLoader.load(CacheProvider.class).forEach(cacheProvider -> {
			if (cacheProvider.getClass().isAnnotationPresent(CacheProviderImpl.class)) {
				registerProvider(cacheProvider.getClass());
			}
		});
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Registered cache provider number: {}", REGISTERED_PROVIDERS.size());
		}
	}

	/**
	 * Constructor
	 */
	private CacheUtils() {
		this.registeredCache = new Hashtable<>();
	}

	/**
	 * Retrieve cache utils instance
	 * <p>
	 *     If CacheUtils not initialized, this method will initialize first
	 * </p>
	 * @return  initialized instance of cache utils
	 */
	public static CacheUtils getInstance() {
		if (CacheUtils.INSTANCE == null) {
			synchronized (CacheUtils.class) {
				if (CacheUtils.INSTANCE == null) {
					CacheUtils.INSTANCE = new CacheUtils();
				}
			}
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
	public static List<String> registeredProviderNames() {
		return new ArrayList<>(REGISTERED_PROVIDERS.keySet());
	}

	/**
	 * Register cache provider
	 * @param providerClass     cache provider class
	 */
	public static void registerProvider(Class<?> providerClass) {
		if (providerClass != null && providerClass.isAnnotationPresent(CacheProviderImpl.class)) {
			CacheProviderImpl cacheProviderImpl = providerClass.getAnnotation(CacheProviderImpl.class);
			REGISTERED_PROVIDERS.put(cacheProviderImpl.name(), providerClass);
		}
	}

	/**
	 * Remove registered cache provider
	 * @param providerName      cache provider name
	 */
	public static void removeProvider(String providerName) {
		REGISTERED_PROVIDERS.remove(providerName);
	}

	/**
	 * Initialize cache by given configure
	 * @param cacheName         cache name
	 * @param cacheConfig       cache config
	 */
	public void registerCache(String cacheName, CacheConfig cacheConfig) {
		if (cacheName == null || cacheConfig == null
				|| !REGISTERED_PROVIDERS.containsKey(cacheConfig.getProviderName())) {
			return;
		}
		if (this.registeredCache.containsKey(cacheName)) {
			LOGGER.warn("Override cache config, cache name: {}", cacheName);
		}

		try {
			this.registeredCache.put(cacheName,
					new CacheInstance(cacheConfig, REGISTERED_PROVIDERS.get(cacheConfig.getProviderName())));
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
	 * Set key-value to cache server by default expiry time
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
	 * Set key-value to cache server and set expiry time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expiry	Expire time
	 */
	public void set(String cacheName, String key, String value, int expiry) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).set(key, value, expiry);
		}
	}

	/**
	 * Add a new key-value to cache server by default expiry time
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
	 * Add a new key-value to cache server and set expiry time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expiry	Expire time
	 */
	public void add(String cacheName, String key, String value, int expiry) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).add(key, value, expiry);
		}
	}

	/**
	 * Replace exists value of given key by given value by default expiry time
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
	 * Replace exists value of given key by given value and given expiry time
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expiry	Expire time
	 */
	public void replace(String cacheName, String key, String value, int expiry) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).replace(key, value, expiry);
		}
	}

	/**
	 * Set expiry time to new given expiry value which cache key was given
	 * @param cacheName Cache name
	 * @param key		Cache key
	 * @param expiry	New expiry time
	 */
	public void expire(String cacheName, String key, int expiry) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).expire(key, expiry);
		}
	}

	/**
	 * Operate touch to given keys
	 * @param cacheName Cache name
	 * @param keys      Keys
	 */
	public void touch(String cacheName, String... keys) {
		if (this.registeredCache.containsKey(cacheName)) {
			this.registeredCache.get(cacheName).touch(keys);
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
	public String get(String cacheName, String key) {
		if (key != null && this.registeredCache.containsKey(cacheName)) {
			return this.registeredCache.get(cacheName).get(key);
		}
		return null;
	}

	/**
	 * Destroy all registered cache instance
	 */
	public void destroy() {
		this.registeredCache.values().forEach(CacheInstance::destroy);
		this.registeredCache = null;
		CacheUtils.INSTANCE = null;
	}

	private Object readResolve() {
		return CacheUtils.INSTANCE;
	}
}
