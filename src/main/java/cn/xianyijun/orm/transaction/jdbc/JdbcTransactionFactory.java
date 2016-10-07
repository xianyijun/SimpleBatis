/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.transaction.jdbc;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import cn.xianyijun.orm.transaction.Transaction;
import cn.xianyijun.orm.transaction.TransactionFactory;
import cn.xianyijun.orm.transaction.TransactionIsolationLevel;

/**
 * The type Jdbc transaction factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class JdbcTransactionFactory implements TransactionFactory {

    /**
     * Sets properties.
     *
     * @param props the props
     */
    @Override
    public void setProperties(Properties props) {
        // ignore
    }

    /**
     * New transaction transaction.
     *
     * @param conn the conn
     * @return the transaction
     */
    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    /**
     * New transaction transaction.
     *
     * @param ds         the ds
     * @param level      the level
     * @param autoCommit the auto commit
     * @return the transaction
     */
    @Override
    public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(ds, level, autoCommit);
    }
}
