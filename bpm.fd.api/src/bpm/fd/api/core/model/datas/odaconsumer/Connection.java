package bpm.fd.api.core.model.datas.odaconsumer;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.OdaException;


public class Connection {
	private String dataSourceId;
	private IConnection odaConnection;
	
	public Connection(String dataSourceId, IConnection odaConnection) throws OdaException{
		if (dataSourceId == null || dataSourceId.equals("")){
			throw new OdaException("Cannot instantiate Connection without dataSourceId");
		}
		
		if (odaConnection == null || !odaConnection.isOpen()){
			throw new OdaException("Cannot instantiate Connection without IConnection nor a closed IConnection");
		}
		
		
		this.dataSourceId = dataSourceId;
		this.odaConnection = odaConnection;
	}
	
	public IConnection getOdaConnection(){
		return odaConnection;
	}
	
}
