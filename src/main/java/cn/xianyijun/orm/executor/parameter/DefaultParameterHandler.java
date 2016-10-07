/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.TypeException;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.mapping.ParameterMapping;
import cn.xianyijun.orm.mapping.ParameterMode;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.type.JdbcType;
import cn.xianyijun.orm.type.TypeHandlerRegistry;

/**
 * The type Default parameter handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultParameterHandler implements ParameterHandler {
    private final TypeHandlerRegistry typeHandlerRegistry;
    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private StatementHandler.BoundSql boundSql;
    private StatementHandler.Configuration configuration;

    /**
     * Instantiates a new Default parameter handler.
     *
     * @param mappedStatement the mapped statement
     * @param parameterObject the parameter object
     * @param boundSql        the bound sql
     */
    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, StatementHandler.BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.parameterObject = parameterObject;
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.boundSql = boundSql;
    }

    /**
     * Gets parameter object.
     *
     * @return the parameter object
     */
    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    /**
     * Sets parameters.
     *
     * @param ps the ps
     */
    @Override
    public void setParameters(PreparedStatement ps) {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    if (boundSql.hasAdditionalParameter(propertyName)) { // issue #448 ask first for additional params
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else {
                        MetaObject metaObject = configuration.newMetaObject(parameterObject);
                        value = metaObject.getValue(propertyName);
                    }
                    ResultSetHandler.TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    JdbcType jdbcType = parameterMapping.getJdbcType();
                    try {
                        typeHandler.setParameter(ps, i + 1, value, jdbcType);
                    } catch (TypeException | SQLException e) {
                        throw new TypeException(
                                "Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
    }

}