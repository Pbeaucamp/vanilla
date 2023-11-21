package bpm.forms.runtime.submission;

import javax.sql.DataSource;

public class DatabaseUpdater {

	private DataSource dataSource;
	
	public DatabaseUpdater(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	public void checkTable(String tableName){
		
	}
	
}
