/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import java.util.List;
import java.util.Properties;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.reflection.ObjectFactory;

/**
 * The interface Proxy factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface ProxyFactory {

  /**
   * Sets properties.
   *
   * @param properties the properties
   */
  void setProperties(Properties properties);

  /**
   * Create proxy object.
   *
   * @param target              the target
   * @param lazyLoader          the lazy loader
   * @param configuration       the configuration
   * @param objectFactory       the object factory
   * @param constructorArgTypes the constructor arg types
   * @param constructorArgs     the constructor args
   * @return the object
   */
  Object createProxy(Object target, ResultLoaderMap lazyLoader, StatementHandler.Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);
  
}
