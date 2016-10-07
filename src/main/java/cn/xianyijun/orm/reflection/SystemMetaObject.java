/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection;

import cn.xianyijun.orm.reflection.reflector.DefaultReflectorFactory;
import cn.xianyijun.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import cn.xianyijun.orm.reflection.wrapper.ObjectWrapperFactory;

/**
 * The type System meta object.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public final class SystemMetaObject {

	/**
	 * The constant DEFAULT_OBJECT_FACTORY.
	 */
	public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	/**
	 * The constant DEFAULT_OBJECT_WRAPPER_FACTORY.
	 */
	public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	/**
	 * The constant NULL_META_OBJECT.
	 */
	public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class, DEFAULT_OBJECT_FACTORY,
			DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());

	private SystemMetaObject() {
	}

	private static class NullObject {
	}

	/**
	 * For object meta object.
	 *
	 * @param object the object
	 * @return the meta object
	 */
	public static MetaObject forObject(Object object) {
		return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY,
				new DefaultReflectorFactory());
	}

}
