package bpm.fd.api.core.model.datas;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import bpm.fd.api.core.model.IStatuable;
import bpm.fd.api.internal.ILabelable;


public class DataSet implements ILabelable, IStatuable{
	final public static String DESCRIPTOR_CHANGED = "bpm.fd.api.core.model.datas.DataSet.descriptorchanged";
	
	
//	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	
	private String name;
	private Properties publicProperties, privateProperties;
	private String odaExtensionDataSetId;
	private String odaExtensionDataSourceId;
	private String queryText;
	private String dataSourceName;
	private DataSetDescriptor descriptor;
	
	private int status = IStatuable.UNDEFINED;
	private List<Exception> problems = new ArrayList<Exception>();
	
	public DataSet(String name, String odaExtensionDataSetId, String odaExtensionDataSourceId, Properties publicProp, Properties privateProp, String queryText, DataSource dataSource){
		this.name = name;
		this.privateProperties = privateProp;
		this.publicProperties = publicProp;
		this.odaExtensionDataSetId = odaExtensionDataSetId;
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
		this.dataSourceName = dataSource.getName();
//		try {
//			this.queryText = new String(queryText.getBytes(), "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			
//			e.printStackTrace();
//		
//		}
		this.queryText = queryText;
	}
	
//	public void addPropertyChangeListener(PropertyChangeListener listener){
//		listeners.addPropertyChangeListener(listener);
//	}
//	
//	public void removePropertyChangeListener(PropertyChangeListener listener){
//		listeners.removePropertyChangeListener(listener);
//	}
	
	public String getLabel(){
		return getName();
	}
	
	/**
	 * rebuild the descriptor with the given dataSource
	 * and set the dataSourceName with the given DataSOurce
	 * @throws 
	 *  - Exception if a problem occur when rebuilding DataSetDesciptor
	 * and set the descriptor as an empty one(no column, no parameters)
	 *  - Exception if dataSource is null
	 */
	public void buildDescriptor(DataSource dataSource) throws Exception{
		if (dataSource == null){
			throw new Exception("Cannot rebuild DataSetDescriptor from a null DataSource");
		}
		DataSetDescriptor old = getDataSetDescriptor();
		try{
			 
			setQueryText(dataSource, queryText);
			problems.clear();
			status = IStatuable.OK;
			setDataSourceName(dataSource.getName());
		} catch(ClassNotFoundException e) {
			this.descriptor = new DataSetDescriptor(this);
		} catch(Exception e){
			status = IStatuable.ERROR;
			problems.add(e);
			this.descriptor = new DataSetDescriptor(this);
//			e.printStackTrace();
		}

	}
	
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return getName().replace(" ", "_");
	}
	
	
	public Element getElement() {
		Element e = DocumentHelper.createElement("dataSet");
		e.addElement("name").setText(getName());
		e.addElement("dataSourceName").setText(dataSourceName);
		e.addElement("odaExtensionDataSetId").setText(odaExtensionDataSetId + "");
		e.addElement("odaExtensionDataSourceId").setText(odaExtensionDataSourceId + "");
		e.addElement("queryText").addCDATA(queryText);
		
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
			
		//parameters names
		
		Element params = e.addElement("parameters");
		for(ParameterDescriptor p : getDataSetDescriptor().getParametersDescriptors()){
			Element o = params.addElement("parameter").addAttribute("name", p.getName());
			o.addAttribute("label", p.getLabel());
			o.addAttribute("position", p.getPosition() + "") ;
		}
		
		return e;
	}

	public void setDataSourceName(String name2) {
		dataSourceName = name2;
		
	}
	
	public String getDataSourceName(){
		return dataSourceName;
	}

	/**
	 * @return the odaExtensionDataSetId
	 */
	public String getOdaExtensionDataSetId() {
		return odaExtensionDataSetId;
	}

	/**
	 * @param odaExtensionDataSetId the odaExtensionDataSetId to set
	 */
	public void setOdaExtensionDataSetId(String odaExtensionDataSetId) {
		this.odaExtensionDataSetId = odaExtensionDataSetId;
	}

	/**
	 * @return the odaExtensionDataSourceId
	 */
	public String getOdaExtensionDataSourceId() {
		return odaExtensionDataSourceId;
	}

	/**
	 * @param odaExtensionDataSourceId the odaExtensionDataSourceId to set
	 */
	public void setOdaExtensionDataSourceId(String odaExtensionDataSourceId) {
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
	}

	/**
	 * @return the queryText
	 */
	public String getQueryText() {
		return queryText;
	}

	/**
	 * @param queryText the queryText to set
	 */
	private void setQueryText(DataSource dataSource, String queryText) throws Exception{
		this.queryText = queryText;
		try{
			IQuery query = QueryHelper.buildquery(dataSource, this);
			IResultSetMetaData meta = null;
			try{
				meta = query.getMetaData();
			}catch(Exception e){
				if (getQueryText().contains("?")){
					int start = 0;
					int parameterCounter = 0;
					while(getQueryText().substring(start).contains("?")){
						start = start + getQueryText().substring(start).indexOf("?") + 1;
						parameterCounter++;
					}
					
					for(int i = 0; i < parameterCounter; i++){
						query.setString(i + 1, "");
					}
					try{
						meta = query.getMetaData();
					}catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
			DataSetDescriptor descriptor = new DataSetDescriptor(this);
			for( int i = 1; i <= meta.getColumnCount(); i ++){
				
				ColumnDescriptor col = new ColumnDescriptor(this, 
						i,
						meta.getColumnLabel(i),
						meta.getColumnName(i),
						meta.getColumnType(i),
						meta.getColumnTypeName(i));
				descriptor.addColumn(col);
			}
			try {
				IParameterMetaData pMeta = query.getParameterMetaData();
				
				for( int i = 1; i <= pMeta.getParameterCount(); i ++){
					int mode = IParameterMetaData.parameterModeIn;
					try{
						mode = pMeta.getParameterMode(i);
					}catch(Exception ex){
						ex.printStackTrace();
					}
					
					String name = pMeta.getParameterName(i);
					boolean noName = false;
					if (name == null){
						name = "parameter_" + i;
						noName = true;
					}
					String typeName = "String";
					Integer type = 0;
					try{
						typeName = pMeta.getParameterTypeName(i);
						type = pMeta.getParameterType(i);
					}catch(Exception e){
						
					}
					
					
					ParameterDescriptor p = new ParameterDescriptor(this, i,
							mode, name,
							type, typeName);
					
					descriptor.addParameter(p);
					
					//apply the old label value if exists
					if (this.descriptor != null && i <= this.descriptor.getParametersDescriptors().size() && noName){
						p.setLabel(this.getDataSetDescriptor().getParametersDescriptors().get(i - 1).getLabel());

					}
					
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.descriptor = descriptor;
			query.close();
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Unable to rebuild dataSet descriptor from query :" + e.getMessage(), e);
		}
		
		
		
	}

	/**
	 * @return the properties
	 */
	public java.util.Properties getPublicProperties() {
		return publicProperties;
	}
	
	/**
	 * @return the properties
	 */
	public java.util.Properties getPrivateProperties() {
		return privateProperties;
	}

	public DataSetDescriptor getDataSetDescriptor() {
		return descriptor;
	}

	public void resetDefinition(Properties publicProp, Properties privateProp, String queryText, DataSource dataSource) throws Exception{
		this.privateProperties = privateProp;
		this.publicProperties = publicProp;
		this.queryText = queryText;
		buildDescriptor(dataSource);
		
	}



	public int getStatus() {
		return status;
	}
	
	public List<Exception> getProblems(){
		return problems;
	}
	
}
