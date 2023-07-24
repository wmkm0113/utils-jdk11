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
package org.nervousync.beans.converter;

/**
 * <h2 class="en">DataConverter interface class</h2>
 * <h2 class="zh-CN">数据转换器接口类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jun 21, 2023 11:22:15 $
 */
public interface Adapter<ValueType, BoundType> {
    /**
     * <h3 class="en">Encode object to target type</h3>
     * <h3 class="zh-CN">将对象编码为指定类型</h3>
     *
     * @param object    <span class="en">Target object instance</span>
     *                  <span class="zh-CN">目标对象实例</span>
     *
     * @return          <span class="en">Encoded object instance or null if target object instance is null</span>
     *                  <span class="zh-CN">转换后的对象，如果目标对象实例为空，则返回null</span>
     * @throws Exception
     * <span class="en">If an error occurs when execute</span>
     * <span class="zh-CN">当执行时出现错误</span>
     */
    BoundType unmarshal(final ValueType object) throws Exception;
    /**
     * <h3 class="en">Decode target instance to object</h3>
     * <h3 class="zh-CN">将给定的对象解码为对象</h3>
     *
     * @param object    <span class="en">Source object instance</span>
     *                  <span class="zh-CN">源对象实例</span>
     *
     * @return          <span class="en">Decode object instance</span>
     *                  <span class="zh-CN">解码后的对象实例</span>
     * @throws Exception
     * <span class="en">If an error occurs when execute</span>
     * <span class="zh-CN">当执行时出现错误</span>
     */
    ValueType marshal(final BoundType object) throws Exception;
}
