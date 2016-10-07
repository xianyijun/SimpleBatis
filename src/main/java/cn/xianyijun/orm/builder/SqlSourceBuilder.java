/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.mapping.ParameterMapping;
import cn.xianyijun.orm.mapping.StaticSqlSource;
import cn.xianyijun.orm.parse.GenericTokenParser;
import cn.xianyijun.orm.parse.TokenHandler;
import cn.xianyijun.orm.reflection.MetaClass;
import cn.xianyijun.orm.reflection.MetaObject;

/**
 * The type Sql source builder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SqlSourceBuilder extends BaseBuilder {

    private static final String parameterProperties = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

    /**
     * Instantiates a new Sql source builder.
     *
     * @param configuration the configuration
     */
    public SqlSourceBuilder(StatementHandler.Configuration configuration) {
        super(configuration);
    }

    /**
     * Parse sql source.
     *
     * @param originalSql          the original sql
     * @param parameterType        the parameter type
     * @param additionalParameters the additional parameters
     * @return the sql source
     */
    public SqlSession.SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType,
                additionalParameters);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql);
        return new StaticSqlSource(configuration, sql, handler.getParameterMappings());
    }

    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        private List<ParameterMapping> parameterMappings = new ArrayList<>();
        private Class<?> parameterType;
        private MetaObject metaParameters;

        /**
         * Instantiates a new Parameter mapping token handler.
         *
         * @param configuration        the configuration
         * @param parameterType        the parameter type
         * @param additionalParameters the additional parameters
         */
        public ParameterMappingTokenHandler(StatementHandler.Configuration configuration, Class<?> parameterType,
                                            Map<String, Object> additionalParameters) {
            super(configuration);
            this.parameterType = parameterType;
            this.metaParameters = configuration.newMetaObject(additionalParameters);
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
         * Handle token string.
         *
         * @param content the content
         * @return the string
         */
        @Override
        public String handleToken(String content) {
            parameterMappings.add(buildParameterMapping(content));
            return "?";
        }

        private ParameterMapping buildParameterMapping(String content) {
            Map<String, String> propertiesMap = parseParameterMapping(content);
            String property = propertiesMap.get("property");
            Class<?> propertyType;
            if (metaParameters.hasGetter(property)) {
                propertyType = metaParameters.getGetterType(property);
            } else if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
                propertyType = parameterType;
            } else if (property != null) {
                MetaClass metaClass = MetaClass.forClass(parameterType, configuration.getReflectorFactory());
                if (metaClass.hasGetter(property)) {
                    propertyType = metaClass.getGetterType(property);
                } else {
                    propertyType = Object.class;
                }
            } else {
                propertyType = Object.class;
            }
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
            for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                if ("mode".equals(name)) {
                    builder.mode(resolveParameterMode(value));
                } else if ("numericScale".equals(name)) {
                    builder.numericScale(Integer.valueOf(value));
                } else if ("resultMap".equals(name)) {
                    builder.resultMapId(value);
                } else if ("property".equals(name)) {
                } else if ("expression".equals(name)) {
                    throw new BuilderException("Expression based parameters are not supported yet");
                } else {
                    throw new BuilderException("An invalid property '" + name + "' was found in mapping #{" + content
                            + "}.  Valid properties are " + parameterProperties);
                }
            }
            return builder.build();
        }

        private Map<String, String> parseParameterMapping(String content) {
            try {
                return new ParameterExpression(content);
            } catch (BuilderException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new BuilderException("Parsing error was found in mapping #{" + content
                        + "}.  Check syntax #{property|(expression), var1=value1, var2=value2, ...} ", ex);
            }
        }
    }

}
