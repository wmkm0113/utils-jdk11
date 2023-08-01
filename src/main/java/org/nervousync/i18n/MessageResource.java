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
package org.nervousync.i18n;

import jakarta.annotation.Nonnull;
import org.nervousync.commons.Globals;
import org.nervousync.utils.ConvertUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * <h2 class="en">Internationalization Message Resource Define</h2>
 * <h2 class="zh-CN">国际化信息资源定义</h2>
 *.0
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 19, 2023 16:39:41 $
 */
public final class MessageResource {
    /**
     * <span class="en">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * <span class="en">Resource information map</span>
     * <span class="zh-CN">资源信息映射表</span>
     */
    private final Map<String, String> resourcesMap;
    /**
     * <span class="en">Cached resource formatter map</span>
     * <span class="zh-CN">缓存的资源信息格式化器实例映射表</span>
     */
    private final Map<String, MessageFormat> cachedFormaterMap;
	/**
	 * <h3 class="en">Constructor for Resource</h3>
	 * <h3 class="zh-CN">国际化资源的构造方法</h3>
     *
     * @param properties    <span class="en">Resource information properties instance</span>
     *                      <span class="zh-CN">资源信息属性实例对象</span>
	 */
    public MessageResource(@Nonnull final Properties properties) {
        this.resourcesMap = ConvertUtils.toMap(properties);
        this.cachedFormaterMap = new HashMap<>();
    }

    /**
	 * <h3 class="en">Update resource messages</h3>
	 * <h3 class="zh-CN">更新国际化信息内容</h3>
     *
     * @param properties    <span class="en">Resource information properties instance</span>
     *                      <span class="zh-CN">资源信息属性实例对象</span>
     */
    public void updateResource(@Nonnull final Properties properties) {
        ConvertUtils.toMap(properties)
                .forEach((key, value) -> {
                    if (this.resourcesMap.containsKey(key)) {
                        this.cachedFormaterMap.remove(key);
                        this.logger.warn("Override resource key: {}, original value: {}, new value: {}",
                                key, this.resourcesMap.get(key), value);
                    }
                    this.resourcesMap.put(key, value);
                });
    }
    /**
	 * <h3 class="en">Retrieve internationalization information content and formatted by given collections</h3>
	 * <h3 class="zh-CN">读取国际化资源信息详情并使用给定的参数集合格式化资源信息</h3>
     *
     * @param messageKey    <span class="en">Message identify key</span>
     *                      <span class="zh-CN">信息识别键值</span>
     * @param collections   <span class="en">given parameters of information formatter</span>
     *                      <span class="zh-CN">用于资源信息格式化的参数</span>
     *
     * @return  <span class="en">Formatted resource information or empty string if not found</span>
     *          <span class="zh-CN">格式化的资源信息，如果未找到则返回空字符串</span>
     */
    public String findMessage(final String messageKey, final Object... collections) {
        return Optional.ofNullable(this.retrieveFormatter(messageKey))
                .map(messageFormat -> messageFormat.format(collections))
                .orElse(Globals.DEFAULT_VALUE_STRING);
    }
    /**
	 * <h3 class="en">Retrieve message formatter instance</h3>
	 * <h3 class="zh-CN">读取信息格式化器实例对象</h3>
     *
     * @param messageKey    <span class="en">Message identify key</span>
     *                      <span class="zh-CN">信息识别键值</span>
     *
     * @return  <span class="en">Retrieved message formatter instance or <code>null</code> if argument messageKey is <code>null</code> or empty string</span>
     *          <span class="zh-CN">读取的信息格式化器实例对象，如果参数 messageKey 未找到或为空字符串，则返回 <code>null</code></span>
     */
    private MessageFormat retrieveFormatter(final String messageKey) {
        MessageFormat messageFormat = null;
        if (StringUtils.notBlank(messageKey) && this.resourcesMap.containsKey(messageKey)) {
            if (this.cachedFormaterMap.containsKey(messageKey)) {
                messageFormat = this.cachedFormaterMap.get(messageKey);
            } else {
                messageFormat = new MessageFormat(this.resourcesMap.get(messageKey));
                this.cachedFormaterMap.put(messageKey, messageFormat);
            }
        }
        return messageFormat;
    }
}
