/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.ResultMapException;
import cn.xianyijun.orm.exception.TypeException;

/**
 * The type Base type handler.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public abstract class BaseTypeHandler<T> extends TypeReference<T> implements ResultSetHandler.TypeHandler<T> {

	/**
	 * The Configuration.
	 */
	protected StatementHandler.Configuration configuration;

	/**
	 * Sets configuration.
	 *
	 * @param c the c
	 */
	public void setConfiguration(StatementHandler.Configuration c) {
		this.configuration = c;
	}

	/**
	 * Sets parameter.
	 *
	 * @param ps        the ps
	 * @param i         the
	 * @param parameter the parameter
	 * @param jdbcType  the jdbc type
	 * @throws SQLException the sql exception
	 */
	@Override
	public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			if (jdbcType == null) {
				throw new TypeException(
						"JDBC requires that the JdbcType must be specified for all nullable parameters.");
			}
			try {
				ps.setNull(i, jdbcType.TYPE_CODE);
			} catch (SQLException e) {
				throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
						+ "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. "
						+ "Cause: " + e, e);
			}
		} else {
			try {
				setNonNullParameter(ps, i, parameter, jdbcType);
			} catch (Exception e) {
				throw new TypeException(
						"Error setting non null for parameter #" + i + " with JdbcType " + jdbcType + " . "
								+ "Try setting a different JdbcType for this parameter or a different configuration property. "
								+ "Cause: " + e,
						e);
			}
		}
	}

	/**
	 * Gets result.
	 *
	 * @param rs         the rs
	 * @param columnName the column name
	 * @return the result
	 * @throws SQLException the sql exception
	 */
	@Override
	public T getResult(ResultSet rs, String columnName) throws SQLException {
		T result;
		try {
			result = getNullableResult(rs, columnName);
		} catch (Exception e) {
			throw new ResultMapException(
					"Error attempting to get column '" + columnName + "' from result set.  Cause: " + e, e);
		}
		if (rs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	/**
	 * Gets result.
	 *
	 * @param rs          the rs
	 * @param columnIndex the column index
	 * @return the result
	 * @throws SQLException the sql exception
	 */
	@Override
	public T getResult(ResultSet rs, int columnIndex) throws SQLException {
		T result;
		try {
			result = getNullableResult(rs, columnIndex);
		} catch (Exception e) {
			throw new ResultMapException(
					"Error attempting to get column #" + columnIndex + " from result set.  Cause: " + e, e);
		}
		if (rs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	/**
	 * Gets result.
	 *
	 * @param cs          the cs
	 * @param columnIndex the column index
	 * @return the result
	 * @throws SQLException the sql exception
	 */
	@Override
	public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
		T result;
		try {
			result = getNullableResult(cs, columnIndex);
		} catch (Exception e) {
			throw new ResultMapException(
					"Error attempting to get column #" + columnIndex + " from callable statement.  Cause: " + e, e);
		}
		if (cs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	/**
	 * Sets non null parameter.
	 *
	 * @param ps        the ps
	 * @param i         the
	 * @param parameter the parameter
	 * @param jdbcType  the jdbc type
	 * @throws SQLException the sql exception
	 */
	public abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
			throws SQLException;

	/**
	 * Gets nullable result.
	 *
	 * @param rs         the rs
	 * @param columnName the column name
	 * @return the nullable result
	 * @throws SQLException the sql exception
	 */
	public abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

	/**
	 * Gets nullable result.
	 *
	 * @param rs          the rs
	 * @param columnIndex the column index
	 * @return the nullable result
	 * @throws SQLException the sql exception
	 */
	public abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

	/**
	 * Gets nullable result.
	 *
	 * @param cs          the cs
	 * @param columnIndex the column index
	 * @return the nullable result
	 * @throws SQLException the sql exception
	 */
	public abstract T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException;

}
