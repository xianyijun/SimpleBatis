/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.transaction.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xianyijun.orm.exception.TransactionException;
import cn.xianyijun.orm.transaction.Transaction;
import cn.xianyijun.orm.transaction.TransactionIsolationLevel;

/**
 * The type Jdbc transaction.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class JdbcTransaction implements Transaction {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTransaction.class);

    /**
     * The Connection.
     */
    protected Connection connection;
    /**
     * The Data source.
     */
    protected DataSource dataSource;
    /**
     * The Level.
     */
    protected TransactionIsolationLevel level;
    /**
     * The Auto commit.
     */
    protected boolean autoCommit;

    /**
     * Instantiates a new Jdbc transaction.
     *
     * @param ds                the ds
     * @param desiredLevel      the desired level
     * @param desiredAutoCommit the desired auto commit
     */
    public JdbcTransaction(DataSource ds, TransactionIsolationLevel desiredLevel, boolean desiredAutoCommit) {
        dataSource = ds;
        level = desiredLevel;
        autoCommit = desiredAutoCommit;
    }

    /**
     * Instantiates a new Jdbc transaction.
     *
     * @param connection the connection
     */
    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    /**
     * Commit.
     *
     * @throws SQLException the sql exception
     */
    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Committing JDBC Connection [" + connection + "]");
            }
            connection.commit();
        }
    }

    /**
     * Rollback.
     *
     * @throws SQLException the sql exception
     */
    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Rolling back JDBC Connection [" + connection + "]");
            }
            connection.rollback();
        }
    }

    /**
     * Close.
     *
     * @throws SQLException the sql exception
     */
    @Override
    public void close() throws SQLException {
        if (connection != null) {
            resetAutoCommit();
            if (logger.isDebugEnabled()) {
                logger.debug("Closing JDBC Connection [" + connection + "]");
            }
            connection.close();
        }
    }

    /**
     * Sets desired auto commit.
     *
     * @param desiredAutoCommit the desired auto commit
     */
    protected void setDesiredAutoCommit(boolean desiredAutoCommit) {
        try {
            if (connection.getAutoCommit() != desiredAutoCommit) {
                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "Setting autocommit to " + desiredAutoCommit + " on JDBC Connection [" + connection + "]");
                }
                connection.setAutoCommit(desiredAutoCommit);
            }
        } catch (SQLException e) {
            throw new TransactionException("Error configuring AutoCommit.  "
                    + "Your driver may not support getAutoCommit() or setAutoCommit(). " + "Requested setting: "
                    + desiredAutoCommit + ".  Cause: " + e, e);
        }
    }

    /**
     * Reset auto commit.
     */
    protected void resetAutoCommit() {
        try {
            if (!connection.getAutoCommit()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Resetting autocommit to true on JDBC Connection [" + connection + "]");
                }
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error resetting autocommit to true " + "before closing the connection.  Cause: " + e);
            }
        }
    }

    /**
     * Open connection.
     *
     * @throws SQLException the sql exception
     */
    protected void openConnection() throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Opening JDBC Connection");
        }
        connection = dataSource.getConnection();
        if (level != null) {
            connection.setTransactionIsolation(level.getLevel());
        }
        setDesiredAutoCommit(autoCommit);
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     * @throws SQLException the sql exception
     */
    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }

}
