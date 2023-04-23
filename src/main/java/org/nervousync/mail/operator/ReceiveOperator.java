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

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.nervousync.mail.config.MailConfig;

import java.util.List;
import java.util.Properties;

/**
 * The interface Mail receiver.
 */
public interface ReceiveOperator {

    /**
     * Read uid string.
     *
     * @param folder  the folder
     * @param message the message
     * @return the string
     * @throws MessagingException the messaging exception
     */
    String readUID(final Folder folder, final Message message) throws MessagingException;

    /**
     * Read the folder message.
     *
     * @param folder the folder
     * @param uid    the uid
     * @return the message
     * @throws MessagingException the messaging exception
     */
    Message readMessage(final Folder folder, final String uid) throws MessagingException;

    /**
     * Read the folder messages list.
     *
     * @param folder    the folder
     * @param uidArrays the uid arrays
     * @return the list
     * @throws MessagingException the messaging exception
     */
    List<Message> readMessages(final Folder folder, final String... uidArrays) throws MessagingException;

    /**
     * Read config properties.
     *
     * @param serverConfig the server config
     * @return the properties
     */
    Properties readConfig(final MailConfig.ServerConfig serverConfig);

}
