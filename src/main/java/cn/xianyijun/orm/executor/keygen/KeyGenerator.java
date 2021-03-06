/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.keygen;

import java.sql.Statement;

import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.core.MappedStatement;

/**
 * The interface Key generator.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface KeyGenerator {

  /**
   * Process before.
   *
   * @param executor  the executor
   * @param ms        the ms
   * @param stmt      the stmt
   * @param parameter the parameter
   */
  void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter);

  /**
   * Process after.
   *
   * @param executor  the executor
   * @param ms        the ms
   * @param stmt      the stmt
   * @param parameter the parameter
   */
  void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter);

}
