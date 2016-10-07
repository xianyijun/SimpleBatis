/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.transaction;

import java.sql.Connection;

/**
 * The enum Transaction isolation level.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public enum TransactionIsolationLevel {
    /**
     * None transaction isolation level.
     */
    NONE(Connection.TRANSACTION_NONE), /**
     * Read committed transaction isolation level.
     */
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED), /**
     * Read uncommitted transaction isolation level.
     */
    READ_UNCOMMITTED(
                    Connection.TRANSACTION_READ_UNCOMMITTED), /**
     * Repeatable read transaction isolation level.
     */
    REPEATABLE_READ(
                    Connection.TRANSACTION_REPEATABLE_READ), /**
     * Serializable transaction isolation level.
     */
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

    private final int level;

    private TransactionIsolationLevel(int level) {
        this.level = level;
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    public int getLevel() {
        return level;
    }
}
