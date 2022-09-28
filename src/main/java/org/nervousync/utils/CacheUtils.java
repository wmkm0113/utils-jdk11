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
package org.nervousync.utils;

import org.nervousync.cache.CacheClient;
import org.nervousync.cache.CacheManager;

import java.util.*;

public final class CacheUtils {

	private final static CacheManager CACHE_MANAGER;

	static {
		CACHE_MANAGER = ServiceLoader.load(CacheManager.class).findFirst().orElse(null);
	}

	private CacheUtils() {
	}

	public static boolean register(final String cacheName, final Object cacheConfig) {
		return Optional.ofNullable(CACHE_MANAGER)
				.map(cacheManager -> cacheManager.register(cacheName, cacheConfig))
				.orElse(Boolean.FALSE);
	}

	public static CacheClient cacheClient(final String cacheName) {
		return Optional.ofNullable(CACHE_MANAGER)
				.map(cacheManager -> cacheManager.generateClient(cacheName))
				.orElse(null);
	}

	public static void destroyCache(final String cacheName) {
		Optional.ofNullable(CACHE_MANAGER).ifPresent(cacheManager -> cacheManager.destroyCache(cacheName));
	}

	public static void destroy() {
		Optional.ofNullable(CACHE_MANAGER).ifPresent(CacheManager::destroy);
	}
}
