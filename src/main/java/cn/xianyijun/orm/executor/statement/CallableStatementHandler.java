/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.executor.keygen.KeyGenerator;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.mapping.ParameterMapping;
import cn.xianyijun.orm.mapping.ParameterMode;
import cn.xianyijun.orm.session.RowBounds;

/**
 * The type Callable statement handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class CallableStatementHandler extends BaseStatementHandler {

	/**
	 * Instantiates a new Callable statement handler.
	 *
	 * @param executor        the executor
	 * @param mappedStatement the mapped statement
	 * @param parameter       the parameter
	 * @param rowBounds       the row bounds
	 * @param resultHandler   the result handler
	 * @param boundSql        the bound sql
	 */
	public CallableStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter,
			RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		super(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
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
		CallableStatement cs = (CallableStatement) statement;
		cs.execute();
		int rows = cs.getUpdateCount();
		Object parameterObject = boundSql.getParameterObject();
		KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
		keyGenerator.processAfter(executor, mappedStatement, cs, parameterObject);
		resultSetHandler.handleOutputParameters(cs);
		return rows;
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
		CallableStatement cs = (CallableStatement) statement;
		cs.execute();
		List<E> resultList = resultSetHandler.<E> handleResultSets(cs);
		resultSetHandler.handleOutputParameters(cs);
		return resultList;
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
		CallableStatement cs = (CallableStatement) statement;
		cs.execute();
		Cursor<E> resultList = resultSetHandler.<E> handleCursorResultSets(cs);
		resultSetHandler.handleOutputParameters(cs);
		return resultList;
	}

	/**
	 * Instantiate statement statement.
	 *
	 * @param connection the connection
	 * @return the statement
	 * @throws SQLException the sql exception
	 */
	@Override
	protected Statement instantiateStatement(Connection connection) throws SQLException {
		String sql = boundSql.getSql();
		if (mappedStatement.getResultSetType() != null) {
			return connection.prepareCall(sql, mappedStatement.getResultSetType().getValue(),
					ResultSet.CONCUR_READ_ONLY);
		} else {
			return connection.prepareCall(sql);
		}
	}

	/**
	 * Parameterize.
	 *
	 * @param statement the statement
	 * @throws SQLException the sql exception
	 */
	@Override
	public void parameterize(Statement statement) throws SQLException {
		registerOutputParameters((CallableStatement) statement);
		parameterHandler.setParameters((CallableStatement) statement);
	}

	private void registerOutputParameters(CallableStatement cs) throws SQLException {
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		for (int i = 0, n = parameterMappings.size(); i < n; i++) {
			ParameterMapping parameterMapping = parameterMappings.get(i);
			if (parameterMapping.getMode() == ParameterMode.OUT || parameterMapping.getMode() == ParameterMode.INOUT) {
				//TODO
			}
		}
	}

}
