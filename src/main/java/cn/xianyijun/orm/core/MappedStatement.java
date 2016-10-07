/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.core;

import cn.xianyijun.orm.cache.Cache;
import cn.xianyijun.orm.executor.keygen.KeyGenerator;
import cn.xianyijun.orm.mapping.*;
import cn.xianyijun.orm.script.LanguageDriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Mapped statement.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public final class MappedStatement {
    private String resource;
    private StatementHandler.Configuration configuration;
    private String id;
    private Integer fetchSize;
    private Integer timeout;
    private StatementType statementType;
    private ResultSetType resultSetType;
    private SqlSession.SqlSource sqlSource;
    private Cache cache;
    private ParameterMap parameterMap;
    private List<ResultMap> resultMaps;
    private boolean flushCacheRequired;
    private boolean useCache;
    private boolean resultOrdered;
    private SqlCommandType sqlCommandType;
    private KeyGenerator keyGenerator;
    private String[] keyProperties;
    private String[] keyColumns;
    private boolean hasNestedResultMaps;
    private String databaseId;
    private LanguageDriver lang;
    private String[] resultSets;

    /**
     * Instantiates a new Mapped statement.
     */
    MappedStatement() {
    }

    /**
     * The type Builder.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        /**
         * Instantiates a new Builder.
         *
         * @param configuration  the configuration
         * @param id             the id
         * @param sqlSource      the sql source
         * @param sqlCommandType the sql command type
         */
        public Builder(StatementHandler.Configuration configuration, String id, SqlSession.SqlSource sqlSource, SqlCommandType sqlCommandType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.statementType = StatementType.PREPARED;
            mappedStatement.parameterMap = new ParameterMap.Builder("defaultParameterMap", null,
                    new ArrayList<ParameterMapping>()).build();
            mappedStatement.resultMaps = new ArrayList<>();
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.lang = configuration.getDefaultScriptingLanuageInstance();
        }

        /**
         * Resource builder.
         *
         * @param resource the resource
         * @return the builder
         */
        public Builder resource(String resource) {
            mappedStatement.resource = resource;
            return this;
        }

        /**
         * Id string.
         *
         * @return the string
         */
        public String id() {
            return mappedStatement.id;
        }

        /**
         * Parameter map builder.
         *
         * @param parameterMap the parameter map
         * @return the builder
         */
        public Builder parameterMap(ParameterMap parameterMap) {
            mappedStatement.parameterMap = parameterMap;
            return this;
        }

        /**
         * Result maps builder.
         *
         * @param resultMaps the result maps
         * @return the builder
         */
        public Builder resultMaps(List<ResultMap> resultMaps) {
            mappedStatement.resultMaps = resultMaps;
            for (ResultMap resultMap : resultMaps) {
                mappedStatement.hasNestedResultMaps = mappedStatement.hasNestedResultMaps
                        || resultMap.hasNestedResultMaps();
            }
            return this;
        }

        /**
         * Fetch size builder.
         *
         * @param fetchSize the fetch size
         * @return the builder
         */
        public Builder fetchSize(Integer fetchSize) {
            mappedStatement.fetchSize = fetchSize;
            return this;
        }

        /**
         * Timeout builder.
         *
         * @param timeout the timeout
         * @return the builder
         */
        public Builder timeout(Integer timeout) {
            mappedStatement.timeout = timeout;
            return this;
        }

        /**
         * Statement type builder.
         *
         * @param statementType the statement type
         * @return the builder
         */
        public Builder statementType(StatementType statementType) {
            mappedStatement.statementType = statementType;
            return this;
        }

        /**
         * Result set type builder.
         *
         * @param resultSetType the result set type
         * @return the builder
         */
        public Builder resultSetType(ResultSetType resultSetType) {
            mappedStatement.resultSetType = resultSetType;
            return this;
        }

        /**
         * Cache builder.
         *
         * @param cache the cache
         * @return the builder
         */
        public Builder cache(Cache cache) {
            mappedStatement.cache = cache;
            return this;
        }

        /**
         * Flush cache required builder.
         *
         * @param flushCacheRequired the flush cache required
         * @return the builder
         */
        public Builder flushCacheRequired(boolean flushCacheRequired) {
            mappedStatement.flushCacheRequired = flushCacheRequired;
            return this;
        }

        /**
         * Use cache builder.
         *
         * @param useCache the use cache
         * @return the builder
         */
        public Builder useCache(boolean useCache) {
            mappedStatement.useCache = useCache;
            return this;
        }

        /**
         * Result ordered builder.
         *
         * @param resultOrdered the result ordered
         * @return the builder
         */
        public Builder resultOrdered(boolean resultOrdered) {
            mappedStatement.resultOrdered = resultOrdered;
            return this;
        }

        /**
         * Key generator builder.
         *
         * @param keyGenerator the key generator
         * @return the builder
         */
        public Builder keyGenerator(KeyGenerator keyGenerator) {
            mappedStatement.keyGenerator = keyGenerator;
            return this;
        }

        /**
         * Key property builder.
         *
         * @param keyProperty the key property
         * @return the builder
         */
        public Builder keyProperty(String keyProperty) {
            mappedStatement.keyProperties = delimitedStringToArray(keyProperty);
            return this;
        }

        /**
         * Key column builder.
         *
         * @param keyColumn the key column
         * @return the builder
         */
        public Builder keyColumn(String keyColumn) {
            mappedStatement.keyColumns = delimitedStringToArray(keyColumn);
            return this;
        }

        /**
         * Database id builder.
         *
         * @param databaseId the database id
         * @return the builder
         */
        public Builder databaseId(String databaseId) {
            mappedStatement.databaseId = databaseId;
            return this;
        }

        /**
         * Lang builder.
         *
         * @param driver the driver
         * @return the builder
         */
        public Builder lang(LanguageDriver driver) {
            mappedStatement.lang = driver;
            return this;
        }

        /**
         * Result sets builder.
         *
         * @param resultSet the result set
         * @return the builder
         */
        public Builder resultSets(String resultSet) {
            mappedStatement.resultSets = delimitedStringToArray(resultSet);
            return this;
        }

        /**
         * Build mapped statement.
         *
         * @return the mapped statement
         */
        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            assert mappedStatement.sqlSource != null;
            assert mappedStatement.lang != null;
            mappedStatement.resultMaps = Collections.unmodifiableList(mappedStatement.resultMaps);
            return mappedStatement;
        }

        private static String[] delimitedStringToArray(String in) {
            if (in == null || in.trim().length() == 0) {
                return new String[]{};
            } else {
                return in.split(",");
            }
        }
    }

    /**
     * Gets key generator.
     *
     * @return the key generator
     */
    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * Gets sql command type.
     *
     * @return the sql command type
     */
    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    /**
     * Gets resource.
     *
     * @return the resource
     */
    public String getResource() {
        return resource;
    }

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    public StatementHandler.Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Has nested result maps boolean.
     *
     * @return the boolean
     */
    public boolean hasNestedResultMaps() {
        return hasNestedResultMaps;
    }

    /**
     * Gets fetch size.
     *
     * @return the fetch size
     */
    public Integer getFetchSize() {
        return fetchSize;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Gets statement type.
     *
     * @return the statement type
     */
    public StatementType getStatementType() {
        return statementType;
    }

    /**
     * Gets result set type.
     *
     * @return the result set type
     */
    public ResultSetType getResultSetType() {
        return resultSetType;
    }

    /**
     * Gets sql source.
     *
     * @return the sql source
     */
    public SqlSession.SqlSource getSqlSource() {
        return sqlSource;
    }

    /**
     * Gets parameter map.
     *
     * @return the parameter map
     */
    public ParameterMap getParameterMap() {
        return parameterMap;
    }

    /**
     * Gets result maps.
     *
     * @return the result maps
     */
    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }

    /**
     * Gets cache.
     *
     * @return the cache
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Is flush cache required boolean.
     *
     * @return the boolean
     */
    public boolean isFlushCacheRequired() {
        return flushCacheRequired;
    }

    /**
     * Is use cache boolean.
     *
     * @return the boolean
     */
    public boolean isUseCache() {
        return useCache;
    }

    /**
     * Is result ordered boolean.
     *
     * @return the boolean
     */
    public boolean isResultOrdered() {
        return resultOrdered;
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
     * Get key properties string [ ].
     *
     * @return the string [ ]
     */
    public String[] getKeyProperties() {
        return keyProperties;
    }

    /**
     * Get key columns string [ ].
     *
     * @return the string [ ]
     */
    public String[] getKeyColumns() {
        return keyColumns;
    }

    /**
     * Gets lang.
     *
     * @return the lang
     */
    public LanguageDriver getLang() {
        return lang;
    }

    /**
     * Get result sets string [ ].
     *
     * @return the string [ ]
     */
    public String[] getResultSets() {
        return resultSets;
    }

    /**
     * Gets bound sql.
     *
     * @param parameterObject the parameter object
     * @return the bound sql
     */
    public StatementHandler.BoundSql getBoundSql(Object parameterObject) {
        StatementHandler.BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings == null || parameterMappings.isEmpty()) {
            boundSql = new StatementHandler.BoundSql(configuration, boundSql.getSql(), parameterMap.getParameterMappings(),
                    parameterObject);
        }

        for (ParameterMapping pm : boundSql.getParameterMappings()) {
            String rmId = pm.getResultMapId();
            if (rmId != null) {
                ResultMap rm = configuration.getResultMap(rmId);
                if (rm != null) {
                    hasNestedResultMaps |= rm.hasNestedResultMaps();
                }
            }
        }

        return boundSql;
    }


}
