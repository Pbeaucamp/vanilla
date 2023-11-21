package bpm.vanilla.platform.core.wrapper.servlets40;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs;
import bpm.vanilla.platform.core.beans.ged.Category;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.IGedComponent.ActionType;
import bpm.vanilla.platform.core.components.ged.GedAdvancedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.runtime.components.GedIndexManager;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.wrapper.VanillaCoreWrapper;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class GedIndexServlet extends AbstractComponentServlet {
	
	public GedIndexServlet(VanillaCoreWrapper vanillaCoreWrapper, IVanillaLogger logger) {
		this.logger = logger;
		this.component = vanillaCoreWrapper;
	}
	
	@Override
	public void init() throws ServletException {
		logger.info("Initializing GedIndexServlet...");
		super.init();
//		xstream.registerConverter(new DateConverter());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			Object actionResult = null;
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IGedComponent.ActionType)){
				throw new Exception("ActionType not a IGedComponent");
			}
			
			IGedComponent.ActionType type = (IGedComponent.ActionType)action.getActionType();
			
			try{
				switch (type) {
				case ADD_ACCESS:
					addAccess(args);
					break;
				case ADD_CATEGORY:
					addCategory(args);
					break;
				case ADD_DOC_TO_CAT:
					addDocumentCategory(args);
					break;
				case ADD_FIELD_DEFINITION:
					addFieldDefinition(args);
					break;
				case DELETE_ACCESS:
					deleteAccess(args);
					break;
				case DELETE_CATEGORY:
					deleteCategory(args);
					break;
				case DELETE_DOC_TO_CAT:
					deleteDocumentCategory(args);
					break;
				case DELETE_FIELD_DEFINITION:
					deleteFieldDefinition(args);
					break;
				case GET_CATEGORIES:
					actionResult = getCategories(args);
					break;
				case GET_DOCS_FOR_CAT:
					actionResult = getDocumentByCategory(args);
					break;
				case GET_FIELD_DEFINITIONS:
					actionResult = getFieldDefinitions(args);
					break;
				case INDEX:
					actionResult = index(args);
					break;
				case LOAD_DOCUMENT:
					loadDocumentAsStream(args, resp.getOutputStream());
					break;
				case SEARCH:
					actionResult = search(args);
					break;
				case UPDATE_CATEGORY:
					updateCategory(args);
					break;
				case UPDATE_FIELD_DEFINITION:
					updateFieldDefinition(args);
					break;
				case GET_DOC:
					actionResult = getDocumentDefinition(args);
					break;
				case GET_DOCUMENT_VERSION_BY_ID:
					actionResult = getDocumentVersionById(args);
					break;
				case GET_ALL_DOCS:
					actionResult = getAllDocumentDefinition(args);
					break;
				case GET_DOC_BY_GROUP:
					actionResult = getDocuments(args);
					break;
				case GET_WAITING_DOCS:
					actionResult = getWaitingDocumentDefinition(args);
					break;
				case ADD_GED_DOC:
					actionResult = addGedDocument(args);
					break;
				case DEL_GED_DOC:
					deleteGedDocument(args);
					break;
				case INDEX_EXISTING:
					indexExistingDocument(args);
					break;
				case RESET_GED_INDEX:
					resetGedIndex(args);
					break;
				case REBUILD_INDEX:
					actionResult = rebuildGedIndex(args);
					break;
				case ADVANCED_SEARCH:
					actionResult = advancedSearch(args);
					break;
				case GET_SECU_BY_DOC:
					actionResult = getSecuByDocument(args);
					break;
				case UPDATE_SECU:
					updateSecurity(args);
					break;
				case CREATE_GED_DOCUMENT:
					actionResult = createDocument(args);
					break;
				case ADD_VERSION:
					actionResult = addVersion(args);
					break;
				case CAN_CHECKIN:
					actionResult = canCheckin(args);
					break;
				case CHECKIN:
					checkin(args);
					break;
				case CAN_CHECKOUT:
					actionResult = canCheckout(args);
					break;
				case CHECKOUT:
					checkout(args);
					break;
				case UPDATE_VERSION:
					updateVersion(args);
					break;
				case DELETE_VERSION:
					deleteVersion(args);
					break;
				case COME_BACK_TO_VERSION:
					actionResult = comeBackToVersion(args);
					break;
				}
					
				try {
					if(type == ActionType.SEARCH) {
						log(type, ((GedIndexManager)component.getGedIndexComponent()).getComponentName(), req, (GedSearchRuntimeConfig) args.getArguments().get(0));
					}
					else {
						log(type, ((GedIndexManager)component.getGedIndexComponent()).getComponentName(), req);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}catch(Exception ex){
				throw new ActionException("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
				resp.getWriter().close();	
			}
			
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}

	private void deleteVersion(XmlArgumentsHolder args) throws Exception {
		DocumentVersion version = (DocumentVersion) args.getArguments().get(0);
		component.getGedIndexComponent().deleteVersion(version);
	}

	private void log(ActionType type, String componentName, HttpServletRequest req, GedSearchRuntimeConfig gedSearchRuntimeConfig) throws Exception {
		User user = null;
		try {
			user = extractUser(req);
		} catch (Exception e) {
			return;
		}
		String ipAddress = req.getRemoteAddr();
		VanillaLogs log = new VanillaLogs(VanillaLogs.Level.DEBUG, componentName, type.toString(), new Date(), user.getId(), ipAddress);
		StringBuilder buf  = new StringBuilder();
		for(String word : gedSearchRuntimeConfig.getKeywords()) {
			buf.append(word + " ");
		}
		log.setMessage(buf.toString());
		component.getLoggingManager().addVanillaLog(log);
	}

	private Object comeBackToVersion(XmlArgumentsHolder args) throws Exception {
		GedDocument doc = (GedDocument) args.getArguments().get(0);
		DocumentVersion oldVersion = (DocumentVersion)args.getArguments().get(1);
		Group group = (Group) args.getArguments().get(2);
		Repository rep = (Repository) args.getArguments().get(3);
		User user = (User) args.getArguments().get(4);
		
		return component.getGedIndexComponent().comeBackToVersion(doc, oldVersion, group , rep, user);
	}

	private void updateVersion(XmlArgumentsHolder args) throws Exception {
		DocumentVersion version = (DocumentVersion) args.getArguments().get(0);
		component.getGedIndexComponent().updateVersion(version);
	}

	private Object canCheckin(XmlArgumentsHolder args) throws Exception {
		int documentId = (Integer) args.getArguments().get(0);
		int userId = (Integer)args.getArguments().get(1);
		
		return component.getGedIndexComponent().canCheckin(documentId, userId);
	}

	private void checkin(XmlArgumentsHolder args) throws Exception {
		int documentId = (Integer) args.getArguments().get(0);
		int userId = (Integer)args.getArguments().get(1);
		String format = (String)args.getArguments().get(2);
		byte[] bytes = (byte[]) args.getArguments().get(3);
		boolean index = (Boolean) args.getArguments().get(4);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream docInputStream = new ByteArrayInputStream(decodedBytes);
		
		component.getGedIndexComponent().checkin(documentId, userId, format, docInputStream, index);
	}

	private Object canCheckout(XmlArgumentsHolder args) throws Exception {
		int documentId = (Integer) args.getArguments().get(0);
		
		return component.getGedIndexComponent().canCheckout(documentId);
	}

	private void checkout(XmlArgumentsHolder args) throws Exception {
		int documentId = (Integer) args.getArguments().get(0);
		int userId = (Integer)args.getArguments().get(1);
		
		component.getGedIndexComponent().checkout(documentId, userId);
	}

	@SuppressWarnings("unchecked")
	private GedDocument createDocument(XmlArgumentsHolder args) throws Exception {
		String documentName = (String) args.getArguments().get(0);
		String format = (String)args.getArguments().get(1);
		Integer userId = (Integer)args.getArguments().get(2);
		List<Integer> groupIds = (List<Integer>)args.getArguments().get(3);
		Integer repositoryId = (Integer)args.getArguments().get(4);
		byte[] bytes = (byte[]) args.getArguments().get(5);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream docInputStream = new ByteArrayInputStream(decodedBytes);
		return component.getGedIndexComponent().createDocument(documentName, format, userId, groupIds, repositoryId, docInputStream);
	}

	private DocumentVersion addVersion(XmlArgumentsHolder args) throws Exception {
		GedDocument doc = (GedDocument) args.getArguments().get(0);
		String format = (String)args.getArguments().get(1);
		byte[] bytes = (byte[]) args.getArguments().get(2);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream docInputStream = new ByteArrayInputStream(decodedBytes);
		return component.getGedIndexComponent().addVersionToDocument(doc, format, docInputStream);
	}

	private void updateSecurity(XmlArgumentsHolder args) throws Exception {
		GedDocument document = (GedDocument) args.getArguments().get(0);
		Group group = (Group) args.getArguments().get(1);
		boolean authorized = (Boolean) args.getArguments().get(2);
		int repositoryId = (Integer) args.getArguments().get(3);
		
		component.getGedIndexComponent().updateSecurity(document, group, authorized,repositoryId);
	}

	private Object getSecuByDocument(XmlArgumentsHolder args) throws Exception {
		GedDocument document = (GedDocument) args.getArguments().get(0);
		return component.getGedIndexComponent().getSecuForDocument(document);
	}

	private Object advancedSearch(XmlArgumentsHolder args) throws Exception {
		GedAdvancedSearchRuntimeConfig config = (GedAdvancedSearchRuntimeConfig) args.getArguments().get(0);
		return component.getGedIndexComponent().advancedSearch(config);
	}

	private Object rebuildGedIndex(XmlArgumentsHolder args) throws Exception {
		return component.getGedIndexComponent().rebuildGedIndex();
	}

	private void resetGedIndex(XmlArgumentsHolder args) throws Exception {
		component.getGedIndexComponent().resetGedIndex();
	}

	private void indexExistingDocument(XmlArgumentsHolder args) throws Exception {
		if(args.getArguments().size() > 3) {
			HistoricRuntimeConfiguration config = (HistoricRuntimeConfiguration) args.getArguments().get(0);
			int docId = (Integer) args.getArguments().get(1);
			int version = (Integer) args.getArguments().get(2);
			boolean createEntry = (Boolean) args.getArguments().get(3);
			component.getGedIndexComponent().indexExistingFile(config, docId, version, createEntry);
		}
		else {
			HistoricRuntimeConfiguration config = (HistoricRuntimeConfiguration) args.getArguments().get(0);
			int docId = (Integer) args.getArguments().get(1);
			boolean createEntry = (Boolean) args.getArguments().get(2);
			component.getGedIndexComponent().indexExistingFile(config, docId, createEntry);
		}
	}

	private void deleteGedDocument(XmlArgumentsHolder args) throws Exception {
		int doc = (Integer) args.getArguments().get(0);
		component.getGedIndexComponent().deleteGedDocument(doc);
	}

	private Object addGedDocument(XmlArgumentsHolder args) throws Exception {
		GedDocument doc = (GedDocument) args.getArguments().get(0);
		byte[] bytes = (byte[]) args.getArguments().get(1);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream docInputStream = new ByteArrayInputStream(decodedBytes);
		String format = (String) args.getArguments().get(2);
		return component.getGedIndexComponent().addGedDocument(doc ,docInputStream, format);
	}

	private Object getWaitingDocumentDefinition(XmlArgumentsHolder args) throws Exception {
		return component.getGedIndexComponent().getDocumentsToIndex();
	}

	private Object getAllDocumentDefinition(XmlArgumentsHolder args) throws Exception {
		return component.getGedIndexComponent().getAllDocuments();
	}

	private Object getDocuments(XmlArgumentsHolder args) throws Exception {
		return component.getGedIndexComponent().getDocuments((List<Integer>)args.getArguments().get(0), (Integer)args.getArguments().get(1));
	}

	private Object getDocumentDefinition(XmlArgumentsHolder args) throws Exception {
		int id = (Integer)args.getArguments().get(0);
		return component.getGedIndexComponent().getDocumentDefinitionById(id);
	}

	private Object getDocumentVersionById(XmlArgumentsHolder args) throws Exception {
		int id = (Integer)args.getArguments().get(0);
		return component.getGedIndexComponent().getDocumentVersionById(id);
	}

	private void loadDocumentAsStream(XmlArgumentsHolder args, ServletOutputStream servletOutputStream) throws Exception {
		GedLoadRuntimeConfig conf = (GedLoadRuntimeConfig) args.getArguments().get(0);
		InputStream is = component.getGedIndexComponent().loadGedDocument(conf);
		IOWriter.write(is, servletOutputStream, true, false);
	}

	private Object index(XmlArgumentsHolder args) throws Exception {
		GedIndexRuntimeConfig conf = (GedIndexRuntimeConfig) args.getArguments().get(0);
		byte[] bytes = (byte[]) args.getArguments().get(1);
		
		byte[] decodedBytes = Base64.decodeBase64(bytes);
		InputStream is = new ByteArrayInputStream(decodedBytes);
		
		
		return component.getGedIndexComponent().index(conf, is);
	}

	private Object search(XmlArgumentsHolder args) throws Exception {
		GedSearchRuntimeConfig conf = (GedSearchRuntimeConfig) args.getArguments().get(0);		
		return component.getGedIndexComponent().search(conf);
	}

	private void updateFieldDefinition(XmlArgumentsHolder args) throws Exception {
		Definition def = (Definition) args.getArguments().get(0);
		component.getGedIndexComponent().updateFieldDefinition(def);
	}

	private void updateCategory(XmlArgumentsHolder args) throws Exception {
		Category cat = (Category) args.getArguments().get(0);
		component.getGedIndexComponent().updateCategory(cat);
	}

	private Object getFieldDefinitions(XmlArgumentsHolder args) throws Exception {
		boolean includeCustom = (Boolean) args.getArguments().get(0);
		return component.getGedIndexComponent().getFieldDefinitions(includeCustom);
	}

	private Object getDocumentByCategory(XmlArgumentsHolder args) throws Exception {
		int catId = (Integer)args.getArguments().get(0); 
		return component.getGedIndexComponent().getDocumentsByCategory(catId);
	}

	private Object getCategories(XmlArgumentsHolder args) throws Exception {
		return component.getGedIndexComponent().getGedCategories();
	}

	private void deleteFieldDefinition(XmlArgumentsHolder args) throws Exception {
		Definition def = (Definition) args.getArguments().get(0);
		component.getGedIndexComponent().deleteFieldDefinition(def);
	}

	private void deleteDocumentCategory(XmlArgumentsHolder args) throws Exception {
		int docId = (Integer)args.getArguments().get(0);
		int catId = (Integer)args.getArguments().get(1);
		
		component.getGedIndexComponent().deleteDocumentCategory(docId, catId);
	}

	private void deleteCategory(XmlArgumentsHolder args) throws Exception {
		Category cat = (Category) args.getArguments().get(0);
		component.getGedIndexComponent().deleteCategory(cat);
	}

	private void deleteAccess(XmlArgumentsHolder args) throws Exception {
		int docId = (Integer) args.getArguments().get(0);
		int groupId = (Integer) args.getArguments().get(1);
		int repId = (Integer) args.getArguments().get(2);
		
		component.getGedIndexComponent().removeAccess(docId, groupId, repId);
	}

	private void addFieldDefinition(XmlArgumentsHolder args) throws Exception {
		Definition def = (Definition) args.getArguments().get(0);
		component.getGedIndexComponent().addFieldDefinition(def);
	}

	private void addDocumentCategory(XmlArgumentsHolder args) throws Exception {
		int docId = (Integer)args.getArguments().get(0);
		int catId = (Integer)args.getArguments().get(1);
		
		component.getGedIndexComponent().addDocumentCategory(docId, catId);
	}

	private void addCategory(XmlArgumentsHolder args) throws Exception {
		Category cat = (Category) args.getArguments().get(0);
		component.getGedIndexComponent().addCategory(cat);
	}

	private void addAccess(XmlArgumentsHolder args) throws Exception {
		int docId = (Integer) args.getArguments().get(0);
		int groupId = (Integer) args.getArguments().get(1);
		int repId = (Integer) args.getArguments().get(2);
		
		component.getGedIndexComponent().addAccess(docId, groupId, repId);
	}
}


