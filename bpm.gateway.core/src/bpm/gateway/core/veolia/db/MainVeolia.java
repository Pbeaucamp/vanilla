package bpm.gateway.core.veolia.db;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import bpm.gateway.core.veolia.abonnes.AbonnesDAO;
import bpm.gateway.core.veolia.abonnes.GRC;

public class MainVeolia {

	public static void main(String[] args) {
		loadAbonneXML();
	}

	private static void loadAbonneXML() {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(GRC.class);
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
			GRC grc = (GRC) jaxbUnmarshaller.unmarshal(new File("C:/BPM/Clients/VendeeEau/XML_XSD/DernierXML/xml_test_abonnes_v1.1.xml"));
			
			VeoliaAbonnesDaoComponent veoliaDaoComp = new VeoliaAbonnesDaoComponent();
			veoliaDaoComp.init("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/abonne", "root", "root");
			AbonnesDAO abonneDao = veoliaDaoComp.getAbonnesDao();

			save(abonneDao, grc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void save(AbonnesDAO abonneDao, GRC grc) {
		abonneDao.save(grc);	
	}
}
