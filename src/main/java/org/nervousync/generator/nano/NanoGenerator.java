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
package org.nervousync.generator.nano;

import org.nervousync.annotations.generator.GeneratorProvider;
import org.nervousync.generator.IGenerator;
import org.nervousync.utils.IDUtils;
import org.nervousync.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

/**
 * The type Nano generator.
 */
@GeneratorProvider(IDUtils.NANO_ID)
public final class NanoGenerator implements IGenerator<String> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyz";
    private static final int DEFAULT_LENGTH = 27;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private char[] alphabetArray = DEFAULT_ALPHABET.toCharArray();
    private int generateLength = DEFAULT_LENGTH;

    /**
     * Config.
     *
     * @param alphabetConfig the alphabet config
     * @param generateLength generate length
     */
    public void config(final String alphabetConfig, final int generateLength) {
        if (StringUtils.notBlank(alphabetConfig)) {
            if (alphabetConfig.length() > 255) {
                this.logger.error("Alphabet must contain between 1 and 255 symbols.");
            } else {
                this.alphabetArray = alphabetConfig.toCharArray();
            }
        }
        this.generateLength = generateLength > 0 ? generateLength : DEFAULT_LENGTH;
    }

    @Override
    public String random() {
        final int mask = (2 << (int) Math.floor(Math.log(this.alphabetArray.length - 1) / Math.log(2))) - 1;
        final int length = (int) Math.ceil(1.6 * mask * this.generateLength / this.alphabetArray.length);

        final StringBuilder idBuilder = new StringBuilder();

        while (true) {
            final byte[] dataBytes = new byte[length];
            SECURE_RANDOM.nextBytes(dataBytes);
            for (int i = 0; i < length; i++) {
                final int alphabetIndex = dataBytes[i] & mask;
                if (alphabetIndex < this.alphabetArray.length) {
                    idBuilder.append(this.alphabetArray[alphabetIndex]);
                    if (idBuilder.length() == this.generateLength) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }

    @Override
    public String random(byte[] dataBytes) {
        return this.random();
    }

    @Override
    public void destroy() {
    }
}
