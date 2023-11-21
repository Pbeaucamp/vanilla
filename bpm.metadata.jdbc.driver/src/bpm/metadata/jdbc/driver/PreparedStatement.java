package bpm.metadata.jdbc.driver;

import java.sql.Connection;
import java.util.ArrayList;

public class PreparedStatement extends Statement {
	
	private String originalSql;

	public PreparedStatement(Connection connection, String sql) {
		
		super(connection);
		this.originalSql = sql;
		
		ArrayList params = new ArrayList();
	}
	
	

}
