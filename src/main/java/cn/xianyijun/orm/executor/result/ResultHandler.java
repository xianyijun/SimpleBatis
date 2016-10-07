/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.result;

/**
 * The interface Result handler.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public interface ResultHandler<T> {

	/**
	 * Handle result.
	 *
	 * @param resultContext the result context
	 */
	void handleResult(ResultContext<? extends T> resultContext);

}
