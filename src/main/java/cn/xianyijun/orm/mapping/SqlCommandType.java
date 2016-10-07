/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

/**
 * The enum Sql command type.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public enum SqlCommandType {
	/**
	 * Unknown sql command type.
	 */
	UNKNOWN, /**
	 * Insert sql command type.
	 */
	INSERT, /**
	 * Update sql command type.
	 */
	UPDATE, /**
	 * Delete sql command type.
	 */
	DELETE, /**
	 * Select sql command type.
	 */
	SELECT;
}
