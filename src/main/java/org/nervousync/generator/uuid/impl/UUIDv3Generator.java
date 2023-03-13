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
package org.nervousync.generator.uuid.impl;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.commons.core.Globals;
import org.nervousync.generator.uuid.UUIDGenerator;
import org.nervousync.utils.IDUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * The type Uui dv 3 generator.
 */
@GeneratorProvider(IDUtils.UUIDv3)
public final class UUIDv3Generator extends UUIDGenerator {

    @Override
    public String random() {
        return this.random(new byte[0]);
    }

    @Override
    public String random(byte[] dataBytes) {
        try {
            byte[] randomBytes = MessageDigest.getInstance("MD5").digest(dataBytes);
            randomBytes[6] &= 0x0F;     /* clear version        */
            randomBytes[6] |= 0x30;     /* set to version 3     */
            randomBytes[8] &= 0x3F;     /* clear variant        */
            randomBytes[8] |= 0x80;     /* set to IETF variant  */
            return new UUID(super.highBits(randomBytes), super.lowBits(randomBytes)).toString();
        } catch (NoSuchAlgorithmException e) {
            return Globals.DEFAULT_VALUE_STRING;
        }
    }
}
