/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.type.TypeHandlerRegistry;

/**
 * The type Result mapping.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResultMapping {

    private StatementHandler.Configuration configuration;
    private Class<?> javaType;
    private String property;
    private String column;
    private ResultSetHandler.TypeHandler<?> typeHandler;
    private String nestedResultMapId;
    private String nestedQueryId;
    private Set<String> notNullColumns;
    private String columnPrefix;
    private List<ResultFlag> flags;
    private List<ResultMapping> composites;
    private String resultSet;
    private String foreignColumn;
    private boolean lazy;

    /**
     * Instantiates a new Result mapping.
     */
    ResultMapping() {
    }

    /**
     * The type Builder.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    public static class Builder {
        private ResultMapping resultMapping = new ResultMapping();

        /**
         * Instantiates a new Builder.
         *
         * @param configuration the configuration
         * @param property      the property
         * @param column        the column
         * @param typeHandler   the type handler
         */
        public Builder(StatementHandler.Configuration configuration, String property, String column, ResultSetHandler.TypeHandler<?> typeHandler) {
            this(configuration, property);
            resultMapping.column = column;
            resultMapping.typeHandler = typeHandler;
        }

        /**
         * Instantiates a new Builder.
         *
         * @param configuration the configuration
         * @param property      the property
         * @param column        the column
         * @param javaType      the java type
         */
        public Builder(StatementHandler.Configuration configuration, String property, String column, Class<?> javaType) {
            this(configuration, property);
            resultMapping.column = column;
            resultMapping.javaType = javaType;
        }

        /**
         * Instantiates a new Builder.
         *
         * @param configuration the configuration
         * @param property      the property
         */
        public Builder(StatementHandler.Configuration configuration, String property) {
            resultMapping.configuration = configuration;
            resultMapping.property = property;
            resultMapping.flags = new ArrayList<ResultFlag>();
            resultMapping.composites = new ArrayList<ResultMapping>();
            resultMapping.lazy = configuration.isLazyLoadingEnabled();
        }

        /**
         * Nested result map id builder.
         *
         * @param nestedResultMapId the nested result map id
         * @return the builder
         */
        public Builder nestedResultMapId(String nestedResultMapId) {
            resultMapping.nestedResultMapId = nestedResultMapId;
            return this;
        }

        /**
         * Nested query id builder.
         *
         * @param nestedQueryId the nested query id
         * @return the builder
         */
        public Builder nestedQueryId(String nestedQueryId) {
            resultMapping.nestedQueryId = nestedQueryId;
            return this;
        }

        /**
         * Result set builder.
         *
         * @param resultSet the result set
         * @return the builder
         */
        public Builder resultSet(String resultSet) {
            resultMapping.resultSet = resultSet;
            return this;
        }

        /**
         * Foreign column builder.
         *
         * @param foreignColumn the foreign column
         * @return the builder
         */
        public Builder foreignColumn(String foreignColumn) {
            resultMapping.foreignColumn = foreignColumn;
            return this;
        }

        /**
         * Not null columns builder.
         *
         * @param notNullColumns the not null columns
         * @return the builder
         */
        public Builder notNullColumns(Set<String> notNullColumns) {
            resultMapping.notNullColumns = notNullColumns;
            return this;
        }

        /**
         * Java type builder.
         *
         * @param javaType the java type
         * @return the builder
         */
        public Builder javaType(Class<?> javaType) {
            resultMapping.javaType = javaType;
            return this;
        }

        /**
         * Column prefix builder.
         *
         * @param columnPrefix the column prefix
         * @return the builder
         */
        public Builder columnPrefix(String columnPrefix) {
            resultMapping.columnPrefix = columnPrefix;
            return this;
        }

        /**
         * Flags builder.
         *
         * @param flags the flags
         * @return the builder
         */
        public Builder flags(List<ResultFlag> flags) {
            resultMapping.flags = flags;
            return this;
        }

        /**
         * Composites builder.
         *
         * @param composites the composites
         * @return the builder
         */
        public Builder composites(List<ResultMapping> composites) {
            resultMapping.composites = composites;
            return this;
        }

        /**
         * Lazy builder.
         *
         * @param lazy the lazy
         * @return the builder
         */
        public Builder lazy(boolean lazy) {
            resultMapping.lazy = lazy;
            return this;
        }

        /**
         * Build result mapping.
         *
         * @return the result mapping
         */
        public ResultMapping build() {
            // lock down collections
            resultMapping.flags = Collections.unmodifiableList(resultMapping.flags);
            resultMapping.composites = Collections.unmodifiableList(resultMapping.composites);
            resolveTypeHandler();
            validate();
            return resultMapping;
        }

        private void validate() {
            if (resultMapping.nestedQueryId != null && resultMapping.nestedResultMapId != null) {
                throw new IllegalStateException(
                        "Cannot define both nestedQueryId and nestedResultMapId in property " + resultMapping.property);
            }
            if (resultMapping.nestedQueryId == null && resultMapping.nestedResultMapId == null && resultMapping.typeHandler == null) {
                throw new IllegalStateException("No typehandler found for property " + resultMapping.property);
            }
            if (resultMapping.nestedResultMapId == null && resultMapping.column == null
                    && resultMapping.composites.isEmpty()) {
                throw new IllegalStateException(
                        "Mapping is missing column attribute for property " + resultMapping.property);
            }
            if (resultMapping.getResultSet() != null) {
                int numColumns = 0;
                if (resultMapping.column != null) {
                    numColumns = resultMapping.column.split(",").length;
                }
                int numForeignColumns = 0;
                if (resultMapping.foreignColumn != null) {
                    numForeignColumns = resultMapping.foreignColumn.split(",").length;
                }
                if (numColumns != numForeignColumns) {
                    throw new IllegalStateException(
                            "There should be the same number of columns and foreignColumns in property "
                                    + resultMapping.property);
                }
            }
        }

        private void resolveTypeHandler() {
            if (resultMapping.typeHandler == null && resultMapping.javaType != null) {
                StatementHandler.Configuration configuration = resultMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                resultMapping.typeHandler = typeHandlerRegistry.getTypeHandler(resultMapping.javaType);
            }
        }

        /**
         * Column builder.
         *
         * @param column the column
         * @return the builder
         */
        public Builder column(String column) {
            resultMapping.column = column;
            return this;
        }
    }

    /**
     * Gets java type.
     *
     * @return the java type
     */
    public Class<?> getJavaType() {
        return javaType;
    }

    /**
     * Gets property.
     *
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * Gets column.
     *
     * @return the column
     */
    public String getColumn() {
        return column;
    }

    /**
     * Gets nested result map id.
     *
     * @return the nested result map id
     */
    public String getNestedResultMapId() {
        return nestedResultMapId;
    }

    /**
     * Gets nested query id.
     *
     * @return the nested query id
     */
    public String getNestedQueryId() {
        return nestedQueryId;
    }

    /**
     * Gets not null columns.
     *
     * @return the not null columns
     */
    public Set<String> getNotNullColumns() {
        return notNullColumns;
    }

    /**
     * Gets column prefix.
     *
     * @return the column prefix
     */
    public String getColumnPrefix() {
        return columnPrefix;
    }

    /**
     * Gets flags.
     *
     * @return the flags
     */
    public List<ResultFlag> getFlags() {
        return flags;
    }

    /**
     * Gets composites.
     *
     * @return the composites
     */
    public List<ResultMapping> getComposites() {
        return composites;
    }

    /**
     * Is composite result boolean.
     *
     * @return the boolean
     */
    public boolean isCompositeResult() {
        return this.composites != null && !this.composites.isEmpty();
    }

    /**
     * Gets result set.
     *
     * @return the result set
     */
    public String getResultSet() {
        return this.resultSet;
    }

    /**
     * Gets foreign column.
     *
     * @return the foreign column
     */
    public String getForeignColumn() {
        return foreignColumn;
    }

    /**
     * Sets foreign column.
     *
     * @param foreignColumn the foreign column
     */
    public void setForeignColumn(String foreignColumn) {
        this.foreignColumn = foreignColumn;
    }

    /**
     * Is lazy boolean.
     *
     * @return the boolean
     */
    public boolean isLazy() {
        return lazy;
    }

    /**
     * Sets lazy.
     *
     * @param lazy the lazy
     */
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }

    /**
     * Gets type handler.
     *
     * @return the type handler
     */
    public ResultSetHandler.TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResultMapping that = (ResultMapping) o;

        if (property == null || !property.equals(that.property)) {
            return false;
        }

        return true;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        if (property != null) {
            return property.hashCode();
        } else if (column != null) {
            return column.hashCode();
        } else {
            return 0;
        }
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResultMapping{");
        //sb.append("configuration=").append(configuration); // configuration doesn't have a useful .toString()
        sb.append("property='").append(property).append('\'');
        sb.append(", column='").append(column).append('\'');
        //sb.append(", typeHandler=").append(typeHandler); // typeHandler also doesn't have a useful .toString()
        sb.append(", nestedResultMapId='").append(nestedResultMapId).append('\'');
        sb.append(", nestedQueryId='").append(nestedQueryId).append('\'');
        sb.append(", notNullColumns=").append(notNullColumns);
        sb.append(", columnPrefix='").append(columnPrefix).append('\'');
        sb.append(", flags=").append(flags);
        sb.append(", composites=").append(composites);
        sb.append(", resultSet='").append(resultSet).append('\'');
        sb.append(", foreignColumn='").append(foreignColumn).append('\'');
        sb.append(", lazy=").append(lazy);
        sb.append('}');
        return sb.toString();
    }
}
