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
 * The type String type handler.
 *
 * @author Clinton Begin
 */
public class StringTypeHandler extends BaseTypeHandler<String> {

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
  public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setString(i, parameter);
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
  public String getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    return rs.getString(columnName);
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
  public String getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    return rs.getString(columnIndex);
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
  public String getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    return cs.getString(columnIndex);
  }
}
