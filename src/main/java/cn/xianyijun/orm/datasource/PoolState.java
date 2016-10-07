/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.datasource;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Pool state.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class PoolState {

  /**
   * The Data source.
   */
  protected PooledDataSource dataSource;

  /**
   * The Idle connections.
   */
  protected final List<PooledConnection> idleConnections = new ArrayList<>();
  /**
   * The Active connections.
   */
  protected final List<PooledConnection> activeConnections = new ArrayList<>();
  /**
   * The Request count.
   */
  protected long requestCount = 0;
  /**
   * The Accumulated request time.
   */
  protected long accumulatedRequestTime = 0;
  /**
   * The Accumulated checkout time.
   */
  protected long accumulatedCheckoutTime = 0;
  /**
   * The Claimed overdue connection count.
   */
  protected long claimedOverdueConnectionCount = 0;
  /**
   * The Accumulated checkout time of overdue connections.
   */
  protected long accumulatedCheckoutTimeOfOverdueConnections = 0;
  /**
   * The Accumulated wait time.
   */
  protected long accumulatedWaitTime = 0;
  /**
   * The Had to wait count.
   */
  protected long hadToWaitCount = 0;
  /**
   * The Bad connection count.
   */
  protected long badConnectionCount = 0;

  /**
   * Instantiates a new Pool state.
   *
   * @param dataSource the data source
   */
  public PoolState(PooledDataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Gets request count.
   *
   * @return the request count
   */
  public synchronized long getRequestCount() {
    return requestCount;
  }

  /**
   * Gets average request time.
   *
   * @return the average request time
   */
  public synchronized long getAverageRequestTime() {
    return requestCount == 0 ? 0 : accumulatedRequestTime / requestCount;
  }

  /**
   * Gets average wait time.
   *
   * @return the average wait time
   */
  public synchronized long getAverageWaitTime() {
    return hadToWaitCount == 0 ? 0 : accumulatedWaitTime / hadToWaitCount;

  }

  /**
   * Gets had to wait count.
   *
   * @return the had to wait count
   */
  public synchronized long getHadToWaitCount() {
    return hadToWaitCount;
  }

  /**
   * Gets bad connection count.
   *
   * @return the bad connection count
   */
  public synchronized long getBadConnectionCount() {
    return badConnectionCount;
  }

  /**
   * Gets claimed overdue connection count.
   *
   * @return the claimed overdue connection count
   */
  public synchronized long getClaimedOverdueConnectionCount() {
    return claimedOverdueConnectionCount;
  }

  /**
   * Gets average overdue checkout time.
   *
   * @return the average overdue checkout time
   */
  public synchronized long getAverageOverdueCheckoutTime() {
    return claimedOverdueConnectionCount == 0 ? 0 : accumulatedCheckoutTimeOfOverdueConnections / claimedOverdueConnectionCount;
  }

  /**
   * Gets average checkout time.
   *
   * @return the average checkout time
   */
  public synchronized long getAverageCheckoutTime() {
    return requestCount == 0 ? 0 : accumulatedCheckoutTime / requestCount;
  }


  /**
   * Gets idle connection count.
   *
   * @return the idle connection count
   */
  public synchronized int getIdleConnectionCount() {
    return idleConnections.size();
  }

  /**
   * Gets active connection count.
   *
   * @return the active connection count
   */
  public synchronized int getActiveConnectionCount() {
    return activeConnections.size();
  }

  /**
   * To string string.
   *
   * @return the string
   */
  @Override
  public synchronized String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("\n===CONFINGURATION==============================================");
    builder.append("\n jdbcDriver                     ").append(dataSource.getDriver());
    builder.append("\n jdbcUrl                        ").append(dataSource.getUrl());
    builder.append("\n jdbcUsername                   ").append(dataSource.getUsername());
    builder.append("\n poolMaxActiveConnections       ").append(dataSource.poolMaximumActiveConnections);
    builder.append("\n poolMaxIdleConnections         ").append(dataSource.poolMaximumIdleConnections);
    builder.append("\n poolMaxCheckoutTime            ").append(dataSource.poolMaximumCheckoutTime);
    builder.append("\n poolTimeToWait                 ").append(dataSource.poolTimeToWait);
    builder.append("\n poolPingEnabled                ").append(dataSource.poolPingEnabled);
    builder.append("\n poolPingQuery                  ").append(dataSource.poolPingQuery);
    builder.append("\n poolPingConnectionsNotUsedFor  ").append(dataSource.poolPingConnectionsNotUsedFor);
    builder.append("\n ---STATUS-----------------------------------------------------");
    builder.append("\n activeConnections              ").append(getActiveConnectionCount());
    builder.append("\n idleConnections                ").append(getIdleConnectionCount());
    builder.append("\n requestCount                   ").append(getRequestCount());
    builder.append("\n averageRequestTime             ").append(getAverageRequestTime());
    builder.append("\n averageCheckoutTime            ").append(getAverageCheckoutTime());
    builder.append("\n claimedOverdue                 ").append(getClaimedOverdueConnectionCount());
    builder.append("\n averageOverdueCheckoutTime     ").append(getAverageOverdueCheckoutTime());
    builder.append("\n hadToWait                      ").append(getHadToWaitCount());
    builder.append("\n averageWaitTime                ").append(getAverageWaitTime());
    builder.append("\n badConnectionCount             ").append(getBadConnectionCount());
    builder.append("\n===============================================================");
    return builder.toString();
  }

}
