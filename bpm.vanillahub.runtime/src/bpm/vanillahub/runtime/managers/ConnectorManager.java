package bpm.vanillahub.runtime.managers;

import java.util.ArrayList;
import java.util.List;

import bpm.connector.seveneleven.ConnectorDefinition;
import bpm.vanillahub.core.beans.resources.Connector;

public class ConnectorManager {

	public List<Connector> getResources() throws Exception {
		List<Connector> connectors = new ArrayList<Connector>();
		connectors.add(new Connector(ConnectorDefinition.CONNECTOR_NAME));
		return connectors;
	}
}
