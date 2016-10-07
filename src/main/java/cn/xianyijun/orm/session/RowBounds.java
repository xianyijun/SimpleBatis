/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.session;

/**
 * The type Row bounds.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class RowBounds {

	/**
	 * The constant NO_ROW_OFFSET.
	 */
	public static final int NO_ROW_OFFSET = 0;
	/**
	 * The constant NO_ROW_LIMIT.
	 */
	public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;
	/**
	 * The constant DEFAULT.
	 */
	public static final RowBounds DEFAULT = new RowBounds();

	private int offset;
	private int limit;

	/**
	 * Instantiates a new Row bounds.
	 */
	public RowBounds() {
		this.offset = NO_ROW_OFFSET;
		this.limit = NO_ROW_LIMIT;
	}

	/**
	 * Instantiates a new Row bounds.
	 *
	 * @param offset the offset
	 * @param limit  the limit
	 */
	public RowBounds(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	/**
	 * Gets offset.
	 *
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Gets limit.
	 *
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

}
