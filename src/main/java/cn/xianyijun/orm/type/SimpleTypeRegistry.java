/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */
package cn.xianyijun.orm.type;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Simple type registry.
 *
 * @author Clinton Begin
 */
public class SimpleTypeRegistry {

  private static final Set<Class<?>> SIMPLE_TYPE_SET = new HashSet<Class<?>>();

  static {
    SIMPLE_TYPE_SET.add(String.class);
    SIMPLE_TYPE_SET.add(Byte.class);
    SIMPLE_TYPE_SET.add(Short.class);
    SIMPLE_TYPE_SET.add(Character.class);
    SIMPLE_TYPE_SET.add(Integer.class);
    SIMPLE_TYPE_SET.add(Long.class);
    SIMPLE_TYPE_SET.add(Float.class);
    SIMPLE_TYPE_SET.add(Double.class);
    SIMPLE_TYPE_SET.add(Boolean.class);
    SIMPLE_TYPE_SET.add(Date.class);
    SIMPLE_TYPE_SET.add(Class.class);
    SIMPLE_TYPE_SET.add(BigInteger.class);
    SIMPLE_TYPE_SET.add(BigDecimal.class);
  }

  private SimpleTypeRegistry() {
    // Prevent Instantiation
  }

  /**
   * Is simple type boolean.
   *
   * @param clazz the clazz
   * @return the boolean
   */
/*
   * Tells us if the class passed in is a known common type
   *
   * @param clazz The class to check
   * @return True if the class is known
   */
  public static boolean isSimpleType(Class<?> clazz) {
    return SIMPLE_TYPE_SET.contains(clazz);
  }

}
