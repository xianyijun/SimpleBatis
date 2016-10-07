/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import cn.xianyijun.orm.exception.ObjectStreamException;

/**
 * The interface Write replace interface.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface WriteReplaceInterface {

  /**
   * Write replace object.
   *
   * @return the object
   * @throws ObjectStreamException the object stream exception
   */
  Object writeReplace() throws ObjectStreamException;

}
