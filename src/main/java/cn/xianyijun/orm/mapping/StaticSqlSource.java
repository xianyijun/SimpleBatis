/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

import java.util.List;

import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;

/**
 * The type Static sql source.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class StaticSqlSource implements SqlSession.SqlSource {

  private String sql;
  private List<ParameterMapping> parameterMappings;
  private StatementHandler.Configuration configuration;

  /**
   * Instantiates a new Static sql source.
   *
   * @param configuration the configuration
   * @param sql           the sql
   */
  public StaticSqlSource(StatementHandler.Configuration configuration, String sql) {
    this(configuration, sql, null);
  }

  /**
   * Instantiates a new Static sql source.
   *
   * @param configuration     the configuration
   * @param sql               the sql
   * @param parameterMappings the parameter mappings
   */
  public StaticSqlSource(StatementHandler.Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
    this.sql = sql;
    this.parameterMappings = parameterMappings;
    this.configuration = configuration;
  }

  /**
   * Gets bound sql.
   *
   * @param parameterObject the parameter object
   * @return the bound sql
   */
  @Override
  public StatementHandler.BoundSql getBoundSql(Object parameterObject) {
    return new StatementHandler.BoundSql(configuration, sql, parameterMappings, parameterObject);
  }

}
