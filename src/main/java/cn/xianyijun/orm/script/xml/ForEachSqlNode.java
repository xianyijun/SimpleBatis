/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.Map;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.parse.GenericTokenParser;
import cn.xianyijun.orm.parse.TokenHandler;

/**
 * The type For each sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ForEachSqlNode implements SqlNode {
  /**
   * The constant ITEM_PREFIX.
   */
  public static final String ITEM_PREFIX = "__frch_";

  private ExpressionEvaluator evaluator;
  private String collectionExpression;
  private SqlNode contents;
  private String open;
  private String close;
  private String separator;
  private String item;
  private String index;
  private StatementHandler.Configuration configuration;

  /**
   * Instantiates a new For each sql node.
   *
   * @param configuration        the configuration
   * @param contents             the contents
   * @param collectionExpression the collection expression
   * @param index                the index
   * @param item                 the item
   * @param open                 the open
   * @param close                the close
   * @param separator            the separator
   */
  public ForEachSqlNode(StatementHandler.Configuration configuration, SqlNode contents, String collectionExpression, String index, String item, String open, String close, String separator) {
    this.evaluator = new ExpressionEvaluator();
    this.collectionExpression = collectionExpression;
    this.contents = contents;
    this.open = open;
    this.close = close;
    this.separator = separator;
    this.index = index;
    this.item = item;
    this.configuration = configuration;
  }

  /**
   * Apply boolean.
   *
   * @param context the context
   * @return the boolean
   */
  @Override
  public boolean apply(DynamicContext context) {
    Map<String, Object> bindings = context.getBindings();
    final Iterable<?> iterable = evaluator.evaluateIterable(collectionExpression, bindings);
    if (!iterable.iterator().hasNext()) {
      return true;
    }
    boolean first = true;
    applyOpen(context);
    int i = 0;
    for (Object o : iterable) {
      DynamicContext oldContext = context;
      if (first) {
        context = new PrefixedContext(context, "");
      } else if (separator != null) {
        context = new PrefixedContext(context, separator);
      } else {
          context = new PrefixedContext(context, "");
      }
      int uniqueNumber = context.getUniqueNumber();
      // Issue #709 
      if (o instanceof Map.Entry) {
        @SuppressWarnings("unchecked") 
        Map.Entry<Object, Object> mapEntry = (Map.Entry<Object, Object>) o;
        applyIndex(context, mapEntry.getKey(), uniqueNumber);
        applyItem(context, mapEntry.getValue(), uniqueNumber);
      } else {
        applyIndex(context, i, uniqueNumber);
        applyItem(context, o, uniqueNumber);
      }
      contents.apply(new FilteredDynamicContext(configuration, context, index, item, uniqueNumber));
      if (first) {
        first = !((PrefixedContext) context).isPrefixApplied();
      }
      context = oldContext;
      i++;
    }
    applyClose(context);
    return true;
  }

  private void applyIndex(DynamicContext context, Object o, int i) {
    if (index != null) {
      context.bind(index, o);
      context.bind(itemizeItem(index, i), o);
    }
  }

  private void applyItem(DynamicContext context, Object o, int i) {
    if (item != null) {
      context.bind(item, o);
      context.bind(itemizeItem(item, i), o);
    }
  }

  private void applyOpen(DynamicContext context) {
    if (open != null) {
      context.appendSql(open);
    }
  }

  private void applyClose(DynamicContext context) {
    if (close != null) {
      context.appendSql(close);
    }
  }

  private static String itemizeItem(String item, int i) {
    return new StringBuilder(ITEM_PREFIX).append(item).append("_").append(i).toString();
  }

  private static class FilteredDynamicContext extends DynamicContext {
    private DynamicContext delegate;
    private int index;
    private String itemIndex;
    private String item;

    /**
     * Instantiates a new Filtered dynamic context.
     *
     * @param configuration the configuration
     * @param delegate      the delegate
     * @param itemIndex     the item index
     * @param item          the item
     * @param i             the
     */
    public FilteredDynamicContext(StatementHandler.Configuration configuration, DynamicContext delegate, String itemIndex, String item, int i) {
      super(configuration, null);
      this.delegate = delegate;
      this.index = i;
      this.itemIndex = itemIndex;
      this.item = item;
    }

    /**
     * Gets bindings.
     *
     * @return the bindings
     */
    @Override
    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }

    /**
     * Bind.
     *
     * @param name  the name
     * @param value the value
     */
    @Override
    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }

    /**
     * Gets sql.
     *
     * @return the sql
     */
    @Override
    public String getSql() {
      return delegate.getSql();
    }

    /**
     * Append sql.
     *
     * @param sql the sql
     */
    @Override
    public void appendSql(String sql) {
      GenericTokenParser parser = new GenericTokenParser("#{", "}", new TokenHandler() {
        @Override
        public String handleToken(String content) {
          String newContent = content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", itemizeItem(item, index));
          if (itemIndex != null && newContent.equals(content)) {
            newContent = content.replaceFirst("^\\s*" + itemIndex + "(?![^.,:\\s])", itemizeItem(itemIndex, index));
          }
          return new StringBuilder("#{").append(newContent).append("}").toString();
        }
      });

      delegate.appendSql(parser.parse(sql));
    }

    /**
     * Gets unique number.
     *
     * @return the unique number
     */
    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }

  }


  private class PrefixedContext extends DynamicContext {
    private DynamicContext delegate;
    private String prefix;
    private boolean prefixApplied;

    /**
     * Instantiates a new Prefixed context.
     *
     * @param delegate the delegate
     * @param prefix   the prefix
     */
    public PrefixedContext(DynamicContext delegate, String prefix) {
      super(configuration, null);
      this.delegate = delegate;
      this.prefix = prefix;
      this.prefixApplied = false;
    }

    /**
     * Is prefix applied boolean.
     *
     * @return the boolean
     */
    public boolean isPrefixApplied() {
      return prefixApplied;
    }

    /**
     * Gets bindings.
     *
     * @return the bindings
     */
    @Override
    public Map<String, Object> getBindings() {
      return delegate.getBindings();
    }

    /**
     * Bind.
     *
     * @param name  the name
     * @param value the value
     */
    @Override
    public void bind(String name, Object value) {
      delegate.bind(name, value);
    }

    /**
     * Append sql.
     *
     * @param sql the sql
     */
    @Override
    public void appendSql(String sql) {
      if (!prefixApplied && sql != null && sql.trim().length() > 0) {
        delegate.appendSql(prefix);
        prefixApplied = true;
      }
      delegate.appendSql(sql);
    }

    /**
     * Gets sql.
     *
     * @return the sql
     */
    @Override
    public String getSql() {
      return delegate.getSql();
    }

    /**
     * Gets unique number.
     *
     * @return the unique number
     */
    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }
  }

}
