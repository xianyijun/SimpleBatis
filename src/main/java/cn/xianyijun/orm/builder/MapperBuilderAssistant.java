/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.builder;

import cn.xianyijun.orm.cache.Cache;
import cn.xianyijun.orm.cache.CacheBuilder;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.exception.IncompleteElementException;
import cn.xianyijun.orm.executor.keygen.KeyGenerator;
import cn.xianyijun.orm.mapping.*;
import cn.xianyijun.orm.reflection.MetaClass;
import cn.xianyijun.orm.script.LanguageDriver;
import cn.xianyijun.orm.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The type Mapper builder assistant.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class MapperBuilderAssistant extends BaseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(MapperBuilderAssistant.class);

    private String currentNamespace;
    private String resource;
    private Cache currentCache;
    private boolean unresolvedCacheRef;
    /**
     * The Type handler registry.
     */
    protected final TypeHandlerRegistry typeHandlerRegistry;

    /**
     * Instantiates a new Mapper builder assistant.
     *
     * @param configuration the configuration
     * @param resource      the resource
     */
    public MapperBuilderAssistant(StatementHandler.Configuration configuration, String resource) {
        super(configuration);
        this.resource = resource;
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
    }

    /**
     * Gets current namespace.
     *
     * @return the current namespace
     */
    public String getCurrentNamespace() {
        return currentNamespace;
    }

    /**
     * Sets current namespace.
     *
     * @param currentNamespace the current namespace
     */
    public void setCurrentNamespace(String currentNamespace) {
        if (currentNamespace == null) {
            throw new BuilderException("The mapper element requires a namespace attribute to be specified.");
        }

        if (this.currentNamespace != null && !this.currentNamespace.equals(currentNamespace)) {
            throw new BuilderException(
                    "Wrong namespace. Expected '" + this.currentNamespace + "' but found '" + currentNamespace + "'.");
        }

        this.currentNamespace = currentNamespace;
    }

    /**
     * Apply current namespace string.
     *
     * @param base        the base
     * @param isReference the is reference
     * @return the string
     */
    public String applyCurrentNamespace(String base, boolean isReference) {
        if (base == null) {
            return null;
        }
        if (isReference) {
            if (base.contains(".")) {
                return base;
            }
        } else {
            if (base.startsWith(currentNamespace + ".")) {
                return base;
            }
            if (base.contains(".")) {
                throw new BuilderException("Dots are not allowed in element names, please remove it from " + base);
            }
        }
        return currentNamespace + "." + base;
    }

    /**
     * Use cache ref cache.
     *
     * @param namespace the namespace
     * @return the cache
     */
    public Cache useCacheRef(String namespace) {
        if (namespace == null) {
            throw new BuilderException("cache-ref element requires a namespace attribute.");
        }
        try {
            unresolvedCacheRef = true;
            Cache cache = configuration.getCache(namespace);
            if (cache == null) {
                throw new IncompleteElementException("No cache for namespace '" + namespace + "' could be found.");
            }
            currentCache = cache;
            unresolvedCacheRef = false;
            return cache;
        } catch (IllegalArgumentException e) {
            throw new IncompleteElementException("No cache for namespace '" + namespace + "' could be found.", e);
        }
    }

    /**
     * Add result map result map.
     *
     * @param id             the id
     * @param type           the type
     * @param extend         the extend
     * @param resultMappings the result mappings
     * @param autoMapping    the auto mapping
     * @return the result map
     */
    public ResultMap addResultMap(String id, Class<?> type, String extend, List<ResultMapping> resultMappings,
                                  Boolean autoMapping) {
        id = applyCurrentNamespace(id, false);
        extend = applyCurrentNamespace(extend, true);

        if (extend != null) {
            if (!configuration.hasResultMap(extend)) {
                throw new IncompleteElementException("Could not find a parent resultmap with id '" + extend + "'");
            }
            ResultMap resultMap = configuration.getResultMap(extend);
            List<ResultMapping> extendedResultMappings = new ArrayList<>(resultMap.getResultMappings());
            extendedResultMappings.removeAll(resultMappings);
            boolean declaresConstructor = false;
            for (ResultMapping resultMapping : resultMappings) {
                if (resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR)) {
                    declaresConstructor = true;
                    break;
                }
            }
            if (declaresConstructor) {
                Iterator<ResultMapping> extendedResultMappingsIter = extendedResultMappings.iterator();
                while (extendedResultMappingsIter.hasNext()) {
                    if (extendedResultMappingsIter.next().getFlags().contains(ResultFlag.CONSTRUCTOR)) {
                        extendedResultMappingsIter.remove();
                    }
                }
            }
            resultMappings.addAll(extendedResultMappings);
        }
        ResultMap resultMap = new ResultMap.Builder(configuration, id, type, resultMappings, autoMapping).build();
        configuration.addResultMap(resultMap);
        return resultMap;
    }

    /**
     * Use new cache cache.
     *
     * @param typeClass     the type class
     * @param evictionClass the eviction class
     * @param flushInterval the flush interval
     * @param size          the size
     * @param readWrite     the read write
     * @param blocking      the blocking
     * @param props         the props
     * @return the cache
     */
    public Cache useNewCache(Class<? extends Cache> typeClass, Class<? extends Cache> evictionClass, Long flushInterval,
                             Integer size, boolean readWrite, boolean blocking, Properties props) {
        Cache cache = new CacheBuilder(currentNamespace).implementation(typeClass).addDecorator(evictionClass)
                .clearInterval(flushInterval).size(size).readWrite(readWrite).blocking(blocking).properties(props)
                .build();
        configuration.addCache(cache);
        currentCache = cache;
        return cache;
    }

    /**
     * Build parameter mapping parameter mapping.
     *
     * @param parameterType the parameter type
     * @param property      the property
     * @param resultMap     the result map
     * @param numericScale  the numeric scale
     * @param javaType      the java type
     * @param typeHandler   the type handler
     * @return the parameter mapping
     */
    public ParameterMapping buildParameterMapping(Class<?> parameterType, String property, String resultMap,
                                                  Integer numericScale, Class<?> javaType, Class<? extends ResultSetHandler.TypeHandler<?>> typeHandler) {
        resultMap = applyCurrentNamespace(resultMap, true);

        Class<?> javaTypeClass = resolveParameterJavaType(parameterType, property, javaType);
        ResultSetHandler.TypeHandler<?> typeHandlerInstance = resolveTypeHandler(javaTypeClass, typeHandler);

        return new ParameterMapping.Builder(configuration, property, typeHandlerInstance).resultMapId(resultMap)
                .numericScale(numericScale).build();
    }

    /**
     * Add parameter map parameter map.
     *
     * @param id                the id
     * @param parameterClass    the parameter class
     * @param parameterMappings the parameter mappings
     * @return the parameter map
     */
    public ParameterMap addParameterMap(String id, Class<?> parameterClass, List<ParameterMapping> parameterMappings) {
        id = applyCurrentNamespace(id, false);
        ParameterMap parameterMap = new ParameterMap.Builder(id, parameterClass, parameterMappings)
                .build();
        configuration.addParameterMap(parameterMap);
        return parameterMap;
    }


    /**
     * Build result mapping result mapping.
     *
     * @param resultType      the result type
     * @param property        the property
     * @param column          the column
     * @param nestedSelect    the nested select
     * @param nestedResultMap the nested result map
     * @param notNullColumn   the not null column
     * @param columnPrefix    the column prefix
     * @param flags           the flags
     * @param resultSet       the result set
     * @param foreignColumn   the foreign column
     * @param lazy            the lazy
     * @return the result mapping
     */
    public ResultMapping buildResultMapping(Class<?> resultType, String property, String column, String nestedSelect,
                                            String nestedResultMap, String notNullColumn, String columnPrefix, List<ResultFlag> flags, String resultSet,
                                            String foreignColumn, boolean lazy) {
        Class<?> resultTypeClass = resolveResultJavaType(resultType, property);
        List<ResultMapping> composites = parseCompositeColumnName(column);
        if (composites.isEmpty()) {
            column = null;
        }
        return new ResultMapping.Builder(configuration, property, column, resultTypeClass)
                .nestedQueryId(applyCurrentNamespace(nestedSelect, true))
                .nestedResultMapId(applyCurrentNamespace(nestedResultMap, true)).resultSet(resultSet)
                .flags(flags == null ? new ArrayList<ResultFlag>() : flags).composites(composites)
                .notNullColumns(parseMultipleColumnNames(notNullColumn)).columnPrefix(columnPrefix)
                .foreignColumn(foreignColumn).lazy(lazy).build();
    }

    private List<ResultMapping> parseCompositeColumnName(String columnName) {
        List<ResultMapping> composites = new ArrayList<>();
        if (columnName != null && (columnName.indexOf('=') > -1 || columnName.indexOf(',') > -1)) {
            StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
            while (parser.hasMoreTokens()) {
                String property = parser.nextToken();
                String column = parser.nextToken();
                ResultMapping complexResultMapping = new ResultMapping.Builder(configuration, property, column,
                        configuration.getTypeHandlerRegistry().getUnknownTypeHandler()).build();
                composites.add(complexResultMapping);
            }
        }
        return composites;
    }

    private Set<String> parseMultipleColumnNames(String columnName) {
        Set<String> columns = new HashSet<>();
        if (columnName != null) {
            if (columnName.indexOf(',') > -1) {
                StringTokenizer parser = new StringTokenizer(columnName, "{}, ", false);
                while (parser.hasMoreTokens()) {
                    String column = parser.nextToken();
                    columns.add(column);
                }
            } else {
                columns.add(columnName);
            }
        }
        return columns;
    }

    /**
     * Gets language driver.
     *
     * @param langClass the lang class
     * @return the language driver
     */
    public LanguageDriver getLanguageDriver(Class<?> langClass) {
        if (langClass != null) {
            configuration.getLanguageRegistry().register(langClass);
        } else {
            langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        }
        return configuration.getLanguageRegistry().getDriver(langClass);
    }

    /**
     * Add mapped statement mapped statement.
     *
     * @param id             the id
     * @param sqlSource      the sql source
     * @param statementType  the statement type
     * @param sqlCommandType the sql command type
     * @param fetchSize      the fetch size
     * @param timeout        the timeout
     * @param parameterMap   the parameter map
     * @param parameterType  the parameter type
     * @param resultMap      the result map
     * @param resultType     the result type
     * @param resultSetType  the result set type
     * @param flushCache     the flush cache
     * @param useCache       the use cache
     * @param resultOrdered  the result ordered
     * @param keyGenerator   the key generator
     * @param keyProperty    the key property
     * @param keyColumn      the key column
     * @param databaseId     the database id
     * @param lang           the lang
     * @param resultSets     the result sets
     * @return the mapped statement
     */
    public MappedStatement addMappedStatement(String id, SqlSession.SqlSource sqlSource, StatementType statementType,
                                              SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap,
                                              Class<?> parameterType, String resultMap, Class<?> resultType, ResultSetType resultSetType,
                                              boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty,
                                              String keyColumn, String databaseId, LanguageDriver lang, String resultSets) {

        if (unresolvedCacheRef) {
            throw new IncompleteElementException("Cache-ref not yet resolved");
        }

        id = applyCurrentNamespace(id, false);
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlSource,
                sqlCommandType).resource(resource).fetchSize(fetchSize).timeout(timeout).statementType(statementType)
                .keyGenerator(keyGenerator).keyProperty(keyProperty).keyColumn(keyColumn).databaseId(databaseId)
                .lang(lang).resultOrdered(resultOrdered).resultSets(resultSets).keyGenerator(keyGenerator)
                .resultMaps(getStatementResultMaps(resultMap, resultType, id)).resultSetType(resultSetType)
                .flushCacheRequired(flushCache).useCache(useCache).cache(currentCache);

        ParameterMap statementParameterMap = getStatementParameterMap(parameterMap, parameterType, id);
        if (statementParameterMap != null) {
            statementBuilder.parameterMap(statementParameterMap);
        }

        MappedStatement statement = statementBuilder.build();
        configuration.addMappedStatement(statement);
        return statement;
    }

    /**
     * Add mapped statement.
     *
     * @param id             the id
     * @param sqlSource      the sql source
     * @param statementType  the statement type
     * @param sqlCommandType the sql command type
     * @param fetchSize      the fetch size
     * @param timeout        the timeout
     * @param parameterMap   the parameter map
     * @param parameterType  the parameter type
     * @param resultMap      the result map
     * @param resultType     the result type
     * @param resultSetType  the result set type
     * @param flushCache     the flush cache
     * @param useCache       the use cache
     * @param resultOrdered  the result ordered
     * @param keyGenerator   the key generator
     * @param keyProperty    the key property
     * @param keyColumn      the key column
     * @param databaseId     the database id
     * @param lang           the lang
     */
    public void addMappedStatement(String id, SqlSession.SqlSource sqlSource, StatementType statementType,
                                   SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap,
                                   Class<?> parameterType, String resultMap, Class<?> resultType, ResultSetType resultSetType,
                                   boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty,
                                   String keyColumn, String databaseId, LanguageDriver lang) {
        addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap,
                parameterType, resultMap, resultType, resultSetType, flushCache, useCache, resultOrdered, keyGenerator,
                keyProperty, keyColumn, databaseId, lang, null);
    }

    private ParameterMap getStatementParameterMap(String parameterMapName, Class<?> parameterTypeClass,
                                                  String statementId) {
        parameterMapName = applyCurrentNamespace(parameterMapName, true);
        ParameterMap parameterMap = null;
        if (parameterMapName != null) {
            try {
                parameterMap = configuration.getParameterMap(parameterMapName);
            } catch (IllegalArgumentException e) {
                throw new IncompleteElementException("Could not find parameter map " + parameterMapName, e);
            }
        } else if (parameterTypeClass != null) {
            List<ParameterMapping> parameterMappings = new ArrayList<>();
            parameterMap = new ParameterMap.Builder(statementId + "-Inline", parameterTypeClass,
                    parameterMappings).build();
        }
        return parameterMap;
    }

    private List<ResultMap> getStatementResultMaps(String resultMap, Class<?> resultType, String statementId) {
        resultMap = applyCurrentNamespace(resultMap, true);

        List<ResultMap> resultMaps = new ArrayList<>();
        if (resultMap != null) {
            String[] resultMapNames = resultMap.split(",");
            for (String resultMapName : resultMapNames) {
                try {
                    resultMaps.add(configuration.getResultMap(resultMapName.trim()));
                } catch (IllegalArgumentException e) {
                    throw new IncompleteElementException("Could not find result map " + resultMapName, e);
                }
            }
        } else if (resultType != null) {
            ResultMap inlineResultMap = new ResultMap.Builder(configuration, statementId + "-Inline", resultType,
                    new ArrayList<ResultMapping>(), null).build();
            resultMaps.add(inlineResultMap);
        }
        return resultMaps;
    }

    private Class<?> resolveResultJavaType(Class<?> resultType, String property) {
        Class<?> javaType = null;
        if (property != null) {
            try {
                MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
                javaType = metaResultType.getSetterType(property);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

    private Class<?> resolveParameterJavaType(Class<?> resultType, String property, Class<?> javaType) {
        if (javaType == null) {
            if (Map.class.isAssignableFrom(resultType)) {
                javaType = Object.class;
            } else {
                MetaClass metaResultType = MetaClass.forClass(resultType, configuration.getReflectorFactory());
                javaType = metaResultType.getGetterType(property);
            }
        }
        if (javaType == null) {
            javaType = Object.class;
        }
        return javaType;
    }

    /**
     * Resolve type handler type handler.
     *
     * @param javaType        the java type
     * @param typeHandlerType the type handler type
     * @return the type handler
     */
    protected ResultSetHandler.TypeHandler resolveTypeHandler(Class<?> javaType, Class<? extends ResultSetHandler.TypeHandler<?>> typeHandlerType) {
        if (typeHandlerType == null) {
            return null;
        }
        ResultSetHandler.TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
        if (handler == null) {
            handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
        }
        return handler;
    }

}
