/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The interface Transaction.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface Transaction {
    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    Connection getConnection() throws SQLException;

    /**
     * Commit.
     *
     * @throws SQLException the sql exception
     */
    void commit() throws SQLException;

    /**
     * Rollback.
     *
     * @throws SQLException the sql exception
     */
    void rollback() throws SQLException;

    /**
     * Close.
     *
     * @throws SQLException the sql exception
     */
    void close() throws SQLException;

    /**
     * Gets timeout.
     *
     * @return the timeout
     * @throws SQLException the sql exception
     */
    Integer getTimeout() throws SQLException;

}
