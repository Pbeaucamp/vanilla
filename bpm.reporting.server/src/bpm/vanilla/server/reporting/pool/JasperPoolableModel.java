package bpm.vanilla.server.reporting.pool;

import java.io.ByteArrayInputStream;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.server.commons.pool.PoolableModel;
import bpm.vanilla.server.commons.pool.VanillaItemKey;


public class JasperPoolableModel extends PoolableModel<JasperReport>{

	public static final int DATASOURCE_FMDT = 10;
	public static final int DATASOURCE_DB_REP = 11;
	public static final int NO_DATASOURCE_DEFINED = 0;
	
	
	private int jasperModelType = NO_DATASOURCE_DEFINED;
	
	private Integer dataSourceId = null;
	private String fmdtId;
	private String fmdtModelName;
	private String fmdtPackageName;
	private String fmdtQuery;
	
	public JasperPoolableModel(RepositoryItem directoryItem, String xml, VanillaItemKey itemKey) throws Exception {
		super(directoryItem, xml,itemKey);
		
		extractDataSourceDatas();
		
	}

	
	
	/* (non-Javadoc)
	 * @see bpm.vanilla.server.commons.pool.PoolableModel#setXml(java.lang.String)
	 */
	@Override
	protected void setXml(String xml) {
		
		super.setXml(xml);
	}



	@Override
	protected void buildModel() throws Exception {
		
		
	}
	
	
	private void extractDataSourceDatas() throws Exception{
		Document doc = DocumentHelper.parseText(getXml());
		Element root = doc.getRootElement();
		Element report = root.element("xml").element("jasperReport");
		
		
		/*
		 * DataSource as DataBase connection defined in the repository
		 */
		try{
//			Element customModelElement = root.element("CustomModel");
			if (root.element("datasourceid") != null){
				String dataSourceId = root.element("datasourceid").getStringValue();
				setDataSourceId(Integer.parseInt(dataSourceId));
//				report = report.element("xml").element("jasperReport");
				setJasperModelType(DATASOURCE_DB_REP);
			}
		}catch(Exception ex){
			
		}
		/*
		 * DataSource as FMDT
		 */
		
		if (root.element("metadataID") != null){
			try{
				fmdtId = root.element("metadataID").getStringValue();
				fmdtModelName = root.element("modelName").getStringValue();
				fmdtPackageName = root.element("packageName").getStringValue();
				fmdtQuery = root.element("query").getStringValue();
				
				jasperModelType = DATASOURCE_FMDT;
			}catch(Exception ex){
				
				throw ex;
			}
		}
		/*
		 * build JasperDesign	
		 */
		String sXml = report.asXML();
		if(!sXml.startsWith("<?xml  version=\"1.0\"?>" +
				"<!DOCTYPE jasperReport PUBLIC \"-//JasperReports//DTD Report Design//EN\"" +
		" \"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd\">")){
			
			
			sXml ="<?xml version=\"1.0\"?>" +sXml ;
			
//			sXml= sXml+"<!DOCTYPE jasperReport PUBLIC \"-//JasperReports//DTD Report Design//EN\"" +
//			" \"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd\">" + sXml;
		}
		
		byte currentXMLBytes[] = sXml.getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes); 
//		System.setProperty("jasper.reports.compile.class.path", "D:\\workspace\\vanilla-tomcat\\webapps\\FdDeployer\\WEB-INF\\classes\\");
		JasperDesign jasperDesign = null;
		try{
			jasperDesign = JRXmlLoader.load(byteArrayInputStream);
		}catch(Exception ex){
			throw new Exception("Cannot create JasperDeesign from XML", ex);
		}
		try{
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			setModel(jasperReport);
		}catch(Exception ex){
			throw new Exception("Cannot compile JasperDesign ", ex);
		}
	}

	private void setDataSourceId(Integer id){
		dataSourceId = id;
		if(dataSourceId == null){
			jasperModelType = DATASOURCE_DB_REP;
		}
	}



	/**
	 * @return the jasperModelType
	 */
	public int getJasperModelType() {
		return jasperModelType;
	}



	/**
	 * @param jasperModelType the jasperModelType to set
	 */
	public void setJasperModelType(int jasperModelType) {
		this.jasperModelType = jasperModelType;
	}



	/**
	 * @return the fmdtId
	 */
	public String getFmdtId() {
		return fmdtId;
	}



	/**
	 * @param fmdtId the fmdtId to set
	 */
	public void setFmdtId(String fmdtId) {
		this.fmdtId = fmdtId;
	}



	/**
	 * @return the fmdtModelName
	 */
	public String getFmdtModelName() {
		return fmdtModelName;
	}



	/**
	 * @param fmdtModelName the fmdtModelName to set
	 */
	public void setFmdtModelName(String fmdtModelName) {
		this.fmdtModelName = fmdtModelName;
	}



	/**
	 * @return the fmdtPackageName
	 */
	public String getFmdtPackageName() {
		return fmdtPackageName;
	}



	/**
	 * @param fmdtPackageName the fmdtPackageName to set
	 */
	public void setFmdtPackageName(String fmdtPackageName) {
		this.fmdtPackageName = fmdtPackageName;
	}



	/**
	 * @return the fmdtQuery
	 */
	public String getFmdtQuery() {
		return fmdtQuery;
	}



	/**
	 * @param fmdtQuery the fmdtQuery to set
	 */
	public void setFmdtQuery(String fmdtQuery) {
		this.fmdtQuery = fmdtQuery;
	}



	/**
	 * @return the dataSourceId
	 */
	public Integer getDataSourceId() {
		return dataSourceId;
	}
	
	
}
