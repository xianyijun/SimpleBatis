/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cursor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.NoSuchElementException;

import cn.xianyijun.orm.exception.DataSourceException;
import cn.xianyijun.orm.executor.result.ResultContext;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.executor.resultset.DefaultResultSetHandler;
import cn.xianyijun.orm.executor.resultset.ResultSetWrapper;
import cn.xianyijun.orm.mapping.ResultMap;
import cn.xianyijun.orm.session.RowBounds;

/**
 * The type Default cursor.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultCursor<T> implements Cursor<T> {

    private final DefaultResultSetHandler resultSetHandler;
    private final ResultMap resultMap;
    private final ResultSetWrapper rsw;
    private final RowBounds rowBounds;
    private final ObjectWrapperResultHandler<T> objectWrapperResultHandler = new ObjectWrapperResultHandler<>();

    private final CursorIterator cursorIterator = new CursorIterator();
    private boolean iteratorRetrieved = false;

    private CursorStatus status = CursorStatus.CREATED;
    private int indexWithRowBound = -1;

    private enum CursorStatus {

        /**
         * Created cursor status.
         */
        CREATED, /**
         * Open cursor status.
         */
        OPEN, /**
         * Closed cursor status.
         */
        CLOSED, /**
         * Consumed cursor status.
         */
        CONSUMED
    }

    /**
     * Instantiates a new Default cursor.
     *
     * @param resultSetHandler the result set handler
     * @param resultMap        the result map
     * @param rsw              the rsw
     * @param rowBounds        the row bounds
     */
    public DefaultCursor(DefaultResultSetHandler resultSetHandler, ResultMap resultMap, ResultSetWrapper rsw,
                         RowBounds rowBounds) {
        this.resultSetHandler = resultSetHandler;
        this.resultMap = resultMap;
        this.rsw = rsw;
        this.rowBounds = rowBounds;
    }

    /**
     * Is open boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isOpen() {
        return status == CursorStatus.OPEN;
    }

    /**
     * Is consumed boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isConsumed() {
        return status == CursorStatus.CONSUMED;
    }

    /**
     * Gets current index.
     *
     * @return the current index
     */
    @Override
    public int getCurrentIndex() {
        return rowBounds.getOffset() + cursorIterator.iteratorIndex;
    }

    /**
     * Iterator iterator.
     *
     * @return the iterator
     */
    @Override
    public Iterator<T> iterator() {
        if (iteratorRetrieved) {
            throw new IllegalStateException("Cannot open more than one iterator on a Cursor");
        }
        iteratorRetrieved = true;
        return cursorIterator;
    }

    /**
     * Close.
     */
    @Override
    public void close() {
        if (isClosed()) {
            return;
        }

        ResultSet rs = rsw.getResultSet();
        try {
            if (rs != null) {
                Statement statement = rs.getStatement();

                rs.close();
                if (statement != null) {
                    statement.close();
                }
            }
            status = CursorStatus.CLOSED;
        } catch (SQLException e) {
            // ignore
        }
    }

    /**
     * Fetch next using row bound t.
     *
     * @return the t
     */
    protected T fetchNextUsingRowBound() {
        T result = fetchNextObjectFromDatabase();
        while (result != null && indexWithRowBound < rowBounds.getOffset()) {
            result = fetchNextObjectFromDatabase();
        }
        return result;
    }

    /**
     * Fetch next object from database t.
     *
     * @return the t
     */
    protected T fetchNextObjectFromDatabase() {
        if (isClosed()) {
            return null;
        }

        try {
            status = CursorStatus.OPEN;
            resultSetHandler.handleRowValues(rsw, resultMap, objectWrapperResultHandler, RowBounds.DEFAULT, null);
        } catch (SQLException e) {
            throw new DataSourceException(e);
        }

        T next = objectWrapperResultHandler.result;
        if (next != null) {
            indexWithRowBound++;
        }
        // No more object or limit reached
        if (next == null || (getReadItemsCount() == rowBounds.getOffset() + rowBounds.getLimit())) {
            close();
            status = CursorStatus.CONSUMED;
        }
        objectWrapperResultHandler.result = null;

        return next;
    }

    private boolean isClosed() {
        return status == CursorStatus.CLOSED || status == CursorStatus.CONSUMED;
    }

    private int getReadItemsCount() {
        return indexWithRowBound + 1;
    }

    private static class ObjectWrapperResultHandler<T> implements ResultHandler<T> {

        private T result;

        /**
         * Handle result.
         *
         * @param context the context
         */
        @Override
        public void handleResult(ResultContext<? extends T> context) {
            this.result = context.getResultObject();
            context.stop();
        }
    }

    private class CursorIterator implements Iterator<T> {

        /**
         * Holder for the next object to be returned
         */
        T object;

        /**
         * Index of objects returned using next(), and as such, visible to users.
         */
        int iteratorIndex = -1;

        /**
         * Has next boolean.
         *
         * @return the boolean
         */
        @Override
        public boolean hasNext() {
            if (object == null) {
                object = fetchNextUsingRowBound();
            }
            return object != null;
        }

        /**
         * Next t.
         *
         * @return the t
         */
        @Override
        public T next() {
            // Fill next with object fetched from hasNext()
            T next = object;

            if (next == null) {
                next = fetchNextUsingRowBound();
            }

            if (next != null) {
                object = null;
                iteratorIndex++;
                return next;
            }
            throw new NoSuchElementException();
        }

        /**
         * Remove.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove element from Cursor");
        }
    }
}
