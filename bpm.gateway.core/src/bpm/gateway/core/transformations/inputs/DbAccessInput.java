package bpm.gateway.core.transformations.inputs;

import org.dom4j.Element;

import bpm.gateway.core.Server;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.studio.jdbc.management.model.ListDriver;

public class DbAccessInput extends DataBaseInputStream{

	@Override
	public void setServer(Server server) {
		if (server instanceof DataBaseServer && ((DataBaseConnection)((DataBaseServer)server).getCurrentConnection(null)).getDriverName().equals(ListDriver.MS_ACCESS)){
			super.setServer(server);
		}
	}
	
	
	@Override
	public Element getElement() {
		Element e = super.getElement();
		e.setName("dataBaseAccessInput");
		return e;
	}
}
