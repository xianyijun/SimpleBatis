/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.wrapper;

import cn.xianyijun.orm.exception.ReflectionException;
import cn.xianyijun.orm.reflection.MetaObject;

public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

	@Override
	public boolean hasWrapperFor(Object object) {
		return false;
	}

	@Override
	public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
		throw new ReflectionException(
				"The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
	}

}
