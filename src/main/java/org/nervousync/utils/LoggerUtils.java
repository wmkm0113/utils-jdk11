package org.nervousync.utils;

import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.nervousync.commons.core.Globals;

import java.util.*;

/**
 * The type Logger utils.
 */
public final class LoggerUtils {

    /**
     * Update logger configure.
     *
     * @param rootLevel the root level
     */
    public static void initLoggerConfigure(final Level rootLevel) {
        initLoggerConfigure(rootLevel, new PackageLogger[0]);
    }

    /**
     * Update logger configure.
     *
     * @param rootLevel      the root level
     * @param packageLoggers the package loggers
     */
    public static void initLoggerConfigure(final Level rootLevel, final PackageLogger... packageLoggers) {
        initLoggerConfigure(Globals.DEFAULT_VALUE_STRING, rootLevel, packageLoggers);
    }

    /**
     * Update logger configure.
     *
     * @param basePath       the base path
     * @param rootLevel      the root level
     * @param packageLoggers the package loggers
     */
    public static void initLoggerConfigure(final String basePath, final Level rootLevel,
                                           final PackageLogger... packageLoggers) {
        generateConfiguration(generateConfig(basePath, rootLevel, packageLoggers))
                .ifPresent(configuration -> {
                    LoggerContext loggerContext = (LoggerContext) LogManager.getContext(Boolean.FALSE);
                    loggerContext.setConfiguration(configuration);
                    loggerContext.updateLoggers();
                });
    }

    public static PackageLogger newLogger(final String packageName, final Level loggerLevel) {
        return new PackageLogger(packageName, loggerLevel);
    }

    /**
     * The type Package logger.
     */
    public static final class PackageLogger {
        private final String packageName;
        private final Level loggerLevel;

        private PackageLogger(final String packageName, final Level loggerLevel) {
            this.packageName = packageName;
            this.loggerLevel = loggerLevel;
        }

        /**
         * Gets package name.
         *
         * @return the package name
         */
        public String getPackageName() {
            return packageName;
        }

        /**
         * Gets logger level.
         *
         * @return the logger level
         */
        public Level getLoggerLevel() {
            return loggerLevel;
        }
    }

    private static Optional<Configuration> generateConfiguration(final LogConfig logConfig) {
        if (logConfig == null || logConfig.getAppenderConfigures() == null) {
            return Optional.empty();
        }
        final ConfigurationBuilder<BuiltConfiguration> configurationBuilder =
                ConfigurationBuilderFactory.newConfigurationBuilder();
        configurationBuilder.setStatusLevel(Level.ERROR);
        Optional<LayoutComponentBuilder> layoutComponentBuilder = layoutBuilder(logConfig.getPatternLayoutConfigure());

        logConfig.getAppenderConfigures()
                .stream()
                .filter(appenderConfigure ->
                        StringUtils.notBlank(appenderConfigure.getAppenderName())
                                && StringUtils.notBlank(appenderConfigure.getAppenderPlugin()))
                .forEach(appenderConfigure -> {
                    AppenderComponentBuilder appenderComponentBuilder =
                            configurationBuilder.newAppender(appenderConfigure.getAppenderName(),
                                    appenderConfigure.getAppenderPlugin());
                    Optional.ofNullable(appenderConfigure.getAppenderAttributes())
                            .ifPresent(appenderAttributes ->
                                    appenderAttributes.forEach(appenderComponentBuilder::addAttribute));
                    Optional.ofNullable(appenderConfigure.getAppenderComponents())
                            .ifPresent(appenderComponents ->
                                    appenderComponents.forEach(componentConfigure ->
                                            initComponentConfig(appenderComponentBuilder, componentConfigure)));
                    if (appenderConfigure.getPatternLayoutConfigure() == null) {
                        layoutComponentBuilder.ifPresent(appenderComponentBuilder::add);
                    } else {
                        layoutBuilder(appenderConfigure.getPatternLayoutConfigure())
                                .ifPresent(appenderComponentBuilder::add);
                    }
                    configurationBuilder.add(appenderComponentBuilder);
                });

        Optional.ofNullable(logConfig.getLoggerConfigures())
                .ifPresent(loggerConfigures ->
                        loggerConfigures.stream()
                                .filter(loggerConfigure ->
                                        StringUtils.notBlank(loggerConfigure.getPackageName())
                                                && loggerConfigure.getAppenderNames() != null
                                                && loggerConfigure.getAppenderNames().size() > 0)
                                .forEach(loggerConfigure -> {
                                    LoggerComponentBuilder loggerComponentBuilder =
                                            configurationBuilder.newLogger(loggerConfigure.getPackageName(),
                                                    loggerConfigure.getLoggerLevel());
                                    loggerComponentBuilder.addAttribute("additivity", Boolean.FALSE);
                                    loggerConfigure.getAppenderNames().forEach(appenderName ->
                                            loggerComponentBuilder.add(configurationBuilder.newAppenderRef(appenderName)));
                                    configurationBuilder.add(loggerComponentBuilder);
                                }));

        RootLoggerConfigure rootLoggerConfigure = logConfig.getRootLoggerConfigure();
        if (rootLoggerConfigure.getAppenderNames() != null && rootLoggerConfigure.getAppenderNames().size() > 0) {
            RootLoggerComponentBuilder rootLoggerComponentBuilder =
                    configurationBuilder.newRootLogger(rootLoggerConfigure.getLoggerLevel());
            rootLoggerConfigure.getAppenderNames()
                    .forEach(appenderName ->
                            rootLoggerComponentBuilder.add(configurationBuilder.newAppenderRef(appenderName)));
            configurationBuilder.add(rootLoggerComponentBuilder);
        }
        return Optional.of(configurationBuilder.build());
    }

    private static LogConfig generateConfig(final String basePath, final Level rootLevel,
                                            final PackageLogger... packageLoggers) {
        PatternLayoutConfigure patternLayoutConfigure = new PatternLayoutConfigure();
        Map<String, Object> layoutAttributes = new HashMap<>();
        layoutAttributes.put("pattern", "%d{yyyy-MM-dd HH:mm:ss} %p [%t] [%c:%L]: %m%n");
        patternLayoutConfigure.setAttributesMap(layoutAttributes);

        List<AppenderConfigure> appenderConfigures = new ArrayList<>();

        AppenderConfigure appenderConfigure = new AppenderConfigure("Console", "Console");
        Map<String, Object> consoleAppenderAttributes = new HashMap<>();
        consoleAppenderAttributes.put("target", ConsoleAppender.Target.SYSTEM_OUT);
        appenderConfigure.setAppenderAttributes(consoleAppenderAttributes);
        appenderConfigures.add(appenderConfigure);

        RootLoggerConfigure rootLoggerConfigure = new RootLoggerConfigure(rootLevel);

        List<String> appenderNames;
        if (StringUtils.notBlank(basePath)) {
            FileUtils.makeDir(basePath);
            AppenderConfigure fileAppenderConfigure = new AppenderConfigure("File", "File");
            Map<String, Object> fileAppenderAttributes = new HashMap<>();
            fileAppenderAttributes.put("fileName", basePath + Globals.DEFAULT_LOG_FILE_PATH);
            fileAppenderConfigure.setAppenderAttributes(fileAppenderAttributes);
            appenderConfigures.add(fileAppenderConfigure);
            appenderNames = List.of("Console", "File");
        } else {
            appenderNames = List.of("Console");
        }
        rootLoggerConfigure.setAppenderNames(appenderNames);

        LogConfig logConfig = new LogConfig(patternLayoutConfigure);
        logConfig.setAppenderConfigures(appenderConfigures);
        logConfig.setRootLoggerConfigure(rootLoggerConfigure);
        if (packageLoggers != null) {
            List<LoggerConfigure> loggerConfigures = new ArrayList<>();
            Arrays.stream(packageLoggers)
                    .filter(packageLogger -> StringUtils.notBlank(packageLogger.getPackageName()))
                    .forEach(packageLogger -> {
                        LoggerConfigure loggerConfigure = new LoggerConfigure(packageLogger);
                        loggerConfigure.setAppenderNames(appenderNames);
                        loggerConfigures.add(loggerConfigure);
                    });
            logConfig.setLoggerConfigures(loggerConfigures);
        }
        return logConfig;
    }

    private static void initComponentConfig(final ComponentBuilder<?> parentBuilder,
                                            final ComponentConfigure componentConfigure) {
        if (parentBuilder == null || componentConfigure == null
                || StringUtils.isEmpty(componentConfigure.getComponentPlugin())) {
            return;
        }

        final ComponentBuilder<?> componentBuilder =
                ConfigurationBuilderFactory.newConfigurationBuilder()
                        .newComponent(componentConfigure.getComponentPlugin());

        if (componentConfigure.getComponentAttributes() != null) {
            componentConfigure.getComponentAttributes().forEach(componentBuilder::addAttribute);
        }

        if (componentConfigure.getChildComponents() != null) {
            componentConfigure.getChildComponents()
                    .forEach(childComponentConfig ->
                            initComponentConfig(componentBuilder, childComponentConfig));
        }

        parentBuilder.addComponent(componentBuilder);
    }

    private static Optional<LayoutComponentBuilder> layoutBuilder(final PatternLayoutConfigure patternLayoutConfigure) {
        if (patternLayoutConfigure == null) {
            return Optional.empty();
        }
        LayoutComponentBuilder layoutComponentBuilder =
                ConfigurationBuilderFactory.newConfigurationBuilder().newLayout("PatternLayout");
        if (patternLayoutConfigure.getAttributesMap() != null) {
            patternLayoutConfigure.getAttributesMap().forEach(layoutComponentBuilder::addAttribute);
        }
        if (patternLayoutConfigure.getLoggerComponents() != null) {
            patternLayoutConfigure.getLoggerComponents()
                    .forEach(componentConfigure ->
                            initComponentConfig(layoutComponentBuilder, componentConfigure));
        }
        return Optional.of(layoutComponentBuilder);
    }

    /**
     * The type Log config.
     */
    private static final class LogConfig {

        private final PatternLayoutConfigure patternLayoutConfigure;
        private List<AppenderConfigure> appenderConfigures;
        private List<LoggerConfigure> loggerConfigures;
        private RootLoggerConfigure rootLoggerConfigure;

        /**
         * Instantiates a new Log config.
         *
         * @param patternLayoutConfigure the pattern layout configures
         */
        public LogConfig(PatternLayoutConfigure patternLayoutConfigure) {
            this.patternLayoutConfigure = patternLayoutConfigure;
        }

        /**
         * Gets pattern layout configure.
         *
         * @return the pattern layout configures
         */
        public PatternLayoutConfigure getPatternLayoutConfigure() {
            return patternLayoutConfigure;
        }

        /**
         * Gets appender configures.
         *
         * @return the appender configured
         */
        public List<AppenderConfigure> getAppenderConfigures() {
            return appenderConfigures;
        }

        /**
         * Sets appender configures.
         *
         * @param appenderConfigures the appender configured
         */
        public void setAppenderConfigures(List<AppenderConfigure> appenderConfigures) {
            this.appenderConfigures = appenderConfigures;
        }

        /**
         * Gets logger configures.
         *
         * @return the logger configures
         */
        public List<LoggerConfigure> getLoggerConfigures() {
            return loggerConfigures;
        }

        /**
         * Sets logger configures.
         *
         * @param loggerConfigures the logger configures
         */
        public void setLoggerConfigures(List<LoggerConfigure> loggerConfigures) {
            this.loggerConfigures = loggerConfigures;
        }

        /**
         * Gets root logger configure.
         *
         * @return the root logger configured
         */
        public RootLoggerConfigure getRootLoggerConfigure() {
            return rootLoggerConfigure;
        }

        /**
         * Sets root logger configure.
         *
         * @param rootLoggerConfigure the root logger configured
         */
        public void setRootLoggerConfigure(RootLoggerConfigure rootLoggerConfigure) {
            this.rootLoggerConfigure = rootLoggerConfigure;
        }
    }

    /**
     * The type Pattern layout configures.
     */
    private static final class PatternLayoutConfigure {

        private Map<String, Object> attributesMap;
        private List<ComponentConfigure> loggerComponents;

        /**
         * Instantiates a new Pattern layout configure.
         */
        public PatternLayoutConfigure() {
        }

        /**
         * Gets the attribute map.
         *
         * @return the attribute map
         */
        public Map<String, Object> getAttributesMap() {
            return attributesMap;
        }

        /**
         * Sets attributes map.
         *
         * @param attributesMap the attribute map
         */
        public void setAttributesMap(Map<String, Object> attributesMap) {
            this.attributesMap = attributesMap;
        }

        /**
         * Gets logger components.
         *
         * @return the logger components
         */
        public List<ComponentConfigure> getLoggerComponents() {
            return loggerComponents;
        }

        /**
         * Sets logger components.
         *
         * @param loggerComponents the logger components
         */
        public void setLoggerComponents(List<ComponentConfigure> loggerComponents) {
            this.loggerComponents = loggerComponents;
        }
    }

    /**
     * The type Appender configured.
     */
    private static final class AppenderConfigure {

        private final String appenderName;
        private final String appenderPlugin;
        private Map<String, Object> appenderAttributes;
        private List<ComponentConfigure> appenderComponents;
        private PatternLayoutConfigure patternLayoutConfigure;

        /**
         * Instantiates a new Appender configure.
         *
         * @param appenderName   the appender name
         * @param appenderPlugin the appender plugin
         */
        public AppenderConfigure(final String appenderName, final String appenderPlugin) {
            this.appenderName = appenderName;
            this.appenderPlugin = appenderPlugin;
        }

        /**
         * Gets appender name.
         *
         * @return the appender name
         */
        public String getAppenderName() {
            return appenderName;
        }

        /**
         * Gets appender plugin.
         *
         * @return the appender plugin
         */
        public String getAppenderPlugin() {
            return appenderPlugin;
        }

        /**
         * Gets appender attributes.
         *
         * @return the appender attributes
         */
        public Map<String, Object> getAppenderAttributes() {
            return appenderAttributes;
        }

        /**
         * Sets appender attributes.
         *
         * @param appenderAttributes the appender attributes
         */
        public void setAppenderAttributes(Map<String, Object> appenderAttributes) {
            this.appenderAttributes = appenderAttributes;
        }

        /**
         * Gets appender components.
         *
         * @return the appender components
         */
        public List<ComponentConfigure> getAppenderComponents() {
            return appenderComponents;
        }

        /**
         * Sets appender components.
         *
         * @param appenderComponents the appender components
         */
        public void setAppenderComponents(List<ComponentConfigure> appenderComponents) {
            this.appenderComponents = appenderComponents;
        }

        /**
         * Gets pattern layout configure.
         *
         * @return the pattern layout configures
         */
        public PatternLayoutConfigure getPatternLayoutConfigure() {
            return patternLayoutConfigure;
        }

        /**
         * Sets pattern layout configure.
         *
         * @param patternLayoutConfigure the pattern layout configures
         */
        public void setPatternLayoutConfigure(PatternLayoutConfigure patternLayoutConfigure) {
            this.patternLayoutConfigure = patternLayoutConfigure;
        }
    }

    /**
     * The type Root logger configured.
     */
    private static final class RootLoggerConfigure {

        private final Level loggerLevel;
        private List<String> appenderNames;

        /**
         * Instantiates a new Root logger configure.
         *
         * @param loggerLevel the logger level
         */
        public RootLoggerConfigure(final Level loggerLevel) {
            this.loggerLevel = loggerLevel;
        }

        /**
         * Gets logger level.
         *
         * @return the logger level
         */
        public Level getLoggerLevel() {
            return loggerLevel;
        }

        /**
         * Gets appender names.
         *
         * @return the appender names
         */
        public List<String> getAppenderNames() {
            return appenderNames;
        }

        /**
         * Sets appender names.
         *
         * @param appenderNames the appender names
         */
        public void setAppenderNames(List<String> appenderNames) {
            this.appenderNames = appenderNames;
        }
    }

    /**
     * The type Logger configured.
     */
    private static final class LoggerConfigure {

        private final String packageName;
        private final Level loggerLevel;
        private List<String> appenderNames;

        /**
         * Instantiates a new Logger configure.
         *
         * @param packageLogger the package logger
         */
        public LoggerConfigure(final PackageLogger packageLogger) {
            this.packageName = packageLogger.getPackageName();
            this.loggerLevel = packageLogger.getLoggerLevel();
        }

        /**
         * Gets package name.
         *
         * @return the package name
         */
        public String getPackageName() {
            return packageName;
        }

        /**
         * Gets logger level.
         *
         * @return the logger level
         */
        public Level getLoggerLevel() {
            return loggerLevel;
        }

        /**
         * Gets appender names.
         *
         * @return the appender names
         */
        public List<String> getAppenderNames() {
            return appenderNames;
        }

        /**
         * Sets appender names.
         *
         * @param appenderNames the appender names
         */
        public void setAppenderNames(List<String> appenderNames) {
            this.appenderNames = appenderNames;
        }
    }

    /**
     * The type Components configure.
     */
    private static final class ComponentConfigure {

        private final String componentPlugin;
        private Map<String, Object> componentAttributes;
        private List<ComponentConfigure> childComponents;

        /**
         * Instantiates a new Component configure.
         *
         * @param componentPlugin the component plugin
         */
        public ComponentConfigure(final String componentPlugin) {
            this.componentPlugin = componentPlugin;
            this.componentAttributes = new HashMap<>();
            this.childComponents = new ArrayList<>();
        }

        /**
         * Gets component plugin.
         *
         * @return the component plugin
         */
        public String getComponentPlugin() {
            return componentPlugin;
        }

        /**
         * Gets component attributes.
         *
         * @return the component attributes
         */
        public Map<String, Object> getComponentAttributes() {
            return componentAttributes;
        }

        /**
         * Sets component attributes.
         *
         * @param componentAttributes the component attributes
         */
        public void setComponentAttributes(Map<String, Object> componentAttributes) {
            this.componentAttributes = componentAttributes;
        }

        /**
         * Gets child components.
         *
         * @return the child components
         */
        public List<ComponentConfigure> getChildComponents() {
            return childComponents;
        }

        /**
         * Sets child components.
         *
         * @param childComponents the child components
         */
        public void setChildComponents(List<ComponentConfigure> childComponents) {
            this.childComponents = childComponents;
        }
    }
}
