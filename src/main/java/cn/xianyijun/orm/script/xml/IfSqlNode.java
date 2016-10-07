/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

/**
 * The type If sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class IfSqlNode implements SqlNode {
  private ExpressionEvaluator evaluator;
  private String test;
  private SqlNode contents;

  /**
   * Instantiates a new If sql node.
   *
   * @param contents the contents
   * @param test     the test
   */
  public IfSqlNode(SqlNode contents, String test) {
    this.test = test;
    this.contents = contents;
    this.evaluator = new ExpressionEvaluator();
  }

  /**
   * Apply boolean.
   *
   * @param context the context
   * @return the boolean
   */
  @Override
  public boolean apply(DynamicContext context) {
    if (evaluator.evaluateBoolean(test, context.getBindings())) {
      contents.apply(context);
      return true;
    }
    return false;
  }

}
