/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
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
package org.nervousync.enumerations.zip;

/**
 * <h2 class="en-US">Zip Compress Level Enumeration</h2>
 * <h2 class="zh-CN">Zip压缩等级</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 28, 2016 12:32:27 $
 */
public enum CompressLevel {
	/**
     * <span class="en-US">Fastest Compress</span>
     * <span class="zh-CN">极快压缩</span>
	 */
	FASTEST,
	/**
     * <span class="en-US">Fast Compress</span>
     * <span class="zh-CN">快压缩</span>
	 */
	FAST,
	/**
     * <span class="en-US">Normal Compress</span>
     * <span class="zh-CN">正常压缩</span>
	 */
	NORMAL,
	/**
     * <span class="en-US">Maximum Compress</span>
     * <span class="zh-CN">最大压缩</span>
	 */
	MAXIMUM,
	/**
     * <span class="en-US">Ultra Compress</span>
     * <span class="zh-CN">极限压缩</span>
	 */
	ULTRA
}
