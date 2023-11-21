package bpm.gateway.runtime2.transformation.tsbn;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.database.DataBaseConnection;
import bpm.gateway.core.server.database.DataBaseServer;
import bpm.gateway.core.server.database.jdbc.JdbcConnectionProvider;
import bpm.gateway.core.server.file.AbstractFileServer;
import bpm.gateway.core.tsbn.ConnectorReferentielXML;
import bpm.gateway.core.tsbn.db.TsbnConnectionManager;
import bpm.gateway.core.tsbn.db.TsbnRefDaoComponent;
import bpm.gateway.core.tsbn.referentiels.DwhRef;
import bpm.gateway.core.tsbn.referentiels.DwhRefDAO;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.studio.jdbc.management.model.DriverInfo;

public class RunConnectorReferentiel extends RuntimeStep {

	private TsbnRefDaoComponent tsbnDaoComp;
	private DwhRef dwhRef;

	public RunConnectorReferentiel(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(DwhRef.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
		
		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		ConnectorReferentielXML transfo = (ConnectorReferentielXML) getTransformation();

		InputStream fis = null;
		try {
			fis = ((AbstractFileServer) transfo.getFileServer()).getInpuStream(transfo);
		} catch (Exception e) {
			error("unable to open file ", e);
			throw e;
		}
		
		if(fis != null) {
			dwhRef = (DwhRef) jaxbUnmarshaller.unmarshal(fis);
			
			fis.close();
			
			DataBaseServer dbServer = transfo.getServer() != null ? (DataBaseServer) transfo.getServer() : null;
			if(dbServer == null) {
				throw new Exception("Please, define database server before running.");
			}
			
			DataBaseConnection c = (DataBaseConnection)dbServer.getCurrentConnection(adapter);
			DriverInfo driverInfo = JdbcConnectionProvider.getDriver(c.getDriverName());
			
			String fullUrl = "";
			if(c.isUseFullUrl()) {
				fullUrl = c.getFullUrl();
			}
			else {
				fullUrl = JdbcConnectionProvider.getFullUrl(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName());
			}
			
			if(fullUrl != null && !fullUrl.isEmpty()) {
				TsbnConnectionManager manager = TsbnConnectionManager.getInstance();
				tsbnDaoComp = manager.getTsbnRefDao(fullUrl, c.getLogin(), c.getPassword(), driverInfo.getClassName());
//				tsbnDaoComp = new TsbnAppDaoComponent();
//				tsbnDaoComp.init(driverInfo.getClassName(), fullUrl, c.getLogin(), c.getPassword());
			}
			else {
				throw new Exception("Unable to get Full URL");
			}
		}
	}

	@Override
	public void performRow() throws Exception {
		try {
			if(tsbnDaoComp != null) {
				DwhRefDAO dwhRefDao = tsbnDaoComp.getDwhRefDao();
				saveDwhRef(dwhRefDao, dwhRef);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setEnd();
			throw new Exception("Unable to load the file : " + e.getMessage());
		}
		
		setEnd();
	}
	
	private void saveDwhRef(DwhRefDAO dwhRefDao, DwhRef dwhRef) {
		dwhRefDao.save(dwhRef);
	}

	@Override
	public void releaseResources() {
		tsbnDaoComp = null;
		dwhRef = null;
	}

}
