package bpm.gwt.aklabox.commons.client.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.Workflow;
import bpm.aklabox.workflow.core.model.Workflow.Type;
import bpm.aklabox.workflow.core.model.activities.Activity;
import bpm.aklabox.workflow.core.model.activities.UnzipFileActivity;
import bpm.aklabox.workflow.core.model.resources.AkLadExportObject;
import bpm.aklabox.workflow.core.model.resources.AklaBoxServer;
import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.FormCellLink;
import bpm.aklabox.workflow.core.model.resources.FormCellResult;
import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.document.management.core.model.AkladematAdminEntity;
import bpm.document.management.core.model.AkladematAdminEntity.DocType;
import bpm.document.management.core.model.AkladematAdminEntity.Status;
import bpm.document.management.core.model.Archive;
import bpm.document.management.core.model.Archiving;
import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.City;
import bpm.document.management.core.model.Comments;
import bpm.document.management.core.model.Comments.CommentStatus;
import bpm.document.management.core.model.Country;
import bpm.document.management.core.model.Departement;
import bpm.document.management.core.model.DocPages;
import bpm.document.management.core.model.Documents;
import bpm.document.management.core.model.EmailInfo;
import bpm.document.management.core.model.Enterprise;
import bpm.document.management.core.model.Enterprise.TypeUser;
import bpm.document.management.core.model.FileType;
import bpm.document.management.core.model.Form;
import bpm.document.management.core.model.Form.FormType;
import bpm.document.management.core.model.FormField;
import bpm.document.management.core.model.FormFieldValue;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.IAdminDematObject;
import bpm.document.management.core.model.ILog;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.ItemValidation;
import bpm.document.management.core.model.ItemValidation.ItemType;
import bpm.document.management.core.model.LOV;
import bpm.document.management.core.model.Log;
import bpm.document.management.core.model.Log.LogType;
import bpm.document.management.core.model.MailEntity;
import bpm.document.management.core.model.MailFilter;
import bpm.document.management.core.model.MailServer;
import bpm.document.management.core.model.ManagerAction;
import bpm.document.management.core.model.MetadataLink.LinkType;
import bpm.document.management.core.model.OrbeonFormSection;
import bpm.document.management.core.model.OrganigramElement;
import bpm.document.management.core.model.Permission;
import bpm.document.management.core.model.Permission.ShareType;
import bpm.document.management.core.model.PermissionItem;
import bpm.document.management.core.model.SourceConnection;
import bpm.document.management.core.model.Tasks;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.TypeTask;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.Versions;
import bpm.document.management.core.model.aklademat.Classification;
import bpm.document.management.core.utils.TreatmentImageObject;
import bpm.gwt.aklabox.commons.client.services.exceptions.ServiceException;
import bpm.gwt.aklabox.commons.shared.AklaboxConnection;
import bpm.gwt.aklabox.commons.shared.EntityUserHelper;
import bpm.gwt.aklabox.commons.shared.OCRSearchResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AklaCommonServiceAsync {
	
	public void manageItem(Serializable item, ManagerAction action, AsyncCallback<Integer> callback);

	public void getAllLov(AsyncCallback<List<LOV>> callback);

	public void getColumns(SourceConnection connection, String tableName, AsyncCallback<List<String>> callback);

	public void getTables(SourceConnection connection, AsyncCallback<List<String>> callback);

	public void getAllConnections(AsyncCallback<List<SourceConnection>> callback);

	public void saveLOV(LOV lov, AsyncCallback<LOV> callback);

	public void updateLOV(LOV lov, AsyncCallback<Void> callback);

	public void deleteLOV(LOV lov, AsyncCallback<Void> callback);

	public void getLisDriver(AsyncCallback<List<String>> callback);

	public void saveSourceConnection(SourceConnection connection, AsyncCallback<SourceConnection> callback);

	public void testConnection(SourceConnection connection, AsyncCallback<Boolean> callback);
	
	public void getAllLovByTableCode(LOV tableCode, AsyncCallback<HashMap<String, String>> callback);
	
	public void scan(AsyncCallback<String> callback);

	public void isScannedImageReady(String fileName, AsyncCallback<Boolean> callback);

	public void getAllAklaBoxFormFields(/*int aklaBoxServerId,*/ int formId, AsyncCallback<List<FormField>> callback);

	public void getAllFormCellLinksbyFormCell(FormCell cell, AsyncCallback<List<FormCellLink>> callback);

	public void getAllFormCells(StandardForm form, AsyncCallback<List<FormCell>> callback);

	public void getAllAklaBoxServers(User user, AsyncCallback<List<AklaBoxServer>> callback);

	public void getAllAklaBoxForm(/*int aklaBoxServerId,*/ AsyncCallback<List<Form>> callback);

	public void saveStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList, AsyncCallback<StandardForm> callback);
	
	public void updateStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList, AsyncCallback<Void> callback);
	
	public void updateFormCellResult(FormCellResult cellResult, AsyncCallback<Void> callback);

	public void getFormCellResult(Documents doc, FormCell cell, AsyncCallback<FormCellResult> callback);

	public void getFoldersPerEnterprise(int enterpriseId, boolean lightWeight, AsyncCallback<List<Tree>> callback);
	
	public void getFoldersPerEnterprise(AklaboxConnection server, int enterpriseId, AsyncCallback<List<Tree>> callback);

	public void getEnterprisePerUser(String email, AsyncCallback<List<Enterprise>> callback);
	
	public void getEnterprisePerUser(AklaboxConnection server, String email, AsyncCallback<List<Enterprise>> callback);

	public void getDirectoryContent(int parentId, AsyncCallback<List<IObject>> callback);
	
	public void getDirectoryContent(AklaboxConnection server, int parentId, AsyncCallback<List<IObject>> callback);

	public void analyzeForm(Documents doc, AsyncCallback<Void> callback);

	public void saveComments(List<Comments> comments, AsyncCallback<Void> callback);
	
	public void saveDocumentsWithAnalyse(List<AkLadExportObject> docsComments, boolean sendToAccounting, AsyncCallback<Map<AkLadExportObject, String>> callback);

	public void saveFormTextTemplate(StandardForm standardForm, AsyncCallback<Void> callback);

	public void exportWorkspace(List<AkLadExportObject> docs, AsyncCallback<String> callback);
	
	public void getUsersFromOrganigramme(int folderId, AsyncCallback<List<User>> callback);
	
	public void saveDocument(Documents doc, List<Tasks> tasks, List<FormFieldValue> fieldValues, Tasks taskDelegated, AsyncCallback<Documents> callback);

	public void getAllArchiving(AsyncCallback<List<Archiving>> callback);
	
	public void getAkladematAdminEntityById(int id, AsyncCallback<AkladematAdminEntity<? extends IAdminDematObject>> callback);

	public void addoreditAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity, AsyncCallback<AkladematAdminEntity<? extends IAdminDematObject>> callback);
	
	public void getAdminEntitybyDoc(int idDoc, DocType type, AsyncCallback<AkladematAdminEntity<? extends IAdminDematObject>> callback);

	public void pushToIparapheur(int docId, AsyncCallback<Boolean> callback);

	public void getImageSize(String path, AsyncCallback<int[]> callback);
	
	public void getDeedClassifications(boolean flatStructure, AsyncCallback<List<Classification>> callback);
	
	public void getLogs(LogType type, AsyncCallback<List<Log<? extends ILog>>> callback);
	
	public void getUsersForEntity(Documents document, AkladematAdminEntity<? extends IAdminDematObject> entity, Integer financeServiceId, AsyncCallback<EntityUserHelper> callback);

	public void actionOnAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> entity, Status action, ItemValidation validation, User currentUser, List<User> users, boolean fromDemo, String messageAsk, AsyncCallback<Void> callback);

	public void getAdminEntities(List<Documents> docs, AsyncCallback<HashMap<Documents, AkladematAdminEntity<? extends IAdminDematObject>>> callback);
	
	public void getItemPerEnterpriseFolder(Enterprise enterprise, Tree parentFolder, boolean lightWeight, AsyncCallback<List<IObject>> callback);

	public void manageEntities(List<AkladematAdminEntity<? extends IAdminDematObject>> selectedEntitites, Status action, boolean fromDemo, AsyncCallback<Void> callback);

	public void addoreditAkladematAdminEntities(List<AkladematAdminEntity<? extends IAdminDematObject>> entities, AsyncCallback<Void> callback);

	public void searchUsers(String filter, AsyncCallback<List<User>> callback);

	public void notifyEnterpriseAdmins(Enterprise enterprise, IObject item, String message, AsyncCallback<Void> callback);

	public void getUserEnterprise(Enterprise selectedEnterprise, TypeUser typeUser, AsyncCallback<List<User>> callback);
	
	public void getEnterpriseUsers(int folderId, AsyncCallback<HashMap<User, List<TypeUser>>> callback);
	
	public void notifyUser(User notifyUser, IObject item, String message, AsyncCallback<Void> callback);

	public void checkIfFormsAreComplete(Documents document, LinkType linkType, AsyncCallback<Boolean> callback);

	public void requestValidation(List<User> validators, Tree item, String messageReceive, int delay, AsyncCallback<Void> callback);

	public void requestValidation(List<User> validators, AkladematAdminEntity<? extends IAdminDematObject> item, String messageReceive, int delay, AsyncCallback<Void> callback);
	
	public void getItemValidations(int itemId, ItemType type, AsyncCallback<List<ItemValidation>> callback);

	public void validateItem(ItemValidation validation, Enterprise enterprise, Tree item, String messageAsk, String messageReceive, AsyncCallback<Void> callback);

	public void postProcessDocument(Enterprise enterpriseParent, int documentId, AsyncCallback<Documents> callback);

	public void hasForms(Documents document, LinkType linkType, AsyncCallback<Boolean> callback);

	public void getVersions(Documents doc, AsyncCallback<List<Versions>> callback);

	public void getPages(int docId, int versionNumber, AsyncCallback<List<DocPages>> callback);

	public void getSpecificVersion(Versions version, String email, boolean isDownload, AsyncCallback<Versions> callback);

	public void searchDocContent(String query, int docId, int versionNumber, AsyncCallback<List<OCRSearchResult>> callback);

	public void getComments(int docId, CommentStatus status, AsyncCallback<List<Comments>> callback);

	public void generateVideoThumb(int sec, int versionNumber, Documents doc, AsyncCallback<Void> callback);

	public void getDocInfo(int docId, AsyncCallback<Documents> callback);

	public void getDirectoryInfo(int id, AsyncCallback<Tree> callback);

	public void updateDocStatus(Documents doc, AsyncCallback<Void> callback);

	public void treatImage(TreatmentImageObject treatment, Documents doc, AsyncCallback<Void> callback);

	public void returnToOriginal(Documents doc,	AsyncCallback<Void> asyncCallback);

	public void removeComment(Comments comment,	AsyncCallback<Void> asyncCallback);

	public void updateComment(Comments comment,	AsyncCallback<Void> asyncCallback);

	public void saveComment(Comments comment, String userType,AsyncCallback<Void> asyncCallback);

	public void getUserInfo(String email, AsyncCallback<User> callback);
	
	public void searchCocktailEngagement(String numEj, AsyncCallback<String> callback);

	public void getItems(Integer parentId, PermissionItem permissionParent, ItemTreeType treeItemType, IObject.ItemType type, Boolean isDeleted, Boolean isActivated, boolean checkPermission, boolean loadChilds, AsyncCallback<List<IObject>> callback);

	public void getItems(Tree parent, ItemTreeType itemTreeType, AsyncCallback<List<IObject>> callback);
	
	public void managePermissionItem(IObject item, PermissionItem permissionItem, ManagerAction action, ShareType shareType, List<Permission> permissions, boolean savePermission, AsyncCallback<PermissionItem> callback);
	
	public void getPermissions(int permissionItemId, ShareType type, boolean lightWeight, AsyncCallback<List<Permission>> callback);

	public void getPermissionItemByHash(String hash, AsyncCallback<PermissionItem> callback);

	public void getGroups(AsyncCallback<List<Group>> callback);

	public void getGroupsByUser(String email, AsyncCallback<List<Group>> callback);
	
	public void getUsersByGroup(List<Group> groups, Integer enterpriseId, AsyncCallback<List<User>> callback);
	
	public void getItem(int itemId, IObject.ItemType type, ItemTreeType itemTreeType, Boolean isDeleted, boolean checkPermission, boolean loadParent, boolean lightWeight, AsyncCallback<IObject> callback);

	public void saveMailServers(List<MailServer> mailServers, AsyncCallback<Void> gwtCallbackWrapper);

	public void getMailServers(AsyncCallback<List<MailServer>> asyncCallback);

	public void getMailEntities(int mailServerId, MailFilter filter, AsyncCallback<List<AkladematAdminEntity<MailEntity>>> asyncCallback);

	/**
	 * The forms returned don't exist in the database
	 * They are object created to retrieve orbeon form informations
	 * Don't use this outside of form creation
	 * @param asyncCallback
	 */
	public void getOrbeonForms(AsyncCallback<List<Form>> asyncCallback);

	/**
	 * The sections are retrieved from within orbeon
	 * The object don't exist in the database
	 * Don't use this outside of form creation
	 * @param form
	 * @param asyncCallback
	 */
	public void getOrbeonFormSection(Form form, AsyncCallback<List<OrbeonFormSection>> asyncCallback);
	
	public void getAllFormByType(FormType type, AsyncCallback<List<Form>> asyncCallback);

	public void getOrganigram(AsyncCallback<List<OrganigramElement>> asyncCallback);

	public void getAllWorkflows(Type type, AsyncCallback<List<Workflow>> asyncCallback);

	public void saveInstance(Instance instance, Workflow workflow, User user, AsyncCallback<List<bpm.gwt.aklabox.commons.shared.Log>> callback);
	
	public void getWorkflow(int workflowId, AsyncCallback<Workflow> callback);

	/**
	 * This method will return an url to use directly to show an orbeon form
	 * It will look for workflow/user and handle necessary security elements
	 * @param doc
	 * @param asyncCallback
	 */
	public void getOrbeonUrl(Documents doc, AsyncCallback<String> asyncCallback);

	public void getAllInstanceByUser(AsyncCallback<List<Instance>> callback);

	public void updateInstance(Instance instance, AsyncCallback<List<bpm.gwt.aklabox.commons.shared.Log>> callback);

	public void getAllAklaboxFiles(Activity activity, int versionNumber, AsyncCallback<Tree> callback);

	public void extractZipFromAklaFlow(UnzipFileActivity unzipFileActivity, int versionNumber, AsyncCallback<List<Documents>> callback);

	public void extractZip(Documents doc, int fileId, AsyncCallback<List<Documents>> callback);
	
	public void getOrbeonNextActivity(Workflow workflow, Instance instance, AsyncCallback<List<Activity>> callback);
	
	public void getAllUsers(AsyncCallback<List<User>> callback);
	
	public void getActivitiesByWorkflow(Workflow workflow, AsyncCallback<List<Activity>> callback);

	public void getLastWorkflowInstance(Workflow workflow, AsyncCallback<Instance> callback);

	public void getCities(int depId, AsyncCallback<List<City>> callback);

	public void getDepartements(int countryId, AsyncCallback<List<Departement>> callback);

	public void getTaskTypes(AsyncCallback<List<TypeTask>> asyncCallback);
	
	public void saveFormField(FormField formField, AsyncCallback<Void> callback);
	
	public void deleteFormField(FormField formField, AsyncCallback<Void> callback);
	
	/**
	 * Not working. It calls aklaflow so it not looks in the right tables
	 * I don't get it... This never worked (the "form" tab in openDoc never displays)
	 * @param formId
	 * @param callback
	 */
	
	public void getAllFormField(int formId, AsyncCallback<List<FormField>> callback);

	public void saveFormFieldValue(int docId, List<FormFieldValue> values, AsyncCallback<Void> callback);
	
	public void updateForm(Form form, AsyncCallback<Void> callback);

	public void getFormFieldValueByDoc(int ffId, int userId, int docId, AsyncCallback<FormFieldValue> callback);

	public void addListValue(LOV listOfValues, String value, AsyncCallback<Void> asyncCallback);

	public void getCountry(AsyncCallback<List<Country>> callback);
	
	public void getForm(int formId, AsyncCallback<Form> callback);
	
	public void getFileType(int id, AsyncCallback<FileType> callback);
	
	public void sendDelegationNotification(int userId, Instance instance, AsyncCallback<Void> callback);

	public void sendEmails(List<EmailInfo> emails, AsyncCallback<Void> callback);

	public void addTask(Documents documents, User u, Tasks t, String message, AsyncCallback<Void> asyncCallback);

	public void getInstanceById(int id, AsyncCallback<Instance> asyncCallback);

	public void sendArchiveOzwillo(HashMap<Archiving, AkladematAdminEntity<Archive>> entities, AsyncCallback<Void> asyncCallback);

	public void getColumnsbyRequest(SourceConnection connection, String request, AsyncCallback<List<String>> asyncCallback);
	
	public void getAllFileTypes(AklaboxConnection server, AsyncCallback<List<FileType>> asyncCallback);
	
	public void getFormValuesFromSource(int docId, Form form, List<FormFieldValue> filters, AsyncCallback<List<FormFieldValue>> asyncCallback);
	
	public void getEnterpriseParent(int folderId, AsyncCallback<Enterprise> asyncCallback);
	
	public void getEnterpriseParent(int folderId, AklaboxConnection server, AsyncCallback<Enterprise> asyncCallback);

	public void rejectCocktail(AkladematAdminEntity<Chorus> entity, String text, AsyncCallback<Void> asyncCallback);
}
