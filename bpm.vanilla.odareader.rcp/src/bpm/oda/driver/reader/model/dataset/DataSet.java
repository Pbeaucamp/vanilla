package bpm.oda.driver.reader.model.dataset;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import bpm.oda.driver.reader.model.ILabelable;
import bpm.oda.driver.reader.model.datasource.DataSource;



public class DataSet implements ILabelable{
	
	private String name;
	private Properties publicProperties, privateProperties;
	private String odaExtensionDataSetId;
	private String odaExtensionDataSourceId;
	private String queryText;
	private String dataSourceName;
	private DataSetDescriptor descriptor;
	
	private IConnection connectionDataSet;
	private IResultSet resultSet;
	private boolean resultSetUpdated;
	
	public DataSet() {
		super();
	}
	
	
	public DataSet(String name, String odaExtensionDataSetId, String odaExtensionDataSourceId, Properties publicProp, Properties privateProp, String queryText, DataSource dataSource){
		this.name = name;
		this.privateProperties = privateProp;
		this.publicProperties = publicProp;
		this.odaExtensionDataSetId = odaExtensionDataSetId;
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
		this.dataSourceName = dataSource.getName();
		
		try {
			setQueryText(dataSource, queryText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resultSetUpdated = true;
	}
	
	



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Properties getPublicProperties() {
		return publicProperties;
	}
	public void setPublicProperties(Properties publicProperties) {
		this.publicProperties = publicProperties;
	}
	public Properties getPrivateProperties() {
		return privateProperties;
	}
	public void setPrivateProperties(Properties privateProperties) {
		this.privateProperties = privateProperties;
	}
	public String getOdaExtensionDataSetId() {
		return odaExtensionDataSetId;
	}
	public void setOdaExtensionDataSetId(String odaExtensionDataSetId) {
		this.odaExtensionDataSetId = odaExtensionDataSetId;
	}
	public String getOdaExtensionDataSourceId() {
		return odaExtensionDataSourceId;
	}
	public void setOdaExtensionDataSourceId(String odaExtensionDataSourceId) {
		this.odaExtensionDataSourceId = odaExtensionDataSourceId;
	}
	public String getQueryText() {
		return queryText;
	}
	
	public void setQueryText(String queryText){
		this.queryText = queryText;
	}
	
	public void setQueryText(DataSource dataSource,  String queryText) throws Exception{
		this.queryText = queryText;
		try{
			
			IQuery query = QueryHelper.buildquery(dataSource, this);
			
			IResultSetMetaData meta = null;
			try{
				meta = query.getMetaData();
			}catch(Exception e){
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
			IParameterMetaData pMeta = query.getParameterMetaData();
			
			for( int i = 1; i <= pMeta.getParameterCount(); i ++){
				int mode = pMeta.getParameterMode(i);
				
				String name = pMeta.getParameterName(i);
				if (name == null){
					name = "parameter_" + i;
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
				if (this.descriptor != null && i <= this.descriptor.getParametersDescriptors().size() ){
					p.setLabel(this.getDescriptor().getParametersDescriptors().get(i - 1).getLabel());

				}
				
				
			}
			this.descriptor = descriptor;
			
			query.close();
			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Unable to rebuild dataSet descriptor from query :" + e.getMessage(), e);
		}
		
		
		
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}



	public DataSetDescriptor getDescriptor() {
		return descriptor;
	}



	public void setDescriptor(DataSetDescriptor descriptor) {
		this.descriptor = descriptor;
	}


	public IConnection getConnectionDataSet() {
		return connectionDataSet;
	}


	public void setConnectionDataSet(IConnection connectionDataSet) {
		this.connectionDataSet = connectionDataSet;
	}


	public IResultSet getResultSet() {
		return resultSet;
	}


	public void setResultSet(IResultSet resultSet) {
		this.resultSet = resultSet;
	}


	public boolean isResultSetUpdated() {
		return resultSetUpdated;
	}


	public void setResultSetUpdated(boolean resultSetUpdated) {
		this.resultSetUpdated = resultSetUpdated;
	}


}
