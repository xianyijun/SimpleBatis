/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.parse;

/**
 * The interface Token handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface TokenHandler {
	/**
	 * Handle token string.
	 *
	 * @param content the content
	 * @return the string
	 */
	String handleToken(String content);
}
