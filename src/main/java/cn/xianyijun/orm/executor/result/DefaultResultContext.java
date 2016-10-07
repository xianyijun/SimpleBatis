/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.result;

/**
 * The type Default result context.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultResultContext<T> implements ResultContext<T> {

	private T resultObject;
	private int resultCount;
	private boolean stopped;

	/**
	 * Instantiates a new Default result context.
	 */
	public DefaultResultContext() {
		resultObject = null;
		resultCount = 0;
		stopped = false;
	}

	/**
	 * Gets result object.
	 *
	 * @return the result object
	 */
	@Override
	public T getResultObject() {
		return resultObject;
	}

	/**
	 * Gets result count.
	 *
	 * @return the result count
	 */
	@Override
	public int getResultCount() {
		return resultCount;
	}

	/**
	 * Is stopped boolean.
	 *
	 * @return the boolean
	 */
	@Override
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * Next result object.
	 *
	 * @param resultObject the result object
	 */
	public void nextResultObject(T resultObject) {
		resultCount++;
		this.resultObject = resultObject;
	}

	/**
	 * Stop.
	 */
	@Override
	public void stop() {
		this.stopped = true;
	}

}
