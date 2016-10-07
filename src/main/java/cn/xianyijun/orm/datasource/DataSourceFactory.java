/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.datasource;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * The interface Data source factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface DataSourceFactory {

  /**
   * Sets properties.
   *
   * @param props the props
   */
  void setProperties(Properties props);

  /**
   * Gets data source.
   *
   * @return the data source
   */
  DataSource getDataSource();

}
