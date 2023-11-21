package bpm.connection.manager.connection.oda;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.connection.manager.connection.VanillaConnection;

/**
 * A wrapper for an ODA connection
 * @author Marc
 *
 */
public class VanillaOdaConnection implements VanillaConnection {

	private IConnection odaConnection;

	private Properties datasourcePublicProperties = new Properties();
	private Properties datasourcePrivateProperties = new Properties();
	
	private String datasourceId;
	
	public VanillaOdaConnection(IConnection connection, Properties publicProperties, Properties privateProperties, String datasourceId) {
		this.odaConnection = connection;
		this.datasourcePublicProperties = publicProperties;
		this.datasourcePrivateProperties = privateProperties;
		this.datasourceId = datasourceId;
	}

	public Properties getDatasourcePublicProperties() {
		return datasourcePublicProperties;
	}

	public void setDatasourcePublicProperties(Properties datasourcePublicProperties) {
		this.datasourcePublicProperties = datasourcePublicProperties;
	}

	public Properties getDatasourcePrivateProperties() {
		return datasourcePrivateProperties;
	}

	public void setDatasourcePrivateProperties(Properties datasourcePrivateProperties) {
		this.datasourcePrivateProperties = datasourcePrivateProperties;
	}

	public String getDatasourceId() {
		return datasourceId;
	}

	public void setDatasourceId(String datasourceId) {
		this.datasourceId = datasourceId;
	}

	public IConnection getOdaConnection() {
		return odaConnection;
	}
	
	public VanillaOdaQuery newQuery(String odaExtensionId) throws Exception {
		IQuery query = odaConnection.newQuery(odaExtensionId);
		return new VanillaOdaQuery(this, query);
	}
}
