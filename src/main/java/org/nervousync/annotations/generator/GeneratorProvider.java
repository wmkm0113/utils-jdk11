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
package org.nervousync.annotations.generator;

import java.lang.annotation.*;

/**
 * <h2 class="en-US">Annotation class for ID generator implement class</h2>
 * <h2 class="zh-CN">ID生成器实现类的标注</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 10, 2021 15:24:26 $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GeneratorProvider {
    /**
     * <span class="en-US">ID generator implement name</span>
     * <span class="zh-CN">ID生成器实现名称</span>
     *
     * @return  <span class="en-US">Implement name</span>
     *          <span class="zh-CN">实现名称</span>
     */
    String value();
}
