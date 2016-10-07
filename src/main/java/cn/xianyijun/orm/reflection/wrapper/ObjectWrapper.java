/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.wrapper;

import java.util.List;

import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.reflection.ObjectFactory;
import cn.xianyijun.orm.reflection.property.PropertyTokenizer;

public interface ObjectWrapper {

	Object get(PropertyTokenizer prop);

	void set(PropertyTokenizer prop, Object value);

	String findProperty(String name, boolean useCamelCaseMapping);

	String[] getGetterNames();

	String[] getSetterNames();

	Class<?> getSetterType(String name);

	Class<?> getGetterType(String name);

	boolean hasSetter(String name);

	boolean hasGetter(String name);

	MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

	boolean isCollection();

	void add(Object element);

	<E> void addAll(List<E> element);

}
