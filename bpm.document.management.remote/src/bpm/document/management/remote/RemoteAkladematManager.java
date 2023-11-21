package bpm.document.management.remote;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;

import bpm.document.management.core.IAkladematManager;
import bpm.document.management.core.model.AkladematAdminEntity;
import bpm.document.management.core.model.AkladematAdminEntity.DocType;
import bpm.document.management.core.model.AkladematAdminEntity.Status;
import bpm.document.management.core.model.Archive;
import bpm.document.management.core.model.Archiving;
import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.IAdminDematObject;
import bpm.document.management.core.model.MailEntity;
import bpm.document.management.core.model.MailFilter;
import bpm.document.management.core.model.ScanMetaObject;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.core.model.aklademat.AkladematSettings;
import bpm.document.management.core.model.aklademat.AkladematTypeSettings;
import bpm.document.management.core.model.aklademat.Classification;
import bpm.document.management.core.model.aklademat.PastellFile.FileType;
import bpm.document.management.core.model.aklademat.PastellResult;
import bpm.document.management.core.xstream.XmlAction;
import bpm.document.management.core.xstream.XmlArgumentsHolder;
import bpm.document.management.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.xstream.DateConverter;

import com.thoughtworks.xstream.XStream;

public class RemoteAkladematManager implements IAkladematManager {
	
	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private XStream xstream;

	public RemoteAkladematManager(VdmContext ctx) {
		httpCommunicator.init(ctx.getVdmUrl(), ctx.getMail(), ctx.getPassword(), null);
		xstream = new XStream();
		xstream.registerConverter(new DateConverter());
	}

	private static XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}

//	@Override
//	public AkladematDocumentEntity addAkladematEntity(AkladematDocumentEntity entity) throws Exception {
//		XmlAction op = new XmlAction(createArguments(entity), IAkladematManager.ActionType.AKLADEMAT_ADD_ENTITY);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (AkladematDocumentEntity) xstream.fromXML(xml);
//	}

	@Override
	@SuppressWarnings("unchecked")
	public AkladematAdminEntity<? extends IAdminDematObject> getAkladematAdminEntityById(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), IAkladematManager.ActionType.GET_AKLADEMAT_ADMIN_ENTITY_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AkladematAdminEntity<? extends IAdminDematObject>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public AkladematAdminEntity<? extends IAdminDematObject> addAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws Exception {
		XmlAction op = new XmlAction(createArguments(adminEntity), IAkladematManager.ActionType.ADD_AKLADEMAT_ADMIN_ENTITY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AkladematAdminEntity<? extends IAdminDematObject>) xstream.fromXML(xml);
	}

	@Override
	public void updateAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws Exception {
		XmlAction op = new XmlAction(createArguments(adminEntity), IAkladematManager.ActionType.UPDATE_AKLADEMAT_ADMIN_ENTITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deletAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws Exception {
		XmlAction op = new XmlAction(createArguments(adminEntity), IAkladematManager.ActionType.DELETE_AKLADEMAT_ADMIN_ENTITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

//	@Override
//	public void removeAkladematEntity(AkladematDocumentEntity entity) throws Exception {
//		XmlAction op = new XmlAction(createArguments(entity), IAkladematManager.ActionType.AKLADEMAT_REMOVE_ENTITY);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}
//
//	@Override
//	@SuppressWarnings("unchecked")
//	public List<AkladematDocumentEntity> getAkladematEntities(DocType docType, Status status) throws Exception {
//		XmlAction op = new XmlAction(createArguments(docType, status), IAkladematManager.ActionType.AKLADEMAT_GET_ENTITIES);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (List<AkladematDocumentEntity>) xstream.fromXML(xml);
//	}
//
//	@Override
//	public void manageAkladematEntities(List<AkladematDocumentEntity> selectedEntitites, Status status) throws Exception {
//		XmlAction op = new XmlAction(createArguments(selectedEntitites, status), IAkladematManager.ActionType.AKLADEMAT_MANAGE_ENTITY);
//		httpCommunicator.executeAction(op, xstream.toXML(op));
//	}
//
//	@Override
//	public AkladematDocumentEntity getAkladematEntity(int entityId, DocType docType) throws Exception {
//		XmlAction op = new XmlAction(createArguments(entityId, docType), IAkladematManager.ActionType.AKLADEMAT_GET_ENTITY);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return xml != null && !xml.isEmpty() ? (AkladematDocumentEntity) xstream.fromXML(xml) : null;
//	}
//
//	@Override
//	public AkladematDocumentEntity getAkladematEntity(int id) throws Exception {
//		XmlAction op = new XmlAction(createArguments(id), IAkladematManager.ActionType.AKLADEMAT_GET_ENTITY_BY_ID);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return xml != null && !xml.isEmpty() ? (AkladematDocumentEntity) xstream.fromXML(xml) : null;
//	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AkladematAdminEntity<? extends IAdminDematObject>> getAkladematAdminEntities(DocType docType) throws Exception {
		XmlAction op = new XmlAction(createArguments(docType), IAkladematManager.ActionType.GET_AKLADEMAT_ADMIN_ENTITIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AkladematAdminEntity<? extends IAdminDematObject>>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public AkladematAdminEntity<? extends IAdminDematObject> getAdminEntitybyDoc(int idDoc, DocType docType) throws Exception {
		XmlAction op = new XmlAction(createArguments(idDoc, docType), IAkladematManager.ActionType.GET_ADMIN_ENTITY_BY_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (AkladematAdminEntity<? extends IAdminDematObject>) xstream.fromXML(xml) : null;
	}

	@Override
	public AkladematSettings saveOrUpdateSettings(AkladematSettings settings) throws Exception {
		XmlAction op = new XmlAction(createArguments(settings), IAkladematManager.ActionType.SAVE_OR_UPDATE_SETTINGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (AkladematSettings) xstream.fromXML(xml) : null;
	}

	@Override
	public AkladematSettings getSettings(AkladematTypeSettings type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), IAkladematManager.ActionType.GET_SETTINGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (AkladematSettings) xstream.fromXML(xml) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Classification> getDeedClassifications(boolean flatStructure) throws Exception {
		XmlAction op = new XmlAction(createArguments(flatStructure), IAkladematManager.ActionType.GET_DEED_CLASSIFICATION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (List<Classification>) xstream.fromXML(xml) : null;
	}

	@Override
	public String createDocument(AkladematAdminEntity<? extends IAdminDematObject> entity) throws Exception {
		XmlAction op = new XmlAction(createArguments(entity), IAkladematManager.ActionType.CREATE_PASTELL_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (String) xstream.fromXML(xml) : null;
	}

	@Override
	public PastellResult modifyAndUploadDocument(AkladematAdminEntity<? extends IAdminDematObject> entity) throws Exception {
		XmlAction op = new XmlAction(createArguments(entity), IAkladematManager.ActionType.MODIFY_AND_UPLOAD_PASTELL_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (PastellResult) xstream.fromXML(xml) : null;
	}

	@Override
	public InputStream getFileStream(String pastellId, FileType fileType, int index) throws Exception {
		XmlAction op = new XmlAction(createArguments(pastellId, fileType, index), IAkladematManager.ActionType.GET_FILE);
		return httpCommunicator.executeActionAsStream(op, xstream.toXML(op), false);
	}

	@Override
	public Map<AkladematAdminEntity<Chorus>, String> pushChorus(Tree selectedFolder, List<AkladematAdminEntity<Chorus>> entities, boolean toAccounting) throws Exception {
		XmlAction op = new XmlAction(createArguments(selectedFolder, entities, toAccounting), IAkladematManager.ActionType.PUSH_CHORUS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (Map<AkladematAdminEntity<Chorus>, String>) xstream.fromXML(xml) : null;
	}

	@Override
	public void actionOnAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> entity, Status action, User currentUser, List<User> users, boolean fromDemo) throws Exception {
		XmlAction op = new XmlAction(createArguments(entity, action, currentUser, users, fromDemo), IAkladematManager.ActionType.ACTION_ON_ENTITY);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void checkChorusBills(DocType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), IAkladematManager.ActionType.CHECK_CHORUS_BILLS);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void pushCocktail(AkladematAdminEntity<Chorus> entity) throws Exception {
		XmlAction op = new XmlAction(createArguments(entity), IAkladematManager.ActionType.PUSH_COCKTAIL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public String searchCocktailEngagement(String numEj) throws Exception {
		XmlAction op = new XmlAction(createArguments(numEj), IAkladematManager.ActionType.SEARCH_COCKTAIL_EJ);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (String) xstream.fromXML(xml) : null;
	}

	@Override
	public List<AkladematAdminEntity<MailEntity>> getMailEntities(int mailServerId, MailFilter filter) throws Exception {
		XmlAction op = new XmlAction(createArguments(mailServerId, filter), IAkladematManager.ActionType.GET_MAIL_ENTITIES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return xml != null && !xml.isEmpty() ? (List<AkladematAdminEntity<MailEntity>>) xstream.fromXML(xml) : null;
	}

	@Override
	public ScanMetaObject getMailAttachements(List<AkladematAdminEntity<MailEntity>> selectedMails) throws Exception {
		XmlAction op = new XmlAction(createArguments(selectedMails), IAkladematManager.ActionType.GET_MAIL_ATTACHEMENTS);
		InputStream is = httpCommunicator.executeActionAsStream(op, xstream.toXML(op));
		return SerializationUtils.deserialize(IOUtils.toByteArray(is));
	}

	@Override
	public void buildMetadata(int docId, AkladematAdminEntity<Chorus> entity) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, entity), IAkladematManager.ActionType.BUILD_METADATA);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void sendArchiveOzwillo(HashMap<Archiving, AkladematAdminEntity<Archive>> entities) throws Exception {
		XmlAction op = new XmlAction(createArguments(entities), IAkladematManager.ActionType.SEND_ARCHIVE_OZWILLO);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void rejectCocktail(AkladematAdminEntity<Chorus> entity, String com) throws Exception {
		XmlAction op = new XmlAction(createArguments(entity, com), IAkladematManager.ActionType.REJECT_COCKTAIL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
}
