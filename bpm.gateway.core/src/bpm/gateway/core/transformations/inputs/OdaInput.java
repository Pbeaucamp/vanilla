package bpm.gateway.core.transformations.inputs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.gateway.core.AbstractTransformation;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.inputs.odaconsumer.OdaHelper;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.transformations.inputs.RunOdaInput;
import bpm.vanilla.platform.core.IRepositoryContext;

public class OdaInput extends AbstractTransformation{

	protected StreamDescriptor descriptor = new DefaultStreamDescriptor();
	private Properties datasourcePublicProperties = new Properties();
	private Properties datasourcePrivateProperties = new Properties();
	private Properties datasetPublicProperties = new Properties();
	private Properties datasetPrivateProperties = new Properties();
	private String odaExtensionDataSourceId;
	private String odaExtensionId;
	private String queryText;
	
	
	private HashMap<String, Integer> parameterMap = new LinkedHashMap<String, Integer>();
	
	
	/**
	 * @return the odaExtensionDataSourceId
	 */
	public String getOdaExtensionDataSourceId() {
		return odaExtensionDataSourceId;
	}

	/**
	 * @return the datasourcePublicProperties
	 */
	public Properties getDatasourcePublicProperties() {
		return datasourcePublicProperties;
	}

	/**
	 * @param datasourcePublicProperties the datasourcePublicProperties to set
	 */
	public void setDatasourcePublicProperties(Properties datasourcePublicProperties) {
		this.datasourcePublicProperties = datasourcePublicProperties;
	}
	public void setDatasourcePublicProperty(String name, String value) {
		this.datasourcePublicProperties.setProperty(name, value);
		
	}

	/**
	 * @return the datasourcePrivateProperties
	 */
	public Properties getDatasourcePrivateProperties() {
		return datasourcePrivateProperties;
	}

	/**
	 * @param datasourcePrivateProperties the datasourcePrivateProperties to set
	 */
	public void setDatasourcePrivateProperties(	Properties datasourcePrivateProperties) {
		this.datasourcePrivateProperties = datasourcePrivateProperties;
	}
	public void setDatasourcePrivateProperty(String name, String value) {
		this.datasourcePrivateProperties.setProperty(name, value);
		
	}
	
	@Override
	public StreamDescriptor getDescriptor(Transformation transfo) throws ServerException {
		return descriptor;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("odaInput");
		
		e.addElement("name").setText(name);
		e.addElement("description").setText(description);
		
		
		e.addElement("positionX").setText("" + x);
		e.addElement("positionY").setText("" + y);
		e.addElement("temporaryFileName").setText(getTemporaryFilename());
		e.addElement("temporarySeparator").setText(getTemporarySpliterChar() + "");
		
		if (getOdaExtensionId() != null){
			e.addElement("odaExtensionId").setText(getOdaExtensionId());
		}
		
		if (getOdaExtensionDataSourceId() != null){
			e.addElement("odaExtensionDataSourceId").setText(getOdaExtensionDataSourceId());
		}
		
		
		
		Element p = null;
		Enumeration<String> en =null;
		
		if (getDatasourcePublicProperties() != null){
			p = e.addElement("publicDataSource");
			en = (Enumeration<String>)datasourcePublicProperties.propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				
				_p.addElement("value").setText(datasourcePublicProperties.getProperty(pname));
			}
		}
		
		if (getDatasourcePrivateProperties() != null){
			p = e.addElement("privateDataSource");
			en = (Enumeration<String>)datasourcePrivateProperties.propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				_p.addElement("value").setText(datasourcePrivateProperties.getProperty(pname));
			}
		}
		
		
		if (getDatasetPrivateProperties() != null){
			p = e.addElement("privateDataSet");
			en = (Enumeration<String>)datasetPrivateProperties.propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				_p.addElement("value").setText(datasetPrivateProperties.getProperty(pname));
			}
		}
		
		if (getDatasetPublicProperties() != null){
			p = e.addElement("publicDataSet");
			en = (Enumeration<String>)datasetPublicProperties.propertyNames();
			
			while(en.hasMoreElements()){
				String pname = en.nextElement();
				Element _p = p.addElement("property");
				_p.addElement("name").setText(pname);
				_p.addElement("value").setText(datasetPublicProperties.getProperty(pname));
			}
		}
		
		if (getQueryText() != null){
			e.addElement("queryText").setText(getQueryText());
		}
		
		
		for(String s : getParameterNames()){
			p = e.addElement("parameter");
			p.addElement("name").setText(s);
			Integer i =getParameterValue(s) ;
			
			if (i != null){
				p.addElement("value").setText( i + "");
			}
			else{
				p.addElement("value");
			}
			
			
		}
		return e;
	}

	@Override
	public RuntimeStep getExecutioner(IRepositoryContext repositoryCtx, int bufferSize) {
		return new RunOdaInput(this, bufferSize);
	}

	@Override
	public void refreshDescriptor() {
		if (!isInited()){
			return;
		}
		try {
			descriptor = OdaHelper.createDescriptor(this);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		for(Transformation t : getOutputs()){
			t.refreshDescriptor();
		}
	}

	public Transformation copy() {
		
		return null;
	}

	
	public void setOdaExtensionDataSourceId(String odaExtensionDataSourceId) {
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
		
	}

	public String getOdaExtensionId() {
		return odaExtensionId;
	}
	
	/**
	 * @return the datasetPublicProperties
	 */
	public Properties getDatasetPublicProperties() {
		return datasetPublicProperties;
	}

	/**
	 * @return the datasetPrivateProperties
	 */
	public Properties getDatasetPrivateProperties() {
		return datasetPrivateProperties;
	}

	/**
	 * @return the queryText
	 */
	public String getQueryText() {
		return queryText;
	}

	public void setOdaExtensionId(String odaExtensionId) {
		this.odaExtensionId = odaExtensionId;
	}

	public void setDatasetPublicProperties(Properties publicProp) {
		this.datasetPublicProperties = publicProp;
		
	}
	
	public void setDatasetPublicProperty(String name, String value) {
		this.datasetPublicProperties.setProperty(name, value);
		
	}
	public void setDatasetPrivateProperties(Properties privateProp) {
		this.datasetPrivateProperties = privateProp;
		
	}
	public void setDatasetPrivateProperty(String name, String value) {
		this.datasetPrivateProperties.setProperty(name, value);
		
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
		refreshDescriptor();
	}

	public Collection<String> getParameterNames(){
		return new ArrayList<String>(parameterMap.keySet());
	}
	
	/**
	 * add a Parameter
	 * should not be called, it is called when building the 
	 * Descriptro by OdaHelper.buildDescriptor class
	 * @param parameter
	 */
	public void addParameterNames(String parameter){
		for(String s : parameterMap.keySet()){
			if (s.equals(parameter)){
				return;
			}
		}
		
		parameterMap.put(parameter, null);
	}
	
	
	public void removeParameter(String pname){
		for(String s : parameterMap.keySet()){
			if (s.equals(pname)){
				parameterMap.remove(s);
				return;
			}
		}
	}
	
	public void setParameter(String param, String value){
		try{
			setParameter(param, Integer.parseInt(value));
		}catch(Exception ex){
			
		}
	}
	
	public void setParameter(String param, Integer i){
		parameterMap.put(param, i);
	}

	public Integer getParameterValue(String element) {
		for(String s : parameterMap.keySet()){
			if (s.equals(element)){
				return parameterMap.get(s);
			}
		}
		return null;
	}

	/**
	 * remove all parameters
	 * should not be called by user, only called when OdaHelper build the
	 * Descriptor
	 */
	public void removeAllParameters() {
		parameterMap.clear();
		
	}
	
	public String getAutoDocumentationDetails() {
		StringBuffer buf = new StringBuffer();
		buf.append("DataSource Properties : \n")		;
		for(Object o : datasourcePublicProperties.keySet()){
			buf.append("\t- " + o.toString() + "=" + datasourcePublicProperties.get(o) + "\n");
		}
		for(Object o : datasourcePrivateProperties.keySet()){
			buf.append("\t- " + o.toString() + "=" + datasourcePrivateProperties.get(o) + "\n");
		}
		buf.append("DataSet Properties : \n")		;
		for(Object o : datasetPublicProperties.keySet()){
			buf.append("\t- " + o.toString() + "=" + datasetPublicProperties.get(o) + "\n");
		}
		for(Object o : datasetPrivateProperties.keySet()){
			buf.append("\t- " + o.toString() + "=" + datasetPrivateProperties.get(o) + "\n");
		}
		
		buf.append("Query : \n" + queryText + "\n");
		return buf.toString();
	}
}
