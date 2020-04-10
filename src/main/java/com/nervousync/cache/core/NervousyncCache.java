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
package com.nervousync.cache.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervousync.commons.beans.xml.cache.CacheConfig;
import com.nervousync.exceptions.cache.CacheException;
import com.nervousync.cache.provider.AbstractCacheProvider;

/**
 * Nervousync Cache Utils
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jun 26, 2018 $
 */
public final class NervousyncCache {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Current cache provider implement object
	 */
	private final AbstractCacheProvider cacheProvider;
	
	/**
	 * Constructor
	 * @param cacheConfig				Cache server config information
	 * @param providerImplClass			Cache provider implement class
	 * @throws CacheException           Generate instance of provider failed
	 *                                  or provider implement class not extends with AbstractCacheProvider
	 */
	public NervousyncCache(CacheConfig cacheConfig, Class<?> providerImplClass) throws CacheException {
		if (providerImplClass != null && AbstractCacheProvider.class.isAssignableFrom(providerImplClass)) {
			try {
				this.cacheProvider = (AbstractCacheProvider)providerImplClass.newInstance();
				this.cacheProvider.initialize(cacheConfig);
				
				return;
			} catch (InstantiationException |IllegalAccessException e) {
				throw new CacheException(e);
			}
		}
		throw new CacheException("Provider implement class is invalid! ");
	}
	
	/**
	 * Set key-value to cache server by default expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public void set(String key, Object value) {
		this.logInfo(key, value);
		this.cacheProvider.set(key, value);
	}

	/**
	 * Set key-value to cache server and set expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	public void set(String key, Object value, int expire) {
		this.logInfo(key, value);
		this.cacheProvider.set(key, value, expire);
	}

	/**
	 * Add a new key-value to cache server by default expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public void add(String key, Object value) {
		this.logInfo(key, value);
		this.cacheProvider.add(key, value);
	}

	/**
	 * Add a new key-value to cache server and set expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	public void add(String key, Object value, int expire) {
		this.logInfo(key, value);
		this.cacheProvider.add(key, value, expire);
	}

	/**
	 * Replace exists value of given key by given value by default expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public void replace(String key, Object value) {
		this.logInfo(key, value);
		this.cacheProvider.replace(key, value);
	}

	/**
	 * Replace exists value of given key by given value and given expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	public void replace(String key, Object value, int expire) {
		this.logInfo(key, value);
		this.cacheProvider.replace(key, value, expire);
	}

	/**
	 * Set expire time to new given expire value which cache key was given
	 * @param key		Cache key
	 * @param expire	New expire time
	 */
	public void touch(String key, int expire) {
		this.cacheProvider.touch(key, expire);
	}

	/**
	 * Remove cache key-value from cache server
	 * @param key		Cache key
	 */
	public void delete(String key) {
		this.cacheProvider.delete(key);
	}

	/**
	 * Read cache value from cache key which cache key was given
	 * @param key		Cache key
	 * @return			Cache value or null if cache key was not exists or it was expired
	 */
	public Object get(String key) {
		if (key == null) {
			return null;
		}
		return this.cacheProvider.get(key);
	}

	/**
	 * Destroy provider
	 */
	public void destroy() {
		this.cacheProvider.destroy();
	}
	
	private void logInfo(String key, Object value) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Cached key: {}", key);
			this.logger.debug("Cached value: {}", value);
		}
	}
}
