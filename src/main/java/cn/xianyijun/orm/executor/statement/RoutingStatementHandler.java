/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cn.xianyijun.orm.core.PreparedStatementHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.executor.parameter.ParameterHandler;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.session.RowBounds;

/**
 * The type Routing statement handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class RoutingStatementHandler implements StatementHandler {

	private final StatementHandler delegate;

	/**
	 * Instantiates a new Routing statement handler.
	 *
	 * @param executor      the executor
	 * @param ms            the ms
	 * @param parameter     the parameter
	 * @param rowBounds     the row bounds
	 * @param resultHandler the result handler
	 * @param boundSql      the bound sql
	 */
	public RoutingStatementHandler(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
								   ResultHandler<?> resultHandler, BoundSql boundSql) {

		switch (ms.getStatementType()) {
		case STATEMENT:
			delegate = new SimpleStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
			break;
		case PREPARED:
			delegate = new PreparedStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
			break;
		case CALLABLE:
			delegate = new CallableStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
			break;
		default:
			throw new ExecutorException("Unknown statement type: " + ms.getStatementType());
		}

	}

	/**
	 * Prepare statement.
	 *
	 * @param connection         the connection
	 * @param transactionTimeout the transaction timeout
	 * @return the statement
	 * @throws SQLException the sql exception
	 */
	@Override
	public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
		return delegate.prepare(connection, transactionTimeout);
	}

	/**
	 * Parameterize.
	 *
	 * @param statement the statement
	 * @throws SQLException the sql exception
	 */
	@Override
	public void parameterize(Statement statement) throws SQLException {
		delegate.parameterize(statement);
	}

	/**
	 * Update int.
	 *
	 * @param statement the statement
	 * @return the int
	 * @throws SQLException the sql exception
	 */
	@Override
	public int update(Statement statement) throws SQLException {
		return delegate.update(statement);
	}

	/**
	 * Query list.
	 *
	 * @param <E>           the type parameter
	 * @param statement     the statement
	 * @param resultHandler the result handler
	 * @return the list
	 * @throws SQLException the sql exception
	 */
	@Override
	public <E> List<E> query(Statement statement, ResultHandler<?> resultHandler) throws SQLException {
		return delegate.<E> query(statement, resultHandler);
	}

	/**
	 * Query cursor cursor.
	 *
	 * @param <E>       the type parameter
	 * @param statement the statement
	 * @return the cursor
	 * @throws SQLException the sql exception
	 */
	@Override
	public <E> Cursor<E> queryCursor(Statement statement) throws SQLException {
		return delegate.queryCursor(statement);
	}

	/**
	 * Gets bound sql.
	 *
	 * @return the bound sql
	 */
	@Override
	public BoundSql getBoundSql() {
		return delegate.getBoundSql();
	}

	/**
	 * Gets parameter handler.
	 *
	 * @return the parameter handler
	 */
	@Override
	public ParameterHandler getParameterHandler() {
		return delegate.getParameterHandler();
	}

}
