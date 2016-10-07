/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.wrapper;

import cn.xianyijun.orm.reflection.MetaObject;

public interface ObjectWrapperFactory {

	boolean hasWrapperFor(Object object);

	ObjectWrapper getWrapperFor(MetaObject metaObject, Object object);

}
