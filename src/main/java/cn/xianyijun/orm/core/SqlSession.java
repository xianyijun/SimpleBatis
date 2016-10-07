/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.core;

import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.session.RowBounds;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;

/**
 * The interface Sql session.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface SqlSession extends Closeable {

    /**
     * Select one t.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @return the t
     */
    <T> T selectOne(String statement);

    /**
     * Select one t.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @return the t
     */
    <T> T selectOne(String statement, Object parameter);

    /**
     * Select list list.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @return the list
     */
    <E> List<E> selectList(String statement);

    /**
     * Select list list.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @return the list
     */
    <E> List<E> selectList(String statement, Object parameter);

    /**
     * Select list list.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @param rowBounds the row bounds
     * @return the list
     */
    <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);

    /**
     * Select cursor cursor.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @return the cursor
     */
    <T> Cursor<T> selectCursor(String statement);

    /**
     * Select cursor cursor.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @return the cursor
     */
    <T> Cursor<T> selectCursor(String statement, Object parameter);

    /**
     * Select cursor cursor.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @param rowBounds the row bounds
     * @return the cursor
     */
    <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);

    /**
     * Select.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @param handler   the handler
     */
    void select(String statement, Object parameter, ResultHandler<?> handler);

    /**
     * Select.
     *
     * @param statement the statement
     * @param handler   the handler
     */
    void select(String statement, ResultHandler<?> handler);

    /**
     * Select.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @param rowBounds the row bounds
     * @param handler   the handler
     */
    void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler<?> handler);

    /**
     * Insert int.
     *
     * @param statement the statement
     * @return the int
     */
    int insert(String statement);

    /**
     * Insert int.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @return the int
     */
    int insert(String statement, Object parameter);

    /**
     * Update int.
     *
     * @param statement the statement
     * @return the int
     */
    int update(String statement);

    /**
     * Update int.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @return the int
     */
    int update(String statement, Object parameter);

    /**
     * Delete int.
     *
     * @param statement the statement
     * @return the int
     */
    int delete(String statement);

    /**
     * Delete int.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @return the int
     */
    int delete(String statement, Object parameter);

    /**
     * Commit.
     */
    void commit();

    /**
     * Commit.
     *
     * @param force the force
     */
    void commit(boolean force);

    /**
     * Rollback.
     */
    void rollback();

    /**
     * Rollback.
     *
     * @param force the force
     */
    void rollback(boolean force);

    /**
     * Close.
     */
    @Override
    void close();

    /**
     * Clear cache.
     */
    void clearCache();

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    StatementHandler.Configuration getConfiguration();

    /**
     * Gets mapper.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the mapper
     */
    <T> T getMapper(Class<T> type);

    /**
     * Gets connection.
     *
     * @return the connection
     */
    Connection getConnection();

    /**
     * The interface Sql source.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    @FunctionalInterface
    interface SqlSource {
        /**
         * Gets bound sql.
         *
         * @param parameterObject the parameter object
         * @return the bound sql
         */
        StatementHandler.BoundSql getBoundSql(Object parameterObject);
    }
}
