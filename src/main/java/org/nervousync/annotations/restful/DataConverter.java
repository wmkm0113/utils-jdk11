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
package org.nervousync.annotations.restful;

import org.nervousync.beans.converter.Adapter;
import java.lang.annotation.*;

/**
 * <h2 class="en">Annotation for Restful service request parameter to register data converter</h2>
 * <h2 class="zh-CN">用于标注在Restful服务接口参数的注解，注册数据转换器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 26, 2023 10:47:28 $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface DataConverter {
	/**
	 * <h3 class="en">Data converter class</h3>
	 * <p class="en">Class must implements org.nervousync.beans.converter.IConverter, T is current field type class, U is target bean field type class</p>
	 * <h3 class="zh-CN">数据转换类</h3>
	 * <p class="zh-CN">类必须实现接口org.nervousync.beans.converter.IConverter，T是注解属性的数据类型，U是目标属性的数据类型</p>
	 *
	 * @see Adapter
	 * @return	<span class="en">Data converter class</span>
	 * 			<span class="zh-CN">数据转换类</span>
	 */
    Class<?> value() default Adapter.class;
}
