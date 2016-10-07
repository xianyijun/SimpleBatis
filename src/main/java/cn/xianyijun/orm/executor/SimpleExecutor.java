/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor;

import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.session.RowBounds;
import cn.xianyijun.orm.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * The type Simple executor.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SimpleExecutor extends BaseExecutor {

    /**
     * Instantiates a new Simple executor.
     *
     * @param configuration the configuration
     * @param transaction   the transaction
     */
    public SimpleExecutor(StatementHandler.Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    /**
     * Do update int.
     *
     * @param ms        the ms
     * @param parameter the parameter
     * @return the int
     * @throws SQLException the sql exception
     */
    @Override
    public int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            StatementHandler.Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null,
                    null);
            stmt = prepareStatement(handler);
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    /**
     * Do query list.
     *
     * @param <E>           the type parameter
     * @param ms            the ms
     * @param parameter     the parameter
     * @param rowBounds     the row bounds
     * @param resultHandler the result handler
     * @param boundSql      the bound sql
     * @return the list
     * @throws SQLException the sql exception
     */
    @Override
    public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler<?> resultHandler,
                               StatementHandler.BoundSql boundSql) throws SQLException {
        Statement stmt = null;
        try {
            StatementHandler.Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds,
                    resultHandler, boundSql);
            stmt = prepareStatement(handler);
            return handler.<E>query(stmt, resultHandler);
        } finally {
            closeStatement(stmt);
        }
    }

    /**
     * Do query cursor cursor.
     *
     * @param <E>       the type parameter
     * @param ms        the ms
     * @param parameter the parameter
     * @param rowBounds the row bounds
     * @param boundSql  the bound sql
     * @return the cursor
     * @throws SQLException the sql exception
     */
    @Override
    protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds, StatementHandler.BoundSql boundSql)
            throws SQLException {
        StatementHandler.Configuration configuration = ms.getConfiguration();
        StatementHandler handler = configuration.newStatementHandler(wrapper, ms, parameter, rowBounds, null, boundSql);
        Statement stmt = prepareStatement(handler);
        return handler.<E>queryCursor(stmt);
    }

    private Statement prepareStatement(StatementHandler handler) throws SQLException {
        Statement stmt;
        Connection connection = getConnection();
        stmt = handler.prepare(connection, transaction.getTimeout());
        handler.parameterize(stmt);
        return stmt;
    }

}
