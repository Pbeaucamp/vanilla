package bpm.vanilla.platform.core.remote.impl.components;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.VanillaConstants;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.Category;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.components.ged.GedAdvancedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedIndexRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.historic.HistoricRuntimeConfiguration;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RemoteGedComponent implements IGedComponent {

	private HttpCommunicator httpCommunicator;
	private static XStream xstream;

	public RemoteGedComponent(String vanillaUrl, String login, String password) {
		this.httpCommunicator = new HttpCommunicator();
		httpCommunicator.init(vanillaUrl, login, password);
	}

	public RemoteGedComponent(IVanillaContext ctx) {
		this(ctx.getVanillaUrl(), ctx.getLogin(), ctx.getPassword());
	}

	static {
		xstream = new XStream();
//		xstream.registerConverter(new DateConverter());
	}

	@Override
	public List<GedDocument> search(GedSearchRuntimeConfig config) throws Exception {
		XmlAction op = new XmlAction(createArguments(config), IGedComponent.ActionType.SEARCH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List<GedDocument>) xstream.fromXML(xml);
	}

	@Override
	public List<Definition> getFieldDefinitions(boolean includeCustom) throws Exception {
		XmlAction op = new XmlAction(createArguments(includeCustom), IGedComponent.ActionType.GET_FIELD_DEFINITIONS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List<Definition>) xstream.fromXML(xml);
	}

	@Override
	public List<Category> getGedCategories() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IGedComponent.ActionType.GET_CATEGORIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (List<Category>) xstream.fromXML(xml);
	}

	@Override
	public int index(GedIndexRuntimeConfig config, InputStream is) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = is.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		is.close();

		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(config, streamDatas), IGedComponent.ActionType.INDEX);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public InputStream loadGedDocument(GedLoadRuntimeConfig config) throws Exception {
		XmlAction op = new XmlAction(createArguments(config), IGedComponent.ActionType.LOAD_DOCUMENT);
		return httpCommunicator.executeActionForBigStream(VanillaConstants.VANILLA_GED_INDEX_SERVLET, xstream.toXML(op));
	}

	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public void addCategory(Category category) throws Exception {
		XmlAction op = new XmlAction(createArguments(category), IGedComponent.ActionType.ADD_CATEGORY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void addDocumentCategory(int docId, int catId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, catId), IGedComponent.ActionType.ADD_DOC_TO_CAT);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void addFieldDefinition(Definition definition) throws Exception {
		XmlAction op = new XmlAction(createArguments(definition), IGedComponent.ActionType.ADD_FIELD_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteCategory(Category category) throws Exception {
		XmlAction op = new XmlAction(createArguments(category), IGedComponent.ActionType.DELETE_CATEGORY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteDocumentCategory(int docId, int catId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, catId), IGedComponent.ActionType.DELETE_DOC_TO_CAT);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteFieldDefinition(Definition definition) throws Exception {
		XmlAction op = new XmlAction(createArguments(definition), IGedComponent.ActionType.DELETE_FIELD_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void updateCategory(Category category) throws Exception {
		XmlAction op = new XmlAction(createArguments(category), IGedComponent.ActionType.UPDATE_CATEGORY);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void updateFieldDefinition(Definition definition) throws Exception {
		XmlAction op = new XmlAction(createArguments(definition), IGedComponent.ActionType.UPDATE_FIELD_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void addAccess(int docId, int groupId, int repId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, groupId, repId), IGedComponent.ActionType.ADD_ACCESS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void removeAccess(int docId, int groupId, int repId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, groupId, repId), IGedComponent.ActionType.DELETE_ACCESS);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public GedDocument getDocumentDefinitionById(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IGedComponent.ActionType.GET_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (GedDocument) xstream.fromXML(xml);
	}

	@Override
	public DocumentVersion getDocumentVersionById(int versionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(versionId), IGedComponent.ActionType.GET_DOCUMENT_VERSION_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (DocumentVersion) xstream.fromXML(xml);
	}

	@Override
	public List<GedDocument> getAllDocuments() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IGedComponent.ActionType.GET_ALL_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GedDocument>) xstream.fromXML(xml);
	}

	@Override
	public List<GedDocument> getDocumentsToIndex() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IGedComponent.ActionType.GET_WAITING_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GedDocument>) xstream.fromXML(xml);
	}

	@Override
	public int addGedDocument(GedDocument doc, InputStream docInputStream, String format) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = docInputStream.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		docInputStream.close();

		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(doc, streamDatas, format), IGedComponent.ActionType.ADD_GED_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Integer) xstream.fromXML(xml);
	}

	@Override
	public void deleteGedDocument(int doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IGedComponent.ActionType.DEL_GED_DOC);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void indexExistingFile(HistoricRuntimeConfiguration config, int docId, int version, boolean createEntry) throws Exception {
		XmlAction op = new XmlAction(createArguments(config, docId, version, createEntry), IGedComponent.ActionType.INDEX_EXISTING);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<GedDocument> getDocumentsByCategory(int catId) throws Exception {
		XmlAction op = new XmlAction(createArguments(catId), IGedComponent.ActionType.GET_DOCS_FOR_CAT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GedDocument>) xstream.fromXML(xml);
	}

	@Override
	public void resetGedIndex() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IGedComponent.ActionType.RESET_GED_INDEX);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public boolean rebuildGedIndex() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IGedComponent.ActionType.REBUILD_INDEX);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public List<GedDocument> advancedSearch(GedAdvancedSearchRuntimeConfig config) throws Exception {
		XmlAction op = new XmlAction(createArguments(config), IGedComponent.ActionType.ADVANCED_SEARCH);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GedDocument>) xstream.fromXML(xml);
	}

	@Override
	public List<Security> getSecuForDocument(GedDocument document) throws Exception {
		XmlAction op = new XmlAction(createArguments(document), IGedComponent.ActionType.GET_SECU_BY_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Security>) xstream.fromXML(xml);
	}

	@Override
	public void updateSecurity(GedDocument document, Group group, boolean authorized, int repositoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(document, group, authorized, repositoryId), IGedComponent.ActionType.UPDATE_SECU);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public GedDocument createDocument(String documentName, String format, int userId, List<Integer> groupIds, int repositoryId, InputStream fileInputStream) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = fileInputStream.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		fileInputStream.close();

		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(documentName, format, userId, groupIds, repositoryId, streamDatas), IGedComponent.ActionType.CREATE_GED_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (GedDocument) xstream.fromXML(xml);
	}

	@Override
	public GedDocument createDocumentThroughServlet(String documentName, String format, int userId, List<Integer> groupIds, int repositoryId, InputStream fileInputStream) throws Exception {
		StringBuffer bufGroup = new StringBuffer();
		if (groupIds != null) {
			boolean first = true;
			for (Integer groupId : groupIds) {
				if (!first) {
					bufGroup.append(";");
				}
				bufGroup.append(groupId);
				first = false;
			}
		}
		
		HashMap<String, String> params = new HashMap<>();
		params.put(IGedComponent.PARAM_ACTION, IGedComponent.ActionType.CREATE_GED_DOCUMENT.toString());
		params.put(IGedComponent.PARAM_DOCUMENT_NAME, documentName);
		params.put(IGedComponent.PARAM_FORMAT, format);
		params.put(IGedComponent.PARAM_USER_ID, userId + "");
		params.put(IGedComponent.PARAM_GROUP_IDS, bufGroup.toString());
		params.put(IGedComponent.PARAM_REPOSITORY_ID, repositoryId + "");
		
		String xml = httpCommunicator.executeActionThroughServlet(params, fileInputStream);

		return (GedDocument) xstream.fromXML(xml);
	}

	@Override
	public DocumentVersion addVersionToDocument(GedDocument doc, String format, InputStream fileInputStream) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = fileInputStream.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		fileInputStream.close();

		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(doc, format, streamDatas), IGedComponent.ActionType.ADD_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (DocumentVersion) xstream.fromXML(xml);
	}

	@Override
	public DocumentVersion addVersionToDocumentThroughServlet(int docId, String format, InputStream fileInputStream) throws Exception {
		HashMap<String, String> params = new HashMap<>();
		params.put(IGedComponent.PARAM_ACTION, IGedComponent.ActionType.ADD_VERSION.toString());
		params.put(IGedComponent.PARAM_DOC_ID, docId + "");
		params.put(IGedComponent.PARAM_FORMAT, format);
		
		String xml = httpCommunicator.executeActionThroughServlet(params, fileInputStream);

		return (DocumentVersion) xstream.fromXML(xml);
	}

	@Override
	public void indexExistingFile(HistoricRuntimeConfiguration config, Integer id, boolean createEntry) throws Exception {
		XmlAction op = new XmlAction(createArguments(config, id, createEntry), IGedComponent.ActionType.INDEX_EXISTING);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void checkin(int documentId, int userId, String format, InputStream stream, boolean index) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int sz = 0;
		byte[] buf = new byte[1024];
		while ((sz = stream.read(buf)) >= 0) {
			bos.write(buf, 0, sz);
		}
		stream.close();

		byte[] streamDatas = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(documentId, userId, format, streamDatas, index), IGedComponent.ActionType.CHECKIN);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void checkout(int documentId, int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentId, userId), IGedComponent.ActionType.CHECKOUT);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public boolean canCheckin(int documentId, int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentId, userId), IGedComponent.ActionType.CAN_CHECKIN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public boolean canCheckout(int documentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentId), IGedComponent.ActionType.CAN_CHECKOUT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) xstream.fromXML(xml);
	}

	@Override
	public List<GedDocument> getDocuments(List<Integer> groupIds, int repositoryId) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupIds, repositoryId), IGedComponent.ActionType.GET_DOC_BY_GROUP);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<GedDocument>) xstream.fromXML(xml);
	}

	@Override
	public void updateVersion(DocumentVersion version) throws Exception {
		XmlAction op = new XmlAction(createArguments(version), IGedComponent.ActionType.UPDATE_VERSION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public DocumentVersion comeBackToVersion(GedDocument doc, DocumentVersion oldVersion, Group group, Repository repository, User user) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc, oldVersion, group, repository, user), IGedComponent.ActionType.COME_BACK_TO_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (DocumentVersion) xstream.fromXML(xml);
	}

	public void deleteVersion(DocumentVersion version) throws Exception {
		XmlAction op = new XmlAction(createArguments(version), IGedComponent.ActionType.DELETE_VERSION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

}
