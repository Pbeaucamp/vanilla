package bpm.connection.manager.connection.oda;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

public class VanillaOdaQuery {

	private String datasetId;
	private Properties datasetPublicProperties = new Properties();
	private Properties datasetPrivateProperties = new Properties();
	
	private VanillaOdaConnection connection;
	private IQuery odaQuery;

	public VanillaOdaQuery(VanillaOdaConnection vanillaOdaConnection, IQuery query) {
		this.connection = vanillaOdaConnection;
		this.odaQuery = query;
	}

	public String getDatasetId() {
		return datasetId;
	}

	public void setDatasetId(String datasetId) {
		this.datasetId = datasetId;
	}

	public Properties getDatasetPublicProperties() {
		return datasetPublicProperties;
	}

	public void setDatasetPublicProperties(Properties datasetPublicProperties) {
		this.datasetPublicProperties = datasetPublicProperties;
	}

	public Properties getDatasetPrivateProperties() {
		return datasetPrivateProperties;
	}

	public void setDatasetPrivateProperties(Properties datasetPrivateProperties) {
		this.datasetPrivateProperties = datasetPrivateProperties;
	}

	public void prepareQuery(String queryText) throws Exception {
		odaQuery.prepare(queryText);
	}
	
	public IResultSet executeQuery() throws Exception {
		return odaQuery.executeQuery();
	}
	
	public IResultSetMetaData getMetadata() throws Exception {
		return odaQuery.getMetaData();
	}
	
	public IParameterMetaData getParameterMetadata() throws Exception {
		return odaQuery.getParameterMetaData();
	}
}
