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
package org.nervousync.security.factory;

import org.nervousync.beans.core.BeanObject;

import jakarta.xml.bind.annotation.*;

/**
 * <h2 class="en">Secure configure information define</h2>
 * <p class="en">Using for protect password in any configure files</p>
 * <h2 class="zh-CN">安全配置信息定义</h2>
 * <p class="zh-CN">用于在任何配置文件中保护密码</p>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Dec 12, 2020 23:05:27 $
 */
@XmlType(name = "secure_config", namespace = "https://nervousync.org/schemas/secure")
@XmlRootElement(name = "secure_config", namespace = "https://nervousync.org/schemas/secure")
@XmlAccessorType(XmlAccessType.NONE)
public final class SecureConfig extends BeanObject {
	/**
	 * <span class="en">Serial version UID</span>
	 * <span class="zh-CN">序列化UID</span>
	 */
    private static final long serialVersionUID = -4333190425770207630L;
    /**
	 * <span class="en">Secure algorithm</span>
	 * <span class="zh-CN">安全算法</span>
     */
    @XmlElement(name = "secure_algorithm")
    private SecureFactory.SecureAlgorithm secureAlgorithm = null;
    /**
	 * <span class="en">Secure key</span>
	 * <span class="zh-CN">安全密钥</span>
     */
    @XmlElement(name = "secure_key")
    private String secureKey = null;
    /**
	 * <h3 class="en">Constructor method for SecureConfig</h3>
	 * <h3 class="zh-CN">安全配置信息构造方法</h3>
     */
    public SecureConfig() {
    }
    /**
	 * <h3 class="en">Getter method for secure algorithm</h3>
	 * <h3 class="zh-CN">安全算法的Getter方法</h3>
	 *
     * @return  <span class="en">Secure algorithm</span>
	 *          <span class="zh-CN">安全算法</span>
     */
    public SecureFactory.SecureAlgorithm getSecureAlgorithm() {
        return secureAlgorithm;
    }
    /**
	 * <h3 class="en">Setter method for secure algorithm</h3>
	 * <h3 class="zh-CN">安全算法的Setter方法</h3>
	 *
     * @param secureAlgorithm   <span class="en">Secure algorithm</span>
	 *                          <span class="zh-CN">安全算法</span>
     */
    public void setSecureAlgorithm(SecureFactory.SecureAlgorithm secureAlgorithm) {
        this.secureAlgorithm = secureAlgorithm;
    }
    /**
	 * <h3 class="en">Getter method for secure key</h3>
	 * <h3 class="zh-CN">安全密钥的Getter方法</h3>
	 *
     * @return  <span class="en">Secure key</span>
	 *          <span class="zh-CN">安全密钥</span>
     */
    public String getSecureKey() {
        return secureKey;
    }
    /**
	 * <h3 class="en">Setter method for secure key</h3>
	 * <h3 class="zh-CN">安全密钥的Setter方法</h3>
	 *
     * @param secureKey     <span class="en">Secure key</span>
	 *                      <span class="zh-CN">安全密钥</span>
     */
    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }
}
