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

import org.nervousync.beans.converter.Adapter;
import org.nervousync.commons.Globals;
import org.nervousync.enumerations.beans.DataFlow;

import java.lang.annotation.*;

/**
 * <h2 class="en">JavaBean Property Annotation</h2>
 * <span class="en">
 *     <p>If annotation fields means copy data from target bean</p>
 *     <p>If using for Annotation Mappings field, means copy data to target bean</p>
 * </span>
 * <h2 class="zh-CN">JavaBean属性注解</h2>
 * <span class="zh-CN">
 *     <p>如果直接标注在属性上，表示复制数据来自目标对象</p>
 *     <p>如果用于Mappings注解的参数，表示将数据复制到目标对象</p>
 * </span>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Dec 25, 2022 14:28:33 $
 */
@Documented
@Repeatable(BeanProperties.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BeanProperty {
	/**
	 * <h3 class="en">Priority value of JavaBean property</h3>
	 * <p class="en">Priority value is descending sort for copy property value</p>
	 * <h3 class="zh-CN">JavaBean属性的优先级</h3>
	 * <p class="en">需要复制的属性值依据优先级值进行降序排列</p>
	 *
	 * @return	<span class="en">Priority value</span>
	 * 			<span class="zh-CN">优先级数值</span>
	 */
	int sortCode() default Globals.INITIALIZE_INT_VALUE;
	/**
	 * <h3 class="en">Enumeration value of JavaBean property data flow</h3>
	 * <h3 class="zh-CN">JavaBean属性数据流向的枚举值</h3>
	 *
	 * @see org.nervousync.enumerations.beans.DataFlow
	 * @return	<span class="en">Enumeration value</span>
	 * 			<span class="zh-CN">枚举值</span>
	 */
	DataFlow dataFlow();
	/**
	 * <h3 class="en">Target bean class</h3>
	 * <h3 class="zh-CN">目标对象类</h3>
	 *
	 * @return	<span class="en">Target bean class</span>
	 * 			<span class="zh-CN">目标对象类</span>
	 */
	Class<?> beanClass();
	/**
	 * <h3 class="en">Target field name</h3>
	 * <h3 class="zh-CN">目标属性名</h3>
	 *
	 * @return	<span class="en">Target field name</span>
	 * 			<span class="zh-CN">目标属性名</span>
	 */
	String targetField() default Globals.DEFAULT_VALUE_STRING;
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
	Class<?> converter() default Adapter.class;
}
