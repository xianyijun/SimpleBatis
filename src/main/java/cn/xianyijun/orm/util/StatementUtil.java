/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.util;

import java.sql.SQLException;
import java.sql.Statement;

/**
 * The type Statement util.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class StatementUtil {
	private StatementUtil() {

	}

	/**
	 * Apply transaction timeout.
	 *
	 * @param statement          the statement
	 * @param queryTimeout       the query timeout
	 * @param transactionTimeout the transaction timeout
	 * @throws SQLException the sql exception
	 */
	public static void applyTransactionTimeout(Statement statement, Integer queryTimeout, Integer transactionTimeout)
			throws SQLException {
		if (transactionTimeout == null) {
			return;
		}
		Integer timeToLiveOfQuery = null;
		if (queryTimeout == null || queryTimeout == 0) {
			timeToLiveOfQuery = transactionTimeout;
		} else if (transactionTimeout < queryTimeout) {
			timeToLiveOfQuery = transactionTimeout;
		}
		if (timeToLiveOfQuery != null) {
			statement.setQueryTimeout(timeToLiveOfQuery);
		}
	}
}
