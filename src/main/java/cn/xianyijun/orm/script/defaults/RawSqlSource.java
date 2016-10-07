/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.defaults;

import java.util.HashMap;

import cn.xianyijun.orm.builder.SqlSourceBuilder;
import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.script.xml.DynamicContext;
import cn.xianyijun.orm.script.xml.SqlNode;

/**
 * The type Raw sql source.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class RawSqlSource implements SqlSession.SqlSource {

  private final SqlSession.SqlSource sqlSource;

  /**
   * Instantiates a new Raw sql source.
   *
   * @param configuration the configuration
   * @param rootSqlNode   the root sql node
   * @param parameterType the parameter type
   */
  public RawSqlSource(StatementHandler.Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType) {
    this(configuration, getSql(configuration, rootSqlNode), parameterType);
  }

  /**
   * Instantiates a new Raw sql source.
   *
   * @param configuration the configuration
   * @param sql           the sql
   * @param parameterType the parameter type
   */
  public RawSqlSource(StatementHandler.Configuration configuration, String sql, Class<?> parameterType) {
    SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
    Class<?> clazz = parameterType == null ? Object.class : parameterType;
    sqlSource = sqlSourceParser.parse(sql, clazz, new HashMap<String, Object>());
  }

  private static String getSql(StatementHandler.Configuration configuration, SqlNode rootSqlNode) {
    DynamicContext context = new DynamicContext(configuration, null);
    rootSqlNode.apply(context);
    return context.getSql();
  }

  /**
   * Gets bound sql.
   *
   * @param parameterObject the parameter object
   * @return the bound sql
   */
  @Override
  public StatementHandler.BoundSql getBoundSql(Object parameterObject) {
    return sqlSource.getBoundSql(parameterObject);
  }

}
