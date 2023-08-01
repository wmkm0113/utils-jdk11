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
package org.nervousync.enumerations.mail;

/**
 * <h2 class="en">Mail server protocol Enumerations</h2>
 * <h2 class="zh-CN">邮件服务器协议枚举</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $Date: Sep 21, 2018 17:21:28 $
 */
public enum MailProtocol {
	/**
     * <span class="en">SMTP Protocol</span>
     * <span class="zh-CN">SMTP协议</span>
	 */
	SMTP,
	/**
     * <span class="en">POP3 Protocol</span>
     * <span class="zh-CN">POP3协议</span>
	 */
	POP3,
	/**
     * <span class="en">IMAP Protocol</span>
     * <span class="zh-CN">IMAP协议</span>
	 */
	IMAP,
	/**
     * <span class="en">Unknown type</span>
     * <span class="zh-CN">未知类型</span>
	 */
	UNKNOWN
}
