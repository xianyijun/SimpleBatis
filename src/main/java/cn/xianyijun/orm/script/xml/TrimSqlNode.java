/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import cn.xianyijun.orm.core.StatementHandler;

/**
 * The type Trim sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class TrimSqlNode implements SqlNode {

  private SqlNode contents;
  private String prefix;
  private String suffix;
  private List<String> prefixesToOverride;
  private List<String> suffixesToOverride;
  private StatementHandler.Configuration configuration;

  /**
   * Instantiates a new Trim sql node.
   *
   * @param configuration      the configuration
   * @param contents           the contents
   * @param prefix             the prefix
   * @param prefixesToOverride the prefixes to override
   * @param suffix             the suffix
   * @param suffixesToOverride the suffixes to override
   */
  public TrimSqlNode(StatementHandler.Configuration configuration, SqlNode contents, String prefix, String prefixesToOverride, String suffix, String suffixesToOverride) {
    this(configuration, contents, prefix, parseOverrides(prefixesToOverride), suffix, parseOverrides(suffixesToOverride));
  }

  /**
   * Instantiates a new Trim sql node.
   *
   * @param configuration      the configuration
   * @param contents           the contents
   * @param prefix             the prefix
   * @param prefixesToOverride the prefixes to override
   * @param suffix             the suffix
   * @param suffixesToOverride the suffixes to override
   */
  protected TrimSqlNode(StatementHandler.Configuration configuration, SqlNode contents, String prefix, List<String> prefixesToOverride, String suffix, List<String> suffixesToOverride) {
    this.contents = contents;
    this.prefix = prefix;
    this.prefixesToOverride = prefixesToOverride;
    this.suffix = suffix;
    this.suffixesToOverride = suffixesToOverride;
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
    FilteredDynamicContext filteredDynamicContext = new FilteredDynamicContext(context);
    boolean result = contents.apply(filteredDynamicContext);
    filteredDynamicContext.applyAll();
    return result;
  }

  private static List<String> parseOverrides(String overrides) {
    if (overrides != null) {
      final StringTokenizer parser = new StringTokenizer(overrides, "|", false);
      final List<String> list = new ArrayList<String>(parser.countTokens());
      while (parser.hasMoreTokens()) {
        list.add(parser.nextToken().toUpperCase(Locale.ENGLISH));
      }
      return list;
    }
    return Collections.emptyList();
  }

  private class FilteredDynamicContext extends DynamicContext {
    private DynamicContext delegate;
    private boolean prefixApplied;
    private boolean suffixApplied;
    private StringBuilder sqlBuffer;

    /**
     * Instantiates a new Filtered dynamic context.
     *
     * @param delegate the delegate
     */
    public FilteredDynamicContext(DynamicContext delegate) {
      super(configuration, null);
      this.delegate = delegate;
      this.prefixApplied = false;
      this.suffixApplied = false;
      this.sqlBuffer = new StringBuilder();
    }

    /**
     * Apply all.
     */
    public void applyAll() {
      sqlBuffer = new StringBuilder(sqlBuffer.toString().trim());
      String trimmedUppercaseSql = sqlBuffer.toString().toUpperCase(Locale.ENGLISH);
      if (trimmedUppercaseSql.length() > 0) {
        applyPrefix(sqlBuffer, trimmedUppercaseSql);
        applySuffix(sqlBuffer, trimmedUppercaseSql);
      }
      delegate.appendSql(sqlBuffer.toString());
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
     * Gets unique number.
     *
     * @return the unique number
     */
    @Override
    public int getUniqueNumber() {
      return delegate.getUniqueNumber();
    }

    /**
     * Append sql.
     *
     * @param sql the sql
     */
    @Override
    public void appendSql(String sql) {
      sqlBuffer.append(sql);
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

    private void applyPrefix(StringBuilder sql, String trimmedUppercaseSql) {
      if (!prefixApplied) {
        prefixApplied = true;
        if (prefixesToOverride != null) {
          for (String toRemove : prefixesToOverride) {
            if (trimmedUppercaseSql.startsWith(toRemove)) {
              sql.delete(0, toRemove.trim().length());
              break;
            }
          }
        }
        if (prefix != null) {
          sql.insert(0, " ");
          sql.insert(0, prefix);
        }
      }
    }

    private void applySuffix(StringBuilder sql, String trimmedUppercaseSql) {
      if (!suffixApplied) {
        suffixApplied = true;
        if (suffixesToOverride != null) {
          for (String toRemove : suffixesToOverride) {
            if (trimmedUppercaseSql.endsWith(toRemove) || trimmedUppercaseSql.endsWith(toRemove.trim())) {
              int start = sql.length() - toRemove.trim().length();
              int end = sql.length();
              sql.delete(start, end);
              break;
            }
          }
        }
        if (suffix != null) {
          sql.append(" ");
          sql.append(suffix);
        }
      }
    }

  }

}
