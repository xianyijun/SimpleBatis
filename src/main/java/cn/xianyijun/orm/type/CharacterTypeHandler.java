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

/**
 * The type Character type handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class CharacterTypeHandler extends BaseTypeHandler<Character> {

  /**
   * Sets non null parameter.
   *
   * @param ps        the ps
   * @param i         the
   * @param parameter the parameter
   * @param jdbcType  the jdbc type
   * @throws SQLException the sql exception
   */
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Character parameter, JdbcType jdbcType) throws SQLException {
    ps.setString(i, parameter.toString());
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
  public Character getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String columnValue = rs.getString(columnName);
    if (columnValue != null) {
      return columnValue.charAt(0);
    } else {
      return null;
    }
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
  public Character getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String columnValue = rs.getString(columnIndex);
    if (columnValue != null) {
      return columnValue.charAt(0);
    } else {
      return null;
    }
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
  public Character getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String columnValue = cs.getString(columnIndex);
    if (columnValue != null) {
      return columnValue.charAt(0);
    } else {
      return null;
    }
  }
}
