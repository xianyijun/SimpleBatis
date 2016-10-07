/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.Properties;

/**
 * The type Pooled data source.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class PooledDataSource implements DataSource {
    private static final Logger logger = LoggerFactory.getLogger(PooledDataSource.class);
    private final PoolState state = new PoolState(this);

    private final UnPooledDataSource dataSource;

    /**
     * The Pool maximum active connections.
     */
    protected int poolMaximumActiveConnections = 10;
    /**
     * The Pool maximum idle connections.
     */
    protected int poolMaximumIdleConnections = 5;
    /**
     * The Pool maximum checkout time.
     */
    protected int poolMaximumCheckoutTime = 20000;
    /**
     * The Pool time to wait.
     */
    protected int poolTimeToWait = 20000;
    /**
     * The Pool ping query.
     */
    protected String poolPingQuery = "NO PING QUERY SET";
    /**
     * The Pool ping enabled.
     */
    protected boolean poolPingEnabled = false;
    /**
     * The Pool ping connections not used for.
     */
    protected int poolPingConnectionsNotUsedFor = 0;

    private int expectedConnectionTypeCode;

    /**
     * Instantiates a new Pooled data source.
     */
    public PooledDataSource() {
        dataSource = new UnPooledDataSource();
    }

    /**
     * Instantiates a new Pooled data source.
     *
     * @param dataSource the data source
     */
    public PooledDataSource(UnPooledDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Instantiates a new Pooled data source.
     *
     * @param driver   the driver
     * @param url      the url
     * @param username the username
     * @param password the password
     */
    public PooledDataSource(String driver, String url, String username, String password) {
        dataSource = new UnPooledDataSource(driver, url, username, password);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
                dataSource.getPassword());
    }

    /**
     * Instantiates a new Pooled data source.
     *
     * @param driver           the driver
     * @param url              the url
     * @param driverProperties the driver properties
     */
    public PooledDataSource(String driver, String url, Properties driverProperties) {
        dataSource = new UnPooledDataSource(driver, url, driverProperties);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
                dataSource.getPassword());
    }

    /**
     * Instantiates a new Pooled data source.
     *
     * @param driverClassLoader the driver class loader
     * @param driver            the driver
     * @param url               the url
     * @param username          the username
     * @param password          the password
     */
    public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username,
                            String password) {
        dataSource = new UnPooledDataSource(driverClassLoader, driver, url, username, password);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
                dataSource.getPassword());
    }

    /**
     * Instantiates a new Pooled data source.
     *
     * @param driverClassLoader the driver class loader
     * @param driver            the driver
     * @param url               the url
     * @param driverProperties  the driver properties
     */
    public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
        dataSource = new UnPooledDataSource(driverClassLoader, driver, url, driverProperties);
        expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
                dataSource.getPassword());
    }

    /**
     * Gets connection.
     *
     * @return the connection
     * @throws SQLException the sql exception
     */
    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(dataSource.getUsername(), dataSource.getPassword()).getProxyConnection();
    }

    /**
     * Gets connection.
     *
     * @param username the username
     * @param password the password
     * @return the connection
     * @throws SQLException the sql exception
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    /**
     * Sets login timeout.
     *
     * @param loginTimeout the login timeout
     * @throws SQLException the sql exception
     */
    @Override
    public void setLoginTimeout(int loginTimeout) throws SQLException {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    /**
     * Gets login timeout.
     *
     * @return the login timeout
     * @throws SQLException the sql exception
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    /**
     * Sets log writer.
     *
     * @param logWriter the log writer
     * @throws SQLException the sql exception
     */
    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        DriverManager.setLogWriter(logWriter);
    }

    /**
     * Gets log writer.
     *
     * @return the log writer
     * @throws SQLException the sql exception
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    /**
     * Sets driver.
     *
     * @param driver the driver
     */
    public void setDriver(String driver) {
        dataSource.setDriver(driver);
        forceCloseAll();
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        dataSource.setUrl(url);
        forceCloseAll();
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        dataSource.setUsername(username);
        forceCloseAll();
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        dataSource.setPassword(password);
        forceCloseAll();
    }

    /**
     * Sets default auto commit.
     *
     * @param defaultAutoCommit the default auto commit
     */
    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        dataSource.setAutoCommit(defaultAutoCommit);
        forceCloseAll();
    }

    /**
     * Sets default transaction isolation level.
     *
     * @param defaultTransactionIsolationLevel the default transaction isolation level
     */
    public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
        dataSource.setDefaultTransactionIsolationLevel(defaultTransactionIsolationLevel);
        forceCloseAll();
    }

    /**
     * Sets driver properties.
     *
     * @param driverProps the driver props
     */
    public void setDriverProperties(Properties driverProps) {
        dataSource.setDriverProperties(driverProps);
        forceCloseAll();
    }

    /**
     * Sets pool maximum active connections.
     *
     * @param poolMaximumActiveConnections the pool maximum active connections
     */
/*
     * The maximum number of active connections
	 *
	 * @param poolMaximumActiveConnections The maximum number of active connections
	 */
    public void setPoolMaximumActiveConnections(int poolMaximumActiveConnections) {
        this.poolMaximumActiveConnections = poolMaximumActiveConnections;
        forceCloseAll();
    }

    /**
     * Sets pool maximum idle connections.
     *
     * @param poolMaximumIdleConnections the pool maximum idle connections
     */
/*
     * The maximum number of idle connections
	 *
	 * @param poolMaximumIdleConnections The maximum number of idle connections
	 */
    public void setPoolMaximumIdleConnections(int poolMaximumIdleConnections) {
        this.poolMaximumIdleConnections = poolMaximumIdleConnections;
        forceCloseAll();
    }

    /**
     * Sets pool maximum checkout time.
     *
     * @param poolMaximumCheckoutTime the pool maximum checkout time
     */
    public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
        this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
        forceCloseAll();
    }

    /**
     * Sets pool time to wait.
     *
     * @param poolTimeToWait the pool time to wait
     */
    public void setPoolTimeToWait(int poolTimeToWait) {
        this.poolTimeToWait = poolTimeToWait;
        forceCloseAll();
    }

    /**
     * Sets pool ping query.
     *
     * @param poolPingQuery the pool ping query
     */
    public void setPoolPingQuery(String poolPingQuery) {
        this.poolPingQuery = poolPingQuery;
        forceCloseAll();
    }

    /**
     * Sets pool ping enabled.
     *
     * @param poolPingEnabled the pool ping enabled
     */
    public void setPoolPingEnabled(boolean poolPingEnabled) {
        this.poolPingEnabled = poolPingEnabled;
        forceCloseAll();
    }

    /**
     * Sets pool ping connections not used for.
     *
     * @param milliseconds the milliseconds
     */
    public void setPoolPingConnectionsNotUsedFor(int milliseconds) {
        this.poolPingConnectionsNotUsedFor = milliseconds;
        forceCloseAll();
    }

    /**
     * Gets driver.
     *
     * @return the driver
     */
    public String getDriver() {
        return dataSource.getDriver();
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return dataSource.getUrl();
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return dataSource.getUsername();
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return dataSource.getPassword();
    }

    /**
     * Is auto commit boolean.
     *
     * @return the boolean
     */
    public boolean isAutoCommit() {
        return dataSource.isAutoCommit();
    }

    /**
     * Gets default transaction isolation level.
     *
     * @return the default transaction isolation level
     */
    public Integer getDefaultTransactionIsolationLevel() {
        return dataSource.getDefaultTransactionIsolationLevel();
    }

    /**
     * Gets driver properties.
     *
     * @return the driver properties
     */
    public Properties getDriverProperties() {
        return dataSource.getDriverProperties();
    }

    /**
     * Gets pool maximum active connections.
     *
     * @return the pool maximum active connections
     */
    public int getPoolMaximumActiveConnections() {
        return poolMaximumActiveConnections;
    }

    /**
     * Gets pool maximum idle connections.
     *
     * @return the pool maximum idle connections
     */
    public int getPoolMaximumIdleConnections() {
        return poolMaximumIdleConnections;
    }

    /**
     * Gets pool maximum checkout time.
     *
     * @return the pool maximum checkout time
     */
    public int getPoolMaximumCheckoutTime() {
        return poolMaximumCheckoutTime;
    }

    /**
     * Gets pool time to wait.
     *
     * @return the pool time to wait
     */
    public int getPoolTimeToWait() {
        return poolTimeToWait;
    }

    /**
     * Gets pool ping query.
     *
     * @return the pool ping query
     */
    public String getPoolPingQuery() {
        return poolPingQuery;
    }

    /**
     * Is pool ping enabled boolean.
     *
     * @return the boolean
     */
    public boolean isPoolPingEnabled() {
        return poolPingEnabled;
    }

    /**
     * Gets pool ping connections not used for.
     *
     * @return the pool ping connections not used for
     */
    public int getPoolPingConnectionsNotUsedFor() {
        return poolPingConnectionsNotUsedFor;
    }

    /**
     * Force close all.
     */
/*
     * Closes all active and idle connections in the pool
	 */
    public void forceCloseAll() {
        synchronized (state) {
            expectedConnectionTypeCode = assembleConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(),
                    dataSource.getPassword());
            for (int i = state.activeConnections.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.activeConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            for (int i = state.idleConnections.size(); i > 0; i--) {
                try {
                    PooledConnection conn = state.idleConnections.remove(i - 1);
                    conn.invalidate();

                    Connection realConn = conn.getRealConnection();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    realConn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Gets pool state.
     *
     * @return the pool state
     */
    public PoolState getPoolState() {
        return state;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return ("" + url + username + password).hashCode();
    }

    /**
     * Push connection.
     *
     * @param conn the conn
     * @throws SQLException the sql exception
     */
    protected void pushConnection(PooledConnection conn) throws SQLException {

        synchronized (state) {
            state.activeConnections.remove(conn);
            if (conn.isValid()) {
                if (state.idleConnections.size() < poolMaximumIdleConnections
                        && conn.getConnectionTypeCode() == expectedConnectionTypeCode) {
                    state.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    PooledConnection newConn = new PooledConnection(conn.getRealConnection(), this);
                    state.idleConnections.add(newConn);
                    newConn.setCreatedTimestamp(conn.getCreatedTimestamp());
                    newConn.setLastUsedTimestamp(conn.getLastUsedTimestamp());
                    conn.invalidate();
                    state.notifyAll();
                } else {
                    state.accumulatedCheckoutTime += conn.getCheckoutTime();
                    if (!conn.getRealConnection().getAutoCommit()) {
                        conn.getRealConnection().rollback();
                    }
                    conn.getRealConnection().close();
                    conn.invalidate();
                }
            } else {
                state.badConnectionCount++;
            }
        }
    }

    private PooledConnection popConnection(String username, String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection conn = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        while (conn == null) {
            synchronized (state) {
                if (!state.idleConnections.isEmpty()) {
                    conn = state.idleConnections.remove(0);
                } else {
                    if (state.activeConnections.size() < poolMaximumActiveConnections) {
                        conn = new PooledConnection(dataSource.getConnection(), this);
                    } else {
                        PooledConnection oldestActiveConnection = state.activeConnections.get(0);
                        long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
                        if (longestCheckoutTime > poolMaximumCheckoutTime) {
                            state.claimedOverdueConnectionCount++;
                            state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
                            state.accumulatedCheckoutTime += longestCheckoutTime;
                            state.activeConnections.remove(oldestActiveConnection);
                            if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                                try {
                                    oldestActiveConnection.getRealConnection().rollback();
                                } catch (SQLException e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                            conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
                            conn.setCreatedTimestamp(oldestActiveConnection.getCreatedTimestamp());
                            conn.setLastUsedTimestamp(oldestActiveConnection.getLastUsedTimestamp());
                            oldestActiveConnection.invalidate();
                        } else {
                            // Must wait
                            try {
                                if (!countedWait) {
                                    state.hadToWaitCount++;
                                    countedWait = true;
                                }
                                long wt = System.currentTimeMillis();
                                state.wait(poolTimeToWait);
                                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                                break;
                            }
                        }
                    }
                }
                if (conn != null) {
                    if (conn.isValid()) {
                        if (!conn.getRealConnection().getAutoCommit()) {
                            conn.getRealConnection().rollback();
                        }
                        conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
                        conn.setCheckoutTimestamp(System.currentTimeMillis());
                        conn.setLastUsedTimestamp(System.currentTimeMillis());
                        state.activeConnections.add(conn);
                        state.requestCount++;
                        state.accumulatedRequestTime += System.currentTimeMillis() - t;
                    } else {
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        conn = null;
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            throw new SQLException(
                                    "PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }

        }

        if (conn == null) {
            throw new SQLException(
                    "PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
        }

        return conn;
    }

    /**
     * Ping connection boolean.
     *
     * @param conn the conn
     * @return the boolean
     */
    protected boolean pingConnection(PooledConnection conn) {
        boolean result = true;

        try {
            result = !conn.getRealConnection().isClosed();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            result = false;
        }

        if (result) {
            if (poolPingEnabled && poolPingConnectionsNotUsedFor >= 0
                    && conn.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
                Connection realConn = null;
                Statement statement = null;
                try {
                    realConn = conn.getRealConnection();
                    statement = realConn.createStatement();
                    ResultSet rs = statement.executeQuery(poolPingQuery);
                    rs.close();
                    statement.close();
                    if (!realConn.getAutoCommit()) {
                        realConn.rollback();
                    }
                    result = true;
                } catch (Exception e) {
                    try {
                        conn.getRealConnection().close();
                    } catch (Exception e2) {
                        //ignore
                    }
                    result = false;
                } finally {
                    try {
                        if (statement != null) {
                            statement.close();
                        }
                    } catch (SQLException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Unwrap connection connection.
     *
     * @param conn the conn
     * @return the connection
     */
    public static Connection unwrapConnection(Connection conn) {
        if (Proxy.isProxyClass(conn.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(conn);
            if (handler instanceof PooledConnection) {
                return ((PooledConnection) handler).getRealConnection();
            }
        }
        return conn;
    }

    /**
     * Finalize.
     *
     * @throws Throwable the throwable
     */
    @Override
    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }

    /**
     * Unwrap t.
     *
     * @param <T>   the type parameter
     * @param iface the iface
     * @return the t
     * @throws SQLException the sql exception
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    /**
     * Is wrapper for boolean.
     *
     * @param iface the iface
     * @return the boolean
     * @throws SQLException the sql exception
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    /**
     * Gets parent logger.
     *
     * @return the parent logger
     */
    @Override
    public java.util.logging.Logger getParentLogger() {
        return java.util.logging.Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
    }

}
