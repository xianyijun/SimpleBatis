/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.Arrays;
import java.util.List;

import cn.xianyijun.orm.core.StatementHandler;

/**
 * The type Where sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class WhereSqlNode extends TrimSqlNode {

  private static List<String> prefixList = Arrays.asList("AND ","OR ","AND\n", "OR\n", "AND\r", "OR\r", "AND\t", "OR\t");

  /**
   * Instantiates a new Where sql node.
   *
   * @param configuration the configuration
   * @param contents      the contents
   */
  public WhereSqlNode(StatementHandler.Configuration configuration, SqlNode contents) {
    super(configuration, contents, "WHERE", prefixList, null, null);
  }

}
