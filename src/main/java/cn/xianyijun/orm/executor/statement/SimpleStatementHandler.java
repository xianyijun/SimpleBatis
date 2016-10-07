/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.statement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.executor.keygen.KeyGenerator;
import cn.xianyijun.orm.executor.keygen.SelectKeyGenerator;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.session.RowBounds;

/**
 * The type Simple statement handler.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SimpleStatementHandler extends BaseStatementHandler {

    /**
     * Instantiates a new Simple statement handler.
     *
     * @param executor        the executor
     * @param mappedStatement the mapped statement
     * @param parameter       the parameter
     * @param rowBounds       the row bounds
     * @param resultHandler   the result handler
     * @param boundSql        the bound sql
     */
    public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter,
                                  RowBounds rowBounds, ResultHandler<?> resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
    }

    /**
     * Update int.
     *
     * @param statement the statement
     * @return the int
     * @throws SQLException the sql exception
     */
    @Override
    public int update(Statement statement) throws SQLException {
        String sql = boundSql.getSql();
        Object parameterObject = boundSql.getParameterObject();
        KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();
        int rows;
        if (keyGenerator instanceof SelectKeyGenerator) {
            statement.execute(sql);
            rows = statement.getUpdateCount();
            keyGenerator.processAfter(executor, mappedStatement, statement, parameterObject);
        } else {
            statement.execute(sql);
            rows = statement.getUpdateCount();
        }
        return rows;
    }

    /**
     * Query list.
     *
     * @param <E>           the type parameter
     * @param statement     the statement
     * @param resultHandler the result handler
     * @return the list
     * @throws SQLException the sql exception
     */
    @Override
    public <E> List<E> query(Statement statement, ResultHandler<?> resultHandler) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        return resultSetHandler.<E>handleResultSets(statement);
    }

    /**
     * Query cursor cursor.
     *
     * @param <E>       the type parameter
     * @param statement the statement
     * @return the cursor
     * @throws SQLException the sql exception
     */
    @Override
    public <E> Cursor<E> queryCursor(Statement statement) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        return resultSetHandler.<E>handleCursorResultSets(statement);
    }

    /**
     * Instantiate statement statement.
     *
     * @param connection the connection
     * @return the statement
     * @throws SQLException the sql exception
     */
    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        if (mappedStatement.getResultSetType() != null) {
            return connection.createStatement(mappedStatement.getResultSetType().getValue(),
                    ResultSet.CONCUR_READ_ONLY);
        } else {
            return connection.createStatement();
        }
    }

    /**
     * Parameterize.
     *
     * @param statement the statement
     * @throws SQLException the sql exception
     */
    @Override
    public void parameterize(Statement statement) throws SQLException {
        // N/A
    }

}
