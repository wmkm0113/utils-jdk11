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
import org.nervousync.commons.Globals;
import org.nervousync.i18n.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * <h2 class="en">Internationalization Utilities</h2>
 * <h2 class="zh-CN">国际化工具集</h2>
 *.0
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision : 1.0 $ $Date: Jul 19, 2023 16:39:41 $
 */
public final class MultilingualUtils {
    /**
     * <span class="en">Logger instance</span>
     * <span class="zh-CN">日志实例</span>
     */
	private final static Logger LOGGER = LoggerFactory.getLogger(MultilingualUtils.class);
    /**
     * <span class="en">Registered resources map</span>
     * <span class="zh-CN">已注册的资源信息映射表</span>
     */
    private static final Map<String, Map<String, MessageResource>> REGISTERED_RESOURCES = new HashMap<>();
    /**
     * <span class="en">Default locale instance, usually value is default locale for this instance of the Java Virtual Machine.</span>
     * <span class="zh-CN">默认区域设置实例，通常值是 Java 虚拟机实例的默认区域设置。</span>
     */
    private static String DEFAULT_LANGUAGE_CODE = toLanguageCode(Globals.DEFAULT_LOCALE);
    /**
     * <span class="en">XML Schema file mapping resource path</span>
     * <span class="zh-CN">XML约束文档的资源映射文件</span>
     */
    private static final String BUNDLE_RESOURCE_PATH = "META-INF/nervousync.i18n";
    private static final String MESSAGE_RESOURCE_PREFIX = "META-INF/i18n/";
    static {
        try {
            ClassUtils.getDefaultClassLoader().getResources(BUNDLE_RESOURCE_PATH)
                    .asIterator()
                    .forEachRemaining(MultilingualUtils::REGISTER_BUNDLE);
        } catch (IOException ignore) {
        }
    }
    private static void REGISTER_BUNDLE(final URL url) {
        final String basePath = url.getPath().substring(0, url.getPath().length() - BUNDLE_RESOURCE_PATH.length())
                + MESSAGE_RESOURCE_PREFIX;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Base path: {}", basePath);
        }
        Map<String, String> dataMap = ConvertUtils.toMap(url);
        String bundle = dataMap.getOrDefault("bundle", Globals.DEFAULT_VALUE_STRING);
        String[] languages =
                StringUtils.tokenizeToStringArray(dataMap.getOrDefault("languages", Globals.DEFAULT_VALUE_STRING),
                        Globals.DEFAULT_SPLIT_SEPARATOR);
        if (StringUtils.notBlank(bundle) && !CollectionUtils.isEmpty(languages)) {
            for (String languageCode : languages) {
                String resourcePath = basePath + languageCode + ".xml";
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Bundle: {}, resource path: {}", bundle, resourcePath);
                }
                try {
                    boolean result =
                            registerResource(bundle, languageCode, FileUtils.readFileBytes(resourcePath), Boolean.TRUE);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Register bundle: {}, language: {}, result: {}", bundle, languageCode, result);
                    }
                } catch (FileNotFoundException ignored) {
                    LOGGER.error("Load resource path: {} error! ", resourcePath);
                }
            }
        }
    }
    /**
	 * <h3 class="en">Private constructor for MultilingualUtils</h3>
	 * <h3 class="zh-CN">国际化工具集的私有构造方法</h3>
     */
    private MultilingualUtils() {
    }
    /**
	 * <h3 class="en">Convert locale instance to string.</h3>
	 * <h3 class="zh-CN">将语言环境实例转换为字符串。</h3>
     *
     * @param locale    <span class="en">locale instance</span>
     *                  <span class="zh-CN">区域设置实例</span>
     *
     * @return  <span class="en">Converted string</span>
     *          <span class="zh-CN">转换后的字符串</span>
     */
    public static String toLanguageCode(@Nonnull final Locale locale) {
        return Optional.of(locale.getCountry())
                .filter(StringUtils::notBlank)
                .map(countryCode -> locale.getLanguage() + "-" + countryCode)
                .orElse(locale.getLanguage());
    }
    /**
	 * <h3 class="en">Configure default locale</h3>
	 * <h3 class="zh-CN">设置默认语言</h3>
     *
     * @param locale    <span class="en">Default locale instance</span>
     *                  <span class="zh-CN">默认区域设置实例</span>
     */
    public static void defaultLocale(@Nonnull final Locale locale) {
        DEFAULT_LANGUAGE_CODE = toLanguageCode(locale);
    }
    /**
	 * <h3 class="en">Read resource files and register</h3>
	 * <h3 class="zh-CN">读取资源文件并注册</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
     * @param resourcePath  <span class="en">Resource file path</span>
     *                      <span class="zh-CN">资源文件地址</span>
     *
     * @return  <span class="en">Registered result, <code>Boolean.TRUE</code> for success, <code>Boolean.FALSE</code> for failed</span>
     *          <span class="zh-CN">注册结果，成功返回 <code>Boolean.TRUE</code>，失败返回 <code>Boolean.FALSE</code></span>
     */
    public static boolean registerResource(@Nonnull final String bundle, @Nonnull final String languageCode,
                                           @Nonnull final String resourcePath) {
        return registerResource(bundle, languageCode, PropertiesUtils.loadProperties(resourcePath));
    }
    /**
	 * <h3 class="en">Read resource files and register</h3>
	 * <h3 class="zh-CN">读取资源文件并注册</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
	 * @param url 	        <span class="en">URL instance</span>
	 *                      <span class="zh-CN">网络路径</span>
     *
     * @return  <span class="en">Registered result, <code>Boolean.TRUE</code> for success, <code>Boolean.FALSE</code> for failed</span>
     *          <span class="zh-CN">注册结果，成功返回 <code>Boolean.TRUE</code>，失败返回 <code>Boolean.FALSE</code></span>
     */
    public static boolean registerResource(@Nonnull final String bundle, @Nonnull final String languageCode,
                                           @Nonnull final URL url) {
        return registerResource(bundle, languageCode, PropertiesUtils.loadProperties(url));
    }
    /**
	 * <h3 class="en">Read resource files and register</h3>
	 * <h3 class="zh-CN">读取资源文件并注册</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
	 * @param dataBytes 	<span class="en">Message resource data bytes</span>
	 *                      <span class="zh-CN">信息资源文件二进制数组</span>
	 * @param isXML         <span class="en">Data is XML format</span>
	 *              		<span class="zh-CN">数据是XML格式</span>
     *
     * @return  <span class="en">Registered result, <code>Boolean.TRUE</code> for success, <code>Boolean.FALSE</code> for failed</span>
     *          <span class="zh-CN">注册结果，成功返回 <code>Boolean.TRUE</code>，失败返回 <code>Boolean.FALSE</code></span>
     */
    public static boolean registerResource(@Nonnull final String bundle, @Nonnull final String languageCode,
                                           @Nonnull final byte[] dataBytes, final boolean isXML) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes)) {
            return registerResource(bundle, languageCode, byteArrayInputStream, isXML);
        } catch (IOException e) {
            return Boolean.FALSE;
        }
    }
    /**
	 * <h3 class="en">Read resource files and register</h3>
	 * <span class="en">Caution: users need to call the close method for argument inputStream manually.</span>
	 * <h3 class="zh-CN">读取资源文件并注册</h3>
	 * <span class="zh-CN">注意：用户必须手动调用参数 inputStream 的 close 方法。</span>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
	 * @param inputStream 	<span class="en">Input stream instance</span>
	 *              		<span class="zh-CN">输入流实例对象</span>
	 * @param isXML         <span class="en">Data is XML format</span>
	 *              		<span class="zh-CN">数据是XML格式</span>
     *
     * @return  <span class="en">Registered result, <code>Boolean.TRUE</code> for success, <code>Boolean.FALSE</code> for failed</span>
     *          <span class="zh-CN">注册结果，成功返回 <code>Boolean.TRUE</code>，失败返回 <code>Boolean.FALSE</code></span>
     */
    public static boolean registerResource(@Nonnull final String bundle, @Nonnull final String languageCode,
                                           @Nonnull final InputStream inputStream, final boolean isXML) {
        return registerResource(bundle, languageCode, PropertiesUtils.loadProperties(inputStream, isXML));
    }
    /**
	 * <h3 class="en">Retrieve internationalization information content and formatted by given collections</h3>
	 * <h3 class="zh-CN">读取国际化资源信息详情并使用给定的参数集合格式化资源信息</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param messageKey    <span class="en">Message identify key</span>
     *                      <span class="zh-CN">信息识别键值</span>
     * @param collections   <span class="en">given parameters of information formatter</span>
     *                      <span class="zh-CN">用于资源信息格式化的参数</span>
     *
     * @return  <span class="en">Formatted resource information or joined string by character '/' if not found</span>
     *          <span class="zh-CN">格式化的资源信息，如果未找到则返回使用'/'拼接的字符串</span>
     */
    public static String findMessage(final String bundle, final String messageKey, final Object... collections) {
        return findMessage(bundle, DEFAULT_LANGUAGE_CODE, messageKey, collections);
    }
    /**
	 * <h3 class="en">Retrieve internationalization information content and formatted by given collections</h3>
	 * <h3 class="zh-CN">读取国际化资源信息详情并使用给定的参数集合格式化资源信息</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param locale        <span class="en">locale instance</span>
     *                      <span class="zh-CN">区域设置实例</span>
     * @param messageKey    <span class="en">Message identify key</span>
     *                      <span class="zh-CN">信息识别键值</span>
     * @param collections   <span class="en">given parameters of information formatter</span>
     *                      <span class="zh-CN">用于资源信息格式化的参数</span>
     *
     * @return  <span class="en">Formatted resource information or joined string by character '/' if not found</span>
     *          <span class="zh-CN">格式化的资源信息，如果未找到则返回使用'/'拼接的字符串</span>
     */
    public static String findMessage(final String bundle, final Locale locale,
                                     final String messageKey, final Object... collections) {
        return findMessage(bundle, toLanguageCode(locale), messageKey, collections);
    }
    /**
	 * <h3 class="en">Retrieve internationalization information content and formatted by given collections</h3>
	 * <h3 class="zh-CN">读取国际化资源信息详情并使用给定的参数集合格式化资源信息</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
     * @param messageKey    <span class="en">Message identify key</span>
     *                      <span class="zh-CN">信息识别键值</span>
     * @param collections   <span class="en">given parameters of information formatter</span>
     *                      <span class="zh-CN">用于资源信息格式化的参数</span>
     *
     * @return  <span class="en">Formatted resource information or joined string by character '/' if not found</span>
     *          <span class="zh-CN">格式化的资源信息，如果未找到则返回使用'/'拼接的字符串</span>
     */
    public static String findMessage(final String bundle, final String languageCode,
                                     final String messageKey, final Object... collections) {
        if (StringUtils.notBlank(bundle) && StringUtils.notBlank(languageCode)) {
            return Optional.ofNullable(REGISTERED_RESOURCES.get(bundle))
                    .map(bundleMap -> bundleMap.containsKey(languageCode)
                            ? bundleMap.get(languageCode) : bundleMap.get(DEFAULT_LANGUAGE_CODE))
                    .map(messageResource -> messageResource.findMessage(messageKey, collections))
                    .filter(StringUtils::notBlank)
                    .orElse(bundle + Globals.DEFAULT_URL_SEPARATOR + languageCode + Globals.DEFAULT_URL_SEPARATOR + messageKey);
        }
        return messageKey;
    }
    /**
	 * <h3 class="en">Remove bundle resources by given argument bundle if registered.</h3>
	 * <h3 class="zh-CN">根据给定的参数 bundle 移除已注册的国际化信息资源</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     */
    public static void removeBundle(@Nonnull final String bundle) {
        if (StringUtils.notBlank(bundle)) {
            REGISTERED_RESOURCES.remove(bundle);
        }
    }
    /**
	 * <h3 class="en">Remove message resources by given argument languageCode if registered.</h3>
	 * <h3 class="zh-CN">根据给定的参数 languageCode 移除已注册的国际化信息资源</h3>
     *
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
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
	 * <h3 class="en">Remove message resources by given argument bundle and languageCode if registered.</h3>
	 * <h3 class="zh-CN">根据给定的参数 bundle 和 languageCode 移除已注册的国际化信息资源</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
     */
    public static void removeResource(@Nonnull final String bundle, @Nonnull final String languageCode) {
        if (StringUtils.notBlank(bundle) && StringUtils.notBlank(languageCode)) {
            Map<String, MessageResource> bundleMap = REGISTERED_RESOURCES.getOrDefault(bundle, new HashMap<>());
            bundleMap.remove(languageCode);
            REGISTERED_RESOURCES.put(bundle, bundleMap);
        }
    }
    /**
	 * <h3 class="en">Register i18n message resource</h3>
	 * <h3 class="zh-CN">注册国际化信息资源</h3>
     *
     * @param bundle        <span class="en">Resource bundle name</span>
     *                      <span class="zh-CN">资源包名</span>
     * @param languageCode  <span class="en">Resource language code</span>
     *                      <span class="zh-CN">资源语言代码</span>
     * @param properties    <span class="en">Resource information properties instance</span>
     *                      <span class="zh-CN">资源信息属性实例对象</span>
     *
     * @return  <span class="en">Registered result, <code>Boolean.TRUE</code> for success, <code>Boolean.FALSE</code> for failed</span>
     *          <span class="zh-CN">注册结果，成功返回 <code>Boolean.TRUE</code>，失败返回 <code>Boolean.FALSE</code></span>
     */
    private static boolean registerResource(@Nonnull final String bundle, @Nonnull final String languageCode,
                                            @Nonnull final Properties properties) {
        if (StringUtils.isEmpty(bundle) || properties.isEmpty()) {
            return Boolean.FALSE;
        }
        Map<String, MessageResource> bundleMap = REGISTERED_RESOURCES.getOrDefault(bundle, new HashMap<>());
        if (bundleMap.containsKey(languageCode)) {
            LOGGER.warn("Override i18n resource, bundle: {}, language code: {}", bundle, languageCode);
        }
        bundleMap.put(languageCode, new MessageResource(properties));
        REGISTERED_RESOURCES.put(bundle, bundleMap);
        return Boolean.TRUE;
    }
}
