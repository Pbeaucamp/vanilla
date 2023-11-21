/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package bpm.vanilla.platform.core.beans.data;

/**
 * <P>Defines the constants that are used to identify generic
 * SQL types, called JDBC types.
 * <p>
 * @see SQLType
 * @since 1.8
 */
public enum JDBCType implements SQLType {

    /**
     * Identifies the generic SQL type {@code BIT}.
     */
    BIT(Types.BIT),
    /**
     * Identifies the generic SQL type {@code TINYINT}.
     */
    TINYINT(Types.TINYINT),
    /**
     * Identifies the generic SQL type {@code SMALLINT}.
     */
    SMALLINT(Types.SMALLINT),
    /**
     * Identifies the generic SQL type {@code INTEGER}.
     */
    INTEGER(Types.INTEGER),
    /**
     * Identifies the generic SQL type {@code BIGINT}.
     */
    BIGINT(Types.BIGINT),
    /**
     * Identifies the generic SQL type {@code FLOAT}.
     */
    FLOAT(Types.FLOAT),
    /**
     * Identifies the generic SQL type {@code REAL}.
     */
    REAL(Types.REAL),
    /**
     * Identifies the generic SQL type {@code DOUBLE}.
     */
    DOUBLE(Types.DOUBLE),
    /**
     * Identifies the generic SQL type {@code NUMERIC}.
     */
    NUMERIC(Types.NUMERIC),
    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL(Types.DECIMAL),
    /**
     * Identifies the generic SQL type {@code CHAR}.
     */
    CHAR(Types.CHAR),
    /**
     * Identifies the generic SQL type {@code VARCHAR}.
     */
    VARCHAR(Types.VARCHAR),
    /**
     * Identifies the generic SQL type {@code LONGVARCHAR}.
     */
    LONGVARCHAR(Types.LONGVARCHAR),
    /**
     * Identifies the generic SQL type {@code DATE}.
     */
    DATE(Types.DATE),
    /**
     * Identifies the generic SQL type {@code TIME}.
     */
    TIME(Types.TIME),
    /**
     * Identifies the generic SQL type {@code TIMESTAMP}.
     */
    TIMESTAMP(Types.TIMESTAMP),
    /**
     * Identifies the generic SQL type {@code BINARY}.
     */
    BINARY(Types.BINARY),
    /**
     * Identifies the generic SQL type {@code VARBINARY}.
     */
    VARBINARY(Types.VARBINARY),
    /**
     * Identifies the generic SQL type {@code LONGVARBINARY}.
     */
    LONGVARBINARY(Types.LONGVARBINARY),
    /**
     * Identifies the generic SQL value {@code NULL}.
     */
    NULL(Types.NULL),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    OTHER(Types.OTHER),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    /**
     * Identifies the generic SQL type {@code DISTINCT}.
     */
    DISTINCT(Types.DISTINCT),
    /**
     * Identifies the generic SQL type {@code STRUCT}.
     */
    STRUCT(Types.STRUCT),
    /**
     * Identifies the generic SQL type {@code ARRAY}.
     */
    ARRAY(Types.ARRAY),
    /**
     * Identifies the generic SQL type {@code BLOB}.
     */
    BLOB(Types.BLOB),
    /**
     * Identifies the generic SQL type {@code CLOB}.
     */
    CLOB(Types.CLOB),
    /**
     * Identifies the generic SQL type {@code REF}.
     */
    REF(Types.REF),
    /**
     * Identifies the generic SQL type {@code DATALINK}.
     */
    DATALINK(Types.DATALINK),
    /**
     * Identifies the generic SQL type {@code BOOLEAN}.
     */
    BOOLEAN(Types.BOOLEAN),

    /* JDBC 4.0 Types */

    /**
     * Identifies the SQL type {@code ROWID}.
     */
    ROWID(Types.ROWID),
    /**
     * Identifies the generic SQL type {@code NCHAR}.
     */
    NCHAR(Types.NCHAR),
    /**
     * Identifies the generic SQL type {@code NVARCHAR}.
     */
    NVARCHAR(Types.NVARCHAR),
    /**
     * Identifies the generic SQL type {@code LONGNVARCHAR}.
     */
    LONGNVARCHAR(Types.LONGNVARCHAR),
    /**
     * Identifies the generic SQL type {@code NCLOB}.
     */
    NCLOB(Types.NCLOB),
    /**
     * Identifies the generic SQL type {@code SQLXML}.
     */
    SQLXML(Types.SQLXML);

    /* JDBC 4.2 Types */

//    /**
//     * Identifies the generic SQL type {@code REF_CURSOR}.
//     */
//    REF_CURSOR(Types.REF_CURSOR),
//
//    /**
//     * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
//     */
//    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
//
//    /**
//     * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
//     */
//    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE);

    /**
     * The Integer value for the JDBCType.  It maps to a value in
     * {@code Types.java}
     */
    private Integer type;

    /**
     * Constructor to specify the data type value from {@code Types) for
     * this data type.
     * @param type The value from {@code Types) for this data type
     */
    JDBCType(final Integer type) {
        this.type = type;
    }

    /**
     *{@inheritDoc }
     * @return The name of this {@code SQLType}.
     */
    public String getName() {
        return name();
    }
    /**
     * Returns the name of the vendor that supports this data type.
     * @return  The name of the vendor for this data type which is
     * {@literal java.sql} for JDBCType.
     */
    public String getVendor() {
        return "java.sql";
    }

    /**
     * Returns the vendor specific type number for the data type.
     * @return  An Integer representing the data type. For {@code JDBCType},
     * the value will be the same value as in {@code Types} for the data type.
     */
    public Integer getVendorTypeNumber() {
        return type;
    }
    /**
     * Returns the {@code JDBCType} that corresponds to the specified
     * {@code Types} value
     * @param type {@code Types} value
     * @return The {@code JDBCType} constant
     * @throws IllegalArgumentException if this enum type has no constant with
     * the specified {@code Types} value
     * @see Types
     */
    public static JDBCType valueOf(int type) {
        for( JDBCType sqlType : JDBCType.class.getEnumConstants()) {
            if(type == sqlType.type)
                return sqlType;
        }
        throw new IllegalArgumentException("Type:" + type + " is not a valid "
                + "Types.java value.");
    }
    
    /**
     * <P>The class that defines the constants that are used to identify generic
     * SQL types, called JDBC types.
     * <p>
     * This class is never instantiated.
     */
    public class Types {

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>BIT</code>.
     */
            public final static int BIT             =  -7;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>TINYINT</code>.
     */
            public final static int TINYINT         =  -6;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>SMALLINT</code>.
     */
            public final static int SMALLINT        =   5;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>INTEGER</code>.
     */
            public final static int INTEGER         =   4;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>BIGINT</code>.
     */
            public final static int BIGINT          =  -5;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>FLOAT</code>.
     */
            public final static int FLOAT           =   6;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>REAL</code>.
     */
            public final static int REAL            =   7;


    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>DOUBLE</code>.
     */
            public final static int DOUBLE          =   8;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>NUMERIC</code>.
     */
            public final static int NUMERIC         =   2;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>DECIMAL</code>.
     */
            public final static int DECIMAL         =   3;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>CHAR</code>.
     */
            public final static int CHAR            =   1;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>VARCHAR</code>.
     */
            public final static int VARCHAR         =  12;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>LONGVARCHAR</code>.
     */
            public final static int LONGVARCHAR     =  -1;


    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>DATE</code>.
     */
            public final static int DATE            =  91;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>TIME</code>.
     */
            public final static int TIME            =  92;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>TIMESTAMP</code>.
     */
            public final static int TIMESTAMP       =  93;


    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>BINARY</code>.
     */
            public final static int BINARY          =  -2;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>VARBINARY</code>.
     */
            public final static int VARBINARY       =  -3;

    /**
     * <P>The constant in the Java programming language, sometimes referred
     * to as a type code, that identifies the generic SQL type
     * <code>LONGVARBINARY</code>.
     */
            public final static int LONGVARBINARY   =  -4;

    /**
     * <P>The constant in the Java programming language
     * that identifies the generic SQL value
     * <code>NULL</code>.
     */
            public final static int NULL            =   0;

        /**
         * The constant in the Java programming language that indicates
         * that the SQL type is database-specific and
         * gets mapped to a Java object that can be accessed via
         * the methods <code>getObject</code> and <code>setObject</code>.
         */
            public final static int OTHER           = 1111;



        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * <code>JAVA_OBJECT</code>.
         * @since 1.2
         */
            public final static int JAVA_OBJECT         = 2000;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * <code>DISTINCT</code>.
         * @since 1.2
         */
            public final static int DISTINCT            = 2001;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * <code>STRUCT</code>.
         * @since 1.2
         */
            public final static int STRUCT              = 2002;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * <code>ARRAY</code>.
         * @since 1.2
         */
            public final static int ARRAY               = 2003;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * <code>BLOB</code>.
         * @since 1.2
         */
            public final static int BLOB                = 2004;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * <code>CLOB</code>.
         * @since 1.2
         */
            public final static int CLOB                = 2005;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * <code>REF</code>.
         * @since 1.2
         */
            public final static int REF                 = 2006;

        /**
         * The constant in the Java programming language, somtimes referred to
         * as a type code, that identifies the generic SQL type <code>DATALINK</code>.
         *
         * @since 1.4
         */
        public final static int DATALINK = 70;

        /**
         * The constant in the Java programming language, somtimes referred to
         * as a type code, that identifies the generic SQL type <code>BOOLEAN</code>.
         *
         * @since 1.4
         */
        public final static int BOOLEAN = 16;

        //------------------------- JDBC 4.0 -----------------------------------

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type <code>ROWID</code>
         *
         * @since 1.6
         *
         */
        public final static int ROWID = -8;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type <code>NCHAR</code>
         *
         * @since 1.6
         */
        public static final int NCHAR = -15;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type <code>NVARCHAR</code>.
         *
         * @since 1.6
         */
        public static final int NVARCHAR = -9;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type <code>LONGNVARCHAR</code>.
         *
         * @since 1.6
         */
        public static final int LONGNVARCHAR = -16;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type <code>NCLOB</code>.
         *
         * @since 1.6
         */
        public static final int NCLOB = 2011;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type <code>XML</code>.
         *
         * @since 1.6
         */
        public static final int SQLXML = 2009;

        //--------------------------JDBC 4.2 -----------------------------

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type {@code REF CURSOR}.
         *
         * @since 1.8
         */
        public static final int REF_CURSOR = 2012;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * {@code TIME WITH TIMEZONE}.
         *
         * @since 1.8
         */
        public static final int TIME_WITH_TIMEZONE = 2013;

        /**
         * The constant in the Java programming language, sometimes referred to
         * as a type code, that identifies the generic SQL type
         * {@code TIMESTAMP WITH TIMEZONE}.
         *
         * @since 1.8
         */
        public static final int TIMESTAMP_WITH_TIMEZONE = 2014;

        // Prevent instantiation
        private Types() {}
    }

}
