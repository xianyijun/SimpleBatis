/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import java.util.List;
import java.util.Map;

import cn.xianyijun.orm.reflection.ObjectFactory;

/**
 * The type Javassist serial state holder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
class JavassistSerialStateHolder extends AbstractSerialStateHolder {

  private static final long serialVersionUID = 8940388717901644661L;

  /**
   * Instantiates a new Javassist serial state holder.
   */
  public JavassistSerialStateHolder() {
  }

  /**
   * Instantiates a new Javassist serial state holder.
   *
   * @param userBean            the user bean
   * @param unloadedProperties  the unloaded properties
   * @param objectFactory       the object factory
   * @param constructorArgTypes the constructor arg types
   * @param constructorArgs     the constructor args
   */
  public JavassistSerialStateHolder(
          final Object userBean,
          final Map<String, ResultLoaderMap.LoadPair> unloadedProperties,
          final ObjectFactory objectFactory,
          List<Class<?>> constructorArgTypes,
          List<Object> constructorArgs) {
    super(userBean, unloadedProperties, objectFactory, constructorArgTypes, constructorArgs);
  }

  /**
   * Create deserialization proxy object.
   *
   * @param target              the target
   * @param unloadedProperties  the unloaded properties
   * @param objectFactory       the object factory
   * @param constructorArgTypes the constructor arg types
   * @param constructorArgs     the constructor args
   * @return the object
   */
  @Override
  protected Object createDeserializationProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
          List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    return new JavassistProxyFactory().createDeserializationProxy(target, unloadedProperties, objectFactory, constructorArgTypes, constructorArgs);
  }
}
