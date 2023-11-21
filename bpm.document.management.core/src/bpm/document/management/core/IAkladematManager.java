package bpm.document.management.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import bpm.document.management.core.model.aklademat.AkladematSettings;
import bpm.document.management.core.model.aklademat.AkladematTypeSettings;
import bpm.document.management.core.model.aklademat.Classification;
import bpm.document.management.core.model.aklademat.PastellFile.FileType;
import bpm.document.management.core.model.aklademat.PastellResult;
import bpm.document.management.core.xstream.IXmlActionType;

public interface IAkladematManager {

	public enum ActionType implements IXmlActionType {
		GET_AKLADEMAT_ADMIN_ENTITY_BY_ID, ADD_AKLADEMAT_ADMIN_ENTITY, UPDATE_AKLADEMAT_ADMIN_ENTITY, DELETE_AKLADEMAT_ADMIN_ENTITY, GET_AKLADEMAT_ADMIN_ENTITIES,
		GET_ADMIN_ENTITY_BY_DOC, SAVE_OR_UPDATE_SETTINGS, GET_SETTINGS, GET_DEED_CLASSIFICATION, CREATE_PASTELL_DOCUMENT, MODIFY_AND_UPLOAD_PASTELL_DOCUMENT, GET_FILE,
		PUSH_CHORUS, ACTION_ON_ENTITY, CHECK_CHORUS_BILLS, PUSH_COCKTAIL, SEARCH_COCKTAIL_EJ, GET_MAIL_ENTITIES, GET_MAIL_ATTACHEMENTS,
		BUILD_METADATA, SEND_ARCHIVE_OZWILLO, REJECT_COCKTAIL
	}

	public AkladematAdminEntity<? extends IAdminDematObject> getAkladematAdminEntityById(int id) throws Exception;

	public AkladematAdminEntity<? extends IAdminDematObject> addAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws Exception;

	public void updateAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws Exception;

	public void deletAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws Exception;

//	public AkladematDocumentEntity addAkladematEntity(AkladematDocumentEntity entity) throws Exception;
	
//	public void removeAkladematEntity(AkladematDocumentEntity entity) throws Exception;
	
//	public List<AkladematDocumentEntity> getAkladematEntities(DocType docType, Status status) throws Exception;

//	public AkladematDocumentEntity getAkladematEntity(int entityId, DocType docType) throws Exception;
	
//	public AkladematDocumentEntity getAkladematEntity(int id) throws Exception;
	
	public List<AkladematAdminEntity<? extends IAdminDematObject>> getAkladematAdminEntities(DocType docType) throws Exception;

	public AkladematAdminEntity<? extends IAdminDematObject> getAdminEntitybyDoc(int idDoc, DocType type) throws Exception;
	
	public AkladematSettings saveOrUpdateSettings(AkladematSettings settings) throws Exception;
	
	public AkladematSettings getSettings(AkladematTypeSettings type) throws Exception;
	
	public List<Classification> getDeedClassifications(boolean flatStructure) throws Exception;
	
	public String createDocument(AkladematAdminEntity<? extends IAdminDematObject> entity) throws Exception;
	
	public PastellResult modifyAndUploadDocument(AkladematAdminEntity<? extends IAdminDematObject> entity) throws Exception;
	
	public InputStream getFileStream(String pastellId, FileType fileType, int index) throws Exception;

	public Map<AkladematAdminEntity<Chorus>, String> pushChorus(Tree selectedFolder, List<AkladematAdminEntity<Chorus>> entities, boolean toAccounting) throws Exception;
	
	public void actionOnAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> entity, Status action, User currentUser, List<User> users, boolean fromDemo) throws Exception;
	
	public void checkChorusBills(DocType type) throws Exception;

	public void pushCocktail(AkladematAdminEntity<Chorus> entity) throws Exception;

	public String searchCocktailEngagement(String numEj) throws Exception;

	public List<AkladematAdminEntity<MailEntity>> getMailEntities(int mailServerId, MailFilter filter) throws Exception;

	public ScanMetaObject getMailAttachements(List<AkladematAdminEntity<MailEntity>> selectedMails) throws Exception;
	
	public void buildMetadata(int docId, AkladematAdminEntity<Chorus> entity) throws Exception;

	public void sendArchiveOzwillo(HashMap<Archiving, AkladematAdminEntity<Archive>> entities) throws Exception;

	public void rejectCocktail(AkladematAdminEntity<Chorus> entity, String com) throws Exception;
}
