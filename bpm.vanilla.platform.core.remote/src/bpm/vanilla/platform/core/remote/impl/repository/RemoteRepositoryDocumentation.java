package bpm.vanilla.platform.core.remote.impl.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;

import com.thoughtworks.xstream.XStream;

public class RemoteRepositoryDocumentation implements IDocumentationService {
	private HttpCommunicator httpCommunicator;
	private static XStream xstream;
	static {
		xstream = new XStream();
	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for(int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

	public RemoteRepositoryDocumentation(HttpCommunicator httpCommunicator) {
		this.httpCommunicator = httpCommunicator;
	}

	private Object handleError(String responseMessage) throws Exception {
		if(responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if(o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}

	@Override
	public void addOrUpdateComment(Comment comment, List<Integer> groupIds) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment, groupIds), IDocumentationService.ActionType.ADD_COMMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}

	@Override
	public void delete(Comment comment) throws Exception {
		XmlAction op = new XmlAction(createArguments(comment), IDocumentationService.ActionType.DELETE_COMMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Comment> getComments(int groupId, int objectId, int type) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, objectId, type), IDocumentationService.ActionType.GET_COMMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Comment>) handleError(xml);
	}

	@Override
	public LinkedDocument attachDocumentToItem(RepositoryItem item, InputStream is, String displayName, String comment, String format, String relativePath) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(is, bos, true, true);
		byte[] raws64 = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(item, raws64, displayName, comment, format, relativePath), IDocumentationService.ActionType.LINK_DOC_TO_ITEM);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);

		return (LinkedDocument) handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getReportHistoricDocumentsId(RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), IDocumentationService.ActionType.LIST_REPORT_HISTO_DOC_IDS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<Integer>) handleError(xml);
	}

	/**
	 * @deprecated Use {@link #importExternalDocument(RepositoryItem)} instead
	 */
	@Override
	public void importExternalDocument(RepositoryItem item, OutputStream outputStream) throws Exception {
		InputStream bis = importExternalDocument(item);
		IOWriter.write(bis, outputStream, true, true);
	}

	@Override
	public InputStream importExternalDocument(RepositoryItem item) throws Exception {
		XmlAction op = new XmlAction(createArguments(item), IDocumentationService.ActionType.LOAD_EXT_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		ByteArrayInputStream bis = (ByteArrayInputStream) handleError(xml);
		return bis;
		
	}

	@Override
	public InputStream importLinkedDocument(int linkedDocumentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(linkedDocumentId), IDocumentationService.ActionType.LOAD_LINKED_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		ByteArrayInputStream bis =  (ByteArrayInputStream) handleError(xml);
		return bis;
	}

	@Override
	public void mapReportItemToReportDocument(RepositoryItem item, int docId, boolean userPrivate) throws Exception {
		XmlAction op = new XmlAction(createArguments(item, docId, userPrivate), IDocumentationService.ActionType.MAP_REPORT_TO_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);

	}

	@Override
	public String updateExternalDocument(RepositoryItem item, InputStream datas) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(datas, bos, true, true);
		byte[] raws64 = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(item, raws64), IDocumentationService.ActionType.UPDATE_EXT_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (String) handleError(xml);
	}

	@Override
	public void addSecuredCommentObject(SecuredCommentObject secObject) throws Exception {
		XmlAction op = new XmlAction(createArguments(secObject), IDocumentationService.ActionType.ADD_SEC_COMMENT_OBJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}

	@Override
	public void addSecuredCommentObjects(List<SecuredCommentObject> secs) throws Exception {
		XmlAction op = new XmlAction(createArguments(secs), IDocumentationService.ActionType.ADD_SEC_COMMENT_OBJECTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}

	@Override
	public void deleteComments(int objectId, int type) throws Exception {
		XmlAction op = new XmlAction(createArguments(objectId, type), IDocumentationService.ActionType.DELETE_COMMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<SecuredCommentObject> getSecuredCommentObjects(int objectId, int type) throws Exception {
		XmlAction op = new XmlAction(createArguments(objectId, type), IDocumentationService.ActionType.GET_SEC_COMMENT_OBJECTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (List<SecuredCommentObject>) handleError(xml);
	}

	@Override
	public void removeSecuredCommentObject(int groupId, int objectId, int type) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, objectId, type), IDocumentationService.ActionType.REMOVE_SEC_COMMENT_OBJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}

	@Override
	public boolean canComment(int groupId, int objectId, int type) throws Exception {
		XmlAction op = new XmlAction(createArguments(groupId, objectId, type), IDocumentationService.ActionType.CAN_COMMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		return (Boolean) handleError(xml);
	}

	@Override
	public void removeSecuredCommentObjects(int objectId, int type) throws Exception {
		XmlAction op = new XmlAction(createArguments(objectId, type), IDocumentationService.ActionType.REMOVE_SEC_COMMENT_OBJECTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op), false);
		handleError(xml);
	}
}
