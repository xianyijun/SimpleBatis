/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import cn.xianyijun.orm.exception.DataSourceException;

/**
 * The type Pooled connection.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
class PooledConnection implements InvocationHandler {

    private static final String CLOSE = "close";
    private static final Class<?>[] IFACES = new Class<?>[]{Connection.class};

    private int hashCode = 0;
    private PooledDataSource dataSource;
    private Connection realConnection;
    private Connection proxyConnection;
    private long checkoutTimestamp;
    private long createdTimestamp;
    private long lastUsedTimestamp;
    private int connectionTypeCode;
    private boolean valid;

    /**
     * Instantiates a new Pooled connection.
     *
     * @param connection the connection
     * @param dataSource the data source
     */
    public PooledConnection(Connection connection, PooledDataSource dataSource) {
        this.hashCode = connection.hashCode();
        this.realConnection = connection;
        this.dataSource = dataSource;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
    }

    /**
     * Invalidate.
     */
    public void invalidate() {
        valid = false;
    }

    /**
     * Is valid boolean.
     *
     * @return the boolean
     */
    public boolean isValid() {
        return valid && realConnection != null && dataSource.pingConnection(this);
    }

    /**
     * Gets real connection.
     *
     * @return the real connection
     */
    public Connection getRealConnection() {
        return realConnection;
    }

    /**
     * Gets proxy connection.
     *
     * @return the proxy connection
     */
    public Connection getProxyConnection() {
        return proxyConnection;
    }

    /**
     * Gets real hash code.
     *
     * @return the real hash code
     */
/*
     * Gets the hashcode of the real connection (or 0 if it is null)
	 *
	 * @return The hashcode of the real connection (or 0 if it is null)
	 */
    public int getRealHashCode() {
        return realConnection == null ? 0 : realConnection.hashCode();
    }

    /**
     * Gets connection type code.
     *
     * @return the connection type code
     */
/*
	 * Getter for the connection type (based on url + user + password)
	 *
	 * @return The connection type
	 */
    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    /**
     * Sets connection type code.
     *
     * @param connectionTypeCode the connection type code
     */
/*
	 * Setter for the connection type
	 *
	 * @param connectionTypeCode - the connection type
	 */
    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    /**
     * Gets created timestamp.
     *
     * @return the created timestamp
     */
/*
	 * Getter for the time that the connection was created
	 *
	 * @return The creation timestamp
	 */
    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets created timestamp.
     *
     * @param createdTimestamp the created timestamp
     */
/*
	 * Setter for the time that the connection was created
	 *
	 * @param createdTimestamp - the timestamp
	 */
    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    /**
     * Gets last used timestamp.
     *
     * @return the last used timestamp
     */
/*
	 * Getter for the time that the connection was last used
	 *
	 * @return - the timestamp
	 */
    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    /**
     * Sets last used timestamp.
     *
     * @param lastUsedTimestamp the last used timestamp
     */
/*
	 * Setter for the time that the connection was last used
	 *
	 * @param lastUsedTimestamp - the timestamp
	 */
    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    /**
     * Gets time elapsed since last use.
     *
     * @return the time elapsed since last use
     */
/*
	 * Getter for the time since this connection was last used
	 *
	 * @return - the time since the last use
	 */
    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    /**
     * Gets age.
     *
     * @return the age
     */
/*
	 * Getter for the age of the connection
	 *
	 * @return the age
	 */
    public long getAge() {
        return System.currentTimeMillis() - createdTimestamp;
    }

    /**
     * Gets checkout timestamp.
     *
     * @return the checkout timestamp
     */
/*
	 * Getter for the timestamp that this connection was checked out
	 *
	 * @return the timestamp
	 */
    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    /**
     * Sets checkout timestamp.
     *
     * @param timestamp the timestamp
     */
/*
	 * Setter for the timestamp that this connection was checked out
	 *
	 * @param timestamp the timestamp
	 */
    public void setCheckoutTimestamp(long timestamp) {
        this.checkoutTimestamp = timestamp;
    }

    /**
     * Gets checkout time.
     *
     * @return the checkout time
     */
/*
	 * Getter for the time that this connection has been checked out
	 *
	 * @return the time
	 */
    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Equals boolean.
     *
     * @param obj the obj
     * @return the boolean
     */
/*
	 * Allows comparing this connection to another
	 *
	 * @param obj - the other connection to test for equality
	 * @see Object#equals(Object)
	 */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PooledConnection) {
            return realConnection.hashCode() == (((PooledConnection) obj).realConnection.hashCode());
        } else if (obj instanceof Connection) {
            return hashCode == obj.hashCode();
        } else {
            return false;
        }
    }

    /**
     * Invoke object.
     *
     * @param proxy  the proxy
     * @param method the method
     * @param args   the args
     * @return the object
     * @throws Throwable the throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
            dataSource.pushConnection(this);
            return null;
        } else {
            try {
                if (!Object.class.equals(method.getDeclaringClass())) {
                    checkConnection();
                }
                return method.invoke(realConnection, args);
            } catch (Exception e) {
                throw new DataSourceException(e);
            }
        }
    }

    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
        }
    }

}
