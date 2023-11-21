package bpm.fd.api.core.model.datas;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fd.api.core.model.resources.Dictionary;
import bpm.fd.api.internal.ILabelable;


public class DataSource implements ILabelable{

	private Properties publicProperties, privateProperties;
	private String odaExtensionDataSourceId;
	private String odaDriverClassName;
	
	private String odaExtensionId;
	private String name;
	private Dictionary dictionary;
	
	private List<IConnection> openedConnections = new ArrayList<IConnection>();
	
	public DataSource(Dictionary dictionary, String name, String odaExtensionDataSourceId, String odaExtensionId, Properties publicProperties, Properties privateProperties){
		this.dictionary = dictionary;
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
		this.odaExtensionId = odaExtensionId;
		this.name = name;
		this.privateProperties = privateProperties;
		this.publicProperties = publicProperties;
		
	}
	public DataSource(Dictionary dictionary, String name, String odaExtensionDataSourceId, String odaExtensionId, String odaDriverClassName, Properties publicProperties, Properties privateProperties){
		this.dictionary = dictionary;
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
		this.odaExtensionId = odaExtensionId;
		this.name = name;
		this.privateProperties = privateProperties;
		this.publicProperties = publicProperties;
		this.odaDriverClassName = odaDriverClassName;
	}
	public String getLabel(){
		return getName();
	}
	public Dictionary getDictionary(){
		return dictionary;
	}
	
	public String getName(){
		return name;
	}
	
	public String getId(){
		return getName().replace(" ", "_");
	}
	
	/**
	 * @param publicProperties the publicProperties to set
	 */
	public void setPublicProperties(Properties publicProperties) {
		this.publicProperties = publicProperties;
	}

	/**
	 * @param privateProperties the privateProperties to set
	 */
	public void setPrivateProperties(Properties privateProperties) {
		this.privateProperties = privateProperties;
	}

	/**
	 * @param odaExtensionDataSourceId the odaExtensionDataSourceId to set
	 */
	public void setOdaExtensionDataSourceId(String odaExtensionDataSourceId) {
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
	}

	/**
	 * @param odaExtensionId the odaExtensionId to set
	 */
	public void setOdaExtensionId(String odaExtensionId) {
		this.odaExtensionId = odaExtensionId;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	
	public String getOdaDriverClassName(){
		return odaDriverClassName;
	}
	public Element getElement() {
		Element e = DocumentHelper.createElement("dataSource");
		e.addElement("name").setText(getName());
		e.addElement("odaExtensionDataSourceId").setText(getOdaExtensionDataSourceId());
		e.addElement("odaExtensionId").setText(getOdaExtensionId());
		if (getOdaDriverClassName() != null){
			e.addElement("odaDriverClassName").setText(getOdaDriverClassName());
		}
		

		
		for(Object o : publicProperties.keySet()){
			Element prop = e.addElement("publicProperty");
			prop.addAttribute("name", (String)o);
			prop.setText(publicProperties.getProperty((String)o));
			
		}
		
		for(Object o : privateProperties.keySet()){
			Element prop = e.addElement("privateProperty");
			prop.addAttribute("name", (String)o);
			prop.setText(privateProperties.getProperty((String)o));
		}

		return e;
	}

	/**
	 * @return the odaExtensionDataSourceId
	 */
	public String getOdaExtensionDataSourceId() {
		return odaExtensionDataSourceId;
	}

	/**
	 * @return the odaExtensionId
	 */
	public String getOdaExtensionId() {
		return odaExtensionId;
	}

	public Properties getPublicProperties() {
		return publicProperties;
	}

	public Properties getPrivateProperties() {
		return privateProperties;
	}

	public Properties getProperties() {
		Properties p = new Properties();
		for(Object o : publicProperties.keySet()){
			p.setProperty((String)o, publicProperties.getProperty((String)o));
		}
		for(Object o : privateProperties.keySet()){
			p.setProperty((String)o, privateProperties.getProperty((String)o));
		}
		return p;
	}

	public List<DataSet> getDataSet() {
		List<DataSet> dataSets = new ArrayList<DataSet>();
		for(DataSet ds : dictionary.getDatasets()){
			if (ds.getDataSourceName().equals(this.getName())){
				dataSets.add(ds);
			}
		}
		return dataSets;
	}

	public void setOdaDriverClassName(String className) {
		this.odaDriverClassName = className;
		
	}
	public void addOpenedConnection(IConnection c) {
		synchronized(openedConnections){
			openedConnections.add(c);
		}
		
		
	}

	public void closeConnections(){
		synchronized(openedConnections){
			for(IConnection c : openedConnections){
				try {
					c.close();
				} catch (OdaException e) {
					
					e.printStackTrace();
				}
			}
			openedConnections.clear();
		}
		
	}

}
