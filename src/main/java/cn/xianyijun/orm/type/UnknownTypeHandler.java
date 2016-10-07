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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.exception.TypeException;
import cn.xianyijun.orm.io.Resources;

/**
 * The type Unknown type handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class UnknownTypeHandler extends BaseTypeHandler<Object> {

	private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();

	private TypeHandlerRegistry typeHandlerRegistry;

	/**
	 * Instantiates a new Unknown type handler.
	 *
	 * @param typeHandlerRegistry the type handler registry
	 */
	public UnknownTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
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
	@SuppressWarnings("unchecked")
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
			throws SQLException {
		ResultSetHandler.TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
		handler.setParameter(ps, i, parameter, jdbcType);
	}

	/**
	 * Gets nullable result.
	 *
	 * @param rs         the rs
	 * @param columnName the column name
	 * @return the nullable result
	 * @throws SQLException the sql exception
	 */
	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		ResultSetHandler.TypeHandler<?> handler = resolveTypeHandler(rs, columnName);
		return handler.getResult(rs, columnName);
	}

	/**
	 * Gets nullable result.
	 *
	 * @param rs          the rs
	 * @param columnIndex the column index
	 * @return the nullable result
	 * @throws SQLException the sql exception
	 */
	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		ResultSetHandler.TypeHandler<?> handler = resolveTypeHandler(rs.getMetaData(), columnIndex);
		if (handler == null || handler instanceof UnknownTypeHandler) {
			handler = OBJECT_TYPE_HANDLER;
		}
		return handler.getResult(rs, columnIndex);
	}

	/**
	 * Gets nullable result.
	 *
	 * @param cs          the cs
	 * @param columnIndex the column index
	 * @return the nullable result
	 * @throws SQLException the sql exception
	 */
	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getObject(columnIndex);
	}

	private ResultSetHandler.TypeHandler<? extends Object> resolveTypeHandler(Object parameter, JdbcType jdbcType) {
		ResultSetHandler.TypeHandler<? extends Object> handler;
		if (parameter == null) {
			handler = OBJECT_TYPE_HANDLER;
		} else {
			handler = typeHandlerRegistry.getTypeHandler(parameter.getClass(), jdbcType);
			// check if handler is null (issue #270)
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = OBJECT_TYPE_HANDLER;
			}
		}
		return handler;
	}

	private ResultSetHandler.TypeHandler<?> resolveTypeHandler(ResultSet rs, String column) {
		try {
			Map<String, Integer> columnIndexLookup;
			columnIndexLookup = new HashMap<String, Integer>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			for (int i = 1; i <= count; i++) {
				String name = rsmd.getColumnName(i);
				columnIndexLookup.put(name, i);
			}
			Integer columnIndex = columnIndexLookup.get(column);
			ResultSetHandler.TypeHandler<?> handler = null;
			if (columnIndex != null) {
				handler = resolveTypeHandler(rsmd, columnIndex);
			}
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = OBJECT_TYPE_HANDLER;
			}
			return handler;
		} catch (SQLException e) {
			throw new TypeException("Error determining JDBC type for column " + column + ".  Cause: " + e, e);
		}
	}

	private ResultSetHandler.TypeHandler<?> resolveTypeHandler(ResultSetMetaData rsmd, Integer columnIndex) throws SQLException {
		ResultSetHandler.TypeHandler<?> handler = null;
		JdbcType jdbcType = safeGetJdbcTypeForColumn(rsmd, columnIndex);
		Class<?> javaType = safeGetClassForColumn(rsmd, columnIndex);
		if (javaType != null && jdbcType != null) {
			handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
		} else if (javaType != null) {
			handler = typeHandlerRegistry.getTypeHandler(javaType);
		} else if (jdbcType != null) {
			handler = typeHandlerRegistry.getTypeHandler(jdbcType);
		}
		return handler;
	}

	private JdbcType safeGetJdbcTypeForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
		try {
			return JdbcType.forCode(rsmd.getColumnType(columnIndex));
		} catch (Exception e) {
			return null;
		}
	}

	private Class<?> safeGetClassForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
		try {
			return Resources.classForName(rsmd.getColumnClassName(columnIndex));
		} catch (Exception e) {
			return null;
		}
	}
}
