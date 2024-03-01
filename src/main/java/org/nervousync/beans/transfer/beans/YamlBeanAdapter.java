/*
 * Licensed to the Nervousync Studio (NSYC) under one or more
 * contributor license agreements. See the NOTICE file distributed with
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
package org.nervousync.beans.transfer.beans;

import org.nervousync.beans.core.BeanObject;
import org.nervousync.commons.Globals;
import org.nervousync.utils.StringUtils;

import java.util.Optional;

/**
 * <h2 class="en-US">JavaBean YamlConverter</h2>
 * <h2 class="zh-CN">JavaBean数据Yaml转换器</h2>
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@gmail.com">wmkm0113@gmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jun 25, 2023 12:39:27 $
 */
public final class YamlBeanAdapter extends AbstractBeanAdapter {

    public YamlBeanAdapter(String className) throws IllegalArgumentException {
        super(className);
    }

    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#unmarshal(Object)
     */
    @Override
    public String marshal(final BeanObject object) {
        return Optional.ofNullable(object)
				.map(BeanObject::toYaml)
				.orElse(Globals.DEFAULT_VALUE_STRING);
    }

    /**
     * @see jakarta.xml.bind.annotation.adapters.XmlAdapter#marshal(Object)
     */
    @Override
    public BeanObject unmarshal(final String string) {
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        return (BeanObject) StringUtils.stringToObject(string, StringUtils.StringType.YAML, this.beanClass);
    }
}
