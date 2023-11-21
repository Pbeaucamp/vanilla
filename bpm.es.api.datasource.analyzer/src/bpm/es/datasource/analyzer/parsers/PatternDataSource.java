package bpm.es.datasource.analyzer.parsers;

import bpm.vanilla.platform.core.repository.DataSource;



public class PatternDataSource implements IPattern{
	private DataSource datasource;
		
	public PatternDataSource(DataSource datasource) throws Exception{
		this.datasource = datasource;
		
		if(!datasource.getOdaExtensionId().equals(DataSource.DATASOURCE_JDBC)) {
			throw new Exception("This type of datasource is not supported : " + datasource.getOdaExtensionId());
		}
	}
	
	public String getUrl(){
		String datasourceUrl = datasource.getDatasourcePublicProperties().getProperty(DataSource.DATASOURCE_JDBC_URL);
		return datasourceUrl != null ? datasourceUrl : "";
	}
}
