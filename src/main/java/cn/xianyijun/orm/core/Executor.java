/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.core;

import cn.xianyijun.orm.cache.CacheKey;
import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.session.RowBounds;
import cn.xianyijun.orm.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

/**
 * The interface Executor.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface Executor {

	/**
	 * The No result handler.
	 */
	ResultHandler<?> NO_RESULT_HANDLER = null;

	/**
	 * Update int.
	 *
	 * @param ms        the ms
	 * @param parameter the parameter
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	int update(MappedStatement ms, Object parameter) throws SQLException;

	/**
	 * Query list.
	 *
	 * @param <E>           the type parameter
	 * @param ms            the ms
	 * @param parameter     the parameter
	 * @param rowBounds     the row bounds
	 * @param resultHandler the result handler
	 * @param cacheKey      the cache key
	 * @param boundSql      the bound sql
	 * @return the list
	 * @throws SQLException the sql exception
	 */
	<E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler<?> resultHandler,
					  CacheKey cacheKey, StatementHandler.BoundSql boundSql) throws SQLException;

	/**
	 * Query list.
	 *
	 * @param <E>           the type parameter
	 * @param ms            the ms
	 * @param parameter     the parameter
	 * @param rowBounds     the row bounds
	 * @param resultHandler the result handler
	 * @return the list
	 * @throws SQLException the sql exception
	 */
	<E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler<?> resultHandler)
			throws SQLException;

	/**
	 * Query cursor cursor.
	 *
	 * @param <E>       the type parameter
	 * @param ms        the ms
	 * @param parameter the parameter
	 * @param rowBounds the row bounds
	 * @return the cursor
	 * @throws SQLException the sql exception
	 */
	<E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;


	/**
	 * Commit.
	 *
	 * @param required the required
	 * @throws SQLException the sql exception
	 */
	void commit(boolean required) throws SQLException;

	/**
	 * Rollback.
	 *
	 * @param required the required
	 * @throws SQLException the sql exception
	 */
	void rollback(boolean required) throws SQLException;

	/**
	 * Create cache key cache key.
	 *
	 * @param ms              the ms
	 * @param parameterObject the parameter object
	 * @param rowBounds       the row bounds
	 * @param boundSql        the bound sql
	 * @return the cache key
	 */
	CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, StatementHandler.BoundSql boundSql);

	/**
	 * Is cached boolean.
	 *
	 * @param ms  the ms
	 * @param key the key
	 * @return the boolean
	 */
	boolean isCached(MappedStatement ms, CacheKey key);

	/**
	 * Clear local cache.
	 */
	void clearLocalCache();

	/**
	 * Defer load.
	 *
	 * @param ms           the ms
	 * @param resultObject the result object
	 * @param property     the property
	 * @param key          the key
	 * @param targetType   the target type
	 */
	void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

	/**
	 * Gets transaction.
	 *
	 * @return the transaction
	 */
	Transaction getTransaction();

	/**
	 * Close.
	 *
	 * @param forceRollback the force rollback
	 */
	void close(boolean forceRollback);

	/**
	 * Is closed boolean.
	 *
	 * @return the boolean
	 */
	boolean isClosed();

	/**
	 * Sets executor wrapper.
	 *
	 * @param executor the executor
	 */
	void setExecutorWrapper(Executor executor);
}
