package bpm.gateway.core.tsbn.db;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import bpm.gateway.core.tsbn.affaires.DwhAff;
import bpm.gateway.core.tsbn.affaires.DwhAffDAO;
import bpm.gateway.core.tsbn.appels.Agents;
import bpm.gateway.core.tsbn.appels.DwhApp;
import bpm.gateway.core.tsbn.appels.DwhAppDAO;
import bpm.gateway.core.tsbn.referentiels.CATEGORIELIEUX;
import bpm.gateway.core.tsbn.referentiels.DEPS;
import bpm.gateway.core.tsbn.referentiels.DwhRef;
import bpm.gateway.core.tsbn.referentiels.DwhRefDAO;
import bpm.gateway.core.tsbn.referentiels.REF;
import bpm.gateway.core.tsbn.referentiels.TYPELIEUX;
import bpm.gateway.core.tsbn.referentiels.VILLES;

public class MainTSBN {

	public static void main(String[] args) {
//		loadAppXML();
//		loadAffXML();
		loadRefXML();
	}

	private static void loadAppXML() {
		JAXBContext jaxbContext = null;
		try {
			jaxbContext = JAXBContext.newInstance(DwhApp.class);
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
			DwhApp dwhApp = (DwhApp) jaxbUnmarshaller.unmarshal(new File("C:/BPM/Dropbox/Test/TSBN_App_Aff/20141126-APPELS-61_20141209003353.xml"));
			
			TsbnAppDaoComponent tsbnDaoComp = new TsbnAppDaoComponent();
			tsbnDaoComp.init("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/app", "root", "root");
			DwhAppDAO dwhAppDao = tsbnDaoComp.getDwhAppDao();

//			saveDwhApp(dwhAppDao, dwhApp);
			saveAgents(dwhAppDao, dwhApp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadAffXML() {
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
		
		try {
			DwhAff dwhAff = (DwhAff) jaxbUnmarshaller.unmarshal(new File("C:/BPM/Dropbox/Documents/Clients/TSBN/XML RAMU/AFFAIRES/GCSDWH.xml"));
			
			TsbnAffDaoComponent tsbnDaoComp = new TsbnAffDaoComponent();
			tsbnDaoComp.init("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/test", "root", "root");
			DwhAffDAO dwhAffDao = tsbnDaoComp.getDwhAffDao();

			saveDwhAff(dwhAffDao, dwhAff);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadRefXML() {
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
		
		try {
			DwhRef dwhRef = (DwhRef) jaxbUnmarshaller.unmarshal(new File("C:/BPM/Clients/TSBN/XML RAMU/DernierXML/GCSDWH-REFERENTIEL-XX_20150421111602.xml"));
			
			TsbnRefDaoComponent tsbnDaoComp = new TsbnRefDaoComponent();
			tsbnDaoComp.init("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/test", "root", "root");
			DwhRefDAO dwhRefDao = tsbnDaoComp.getDwhRefDao();

			saveDwhRef(dwhRefDao, dwhRef);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void saveDwhAff(DwhAffDAO dwhAffDao, DwhAff dwhAff) {
		dwhAffDao.save(dwhAff);	
	}

	private static void saveAgents(DwhAppDAO dwhAppDao, DwhApp dwhApp) {
		for(Agents agt : dwhApp.getReferentiels().getAgents()) {
//			if(agt.getAgtStruct() != null && !agt.getAgtStruct().isEmpty()) {
				agt.setAgtStruct(agt.getAgtStruct());
				dwhAppDao.save(agt);
//				break;
//			}
		}
	}

	private static void saveDwhApp(DwhAppDAO dwhAppDao, DwhApp dwhApp) {
		dwhAppDao.save(dwhApp);
	}

	private static void saveDwhRef(DwhRefDAO dwhRefDao, DwhRef dwhRef) {
		dwhRefDao.save(dwhRef);
		
//		dwhRefDao.save(dwhRef.getRef());
//		
//		CATEGORIELIEUX categorielieux = dwhRef.getRef().getCategorielieux();
//		dwhRefDao.save(categorielieux);
//		
//		DEPS deps = dwhRef.getRef().getDeps();
//		dwhRefDao.save(deps);
//		
//		TYPELIEUX typelieux = dwhRef.getRef().getTypelieux();
//		dwhRefDao.save(typelieux);
//		
//		VILLES villes = dwhRef.getRef().getVilles();
//		dwhRefDao.save(villes);
	}

}
