/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.builder.xml;

import cn.xianyijun.orm.builder.BaseBuilder;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.datasource.DataSourceFactory;
import cn.xianyijun.orm.datasource.PooledDataSourceFactory;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.io.Resources;
import cn.xianyijun.orm.io.VFS;
import cn.xianyijun.orm.mapping.Environment;
import cn.xianyijun.orm.parse.XMLMapperEntityResolver;
import cn.xianyijun.orm.parse.XNode;
import cn.xianyijun.orm.parse.XPathParser;
import cn.xianyijun.orm.reflection.MetaClass;
import cn.xianyijun.orm.reflection.reflector.DefaultReflectorFactory;
import cn.xianyijun.orm.reflection.reflector.ReflectorFactory;
import cn.xianyijun.orm.session.AutoMappingBehavior;
import cn.xianyijun.orm.session.LocalCacheScope;
import cn.xianyijun.orm.transaction.TransactionFactory;
import cn.xianyijun.orm.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * The type Xml config builder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class XMLConfigBuilder extends BaseBuilder {
    private boolean parsed;
    private XPathParser parser;
    private String environment;
    private ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();


    /**
     * Instantiates a new Xml config builder.
     *
     * @param reader      the reader
     * @param environment the environment
     * @param properties  the properties
     */
    public XMLConfigBuilder(Reader reader, String environment, Properties properties) {
        this(new XPathParser(reader, true, properties, new XMLMapperEntityResolver()), environment, properties);
    }

    /**
     * Instantiates a new Xml config builder.
     *
     * @param inputStream the input stream
     * @param environment the environment
     * @param props       the props
     */
    public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
        this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
    }

    private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
        super(new StatementHandler.Configuration());
        this.configuration.setVariables(props);
        this.parsed = false;
        this.environment = environment;
        this.parser = parser;
    }

    /**
     * Parse configuration.
     *
     * @return the configuration
     */
    public StatementHandler.Configuration parse() {
        if (parsed) {
            throw new BuilderException(" the xml only can parse one time");
        }
        parsed = true;
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            propertiesElement(root.evalNode("properties"));
            Properties settings = settingsAsProperties(root.evalNode("settings"));
            loadCustomVfs(settings);
            settingsElement(settings);
            environmentsElement(root.evalNode("environments"));
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    private void mapperElement(XNode parent) throws IOException {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                if ("package".equals(child.getName())) {
                    String mapperPackage = child.getStringAttribute("name");
                    configuration.addMappers(mapperPackage);
                } else {
                    String resource = child.getStringAttribute("resource");
                    String url = child.getStringAttribute("url");
                    String mapperClass = child.getStringAttribute("class");
                    if (resource != null && url == null && mapperClass == null) {
                        InputStream inputStream = Resources.getResourceAsStream(resource);
                        XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(inputStream, configuration, resource,
                                configuration.getSqlFragments());
                        mapperBuilder.parse();
                    } else if (resource == null && url != null && mapperClass == null) {
                        InputStream inputStream = Resources.getUrlAsStream(url);
                        XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(inputStream, configuration, url,
                                configuration.getSqlFragments());
                        mapperBuilder.parse();
                    } else if (resource == null && url == null && mapperClass != null) {
                        Class<?> mapperInterface = resolveClass(mapperClass);
                        configuration.addMapper(mapperInterface);
                    } else {
                        throw new BuilderException(
                                "A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                }
            }
        }
    }

    private void environmentsElement(XNode context) throws Exception {
        if (context != null) {
            if (environment == null) {
                environment = context.getStringAttribute("default");
            }
            for (XNode child : context.getChildren()) {
                String id = child.getStringAttribute("id");
                if (isSpecifiedEnvironment(id)) {
                    TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                    DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
                    DataSource dataSource = dsFactory.getDataSource();
                    Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory)
                            .dataSource(dataSource);
                    configuration.setEnvironment(environmentBuilder.build());
                }
            }
        }
    }

    private void settingsElement(Properties props) {
        configuration.setAutoMappingBehavior(
                AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
        configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
        configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
        configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), true));
        configuration
                .setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
        configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
        configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
        configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
        configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
        configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
        configuration.setLazyLoadTriggerMethods(
                stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
        configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
        configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
        configuration.setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), true));
    }

    private void loadCustomVfs(Properties props) throws ClassNotFoundException {
        String value = props.getProperty("vfsImpl");
        if (value != null) {
            String[] clazzes = value.split(",");
            for (String clazz : clazzes) {
                if (!clazz.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends VFS> vfsImpl = (Class<? extends VFS>) resolveClass(clazz);
                    configuration.setVfsImpl(vfsImpl);
                }
            }
        }
    }

    private Properties settingsAsProperties(XNode context) {
        if (context == null) {
            return new Properties();
        }
        Properties props = context.getChildrenAsProperties();
        MetaClass metaConfig = MetaClass.forClass(StatementHandler.Configuration.class, localReflectorFactory);
        for (Object key : props.keySet()) {
            if (!metaConfig.hasSetter(String.valueOf(key))) {
                throw new BuilderException(
                        "The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
            }
        }
        return props;
    }

    private void propertiesElement(XNode context) throws IOException {
        if (context != null) {
            Properties defaults = context.getChildrenAsProperties();
            String resource = context.getStringAttribute("resource");
            String url = context.getStringAttribute("url");
            if (resource != null && url != null) {
                throw new BuilderException(
                        "The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
            }
            if (resource != null) {
                defaults.putAll(Resources.getResourceAsProperties(resource));
            }
            if (url != null) {
                defaults.putAll(Resources.getUrlAsProperties(url));
            }

            Properties vars = configuration.getVariables();
            if (vars != null) {
                defaults.putAll(vars);
            }
            parser.setVariables(defaults);
            configuration.setVariables(defaults);
        }
    }

    /**
     * Boolean value of boolean.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the boolean
     */
//==============================================================
    protected Boolean booleanValueOf(String value, Boolean defaultValue) {
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    /**
     * Integer value of integer.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the integer
     */
    protected Integer integerValueOf(String value, Integer defaultValue) {
        return value == null ? defaultValue : Integer.valueOf(value);
    }

    /**
     * String set value of set.
     *
     * @param value        the value
     * @param defaultValue the default value
     * @return the set
     */
    protected Set<String> stringSetValueOf(String value, String defaultValue) {
        value = value == null ? defaultValue : value;
        return new HashSet<>(Arrays.asList(value.split(",")));
    }

    private boolean isSpecifiedEnvironment(String id) {
        if (environment == null) {
            throw new BuilderException("No environment specified.");
        } else if (id == null) {
            throw new BuilderException("Environment requires an id attribute.");
        } else if (environment.equals(id)) {
            return true;
        }
        return false;
    }

    private TransactionFactory transactionManagerElement(XNode context) throws IllegalAccessException, InstantiationException {
        TransactionFactory factory = (TransactionFactory) JdbcTransactionFactory.class.newInstance();
        if (context != null) {
            Properties props = context.getChildrenAsProperties();
            factory.setProperties(props);
        }
        return factory;
    }

    private DataSourceFactory dataSourceElement(XNode context) throws Exception {
        if (context != null) {
            Properties props = context.getChildrenAsProperties();
            DataSourceFactory factory = PooledDataSourceFactory.class.newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }
}
