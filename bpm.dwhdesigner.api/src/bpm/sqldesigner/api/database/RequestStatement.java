package bpm.sqldesigner.api.database;

import java.sql.SQLException;
import java.sql.Statement;

import bpm.sqldesigner.api.model.Schema;

public class RequestStatement {

	protected String requestString = "";
	private Schema schema; // Only used for View creation

	public RequestStatement(String reqString) {
		requestString = reqString;
	}

	public RequestStatement() {
	}

	public void execute(DataBaseConnection dataBaseConnection) throws SQLException {
		Statement statement = null;
		SQLException error = null;
		try{
			statement = dataBaseConnection.getSocket().createStatement();
			statement.executeUpdate(requestString);
		}catch(SQLException ex) {
			error = ex;
		}finally{
			
			if (statement != null){
				statement.close();
			}
			if (error != null){
				throw error;
			}
			
		}
		
	}

	public String getRequestString() {
		return requestString;
	}

	public void setRequestString(String requestString) {
		this.requestString = requestString;
	}

	public void appendRequest(String requestString) {
		this.requestString += requestString;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Schema getSchema() {
		return schema;
	}

}
