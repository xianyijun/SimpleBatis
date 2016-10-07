/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

/**
 * The interface Sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface SqlNode {
  /**
   * Apply boolean.
   *
   * @param context the context
   * @return the boolean
   */
  boolean apply(DynamicContext context);
}
