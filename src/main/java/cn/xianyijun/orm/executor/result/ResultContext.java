/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.result;

/**
 * The interface Result context.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public interface ResultContext<T> {

	/**
	 * Gets result object.
	 *
	 * @return the result object
	 */
	T getResultObject();

	/**
	 * Gets result count.
	 *
	 * @return the result count
	 */
	int getResultCount();

	/**
	 * Is stopped boolean.
	 *
	 * @return the boolean
	 */
	boolean isStopped();

	/**
	 * Stop.
	 */
	void stop();

}
