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
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;

import java.util.*;

/**
 * <h2 class="en">Startup Manager</h2>
 * <span class="en">Running in singleton mode</span>
 * <h2 class="zh-CN">启动管理器</h2>
 * <span class="en">使用单例模式运行</span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jun 28, 2022 15:25:33 $
 */
public final class StartupManager {
	/**
 	* <span class="en">Singleton instance of StartupManager</span>
 	* <span class="en">StartupManager的单例对象实例</span>
	 */
	private static StartupManager INSTANCE = null;
	/**
 	* <span class="en">Base path for launcher execute</span>
 	* <span class="en">启动器执行的基本目录</span>
	 */
	private final String basePath;
	/**
 	* <span class="en">Registered startup launcher instance</span>
 	* <span class="en">已注册的启动器实例</span>
	 */
	private final SortedMap<Integer, SortedMap<String, StartupLauncher>> launcherMap;
	/**
	 * <h3 class="en">Private constructor method for StartupManager</h3>
	 * <span class="en">StartupManager will automatic add a hook to schedule execute destroy method when system will shutdown.</span>
	 * <h3 class="zh-CN">启动管理器的私有构造方法</h3>
	 * <span class="zh-CN">启动管理器会自动添加一个钩子程序用于在系统退出时调用destroy方法。</span>
	 *
	 * @param basePath	<span class="en">Base path for launcher execute</span>
	 *                  <span class="zh-CN">启动器执行的基本目录</span>
	 */
	private StartupManager(final String basePath) {
		if (StringUtils.isEmpty(basePath)) {
			this.basePath =  Globals.DEFAULT_VALUE_STRING;
		} else {
			this.basePath =  basePath.endsWith(Globals.DEFAULT_PAGE_SEPARATOR)
					? basePath.substring(0, basePath.length() - 1)
					: basePath;
		}
		this.launcherMap = new TreeMap<>(Comparator.reverseOrder());
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (StartupManager.INSTANCE != null) {
				StartupManager.INSTANCE.destroy();
				StartupManager.INSTANCE = null;
			}
		}));
	}
	/**
	 * <h3 class="en">Static method for initialize StartupManager and execute startup method from all launchers</h3>
	 * <h3 class="zh-CN">静态方法用于初始化启动管理器并执行所有启动器的startup方法</h3>
	 *
	 * @param basePath	<span class="en">Base path for launcher execute</span>
	 *                  <span class="zh-CN">启动器执行的基本目录</span>
	 */
	public static void startup(final String basePath) {
		if (StartupManager.INSTANCE == null) {
			StartupManager.INSTANCE = new StartupManager(basePath);
		}
		StartupManager.INSTANCE.startup();
	}
	/**
	 * <h3 class="en">Load launcher instance and execute startup method</h3>
	 * <h3 class="zh-CN">加载所有启动器并执行startup方法</h3>
	 */
	private void startup() {
		ServiceLoader.load(StartupLauncher.class)
				.stream()
				.filter(provider -> provider.get().getClass().isAnnotationPresent(Launcher.class))
				.forEach(provider -> {
					StartupLauncher startupLauncher = provider.get();
					int sortCode = startupLauncher.getClass().getAnnotation(Launcher.class).value();
					SortedMap<String, StartupLauncher> sortedSet =
							this.launcherMap.getOrDefault(sortCode, new TreeMap<>());
					sortedSet.put(startupLauncher.getClass().getName(), startupLauncher);
					this.launcherMap.put(sortCode, sortedSet);
				});
		this.launcherMap.values().forEach(startupLaunchers ->
				startupLaunchers.values().forEach(startupLauncher -> startupLauncher.startup(this.basePath)));
	}
	/**
	 * <h3 class="en">Destroy all registered launcher instance</h3>
	 * <h3 class="zh-CN">销毁所有已注册的启动器实例</h3>
	 */
	private void destroy() {
		this.launcherMap.values().forEach(sortedSet -> sortedSet.values().forEach(StartupLauncher::destroy));
		this.launcherMap.clear();
	}
}
