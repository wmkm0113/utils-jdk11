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
package org.nervousync.utils;

import jakarta.annotation.Nonnull;
import org.nervousync.beans.ip.path.TargetPath;
import org.nervousync.commons.Globals;
import org.nervousync.i18n.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <h2 class="en-US">Internationalization Utilities</h2>
 * <h2 class="zh-CN">国际化工具集</h2>
 * .0
 *
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0.0 $ $Date: Jul 19, 2023 16:39:41 $
 */
public final class MultilingualUtils {
    /**
     * <span class="en-US">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(MultilingualUtils.class);

    /**
     * <span class="en-US">Registered resources map</span>
     * <span class="zh-CN">已注册的资源信息映射表</span>
     */
    private static final Map<String, Map<String, MessageResource>> REGISTERED_RESOURCES = new HashMap<>();
    private static final Map<String, String> IDENTIFY_KEY_MAP = new HashMap<>();
    /**
     * <span class="en-US">Default locale instance, usually value is default locale for this instance of the Java Virtual Machine.</span>
     * <span class="zh-CN">默认区域设置实例，通常值是 Java 虚拟机实例的默认区域设置。</span>
     */
    private static String DEFAULT_LANGUAGE_CODE = toLanguageCode(Globals.DEFAULT_LOCALE);
    /**
     * <span class="en-US">XML Schema file mapping resource path</span>
     * <span class="zh-CN">XML约束文档的资源映射文件</span>
     */
    private static final String BUNDLE_RESOURCE_PATH = "META-INF/nervousync.i18n";
    private static final String MESSAGE_RESOURCE_PREFIX = "META-INF/i18n/";

    static {
        try {
            ClassUtils.getDefaultClassLoader().getResources(BUNDLE_RESOURCE_PATH)
                    .asIterator()
                    .forEachRemaining(MultilingualUtils::registerBundle);
        } catch (IOException ignore) {
        }
    }

    /**
     * <h3 class="en-US">Private constructor for MultilingualUtils</h3>
     * <h3 class="zh-CN">国际化工具集的私有构造方法</h3>
     */
    private MultilingualUtils() {
    }

    /**
     * <h3 class="en-US">Convert locale instance to string.</h3>
     * <h3 class="zh-CN">将语言环境实例转换为字符串。</h3>
     *
     * @param locale <span class="en-US">locale instance</span>
     *               <span class="zh-CN">区域设置实例</span>
     * @return <span class="en-US">Converted string</span>
     * <span class="zh-CN">转换后的字符串</span>
     */
    public static String toLanguageCode(@Nonnull final Locale locale) {
        return Optional.of(locale.getCountry())
                .filter(StringUtils::notBlank)
                .map(countryCode -> locale.getLanguage() + "-" + countryCode)
                .orElse(locale.getLanguage());
    }

    /**
     * <h3 class="en-US">Configure default locale</h3>
     * <h3 class="zh-CN">设置默认语言</h3>
     *
     * @param locale <span class="en-US">Default locale instance</span>
     *               <span class="zh-CN">默认区域设置实例</span>
     */
    public static void defaultLocale(@Nonnull final Locale locale) {
        DEFAULT_LANGUAGE_CODE = toLanguageCode(locale);
    }

    /**
     * <h3 class="en-US">Generate multilingual agent instance</h3>
     * <h3 class="zh-CN">生成国际化代理实例对象</h3>
     *
     * @param groupId <span class="en-US">Resource group id</span>
     *                <span class="zh-CN">资源的组ID</span>
     * @param bundle  <span class="en-US">Resource bundle</span>
     *                <span class="zh-CN">资源的标识</span>
     * @return <span class="en-US">Generated instance</span>
     * <span class="zh-CN">生成的实例对象</span>
     */
    public static Agent newAgent(final String groupId, final String bundle) {
        return new Agent(groupId, bundle);
    }

    /**
     * <h3 class="en-US">Generate multilingual agent instance</h3>
     * <h3 class="zh-CN">生成国际化代理实例对象</h3>
     *
     * @param clazz <span class="en-US">Class instance</span>
     *              <span class="zh-CN">类实例对象</span>
     * @return <span class="en-US">Generated instance</span>
     * <span class="zh-CN">生成的实例对象</span>
     */
    public static Agent newAgent(final Class<?> clazz) {
        return new Agent(clazz);
    }

    /**
     * <h3 class="en-US">Remove message resources by given argument languageCode if registered.</h3>
     * <h3 class="zh-CN">根据给定的参数 languageCode 移除已注册的国际化信息资源</h3>
     *
     * @param languageCode <span class="en-US">Resource language code</span>
     *                     <span class="zh-CN">资源语言代码</span>
     */
    public static void removeResource(@Nonnull final String languageCode) {
        if (StringUtils.notBlank(languageCode)) {
            for (String bundle : REGISTERED_RESOURCES.keySet()) {
                Map<String, MessageResource> bundleMap = REGISTERED_RESOURCES.getOrDefault(bundle, new HashMap<>());
                bundleMap.remove(languageCode);
                REGISTERED_RESOURCES.put(bundle, bundleMap);
            }
        }
    }

    /**
     * <h3 class="en-US">Remove bundle resources by given argument bundle if registered.</h3>
     * <h3 class="zh-CN">根据给定的参数 bundle 移除已注册的国际化信息资源</h3>
     *
     * @param groupId <span class="en-US">Resource group id</span>
     *                <span class="zh-CN">资源的组ID</span>
     * @param bundle  <span class="en-US">Resource bundle</span>
     *                <span class="zh-CN">资源的标识</span>
     */
    public static void removeBundle(@Nonnull final String groupId, @Nonnull final String bundle) {
        if (StringUtils.notBlank(groupId) && StringUtils.notBlank(bundle)) {
            REGISTERED_RESOURCES.remove(groupId + ":" + bundle);
        }
    }

    /**
     * <h3 class="en-US">Remove message resources by given argument bundle and languageCode if registered.</h3>
     * <h3 class="zh-CN">根据给定的参数 bundle 和 languageCode 移除已注册的国际化信息资源</h3>
     *
     * @param groupId      <span class="en-US">Resource group id</span>
     *                     <span class="zh-CN">资源的组ID</span>
     * @param bundle       <span class="en-US">Resource bundle</span>
     *                     <span class="zh-CN">资源的标识</span>
     * @param languageCode <span class="en-US">Resource language code</span>
     *                     <span class="zh-CN">资源语言代码</span>
     */
    public static void removeResource(@Nonnull final String groupId, @Nonnull final String bundle,
                                      @Nonnull final String languageCode) {
        if (StringUtils.notBlank(groupId) && StringUtils.notBlank(bundle) && StringUtils.notBlank(languageCode)) {
            String identifyKey = groupId + ":" + bundle;
            Map<String, MessageResource> bundleMap = REGISTERED_RESOURCES.getOrDefault(identifyKey, new HashMap<>());
            bundleMap.remove(languageCode);
            REGISTERED_RESOURCES.put(identifyKey, bundleMap);
        }
    }

    /**
     * <h3 class="en-US">Register i18n message resource</h3>
     * <h3 class="zh-CN">注册国际化信息资源</h3>
     *
     * @param identifyKey  <span class="en-US">Resource identified key</span>
     *                     <span class="zh-CN">资源唯一识别码</span>
     * @param languageCode <span class="en-US">Resource language code</span>
     *                     <span class="zh-CN">资源语言代码</span>
     * @param properties   <span class="en-US">Resource information properties instance</span>
     *                     <span class="zh-CN">资源信息属性实例对象</span>
     * @return <span class="en-US">Registered result, <code>Boolean.TRUE</code> for success, <code>Boolean.FALSE</code> for failed</span>
     * <span class="zh-CN">注册结果，成功返回 <code>Boolean.TRUE</code>，失败返回 <code>Boolean.FALSE</code></span>
     */
    private static boolean registerResource(@Nonnull final String identifyKey, @Nonnull final String languageCode,
                                            @Nonnull final Properties properties) {
        if (StringUtils.isEmpty(identifyKey) || properties.isEmpty()) {
            return Boolean.FALSE;
        }
        Map<String, MessageResource> bundleMap = REGISTERED_RESOURCES.getOrDefault(identifyKey, new HashMap<>());
        MessageResource messageResource;
        if (bundleMap.containsKey(languageCode)) {
            messageResource = bundleMap.get(languageCode);
            messageResource.updateResource(properties);
        } else {
            messageResource = new MessageResource(properties);
        }
        bundleMap.put(languageCode, messageResource);
        REGISTERED_RESOURCES.put(identifyKey, bundleMap);
        return Boolean.TRUE;
    }

    /**
     * <h3 class="en-US">Read resource files and register</h3>
     * <h3 class="zh-CN">读取资源文件并注册</h3>
     *
     * @param identifyKey  <span class="en-US">Resource identified key</span>
     *                     <span class="zh-CN">资源唯一识别码</span>
     * @param languageCode <span class="en-US">Resource language code</span>
     *                     <span class="zh-CN">资源语言代码</span>
     * @param dataBytes    <span class="en-US">Message resource data bytes</span>
     *                     <span class="zh-CN">信息资源文件二进制数组</span>
     * @return <span class="en-US">Registered result, <code>Boolean.TRUE</code> for success, <code>Boolean.FALSE</code> for failed</span>
     * <span class="zh-CN">注册结果，成功返回 <code>Boolean.TRUE</code>，失败返回 <code>Boolean.FALSE</code></span>
     */
    private static boolean registerResource(@Nonnull final String identifyKey, @Nonnull final String languageCode,
                                            @Nonnull final byte[] dataBytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(dataBytes)) {
            return registerResource(identifyKey, languageCode,
                    PropertiesUtils.loadProperties(inputStream, Boolean.TRUE));
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }

    /**
     * <h3 class="en-US">Retrieve internationalization information content and formatted by given collections</h3>
     * <h3 class="zh-CN">读取国际化资源信息详情并使用给定的参数集合格式化资源信息</h3>
     *
     * @param identifyKey  <span class="en-US">Resource identified key</span>
     *                     <span class="zh-CN">资源唯一识别码</span>
     * @param messageKey   <span class="en-US">Message identify key</span>
     *                     <span class="zh-CN">信息识别键值</span>
     * @param languageCode <span class="en-US">Resource language code</span>
     *                     <span class="zh-CN">资源语言代码</span>
     * @param collections  <span class="en-US">given parameters of information formatter</span>
     *                     <span class="zh-CN">用于资源信息格式化的参数</span>
     * @return <span class="en-US">Formatted resource information or joined string by character '/' if not found</span>
     * <span class="zh-CN">格式化的资源信息，如果未找到则返回使用'/'拼接的字符串</span>
     */
    private static String findMessage(final String identifyKey, final String messageKey, final String languageCode,
                                      final Object... collections) {
        if (StringUtils.notBlank(identifyKey) && StringUtils.notBlank(languageCode)) {
            return Optional.ofNullable(REGISTERED_RESOURCES.get(identifyKey))
                    .map(bundleMap -> bundleMap.containsKey(languageCode)
                            ? bundleMap.get(languageCode) : bundleMap.get(DEFAULT_LANGUAGE_CODE))
                    .map(messageResource -> messageResource.findMessage(messageKey, collections))
                    .filter(StringUtils::notBlank)
                    .orElse(languageCode + Globals.DEFAULT_URL_SEPARATOR + messageKey);
        }
        return messageKey;
    }

    /**
     * <h3 class="en-US">Retrieve resource identify key by given class</h3>
     * <h3 class="zh-CN">根据给定的类查找资源唯一识别码</h3>
     *
     * @param clazz <span class="en-US">Class instance</span>
     *              <span class="zh-CN">类实例对象</span>
     */
    private static String identifyKey(final Class<?> clazz) {
        String jarPath = URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getFile(),
                Charset.defaultCharset());
        return IDENTIFY_KEY_MAP.getOrDefault(jarPath, Globals.DEFAULT_VALUE_STRING);
    }

    private static void registerBundle(final URL url) {
        String basePath = url.getPath().substring(0, url.getPath().length() - BUNDLE_RESOURCE_PATH.length());
        if (basePath.startsWith(FileUtils.FILE_URL_PREFIX)) {
            basePath = basePath.substring(FileUtils.FILE_URL_PREFIX.length());
        }
        final String resourcePath;
        if (basePath.endsWith(FileUtils.JAR_URL_SEPARATOR)) {
            basePath = basePath.substring(0, basePath.length() - FileUtils.JAR_URL_SEPARATOR.length());
            resourcePath = basePath + FileUtils.JAR_URL_SEPARATOR + MESSAGE_RESOURCE_PREFIX;
        } else {
            resourcePath = basePath + MESSAGE_RESOURCE_PREFIX;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Base resource path: {}", resourcePath);
        }
        Properties properties = new Properties();
//        Map<String, String> dataMap = ConvertUtils.toMap(url);
        try (InputStream inputStream = url.openStream()) {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Load nervousync.i18n file error! ");
            return;
        }

        String groupId = Optional.ofNullable(properties.getProperty("groupId")).orElse(Globals.DEFAULT_VALUE_STRING);
        String bundle = Optional.ofNullable(properties.getProperty("bundle")).orElse(Globals.DEFAULT_VALUE_STRING);
        String languages = Optional.ofNullable(properties.getProperty("languages")).orElse(Globals.DEFAULT_VALUE_STRING);
        if (!groupId.isEmpty() && !bundle.isEmpty() && !languages.isEmpty()) {
            String identifyKey = groupId + ":" + bundle;
            IDENTIFY_KEY_MAP.put(basePath, identifyKey);
            while (!languages.isEmpty()) {
                int index = languages.indexOf(",");
                String languageCode = (index > 0) ? languages.substring(0, index) : languages;
                languages = languages.substring(languageCode.length());
                if (languages.startsWith(",")) {
                    languages = languages.substring(1);
                }
                languageCode = languageCode.trim();
                String filePath = resourcePath + languageCode + ".xml";
                boolean result = registerResource(identifyKey, languageCode, readBytes(filePath));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Group ID: {}, bundle: {}, language: {}, result: {}",
                            groupId, bundle, languageCode, result);
                }
            }
        }
    }

    private static byte[] readBytes(final String filePath) {
        TargetPath targetPath = TargetPath.parse(filePath);
        if (targetPath == null) {
            try (InputStream inputStream = new FileInputStream(filePath)) {
                return readBytes(inputStream);
            } catch (IOException ignored) {
            }
        } else {
            try (JarFile jarFile = new JarFile(new File(targetPath.getFilePath()))) {
                JarEntry packageEntry = jarFile.getJarEntry(targetPath.getEntryPath());
                if (packageEntry != null) {
                    return readBytes(jarFile.getInputStream(packageEntry));
                }
            } catch (IOException ignored) {
            }
        }
        return new byte[0];
    }

    private static byte[] readBytes(final InputStream inputStream) {
        byte[] dataBytes;
        int readLength;
        byte[] readBuffer = new byte[Globals.READ_FILE_BUFFER_SIZE];
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            while ((readLength = inputStream.read(readBuffer)) != Globals.DEFAULT_VALUE_INT) {
                byteArrayOutputStream.write(readBuffer, Globals.INITIALIZE_INT_VALUE, readLength);
            }
            dataBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Read data error! ");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stack message: ", e);
            }
            dataBytes = new byte[0];
        }
        return dataBytes;
    }

    /**
     * <h2 class="en-US">Multilingual Agent</h2>
     * <h2 class="zh-CN">国际化代理</h2>
     */
    public static final class Agent {
        /**
         * <span class="en-US">Resource identified key</span>
         * <span class="zh-CN">资源唯一识别码</span>
         */
        private final String identifyKey;

        /**
         * <h3 class="en-US">Constructor method for MultilingualUtils.Agent</h3>
         * <h3 class="zh-CN">国际化代理的构造方法</h3>
         *
         * @param groupId <span class="en-US">Resource group id</span>
         *                <span class="zh-CN">资源的组ID</span>
         * @param bundle  <span class="en-US">Resource bundle</span>
         *                <span class="zh-CN">资源的标识</span>
         */
        private Agent(final String groupId, final String bundle) {
            this.identifyKey = groupId + ":" + bundle;
        }

        /**
         * <h3 class="en-US">Constructor method for MultilingualUtils.Agent</h3>
         * <h3 class="zh-CN">国际化代理的构造方法</h3>
         *
         * @param clazz <span class="en-US">Class instance</span>
         *              <span class="zh-CN">类实例对象</span>
         */
        private Agent(final Class<?> clazz) {
            this.identifyKey = MultilingualUtils.identifyKey(clazz);
        }

        /**
         * <h3 class="en-US">Retrieve internationalization information content and formatted by given collections</h3>
         * <h3 class="zh-CN">读取国际化资源信息详情并使用给定的参数集合格式化资源信息</h3>
         *
         * @param messageKey  <span class="en-US">Message identify key</span>
         *                    <span class="zh-CN">信息识别键值</span>
         * @param collections <span class="en-US">given parameters of information formatter</span>
         *                    <span class="zh-CN">用于资源信息格式化的参数</span>
         * @return <span class="en-US">Formatted resource information or joined string by character '/' if not found</span>
         * <span class="zh-CN">格式化的资源信息，如果未找到则返回使用'/'拼接的字符串</span>
         */
        public String findMessage(final String messageKey, final Object... collections) {
            return MultilingualUtils.findMessage(this.identifyKey, messageKey, DEFAULT_LANGUAGE_CODE, collections);
        }

        /**
         * <h3 class="en-US">Retrieve internationalization information content and formatted by given collections</h3>
         * <h3 class="zh-CN">读取国际化资源信息详情并使用给定的参数集合格式化资源信息</h3>
         *
         * @param messageKey  <span class="en-US">Message identify key</span>
         *                    <span class="zh-CN">信息识别键值</span>
         * @param locale      <span class="en-US">locale instance</span>
         *                    <span class="zh-CN">区域设置实例</span>
         * @param collections <span class="en-US">given parameters of information formatter</span>
         *                    <span class="zh-CN">用于资源信息格式化的参数</span>
         * @return <span class="en-US">Formatted resource information or joined string by character '/' if not found</span>
         * <span class="zh-CN">格式化的资源信息，如果未找到则返回使用'/'拼接的字符串</span>
         */
        public String findMessage(final String messageKey, final Locale locale, final Object... collections) {
            return MultilingualUtils.findMessage(this.identifyKey, messageKey, toLanguageCode(locale), collections);
        }
    }
}
