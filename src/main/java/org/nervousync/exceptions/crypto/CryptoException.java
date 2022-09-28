/*
 * Copyright 2018 Nervousync Studio
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nervousync.exceptions.crypto;

/**
 * The type Crypto exception.
 */
public class CryptoException extends Exception {

    private static final long serialVersionUID = 6112136690122627440L;

    /**
     * Creates a new instance of CryptoException without detail message.
     */
    public CryptoException() {
    }

    /**
     * Constructs an instance of CryptoException with the specified detail message.
     *
     * @param errorMessage The detail message.
     */
    public CryptoException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Creates an instance of CryptoException with nested exception
     *
     * @param e Nested exception
     */
    public CryptoException(Exception e) {
        super(e);
    }
}
