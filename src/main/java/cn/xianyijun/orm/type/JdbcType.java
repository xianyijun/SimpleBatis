/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */
package cn.xianyijun.orm.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * The enum Jdbc type.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public enum JdbcType {

    /**
     * Array jdbc type.
     */
    ARRAY(Types.ARRAY),
    /**
     * Bit jdbc type.
     */
    BIT(Types.BIT),
    /**
     * Tinyint jdbc type.
     */
    TINYINT(Types.TINYINT),
    /**
     * Smallint jdbc type.
     */
    SMALLINT(Types.SMALLINT),
    /**
     * Integer jdbc type.
     */
    INTEGER(Types.INTEGER),
    /**
     * Bigint jdbc type.
     */
    BIGINT(Types.BIGINT),
    /**
     * Float jdbc type.
     */
    FLOAT(Types.FLOAT),
    /**
     * Real jdbc type.
     */
    REAL(Types.REAL),
    /**
     * Double jdbc type.
     */
    DOUBLE(Types.DOUBLE),
    /**
     * Numeric jdbc type.
     */
    NUMERIC(Types.NUMERIC),
    /**
     * Decimal jdbc type.
     */
    DECIMAL(Types.DECIMAL),
    /**
     * Char jdbc type.
     */
    CHAR(Types.CHAR),
    /**
     * Varchar jdbc type.
     */
    VARCHAR(Types.VARCHAR),
    /**
     * Longvarchar jdbc type.
     */
    LONGVARCHAR(Types.LONGVARCHAR),
    /**
     * Date jdbc type.
     */
    DATE(Types.DATE),
    /**
     * Time jdbc type.
     */
    TIME(Types.TIME),
    /**
     * Timestamp jdbc type.
     */
    TIMESTAMP(Types.TIMESTAMP),
    /**
     * Binary jdbc type.
     */
    BINARY(Types.BINARY),
    /**
     * Varbinary jdbc type.
     */
    VARBINARY(Types.VARBINARY),
    /**
     * Longvarbinary jdbc type.
     */
    LONGVARBINARY(Types.LONGVARBINARY),
    /**
     * Null jdbc type.
     */
    NULL(Types.NULL),
    /**
     * Other jdbc type.
     */
    OTHER(Types.OTHER),
    /**
     * Blob jdbc type.
     */
    BLOB(Types.BLOB),
    /**
     * Clob jdbc type.
     */
    CLOB(Types.CLOB),
    /**
     * Boolean jdbc type.
     */
    BOOLEAN(Types.BOOLEAN),
    /**
     * Cursor jdbc type.
     */
    CURSOR(-10), // Oracle
    /**
     * Undefined jdbc type.
     */
    UNDEFINED(Integer.MIN_VALUE + 1000),
    /**
     * Struct jdbc type.
     */
    STRUCT(Types.STRUCT),
    /**
     * Java object jdbc type.
     */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    /**
     * Distinct jdbc type.
     */
    DISTINCT(Types.DISTINCT),
    /**
     * Ref jdbc type.
     */
    REF(Types.REF);

    /**
     * The Type code.
     */
    public final int TYPE_CODE;
    private static Map<Integer, JdbcType> codeLookup = new HashMap<>();

    static {
        for (JdbcType type : JdbcType.values()) {
            codeLookup.put(type.TYPE_CODE, type);
        }
    }

    JdbcType(int code) {
        this.TYPE_CODE = code;
    }

    /**
     * For code jdbc type.
     *
     * @param code the code
     * @return the jdbc type
     */
    public static JdbcType forCode(int code) {
        return codeLookup.get(code);
    }

}
