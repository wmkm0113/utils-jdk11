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
package org.nervousync.launcher;

import org.nervousync.annotations.launcher.Launcher;

import java.util.*;

public final class StartupManager {

	private StartupManager() {
	}

	public static void startup(final String basePath) {
		SortedMap<Integer, SortedMap<String, StartupLauncher>> launcherMap = new TreeMap<>(Comparator.reverseOrder());
		ServiceLoader.load(StartupLauncher.class)
				.forEach(startupLauncher -> {
					if (startupLauncher.getClass().isAnnotationPresent(Launcher.class)) {
						int sortCode = startupLauncher.getClass().getAnnotation(Launcher.class).value();
						SortedMap<String, StartupLauncher> sortedSet =
								launcherMap.getOrDefault(sortCode, new TreeMap<>());
						sortedSet.put(startupLauncher.getClass().getName(), startupLauncher);
						launcherMap.put(sortCode, sortedSet);
					}
				});
		launcherMap.values().forEach(startupLaunchers ->
				startupLaunchers.values().forEach(startupLauncher -> startupLauncher.initialize(basePath)));
	}
}
