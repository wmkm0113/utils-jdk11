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
package org.nervousync.exceptions.utils;

/**
 * The type Data invalid exception.
 */
public final class DataInvalidException extends RuntimeException {

    private static final long serialVersionUID = -2896313924690716673L;

    /**
     * Instantiates a new Data invalid exception.
     */
    public DataInvalidException() {
    }

    /**
     * Instantiates a new Data invalid exception.
     *
     * @param errorMessage the error message
     */
    public DataInvalidException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Instantiates a new Data invalid exception.
     *
     * @param e the e
     */
    public DataInvalidException(Exception e) {
        super(e);
    }

    /**
     * Instantiates a new Data invalid exception.
     *
     * @param errorMessage the error message
     * @param e            the e
     */
    public DataInvalidException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }
}
