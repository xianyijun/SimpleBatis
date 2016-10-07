/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.result;

import cn.xianyijun.orm.reflection.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Default result handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultResultHandler implements ResultHandler<Object> {

	private final List<Object> list;

	/**
	 * Instantiates a new Default result handler.
	 */
	public DefaultResultHandler() {
		list = new ArrayList<Object>();
	}

	/**
	 * Instantiates a new Default result handler.
	 *
	 * @param objectFactory the object factory
	 */
	@SuppressWarnings("unchecked")
	public DefaultResultHandler(ObjectFactory objectFactory) {
		list = objectFactory.create(List.class);
	}

	/**
	 * Handle result.
	 *
	 * @param context the context
	 */
	@Override
	public void handleResult(ResultContext<? extends Object> context) {
		list.add(context.getResultObject());
	}

	/**
	 * Gets result list.
	 *
	 * @return the result list
	 */
	public List<Object> getResultList() {
		return list;
	}

}