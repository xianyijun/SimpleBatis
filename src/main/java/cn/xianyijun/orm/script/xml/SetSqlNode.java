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
 * The type Set sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SetSqlNode extends TrimSqlNode {

  private static List<String> suffixList = Arrays.asList(",");

  /**
   * Instantiates a new Set sql node.
   *
   * @param configuration the configuration
   * @param contents      the contents
   */
  public SetSqlNode(StatementHandler.Configuration configuration, SqlNode contents) {
    super(configuration, contents, "SET", null, null, suffixList);
  }

}
