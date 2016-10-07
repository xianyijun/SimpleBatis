/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.session.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.exception.BindingException;
import cn.xianyijun.orm.exception.SqlSessionException;
import cn.xianyijun.orm.exception.TooManyResultsException;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.session.RowBounds;
import cn.xianyijun.orm.core.SqlSession;

/**
 * The type Default sql session.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultSqlSession implements SqlSession {

    private StatementHandler.Configuration configuration;
    private Executor executor;

    private boolean autoCommit;
    private boolean dirty;
    private List<Cursor<?>> cursorList;

    /**
     * Instantiates a new Default sql session.
     *
     * @param configuration the configuration
     * @param executor      the executor
     * @param autoCommit    the auto commit
     */
    public DefaultSqlSession(StatementHandler.Configuration configuration, Executor executor, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.dirty = false;
        this.autoCommit = autoCommit;
    }

    /**
     * Instantiates a new Default sql session.
     *
     * @param configuration the configuration
     * @param executor      the executor
     */
    public DefaultSqlSession(StatementHandler.Configuration configuration, Executor executor) {
        this(configuration, executor, false);
    }

    /**
     * Select one t.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @return the t
     */
    @Override
    public <T> T selectOne(String statement) {
        return this.<T>selectOne(statement, null);
    }

    /**
     * Select one t.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @return the t
     */
    @Override
    public <T> T selectOne(String statement, Object parameter) {
        // Popular vote was to return null on 0 results and throw exception on too many.
        List<T> list = this.<T>selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new TooManyResultsException(
                    "Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    /**
     * Select cursor cursor.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @return the cursor
     */
    @Override
    public <T> Cursor<T> selectCursor(String statement) {
        return selectCursor(statement, null);
    }

    /**
     * Select cursor cursor.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @return the cursor
     */
    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter) {
        return selectCursor(statement, parameter, RowBounds.DEFAULT);
    }

    /**
     * Select cursor cursor.
     *
     * @param <T>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @param rowBounds the row bounds
     * @return the cursor
     */
    @Override
    public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            Cursor<T> cursor = executor.queryCursor(ms, wrapCollection(parameter), rowBounds);
            registerCursor(cursor);
            return cursor;
        } catch (Exception e) {
            throw new SqlSessionException(e);
        } finally {
            //
        }
    }

    /**
     * Select list list.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @return the list
     */
    @Override
    public <E> List<E> selectList(String statement) {
        return this.selectList(statement, null);
    }

    /**
     * Select list list.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @return the list
     */
    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return this.selectList(statement, parameter, RowBounds.DEFAULT);
    }

    /**
     * Select list list.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @param parameter the parameter
     * @param rowBounds the row bounds
     * @return the list
     */
    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.query(ms, wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
        } catch (Exception e) {
            throw new SqlSessionException("Error querying database.  Cause: " + e, e);
        } finally {
            //
        }
    }

    /**
     * Select.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @param handler   the handler
     */
    @Override
    public void select(String statement, Object parameter, ResultHandler<?> handler) {
        select(statement, parameter, RowBounds.DEFAULT, handler);
    }

    /**
     * Select.
     *
     * @param statement the statement
     * @param handler   the handler
     */
    @Override
    public void select(String statement, ResultHandler<?> handler) {
        select(statement, null, RowBounds.DEFAULT, handler);
    }

    /**
     * Select.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @param rowBounds the row bounds
     * @param handler   the handler
     */
    @Override
    public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler<?> handler) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            executor.query(ms, wrapCollection(parameter), rowBounds, handler);
        } catch (Exception e) {
            throw new SqlSessionException(e);
        } finally {
        }
    }

    /**
     * Insert int.
     *
     * @param statement the statement
     * @return the int
     */
    @Override
    public int insert(String statement) {
        return insert(statement, null);
    }

    /**
     * Insert int.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @return the int
     */
    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    /**
     * Update int.
     *
     * @param statement the statement
     * @return the int
     */
    @Override
    public int update(String statement) {
        return update(statement, null);
    }

    /**
     * Update int.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @return the int
     */
    @Override
    public int update(String statement, Object parameter) {
        try {
            dirty = true;
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.update(ms, wrapCollection(parameter));
        } catch (Exception e) {
            throw new SqlSessionException("Error updating database.  Cause: " + e, e);
        } finally {
        }
    }

    /**
     * Delete int.
     *
     * @param statement the statement
     * @return the int
     */
    @Override
    public int delete(String statement) {
        return update(statement, null);
    }

    /**
     * Delete int.
     *
     * @param statement the statement
     * @param parameter the parameter
     * @return the int
     */
    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    /**
     * Commit.
     */
    @Override
    public void commit() {
        commit(false);
    }

    /**
     * Commit.
     *
     * @param force the force
     */
    @Override
    public void commit(boolean force) {
        try {
            executor.commit(isCommitOrRollbackRequired(force));
            dirty = false;
        } catch (Exception e) {
            new SqlSessionException(e);
        } finally {
        }
    }

    /**
     * Rollback.
     */
    @Override
    public void rollback() {
        rollback(false);
    }

    /**
     * Rollback.
     *
     * @param force the force
     */
    @Override
    public void rollback(boolean force) {
        try {
            executor.rollback(isCommitOrRollbackRequired(force));
            dirty = false;
        } catch (Exception e) {
            throw new SqlSessionException(e);
        } finally {
        }
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        try {
            executor.close(isCommitOrRollbackRequired(false));
            closeCursors();
            dirty = false;
        } finally {
        }
    }

    private void closeCursors() {
        if (cursorList != null && cursorList.isEmpty()) {
            for (Cursor<?> cursor : cursorList) {
                try {
                    cursor.close();
                } catch (IOException e) {
                    throw new SqlSessionException("Error closing cursor.  Cause: " + e, e);
                }
            }
            cursorList.clear();
        }
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

    /**
     * Gets mapper.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the mapper
     */
    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.<T>getMapper(type, this);
    }

    /**
     * Gets connection.
     *
     * @return the connection
     */
    @Override
    public Connection getConnection() {
        try {
            return executor.getTransaction().getConnection();
        } catch (SQLException e) {
            throw new SqlSessionException(e);
        }
    }

    /**
     * Clear cache.
     */
    @Override
    public void clearCache() {
        executor.clearLocalCache();
    }

    private <T> void registerCursor(Cursor<T> cursor) {
        if (cursorList == null) {
            cursorList = new ArrayList<>();
        }
        cursorList.add(cursor);
    }

    private boolean isCommitOrRollbackRequired(boolean force) {
        return (!autoCommit && dirty) || force;
    }

    private Object wrapCollection(final Object object) {
        if (object instanceof Collection) {
            StrictMap<Object> map = new StrictMap<>();
            map.put("collection", object);
            if (object instanceof List) {
                map.put("list", object);
            }
            return map;
        } else if (object != null && object.getClass().isArray()) {
            StrictMap<Object> map = new StrictMap<>();
            map.put("array", object);
            return map;
        }
        return object;
    }

    /**
     * The type Strict map.
     *
     * @param <V> the type parameter
     * @author xianyijun xianyijun0@gmail.com
     */
    public static class StrictMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -5741767162221585340L;

        /**
         * Get v.
         *
         * @param key the key
         * @return the v
         */
        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new BindingException(
                        "Parameter '" + key + "' not found. Available parameters are " + this.keySet());
            }
            return super.get(key);
        }

    }
}
