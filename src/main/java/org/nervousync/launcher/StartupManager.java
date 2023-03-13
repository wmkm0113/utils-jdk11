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
package org.nervousync.launcher;

import org.nervousync.annotations.launcher.Launcher;

import java.util.*;

public final class StartupManager {

	private static StartupManager INSTANCE = null;

	private final String basePath;
	private final SortedMap<Integer, SortedMap<String, StartupLauncher>> launcherMap;

	private StartupManager(final String basePath) {
		this.basePath = basePath;
		this.launcherMap = new TreeMap<>(Comparator.reverseOrder());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (StartupManager.INSTANCE != null) {
				StartupManager.INSTANCE.destroy();
				StartupManager.INSTANCE = null;
			}
		}));
	}

	public static void startup(final String basePath) {
		if (StartupManager.INSTANCE == null) {
			StartupManager.INSTANCE = new StartupManager(basePath);
		}
		StartupManager.INSTANCE.startup();
	}

	void startup() {
		ServiceLoader.load(StartupLauncher.class)
				.forEach(startupLauncher -> {
					if (startupLauncher.getClass().isAnnotationPresent(Launcher.class)) {
						int sortCode = startupLauncher.getClass().getAnnotation(Launcher.class).value();
						SortedMap<String, StartupLauncher> sortedSet =
								this.launcherMap.getOrDefault(sortCode, new TreeMap<>());
						sortedSet.put(startupLauncher.getClass().getName(), startupLauncher);
						this.launcherMap.put(sortCode, sortedSet);
					}
				});
		this.launcherMap.values().forEach(startupLaunchers ->
				startupLaunchers.values().forEach(startupLauncher -> startupLauncher.initialize(this.basePath)));
	}

	void destroy() {
		this.launcherMap.values().forEach(sortedSet -> sortedSet.values().forEach(StartupLauncher::destroy));
		this.launcherMap.clear();
	}
}
