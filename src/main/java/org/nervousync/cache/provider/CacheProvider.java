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

package org.nervousync.cache.provider;

/**
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: 9/14/2020 10:30 AM $
 */
public interface CacheProvider {

	/**
	 * Set key-value to cache server and set expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expiry	Expire time
	 */
	void set(String key, String value, int expiry);

	/**
	 * Add a new key-value to cache server and set expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expire	Expire time
	 */
	void add(String key, String value, int expire);

	/**
	 * Replace exists value of given key by given value and given expire time
	 * @param key		Cache key
	 * @param value		Cache value
	 * @param expiry	Expire time
	 */
	void replace(String key, String value, int expiry);

	/**
	 * Operate touch to given keys
	 * @param keys      Keys
	 */
	void touch(String... keys);

	/**
	 * Remove cache key-value from cache server
	 * @param key		Cache key
	 */
	void delete(String key);

	/**
	 * Read cache value from cache key which cache key was given
	 * @param key		Cache key
	 * @return			Cache value or null if cache key was not exists or it was expired
	 */
	String get(String key);

	/**
	 * Incr operate
	 * @param key		Cache key
	 * @param step      Step value
	 * @return          Result value
	 */
	long incr(String key, long step);

	/**
	 * Decr operate
	 * @param key		Cache key
	 * @param step      Step value
	 * @return          Result value
	 */
	long decr(String key, long step);

	/**
	 * Destroy provider
	 */
	void destroy();
}
