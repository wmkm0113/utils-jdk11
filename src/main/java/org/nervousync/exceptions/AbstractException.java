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
package org.nervousync.exceptions;

import org.nervousync.utils.MultilingualUtils;
import org.nervousync.utils.ObjectUtils;

/**
 * <h2 class="en">Abstract Exception</h2>
 * <h2 class="zh-CN">异常抽象类</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jul 06, 2023 12:56:26 $
 */
public abstract class AbstractException extends Exception {
	/**
	 * <span class="en">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
	private static final long serialVersionUID = 3698481050554660066L;
	/**
	 * <span class="en">Error identified code</span>
	 * <span class="zh-CN">错误识别代码</span>
	 */
	private final long errorCode;
	/**
	 * <h3 class="en">Constructor method for NetworkInfoException</h3>
	 * <span class="en">Create a new NetworkInfoException with the specified message.</span>
	 * <h3 class="zh-CN">NetworkInfoException构造方法</h3>
	 * <span class="zh-CN">使用特定的信息创建NetworkInfoException实例对象。</span>
	 *
	 * @param errorCode 	<span class="en">Error identified code</span>
     *                      <span class="zh-CN">错误识别代码</span>
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param messageKey    <span class="en">Message identify key</span>
     *                      <span class="zh-CN">信息识别键值</span>
     * @param collections   <span class="en">given parameters of information formatter</span>
     *                      <span class="zh-CN">用于资源信息格式化的参数</span>
	 */
	protected AbstractException(final long errorCode, final String bundle, final String messageKey,
								final Object... collections) {
		super(MultilingualUtils.findMessage(bundle, messageKey, collections));
		this.errorCode = errorCode;
	}
	/**
	 * <h3 class="en">Constructor method for NetworkInfoException</h3>
	 * <span class="en">Create a new NetworkInfoException with the specified message and root cause.</span>
	 * <h3 class="zh-CN">NetworkInfoException构造方法</h3>
	 * <span class="zh-CN">使用特定的信息以及异常信息对象实例创建NetworkInfoException实例对象。</span>
	 *
	 * @param errorCode 	<span class="en">Error identified code</span>
     *                      <span class="zh-CN">错误识别代码</span>
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param messageKey    <span class="en">Message identify key</span>
     *                      <span class="zh-CN">信息识别键值</span>
	 * @param cause 		<span class="en">The root cause</span>
	 *              		<span class="zh-CN">异常信息对象实例</span>
     * @param collections   <span class="en">given parameters of information formatter</span>
     *                      <span class="zh-CN">用于资源信息格式化的参数</span>
	 */
	protected AbstractException(final long errorCode, final String bundle, final String messageKey,
								final Throwable cause, final Object... collections) {
		super(MultilingualUtils.findMessage(bundle, messageKey, collections), cause);
		this.errorCode = errorCode;
	}
	/**
	 * <h3 class="en">Getter method for error identified code</h3>
	 * <h3 class="zh-CN">错误识别代码的Getter方法</h3>
	 *
	 * @return 	<span class="en">Error identified code</span>
	 * 			<span class="zh-CN">错误识别代码</span>
	 */
	public long getErrorCode() {
		return errorCode;
	}

	/**
	 * (non-javadoc)
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(final Object other) {
		if (other == null) {
			return Boolean.FALSE;
		}
		if (this == other) {
			return Boolean.TRUE;
		}
		if (ObjectUtils.nullSafeEquals(other.getClass(), this.getClass())) {
			AbstractException otherBe = (AbstractException) other;
			return (ObjectUtils.nullSafeEquals(this.getMessage(), otherBe.getMessage()) &&
					ObjectUtils.nullSafeEquals(this.getCause(), otherBe.getCause()));
		}
		return Boolean.FALSE;
	}
	/**
	 * (non-javadoc)
	 * @see Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return this.getMessage().hashCode();
	}
}
