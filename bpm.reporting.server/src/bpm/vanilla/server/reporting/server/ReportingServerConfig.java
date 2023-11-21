package bpm.vanilla.server.reporting.server;

import java.util.Properties;

import bpm.vanilla.server.commons.server.ServerConfig;

public class ReportingServerConfig extends ServerConfig{

	public static final String REPOSITORY_POOL_SIZE = "bpm.vanilla.server.reporting.server.repositoryPoolSize";
	public static final String REPORT_POOL_SIZE = "bpm.vanilla.server.reporting.server.reportPoolSize";
	public static final String REPORT_GENERATION_FOLDER = "bpm.vanilla.server.reporting.server.generationFolder";
	public static final String BIRT_REPORT_ENGINE_LOGS_PATH = "bpm.vanilla.server.reporting.server.birtReportEngineLogs";
	public static final String JASPER_IMAGES_URI = "bpm.vanilla.server.reporting.server.imagesUri";
	
	/**
	 * default 10
	 */
	private int repositoryPoolSize = 10;
	
	private int reportPoolSize = 100;
	
	private String birtReportEngineLogsPath;
	private String jasperImagesUri;
	
	
	
	{
		propertyNames.add(REPOSITORY_POOL_SIZE);
		propertyNames.add(REPORT_POOL_SIZE);
		propertyNames.add(REPORT_GENERATION_FOLDER);
		propertyNames.add(BIRT_REPORT_ENGINE_LOGS_PATH);
		propertyNames.add(JASPER_IMAGES_URI);

	}
	
	/**
	 * @return the birtReportEngineLogsPath
	 */
	public String getBirtReportEngineLogsPath() {
		return birtReportEngineLogsPath;
	}

	/**
	 * @param birtReportEngineLogsPath the birtReportEngineLogsPath to set
	 */
	public void setBirtReportEngineLogsPath(String birtReportEngineLogsPath) {
		this.birtReportEngineLogsPath = birtReportEngineLogsPath;
	}

	private String generationFolder;
	
	public ReportingServerConfig(Properties prop) {
		super(prop);
		setRepositoryPoolSize(prop.getProperty(REPOSITORY_POOL_SIZE));
		setReportPoolSize(prop.getProperty(REPORT_POOL_SIZE));
		setGenerationFolder(prop.getProperty(REPORT_GENERATION_FOLDER));
		setBirtReportEngineLogsPath(prop.getProperty(BIRT_REPORT_ENGINE_LOGS_PATH));
		setJasperImagesUri(prop.getProperty(JASPER_IMAGES_URI));
		
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
	

	public String getJasperImagesUri() {
		return jasperImagesUri;
	}

	public void setJasperImagesUri(String jasperImagesUri) {
		this.jasperImagesUri = jasperImagesUri;
	}

	public String getPropertyValue(String name){
		String value = super.getPropertyValue(name);
		if (value != null){
			return value;
		}
		if(BIRT_REPORT_ENGINE_LOGS_PATH.equals(name)){
			return getBirtReportEngineLogsPath() + "";		
		}
		if(REPORT_GENERATION_FOLDER.equals(name)){
			return getGenerationFolder() + "";
		}
		if(REPORT_POOL_SIZE.equals(name)){
			return getReportPoolSize() +"";
		}
		if(REPOSITORY_POOL_SIZE.equals(name)){
			return getRepositoryPoolSize() + "";
		}
		if (JASPER_IMAGES_URI.equals(name)) {
			return getJasperImagesUri();
		}
		return null;
	}
}
