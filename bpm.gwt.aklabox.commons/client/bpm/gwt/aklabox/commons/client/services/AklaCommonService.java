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
import bpm.document.management.core.model.Archiving;
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
import bpm.document.management.core.model.Archive;
import bpm.document.management.core.model.Chorus;
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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("aklaCommonService")
public interface AklaCommonService extends RemoteService {
	
	public static class Connect{
		private static AklaCommonServiceAsync instance;
		public static AklaCommonServiceAsync getService(){
			if(instance == null){
				instance = (AklaCommonServiceAsync) GWT.create(AklaCommonService.class);
			}
			return instance;
		}
	}

	public Integer manageItem(Serializable item, ManagerAction action) throws ServiceException;
	
	public List<LOV> getAllLov() throws ServiceException;

	public List<String> getColumns(SourceConnection connection, String tableName) throws ServiceException;

	public List<String> getTables(SourceConnection connection) throws ServiceException;

	public List<SourceConnection> getAllConnections() throws ServiceException;

	public LOV saveLOV(LOV lov) throws ServiceException;

	public void updateLOV(LOV lov) throws ServiceException;

	public void deleteLOV(LOV lov) throws ServiceException;

	public List<String> getLisDriver() throws ServiceException;

	public SourceConnection saveSourceConnection(SourceConnection connection) throws ServiceException;

	public Boolean testConnection(SourceConnection connection) throws ServiceException;

	public HashMap<String, String> getAllLovByTableCode(LOV tableCode) throws ServiceException;

	public String scan() throws ServiceException;

	public Boolean isScannedImageReady(String fileName) throws ServiceException;

	public List<FormField> getAllAklaBoxFormFields(/*int aklaBoxServerId,*/ int formId) throws ServiceException;

	public List<FormCellLink> getAllFormCellLinksbyFormCell(FormCell cell) throws ServiceException;

	public List<FormCell> getAllFormCells(StandardForm form) throws ServiceException;

	public List<AklaBoxServer> getAllAklaBoxServers(User user) throws ServiceException;

	public List<Form> getAllAklaBoxForm(/*int aklaBoxServerId*/) throws ServiceException;

	public StandardForm saveStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList) throws ServiceException;

	public void updateStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList) throws ServiceException;

	public void updateFormCellResult(FormCellResult cellResult) throws Exception;

	public FormCellResult getFormCellResult(Documents doc, FormCell cell) throws Exception;

	public List<Tree> getFoldersPerEnterprise(int enterpriseId, boolean lightWeight) throws Exception;
	
	public List<Tree> getFoldersPerEnterprise(AklaboxConnection server, int enterpriseId) throws ServiceException;

	public List<Enterprise> getEnterprisePerUser(String email) throws Exception;
	
	public List<Enterprise> getEnterprisePerUser(AklaboxConnection server, String email) throws ServiceException;
	
	public List<IObject> getDirectoryContent(int parentId) throws Exception;
	
	public List<IObject> getDirectoryContent(AklaboxConnection server, int parentId) throws ServiceException;

	public void analyzeForm(Documents doc) throws Exception;
	
	public void saveComments(List<Comments> comments) throws Exception;

	public Map<AkLadExportObject, String> saveDocumentsWithAnalyse(List<AkLadExportObject> docs, boolean sendToAccounting) throws Exception;
	
	public void saveFormTextTemplate(StandardForm standardForm) throws Exception;

	public String exportWorkspace(List<AkLadExportObject> docs) throws Exception;

	public List<User> getUsersFromOrganigramme(int folderId) throws Exception;

	public Documents saveDocument(Documents doc, List<Tasks> task, List<FormFieldValue> fieldValues, Tasks taskDelegated) throws Exception;

	public List<Archiving> getAllArchiving() throws Exception;

	public AkladematAdminEntity<? extends IAdminDematObject> getAkladematAdminEntityById(int id) throws Exception;

	public AkladematAdminEntity<? extends IAdminDematObject> addoreditAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws Exception;

	public AkladematAdminEntity<? extends IAdminDematObject> getAdminEntitybyDoc(int idDoc, DocType type) throws ServiceException;

	public boolean pushToIparapheur(int docId) throws ServiceException;
	
	public int[] getImageSize(String path) throws ServiceException;
	
	public List<Classification> getDeedClassifications(boolean flatStructure) throws ServiceException;
	
	public List<Log<? extends ILog>> getLogs(LogType type) throws ServiceException;
	
	public EntityUserHelper getUsersForEntity(Documents document, AkladematAdminEntity<? extends IAdminDematObject> entity, Integer financeServiceId) throws ServiceException;

	public void actionOnAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> entity, Status action, ItemValidation validation, User currentUser, List<User> users, boolean fromDemo, String messageAsk) throws ServiceException;

	public HashMap<Documents, AkladematAdminEntity<? extends IAdminDematObject>> getAdminEntities(List<Documents> docs) throws ServiceException;
	
	public List<IObject> getItemPerEnterpriseFolder(Enterprise enterprise, Tree parentFolder, boolean lightWeight) throws ServiceException;
	
	public void manageEntities(List<AkladematAdminEntity<? extends IAdminDematObject>> selectedEntitites, Status action, boolean fromDemo) throws ServiceException;

	public void addoreditAkladematAdminEntities(List<AkladematAdminEntity<? extends IAdminDematObject>> entities) throws ServiceException;

	public List<User> searchUsers(String filter) throws ServiceException;

	public void notifyEnterpriseAdmins(Enterprise enterprise, IObject item, String message) throws ServiceException;

	public List<User> getUserEnterprise(Enterprise selectedEnterprise, TypeUser typeUser) throws ServiceException;

	public HashMap<User, List<TypeUser>> getEnterpriseUsers(int folderId) throws ServiceException;
	
	public void notifyUser(User notifyUser, IObject item, String message) throws ServiceException;
	
	public boolean checkIfFormsAreComplete(Documents document, LinkType linkType) throws ServiceException;

	public void requestValidation(List<User> validators, Tree item, String messageReceive, int delay) throws ServiceException;

	public void requestValidation(List<User> validators, AkladematAdminEntity<? extends IAdminDematObject> item, String messageReceive, int delay) throws ServiceException;
	
	public List<ItemValidation> getItemValidations(int itemId, ItemType type) throws ServiceException;

	public void validateItem(ItemValidation validation, Enterprise enterprise, Tree item, String messageAsk, String messageReceive) throws ServiceException;
	
	public Documents postProcessDocument(Enterprise enterpriseParent, int documentId) throws ServiceException;

	public boolean hasForms(Documents document, LinkType linkType) throws ServiceException;
	
	public List<Versions> getVersions(Documents doc) throws ServiceException;

	public List<DocPages> getPages(int docId, int versionNumber) throws ServiceException;

	public Versions getSpecificVersion(Versions version, String email, boolean isDownload) throws ServiceException;

	public List<OCRSearchResult> searchDocContent(String query, int docId, int versionNumber) throws ServiceException;

	public List<Comments> getComments(int docId, CommentStatus status) throws ServiceException;

	public void generateVideoThumb(int versionNumber, int sec, Documents doc) throws ServiceException;

	public Documents getDocInfo(int docId) throws ServiceException;

	public Tree getDirectoryInfo(int id) throws ServiceException;

	public void updateDocStatus(Documents doc) throws ServiceException;

	public void treatImage(TreatmentImageObject treatment, Documents doc) throws ServiceException;

	public void returnToOriginal(Documents doc) throws ServiceException;

	public void removeComment(Comments comment) throws Exception;

	public void updateComment(Comments comment) throws Exception;

	public void saveComment(Comments comment, String userType) throws Exception;

	public User getUserInfo(String email) throws Exception;

	public String searchCocktailEngagement(String numEj) throws Exception;
	
	public List<IObject> getItems(Integer parentId, PermissionItem permissionParent, ItemTreeType treeItemType, IObject.ItemType type, Boolean isDeleted, Boolean isActivated, boolean checkPermission, boolean loadChilds) throws ServiceException;

	public List<IObject> getItems(Tree parent, ItemTreeType itemTreeType) throws ServiceException;
	
	public PermissionItem managePermissionItem(IObject item, PermissionItem permissionItem, ManagerAction action, ShareType shareType, List<Permission> permissions, boolean savePermission) throws ServiceException;
	
	public List<Permission> getPermissions(int permissionItemId, ShareType type, boolean lightWeight) throws ServiceException;

	public PermissionItem getPermissionItemByHash(String hash) throws ServiceException;

	public List<Group> getGroups() throws Exception;

	public List<Group> getGroupsByUser(String email) throws Exception;
	
	public List<User> getUsersByGroup(List<Group> groups, Integer enterpriseId) throws ServiceException;
	
	public IObject getItem(int itemId, IObject.ItemType type, ItemTreeType itemTreeType, Boolean isDeleted, boolean checkPermission, boolean loadParent, boolean lightWeight) throws ServiceException;
	
	public void saveMailServers(List<MailServer> mailServers) throws ServiceException;
	
	public List<MailServer> getMailServers() throws ServiceException;
	
	public List<AkladematAdminEntity<MailEntity>> getMailEntities(int mailServerId, MailFilter filter) throws ServiceException;
	
	public List<Form> getOrbeonForms() throws ServiceException;
	
	public List<OrbeonFormSection> getOrbeonFormSection(Form selectedObject) throws ServiceException;

	public List<Form> getAllFormByType(FormType type) throws ServiceException;

	public List<OrganigramElement> getOrganigram() throws Exception;
	
	public List<Workflow> getAllWorkflows(Type type) throws ServiceException;

	public List<bpm.gwt.aklabox.commons.shared.Log> saveInstance(Instance instance, Workflow workflow, User user) throws Exception;

	public Workflow getWorkflow(int workflowId) throws Exception;
	
	public String getOrbeonUrl(Documents doc) throws Exception;

	public List<Instance> getAllInstanceByUser() throws Exception; 

	public List<bpm.gwt.aklabox.commons.shared.Log> updateInstance(Instance instance) throws Exception;

	public Tree getAllAklaboxFiles(Activity activity, int versionNumber) throws Exception;

	public List<Documents> extractZipFromAklaFlow(UnzipFileActivity unzipFileActivity, int versionNumber) throws ServiceException;

	public List<Documents> extractZip(Documents doc, int fileId) throws ServiceException;

	public List<Activity> getOrbeonNextActivity(Workflow workflow, Instance instance) throws Exception;

	public List<User> getAllUsers() throws Exception;

	public List<Activity> getActivitiesByWorkflow(Workflow workflow) throws Exception;

	public Instance getLastWorkflowInstance(Workflow workflow) throws Exception;
	
	public List<City> getCities(int depId) throws Exception;
	
	public List<Departement> getDepartements(int countryId) throws Exception;

	public List<TypeTask> getTaskTypes() throws Exception;

	public void saveFormField(FormField formField) throws Exception;
	
	public void deleteFormField(FormField formField) throws Exception;

	public List<FormField> getAllFormField(int formId) throws Exception;

	public void saveFormFieldValue(int docId, List<FormFieldValue> values) throws Exception;

	public void updateForm(Form form) throws Exception;

	public FormFieldValue getFormFieldValueByDoc(int ffId, int userId, int docId) throws ServiceException;

	public void addListValue(LOV listOfValues, String value) throws Exception;

	public List<Country> getCountry() throws Exception;

	public Form getForm(int formId) throws Exception;

	public FileType getFileType(int fileTypeId) throws Exception;

	public void sendDelegationNotification(int userId, Instance instance) throws Exception;

	public void sendEmails(List<EmailInfo> emails) throws Exception;
	
	public void addTask(Documents documents, User u, Tasks t, String message) throws Exception;
	
	public Instance getInstanceById(int id) throws Exception;
	
	public void sendArchiveOzwillo(HashMap<Archiving, AkladematAdminEntity<Archive>> entities) throws Exception;

	public List<String> getColumnsbyRequest(SourceConnection connection, String request) throws Exception;

	public List<FileType> getAllFileTypes(AklaboxConnection server) throws Exception;

	public List<FormFieldValue> getFormValuesFromSource(int docId, Form form, List<FormFieldValue> filters) throws Exception;

	public Enterprise getEnterpriseParent(int folderId) throws ServiceException;

	public Enterprise getEnterpriseParent(int folderId, AklaboxConnection server) throws Exception;
	
	public void rejectCocktail(AkladematAdminEntity<Chorus> entity, String text) throws Exception;
}