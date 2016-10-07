/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.core;

import java.sql.*;
import java.util.List;

import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.type.JdbcType;

/**
 * The interface Result set handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface ResultSetHandler {

	/**
	 * Handle result sets list.
	 *
	 * @param <E>  the type parameter
	 * @param stmt the stmt
	 * @return the list
	 * @throws SQLException the sql exception
	 */
	<E> List<E> handleResultSets(Statement stmt) throws SQLException;

	/**
	 * Handle cursor result sets cursor.
	 *
	 * @param <E>  the type parameter
	 * @param stmt the stmt
	 * @return the cursor
	 * @throws SQLException the sql exception
	 */
	<E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

	/**
	 * Handle output parameters.
	 *
	 * @param cs the cs
	 * @throws SQLException the sql exception
	 */
	void handleOutputParameters(CallableStatement cs) throws SQLException;

	/**
     * The interface Type handler.
     *
     * @param <T> the type parameter
     * @author xianyijun xianyijun0@gmail.com
     */
	interface TypeHandler<T> {

        /**
         * Sets parameter.
         *
         * @param ps        the ps
         * @param i         the
         * @param parameter the parameter
         * @param jdbcType  the jdbc type
         * @throws SQLException the sql exception
         */
        void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

        /**
         * Gets result.
         *
         * @param rs         the rs
         * @param columnName the column name
         * @return the result
         * @throws SQLException the sql exception
         */
        T getResult(ResultSet rs, String columnName) throws SQLException;

        /**
         * Gets result.
         *
         * @param rs          the rs
         * @param columnIndex the column index
         * @return the result
         * @throws SQLException the sql exception
         */
        T getResult(ResultSet rs, int columnIndex) throws SQLException;

        /**
         * Gets result.
         *
         * @param cs          the cs
         * @param columnIndex the column index
         * @return the result
         * @throws SQLException the sql exception
         */
        T getResult(CallableStatement cs, int columnIndex) throws SQLException;

    }
}
