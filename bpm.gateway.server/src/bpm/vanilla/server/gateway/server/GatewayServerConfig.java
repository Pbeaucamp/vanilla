package bpm.vanilla.server.gateway.server;

import java.util.Properties;

import bpm.vanilla.server.commons.server.ServerConfig;

public class GatewayServerConfig extends ServerConfig{

	public static final String REPOSITORY_POOL_SIZE = "bpm.vanilla.server.gateway.server.repositoryPoolSize";
	public static final String REPORT_POOL_SIZE = "bpm.vanilla.server.gateway.server.reportPoolSize";
	public static final String RUNTIME_MAX_ROWS = "bpm.vanilla.server.gateway.server.maxrows";
	public static final String GATEWAY_HOME = "bpm.vanilla.server.gateway.server.homeFolder";
	public static final String GATEWAY_TEMP = "bpm.vanilla.server.gateway.server.tempFolder";
	
		
	/**
	 * default 10
	 */
	private int repositoryPoolSize = 10;
	
	private int reportPoolSize = 100;
	
	private int maxRows = 50000;
	
	private String homeFolder;
	private String tempFolder;
	
	
	{
		propertyNames.add(REPOSITORY_POOL_SIZE);
		propertyNames.add(REPORT_POOL_SIZE);
		propertyNames.add(GATEWAY_HOME);
		propertyNames.add(GATEWAY_TEMP);
		

	}

	private String generationFolder;
	
	public GatewayServerConfig(Properties prop) {
		super(prop);
		setRepositoryPoolSize(prop.getProperty(REPOSITORY_POOL_SIZE));
		setReportPoolSize(prop.getProperty(REPORT_POOL_SIZE));
		setMaxRows(prop.getProperty(RUNTIME_MAX_ROWS));
		setHomeFolder(prop.getProperty(GATEWAY_HOME));
		setTempFolder(prop.getProperty(GATEWAY_TEMP));
	}

	/**
	 * @return the repositoryPoolSize
	 */
	public int getRepositoryPoolSize() {
		return repositoryPoolSize;
	}

	/**
	 * @return the reportPoolSize
	 */
	public int getReportPoolSize() {
		return reportPoolSize;
	}

	/**
	 * @param repositoryPoolSize the repositoryPoolSize to set
	 */
	private void setRepositoryPoolSize(String repositoryPoolSize) {
		try{
			this.repositoryPoolSize = Integer.parseInt(repositoryPoolSize);
		}catch(NumberFormatException ex){
			
		}
	}

	/**
	 * @param reportPoolSize the reportPoolSize to set
	 */
	private void setReportPoolSize(String reportPoolSize) {
		try{
			this.reportPoolSize = Integer.parseInt(reportPoolSize);
		}catch(NumberFormatException ex){
			
		}
	}

	/**
	 * @return the generationFolder
	 */
	public String getGenerationFolder() {
		return generationFolder;
	}

	/**
	 * @param generationFolder the generationFolder to set
	 */
	public void setGenerationFolder(String generationFolder) {
		this.generationFolder = generationFolder;
	}

	public String getPropertyValue(String name){
		String value = super.getPropertyValue(name);
		if (value != null){
			return value;
		}
		if(REPORT_POOL_SIZE.equals(name)){
			return getReportPoolSize() +"";
		}
		if(REPOSITORY_POOL_SIZE.equals(name)){
			return getRepositoryPoolSize() + "";
		}
		if(RUNTIME_MAX_ROWS.equals(name)){
			return getMaxRows() + "";
		}
		if(GATEWAY_HOME.equals(name)){
			return getHomeFolder();
		}
		if(GATEWAY_TEMP.equals(name)){
			return getTempFolder();
		}
		return null;
	}

	public int getMaxRows() {
		return maxRows;
	}
	
	public void setMaxRows(String maxRows) {
		try{
			this.maxRows = Integer.parseInt(maxRows);
		}catch(NumberFormatException ex){
			
		}
	}

	/**
	 * @return the homeFolder
	 */
	public String getHomeFolder() {
		return homeFolder + "/";
	}

	/**
	 * @param homeFolder the homeFolder to set
	 */
	private void setHomeFolder(String homeFolder) {
		this.homeFolder = homeFolder;
	}

	/**
	 * @return the tempFolder
	 */
	public String getTempFolder() {
		return tempFolder;
	}

	/**
	 * @param tempFolder the tempFolder to set
	 */
	private void setTempFolder(String tempFolder) {
		this.tempFolder = tempFolder;
	}
}
