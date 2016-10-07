/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.builder;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.mapping.ParameterMode;
import cn.xianyijun.orm.mapping.ResultSetType;
import cn.xianyijun.orm.type.TypeAliasRegistry;
import cn.xianyijun.orm.type.TypeHandlerRegistry;

import java.util.Properties;

/**
 * The type Base builder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public abstract class BaseBuilder {
    /**
     * The Configuration.
     */
    protected final StatementHandler.Configuration configuration;
    /**
     * The Variables.
     */
    protected Properties variables = new Properties();
    /**
     * The Type alias registry.
     */
    protected final TypeAliasRegistry typeAliasRegistry;
    /**
     * The Type handler registry.
     */
    protected final TypeHandlerRegistry typeHandlerRegistry;

    /**
     * Instantiates a new Base builder.
     *
     * @param configuration the configuration
     */
    public BaseBuilder(StatementHandler.Configuration configuration) {
        this.configuration = configuration;
        this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
        this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
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
     * Sets variables.
     *
     * @param variables the variables
     */
    public void setVariables(Properties variables) {
        this.variables = variables;
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
     * Resolve parameter mode parameter mode.
     *
     * @param alias the alias
     * @return the parameter mode
     */
    protected ParameterMode resolveParameterMode(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return ParameterMode.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving ParameterMode. Cause: " + e, e);
        }
    }

    /**
     * Resolve result set type result set type.
     *
     * @param alias the alias
     * @return the result set type
     */
    protected ResultSetType resolveResultSetType(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return ResultSetType.valueOf(alias);
        } catch (IllegalArgumentException e) {
            throw new BuilderException("Error resolving ResultSetType. Cause: " + e, e);
        }
    }

    /**
     * Resolve class class.
     *
     * @param alias the alias
     * @return the class
     */
    protected Class<?> resolveClass(String alias) {
        if (alias == null) {
            return null;
        }
        try {
            return resolveAlias(alias);
        } catch (Exception e) {
            throw new BuilderException("Error resolving class. Cause: " + e, e);
        }
    }

    /**
     * Resolve alias class.
     *
     * @param alias the alias
     * @return the class
     */
    protected Class<?> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }
}
