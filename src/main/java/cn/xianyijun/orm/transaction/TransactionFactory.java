/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.transaction;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;


/**
 * The interface Transaction factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface TransactionFactory {
    /**
     * Sets properties.
     *
     * @param props the props
     */
    void setProperties(Properties props);

    /**
     * New transaction transaction.
     *
     * @param conn the conn
     * @return the transaction
     */
    Transaction newTransaction(Connection conn);

    /**
     * New transaction transaction.
     *
     * @param dataSource the data source
     * @param level      the level
     * @param autoCommit the auto commit
     * @return the transaction
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);

}
