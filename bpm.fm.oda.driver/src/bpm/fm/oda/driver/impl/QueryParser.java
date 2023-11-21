package bpm.fm.oda.driver.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class QueryParser {
	private int metricId = -1;
	private int applicationId = -1;
	
	private boolean dateAsParameter = false;
	private boolean applicationAsParameter = false;
	private boolean metricAsParameter = false;
	
	private boolean generateDateParameter = false;
	private boolean generateAxeParameter = false;
	private boolean generateMetricParameter = false;
	
	private Date startDate = null;
	private Date endDate = null;
	private String dateFormat = "yyyy-MM-dd";
	
	private Document query;
	
	public QueryParser(String query) throws Exception{
		this.query = DocumentHelper.parseText(query);
	}
	
	public QueryParser(InputStream query) throws Exception{
		this.query = DocumentHelper.parseText(IOUtils.toString(query, "UTF-8"));
	}
	
	/* <queryAssoc>
	* <fmApplicationId>id</fmApplicationId>
	* <fmMetricId>id</fmMetricId>
	* </queryAssoc>
	* */
	public void parse()throws Exception{
		metricId = -1;
		applicationId = -1;
		
		Element root = query.getRootElement();
		
		try{
			metricAsParameter = Boolean.parseBoolean(root.element("fmMetricAsParameter").getStringValue());
			if(!metricAsParameter) {
				metricId = Integer.parseInt(root.element("fmMetricId").getStringValue());
			}
		}catch(Exception ex){
			throw new Exception("Unable to get metricId in query definition");
		}
		try{
			applicationAsParameter = Boolean.parseBoolean(root.element("fmApplicationAsParameter").getStringValue());
			if(!applicationAsParameter) {
				applicationId = Integer.parseInt(root.element("fmApplicationId").getStringValue());
			}
		}catch(Exception ex){
			throw new Exception("Unable to get applicationId in query definition");
		}
		try{
			dateAsParameter = Boolean.parseBoolean(root.element("dateAsParameter").getStringValue());
		}catch(Exception ex){
			
		}
		try{
			dateFormat =root.element("dateFormat").getStringValue();
		}catch(Exception ex){
			
		}
		
		if(!dateAsParameter) {
			try {
				String sd = root.element("dateStart").getStringValue();
				startDate = new SimpleDateFormat(dateFormat).parse(sd);
			} catch(Exception e) {

			}
			
			try {
				String ed = root.element("dateEnd").getStringValue();
				endDate = new SimpleDateFormat(dateFormat).parse(ed);
			} catch(Exception e) {

			}
		}
		
		try{
			generateAxeParameter = Boolean.parseBoolean(root.element("generateAxeDataset").getStringValue());
		}catch(Exception ex){
			
		}
		try{
			generateDateParameter = Boolean.parseBoolean(root.element("generateDateDataset").getStringValue());
		}catch(Exception ex){
			
		}
		try{
			generateMetricParameter = Boolean.parseBoolean(root.element("generateMetricDataset").getStringValue());
		}catch(Exception ex){
			
		}
		
	}
	
	public int getMetricId(){
		return metricId;
	}
	
	public int getApplicationId(){
		return applicationId;
	}
	
	public boolean isDateParameter(){
		return dateAsParameter;
	}
	
	public String getDateFormat(){
		return dateFormat;
	}

	public boolean isApplicationAsParameter() {
		return applicationAsParameter;
	}

	public boolean isMetricAsParameter() {
		return metricAsParameter;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public boolean isGenerateDateParameter() {
		return generateDateParameter;
	}

	public boolean isGenerateAxeParameter() {
		return generateAxeParameter;
	}

	public boolean isGenerateMetricParameter() {
		return generateMetricParameter;
	}
}
