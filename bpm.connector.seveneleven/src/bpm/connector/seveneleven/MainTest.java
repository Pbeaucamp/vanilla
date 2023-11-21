package bpm.connector.seveneleven;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import bpm.connector.seveneleven.db.ConnectorDAO;
import bpm.connector.seveneleven.db.ConnectorDaoComponent;
import bpm.connector.seveneleven.model.Sales;

public class MainTest {

	public static void main(String[] args) {
		loadSalesXML();
	}

	private static void loadSalesXML() {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(Sales.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
		
		Unmarshaller jaxbUnmarshaller = null;
		try {
			jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		try {
			Sales sales = (Sales) jaxbUnmarshaller.unmarshal(new File("E:/BPM/Test/XML/0012-83BHYR71-0012-83BHYR72"));
//			Sales sales = (Sales) jaxbUnmarshaller.unmarshal(new File("C:/BPM/Dropbox/Documents/Clients/7-11/20150214/0003/20150214/0003/0003-82E4JDQ8-0003-82E4JDQ9_GOOD"));
			
//			ConnectorDaoComponent connectorDaoComp = new ConnectorDaoComponent();
//			connectorDaoComp.init("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/seven_eleven", "root", "root");
//			ConnectorDAO connectorDao = connectorDaoComp.getConnectorDao();

//			connectorDao.save(sales);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
