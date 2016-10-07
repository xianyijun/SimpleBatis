/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.List;

/**
 * The type Mixed sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class MixedSqlNode implements SqlNode {
  private List<SqlNode> contents;

  /**
   * Instantiates a new Mixed sql node.
   *
   * @param contents the contents
   */
  public MixedSqlNode(List<SqlNode> contents) {
    this.contents = contents;
  }

  /**
   * Apply boolean.
   *
   * @param context the context
   * @return the boolean
   */
  @Override
  public boolean apply(DynamicContext context) {
    for (SqlNode sqlNode : contents) {
      sqlNode.apply(context);
    }
    return true;
  }
}
