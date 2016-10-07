/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

import java.sql.ResultSet;

/**
 * The enum Result set type.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public enum ResultSetType {
	/**
	 * Forward only result set type.
	 */
	FORWARD_ONLY(ResultSet.TYPE_FORWARD_ONLY), /**
	 * Scroll insensitive result set type.
	 */
	SCROLL_INSENSITIVE(ResultSet.TYPE_SCROLL_INSENSITIVE), /**
	 * Scroll sensitive result set type.
	 */
	SCROLL_SENSITIVE(
			ResultSet.TYPE_SCROLL_SENSITIVE);

	private int value;

	ResultSetType(int value) {
		this.value = value;
	}

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
}
