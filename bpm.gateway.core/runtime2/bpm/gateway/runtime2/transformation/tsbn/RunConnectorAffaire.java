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
import bpm.gateway.core.tsbn.ConnectorAffaireXML;
import bpm.gateway.core.tsbn.affaires.DwhAff;
import bpm.gateway.core.tsbn.affaires.DwhAffDAO;
import bpm.gateway.core.tsbn.db.TsbnAffDaoComponent;
import bpm.gateway.core.tsbn.db.TsbnConnectionManager;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.studio.jdbc.management.model.DriverInfo;

public class RunConnectorAffaire extends RuntimeStep {

	private TsbnAffDaoComponent tsbnDaoComp;
	private DwhAff dwhAff;
	private String idSamu;

	public RunConnectorAffaire(Transformation transformation, int bufferSize) {
		super(null, transformation, bufferSize);
	}

	@Override
	public void init(Object adapter) throws Exception {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(DwhAff.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		ConnectorAffaireXML transfo = (ConnectorAffaireXML) getTransformation();

		InputStream fis = null;
		try {
			fis = ((AbstractFileServer) transfo.getFileServer()).getInpuStream(transfo);
		} catch (Exception e) {
			error("unable to open file ", e);
			throw e;
		}

		if (fis != null) {
			dwhAff = (DwhAff) jaxbUnmarshaller.unmarshal(fis);
			this.idSamu = extractIdSamu(transfo);

			fis.close();

			DataBaseServer dbServer = transfo.getServer() != null ? (DataBaseServer) transfo.getServer() : null;
			if (dbServer == null) {
				throw new Exception("Please, define database server before running.");
			}

			DataBaseConnection c = (DataBaseConnection) dbServer.getCurrentConnection(adapter);
			DriverInfo driverInfo = JdbcConnectionProvider.getDriver(c.getDriverName());

			String fullUrl = "";
			if (c.isUseFullUrl()) {
				fullUrl = c.getFullUrl();
			}
			else {
				fullUrl = JdbcConnectionProvider.getFullUrl(c.getDriverName(), c.getHost(), c.getPort(), c.getDataBaseName());
			}

			if (fullUrl != null && !fullUrl.isEmpty()) {
				TsbnConnectionManager manager = TsbnConnectionManager.getInstance();
				tsbnDaoComp = manager.getTsbnAffDao(fullUrl, c.getLogin(), c.getPassword(), driverInfo.getClassName());
//				tsbnDaoComp = new TsbnAffDaoComponent();
//				tsbnDaoComp.init(driverInfo.getClassName(), fullUrl, c.getLogin(), c.getPassword());
			}
			else {
				throw new Exception("Unable to get Full URL");
			}
		}
	}

	private String extractIdSamu(ConnectorAffaireXML transfo) throws Exception {
		String fileName = ((AbstractFileServer) transfo.getFileServer()).getFileName(transfo);
		if (fileName.contains(".xml")) {
			int index = fileName.indexOf(".xml");
			return fileName.substring(index - 2, index);
		}
		throw new Exception("Filename is malformed. It should be something like XXXXXID.xml");
	}

	@Override
	public void performRow() throws Exception {
		try {
			if (tsbnDaoComp != null) {
				DwhAffDAO dwhAppDao = tsbnDaoComp.getDwhAffDao();
				saveDwhAff(dwhAppDao, dwhAff, idSamu);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setEnd();
			throw new Exception("Unable to load the file : " + e.getMessage());
		}

		setEnd();
	}

	private void saveDwhAff(DwhAffDAO dwhAffDao, DwhAff dwhAff, String idSamu) {
		dwhAffDao.save(dwhAff, idSamu);
	}

	@Override
	public void releaseResources() {
		tsbnDaoComp = null;
		dwhAff = null;
	}

}
