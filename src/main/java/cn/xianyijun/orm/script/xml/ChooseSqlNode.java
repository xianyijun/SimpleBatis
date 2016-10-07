/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.List;

/**
 * The type Choose sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ChooseSqlNode implements SqlNode {
  private SqlNode defaultSqlNode;
  private List<SqlNode> ifSqlNodes;

  /**
   * Instantiates a new Choose sql node.
   *
   * @param ifSqlNodes     the if sql nodes
   * @param defaultSqlNode the default sql node
   */
  public ChooseSqlNode(List<SqlNode> ifSqlNodes, SqlNode defaultSqlNode) {
    this.ifSqlNodes = ifSqlNodes;
    this.defaultSqlNode = defaultSqlNode;
  }

  /**
   * Apply boolean.
   *
   * @param context the context
   * @return the boolean
   */
  @Override
  public boolean apply(DynamicContext context) {
    for (SqlNode sqlNode : ifSqlNodes) {
      if (sqlNode.apply(context)) {
        return true;
      }
    }
    if (defaultSqlNode != null) {
      defaultSqlNode.apply(context);
      return true;
    }
    return false;
  }
}
