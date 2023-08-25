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
package org.nervousync.annotations.beans;

import org.nervousync.utils.ObjectUtils;

import java.lang.annotation.*;

/**
 * <h2 class="en-US">JavaBean Property Desensitization Annotation</h2>
 * <span class="en-US">
 *     After using this annotation on the properties of JavaBean,
 *     you can call the desensitization method in ObjectUtils for automatic data desensitization
 * </span>
 * <h2 class="zh-CN">JavaBean属性脱敏注解</h2>
 * <span class="zh-CN">在JavaBean的属性上使用此注解后，可以调用ObjectUtils中的desensitization方法进行数据自动脱敏</span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 25, 2022 14:28:33 $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Desensitization {
    ObjectUtils.SensitiveType value() default ObjectUtils.SensitiveType.NORMAL;
}
