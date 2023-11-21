package bpm.connector.seveneleven;

import java.io.ByteArrayInputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import bpm.connector.seveneleven.db.ConnectorDAO;
import bpm.connector.seveneleven.db.ConnectorDaoComponent;
import bpm.connector.seveneleven.model.Sales;
import bpm.vanilla.platform.hibernate.BatchResult;
import bpm.vanillahub.core.beans.managers.ConnectorResult;
import bpm.vanillahub.core.beans.managers.ConnectorResult.Result;
import bpm.vanillahub.core.beans.managers.TransformManager;

public class RunManager implements TransformManager {

	private ConnectorDAO connectorDao;
	private Unmarshaller jaxbUnmarshaller;

	public RunManager(String driver, String url, String login, String password) throws Exception {
		try {
			this.connectorDao = ConnectorDaoComponent.getConnectorDao(driver, url, login, password);

			JAXBContext jaxbContext = JAXBContext.newInstance(Sales.class);
			this.jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	public ConnectorResult insertFile(String fileName, String parentFolderName, ByteArrayInputStream parentStream, boolean loopEnd) throws Exception {
		Sales sales = null;
		try {
			sales = (Sales) jaxbUnmarshaller.unmarshal(parentStream);
		} catch (Exception e) {
			// e.printStackTrace();
			return new ConnectorResult(fileName, Result.IGNORED, null);
		}
		if (sales != null) {
			sales.setFileName(fileName);
			sales.setParentFolderName(parentFolderName);
			try {
				sales.setStoreId(sales.getSalesTransactionId().substring(0, 4));
			} catch (Exception e) {
			}
			sales.setCreationDate(new Date());

			BatchResult result = connectorDao.saveBatch(sales, loopEnd);
			if (result.getResult() == bpm.vanilla.platform.hibernate.BatchResult.Result.ERROR) {
				return new ConnectorResult(Result.ERROR, result.getError(), result.getNumberOfFileTraited());
			}
			else if (result.getResult() == bpm.vanilla.platform.hibernate.BatchResult.Result.WAITING) {
				return new ConnectorResult(Result.WAITING, null, result.getNumberOfFileTraited());
			}
			else {
				return new ConnectorResult(Result.SUCCESS, null, result.getNumberOfFileTraited());
			}
		}
		return new ConnectorResult(fileName, Result.IGNORED, null);
	}

	public void clearConnections() {
//		if (connectorDao != null) {
//			connectorDao.clearConnections();
//		}
//
//		connectorDaoComp.clearConnections();
//		
//		connectorDao = null;
		jaxbUnmarshaller = null;
	}

	@Override
	public ByteArrayInputStream buildFile(ByteArrayInputStream parentStream) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
