package bpm.sqldesigner.api.constants.types.dbms;

import bpm.sqldesigner.api.constants.types.StandardTypes;

public class MySQLTypes extends DBMSTypes {

	@Override
	public void initEquivStandardTypes() {

		/***********************************************************************
		 * INTEGER
		 **********************************************************************/
		equivStandardTypes.put("TINYINT", StandardTypes.INTEGER);
		equivStandardTypes.put("SMALLINT", StandardTypes.INTEGER);
		equivStandardTypes.put("MEDIUMINT", StandardTypes.INTEGER);
		equivStandardTypes.put("INT", StandardTypes.INTEGER);
		equivStandardTypes.put("INTEGER", StandardTypes.INTEGER);
		equivStandardTypes.put("BIGINT", StandardTypes.INTEGER);

		/***********************************************************************
		 * NUMERIC
		 **********************************************************************/
		equivStandardTypes.put("FLOAT", StandardTypes.NUMERIC);
		equivStandardTypes.put("DOUBLE", StandardTypes.NUMERIC);
		equivStandardTypes.put("REAL", StandardTypes.NUMERIC);
		equivStandardTypes.put("DECIMAL", StandardTypes.NUMERIC);
		equivStandardTypes.put("NUMERIC", StandardTypes.NUMERIC);
		equivStandardTypes.put("DOUBLE PRECISION", StandardTypes.NUMERIC);

		/***********************************************************************
		 * DATE
		 **********************************************************************/
		equivStandardTypes.put("DATE", StandardTypes.DATE);

		/***********************************************************************
		 * DATETIME
		 **********************************************************************/
		equivStandardTypes.put("DATETIME", StandardTypes.DATETIME);
		equivStandardTypes.put("TIMESTAMP", StandardTypes.DATETIME);
		equivStandardTypes.put("TIME", StandardTypes.DATETIME);
		
		/***********************************************************************
		 * TEXT
		 **********************************************************************/
		equivStandardTypes.put("TEXT", StandardTypes.TEXT);
		equivStandardTypes.put("TINYTEXT", StandardTypes.TEXT);

		/***********************************************************************
		 * VARCHAR
		 **********************************************************************/
		equivStandardTypes.put("VARCHAR", StandardTypes.VARCHAR);
		equivStandardTypes.put("CHAR", StandardTypes.VARCHAR);

		/***********************************************************************
		 * BYTE
		 **********************************************************************/
		equivStandardTypes.put("BINARY", StandardTypes.BYTE);

		/***********************************************************************
		 * BOOLEAN
		 **********************************************************************/
		equivStandardTypes.put("BOOLEAN", StandardTypes.BOOLEAN);
		equivStandardTypes.put("BIT", StandardTypes.BOOLEAN);

		/***********************************************************************
		 * NONE
		 **********************************************************************/
		equivStandardTypes.put("BLOB", StandardTypes.NONE);
		equivStandardTypes.put("TINYBLOB", StandardTypes.NONE);
		equivStandardTypes.put("MEDIUMBLOB", StandardTypes.NONE);
		equivStandardTypes.put("LONGBLOB", StandardTypes.NONE);

		equivStandardTypes.put("LONGTEXT", StandardTypes.NONE);
		equivStandardTypes.put("MEDIUMTEXT", StandardTypes.NONE);

		equivStandardTypes.put("ENUM", StandardTypes.NONE);
		equivStandardTypes.put("SET", StandardTypes.NONE);

		equivStandardTypes.put("LONG VARBINARY", StandardTypes.NONE);
		equivStandardTypes.put("LONG VARCHAR", StandardTypes.NONE);
	}

	@Override
	public void initEquivMySQLTypes() {
		
	}

}
