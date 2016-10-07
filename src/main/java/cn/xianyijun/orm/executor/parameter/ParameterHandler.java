/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The interface Parameter handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface ParameterHandler {
	/**
	 * Gets parameter object.
	 *
	 * @return the parameter object
	 */
	Object getParameterObject();

	/**
	 * Sets parameters.
	 *
	 * @param ps the ps
	 * @throws SQLException the sql exception
	 */
	void setParameters(PreparedStatement ps) throws SQLException;

}
