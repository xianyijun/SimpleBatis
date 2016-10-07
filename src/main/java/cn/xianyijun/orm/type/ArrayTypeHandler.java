
/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.type;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * The type Array type handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ArrayTypeHandler extends BaseTypeHandler<Object> {

  /**
   * Instantiates a new Array type handler.
   */
  public ArrayTypeHandler() {
    super();
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
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
    ps.setArray(i, (Array) parameter);
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
    Array array = rs.getArray(columnName);
    return array == null ? null : array.getArray();
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
    Array array = rs.getArray(columnIndex);
    return array == null ? null : array.getArray();
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
    Array array = cs.getArray(columnIndex);
    return array == null ? null : array.getArray();
  }

}
