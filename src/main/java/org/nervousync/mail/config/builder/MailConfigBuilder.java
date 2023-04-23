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
package org.nervousync.mail.config.builder;

import org.nervousync.mail.config.MailConfig;

public final class MailConfigBuilder extends AbstractMailConfigBuilder<MailConfig> {

	/**
	 * Instantiates a new Builder.
	 *
	 * @param mailConfig the mail config
	 */
	private MailConfigBuilder(final MailConfig mailConfig) {
		super(mailConfig, mailConfig);
	}

	@Override
	protected void build() {
		super.parentBuilder.copyProperties(this.mailConfig);
	}

	public static MailConfigBuilder newBuilder() {
		return newBuilder(new MailConfig());
	}

	public static MailConfigBuilder newBuilder(final MailConfig mailConfig) {
		return new MailConfigBuilder(mailConfig);
	}
}
