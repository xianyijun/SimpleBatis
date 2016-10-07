/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.session;

import java.sql.Connection;

import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.transaction.TransactionIsolationLevel;

/**
 * The interface Sql session factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface SqlSessionFactory {
    /**
     * Open session sql session.
     *
     * @return the sql session
     */
    SqlSession openSession();

    /**
     * Open session sql session.
     *
     * @param autoCommit the auto commit
     * @return the sql session
     */
    SqlSession openSession(boolean autoCommit);

    /**
     * Open session sql session.
     *
     * @param connection the connection
     * @return the sql session
     */
    SqlSession openSession(Connection connection);

    /**
     * Open session sql session.
     *
     * @param level the level
     * @return the sql session
     */
    SqlSession openSession(TransactionIsolationLevel level);

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    StatementHandler.Configuration getConfiguration();
}
