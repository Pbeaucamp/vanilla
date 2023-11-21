package bpm.gateway.core.tsbn.db;

public class DialectHelper {
	
	public static final String getDialect(String driver) {
		if(driver.equalsIgnoreCase("com.mysql.jdbc.Driver")) {
			return "org.hibernate.dialect.MySQLDialect";
		}
		else if (driver.equalsIgnoreCase("org.hsqldb.jdbcDriver")) {
			return "org.hibernate.dialect.HSQLDialect";
		}
		else if (driver.equalsIgnoreCase("org.h2.Driver")) {
			return "org.hibernate.dialect.H2Dialect";
		}
		else if (driver.equalsIgnoreCase("com.ingres.jdbc.IngresDriver")) {
			return "org.hibernate.dialect.IngresDialect";
		}
		else if (driver.equalsIgnoreCase("com.teradata.jdbc.TeraDriver")) {
			return "org.hibernate.dialect.TeradataDialect";
		}
		else if (driver.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")) {
			return "org.hibernate.dialect.Oracle10gDialect";
		}
		else if (driver.equalsIgnoreCase("org.postgresql.Driver")) {
			return "org.hibernate.dialect.PostgreSQLDialect";
		}
		else if (driver.equalsIgnoreCase("org.apache.derby.jdbc.ClientDriver")) {
			return "org.hibernate.dialect.DerbyDialect";
		}
		else if (driver.equalsIgnoreCase("net.sourceforge.jtds.jdbc.Driver")) {
			return "org.hibernate.dialect.SQLServerDialect";
		}
		else if (driver.equalsIgnoreCase("com.vertica.Driver")) {
			return "org.hibernate.dialect.PostgreSQLDialect";
		}
		else if (driver.equalsIgnoreCase("org.mariadb.jdbc.Driver")) {
			return "org.hibernate.dialect.MySQLDialect";
		}
		return "";
	}
}
