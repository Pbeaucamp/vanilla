package bpm.document.management.remote;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import bpm.document.management.core.IDocumentManager;
import bpm.document.management.core.model.ArchiveDescriptor;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.FileType;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.SignParameters;
import bpm.document.management.core.model.UserDrivers;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.core.model.Versions;
import bpm.document.management.core.xstream.XmlAction;
import bpm.document.management.core.xstream.XmlArgumentsHolder;
import bpm.document.management.remote.internal.HttpCommunicator;
import bpm.vanilla.platform.xstream.DateConverter;

import com.thoughtworks.xstream.XStream;

public class RemoteDocumentManager implements IDocumentManager {
	
	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private XStream xstream;

	public RemoteDocumentManager(VdmContext ctx) {
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

	@Override
	public Documents uploadDocument(int parentId, String fileName, Double fileSize, InputStream file) throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(IDocumentManager.PARAM_ACTION, IDocumentManager.ActionType.UPLOAD_DOCUMENT.toString());
		params.put(IDocumentManager.PARAM_PARENT_ID, String.valueOf(parentId));
		params.put(IDocumentManager.PARAM_DOCUMENT_NAME, fileName);
		params.put(IDocumentManager.PARAM_DOCUMENT_SIZE, String.valueOf(fileSize));
		
		String xml = httpCommunicator.executeActionThroughServlet(params, file);
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public Documents uploadNewVersion(int docId, String fileName, Double fileSize, boolean isMajor, InputStream file) throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(IDocumentManager.PARAM_ACTION, IDocumentManager.ActionType.UPLOAD_NEW_VERSION.toString());
		params.put(IDocumentManager.PARAM_ITEM_ID, String.valueOf(docId));
		params.put(IDocumentManager.PARAM_DOCUMENT_NAME, fileName);
		params.put(IDocumentManager.PARAM_DOCUMENT_SIZE, String.valueOf(fileSize));
		params.put(IDocumentManager.PARAM_VERSION_MAJOR, String.valueOf(isMajor));
		
		String xml = httpCommunicator.executeActionThroughServlet(params, file);
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public String uploadFile(String fileName, Double fileSize, InputStream file) throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(IDocumentManager.PARAM_ACTION, IDocumentManager.ActionType.UPLOAD_FILE.toString());
		params.put(IDocumentManager.PARAM_DOCUMENT_NAME, fileName);
		params.put(IDocumentManager.PARAM_DOCUMENT_SIZE, String.valueOf(fileSize));
		
		String xml = httpCommunicator.executeActionThroughServlet(params, file);
		return (String) xstream.fromXML(xml);
	}

//	@Override
//	public String createPDFFromDWG(String fileName, String filePath) throws Exception {
//		XmlAction op = new XmlAction(createArguments(fileName, filePath), IDocumentManager.ActionType.CREATE_PDF_FROM_DWG);
//		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
//		return (String) xstream.fromXML(xml);
//	}

	@Override
	public Documents postProcessDocument(Enterprise enterpriseParent, int documentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(enterpriseParent, documentId), IDocumentManager.ActionType.POST_PROCESS_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public Versions comebackToVersion(int docId, Versions selectedVersion) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, selectedVersion), IDocumentManager.ActionType.COMEBACK_TO_VERSION);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Versions) xstream.fromXML(xml);
	}

	@Override
	public void signDocPdf(int documentId, SignParameters params) throws Exception {
		XmlAction op = new XmlAction(createArguments(documentId, params), IDocumentManager.ActionType.SIGN_PDF);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> getGDriveDocs(UserDrivers userDrivers) throws Exception {
		XmlAction op = new XmlAction(createArguments(userDrivers), IDocumentManager.ActionType.GET_GDRIVE_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> saveToGDrive(int parentId, List<Documents> docs, UserDrivers userDrivers) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, docs, userDrivers), IDocumentManager.ActionType.SAVE_TO_GDRIVE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> getFacebookDocs(UserDrivers userDrivers, String userToken) throws Exception {
		XmlAction op = new XmlAction(createArguments(userDrivers, userToken), IDocumentManager.ActionType.GET_FACEBOOK_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> saveToFacebook(int parentId, List<Documents> docs, UserDrivers userDrivers) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, docs, userDrivers), IDocumentManager.ActionType.SAVE_TO_FACEBOOK);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public String connectToDropbox(UserDrivers userDrivers) throws Exception {
		XmlAction op = new XmlAction(createArguments(userDrivers), IDocumentManager.ActionType.CONNECT_TO_DROPBOX);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> getDropboxDocs(UserDrivers userDrivers, String code) throws Exception {
		XmlAction op = new XmlAction(createArguments(userDrivers, code), IDocumentManager.ActionType.GET_DROPBOX_DOCS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> saveToDropbox(int parentId, List<Documents> docs) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, docs), IDocumentManager.ActionType.SAVE_TO_DROPBOX);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public void analyzeZip(int docId, int parentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(docId, parentId), IDocumentManager.ActionType.ANALYSE_ZIP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public Documents saveXakl(String name, String description, List<IObject> objects, int fileId, int versionNumber, boolean isTransfer, boolean isAnalyze, FileType fileType, int typeXakl) throws Exception {
		XmlAction op = new XmlAction(createArguments(name, description, objects, fileId, versionNumber, isTransfer, isAnalyze, fileType, typeXakl), IDocumentManager.ActionType.SAVE_XAKL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public ArchiveDescriptor extractDescriptor(String filePath) throws Exception {
		XmlAction op = new XmlAction(createArguments(filePath), IDocumentManager.ActionType.ARCHIVE_DESCRIPTOR);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ArchiveDescriptor) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> restoreArchive(int parentId, String zipFilePath, ArchiveDescriptor selectedArchive) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, zipFilePath, selectedArchive), IDocumentManager.ActionType.RESTORE_ARCHIVE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Documents> unzipFile(int parentId, String filePath) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, filePath), IDocumentManager.ActionType.UNZIP_FILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Documents>) xstream.fromXML(xml);
	}

	@Override
	public Documents saveUrl(int parentId, Documents document) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, document), IDocumentManager.ActionType.SAVE_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}

	@Override
	public Documents scannedDocument(Documents doc) throws Exception {
		XmlAction op = new XmlAction(createArguments(doc), IDocumentManager.ActionType.SCAN_DOCUMENT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}
	
	public Documents createDocumentFromModel(int parentId, String documentName, int modelDocumentId) throws Exception {
		XmlAction op = new XmlAction(createArguments(parentId, documentName, modelDocumentId), IDocumentManager.ActionType.CREATE_DOCUMENT_FROM_MODEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Documents) xstream.fromXML(xml);
	}
}
