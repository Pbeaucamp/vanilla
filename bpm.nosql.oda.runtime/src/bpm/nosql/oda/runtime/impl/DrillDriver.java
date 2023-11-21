package bpm.nosql.oda.runtime.impl;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.LogConfiguration;
import org.eclipse.datatools.connectivity.oda.OdaException;

public class DrillDriver implements IDriver{
	static String ODA_DATA_SOURCE_ID = "bpm.nosql.oda.runtime.drill";
	
	@Override
	public IConnection getConnection(String arg0) throws OdaException {
		return new DrillConnection();
	}

	@Override
	public int getMaxConnections() throws OdaException {
		return 0;
	}

	@Override
	public void setAppContext(Object arg0) throws OdaException {
	}

	@Override
	public void setLogConfiguration(LogConfiguration arg0) throws OdaException {
	}

}
