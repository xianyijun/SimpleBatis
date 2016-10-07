/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.executor.keygen.KeyGenerator;
import cn.xianyijun.orm.executor.parameter.ParameterHandler;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.reflection.ObjectFactory;
import cn.xianyijun.orm.session.RowBounds;
import cn.xianyijun.orm.util.StatementUtil;

/**
 * The type Base statement handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public abstract class BaseStatementHandler implements StatementHandler {

    /**
     * The Configuration.
     */
    protected final Configuration configuration;
    /**
     * The Object factory.
     */
    protected final ObjectFactory objectFactory;
    /**
     * The Result set handler.
     */
    protected final ResultSetHandler resultSetHandler;
    /**
     * The Parameter handler.
     */
    protected final ParameterHandler parameterHandler;

    /**
     * The Executor.
     */
    protected final Executor executor;
    /**
     * The Mapped statement.
     */
    protected final MappedStatement mappedStatement;
    /**
     * The Row bounds.
     */
    protected final RowBounds rowBounds;

    /**
     * The Bound sql.
     */
    protected BoundSql boundSql;

    /**
     * Instantiates a new Base statement handler.
     *
     * @param executor        the executor
     * @param mappedStatement the mapped statement
     * @param parameterObject the parameter object
     * @param rowBounds       the row bounds
     * @param resultHandler   the result handler
     * @param boundSql        the bound sql
     */
    protected BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject,
                                   RowBounds rowBounds, ResultHandler<?> resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.rowBounds = rowBounds;

        this.objectFactory = configuration.getObjectFactory();

        if (boundSql == null) {
            generateKeys(parameterObject);
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }

        this.boundSql = boundSql;

        this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject, boundSql);
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, rowBounds,
                parameterHandler, resultHandler, boundSql);
    }

    /**
     * Gets bound sql.
     *
     * @return the bound sql
     */
    @Override
    public BoundSql getBoundSql() {
        return boundSql;
    }

    /**
     * Gets parameter handler.
     *
     * @return the parameter handler
     */
    @Override
    public ParameterHandler getParameterHandler() {
        return parameterHandler;
    }

    /**
     * Prepare statement.
     *
     * @param connection         the connection
     * @param transactionTimeout the transaction timeout
     * @return the statement
     * @throws SQLException the sql exception
     */
    @Override
    public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
        Statement statement = null;
        try {
            statement = instantiateStatement(connection);
            setStatementTimeout(statement, transactionTimeout);
            setFetchSize(statement);
            return statement;
        } catch (SQLException e) {
            closeStatement(statement);
            throw e;
        } catch (Exception e) {
            closeStatement(statement);
            throw new ExecutorException("Error preparing statement.  Cause: " + e, e);
        }
    }

    /**
     * Instantiate statement statement.
     *
     * @param connection the connection
     * @return the statement
     * @throws SQLException the sql exception
     */
    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

    /**
     * Sets statement timeout.
     *
     * @param stmt               the stmt
     * @param transactionTimeout the transaction timeout
     * @throws SQLException the sql exception
     */
    protected void setStatementTimeout(Statement stmt, Integer transactionTimeout) throws SQLException {
        Integer queryTimeout = null;
        if (mappedStatement.getTimeout() != null) {
            queryTimeout = mappedStatement.getTimeout();
        } else if (configuration.getDefaultStatementTimeout() != null) {
            queryTimeout = configuration.getDefaultStatementTimeout();
        }
        if (queryTimeout != null) {
            stmt.setQueryTimeout(queryTimeout);
        }
        StatementUtil.applyTransactionTimeout(stmt, queryTimeout, transactionTimeout);
    }

    /**
     * Sets fetch size.
     *
     * @param stmt the stmt
     * @throws SQLException the sql exception
     */
    protected void setFetchSize(Statement stmt) throws SQLException {
        Integer fetchSize = mappedStatement.getFetchSize();
        if (fetchSize != null) {
            stmt.setFetchSize(fetchSize);
            return;
        }
        Integer defaultFetchSize = configuration.getDefaultFetchSize();
        if (defaultFetchSize != null) {
            stmt.setFetchSize(defaultFetchSize);
        }
    }

    /**
     * Close statement.
     *
     * @param statement the statement
     */
    protected void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            //ignore
        }
    }

    /**
     * Generate keys.
     *
     * @param parameter the parameter
     */
    protected void generateKeys(Object parameter) {
        KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
        keyGenerator.processBefore(executor, mappedStatement, null, parameter);
    }

}
