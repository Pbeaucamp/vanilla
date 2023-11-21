package bpm.document.management.core;

import java.io.InputStream;
import java.util.List;

import bpm.document.management.core.model.ArchiveDescriptor;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.FileType;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.SignParameters;
import bpm.document.management.core.model.UserDrivers;
import bpm.document.management.core.model.Versions;
import bpm.document.management.core.xstream.IXmlActionType;

/**
 * 
 * This interface hold any method managing documents (Some method may still
 * exist in IVdmManager and will be migrated at a later time)
 * 
 */
public interface IDocumentManager {

	public static final String PARAM_ACTION = "bpm.document.param.action";
	public static final String PARAM_PARENT_ID = "bpm.document.param.parentId";
	public static final String PARAM_ITEM_ID = "bpm.document.param.itemId";
	public static final String PARAM_DOCUMENT_NAME = "bpm.document.param.documentName";
	public static final String PARAM_DOCUMENT_SIZE = "bpm.document.param.documentSize";
	public static final String PARAM_VERSION_MAJOR = "bpm.document.param.versionMajor";

	public enum ActionType implements IXmlActionType {
		UPLOAD_DOCUMENT, POST_PROCESS_DOCUMENT, UPLOAD_NEW_VERSION, COMEBACK_TO_VERSION, UPLOAD_FILE,
		SIGN_PDF, GET_GDRIVE_DOCS, SAVE_TO_GDRIVE, GET_FACEBOOK_DOCS, SAVE_TO_FACEBOOK, CONNECT_TO_DROPBOX, GET_DROPBOX_DOCS, SAVE_TO_DROPBOX,
		ANALYSE_ZIP, SAVE_XAKL, ARCHIVE_DESCRIPTOR, RESTORE_ARCHIVE, UNZIP_FILE, SAVE_URL, SCAN_DOCUMENT, CREATE_DOCUMENT_FROM_MODEL
	}

	public enum TypeThumbnail {
		AKLABOX, DRIVERS
	}

	/**
	 * 
	 * This method is the new main method to create a document with his
	 * thumbnails It checks if the platform allows the file (by his extension)
	 * It replace any work done in web application's server parts
	 * 
	 * @param parentId
	 * @param fileName
	 * @param fileSize
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public Documents uploadDocument(int parentId, String fileName, Double fileSize, InputStream file) throws Exception;

	/**
	 * This method is the new main method to upload a new version It checks if
	 * the platform allows the file (by his extension) It replace any work done
	 * in web application's server parts
	 * 
	 * @param docId
	 * @param fileName
	 * @param fileSize
	 * @param isMajor
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public Documents uploadNewVersion(int docId, String fileName, Double fileSize, boolean isMajor, InputStream file) throws Exception;

	/**
	 * This method upload a new file without a document.
	 * It returns the filePath
	 * 
	 * @param name
	 * @param fileSize
	 * @param fileStream
	 * @return The filePath of the created file
	 * @throws Exception
	 */
	public String uploadFile(String name, Double fileSize, InputStream fileStream) throws Exception;
	
	/**
	 * This method define the selected version as the current version for the document
	 * (It add a new version)
	 * 
	 * @param docId
	 * @param selectedVersion
	 * @return The newly created versions
	 * @throws Exception
	 */
	public Versions comebackToVersion(int docId, Versions selectedVersion) throws Exception;

	/**
	 * This method is use to manage all actions after a file upload. We manage
	 * here workflows, notifications, emails
	 * 
	 * @throws Exception
	 */
	public Documents postProcessDocument(Enterprise enterpriseParent, int documentId) throws Exception;
	
	public void signDocPdf(int documentId, SignParameters params) throws Exception;
	
	public List<Documents> getGDriveDocs(UserDrivers userDrivers) throws Exception;
	
	public List<Documents> saveToGDrive(int parentId, List<Documents> docs, UserDrivers userDrivers) throws Exception;
	
	public List<Documents> getFacebookDocs(UserDrivers userDrivers, String userToken) throws Exception;
	
	public List<Documents> saveToFacebook(int parentId, List<Documents> docs, UserDrivers userDrivers) throws Exception;
	
	public String connectToDropbox(UserDrivers userDrivers) throws Exception;
	
	public List<Documents> getDropboxDocs(UserDrivers userDrivers, String code) throws Exception;
	
	public List<Documents> saveToDropbox(int parentId, List<Documents> docs) throws Exception;

	public Documents saveUrl(int parentId, Documents document) throws Exception;
	
	public Documents scannedDocument(Documents doc) throws Exception;
	
	public Documents createDocumentFromModel(int parentId, String documentName, int modelDocumentId) throws Exception;

	
	//TODO: REFACTOR ZIP - The following methods has been move without change from the webapp server to the runtime
	//They will need rewriting somedays. TO use the propers thumbnail creation methods and save documents. But the all system needs to be rework for zip and xakl
	
	public void analyzeZip(int docId, int parentId) throws Exception;

	public Documents saveXakl(String name, String description, List<IObject> objects, int fileId, int versionNumber, boolean isTransfer, boolean isAnalyze, FileType fileType, int typeXakl) throws Exception;
	
	public ArchiveDescriptor extractDescriptor(String filePath) throws Exception;
	
	public List<Documents> restoreArchive(int parentId, String zipFilePath, ArchiveDescriptor selectedArchive) throws Exception;
	
	public List<Documents> unzipFile(int parentId, String filePath) throws Exception;
}
