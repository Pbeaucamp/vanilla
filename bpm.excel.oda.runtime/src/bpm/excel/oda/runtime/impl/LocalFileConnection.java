package bpm.excel.oda.runtime.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.OdaException;

public class LocalFileConnection extends Connection{

	
	public void open(Properties connProperties) throws OdaException {		
       String fileName = connProperties.getProperty(FILE_NAME);
       setTemporaryFileName(fileName);
		
       m_isOpen = true; 
	           
 	}

}
