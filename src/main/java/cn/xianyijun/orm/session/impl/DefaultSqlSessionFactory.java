/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.session.impl;

import java.sql.Connection;
import java.sql.SQLException;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.SqlSessionException;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.mapping.Environment;
import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.session.SqlSessionFactory;
import cn.xianyijun.orm.transaction.Transaction;
import cn.xianyijun.orm.transaction.TransactionFactory;
import cn.xianyijun.orm.transaction.TransactionIsolationLevel;

/**
 * The type Default sql session factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final StatementHandler.Configuration configuration;

    /**
     * Instantiates a new Default sql session factory.
     *
     * @param configuration the configuration
     */
    public DefaultSqlSessionFactory(StatementHandler.Configuration configuration) {
        this.configuration = configuration;
    }


    /**
     * Open session sql session.
     *
     * @return the sql session
     */
    @Override
    public SqlSession openSession() {
        return openSessionFromDataSource(null, false);
    }


    /**
     * Open session sql session.
     *
     * @param level the level
     * @return the sql session
     */
    @Override
    public SqlSession openSession(TransactionIsolationLevel level) {
        return openSessionFromDataSource(level, false);
    }

    /**
     * Open session sql session.
     *
     * @param autoCommit the auto commit
     * @return the sql session
     */
    @Override
    public SqlSession openSession(boolean autoCommit) {
        return openSessionFromDataSource(null, autoCommit);
    }


    /**
     * Open session sql session.
     *
     * @param connection the connection
     * @return the sql session
     */
    @Override
    public SqlSession openSession(Connection connection) {
        return openSessionFromConnection(connection);
    }

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    @Override
    public StatementHandler.Configuration getConfiguration() {
        return configuration;
    }

    private SqlSession openSessionFromDataSource(TransactionIsolationLevel level,
                                                 boolean autoCommit) {
        Transaction tx = null;
        try {
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
            final Executor executor = configuration.newExecutor(tx);
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } catch (Exception e) {
            closeTransaction(tx);
            throw new SqlSessionException(e);
        } finally {
        }
    }

    private SqlSession openSessionFromConnection(Connection connection) {
        try {
            boolean autoCommit;
            try {
                autoCommit = connection.getAutoCommit();
            } catch (SQLException e) {
                autoCommit = true;
            }
            final Environment environment = configuration.getEnvironment();
            final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
            final Transaction tx = transactionFactory.newTransaction(connection);
            final Executor executor = configuration.newExecutor(tx);
            return new DefaultSqlSession(configuration, executor, autoCommit);
        } catch (Exception e) {
            throw new SqlSessionException(e);
        } finally {
        }
    }

    private TransactionFactory getTransactionFactoryFromEnvironment(Environment environment) {
        return environment.getTransactionFactory();
    }

    private void closeTransaction(Transaction tx) {
        if (tx != null) {
            try {
                tx.close();
            } catch (SQLException ignore) {
            }
        }
    }

}
