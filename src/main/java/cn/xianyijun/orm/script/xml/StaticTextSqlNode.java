/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

/**
 * The type Static text sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class StaticTextSqlNode implements SqlNode {
  private String text;

  /**
   * Instantiates a new Static text sql node.
   *
   * @param text the text
   */
  public StaticTextSqlNode(String text) {
    this.text = text;
  }

  /**
   * Apply boolean.
   *
   * @param context the context
   * @return the boolean
   */
  @Override
  public boolean apply(DynamicContext context) {
    context.appendSql(text);
    return true;
  }

}