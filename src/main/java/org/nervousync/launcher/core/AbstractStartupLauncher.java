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
package org.nervousync.launcher.core;

import org.nervousync.launcher.StartupLauncher;
import org.nervousync.utils.LoggerUtils;

/**
 * <h2 class="en-US">Abstract class for startup launcher</h2>
 * <span class="en-US">Only add an unified logger instance</span>
 * <h2 class="zh-CN">启动器抽象实现类</h2>
 * <span class="zh-CN">仅添加了统一的日志对象实例</span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 31, 2021 19:01:22 $
 */
public abstract class AbstractStartupLauncher implements StartupLauncher {
	/**
	 * <span class="en-US">Logger instance</span>
	 * <span class="zh-CN">日志对象</span>
	 */
	protected final LoggerUtils.Logger logger = LoggerUtils.getLogger(this.getClass());
}
