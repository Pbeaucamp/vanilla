package bpm.gateway.core.veolia.abonnes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import bpm.gateway.core.veolia.DateChargement;
import bpm.gateway.core.veolia.IXmlManager;
import bpm.gateway.core.veolia.LogInsertXML;
import bpm.gateway.core.veolia.LogXML;
import bpm.gateway.core.veolia.ReflectionHelper;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class AbonnesDAO extends HibernateDaoSupport implements IXmlManager {

	public static final String TYPE_ABONNE = "abonnes_abonne";
	public static final String TYPE_BRANCHEMENT = "abonnes_branchement";
	public static final String TYPE_POINTFOURNITURE = "abonnes_pointfourniture";
	public static final String TYPE_CONTRATAEP = "abonnes_contrataep";
	public static final String TYPE_CONTRATAC = "abonnes_contratac";
	public static final String TYPE_CONTRATABT = "abonnes_contratabt";
	public static final String TYPE_FACTURE = "abonnes_facture";
	public static final String TYPE_LIGNEFACTURE = "abonnes_lignefacture";
	public static final String TYPE_COMPTEUR = "abonnes_compteur";
	public static final String TYPE_RELEVE = "abonnes_releve";
	public static final String TYPE_INTERVENTION = "abonnes_intervention";

	public static final String ROOT_ABONNE = "abonne";
	public static final String ROOT_BRANCHEMENT = "branchement";
	public static final String ROOT_POINTFOURNITURE = "pointFourniture";
	public static final String ROOT_CONTRATAEP = "contratAep";
	public static final String ROOT_CONTRATAC = "contratAc";
	public static final String ROOT_CONTRATABT = "contratAbt";
	public static final String ROOT_FACTURE = "facture";
	public static final String ROOT_LIGNEFACTURE = "ligneFacture";
	public static final String ROOT_COMPTEUR = "compteur";
	public static final String ROOT_RELEVE = "releve";
	public static final String ROOT_INTERVENTION = "intervention";

	public static final String ITEM_ABONNE = "abonnes";
	public static final String ITEM_BRANCHEMENT = "branchements";
	public static final String ITEM_POINTFOURNITURE = "pointsFourniture";
	public static final String ITEM_CONTRATAEP = "contratAeps";
	public static final String ITEM_CONTRATAC = "contratAcs";
	public static final String ITEM_CONTRATABT = "contratAbts";
	public static final String ITEM_FACTURE = "factures";
	public static final String ITEM_LIGNEFACTURE = "ligneFactures";
	public static final String ITEM_COMPTEUR = "compteurs";
	public static final String ITEM_RELEVE = "releves";
	public static final String ITEM_INTERVENTION = "interventions";

	public enum ClassAbonnee {
		TYPE_ABONNE(ROOT_ABONNE), TYPE_BRANCHEMENT(ROOT_BRANCHEMENT), TYPE_POINTFOURNITURE(ROOT_POINTFOURNITURE), TYPE_CONTRATAEP(ROOT_CONTRATAEP), TYPE_CONTRATAC(ROOT_CONTRATAC), TYPE_CONTRATABT(ROOT_CONTRATABT), TYPE_FACTURE(ROOT_FACTURE), TYPE_LIGNEFACTURE(ROOT_LIGNEFACTURE), TYPE_COMPTEUR(ROOT_COMPTEUR), TYPE_RELEVE(ROOT_RELEVE), TYPE_INTERVENTION(ROOT_INTERVENTION);

		private String root;

		private static Map<String, ClassAbonnee> map = new HashMap<String, ClassAbonnee>();
		static {
			for (ClassAbonnee classAb : ClassAbonnee.values()) {
				map.put(classAb.getRoot(), classAb);
			}
		}

		private ClassAbonnee(String root) {
			this.root = root;
		}

		public String getRoot() {
			return root;
		}

		public static ClassAbonnee valueOfRoot(String root) {
			return map.get(root);
		}
	}

	@Override
	public LogInsertXML createLog(ClassAbonnee classAb, String fileName, Integer idChg) {
		switch (classAb) {
		case TYPE_ABONNE:
			return new LogInsertXML(TYPE_ABONNE, fileName, idChg);
		case TYPE_BRANCHEMENT:
			return new LogInsertXML(TYPE_BRANCHEMENT, fileName, idChg);
		case TYPE_COMPTEUR:
			return new LogInsertXML(TYPE_COMPTEUR, fileName, idChg);
		case TYPE_CONTRATABT:
			return new LogInsertXML(TYPE_CONTRATABT, fileName, idChg);
		case TYPE_CONTRATAC:
			return new LogInsertXML(TYPE_CONTRATAC, fileName, idChg);
		case TYPE_CONTRATAEP:
			return new LogInsertXML(TYPE_CONTRATAEP, fileName, idChg);
		case TYPE_FACTURE:
			return new LogInsertXML(TYPE_FACTURE, fileName, idChg);
		case TYPE_INTERVENTION:
			return new LogInsertXML(TYPE_INTERVENTION, fileName, idChg);
		case TYPE_LIGNEFACTURE:
			return new LogInsertXML(TYPE_LIGNEFACTURE, fileName, idChg);
		case TYPE_POINTFOURNITURE:
			return new LogInsertXML(TYPE_POINTFOURNITURE, fileName, idChg);
		case TYPE_RELEVE:
			return new LogInsertXML(TYPE_RELEVE, fileName, idChg);
		}
		return null;
	}

	@Override
	public ClassDefinition getClassDefinition(ClassAbonnee classAb, ClassDefinition mainClass) {
		GRC grc = new GRC();
		switch (classAb) {
		case TYPE_ABONNE:
			return ReflectionHelper.getSelectedClassDefinition(grc.getAbonnes().getClass().getName(), mainClass);
		case TYPE_BRANCHEMENT:
			return ReflectionHelper.getSelectedClassDefinition(grc.getBranchements().getClass().getName(), mainClass);
		case TYPE_COMPTEUR:
			return ReflectionHelper.getSelectedClassDefinition(grc.getCompteurs().getClass().getName(), mainClass);
		case TYPE_CONTRATABT:
			return ReflectionHelper.getSelectedClassDefinition(grc.getContratAbts().getClass().getName(), mainClass);
		case TYPE_CONTRATAC:
			return ReflectionHelper.getSelectedClassDefinition(grc.getContratAcs().getClass().getName(), mainClass);
		case TYPE_CONTRATAEP:
			return ReflectionHelper.getSelectedClassDefinition(grc.getContratAeps().getClass().getName(), mainClass);
		case TYPE_FACTURE:
			return ReflectionHelper.getSelectedClassDefinition(grc.getFactures().getClass().getName(), mainClass);
		case TYPE_INTERVENTION:
			return ReflectionHelper.getSelectedClassDefinition(grc.getInterventions().getClass().getName(), mainClass);
		case TYPE_LIGNEFACTURE:
			return ReflectionHelper.getSelectedClassDefinition(grc.getLigneFactures().getClass().getName(), mainClass);
		case TYPE_POINTFOURNITURE:
			return ReflectionHelper.getSelectedClassDefinition(grc.getPointsFourniture().getClass().getName(), mainClass);
		case TYPE_RELEVE:
			return ReflectionHelper.getSelectedClassDefinition(grc.getReleves().getClass().getName(), mainClass);
		}
		return null;
	}

	@Override
	public void processItem(ClassAbonnee classAb, ClassDefinition classDef, Object item, Integer idChg, LogInsertXML logInsert, List<LogXML> logs) {
		switch (classAb) {
		case TYPE_ABONNE:
			TypeAbonne ab = (TypeAbonne) item;

			ab.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, ab.getAbonneVeId());

			break;
		case TYPE_BRANCHEMENT:
			TypeBranchement br = (TypeBranchement) item;

			br.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, br.getBranchementVeId());

			break;
		case TYPE_COMPTEUR:
			TypeCompteur cpt = (TypeCompteur) item;

			cpt.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, cpt.getCompteurVeId());

			break;
		case TYPE_CONTRATABT:
			TypeContratAbt abt = (TypeContratAbt) item;

			abt.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, abt.getContratabtVeId());

			break;
		case TYPE_CONTRATAC:
			TypeContratAc ac = (TypeContratAc) item;

			ac.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, ac.getContratacVeId());

			break;
		case TYPE_CONTRATAEP:
			TypeContratAep aep = (TypeContratAep) item;

			aep.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, aep.getContrataepVeId());

			break;
		case TYPE_FACTURE:
			TypeFacture fac = (TypeFacture) item;

			fac.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, fac.getFactureVeId());

			break;
		case TYPE_INTERVENTION:
			TypeIntervention inter = (TypeIntervention) item;

			inter.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, inter.getInterventionVeId());

			break;
		case TYPE_LIGNEFACTURE:
			TypeLigneFacture ligne = (TypeLigneFacture) item;

			ligne.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, ligne.getLignefactureVeId());

			break;
		case TYPE_POINTFOURNITURE:
			TypePointFourniture ptFourn = (TypePointFourniture) item;

			ptFourn.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, ptFourn.getPointfournitureVeId());

			break;
		case TYPE_RELEVE:
			TypeReleve releve = (TypeReleve) item;

			releve.setIdChg(idChg);
			saveObject(item, classDef, logInsert, logs, releve.getReleveVeId());

			break;
		}
	}

	@Override
	public boolean insertLog(String fileName, Integer idChg, LogInsertXML log, List<LogXML> logs) {
		getHibernateTemplate().save(log);

		for (int i = 0; i < logs.size(); i++) {
			getHibernateTemplate().saveBatch(logs.get(i), i == (logs.size() - 1));
		}

		if (log.getNbLigneError() > 0) {
			logs.add(new LogXML("ALL", fileName, "", log.getNbLigneError() + " erreurs dans '" + TYPE_ABONNE + "', on stop le processus.", idChg));
			return false;
		}

		return true;
	}

	public void save(InputSource source, DateChargement dateChargement, ClassDefinition mainClass, String fileName) throws SAXException, ParserConfigurationException, IOException {
		Integer idChg = (Integer) getHibernateTemplate().save(dateChargement);

		// create a new XML parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XMLReader reader = factory.newSAXParser().getXMLReader();

		// prepare a Splitter
		AbonnesSplitter splitter = new AbonnesSplitter(this, mainClass, fileName, idChg);
		reader.setContentHandler(splitter);

		reader.parse(source);

		// List<LogXML> logs = new ArrayList<>();
		// Try to test if the XML is malformed or any weird error
		// try {
		// LogInsertXML logInsert = new LogInsertXML(TYPE_ABONNE, fileName,
		// idChg);
		// if (grc.getAbonnes() != null) {
		// if (grc.getAbonnes().getAbonne() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getAbonnes().getClass().getName(),
		// mainClass);
		//
		// for (TypeAbonne item : grc.getAbonnes().getAbonne()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs, item.getAbonneVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		// if (logInsert.getNbLigneError() > 0) {
		// logs.add(new LogXML("ALL", fileName, "", logInsert.getNbLigneError()
		// + " erreurs dans '" + TYPE_ABONNE + "', on stop le processus.",
		// idChg));
		// finish(logs);
		// return;
		// }
		//
		// logInsert = new LogInsertXML(TYPE_BRANCHEMENT, fileName, idChg);
		// if (grc.getBranchements() != null) {
		// if (grc.getBranchements().getBranchement() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getBranchements().getClass().getName(),
		// mainClass);
		//
		// for (TypeBranchement item : grc.getBranchements().getBranchement()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs,
		// item.getBranchementVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		// if (logInsert.getNbLigneError() > 0) {
		// logs.add(new LogXML("ALL", fileName, "", logInsert.getNbLigneError()
		// + " erreurs dans '" + TYPE_BRANCHEMENT + "', on stop le processus.",
		// idChg));
		// finish(logs);
		// return;
		// }
		//
		// logInsert = new LogInsertXML(TYPE_POINTFOURNITURE, fileName, idChg);
		// if (grc.getPointsFourniture() != null) {
		// if (grc.getPointsFourniture().getPointFourniture() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getPointsFourniture().getClass().getName(),
		// mainClass);
		//
		// for (TypePointFourniture item :
		// grc.getPointsFourniture().getPointFourniture()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs,
		// item.getPointfournitureVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		//
		// logInsert = new LogInsertXML(TYPE_CONTRATAEP, fileName, idChg);
		// if (grc.getContratAeps() != null) {
		// if (grc.getContratAeps().getContratAep() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getContratAeps().getClass().getName(),
		// mainClass);
		//
		// for (TypeContratAep item : grc.getContratAeps().getContratAep()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs,
		// item.getContrataepVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		//
		// logInsert = new LogInsertXML(TYPE_CONTRATAC, fileName, idChg);
		// if (grc.getContratAcs() != null) {
		// if (grc.getContratAcs().getContratAc() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getContratAcs().getClass().getName(),
		// mainClass);
		//
		// for (TypeContratAc item : grc.getContratAcs().getContratAc()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs, item.getContratacVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		//
		// logInsert = new LogInsertXML(TYPE_CONTRATABT, fileName, idChg);
		// if (grc.getContratAbts() != null) {
		// if (grc.getContratAbts().getContratAbt() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getContratAbts().getClass().getName(),
		// mainClass);
		//
		// for (TypeContratAbt item : grc.getContratAbts().getContratAbt()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs,
		// item.getContratabtVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		// if (logInsert.getNbLigneError() > 0) {
		// logs.add(new LogXML("ALL", fileName, "", logInsert.getNbLigneError()
		// + " erreurs dans '" + TYPE_CONTRATABT + "', on stop le processus.",
		// idChg));
		// finish(logs);
		// return;
		// }
		//
		// logInsert = new LogInsertXML(TYPE_FACTURE, fileName, idChg);
		// if (grc.getFactures() != null) {
		// if (grc.getFactures().getFacture() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getFactures().getClass().getName(),
		// mainClass);
		//
		// for (TypeFacture item : grc.getFactures().getFacture()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs, item.getFactureVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		// if (logInsert.getNbLigneError() > 0) {
		// logs.add(new LogXML("ALL", fileName, "", logInsert.getNbLigneError()
		// + " erreurs dans '" + TYPE_FACTURE + "', on stop le processus.",
		// idChg));
		// finish(logs);
		// return;
		// }
		//
		// logInsert = new LogInsertXML(TYPE_LIGNEFACTURE, fileName, idChg);
		// if (grc.getLigneFactures() != null) {
		// if (grc.getLigneFactures().getLigneFacture() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getLigneFactures().getClass().getName(),
		// mainClass);
		//
		// for (TypeLigneFacture item :
		// grc.getLigneFactures().getLigneFacture()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs,
		// item.getLignefactureVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		//
		// logInsert = new LogInsertXML(TYPE_COMPTEUR, fileName, idChg);
		// if (grc.getCompteurs() != null) {
		// if (grc.getCompteurs().getCompteur() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getCompteurs().getClass().getName(),
		// mainClass);
		//
		// for (TypeCompteur item : grc.getCompteurs().getCompteur()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs, item.getCompteurVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		//
		// logInsert = new LogInsertXML(TYPE_RELEVE, fileName, idChg);
		// if (grc.getReleves() != null) {
		// if (grc.getReleves().getReleve() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getReleves().getClass().getName(),
		// mainClass);
		//
		// for (TypeReleve item : grc.getReleves().getReleve()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs, item.getReleveVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		//
		// logInsert = new LogInsertXML(TYPE_INTERVENTION, fileName, idChg);
		// if (grc.getInterventions() != null) {
		// if (grc.getInterventions().getIntervention() != null) {
		// ClassDefinition classDef =
		// ReflectionHelper.getSelectedClassDefinition(grc.getInterventions().getClass().getName(),
		// mainClass);
		//
		// for (TypeIntervention item :
		// grc.getInterventions().getIntervention()) {
		// item.setIdChg(idChg);
		// saveObject(item, classDef, logInsert, logs,
		// item.getInterventionVeId());
		// }
		// }
		// }
		// getHibernateTemplate().save(logInsert);
		// } catch (Exception e) {
		// e.printStackTrace();
		//
		// logs.add(new LogXML("ALL", fileName, "",
		// "XML non conforme, il est impossible de l'intégrer", idChg));
		// }
		//
		// finish(logs);
	}

	private void saveObject(Object item, ClassDefinition classDefinition, LogInsertXML logInsert, List<LogXML> logs, String lineId) {
		logInsert.incrementIn();
		try {
			ReflectionHelper.testRules(classDefinition, item);

			getHibernateTemplate().save(item);
			logInsert.incrementOut();
		} catch (Exception e) {
			manageException(logs, logInsert.getTableName(), logInsert.getContractName(), lineId, logInsert.getIdChg(), e);
			logInsert.incrementError();
		}
	}

	private void manageException(List<LogXML> logs, String tableName, String fileName, String lineId, Integer idChg, Exception e) {
		String message = "";
		if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof IllegalArgumentException) {
			IllegalArgumentException ex = (IllegalArgumentException) e.getCause().getCause();
			e = ex;

			message = e.getMessage();
		}
		else if (e.getCause() != null) {
			message = e.getCause().getMessage();
		}
		else {
			message = e.getMessage();
		}

		e.printStackTrace();
		logs.add(new LogXML(tableName, fileName, lineId, message, idChg));
	}

	public void buildXMLFromDB(PrintWriter writer, boolean isVE, String secteur, String exploitant, int maxLimit) {
		String rootElement = "GRC";
		
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		try {
			XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(writer);
			// For Debugging - below code to print XML to Console
//			XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(System.out);
			
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			XMLEvent jumpLine = eventFactory.createDTD("\n");
			
			StartDocument startDocument = eventFactory.createStartDocument();
			xmlEventWriter.add(startDocument);
			xmlEventWriter.add(jumpLine);
			
			StartElement configStartElement = eventFactory.createStartElement("", "", rootElement);
			xmlEventWriter.add(configStartElement);
			xmlEventWriter.add(jumpLine);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(GRC.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			
			buildContent(xmlEventWriter, jaxbMarshaller, eventFactory, jumpLine, maxLimit, isVE, secteur, exploitant);
	
			xmlEventWriter.add(eventFactory.createEndElement("", "", rootElement));
			xmlEventWriter.add(jumpLine);
			xmlEventWriter.add(eventFactory.createEndDocument());
			xmlEventWriter.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (PropertyException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void buildContent(XMLEventWriter xmlEventWriter, Marshaller jaxbMarshaller, XMLEventFactory eventFactory, XMLEvent jumpLine, int maxLimit, boolean isVE, String secteur, String exploitant) throws XMLStreamException, JAXBException {
		boolean isQuery = secteur != null && !secteur.isEmpty();
		HashMap<String, QueryWithClass> queries = buildQueries(isVE, secteur, exploitant);
		
		Session session = getHibernateTemplate().getCurrentSession();
		for (String itemElement : queries.keySet()) {
			StartElement itemStartElement = eventFactory.createStartElement("", "", itemElement);
			xmlEventWriter.add(itemStartElement);
			xmlEventWriter.add(jumpLine);
			
			QueryWithClass query = queries.get(itemElement);
			
			int firstResult = 0;
			int maxResult = 500;
			int nbItems = 0;
			
			List<Object> items = new ArrayList<>();
			if (isQuery) {
				SQLQuery sqlQuery = session.createSQLQuery(query.getQuery());
				sqlQuery.setFirstResult(firstResult);
				sqlQuery.setMaxResults(maxResult);
				sqlQuery.addEntity(query.getQueryClazz());
				
				items = sqlQuery.list();
			}
			else {
				items = getHibernateTemplate().find(query.getQuery(), firstResult, maxResult);
			}
			while(!items.isEmpty() && (maxLimit == -1 || nbItems < maxLimit)) {
				for (Object item : items) {
					jaxbMarshaller.marshal(item, xmlEventWriter);
				}
				
				firstResult += maxResult;
				nbItems += items.size();
				if (isQuery) {
					SQLQuery sqlQuery = session.createSQLQuery(query.getQuery());
					sqlQuery.setFirstResult(firstResult);
					sqlQuery.setMaxResults(maxResult);
					sqlQuery.addEntity(query.getQueryClazz());
					
					items = sqlQuery.list();
				}
				else {
					items = getHibernateTemplate().find(query.getQuery(), firstResult, maxResult);
				}
			}

			xmlEventWriter.add(eventFactory.createEndElement("", "", itemElement));
			xmlEventWriter.add(jumpLine);
			
			System.out.println("Found " + nbItems + " of " + itemElement);
		}
		
		//We close the session
		session.flush();
		session.clear();
		session.close();
	}

	public HashMap<String, QueryWithClass> buildQueries(boolean isVE, String query, String query2) {
		if (query != null && !query.isEmpty()) {
			return buildQueriesWithParam(isVE, query, query2);
		}
		else {
			return buildQueries();
		}
	}
	
	private HashMap<String, QueryWithClass> buildQueries() {
		HashMap<String, QueryWithClass> queries = new LinkedHashMap<>();
		queries.put(ITEM_ABONNE, new QueryWithClass(TypeAbonne.class, "FROM TypeAbonne ORDER BY id"));
		queries.put(ITEM_BRANCHEMENT, new QueryWithClass(TypeBranchement.class, "FROM TypeBranchement ORDER BY id"));
		queries.put(ITEM_POINTFOURNITURE, new QueryWithClass(TypePointFourniture.class, "FROM TypePointFourniture ORDER BY id"));
		queries.put(ITEM_CONTRATAEP, new QueryWithClass(TypeContratAep.class, "FROM TypeContratAep ORDER BY id"));
		queries.put(ITEM_CONTRATAC, new QueryWithClass(TypeContratAc.class, "FROM TypeContratAc ORDER BY id"));
		queries.put(ITEM_CONTRATABT, new QueryWithClass(TypeContratAbt.class, "FROM TypeContratAbt ORDER BY id"));
		queries.put(ITEM_FACTURE, new QueryWithClass(TypeFacture.class, "FROM TypeFacture ORDER BY id"));
		queries.put(ITEM_LIGNEFACTURE, new QueryWithClass(TypeLigneFacture.class, "FROM TypeLigneFacture ORDER BY id"));
		queries.put(ITEM_COMPTEUR, new QueryWithClass(TypeCompteur.class, "FROM TypeCompteur ORDER BY id"));
		queries.put(ITEM_RELEVE, new QueryWithClass(TypeReleve.class, "FROM TypeReleve ORDER BY id"));
		queries.put(ITEM_INTERVENTION, new QueryWithClass(TypeIntervention.class, "FROM TypeIntervention ORDER BY id"));
		return queries;
	}

	private HashMap<String, QueryWithClass> buildQueriesWithParam(boolean isODS, String secteur, String exploitant) {
		HashMap<String, QueryWithClass> queries = new LinkedHashMap<>();

		// -----------------------------------------------------//
		// abonnes_abonne
		// -----------------------------------------------------//

		StringBuffer buf = new StringBuffer();
		buf.append("SELECT a.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_abonne a ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_abonne_id = a.abonne_ve_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_abonne_id_dwh = a.abonne_id ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND a.abonne_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.abonne_id  ");
			buf.append("	FROM abonnes_abonne  t1 ");
			buf.append("	WHERE (t1.abonne_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("		SELECT t.abonne_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_abonne  t ");
			buf.append("		GROUP BY t.abonne_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeAbonne> abonnes = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeAbonne.class).list();
//		System.out.println("Insert " + abonnes.size() + " lines of abonnes_abonne");
//		grc.setAbonnes(new Abonnes(abonnes));
		queries.put(ITEM_ABONNE, new QueryWithClass(TypeAbonne.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_branchement
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT b.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_pointfourniture pf, abonnes_branchement b ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_pointfourniture_id = pf.pointfourniture_ve_id ");
			buf.append("AND b.branchement_ve_id = pf.pointfourniture_branchement_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_pointfourniture_id_dwh = pf.pointfourniture_id ");
			buf.append("AND b.branchement_id = pf.pointfourniture_branchement_id_dwh ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND b.branchement_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.branchement_id  ");
			buf.append("	FROM abonnes_branchement t1 ");
			buf.append("	WHERE (t1.branchement_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("		SELECT t.branchement_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_branchement t ");
			buf.append("		GROUP BY t.branchement_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeBranchement> branchements = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeBranchement.class).list();
//		System.out.println("Insert " + branchements.size() + " lines of abonnes_branchement");
//		grc.setBranchements(new Branchements(branchements));
		queries.put(ITEM_BRANCHEMENT, new QueryWithClass(TypeBranchement.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_pointfourniture
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT pf.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_pointfourniture pf ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_pointfourniture_id = pf.pointfourniture_ve_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_pointfourniture_id_dwh = pf.pointfourniture_id ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND pf.pointfourniture_id IN  ");
			buf.append("	( ");
			buf.append("		SELECT t1.pointfourniture_id  ");
			buf.append("		FROM abonnes_pointfourniture t1 ");
			buf.append("		WHERE (t1.pointfourniture_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("			SELECT t.pointfourniture_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("			FROM abonnes_pointfourniture t ");
			buf.append("			GROUP BY t.pointfourniture_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypePointFourniture> pointFournitures = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypePointFourniture.class).list();
//		System.out.println("Insert " + pointFournitures.size() + " lines of abonnes_pointfourniture");
//		grc.setPointsFourniture(new PointsFourniture(pointFournitures));
		queries.put(ITEM_POINTFOURNITURE, new QueryWithClass(TypePointFourniture.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_contrataep
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT aep.* ");
		buf.append(" FROM abonnes_contrataep aep ");
		buf.append("WHERE aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND aep.contrataep_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.contrataep_id  ");
			buf.append("	FROM abonnes_contrataep t1 ");
			buf.append("	WHERE (t1.contrataep_ve_id, t1.id_chg) IN  ");
			buf.append("		( ");
			buf.append("		SELECT t.contrataep_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_contrataep t ");
			buf.append("		GROUP BY t.contrataep_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeContratAep> contratAeps = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeContratAep.class).list();
//		System.out.println("Insert " + contratAeps.size() + " lines of abonnes_contrataep");
//		grc.setContratAeps(new ContratAeps(contratAeps));
		queries.put(ITEM_CONTRATAEP, new QueryWithClass(TypeContratAep.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_contratac
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT ac.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_contratac ac  ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_contratac_id = ac.contratac_ve_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_contratac_id_dwh = ac.contratac_id ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND ac.contratac_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.contratac_id  ");
			buf.append("	FROM abonnes_contratac t1 ");
			buf.append("	WHERE (t1.contratac_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("		SELECT t.contratac_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_contratac  t ");
			buf.append("		GROUP BY t.contratac_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeContratAc> contratAcs = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeContratAc.class).list();
//		System.out.println("Insert " + contratAcs.size() + " lines of abonnes_contratac");
//		grc.setContratAcs(new ContratAcs(contratAcs));
		queries.put(ITEM_CONTRATAC, new QueryWithClass(TypeContratAc.class, buf.toString()));
		
		// -----------------------------------------------------//
		// abonnes_contratabt
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT abt.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt  ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND abt.contratabt_id IN  ");
			buf.append("	( ");
			buf.append("		SELECT t1.contratabt_id  ");
			buf.append("		FROM abonnes_contratabt t1 ");
			buf.append("		WHERE (t1.contratabt_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("		SELECT t.contratabt_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_contratabt t ");
			buf.append("		GROUP BY t.contratabt_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeContratAbt> contratAbts = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeContratAbt.class).list();
//		System.out.println("Insert " + contratAbts.size() + " lines of abonnes_contratabt");
		queries.put(ITEM_CONTRATABT, new QueryWithClass(TypeContratAbt.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_facture
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT f.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_facture f ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_ve_id = f.facture_contratabt_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_id = f.facture_contratabt_id_dwh ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND f.facture_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.facture_id  ");
			buf.append("	FROM abonnes_facture t1 ");
			buf.append("	WHERE (t1.facture_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("		SELECT t.facture_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_facture t ");
			buf.append("		GROUP BY t.facture_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeFacture> factures = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeFacture.class).list();
//		System.out.println("Insert " + factures.size() + " lines of abonnes_facture");
//		grc.setFactures(new Factures(factures));
		queries.put(ITEM_FACTURE, new QueryWithClass(TypeFacture.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_lignefacture
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT lf.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_facture f, abonnes_lignefacture lf ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_ve_id = f.facture_contratabt_id ");
			buf.append("AND lf.lignefacture_facture_id = f.facture_ve_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_id = f.facture_contratabt_id_dwh ");
			buf.append("AND lf.lignefacture_facture_id_dwh = f.facture_id ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND lf.lignefacture_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.lignefacture_id  ");
			buf.append("	FROM abonnes_lignefacture t1 ");
			buf.append("	WHERE (t1.lignefacture_ve_id, t1.id_chg) IN  ");
			buf.append("		( ");
			buf.append("		SELECT t.lignefacture_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_lignefacture t ");
			buf.append("		GROUP BY t.lignefacture_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeLigneFacture> ligneFactures = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeLigneFacture.class).list();
//		System.out.println("Insert " + ligneFactures.size() + " lines of abonnes_lignefacture");
//		grc.setLigneFactures(new LigneFactures(ligneFactures));
		queries.put(ITEM_LIGNEFACTURE, new QueryWithClass(TypeLigneFacture.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_compteur
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT c.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_pointfourniture pf, abonnes_compteur c ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_pointfourniture_id = pf.pointfourniture_ve_id ");
			buf.append("AND c.compteur_pointfourniture_id = pf.pointfourniture_ve_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_pointfourniture_id_dwh = pf.pointfourniture_id ");
			buf.append("AND c.compteur_pointfourniture_id_dwh = pf.pointfourniture_id ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND c.compteur_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.compteur_id  ");
			buf.append("	FROM abonnes_compteur t1 ");
			buf.append("	WHERE (t1.compteur_ve_id, t1.id_chg) IN ");
			buf.append("	( ");
			buf.append("		SELECT t.compteur_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_compteur t ");
			buf.append("		GROUP BY t.compteur_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeCompteur> compteurs = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeCompteur.class).list();
//		System.out.println("Insert " + compteurs.size() + " lines of abonnes_compteur");
//		grc.setCompteurs(new Compteurs(compteurs));
		queries.put(ITEM_COMPTEUR, new QueryWithClass(TypeCompteur.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_releve
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT r.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_pointfourniture pf, abonnes_compteur c, abonnes_releve r ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND abt.contratabt_pointfourniture_id = pf.pointfourniture_ve_id ");
			buf.append("AND c.compteur_pointfourniture_id = pf.pointfourniture_ve_id ");
			buf.append("AND r.releve_compteur_id = c.compteur_ve_id ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND abt.contratabt_pointfourniture_id_dwh = pf.pointfourniture_id ");
			buf.append("AND c.compteur_pointfourniture_id_dwh = pf.pointfourniture_id ");
			buf.append("AND r.releve_compteur_id_dwh = c.compteur_id ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND r.releve_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.releve_id  ");
			buf.append("	FROM abonnes_releve t1 ");
			buf.append("	WHERE (t1.releve_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("		SELECT t.releve_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_releve t ");
			buf.append("		GROUP BY t.releve_ve_id ");
			buf.append("		) ");
			buf.append("	)  ");
		}

//		List<TypeReleve> releves = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeReleve.class).list();
//		System.out.println("Insert " + releves.size() + " lines of abonnes_releve");
//		grc.setReleves(new Releves(releves));
		queries.put(ITEM_RELEVE, new QueryWithClass(TypeReleve.class, buf.toString()));

		// -----------------------------------------------------//
		// abonnes_intervention
		// -----------------------------------------------------//

		buf = new StringBuffer();
		buf.append("SELECT i.* ");
		buf.append(" FROM abonnes_contrataep aep, abonnes_contratabt abt, abonnes_intervention i  ");
		if (isODS) {
			buf.append("WHERE aep.contrataep_ve_id = abt.contratabt_contrataep_id ");
			buf.append("AND i.intervention_contratabt_id = abt.contratabt_ve_id  ");
		}
		else {
			buf.append("WHERE aep.contrataep_id = abt.contratabt_contrataep_id_dwh ");
			buf.append("AND i.intervention_contratabt_id_dwh = abt.contratabt_id  ");
		}
		buf.append("AND aep.contrataep_secteur = '" + secteur + "' ");
		buf.append("AND aep.contrataep_exploitant  = '" + exploitant + "' ");
		if (!isODS) {
			buf.append("AND i.intervention_id IN  ");
			buf.append("	( ");
			buf.append("	SELECT t1.intervention_id  ");
			buf.append("	FROM abonnes_intervention t1 ");
			buf.append("	WHERE (t1.intervention_ve_id, t1.id_chg) IN ");
			buf.append("		( ");
			buf.append("		SELECT t.intervention_ve_id, MAX(t.id_chg) as id_chg  ");
			buf.append("		FROM abonnes_intervention t ");
			buf.append("		GROUP BY t.intervention_ve_id ");
			buf.append("		) ");
			buf.append("	) ");
		}

//		List<TypeIntervention> interventions = getHibernateTemplate().getCurrentSession().createSQLQuery(buf.toString()).addEntity(TypeIntervention.class).list();
//		System.out.println("Insert " + interventions.size() + " lines of abonnes_intervention");
//		grc.setInterventions(new Interventions(interventions));
		queries.put(ITEM_INTERVENTION, new QueryWithClass(TypeIntervention.class, buf.toString()));

		return queries;
	}
	
	private class QueryWithClass {
		
		private Class<?> queryClazz;
		private String query;
		
		public QueryWithClass(Class<?> queryClazz, String query) {
			this.queryClazz = queryClazz;
			this.query = query;
		}
		
		public Class<?> getQueryClazz() {
			return queryClazz;
		}
		
		public String getQuery() {
			return query;
		}
	}
}
