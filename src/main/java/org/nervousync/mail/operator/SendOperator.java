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
package org.nervousync.mail.operator;

import org.nervousync.mail.config.MailConfig;

import java.util.Properties;

/**
 * The interface SendOperator.
 */
public interface SendOperator {

    /**
     * Read config properties.
     *
     * @param serverConfig the server config
     * @return the properties
     */
    Properties readConfig(final MailConfig.ServerConfig serverConfig);

}
