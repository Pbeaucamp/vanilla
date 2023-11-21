package bpm.vanilla.portal.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.helper.DocumentHelper;
import bpm.gwt.commons.server.report.ObjectInputStream;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.shared.GedInformations;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.DocumentVersionDTO;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.portal.client.services.GedService;
import bpm.vanilla.portal.server.security.PortalSession;
import bpm.vanilla.portal.shared.FieldDefinitionDTO;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GedServiceImpl extends RemoteServiceServlet implements GedService {

	private static final long serialVersionUID = 7634630057207093793L;

	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		logger.info("ActionsServiceImpl finished initing.");
	}

	private PortalSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), PortalSession.class);
	}
	
	@Override
	public List<FieldDefinitionDTO> getFieldDefinitions() throws ServiceException{
		try {
			PortalSession session = getSession();
			IGedComponent gedComponent = session.getGedComponent();
			
			List<Definition> defs = gedComponent.getFieldDefinitions(true);
			//List<IFieldDefinition> defs = GedConfiguration.getInstance().getGedComponent().getFieldDefinitions(true);
			
			List<FieldDefinitionDTO> res = new ArrayList<FieldDefinitionDTO>(); 
			for (Definition def : defs) {
				FieldDefinitionDTO d = new FieldDefinitionDTO();
				d.setAnalyzed(def.analized());
				d.setBoostLvl(def.getBoostLvl());
				d.setCustom(def.custom());
				d.setId(def.getId());
				d.setMultiple(def.multiple());
				d.setName(def.getName());
				d.setRequired(def.required());
				d.setStored(def.stored());
				d.setSystem(def.system());
				
				res.add(d);
			}
			return res;
			
		} catch (Exception e) {
			String msg = "Failed to retrieve field definitions : " + e.getMessage();
			throw new ServiceException(msg, e);
		}
	}
	
	@Override
	public List<DocumentVersionDTO> sampleSearch(String[] keywords, Boolean allconditions) throws ServiceException {

		PortalSession session = getSession();
		try {
			logger.info("Preparing sample search for keywords : " + keywords);
			
			List<String> words = new ArrayList<String>();
			for (String s : keywords) {
				words.add(s);
			}
			
			int groupId = session.getCurrentGroup().getId();
			int repositoryId = session.getCurrentRepository().getId();
			
			GedSearchRuntimeConfig config = new GedSearchRuntimeConfig();
			config.setAllOccurences(allconditions);
			config.setKeywords(words);
			config.setProperties(new ComProperties());
			config.addGroupId(groupId);
			config.setRepositoryId(repositoryId);
		
			List<GedDocument> docs = session.getGedComponent().search(config);
			
			List<DocumentVersionDTO> res = new ArrayList<DocumentVersionDTO>();
			for (GedDocument d : docs) {
				
				DocumentDefinitionDTO doc = DocumentHelper.transformGedDocumentToDto(d);
				
				DocumentVersion docVersion = d.getLastVersion();
				if(docVersion != null 
						&& (docVersion.getPeremptionDate() == null || docVersion.getPeremptionDate().after(new Date()))){
					String key = session.addDocumentVersion(docVersion);
					
					res.add(DocumentHelper.transformDocumentVersionToDto(docVersion, doc, key));
				}
			}
			return res;
		} catch (Exception e) {
			String msg = "Search failed : " + e.getMessage();
			logger.error(msg, e);
			throw new ServiceException(msg, e);
		}

	}

	@Override
	public List<DocumentVersionDTO> complexSearch(HashMap<FieldDefinitionDTO, String> wanted, 
		String[] keywords, Boolean allconditions) throws ServiceException {

		PortalSession session = getSession();
		
		List<String> words = new ArrayList<String>();
		for (String s : keywords) {
			words.add(s);
		}
		
		try {
			ComProperties com = new ComProperties();
			List<Definition> defs = session.getGedComponent().getFieldDefinitions(true);
			
			for (FieldDefinitionDTO f : wanted.keySet()) {
				Definition field = findFieldByName(f.getName(), defs);
				com.setProperty(field, wanted.get(f));
			}

			int groupId = session.getCurrentGroup().getId();
			int repositoryId = session.getCurrentRepository().getId();
			
			GedSearchRuntimeConfig config = new GedSearchRuntimeConfig();
			config.setAllOccurences(allconditions);
			config.setKeywords(words);
			config.setProperties(com);
			config.setRepositoryId(repositoryId);
			config.addGroupId(groupId);
			
			List<GedDocument> docs = session.getGedComponent().search(config);
			
			List<DocumentVersionDTO> res = new ArrayList<DocumentVersionDTO>();
			for (GedDocument d : docs) {
				
				DocumentDefinitionDTO doc = DocumentHelper.transformGedDocumentToDto(d);
				
				DocumentVersion docVersion = d.getLastVersion();
				if(docVersion != null
						&& (docVersion.getPeremptionDate() == null || docVersion.getPeremptionDate().after(new Date()))){
					String key = session.addDocumentVersion(docVersion);
					
					res.add(DocumentHelper.transformDocumentVersionToDto(docVersion, doc, key));
				}
			}
			return res;
		} catch (Exception e) {
			String msg = "Search failed : " + e.getMessage();
			throw new ServiceException(msg, e);
		}

	}
	
	@Override
	public void indexFile(GedInformations gedInfos) throws ServiceException{
		logger.info("-- Stock Ged informations --");

		PortalSession session = getSession();
		session.setGedInformations(gedInfos);
		
		logger.info("-- End stock Ged informations --");
	}

	private Definition findFieldByName(String fieldName, List<Definition> defs) {
		for (Definition def : defs) {
			if (def.getName().equals(fieldName)) {
				return def;
			}
		}
		
		return null;
	}

	@Override
	public String loadDocument(String key, String format) {
		logger.info("Requested ged file with key : " + key);
		PortalSession session;
		try {
			session = getSession();
	
			DocumentVersion docVersion = session.getDocumentVersion(key);
			
			GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(docVersion.getParent(), session.getUser().getId(), docVersion.getVersion());
			
			InputStream is = session.getGedComponent().loadGedDocument(config);
			byte currentXMLBytes[] = IOUtils.toByteArray(is);
			ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
			ObjectInputStream repIS = new ObjectInputStream();
			repIS.addStream(format, byteArrayIs);
			
			is.close();
			
			String fullName = key + "_" + new Object().hashCode();
			session.addReport(fullName, repIS);
			
			return fullName;
		} catch (ServiceException e) {
			e.printStackTrace();
			return "<error>" + e.getMessage() + "</error>";
		} catch (Exception e) {
			e.printStackTrace();
			return "<error>" + e.getMessage() + "</error>";
		}
	}

	@Override
	public boolean checkIfItemCanBeCheckin(int documentId, int userId) throws ServiceException {
		PortalSession session;
		try {
			session = getSession();
			
			return session.getGedComponent().canCheckin(documentId, userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public String checkout(int documentId, int userId, String key, String format) throws ServiceException {
		PortalSession session = getSession();
			
		try {
			boolean canCheckout = session.getGedComponent().canCheckout(documentId);
			if(canCheckout){
				session.getGedComponent().checkout(documentId, userId);

				DocumentVersion docVersion = session.getDocumentVersion(key);
				
				GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(docVersion.getParent(), session.getUser().getId());
				
				InputStream is = session.getGedComponent().loadGedDocument(config);
				byte currentXMLBytes[] = IOUtils.toByteArray(is);
				ByteArrayInputStream byteArrayIs = new ByteArrayInputStream(currentXMLBytes);
				ObjectInputStream repIS = new ObjectInputStream();
				repIS.addStream(format, byteArrayIs);
				
				is.close();
				
				String fullName = key + "_" + new Object().hashCode();
				session.addReport(fullName, repIS);
				
				return fullName;
			}
			else {
				throw new ServiceException("Checkout impossible because this document is lock by somebody else.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<DocumentDefinitionDTO> getAllDocuments() throws ServiceException {
		PortalSession session = getSession();
		List<DocumentDefinitionDTO> documents = new ArrayList<DocumentDefinitionDTO>();
		
		List<Integer> groupIds = new ArrayList<Integer>();
		groupIds.add(session.getCurrentGroup().getId());
		
		int repositoryId = session.getCurrentRepository().getId();
		
		List<GedDocument> docs;
		try {
			docs = session.getGedComponent().getDocuments(groupIds, repositoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Failed to get existing documents", e);
		}
		if(docs == null){
			return documents;
		}
		
		for(GedDocument doc : docs){
			DocumentDefinitionDTO d = DocumentHelper.transformGedDocumentToDto(doc);
			if(doc.getDocumentVersions() != null){
				for(DocumentVersion version : doc.getDocumentVersions()){
					if((version.getPeremptionDate() == null || version.getPeremptionDate().after(new Date()))){
						String key = session.addDocumentVersion(version);
						
						d.addVersion(DocumentHelper.transformDocumentVersionToDto(version, d, key));
					}
				}
			}
			
			documents.add(d);
		}
		
		Collections.sort(documents);
		
		return documents;
	}

	@Override
	public void comeBackToVersion(DocumentVersionDTO item) throws ServiceException {
		PortalSession session = getSession();
		
		DocumentVersion docVersion = session.getDocumentVersion(item.getKey());
		try {
			session.getGedComponent().comeBackToVersion(docVersion.getParent(), docVersion, session.getCurrentGroup(), session.getCurrentRepository(), session.getUser());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to update this document.", e);
		}
	}
}
