/*
 * Copyright 2021 Nervousync Studio
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
package org.nervousync.exceptions.builder;

/**
 * The type Builder exception.
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Apr 25, 2017 6:30:42 PM $
 */
public class BuilderException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -156775157749202954L;

    /**
     * Constructs an instance of CachedException with the specified detail message.
     *
     * @param errorMessage The detail message.
     */
    public BuilderException(String errorMessage) {
        super(errorMessage);
    }
}
