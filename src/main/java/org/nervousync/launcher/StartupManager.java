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
