/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import cn.xianyijun.orm.cache.CacheKey;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.executor.ResultExtractor;
import cn.xianyijun.orm.mapping.Environment;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.reflection.ObjectFactory;
import cn.xianyijun.orm.session.RowBounds;
import cn.xianyijun.orm.transaction.Transaction;
import cn.xianyijun.orm.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * The type Result loader.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResultLoader {

  /**
   * The Configuration.
   */
  protected final StatementHandler.Configuration configuration;
  /**
   * The Executor.
   */
  protected final Executor executor;
  /**
   * The Mapped statement.
   */
  protected final MappedStatement mappedStatement;
  /**
   * The Parameter object.
   */
  protected final Object parameterObject;
  /**
   * The Target type.
   */
  protected final Class<?> targetType;
  /**
   * The Object factory.
   */
  protected final ObjectFactory objectFactory;
  /**
   * The Cache key.
   */
  protected final CacheKey cacheKey;
  /**
   * The Bound sql.
   */
  protected final StatementHandler.BoundSql boundSql;
  /**
   * The Result extractor.
   */
  protected final ResultExtractor resultExtractor;
  /**
   * The Creator thread id.
   */
  protected final long creatorThreadId;

  /**
   * The Loaded.
   */
  protected boolean loaded;
  /**
   * The Result object.
   */
  protected Object resultObject;

  /**
   * Instantiates a new Result loader.
   *
   * @param config          the config
   * @param executor        the executor
   * @param mappedStatement the mapped statement
   * @param parameterObject the parameter object
   * @param targetType      the target type
   * @param cacheKey        the cache key
   * @param boundSql        the bound sql
   */
  public ResultLoader(StatementHandler.Configuration config, Executor executor, MappedStatement mappedStatement, Object parameterObject, Class<?> targetType, CacheKey cacheKey, StatementHandler.BoundSql boundSql) {
    this.configuration = config;
    this.executor = executor;
    this.mappedStatement = mappedStatement;
    this.parameterObject = parameterObject;
    this.targetType = targetType;
    this.objectFactory = configuration.getObjectFactory();
    this.cacheKey = cacheKey;
    this.boundSql = boundSql;
    this.resultExtractor = new ResultExtractor(configuration, objectFactory);
    this.creatorThreadId = Thread.currentThread().getId();
  }

  /**
   * Load result object.
   *
   * @return the object
   * @throws SQLException the sql exception
   */
  public Object loadResult() throws SQLException {
    List<Object> list = selectList();
    resultObject = resultExtractor.extractObjectFromList(list, targetType);
    return resultObject;
  }

  private <E> List<E> selectList() throws SQLException {
    Executor localExecutor = executor;
    if (Thread.currentThread().getId() != this.creatorThreadId || localExecutor.isClosed()) {
      localExecutor = newExecutor();
    }
    try {
      return localExecutor.<E> query(mappedStatement, parameterObject, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER, cacheKey, boundSql);
    } finally {
      if (localExecutor != executor) {
        localExecutor.close(false);
      }
    }
  }

  private Executor newExecutor() {
    final Environment environment = configuration.getEnvironment();
    if (environment == null) {
      throw new ExecutorException("ResultLoader could not load lazily.  Environment was not configured.");
    }
    final DataSource ds = environment.getDataSource();
    if (ds == null) {
      throw new ExecutorException("ResultLoader could not load lazily.  DataSource was not configured.");
    }
    final TransactionFactory transactionFactory = environment.getTransactionFactory();
    final Transaction tx = transactionFactory.newTransaction(ds, null, false);
    return configuration.newExecutor(tx);
  }

  /**
   * Was null boolean.
   *
   * @return the boolean
   */
  public boolean wasNull() {
    return resultObject == null;
  }

}
