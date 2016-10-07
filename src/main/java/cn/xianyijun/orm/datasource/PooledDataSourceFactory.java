/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.datasource;

/**
 * The type Pooled data source factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class PooledDataSourceFactory extends UnpooledDataSourceFactory implements DataSourceFactory {

  /**
   * Instantiates a new Pooled data source factory.
   */
  public PooledDataSourceFactory() {
    this.dataSource = new PooledDataSource();
  }

}
