/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.io.Resources;
import cn.xianyijun.orm.mapping.ResultMap;
import cn.xianyijun.orm.type.JdbcType;
import cn.xianyijun.orm.type.ObjectTypeHandler;
import cn.xianyijun.orm.type.TypeHandlerRegistry;
import cn.xianyijun.orm.type.UnknownTypeHandler;

/**
 * The type Result set wrapper.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResultSetWrapper {

	private final ResultSet resultSet;
	private final TypeHandlerRegistry typeHandlerRegistry;
	private final List<String> columnNames = new ArrayList<String>();
	private final List<String> classNames = new ArrayList<String>();
	private final List<JdbcType> jdbcTypes = new ArrayList<JdbcType>();
	private final Map<String, Map<Class<?>, ResultSetHandler.TypeHandler<?>>> typeHandlerMap = new HashMap<String, Map<Class<?>, ResultSetHandler.TypeHandler<?>>>();
	private Map<String, List<String>> mappedColumnNamesMap = new HashMap<String, List<String>>();
	private Map<String, List<String>> unMappedColumnNamesMap = new HashMap<String, List<String>>();

	/**
	 * Instantiates a new Result set wrapper.
	 *
	 * @param rs            the rs
	 * @param configuration the configuration
	 * @throws SQLException the sql exception
	 */
	public ResultSetWrapper(ResultSet rs, StatementHandler.Configuration configuration) throws SQLException {
		super();
		this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		this.resultSet = rs;
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			columnNames.add(configuration.isUseColumnLabel() ? metaData.getColumnLabel(i) : metaData.getColumnName(i));
			jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
			classNames.add(metaData.getColumnClassName(i));
		}
	}

	/**
	 * Gets result set.
	 *
	 * @return the result set
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}

	/**
	 * Gets column names.
	 *
	 * @return the column names
	 */
	public List<String> getColumnNames() {
		return this.columnNames;
	}

	/**
	 * Gets class names.
	 *
	 * @return the class names
	 */
	public List<String> getClassNames() {
		return Collections.unmodifiableList(classNames);
	}

	/**
	 * Gets jdbc type.
	 *
	 * @param columnName the column name
	 * @return the jdbc type
	 */
	public JdbcType getJdbcType(String columnName) {
		for (int i = 0; i < columnNames.size(); i++) {
			if (columnNames.get(i).equalsIgnoreCase(columnName)) {
				return jdbcTypes.get(i);
			}
		}
		return null;
	}

	/**
	 * Gets type handler.
	 *
	 * @param propertyType the property type
	 * @param columnName   the column name
	 * @return the type handler
	 */
	public ResultSetHandler.TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
		ResultSetHandler.TypeHandler<?> handler = null;
		Map<Class<?>, ResultSetHandler.TypeHandler<?>> columnHandlers = typeHandlerMap.get(columnName);
		if (columnHandlers == null) {
			columnHandlers = new HashMap<Class<?>, ResultSetHandler.TypeHandler<?>>();
			typeHandlerMap.put(columnName, columnHandlers);
		} else {
			handler = columnHandlers.get(propertyType);
		}
		if (handler == null) {
			JdbcType jdbcType = getJdbcType(columnName);
			handler = typeHandlerRegistry.getTypeHandler(propertyType, jdbcType);
			if (handler == null || handler instanceof UnknownTypeHandler) {
				final int index = columnNames.indexOf(columnName);
				final Class<?> javaType = resolveClass(classNames.get(index));
				if (javaType != null && jdbcType != null) {
					handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
				} else if (javaType != null) {
					handler = typeHandlerRegistry.getTypeHandler(javaType);
				} else if (jdbcType != null) {
					handler = typeHandlerRegistry.getTypeHandler(jdbcType);
				}
			}
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = new ObjectTypeHandler();
			}
			columnHandlers.put(propertyType, handler);
		}
		return handler;
	}

	private Class<?> resolveClass(String className) {
		try {
			// #699 className could be null
			if (className != null) {
				return Resources.classForName(className);
			}
		} catch (ClassNotFoundException e) {
			// ignore
		}
		return null;
	}

	private void loadMappedAndUnmappedColumnNames(ResultMap resultMap, String columnPrefix) throws SQLException {
		List<String> mappedColumnNames = new ArrayList<String>();
		List<String> unmappedColumnNames = new ArrayList<String>();
		final String upperColumnPrefix = columnPrefix == null ? null : columnPrefix.toUpperCase(Locale.ENGLISH);
		final Set<String> mappedColumns = prependPrefixes(resultMap.getMappedColumns(), upperColumnPrefix);
		for (String columnName : columnNames) {
			final String upperColumnName = columnName.toUpperCase(Locale.ENGLISH);
			if (mappedColumns.contains(upperColumnName)) {
				mappedColumnNames.add(upperColumnName);
			} else {
				unmappedColumnNames.add(columnName);
			}
		}
		mappedColumnNamesMap.put(getMapKey(resultMap, columnPrefix), mappedColumnNames);
		unMappedColumnNamesMap.put(getMapKey(resultMap, columnPrefix), unmappedColumnNames);
	}

	/**
	 * Gets mapped column names.
	 *
	 * @param resultMap    the result map
	 * @param columnPrefix the column prefix
	 * @return the mapped column names
	 * @throws SQLException the sql exception
	 */
	public List<String> getMappedColumnNames(ResultMap resultMap, String columnPrefix) throws SQLException {
		List<String> mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		if (mappedColumnNames == null) {
			loadMappedAndUnmappedColumnNames(resultMap, columnPrefix);
			mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		}
		return mappedColumnNames;
	}

	/**
	 * Gets unmapped column names.
	 *
	 * @param resultMap    the result map
	 * @param columnPrefix the column prefix
	 * @return the unmapped column names
	 * @throws SQLException the sql exception
	 */
	public List<String> getUnmappedColumnNames(ResultMap resultMap, String columnPrefix) throws SQLException {
		List<String> unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		if (unMappedColumnNames == null) {
			loadMappedAndUnmappedColumnNames(resultMap, columnPrefix);
			unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		}
		return unMappedColumnNames;
	}

	private String getMapKey(ResultMap resultMap, String columnPrefix) {
		return resultMap.getId() + ":" + columnPrefix;
	}

	private Set<String> prependPrefixes(Set<String> columnNames, String prefix) {
		if (columnNames == null || columnNames.isEmpty() || prefix == null || prefix.length() == 0) {
			return columnNames;
		}
		final Set<String> prefixed = new HashSet<String>();
		for (String columnName : columnNames) {
			prefixed.add(prefix + columnName);
		}
		return prefixed;
	}

}
