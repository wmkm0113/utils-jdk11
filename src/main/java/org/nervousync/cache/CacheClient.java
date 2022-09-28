/*
 * Copyright 2022 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.cache;

import org.nervousync.commons.core.Globals;

public interface CacheClient {

	/**
	 * <h3 class="en">Set key-value to cache server, using default expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	default void set(String key, String value) {
		this.set(key, value, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h3 class="en">Set key-value to cache server and set expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间设置缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 * @param expiry	<span class="en">Expire time</span>
	 *                  <span class="zhs">过期时间</span>
	 */
	void set(String key, String value, int expiry);

	/**
	 * <h3 class="en">Add a new key-value to cache server, using default expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间添加缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	default void add(String key, String value) {
		this.add(key, value, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h3 class="en">Add a new key-value to cache server and set expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间添加缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 * @param expire	<span class="en">Expire time</span>
	 *                  <span class="zhs">过期时间</span>
	 */
	void add(String key, String value, int expire);

	/**
	 * <h3 class="en">Replace exists value of given key by given value, using default expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间替换已存在的缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 */
	default void replace(String key, String value) {
		this.replace(key, value, Globals.DEFAULT_VALUE_INT);
	}

	/**
	 * <h3 class="en">Replace exists value of given key by given value and set expire time</h3>
	 * <h3 class="zhs">使用指定的过期时间替换已存在的缓存信息</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param value		<span class="en">Cache value</span>
	 *                  <span class="zhs">缓存数据</span>
	 * @param expire	<span class="en">Expire time</span>
	 *                  <span class="zhs">过期时间</span>
	 */
	void replace(String key, String value, int expire);

	/**
	 * Operate touch to given keys
	 * @param keys      Keys
	 */
	void touch(String... keys);

	/**
	 * <h3 class="en">Remove cache key-value from cache server</h3>
	 * <h3 class="zhs">移除指定的缓存键值</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 */
	void delete(String key);

	/**
	 * <h3 class="en">Read cache value from cache key which cache key was given</h3>
	 * <h3 class="zhs">读取指定缓存键值对应的缓存数据</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @return  <span class="en">Cache value or null if cache key was not exists or it was expired</span>
	 *          <span class="zhs">读取的缓存数据，如果缓存键值不存在或已过期，则返回null</span>
	 */
	String get(String key);

	/**
	 * <h3 class="en">Increment data by given cache key and value</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param step      <span class="en">Increment step value</span>
	 *                  <span class="zhs">自增步进值</span>
	 * @return  <span class="en">Operate result</span>
	 *          <span class="zhs">操作结果</span>
	 */
	long incr(String key, long step);

	/**
	 * <h3 class="en">Decrement data by given cache key and value</h3>
	 *
	 * @param key       <span class="en">Cache key</span>
	 *                  <span class="zhs">缓存键值</span>
	 * @param step      <span class="en">Decrement step value</span>
	 *                  <span class="zhs">自减步进值</span>
	 * @return  <span class="en">Operate result</span>
	 *          <span class="zhs">操作结果</span>
	 */
	long decr(String key, long step);
}
