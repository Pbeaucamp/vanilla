package bpm.metadata.birt.oda.runtime.impl;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.ibm.icu.util.ULocale;

public class UnitedOlapConnection extends AbstractFmdtConnection implements IConnection {

	@Override
	public void close() throws OdaException {
		m_isOpen = false;
	}

	@Override
	public void commit() throws OdaException {

	}



	@Override
	public boolean isOpen() throws OdaException {
		return m_isOpen;
	}

	@Override
	public IQuery newQuery(String arg0) throws OdaException {
		return new UnitedOlapQuery(this, fmdtPackage, vanillaGroup);
	}

	@Override
	public void open(Properties connProperties) throws OdaException {
		super.open(connProperties);
		
		m_isOpen = true;
	}

	@Override
	public void rollback() throws OdaException {
		
	}

	@Override
	public void setAppContext(Object arg0) throws OdaException {
		
	}

	@Override
	public void setLocale(ULocale arg0) throws OdaException {
		
	}

	@Override
	protected String getConnectionName() {
    	if(connectionName == null || connectionName.equals("null")){
    		Logger.getLogger(getClass()).warn("No Connection Name defined within OdaFmdt connection");
    		
    		if (fmdtPackage != null){
    			List<String> l = fmdtPackage.getConnectionsNames(groupName);
    			if (l.isEmpty()){
    				Logger.getLogger(getClass()).warn("No connection available for group " + groupName);
    			}
    			else{
    				return l.get(0);
    			}
    		}
    	}
		return super.getConnectionName();
	}
}
