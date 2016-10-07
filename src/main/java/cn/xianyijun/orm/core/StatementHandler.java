/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import cn.xianyijun.orm.binding.MapperRegistry;
import cn.xianyijun.orm.builder.ResultMapResolver;
import cn.xianyijun.orm.builder.xml.XMLStatementBuilder;
import cn.xianyijun.orm.cache.Cache;
import cn.xianyijun.orm.cache.CacheRefResolver;
import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.executor.SimpleExecutor;
import cn.xianyijun.orm.executor.keygen.KeyGenerator;
import cn.xianyijun.orm.executor.loader.JavassistProxyFactory;
import cn.xianyijun.orm.executor.loader.ProxyFactory;
import cn.xianyijun.orm.executor.parameter.ParameterHandler;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.executor.resultset.DefaultResultSetHandler;
import cn.xianyijun.orm.executor.statement.RoutingStatementHandler;
import cn.xianyijun.orm.io.VFS;
import cn.xianyijun.orm.mapping.Environment;
import cn.xianyijun.orm.mapping.ParameterMap;
import cn.xianyijun.orm.mapping.ParameterMapping;
import cn.xianyijun.orm.mapping.ResultMap;
import cn.xianyijun.orm.parse.XNode;
import cn.xianyijun.orm.reflection.DefaultObjectFactory;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.reflection.ObjectFactory;
import cn.xianyijun.orm.reflection.property.PropertyTokenizer;
import cn.xianyijun.orm.reflection.reflector.DefaultReflectorFactory;
import cn.xianyijun.orm.reflection.reflector.ReflectorFactory;
import cn.xianyijun.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import cn.xianyijun.orm.reflection.wrapper.ObjectWrapperFactory;
import cn.xianyijun.orm.script.LanguageDriver;
import cn.xianyijun.orm.script.LanguageDriverRegistry;
import cn.xianyijun.orm.script.defaults.RawLanguageDriver;
import cn.xianyijun.orm.script.xml.XMLLanguageDriver;
import cn.xianyijun.orm.session.AutoMappingBehavior;
import cn.xianyijun.orm.session.LocalCacheScope;
import cn.xianyijun.orm.session.RowBounds;
import cn.xianyijun.orm.transaction.Transaction;
import cn.xianyijun.orm.type.TypeAliasRegistry;
import cn.xianyijun.orm.type.TypeHandlerRegistry;

/**
 * The interface Statement handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface StatementHandler {

    /**
     * Prepare statement.
     *
     * @param connection         the connection
     * @param transactionTimeout the transaction timeout
     * @return the statement
     * @throws SQLException the sql exception
     */
    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

    /**
     * Parameterize.
     *
     * @param statement the statement
     * @throws SQLException the sql exception
     */
    void parameterize(Statement statement) throws SQLException;

    /**
     * Update int.
     *
     * @param statement the statement
     * @return the int
     * @throws SQLException the sql exception
     */
    int update(Statement statement) throws SQLException;

    /**
     * Query list.
     *
     * @param <E>           the type parameter
     * @param statement     the statement
     * @param resultHandler the result handler
     * @return the list
     * @throws SQLException the sql exception
     */
    <E> List<E> query(Statement statement, ResultHandler<?> resultHandler) throws SQLException;

    /**
     * Query cursor cursor.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @return the cursor
     * @throws SQLException the sql exception
     */
    <E> Cursor<E> queryCursor(Statement statement) throws SQLException;

    /**
     * Gets bound sql.
     *
     * @return the bound sql
     */
    BoundSql getBoundSql();

    /**
     * Gets parameter handler.
     *
     * @return the parameter handler
     */
    ParameterHandler getParameterHandler();

	/**
     * The type Bound sql.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
	class BoundSql {
        private String sql;
        private List<ParameterMapping> parameterMappings;
        private Object parameterObject;
        private Map<String, Object> additionalParameters;
        private MetaObject metaParameters;

        /**
         * Instantiates a new Bound sql.
         *
         * @param configuration     the configuration
         * @param sql               the sql
         * @param parameterMappings the parameter mappings
         * @param parameterObject   the parameter object
         */
        public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings,
						Object parameterObject) {
            this.sql = sql;
            this.parameterMappings = parameterMappings;
            this.parameterObject = parameterObject;
            this.additionalParameters = new HashMap<>();
            this.metaParameters = configuration.newMetaObject(additionalParameters);
        }

        /**
         * Gets sql.
         *
         * @return the sql
         */
        public String getSql() {
            return sql;
        }

        /**
         * Gets parameter mappings.
         *
         * @return the parameter mappings
         */
        public List<ParameterMapping> getParameterMappings() {
            return parameterMappings;
        }

        /**
         * Gets parameter object.
         *
         * @return the parameter object
         */
        public Object getParameterObject() {
            return parameterObject;
        }

        /**
         * Has additional parameter boolean.
         *
         * @param name the name
         * @return the boolean
         */
        public boolean hasAdditionalParameter(String name) {
            String paramName = new PropertyTokenizer(name).getName();
            return additionalParameters.containsKey(paramName);
        }

        /**
         * Sets additional parameter.
         *
         * @param name  the name
         * @param value the value
         */
        public void setAdditionalParameter(String name, Object value) {
            metaParameters.setValue(name, value);
        }

        /**
         * Gets additional parameter.
         *
         * @param name the name
         * @return the additional parameter
         */
        public Object getAdditionalParameter(String name) {
            return metaParameters.getValue(name);
        }
    }

	/**
     * The type Configuration.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
	class Configuration {
        /**
         * Instantiates a new Configuration.
         *
         * @param environment the environment
         */
        public Configuration(Environment environment) {
            this();
            this.environment = environment;
        }

        /**
         * Instantiates a new Configuration.
         */
        public Configuration() {
            languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
            languageRegistry.register(RawLanguageDriver.class);
        }

        /**
         * The Reflector factory.
         */
    //=============================obejctFactory=========================
        protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        /**
         * The Object factory.
         */
        protected ObjectFactory objectFactory = new DefaultObjectFactory();
        /**
         * The Object wrapper factory.
         */
        protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
        /**
         * The Configuration factory.
         */
        protected Class<?> configurationFactory;
        /**
         * The Proxy factory.
         */
        protected ProxyFactory proxyFactory = new JavassistProxyFactory();

        /**
         * The Language registry.
         */
    //==============================map========================
        protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();
        /**
         * The Type alias registry.
         */
        protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
        /**
         * The Type handler registry.
         */
        protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
        /**
         * The Environment.
         */
    //=========================================================
        protected Environment environment;
        /**
         * The Variables.
         */
        protected Properties variables = new Properties();
        /**
         * The Mapper registry.
         */
        protected final MapperRegistry mapperRegistry = new MapperRegistry();
        /**
         * The Loaded resources.
         */
        protected final Set<String> loadedResources = new HashSet<>();
        /**
         * The Result maps.
         */
        protected final Map<String, ResultMap> resultMaps = new StrictMap<>("Result Maps collection");
        /**
         * The Sql fragments.
         */
        protected final Map<String, XNode> sqlFragments = new StrictMap<>(
                "XML fragments parsed from previous mappers");
        /**
         * The Mapped statements.
         */
        protected final Map<String, MappedStatement> mappedStatements = new StrictMap<>(
                "Mapped Statements collection");
        /**
         * The Caches.
         */
        protected final Map<String, Cache> caches = new StrictMap<>("Caches collection");
        /**
         * The Parameter maps.
         */
        protected final Map<String, ParameterMap> parameterMaps = new StrictMap<>("Parameter Maps collection");
        /**
         * The Key generators.
         */
        protected final Map<String, KeyGenerator> keyGenerators = new StrictMap<>("Key Generators collection");

        /**
         * The Incomplete statements.
         */
        protected final Collection<XMLStatementBuilder> incompleteStatements = new LinkedList<>();
        /**
         * The Incomplete cache refs.
         */
        protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<>();
        /**
         * The Incomplete result maps.
         */
        protected final Collection<ResultMapResolver> incompleteResultMaps = new LinkedList<>();
        /**
         * The Cache ref map.
         */
        protected final Map<String, String> cacheRefMap = new HashMap<>();
        //============================properties===============================================

        /**
         * The Safe row bounds enabled.
         */
        protected boolean safeRowBoundsEnabled = false;
        /**
         * The Safe result handler enabled.
         */
        protected boolean safeResultHandlerEnabled = true;
        /**
         * The Map underscore to camel case.
         */
        protected boolean mapUnderscoreToCamelCase = false;
        /**
         * The Aggressive lazy loading.
         */
        protected boolean aggressiveLazyLoading = true;
        /**
         * The Multiple result sets enabled.
         */
        protected boolean multipleResultSetsEnabled = true;
        /**
         * The Use generated keys.
         */
        protected boolean useGeneratedKeys = false;
        /**
         * The Use column label.
         */
        protected boolean useColumnLabel = true;
        /**
         * The Cache enabled.
         */
        protected boolean cacheEnabled = true;
        /**
         * The Call setters on nulls.
         */
        protected boolean callSettersOnNulls = false;
        /**
         * The Use actual param name.
         */
        protected boolean useActualParamName = true;
        /**
         * The Vfs.
         */
        protected Class<? extends VFS> vfsImpl;
        /**
         * The Local cache scope.
         */
        protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;

        /**
         * The Lazy load trigger methods.
         */
        protected Set<String> lazyLoadTriggerMethods = new HashSet<>(
                Arrays.asList(new String[]{"equals", "clone", "hashCode", "toString"}));
        /**
         * The Default statement timeout.
         */
        protected Integer defaultStatementTimeout;
        /**
         * The Default fetch size.
         */
        protected Integer defaultFetchSize;
        /**
         * The Auto mapping behavior.
         */
        protected AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;
        /**
         * The Lazy loading enabled.
         */
        protected boolean lazyLoadingEnabled = false;
        /**
         * The Database id.
         */
        protected String databaseId;

        /**
         * Gets result map names.
         *
         * @return the result map names
         */
    //=========================================================
        public Collection<String> getResultMapNames() {
            return resultMaps.keySet();
        }

        /**
         * Gets result maps.
         *
         * @return the result maps
         */
        public Collection<ResultMap> getResultMaps() {
            return resultMaps.values();
        }

        /**
         * Gets result map.
         *
         * @param id the id
         * @return the result map
         */
        public ResultMap getResultMap(String id) {
            return resultMaps.get(id);
        }

        /**
         * Has result map boolean.
         *
         * @param id the id
         * @return the boolean
         */
        public boolean hasResultMap(String id) {
            return resultMaps.containsKey(id);
        }

        /**
         * Add parameter map.
         *
         * @param pm the pm
         */
        public void addParameterMap(ParameterMap pm) {
            parameterMaps.put(pm.getId(), pm);
        }

        /**
         * Gets parameter map names.
         *
         * @return the parameter map names
         */
        public Collection<String> getParameterMapNames() {
            return parameterMaps.keySet();
        }

        /**
         * Gets parameter maps.
         *
         * @return the parameter maps
         */
        public Collection<ParameterMap> getParameterMaps() {
            return parameterMaps.values();
        }

        /**
         * Gets parameter map.
         *
         * @param id the id
         * @return the parameter map
         */
        public ParameterMap getParameterMap(String id) {
            return parameterMaps.get(id);
        }

        /**
         * Has parameter map boolean.
         *
         * @param id the id
         * @return the boolean
         */
        public boolean hasParameterMap(String id) {
            return parameterMaps.containsKey(id);
        }

        /**
         * Add mapped statement.
         *
         * @param ms the ms
         */
        public void addMappedStatement(MappedStatement ms) {
            mappedStatements.put(ms.getId(), ms);
        }

        /**
         * Gets mapped statement names.
         *
         * @return the mapped statement names
         */
        public Collection<String> getMappedStatementNames() {
            buildAllStatements();
            return mappedStatements.keySet();
        }

        /**
         * Gets mapped statements.
         *
         * @return the mapped statements
         */
        public Collection<MappedStatement> getMappedStatements() {
            buildAllStatements();
            return mappedStatements.values();
        }

        /**
         * Gets incomplete statements.
         *
         * @return the incomplete statements
         */
        public Collection<XMLStatementBuilder> getIncompleteStatements() {
            return incompleteStatements;
        }

        /**
         * Add incomplete statement.
         *
         * @param incompleteStatement the incomplete statement
         */
        public void addIncompleteStatement(XMLStatementBuilder incompleteStatement) {
            incompleteStatements.add(incompleteStatement);
        }

        /**
         * Gets incomplete cache refs.
         *
         * @return the incomplete cache refs
         */
        public Collection<CacheRefResolver> getIncompleteCacheRefs() {
            return incompleteCacheRefs;
        }

        /**
         * Add incomplete cache ref.
         *
         * @param incompleteCacheRef the incomplete cache ref
         */
        public void addIncompleteCacheRef(CacheRefResolver incompleteCacheRef) {
            incompleteCacheRefs.add(incompleteCacheRef);
        }

        /**
         * Gets incomplete result maps.
         *
         * @return the incomplete result maps
         */
        public Collection<ResultMapResolver> getIncompleteResultMaps() {
            return incompleteResultMaps;
        }

        /**
         * Add incomplete result map.
         *
         * @param resultMapResolver the result map resolver
         */
        public void addIncompleteResultMap(ResultMapResolver resultMapResolver) {
            incompleteResultMaps.add(resultMapResolver);
        }

        /**
         * Gets mapped statement.
         *
         * @param id the id
         * @return the mapped statement
         */
        public MappedStatement getMappedStatement(String id) {
            return this.getMappedStatement(id, true);
        }

        /**
         * Gets mapped statement.
         *
         * @param id                           the id
         * @param validateIncompleteStatements the validate incomplete statements
         * @return the mapped statement
         */
        public MappedStatement getMappedStatement(String id, boolean validateIncompleteStatements) {
            if (validateIncompleteStatements) {
                buildAllStatements();
            }
            return mappedStatements.get(id);
        }


        /**
         * Add loaded resource.
         *
         * @param resource the resource
         */
        public void addLoadedResource(String resource) {
            loadedResources.add(resource);
        }

        /**
         * Add mappers.
         *
         * @param packageName the package name
         * @param superType   the super type
         */
        public void addMappers(String packageName, Class<?> superType) {
            mapperRegistry.addMappers(packageName, superType);
        }

        /**
         * Add mappers.
         *
         * @param packageName the package name
         */
        public void addMappers(String packageName) {
            mapperRegistry.addMappers(packageName);
        }

        /**
         * Add mapper.
         *
         * @param <T>  the type parameter
         * @param type the type
         */
        public <T> void addMapper(Class<T> type) {
            mapperRegistry.addMapper(type);
        }

        /**
         * Gets mapper.
         *
         * @param <T>        the type parameter
         * @param type       the type
         * @param sqlSession the sql session
         * @return the mapper
         */
        public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
            return mapperRegistry.getMapper(type, sqlSession);
        }

        /**
         * Has mapper boolean.
         *
         * @param type the type
         * @return the boolean
         */
        public boolean hasMapper(Class<?> type) {
            return mapperRegistry.hasMapper(type);
        }

        /**
         * Add cache ref.
         *
         * @param namespace           the namespace
         * @param referencedNamespace the referenced namespace
         */
        public void addCacheRef(String namespace, String referencedNamespace) {
            cacheRefMap.put(namespace, referencedNamespace);
        }

        /**
         * Is resource loaded boolean.
         *
         * @param resource the resource
         * @return the boolean
         */
        public boolean isResourceLoaded(String resource) {
            return loadedResources.contains(resource);
        }

        /**
         * New meta object meta object.
         *
         * @param object the object
         * @return the meta object
         */
        public MetaObject newMetaObject(Object object) {
            return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
        }

        /**
         * Has statement boolean.
         *
         * @param statementName the statement name
         * @return the boolean
         */
        public boolean hasStatement(String statementName) {
            return hasStatement(statementName, true);
        }

        /**
         * Has statement boolean.
         *
         * @param statementName                the statement name
         * @param validateIncompleteStatements the validate incomplete statements
         * @return the boolean
         */
        public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
            if (validateIncompleteStatements) {
                buildAllStatements();
            }
            return mappedStatements.containsKey(statementName);
        }

        /**
         * Gets cache.
         *
         * @param id the id
         * @return the cache
         */
        public Cache getCache(String id) {
            return caches.get(id);
        }

        /**
         * Gets key generator.
         *
         * @param id the id
         * @return the key generator
         */
        public KeyGenerator getKeyGenerator(String id) {
            return keyGenerators.get(id);
        }

        /**
         * Has key generator boolean.
         *
         * @param id the id
         * @return the boolean
         */
        public boolean hasKeyGenerator(String id) {
            return keyGenerators.containsKey(id);
        }

        /**
         * Sets variables.
         *
         * @param props the props
         */
    //============================getter/setter============================
        public void setVariables(Properties props) {
            this.variables = props;
        }

        /**
         * Gets variables.
         *
         * @return the variables
         */
        public Properties getVariables() {
            return variables;
        }

        /**
         * Is safe row bounds enabled boolean.
         *
         * @return the boolean
         */
        public boolean isSafeRowBoundsEnabled() {
            return safeRowBoundsEnabled;
        }

        /**
         * Sets safe row bounds enabled.
         *
         * @param safeRowBoundsEnabled the safe row bounds enabled
         */
        public void setSafeRowBoundsEnabled(boolean safeRowBoundsEnabled) {
            this.safeRowBoundsEnabled = safeRowBoundsEnabled;
        }

        /**
         * Is safe result handler enabled boolean.
         *
         * @return the boolean
         */
        public boolean isSafeResultHandlerEnabled() {
            return safeResultHandlerEnabled;
        }

        /**
         * Sets safe result handler enabled.
         *
         * @param safeResultHandlerEnabled the safe result handler enabled
         */
        public void setSafeResultHandlerEnabled(boolean safeResultHandlerEnabled) {
            this.safeResultHandlerEnabled = safeResultHandlerEnabled;
        }

        /**
         * Is map underscore to camel case boolean.
         *
         * @return the boolean
         */
        public boolean isMapUnderscoreToCamelCase() {
            return mapUnderscoreToCamelCase;
        }

        /**
         * Sets map underscore to camel case.
         *
         * @param mapUnderscoreToCamelCase the map underscore to camel case
         */
        public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
            this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
        }

        /**
         * Is aggressive lazy loading boolean.
         *
         * @return the boolean
         */
        public boolean isAggressiveLazyLoading() {
            return aggressiveLazyLoading;
        }

        /**
         * Sets aggressive lazy loading.
         *
         * @param aggressiveLazyLoading the aggressive lazy loading
         */
        public void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
            this.aggressiveLazyLoading = aggressiveLazyLoading;
        }

        /**
         * Is multiple result sets enabled boolean.
         *
         * @return the boolean
         */
        public boolean isMultipleResultSetsEnabled() {
            return multipleResultSetsEnabled;
        }

        /**
         * Sets multiple result sets enabled.
         *
         * @param multipleResultSetsEnabled the multiple result sets enabled
         */
        public void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
            this.multipleResultSetsEnabled = multipleResultSetsEnabled;
        }

        /**
         * Is use generated keys boolean.
         *
         * @return the boolean
         */
        public boolean isUseGeneratedKeys() {
            return useGeneratedKeys;
        }

        /**
         * Sets use generated keys.
         *
         * @param useGeneratedKeys the use generated keys
         */
        public void setUseGeneratedKeys(boolean useGeneratedKeys) {
            this.useGeneratedKeys = useGeneratedKeys;
        }

        /**
         * Is use column label boolean.
         *
         * @return the boolean
         */
        public boolean isUseColumnLabel() {
            return useColumnLabel;
        }

        /**
         * Sets use column label.
         *
         * @param useColumnLabel the use column label
         */
        public void setUseColumnLabel(boolean useColumnLabel) {
            this.useColumnLabel = useColumnLabel;
        }

        /**
         * Is cache enabled boolean.
         *
         * @return the boolean
         */
        public boolean isCacheEnabled() {
            return cacheEnabled;
        }

        /**
         * Sets cache enabled.
         *
         * @param cacheEnabled the cache enabled
         */
        public void setCacheEnabled(boolean cacheEnabled) {
            this.cacheEnabled = cacheEnabled;
        }

        /**
         * Is call setters on nulls boolean.
         *
         * @return the boolean
         */
        public boolean isCallSettersOnNulls() {
            return callSettersOnNulls;
        }

        /**
         * Sets call setters on nulls.
         *
         * @param callSettersOnNulls the call setters on nulls
         */
        public void setCallSettersOnNulls(boolean callSettersOnNulls) {
            this.callSettersOnNulls = callSettersOnNulls;
        }

        /**
         * Is use actual param name boolean.
         *
         * @return the boolean
         */
        public boolean isUseActualParamName() {
            return useActualParamName;
        }

        /**
         * Sets use actual param name.
         *
         * @param useActualParamName the use actual param name
         */
        public void setUseActualParamName(boolean useActualParamName) {
            this.useActualParamName = useActualParamName;
        }

        /**
         * Gets vfs.
         *
         * @return the vfs
         */
        public Class<? extends VFS> getVfsImpl() {
            return vfsImpl;
        }

        /**
         * Sets vfs.
         *
         * @param vfsImpl the vfs
         */
        public void setVfsImpl(Class<? extends VFS> vfsImpl) {
            this.vfsImpl = vfsImpl;
        }

        /**
         * Gets local cache scope.
         *
         * @return the local cache scope
         */
        public LocalCacheScope getLocalCacheScope() {
            return localCacheScope;
        }

        /**
         * Sets local cache scope.
         *
         * @param localCacheScope the local cache scope
         */
        public void setLocalCacheScope(LocalCacheScope localCacheScope) {
            this.localCacheScope = localCacheScope;
        }

        /**
         * Gets lazy load trigger methods.
         *
         * @return the lazy load trigger methods
         */
        public Set<String> getLazyLoadTriggerMethods() {
            return lazyLoadTriggerMethods;
        }

        /**
         * Sets lazy load trigger methods.
         *
         * @param lazyLoadTriggerMethods the lazy load trigger methods
         */
        public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
            this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
        }

        /**
         * Gets default statement timeout.
         *
         * @return the default statement timeout
         */
        public Integer getDefaultStatementTimeout() {
            return defaultStatementTimeout;
        }

        /**
         * Sets default statement timeout.
         *
         * @param defaultStatementTimeout the default statement timeout
         */
        public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
            this.defaultStatementTimeout = defaultStatementTimeout;
        }

        /**
         * Gets default fetch size.
         *
         * @return the default fetch size
         */
        public Integer getDefaultFetchSize() {
            return defaultFetchSize;
        }

        /**
         * Sets default fetch size.
         *
         * @param defaultFetchSize the default fetch size
         */
        public void setDefaultFetchSize(Integer defaultFetchSize) {
            this.defaultFetchSize = defaultFetchSize;
        }

        /**
         * Gets configuration factory.
         *
         * @return the configuration factory
         */
        public Class<?> getConfigurationFactory() {
            return configurationFactory;
        }

        /**
         * Sets configuration factory.
         *
         * @param configurationFactory the configuration factory
         */
        public void setConfigurationFactory(Class<?> configurationFactory) {
            this.configurationFactory = configurationFactory;
        }

        /**
         * Gets auto mapping behavior.
         *
         * @return the auto mapping behavior
         */
        public AutoMappingBehavior getAutoMappingBehavior() {
            return autoMappingBehavior;
        }

        /**
         * Sets auto mapping behavior.
         *
         * @param autoMappingBehavior the auto mapping behavior
         */
        public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
            this.autoMappingBehavior = autoMappingBehavior;
        }

        /**
         * Is lazy loading enabled boolean.
         *
         * @return the boolean
         */
        public boolean isLazyLoadingEnabled() {
            return lazyLoadingEnabled;
        }

        /**
         * Sets lazy loading enabled.
         *
         * @param lazyLoadingEnabled the lazy loading enabled
         */
        public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
            this.lazyLoadingEnabled = lazyLoadingEnabled;
        }

        /**
         * Gets database id.
         *
         * @return the database id
         */
        public String getDatabaseId() {
            return databaseId;
        }

        /**
         * Sets database id.
         *
         * @param databaseId the database id
         */
        public void setDatabaseId(String databaseId) {
            this.databaseId = databaseId;
        }

        /**
         * Gets environment.
         *
         * @return the environment
         */
        public Environment getEnvironment() {
            return environment;
        }

        /**
         * Sets environment.
         *
         * @param environment the environment
         */
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        /**
         * Gets default scripting lanuage instance.
         *
         * @return the default scripting lanuage instance
         */
        public LanguageDriver getDefaultScriptingLanuageInstance() {
            return languageRegistry.getDefaultDriver();
        }

        /**
         * Gets sql fragments.
         *
         * @return the sql fragments
         */
        public Map<String, XNode> getSqlFragments() {
            return sqlFragments;
        }

        /**
         * Gets object factory.
         *
         * @return the object factory
         */
        public ObjectFactory getObjectFactory() {
            return objectFactory;
        }

        /**
         * Gets language registry.
         *
         * @return the language registry
         */
        public LanguageDriverRegistry getLanguageRegistry() {
            return languageRegistry;
        }

        /**
         * Gets reflector factory.
         *
         * @return the reflector factory
         */
        public ReflectorFactory getReflectorFactory() {
            return reflectorFactory;
        }

        /**
         * Gets type alias registry.
         *
         * @return the type alias registry
         */
        public TypeAliasRegistry getTypeAliasRegistry() {
            return typeAliasRegistry;
        }

        /**
         * Gets type handler registry.
         *
         * @return the type handler registry
         */
        public TypeHandlerRegistry getTypeHandlerRegistry() {
            return typeHandlerRegistry;
        }

        /**
         * The type Strict map.
         *
         * @param <V> the type parameter
         * @author xianyijun xianyijun0@gmail.com
         */
    ///=============================inner Class===============================
        protected static class StrictMap<V> extends HashMap<String, V> {

            private static final long serialVersionUID = 2233394134392722597L;
            private final String name;

            /**
             * Instantiates a new Strict map.
             *
             * @param name            the name
             * @param initialCapacity the initial capacity
             * @param loadFactor      the load factor
             */
            public StrictMap(String name, int initialCapacity, float loadFactor) {
                super(initialCapacity, loadFactor);
                this.name = name;
            }

            /**
             * Instantiates a new Strict map.
             *
             * @param name            the name
             * @param initialCapacity the initial capacity
             */
            public StrictMap(String name, int initialCapacity) {
                super(initialCapacity);
                this.name = name;
            }

            /**
             * Instantiates a new Strict map.
             *
             * @param name the name
             */
            public StrictMap(String name) {
                super();
                this.name = name;
            }

            /**
             * Instantiates a new Strict map.
             *
             * @param name the name
             * @param m    the m
             */
            public StrictMap(String name, Map<String, ? extends V> m) {
                super(m);
                this.name = name;
            }

            /**
             * Put v.
             *
             * @param key   the key
             * @param value the value
             * @return the v
             */
            @SuppressWarnings("unchecked")
            @Override
            public V put(String key, V value) {
                if (containsKey(key)) {
                    throw new IllegalArgumentException(name + " already contains value for " + key);
                }
                if (key.contains(".")) {
                    final String shortKey = getShortName(key);
                    if (super.get(shortKey) == null) {
                        super.put(shortKey, value);
                    } else {
                        super.put(shortKey, (V) new Ambiguity(shortKey));
                    }
                }
                return super.put(key, value);
            }

            /**
             * Get v.
             *
             * @param key the key
             * @return the v
             */
            @Override
            public V get(Object key) {
                V value = super.get(key);
                if (value == null) {
                    throw new IllegalArgumentException(name + " does not contain value for " + key);
                }
                if (value instanceof Ambiguity) {
                    throw new IllegalArgumentException(((Ambiguity) value).getSubject() + " is ambiguous in " + name
                            + " (try using the full name including the namespace, or rename one of the entries)");
                }
                return value;
            }

            private String getShortName(String key) {
                final String[] keyParts = key.split("\\.");
                return keyParts[keyParts.length - 1];
            }

            /**
             * Equals boolean.
             *
             * @param o the o
             * @return the boolean
             */
            @Override
            public boolean equals(Object o) {

                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                if (!super.equals(o)) return false;

                StrictMap<?> strictMap = (StrictMap<?>) o;

                return name != null ? name.equals(strictMap.name) : strictMap.name == null;

            }

            /**
             * Hash code int.
             *
             * @return the int
             */
            @Override
            public int hashCode() {
                int result = super.hashCode();
                result = 31 * result + (name != null ? name.hashCode() : 0);
                return result;
            }

            /**
             * The type Ambiguity.
             *
             * @author xianyijun xianyijun0@gmail.com
             */
            protected static class Ambiguity {
                final private String subject;

                /**
                 * Instantiates a new Ambiguity.
                 *
                 * @param subject the subject
                 */
                public Ambiguity(String subject) {
                    this.subject = subject;
                }

                /**
                 * Gets subject.
                 *
                 * @return the subject
                 */
                public String getSubject() {
                    return subject;
                }
            }
        }

        /**
         * Build all statements.
         */
        protected void buildAllStatements() {
            if (!incompleteResultMaps.isEmpty()) {
                synchronized (incompleteResultMaps) {
                    incompleteResultMaps.iterator().next().resolve();
                }
            }
            if (!incompleteCacheRefs.isEmpty()) {
                synchronized (incompleteCacheRefs) {
                    incompleteCacheRefs.iterator().next().resolveCacheRef();
                }
            }
            if (!incompleteStatements.isEmpty()) {
                synchronized (incompleteStatements) {
                    incompleteStatements.iterator().next().parseStatementNode();
                }
            }
        }

        /**
         * New executor executor.
         *
         * @param transaction the transaction
         * @return the executor
         */
        public Executor newExecutor(Transaction transaction) {
            Executor executor;
            executor = new SimpleExecutor(this, transaction);
            return executor;
        }

        /**
         * Add result map.
         *
         * @param rm the rm
         */
        public void addResultMap(ResultMap rm) {
            resultMaps.put(rm.getId(), rm);
        }

        /**
         * Add cache.
         *
         * @param cache the cache
         */
        public void addCache(Cache cache) {
            caches.put(cache.getId(), cache);
        }

        /**
         * New statement handler statement handler.
         *
         * @param executor        the executor
         * @param mappedStatement the mapped statement
         * @param parameterObject the parameter object
         * @param rowBounds       the row bounds
         * @param resultHandler   the result handler
         * @param boundSql        the bound sql
         * @return the statement handler
         */
        public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement,
													Object parameterObject, RowBounds rowBounds, ResultHandler<?> resultHandler, BoundSql boundSql) {
            return new RoutingStatementHandler(executor, mappedStatement, parameterObject,
                    rowBounds, resultHandler, boundSql);
        }

        /**
         * New parameter handler parameter handler.
         *
         * @param mappedStatement the mapped statement
         * @param parameterObject the parameter object
         * @param boundSql        the bound sql
         * @return the parameter handler
         */
        public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject,
                                                    BoundSql boundSql) {
            return mappedStatement.getLang().createParameterHandler(mappedStatement,
                    parameterObject, boundSql);
        }

        /**
         * New result set handler result set handler.
         *
         * @param executor         the executor
         * @param mappedStatement  the mapped statement
         * @param rowBounds        the row bounds
         * @param parameterHandler the parameter handler
         * @param resultHandler    the result handler
         * @param boundSql         the bound sql
         * @return the result set handler
         */
        public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds,
                                                    ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql) {
            return new DefaultResultSetHandler(executor, mappedStatement, parameterHandler,
                    resultHandler, boundSql, rowBounds);
        }

        /**
         * Add key generator.
         *
         * @param id           the id
         * @param keyGenerator the key generator
         */
        public void addKeyGenerator(String id, KeyGenerator keyGenerator) {
            keyGenerators.put(id, keyGenerator);
        }

        /**
         * Gets proxy factory.
         *
         * @return the proxy factory
         */
        public ProxyFactory getProxyFactory() {
            return proxyFactory;
        }

        /**
         * Sets proxy factory.
         *
         * @param proxyFactory the proxy factory
         */
        public void setProxyFactory(ProxyFactory proxyFactory) {
            if (proxyFactory == null) {
                proxyFactory = new JavassistProxyFactory();
            }
            this.proxyFactory = proxyFactory;
        }

    }
}
