package bpm.vanilla.platform.core.remote.impl.repository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.CDATA;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.thoughtworks.xstream.XStream;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.ManageAction;
import bpm.vanilla.platform.core.beans.comments.CommentDefinition;
import bpm.vanilla.platform.core.beans.comments.CommentParameter;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.validation.UserValidation.UserValidationType;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ItemMetadataTableLink;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.utils.ImpactLevel;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

public class RemoteRepository implements IRepositoryService {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();

	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	public RemoteRepository(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	@Override
	public RepositoryDirectory addDirectory(String name, String comment, RepositoryDirectory parent) throws Exception {

		XmlAction op = new XmlAction(createArguments(name, comment, parent), IRepositoryService.ActionType.CREATE_DIRECTORY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (RepositoryDirectory) handleError(xml);
	}

	@Override
	public RepositoryItem addDirectoryItemWithDisplay(int type, int subType, RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, String xml, boolean display) throws Exception {

		byte[] rawBytes = Base64.encodeBase64(xml.getBytes("UTF-8"));

		XmlAction op = new XmlAction(createArguments(type, subType, target, name, comment, internalVersion, publicVersion, rawBytes, display), IRepositoryService.ActionType.CREATE_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (RepositoryItem) handleError(result);
	}

	@Override
	public void delete(RepositoryDirectory directory) throws Exception {
		XmlAction op = new XmlAction(createArguments(directory), IRepositoryService.ActionType.DELETE_DIRECTORY);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public void delete(RepositoryItem directoryItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItem), IRepositoryService.ActionType.DELETE_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	@Override
	public List<String> getCubeNames(RepositoryItem fasdItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(fasdItem), IRepositoryService.ActionType.LIST_CUBE_NAMES);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<String>) handleError(result);
	}

	@Override
	public List<RepositoryItem> getCubeViews(String cubeName, RepositoryItem fasdItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(cubeName, fasdItem), IRepositoryService.ActionType.LIST_CUBE_VIEWS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<RepositoryItem>) handleError(result);
	}

	@Override
	public List<RepositoryItem> getFmdtDrillers(RepositoryItem fmdtItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(fmdtItem), IRepositoryService.ActionType.LIST_FMDT_DRILLER);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<RepositoryItem>) handleError(result);
	}

	@Override
	public List<RepositoryItem> getDependantItems(RepositoryItem directoryItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItem), IRepositoryService.ActionType.DEPENDANT_ITEMS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<RepositoryItem>) handleError(result);
	}

	@Override
	public RepositoryDirectory getDirectory(int parentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId), IRepositoryService.ActionType.FIND_DIRECTORY);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}

		return (RepositoryDirectory) handleError(result);
	}

	@Override
	public List<IRepositoryObject> getDirectoryContent(RepositoryDirectory parent, int type) throws Exception {
		XmlAction op = new XmlAction(createArguments(parent, type), IRepositoryService.ActionType.DIRECTORY_CONTENT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return (List<IRepositoryObject>) handleError(result);
	}

	@Override
	public RepositoryItem getDirectoryItem(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryService.ActionType.FIND_DIRECTORY_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}

		return (RepositoryItem) handleError(result);
	}

	@Override
	public List<LinkedDocument> getLinkedDocumentsForGroup(int directoryItemId, int groupId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId, groupId), IRepositoryService.ActionType.LIST_LINKED_DOCUMENT);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return (List<LinkedDocument>) handleError(result);

	}

	@Override
	public List<RepositoryItem> getNeededItems(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryService.ActionType.REQUESTED_ITEMS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<RepositoryItem>) handleError(result);
	}

	@Override
	public List<Parameter> getParameters(RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), IRepositoryService.ActionType.ITEM_PARAMETERS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		return (List<Parameter>) handleError(result);
	}

	@Override
	public String loadModel(RepositoryItem repItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(repItem), IRepositoryService.ActionType.IMPORT_MODEL);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}

		byte[] decoded = Base64.decodeBase64((byte[]) handleError(result));
		return new String(decoded, "UTF-8");

	}

	@Override
	public void update(RepositoryDirectory directory) throws Exception {
		XmlAction op = new XmlAction(createArguments(directory), IRepositoryService.ActionType.UPDATE_DIRECTORY);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);

	}

	@Override
	public void updateModel(RepositoryItem item, String xml) throws Exception {
		byte[] raw = Base64.encodeBase64(xml.getBytes("UTF-8"));
		XmlAction op = new XmlAction(createArguments(item, raw), IRepositoryService.ActionType.UPDATE_MODEL_DEFINITION);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(result);
	}

	private Object handleError(String responseMessage) throws Exception {
		if (responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if (o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	@Override
	public RepositoryItem addExternalDocumentWithDisplay(RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, InputStream extDocStream, boolean display, String format) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(extDocStream, bos, true, true);

		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(target, name, comment, internalVersion, publicVersion, rawBytes, display, format), IRepositoryService.ActionType.CREATE_EXT_DOC_ITEM);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		if (result.isEmpty()) {
			return null;
		}

		return (RepositoryItem) handleError(result);
	}

	@Override
	public boolean checkItemUpdate(RepositoryItem item, Date date) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, date), IRepositoryService.ActionType.CHECK_ITEM_UPATE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) handleError(result);
	}

	@Override
	public HashMap<RepositoryItem, byte[]> getCubeViewsWithImageBytes(String cube, RepositoryItem fasdItem) throws Exception {
		XmlAction op = new XmlAction(createArguments(cube, fasdItem), IRepositoryService.ActionType.LIST_CUBE_VIEWS_WITH_IMAGES);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (HashMap<RepositoryItem, byte[]>) xstream.fromXML(result);
	}

	@Override
	public RepositoryItem addExternalUrl(RepositoryDirectory target, String name, String comment, String internalVersion, String publicVersion, String url) throws Exception {
		Element e = DocumentHelper.createElement("exturl");
		Element u = e.addElement("url");

		if (name == null) {
			throw new Exception("Name cannot be null");
		}
		e.addElement("name").setText(name);

		try {
			e.addElement("comment").setText(comment);
		} catch (Exception ex) {

		}

		try {
			e.addElement("internalVersion").setText(internalVersion);
		} catch (Exception ex) {

		}

		CDATA c = DocumentHelper.createCDATA(url);
		u.add(c);
		return addDirectoryItemWithDisplay(IRepositoryApi.URL, -1, target, name, comment, internalVersion, publicVersion, e.asXML(), true);
	}

	@Override
	public String loadUrl(RepositoryItem item) throws Exception {
		String xml = loadModel(item);
		Document doc = null;
		doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		return (String) root.element("url").getData();
	}

	@Override
	public byte[] createDisconnectedPackage(String projectName, int limitRows, List<RepositoryItem> items) throws Exception {
		XmlAction op = new XmlAction(createArguments(projectName, limitRows, items), IRepositoryService.ActionType.CREATE_DISCONNECTED_PACKAGE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}

		byte[] decoded = Base64.decodeBase64((byte[]) handleError(result));
		return decoded;
	}

	@Override
	public List<ImpactLevel> getImpactGraph(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IRepositoryService.ActionType.GET_IMPACT_GRAPH);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<ImpactLevel>) xstream.fromXML(result);
	}

	@Override
	public List<RepositoryItem> getItems(String search) throws Exception {
		XmlAction op = new XmlAction(createArguments(search), IRepositoryService.ActionType.SEARCH_ITEMS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		return (List<RepositoryItem>) handleError(result);
	}

	@Override
	public Validation addOrUpdateValidation(Validation validation) throws Exception {
		XmlAction op = new XmlAction(createArguments(validation), IRepositoryService.ActionType.ADD_OR_UPDATE_VALIDATION);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (Validation) xstream.fromXML(result);
	}

	@Override
	public CommentDefinition addOrUpdateCommentDefinition(CommentDefinition commentDefinition) throws Exception {
		XmlAction op = new XmlAction(createArguments(commentDefinition), IRepositoryService.ActionType.ADD_OR_UPDATE_COMMENT_DEFINITION);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (CommentDefinition) xstream.fromXML(result);
	}

	@Override
	public CommentValue addOrUpdateCommentValue(CommentValue comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IRepositoryService.ActionType.ADD_OR_UPDATE_COMMENT_VALUE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (CommentValue) xstream.fromXML(result);
	}

	@Override
	public void deleteCommentDefinition(CommentDefinition comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IRepositoryService.ActionType.DELETE_COMMENT_DEFINITION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteCommentValue(CommentValue comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IRepositoryService.ActionType.DELETE_COMMENT_VALUE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Validation> getValidations(boolean includeInactive) throws Exception {
		XmlAction op = new XmlAction(createArguments(includeInactive), IRepositoryService.ActionType.GET_VALIDATIONS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (List<Validation>) xstream.fromXML(result);
	}

	@Override
	public Validation getValidation(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IRepositoryService.ActionType.GET_VALIDATION);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (Validation) xstream.fromXML(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Validation> getValidationByStartEtl(int directoryItemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(directoryItemId), IRepositoryService.ActionType.GET_VALIDATION_BY_START_ETL);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return new ArrayList<Validation>();
		}
		return (List<Validation>) xstream.fromXML(result);
	}

	@Override
	public CommentDefinition getCommentDefinition(int itemId, String commentName) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, commentName), IRepositoryService.ActionType.GET_COMMENT_DEFINITION);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (CommentDefinition) xstream.fromXML(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CommentDefinition> getCommentDefinitions(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IRepositoryService.ActionType.GET_COMMENTS_DEFINITION);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return new ArrayList<CommentDefinition>();
		}
		return (List<CommentDefinition>) xstream.fromXML(result);
	}

	@Override
	public CommentValue getCommentNotValidate(int commentDefinitionId) throws Exception {
		XmlAction op = new XmlAction(createArguments(commentDefinitionId), IRepositoryService.ActionType.GET_COMMENT_NOT_VALIDATE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (CommentValue) xstream.fromXML(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CommentValue> getComments(int itemId, String commentName, List<CommentParameter> parameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId, commentName, parameters), IRepositoryService.ActionType.GET_COMMENTS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return new ArrayList<CommentValue>();
		}
		return (List<CommentValue>) xstream.fromXML(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CommentValue> getComments(int commentDefinitionId, int repId, int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(commentDefinitionId, repId, userId), IRepositoryService.ActionType.GET_COMMENTS_FOR_USER);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return new ArrayList<CommentValue>();
		}
		return (List<CommentValue>) xstream.fromXML(result);
	}

	@Override
	public void addSharedFile(String fileName, InputStream sharedFileStream) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(sharedFileStream, bos, true, true);
		byte[] raws64 = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(fileName, raws64), IRepositoryService.ActionType.ADD_SHARED_FILE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public List<RepositoryItem> getItems(List<Integer> ids) throws Exception {
		XmlAction op = new XmlAction(createArguments(ids), IRepositoryService.ActionType.GET_ITEMS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return new ArrayList<RepositoryItem>();
		}
		return (List<RepositoryItem>) xstream.fromXML(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<Template<T>> getTemplates(boolean lightWeight, TypeTemplate type) throws Exception {
		XmlAction op = new XmlAction(createArguments(lightWeight, type), IRepositoryService.ActionType.GET_TEMPLATES);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return new ArrayList<Template<T>>();
		}
		return (List<Template<T>>) xstream.fromXML(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Template<T> getTemplate(int templateId) throws Exception {
		XmlAction op = new XmlAction(createArguments(templateId), IRepositoryService.ActionType.GET_TEMPLATE);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (Template<T>) xstream.fromXML(result);
	}

	@Override
	public void addTemplate(Template<?> template) throws Exception {
		XmlAction op = new XmlAction(createArguments(template), IRepositoryService.ActionType.ADD_TEMPLATE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteTemplate(Template<?> template) throws Exception {
		XmlAction op = new XmlAction(createArguments(template), IRepositoryService.ActionType.DELETE_TEMPLATE);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void addItemMetadataTableLink(ItemMetadataTableLink link) throws Exception {
		XmlAction op = new XmlAction(createArguments(link), IRepositoryService.ActionType.ADD_ITEM_METADATA_LINK);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	public void deleteItemMetadataTableLink(ItemMetadataTableLink link) throws Exception {
		XmlAction op = new XmlAction(createArguments(link), IRepositoryService.ActionType.DELETE_ITEM_METADATA_LINK);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ItemMetadataTableLink> getMetadataLinks(int itemId) throws Exception {
		XmlAction op = new XmlAction(createArguments(itemId), IRepositoryService.ActionType.GET_METADATA_LINKS);
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (List<ItemMetadataTableLink>) xstream.fromXML(result);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<RepositoryItem> getPendingItemsToComment(int userId) throws Exception {
		XmlAction op = new XmlAction(createArguments(userId), IRepositoryService.ActionType.GET_PENDING_ITEMS_TO_COMMENT);

		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (List<RepositoryItem>) xstream.fromXML(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ValidationCircuit> getValidationCircuits() throws Exception {
		XmlAction op = new XmlAction(createArguments(), IRepositoryService.ActionType.GET_VALIDATION_CIRCUITS);

		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		if (result.isEmpty()) {
			return null;
		}
		return (List<ValidationCircuit>) xstream.fromXML(result);
	}

	@Override
	public ValidationCircuit manageValidationCircuit(ValidationCircuit circuit, ManageAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(circuit, action), IRepositoryService.ActionType.MANAGE_VALIDATION_CIRCUIT);
		
		String result = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (ValidationCircuit) xstream.fromXML(result);
	}

	@Override
	public void updateUserValidation(int validationId, int oldUserId, int newUserId, UserValidationType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(validationId, oldUserId, newUserId, type), IRepositoryService.ActionType.UPDATE_NEXT_USER_VALIDATION);
		httpCommunicator.executeAction(op, xstream.toXML(op), false);
	}

}
