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
package org.nervousync.cache.provider.impl;

import java.util.List;

import org.nervousync.cache.annotation.CacheProviderImpl;
import org.nervousync.cache.provider.CacheProvider;
import org.nervousync.exceptions.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.nervousync.commons.beans.xml.cache.CacheConfig;
import org.nervousync.commons.beans.xml.cache.CacheServer;
import org.nervousync.commons.core.Globals;

/**
 * All providers must extends this class
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Apr 25, 2017 3:01:30 PM $
 */
public abstract class AbstractCacheProvider implements CacheProvider {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Default port number
	 */
	private final int defaultPort;
	
	/**
	 * Connect timeout
	 */
	private int connectTimeout = Globals.DEFAULT_VALUE_INT;
	/**
	 * Server timeout
	 */
	private int serverTimeout = Globals.DEFAULT_VALUE_INT;
	/**
	 * Client pool size
	 */
	private int clientPoolSize = Globals.DEFAULT_VALUE_INT;
	/**
	 * Maximum client count
	 */
	private int maximumClient = Globals.DEFAULT_VALUE_INT;
	/**
	 * Default expire time
	 */
	private int expireTime = Globals.DEFAULT_VALUE_INT;
	
	/**
	 * Default constructor
	 */
	public AbstractCacheProvider() throws CacheException {
		if (this.getClass().isAnnotationPresent(CacheProviderImpl.class)) {
			this.defaultPort = this.getClass().getAnnotation(CacheProviderImpl.class).defaultPort();
		} else {
			throw new CacheException("Provider implement class must annotation with " + CacheProviderImpl.class.getName());
		}
	}
	
	/**
	 * Initialize provider using given cache configure information
	 * @param cacheConfig		Cache server configure information
	 */
	public void initialize(CacheConfig cacheConfig) {
		this.connectTimeout = cacheConfig.getConnectTimeout();
		this.serverTimeout = cacheConfig.getServerTimeout();
		this.clientPoolSize = cacheConfig.getClientPoolSize();
		this.maximumClient = cacheConfig.getMaximumClient();
		this.expireTime = cacheConfig.getExpireTime();
		this.initializeConnection(cacheConfig.getServerConfigList());
	}

	/**
	 * Gets the value of defaultPort.
	 *
	 * @return the value of defaultPort
	 */
	public int getDefaultPort() {
		return defaultPort;
	}

	/**
	 * Gets the value of connectTimeout.
	 *
	 * @return the value of connectTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * Gets the value of serverTimeout.
	 *
	 * @return the value of serverTimeout
	 */
	public int getServerTimeout() {
		return serverTimeout;
	}

	/**
	 * Gets the value of clientPoolSize.
	 *
	 * @return the value of clientPoolSize
	 */
	public int getClientPoolSize() {
		return clientPoolSize;
	}

	/**
	 * Gets the value of maximumClient.
	 *
	 * @return the value of maximumClient
	 */
	public int getMaximumClient() {
		return maximumClient;
	}

	/**
	 * Gets the value of expireTime.
	 *
	 * @return the value of expireTime
	 */
	public int getExpireTime() {
		return expireTime;
	}

	/**
	 * Initialize server connections
	 * @param serverConfigList  cache server list
	 */
	protected abstract void initializeConnection(List<CacheServer> serverConfigList);
	
	/**
	 * Set key-value to cache server by default expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public final void set(String key, String value) {
		this.set(key, value, this.expireTime);
	}

	/**
	 * Add a new key-value to cache server by default expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public final void add(String key, String value) {
		this.add(key, value, this.expireTime);
	}

	/**
	 * Replace exists value of given key by given value by default expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 */
	public final void replace(String key, String value) {
		this.replace(key, value, this.expireTime);
	}

	/**
	 * Set expire time to new given expire value which cache key was given
	 * @param key		Cache key
	 * @param expiry	New expire time
	 */
	public abstract void expire(String key, int expiry);

	/**
	 * Set key-value to cache server and set expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expiry	Expire time
	 */
	public abstract void set(String key, String value, int expiry);

	/**
	 * Add a new key-value to cache server and set expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	public abstract void add(String key, String value, int expire);

	/**
	 * Replace exists value of given key by given value and given expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expiry	Expire time
	 */
	public abstract void replace(String key, String value, int expiry);

	/**
	 * Operate touch to given keys
	 * @param keys      Keys
	 */
	public abstract void touch(String... keys);
	
	/**
	 * Remove cache key-value from cache server
	 * @param key		Cache key
	 */
	public abstract void delete(String key);
	
	/**
	 * Read cache value from cache key which cache key was given
	 * @param key		Cache key
	 * @return			Cache value or null if cache key was not exists or it was expired
	 */
	public abstract String get(String key);

	/**
	 * Incr operate
	 * @param key		Cache key
	 * @param step      Step value
	 * @return          Result value
	 */
	public abstract long incr(String key, long step);

	/**
	 * Decr operate
	 * @param key		Cache key
	 * @param step      Step value
	 * @return          Result value
	 */
	public abstract long decr(String key, long step);

	/**
	 * Destroy provider
	 */
	public abstract void destroy();
}
