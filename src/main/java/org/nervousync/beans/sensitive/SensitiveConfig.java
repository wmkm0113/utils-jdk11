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
package org.nervousync.beans.sensitive;

import org.nervousync.utils.ObjectUtils;
import org.nervousync.utils.StringUtils;

/**
 * <h2 class="en-US">Sensitive information handling configuration</h2>
 * <h2 class="zh-CN">敏感信息处理配置</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Sep 12, 2023 15:28:21 $
 */
public final class SensitiveConfig {
    /**
     * <span class="en-US">Show prefix length</span>
     * <span class="zh-CN">显示前缀长度</span>
     */
    private final int prefixLength;
    /**
     * <span class="en-US">Show suffix length</span>
     * <span class="zh-CN">显示后缀长度</span>
     */
    private final int suffixLength;

    /**
     * <h2 class="en-US">Private constructor for sensitive information processing configuration</h2>
     * <h2 class="zh-CN">敏感信息处理配置的私有构造方法</h2>
     *
     * @param prefixLength <span class="en-US">Show prefix length</span>
     *                     <span class="zh-CN">显示前缀长度</span>
     * @param suffixLength <span class="en-US">Show suffix length</span>
     *                     <span class="zh-CN">显示后缀长度</span>
     */
    private SensitiveConfig(final int prefixLength, final int suffixLength) {
        this.prefixLength = prefixLength;
        this.suffixLength = suffixLength;
    }

    /**
     * <h2 class="en-US">Static methods are used to generate sensitive information processing configuration instance objects</h2>
     * <h2 class="zh-CN">静态方法用于生成敏感信息处理配置实例对象</h2>
     *
     * @param sensitiveType <span class="en-US">Sensitive information data type</span>
     *                      <span class="zh-CN">敏感信息数据类型</span>
     * @param sensitiveData <span class="en-US">Sensitive information data content</span>
     *                      <span class="zh-CN">敏感信息数据内容</span>
     * @return <span class="en-US">Generated sensitive information handling configuration</span>
     * <span class="zh-CN">生成的敏感信息处理配置实例对象</span>
     */
    public static SensitiveConfig newInstance(final ObjectUtils.SensitiveType sensitiveType,
                                              final String sensitiveData) {
        if (StringUtils.isEmpty(sensitiveData)) {
            return null;
        }
        int prefixLength, suffixLength;
        switch (sensitiveType) {
            case Luhn:
                prefixLength = 2;
                suffixLength = sensitiveData.length() % 4;
                if (suffixLength == 0) {
                    suffixLength = 4;
                }
                break;
            case CHN_ID_Code:
                prefixLength = 3;
                suffixLength = 1;
                break;
            case CHN_Social_Code:
                prefixLength = 5;
                suffixLength = 1;
                break;
            case E_MAIL:
                prefixLength = 1;
                suffixLength = sensitiveData.length() - sensitiveData.indexOf("@");
                break;
            case PHONE_NUMBER:
                prefixLength = 3;
                suffixLength = 2;
                break;
            default:
                if (sensitiveData.length() > 2) {
                    prefixLength = suffixLength = 1;
                } else {
                    prefixLength = 1;
                    suffixLength = 0;
                }
        }
        return new SensitiveConfig(prefixLength, suffixLength);
    }

    /**
     * <h3 class="en-US">Getter method for show prefix length</h3>
     * <h3 class="zh-CN">显示前缀长度的Getter方法</h3>
     *
     * @return <span class="en-US">Show prefix length</span>
     * <span class="zh-CN">显示前缀长度</span>
     */
    public int getPrefixLength() {
        return prefixLength;
    }

    /**
     * <h3 class="en-US">Getter method for show suffix length</h3>
     * <h3 class="zh-CN">显示后缀长度的Getter方法</h3>
     *
     * @return <span class="en-US">Show suffix length</span>
     * <span class="zh-CN">显示后缀长度</span>
     */
    public int getSuffixLength() {
        return suffixLength;
    }
}
