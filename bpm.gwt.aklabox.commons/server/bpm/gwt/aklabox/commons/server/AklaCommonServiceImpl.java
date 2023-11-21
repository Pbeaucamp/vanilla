package bpm.gwt.aklabox.commons.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import bpm.aklabox.workflow.core.model.Instance;
import bpm.aklabox.workflow.core.model.OrbeonWorkflowInformation;
import bpm.aklabox.workflow.core.model.Workflow;
import bpm.aklabox.workflow.core.model.Workflow.Type;
import bpm.aklabox.workflow.core.model.WorkflowLog;
import bpm.aklabox.workflow.core.model.activities.Activity;
import bpm.aklabox.workflow.core.model.activities.AklaBoxFiles;
import bpm.aklabox.workflow.core.model.activities.UnzipFileActivity;
import bpm.aklabox.workflow.core.model.resources.AkLadExportObject;
import bpm.aklabox.workflow.core.model.resources.AkLadPageInfo;
import bpm.aklabox.workflow.core.model.resources.AklaBoxServer;
import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.FormCellLink;
import bpm.aklabox.workflow.core.model.resources.FormCellResult;
import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.document.management.core.IVdmManager;
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
import bpm.document.management.core.model.FormField.FormFieldType;
import bpm.document.management.core.model.FormFieldValue;
import bpm.document.management.core.model.Group;
import bpm.document.management.core.model.IAdminDematObject;
import bpm.document.management.core.model.ILog;
import bpm.document.management.core.model.IObject;
import bpm.document.management.core.model.IObject.ItemTreeType;
import bpm.document.management.core.model.ItemValidation;
import bpm.document.management.core.model.ItemValidation.ItemType;
import bpm.document.management.core.model.ItemValidation.ValidationStatus;
import bpm.document.management.core.model.LOV;
import bpm.document.management.core.model.Log;
import bpm.document.management.core.model.Log.LogType;
import bpm.document.management.core.model.MailEntity;
import bpm.document.management.core.model.MailFilter;
import bpm.document.management.core.model.MailServer;
import bpm.document.management.core.model.ManagerAction;
import bpm.document.management.core.model.MetadataLink.LinkType;
import bpm.document.management.core.model.OrbeonFormInstance;
import bpm.document.management.core.model.OrbeonFormSection;
import bpm.document.management.core.model.OrganigramElement;
import bpm.document.management.core.model.OrganigramElementSecurity;
import bpm.document.management.core.model.Permission;
import bpm.document.management.core.model.Permission.ShareType;
import bpm.document.management.core.model.PermissionItem;
import bpm.document.management.core.model.ScanDocumentType;
import bpm.document.management.core.model.SourceConnection;
import bpm.document.management.core.model.Tasks;
import bpm.document.management.core.model.Tasks.TaskStatus;
import bpm.document.management.core.model.Tree;
import bpm.document.management.core.model.TypeTask;
import bpm.document.management.core.model.User;
import bpm.document.management.core.model.VdmContext;
import bpm.document.management.core.model.Versions;
import bpm.document.management.core.model.aklademat.AkladematSettings;
import bpm.document.management.core.model.aklademat.AkladematTypeSettings;
import bpm.document.management.core.model.aklademat.Classification;
import bpm.document.management.core.model.aklademat.PastellFile;
import bpm.document.management.core.utils.AklaboxConstant;
import bpm.document.management.core.utils.TreatmentImageObject;
import bpm.document.management.remote.RemoteVdmManager;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.services.exceptions.ServiceException;
import bpm.gwt.aklabox.commons.server.i18n.Labels;
import bpm.gwt.aklabox.commons.server.methods.NotificationHelper;
import bpm.gwt.aklabox.commons.server.methods.PDFTextExtractor;
import bpm.gwt.aklabox.commons.server.methods.WorkFlowNotifier;
import bpm.gwt.aklabox.commons.server.security.CommonSession;
import bpm.gwt.aklabox.commons.server.security.CommonSessionHelper;
import bpm.gwt.aklabox.commons.server.security.ExceptionHelper;
import bpm.gwt.aklabox.commons.server.servlets.ScannerServlet;
import bpm.gwt.aklabox.commons.server.upload.VideoThumbCreator;
import bpm.gwt.aklabox.commons.shared.AklaboxConnection;
import bpm.gwt.aklabox.commons.shared.EntityUserHelper;
import bpm.gwt.aklabox.commons.shared.OCRSearchResult;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.config.ConfigurationAklaboxConstants;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AklaCommonServiceImpl extends RemoteServiceServlet implements AklaCommonService {

	private static final long serialVersionUID = -5796095198921620090L;

	// private static Logger logger =
	// Logger.getLogger(AklaCommonServiceImpl.class);
	private WorkFlowNotifier workFlowNotifier;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	private CommonSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), CommonSession.class);
	}

	@Override
	public Integer manageItem(Serializable item, ManagerAction action) throws ServiceException {
		try {
			return getSession().getAklaboxService().manageItem(item, action);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<LOV> getAllLov() throws ServiceException {
		try {
			return getSession().getAklaboxService().getAllLov();
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<String> getColumns(SourceConnection connection, String tableName) throws ServiceException {
		try {
			return getSession().getAklaboxService().getColumns(connection, tableName);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<String> getTables(SourceConnection connection) throws ServiceException {
		try {
			return getSession().getAklaboxService().getTable(connection);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<SourceConnection> getAllConnections() throws ServiceException {
		try {
			return getSession().getAklaboxService().getAllConnection();
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public LOV saveLOV(LOV lov) throws ServiceException {
		try {
			return getSession().getAklaboxService().saveLov(lov);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void updateLOV(LOV lov) throws ServiceException {
		try {
			getSession().getAklaboxService().updateLov(lov);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void deleteLOV(LOV lov) throws ServiceException {
		try {
			getSession().getAklaboxService().deleteLov(lov);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<String> getLisDriver() throws ServiceException {
		Collection<DriverInfo> listDriver;
		try {
			listDriver = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo();
			List<String> driversName = new ArrayList<String>();
			for (DriverInfo info : listDriver) {
				driversName.add(info.getClassName());
			}
			return driversName;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}

	}

	@Override
	public SourceConnection saveSourceConnection(SourceConnection connection) throws ServiceException {
		try {
			return getSession().getAklaboxService().saveSourceConnection(connection);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public Boolean testConnection(SourceConnection connection) throws ServiceException {
		try {
			return getSession().getAklaboxService().testConnection(connection);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public HashMap<String, String> getAllLovByTableCode(LOV tableCode) throws ServiceException {
		try {
			return getSession().getAklaboxService().getAllLovByTableCode(tableCode);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public String scan() throws ServiceException {

		try {
			ScannerServlet scanner = new ScannerServlet();
			return scanner.getFileName();
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public Boolean isScannedImageReady(String fileName) throws ServiceException {

		try {
			if (new File(fileName).exists()) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<FormField> getAllAklaBoxFormFields(/*int aklaBoxServerId,*/ int formId) throws ServiceException {
		try {
			return getSession().getAklaboxService().getAllFormFields(formId);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<FormCellLink> getAllFormCellLinksbyFormCell(FormCell cell) throws ServiceException {
		try {
			return getSession().getAklaflowService().getAllFormCellLinksbyFormCell(cell);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<FormCell> getAllFormCells(StandardForm form) throws ServiceException {

		try {
			return getSession().getAklaflowService().getAllFormCells(form);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<AklaBoxServer> getAllAklaBoxServers(User user) throws ServiceException {
		try {
			return getSession().getAklaflowService().getAllAklaBoxServers(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<Form> getAllAklaBoxForm(/*int aklaBoxServerId*/) throws ServiceException {
		try {
			return getSession().getAklaboxService().getAllForm();
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public StandardForm saveStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList) throws ServiceException {

		try {
			return getSession().getAklaflowService().saveStandardForm(form, cells, aklaFormList, aklaFormFieldList);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void updateStandardForm(StandardForm form, List<FormCell> cells, List<Integer> aklaFormList, List<Integer> aklaFormFieldList) throws ServiceException {

		try {
			getSession().getAklaflowService().updateResource(form);
			for (FormCell cell : getSession().getAklaflowService().getAllFormCells(form)) {
				getSession().getAklaflowService().deleteFormCell(cell);
			}
			for (FormCell cell : cells) {
				cell.setFormId(form.getId());
				int id = getSession().getAklaflowService().saveFormCell(cell);
				cell.setId(id);
			}

			for (FormCellLink link : getSession().getAklaflowService().getAllFormCellLinksbyOCRForm(form)) {
				getSession().getAklaflowService().deleteFormCellLink(link);
			}
			for (FormCell cell : cells) {
				if (aklaFormFieldList != null && aklaFormFieldList.get(cells.indexOf(cell)) != 0) {
					FormCellLink link = new FormCellLink(form.getId(), cell.getId(), aklaFormFieldList.get(cells.indexOf(cell)), aklaFormList.get(cells.indexOf(cell)));
					saveFormCellLink(link);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	public void saveFormCellLink(FormCellLink cellLink) throws Exception {
		try {
			getSession().getAklaflowService().saveFormCellLink(cellLink);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void updateFormCellResult(FormCellResult cellResult) throws Exception {
		getSession().getAklaflowService().updateFormCellResult(cellResult);
	}

	@Override
	public FormCellResult getFormCellResult(Documents doc, FormCell cell) throws Exception {
		return getSession().getAklaflowService().getFormCellResult(doc, cell);
	}

	private IVdmManager buildManager(AklaboxConnection server) throws Exception {
		VdmContext vdmContext = new VdmContext();
		vdmContext.setMail(server.getLogin());
		vdmContext.setPassword(server.getPassword());
		vdmContext.setVdmUrl(server.getUrl());
		
		RemoteVdmManager manager = new RemoteVdmManager(vdmContext);
		User user = new User();
		user.setEmail(server.getLogin());
		user.setPassword(server.getPassword());
		manager.connect(user);

		return manager;
	}

	@Override
	public List<Tree> getFoldersPerEnterprise(int enterpriseId, boolean lightWeight) throws ServiceException {
		return getFoldersPerEnterprise(getSession().getAklaboxService(), getSession().getUser().getEmail(), enterpriseId, lightWeight);
	}

	@Override
	public List<IObject> getItemPerEnterpriseFolder(Enterprise enterprise, Tree parentFolder, boolean lightWeight) throws ServiceException {
		try {
			IVdmManager manager = getSession().getAklaboxService();
			return manager.findChildrenEnterprise(enterprise, parentFolder, lightWeight);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get folders for enterprise: ", false);
		}
	}

	@Override
	public List<Tree> getFoldersPerEnterprise(AklaboxConnection server, int enterpriseId) throws ServiceException {
		IVdmManager manager = null;
		String login = null;
		if (server != null) {
			try {
				manager = buildManager(server);
			} catch (Exception e) {
				e.printStackTrace();
			}
			login = server.getLogin();
		} else {
			manager = getSession().getAklaboxService();
			login = getSession().getUser().getEmail();
		}
		return getFoldersPerEnterprise(manager, login, enterpriseId, false);
	}

	private List<Tree> getFoldersPerEnterprise(IVdmManager manager, String login, int enterpriseId, boolean lightWeight) throws ServiceException {
		try {
			User user = manager.getUserInfo(login);
			List<Tree> listTree = manager.getFilesPerEnterprise(enterpriseId, lightWeight);
			List<Tree> newListTree = new ArrayList<Tree>();

			if (user.getUserType().equals("Loader")) {
				for (Tree t : listTree) {
					if (t.getUserId() == user.getUserId()) {
						newListTree.add(t);
					}
				}
			} else {
				return listTree;
			}
			return newListTree;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get folders per enterprise: ", false);
		}
	}

	@Override
	public List<Enterprise> getEnterprisePerUser(String email) throws Exception {
		return getEnterprisePerUser(getSession().getAklaboxService(), email);
	}

	@Override
	public List<Enterprise> getEnterprisePerUser(AklaboxConnection server, String email) throws ServiceException {
		IVdmManager manager = null;
		if (server != null) {
			try {
				manager = buildManager(server);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			manager = getSession().getAklaboxService();
		}
		return getEnterprisePerUser(manager, email);
	}

	private List<Enterprise> getEnterprisePerUser(IVdmManager manager, String email) throws ServiceException {
		try {
			return manager.getEnterprisePerUser(email);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get enterprise per user: ", false);
		}
	}

	@Override
	public List<IObject> getDirectoryContent(int parentId) throws Exception {
		return getDirectoryContent(getSession().getAklaboxService(), parentId);
	}

	@Override
	public List<IObject> getDirectoryContent(AklaboxConnection server, int parentId) throws ServiceException {
		IVdmManager manager = null;
		if (server != null) {
			try {
				manager = buildManager(server);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			manager = getSession().getAklaboxService();
		}
		return getDirectoryContent(manager, parentId);
	}

	private List<IObject> getDirectoryContent(IVdmManager vdmManager, int parentId) throws ServiceException {
		try {
			List<IObject> noDeleted = new ArrayList<IObject>();
			List<IObject> docs = vdmManager.getOrganigramFolderContent(parentId);
			for (IObject d : docs) {
				if (!(d instanceof Documents && ((Documents) d).isDeleted())) {
					noDeleted.add(d);
				}
			}
			return noDeleted;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get directory content: ", false);
		}
	}

	// @Override
	// public Documents saveDocument(Documents doc) throws Exception {
	// CommonSession session = getSession();
	// User owner = session.getUser();
	//
	// doc.setAuthorName(owner.getFirstName() + " " + owner.getLastName());
	// doc.setUserId(owner.getUserId());
	//
	// HttpServletRequest request = getThreadLocalRequest();
	// String url = request.getScheme() + "://" + request.getServerName() + ":"
	// + request.getServerPort() + request.getContextPath();
	//
	// doc = new DocsController().saveDocs(doc, owner.getEmail(), session);
	// System.err.println("docName" + doc.getName() + " thumbImage : " +
	// doc.getThumbImage());
	//
	// Date date = new Date();
	// Versions docVersion = new Versions();
	// docVersion.setDocId(doc.getId());
	// docVersion.setDocRelPath(doc.getFilePath());
	// docVersion.setModifiedBy(owner.getEmail());
	// docVersion.setVersionDate(date);
	// docVersion.setVersionNumber(0 + 1);
	// docVersion.setVersionComment("");
	// docVersion.setVersionFileSize(doc.getFileSize());
	// if (doc.getType().equals(DocumentUtils.URL)) {
	// docVersion.setDocName(doc.getFileName());
	// }
	// else {
	// docVersion.setDocName(doc.getName());
	// }
	//
	// saveDocVersion(docVersion, doc, owner.getUserType(), "");
	//
	// List<String> tagWords = new ArrayList<String>();
	// tagWords.add("CHORUS");
	//
	// Tags tags = new Tags();
	// tags.setFileId(doc.getId());
	// tags.setFileType(doc.getTreeType());
	//
	// for (String tagWord : tagWords) {
	// tags.setTagWord(tagWord);
	// getSession().getAklaboxService().saveTag(tags,
	// getSession().getAklaboxService().getUserInfo(getSession().getUser().getEmail()).getUserId());
	// }
	// return doc;
	// }
	//
	// private Versions saveDocVersion(Versions docVersion, Documents doc,
	// String role, String logoPath) throws ServiceException {
	// try {
	// HttpServletRequest request = getThreadLocalRequest();
	// String url = request.getScheme() + "://" + request.getServerName() + ":"
	// + request.getServerPort() + request.getContextPath();
	//
	// User author =
	// getSession().getAklaboxService().getUserInfo(doc.getLastModifiedBy());
	//
	// doc.setAuthorName(author.getFirstName() + " " + author.getLastName());
	//
	// String inputFile = docVersion.getDocRelPath();
	// Documents d = new Documents();
	// d.setFilePath(inputFile);
	// d.setId(docVersion.getDocId());
	// docVersion.setHadoopFile(Boolean.parseBoolean(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.hdfs.use")));
	//
	// String stampPath = null;
	// if (doc.getType().equals(DocumentUtils.OFFICE)) {
	// doc.setFinished(false);
	// getSession().getAklaboxService().updateDocument(doc);
	//
	// boolean isPdf = true;
	//
	// String outputFile = null;
	// if (!inputFile.endsWith(".pdf")) {
	// isPdf = false;
	// outputFile = inputFile + ".pdf";
	// }
	// else {
	// outputFile = inputFile;
	// }
	//
	// Thread th = new Thread(new ImageWriter(inputFile, outputFile, stampPath,
	// isPdf, doc, docVersion.getVersionNumber(), getSession(), url, false,
	// logoPath, false, true));
	// th.start();
	// }
	// else {
	// doc.setFinished(true);
	// new EncryptionTemplate().encrypt(doc, getSession());
	// }
	// getSession().getAklaboxService().updateDocument(doc);
	//
	// if (doc.getParentId() != 0 && docVersion.getVersionNumber() == 1 &&
	// doc.isFromEnterprise()) {
	// User user =
	// getSession().getAklaboxService().getUserInfo(doc.getUserValidator().split(",")[0]);
	// User owner =
	// getSession().getAklaboxService().getUserInfo(getSession().getUser().getEmail());
	//
	// Notifications noti = new Notifications();
	// noti.setDocId(doc.getId());
	// noti.setIsTrigger(true);
	// noti.setUserId(user.getUserId());
	// noti.setNotifiedBy(doc.getUserId());
	// noti.setNotificationDate(new Date());
	// noti.setMessage(owner.getFirstName() + " " + owner.getLastName() + " " +
	// Labels.getLabel(getSession().getCurrentLocale(),
	// Labels.IsAskingYourPermissionToValidate) + " " + doc.getName());
	// noti.setFileType(doc.getTreeType());
	//
	// getSession().getAklaboxService().saveNotification(noti);
	//
	// //Disable send email (too many email)
	// // new Thread(new EmailSender(user, noti, url, getSession())).start();
	// }
	//
	// Versions v = getSession().getAklaboxService().saveDocVersion(docVersion);
	//
	// doc.setValidationStatus("Waiting");
	// updateDocStatus(doc);
	//
	// return v;
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw ExceptionHelper.getClientException(e, "", false);
	// }
	// }

	@Override
	public void updateDocStatus(Documents doc) throws ServiceException {
		try {
			getSession().getAklaboxService().updateDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void analyzeForm(Documents doc) throws Exception {
		getSession().getAklaflowService().analyzeForm(doc);
	}

	public void saveComments(List<Comments> comments) throws Exception {
		for (Comments comment : comments) {
			getSession().getAklaboxService().saveComments(comment);
		}
	}

	public void savePages(List<DocPages> pages) throws Exception {
		getSession().getAklaboxService().savePages(pages);

	}

	@Override
	public Map<AkLadExportObject, String> saveDocumentsWithAnalyse(List<AkLadExportObject> docsComments, boolean sendToAccounting) throws Exception {
		List<Documents> documents = new ArrayList<Documents>();
		Map<AkLadExportObject, String> result = new HashMap<>();
		/// POur Haute Saone
		//exportWorkspace(docsComments);
		///
		for (AkLadExportObject dpco : docsComments) {
			if(dpco.getPagesMeta().get(0).getSelectedType() != null){
				

				if(sendToAccounting){ //on dispatch les factures auto
					int dynFolderId = getDynamicFolderForBill(dpco);
					dpco.getDoc().setParentId(dynFolderId);
				}
				
				if(dpco.getPagesMeta().get(0).getSelectedType().isCreateFolderAuto()){
					//"NOM - PRENOM - CodePostal - DATE+NoChrono" 
					String fname = "John";
					String name = "Doe";
					String cp = "00000";
					String date = "19700101";
					//String nuchrono = "0";

					if(dpco.getCellsFormAklabox() != null){
						for(FormFieldValue v : dpco.getCellsFormAklabox()){
							if(getSession().getAklaboxService().getFormField(v.getFormFieldId()).getLabel().toLowerCase().matches(".*(prenom|prénom).*")){
								fname = v.getValue();
							}
							else if(getSession().getAklaboxService().getFormField(v.getFormFieldId()).getLabel().toLowerCase().contains("nom")){
								name = v.getValue();
							}
							
							else if(getSession().getAklaboxService().getFormField(v.getFormFieldId()).getLabel().toLowerCase().contains("code postal")){
								cp = v.getValue();
							}
//							else if(getSession().getAklaboxService().getFormField(v.getFormFieldId()).getLabel().toLowerCase().contains("adresse")){
//								Pattern p = Pattern.compile("(\\d{5})");
//								Matcher m = p.matcher(v.getValue());
//								if (m.find()) {
//									cp = m.group();
//								}
//							}
							else if(getSession().getAklaboxService().getFormField(v.getFormFieldId()).getLabel().toLowerCase().contains("date")){
								try {
									Date d = new SimpleDateFormat("dd MMMMM yyyy").parse(v.getValue());
									date = new SimpleDateFormat("yyyyMMdd").format(d);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
									
					int folderId = Integer.parseInt(getSession().getAklaboxService().createSubDirectory(name + "-" + fname + "-" + cp + "-" + date, dpco.getDoc().getParentId()+"", getSession().getUser()));
					dpco.getDoc().setParentId(folderId);
					
					Tree folder = getSession().getAklaboxService().getDirById(folderId);
					folder.setName(folder.getName()+ new DecimalFormat("000").format(folder.getId()));
					folder.setTreeType(AklaboxConstant.FOLDER_MARKET);
					folder.setActivateFolder(false);
					folder.setImmediateValidation(true);
					getSession().getAklaboxService().updateDirectory(folder);
					
//					if(dpco.getCellsFormAklabox() != null && !dpco.getCellsFormAklabox().isEmpty()){
//						MetadataLink link = new MetadataLink();
//						link.setFormId(getSession().getAklaboxService().getFileType(dpco.getPagesMeta().get(0).getSelectedType().getIdLink()).getForms().get(0).getId());
//						link.setItemId(folderId);
//						link.setType(MetadataLink.LinkType.FOLDER);
//						getSession().getAklaboxService().saveMetadataLink(link, false);
//					}
				}
			}
			documents.add(dpco.getDoc());
		}
		List<Documents> docsResult = getSession().getAklaboxService().saveDocuments(documents);
		for (Documents doc : docsResult) {
			AkLadExportObject dpco = docsComments.get(docsResult.indexOf(doc));
			
			/* on effectue en premier l'envoi de l'entity en compta, car si echec on abandonne le reste et on supprime le doc */
			int idType = Integer.parseInt(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.document.management.chorus.type.id"));
			if ((doc.getTypeSelectedId() != null && doc.getTypeSelectedId() == idType) || (dpco.getPagesMeta().get(0).getSelectedType() != null && dpco.getPagesMeta().get(0).getSelectedType().isChorusType())) {
				try {
					PastellFile file = new PastellFile(bpm.document.management.core.model.aklademat.PastellFile.FileType.FICHIER_FACTURE, 
							doc.getName(), 0);
					file.setDocumentId(doc.getId());
					dpco.getChorusMetadata().addFile(file);
					AkladematAdminEntity<Chorus> entity = new AkladematAdminEntity<Chorus>(doc.getName(), DocType.CHORUS, doc.getId(), dpco.getChorusMetadata());
					entity = (AkladematAdminEntity<Chorus>) getSession().getAkladematService().addAkladematAdminEntity(entity);

					getSession().getAkladematService().buildMetadata(doc.getId(), entity);

					
					if (sendToAccounting) {
						VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
						if (!config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_CEGID_CHORUS_URL).isEmpty()) {
							// ??? envoi de la facture vers cegid ?
						}
						if (!config.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_COCKTAIL_ORACLE_URL).isEmpty()) {
							try {
								getSession().getAkladematService().pushCocktail(entity);
								
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println(e.getMessage());
								result.put(dpco, e.getMessage());
								getSession().getAklaboxService().deleteDocument(doc, "auto");
								continue;
							}
						}
						
						if(entity.getObject() != null && entity.getObject().getCocktail() != null &&
								entity.getObject().getCocktail().getCommentaire() != null && !entity.getObject().getCocktail().getCommentaire().isEmpty()){
							Comments com = new Comments();
							com.setDocId(doc.getId());
							com.setCommentDate(new Date());
							com.setUser(doc.getLastModifiedBy());
							com.setMessage(entity.getObject().getCocktail().getCommentaire());
							com.setPage(0);
							getSession().getAklaboxService().saveComments(com);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			Versions v = new Versions();
			v.setDocId(doc.getId());
			v.setDocName(doc.getName());
			v.setDocRelPath(doc.getFilePath());
			v.setModifiedBy(doc.getLastModifiedBy());
			v.setVersionComment("");
			v.setVersionDate(new Date());
			v.setVersionFileSize(doc.getFileSize());
			v.setVersionNumber(1);
			v = getSession().getAklaboxService().saveVersion(v);
			
			
			

			for (AkLadPageInfo page : dpco.getPagesMeta()) {
				List<Comments> comments = page.getComments();
				if (comments != null) {
					for (Comments com : comments) {
						com.setDocId(doc.getId());
					}
					saveComments(comments);

				}
			}

			List<DocPages> pages = dpco.getPages();
			if (pages != null) {
				for (DocPages page : pages) {
					page.setDocId(doc.getId());
				}
				savePages(pages);

			}
			

			if (doc.getFormId() != 0) {
				if (dpco.getOcrCells() == null) {
					analyzeForm(doc);
				} else {
					for (FormCell cell : dpco.getOcrCells().keySet()) {

						FormCellResult res = dpco.getOcrCells().get(cell);
						res.setDocId(doc.getId());
						getSession().getAklaflowService().saveFormCellResult(res);
					}
				}

			}
			
			
			if(dpco.getCellsFormAklabox() != null && !dpco.getCellsFormAklabox().isEmpty()){
				int id = dpco.getPagesMeta().get(0).getSelectedType().isCreateFolderAuto() ?doc.getParentId() : doc.getId();
				
				for(FormFieldValue val : dpco.getCellsFormAklabox()){
					val.setDocId(id);
					val.setCreationDate(new Date());
				}
				Form form = getSession().getAklaboxService().getFileType(dpco.getPagesMeta().get(0).getSelectedType().getIdLink()).getForms().get(0);
				HashMap<Form, List<FormFieldValue>> map = new HashMap<Form, List<FormFieldValue>>();
				map.put(form, dpco.getCellsFormAklabox());
				
				LinkType type = dpco.getPagesMeta().get(0).getSelectedType().isCreateFolderAuto() ? LinkType.FOLDER : LinkType.DOCUMENT;

				getSession().getAklaboxService().saveMetadataValues(id, map, type, true);
			}
			
			if(dpco.getPagesMeta().get(0).getSelectedReferent() != null){
				IObject obj;
				if(dpco.getPagesMeta().get(0).getSelectedType().isCreateFolderAuto()){
					obj = getDirectoryInfo(doc.getParentId());
				} else {
					obj = getDocInfo(doc.getId());
				}
				
				NotificationHelper.notifyUser(getThreadLocalRequest(), getSession(), getSession().getUser(), dpco.getPagesMeta().get(0).getSelectedReferent(), obj, Labels.HasAddedAFolder);
			}

			result.put(dpco, null);
		}
		return result;
	}
	
//	private void buildMetadata(IVdmManager service, int docId, AkladematAdminEntity<Chorus> entity) throws Exception {
//		List<MetadataLink> links = service.getMetadatas(null, LinkType.CHORUS, false);
//		int metadataLinkId = 0;
//
//		Form form = null;
//		if (links != null && !links.isEmpty()) {
//			// Should only be one
//			for (MetadataLink link : links) {
//				form = link.getForm();
//				metadataLinkId = link.getId();
//				break;
//			}
//		}
//
//		HashMap<String, FormField> fields = new HashMap<String, FormField>();
//		if (form == null) {
//			form = createChorusForm(service);
//
//			MetadataLink link = new MetadataLink();
//			link.setItemId(-1);
//			link.setFormId(form.getId());
//			link.setType(LinkType.CHORUS);
//			link.setApplyToChild(false);
//
//			metadataLinkId = service.saveMetadataLink(link, true);
//
//			fields = createChorusFormFields(service, form, true);
//		}
//		else {
//			fields = createChorusFormFields(service, form, false);
//		}
//
//		Chorus chorus = entity.getObject();
//
//		List<FormFieldValue> values = new ArrayList<FormFieldValue>();
//		for (String param : Chorus.P_CPP_PARAMS) {
//			PastellData pastellData = chorus.getMetadata().get(param);
//			FormField field = fields.get(param);
//			if (pastellData != null && field != null) {
//				FormFieldValue value = new FormFieldValue(field.getId(), pastellData.getValueAsString(), field.getType(), docId);
//				value.setMetadataLinkId(metadataLinkId);
//				values.add(value);
//			}
//		}
//		service.saveFormFieldValue(docId, values);
//	}
//	
//	private Form createChorusForm(IVdmManager service) throws Exception {
//		Form chorusForm = new Form(Chorus.DEFAULT_FORM_NAME, "", AklaboxConstant.FORM_DEFAULT, "", "");
//		chorusForm.setType(FormType.METADATA);
//		return service.saveForm(chorusForm);
//	}
//
//	private HashMap<String, FormField> createChorusFormFields(IVdmManager service, Form chorusForm, boolean saveFields) throws Exception {
//		HashMap<String, FormField> fields = new HashMap<String, FormField>();
//		for (String param : Chorus.P_CPP_PARAMS) {
//			if (saveFields) {
//				fields.put(param, new FormField(param, "", FormFieldType.STRING, false, chorusForm.getId()));
//			}
//			else {
//				for (FormField field : chorusForm.getFields()) {
//					if (field.getLabel().equals(param)) {
//						fields.put(param, field);
//						break;
//					}
//				}
//			}
//		}
//		if (saveFields) {
//			HashMap<String, FormField> ffs = new HashMap<>();
//			for(String pa : fields.keySet()) {
//				ffs.put(pa, service.saveFormField(fields.get(pa)));
//			}
//			fields = ffs;
//		}
//
//		return fields;
//	}

	@Override
	public void saveFormTextTemplate(StandardForm standardForm) throws Exception {
		getSession().getAklaflowService().saveFormTextTemplate(standardForm);
	}

	@Override
	public String exportWorkspace(List<AkLadExportObject> docs) throws Exception {
		String name = "Aklad_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		ByteArrayInputStream byteArrayIs = (ByteArrayInputStream) getSession().getAklaflowService().exportAkladWorkSpace(docs);
		if (byteArrayIs == null) {
			throw new Exception("Unable to create the requested document.");
		}

		// getSession().addStream(name, AklaboxConstant.XAKLAD2, byteArrayIs);
		return name;
	}

	@Override
	public List<User> getUsersFromOrganigramme(int folderId) throws Exception {
		List<OrganigramElement> orgaEls = getSession().getAklaboxService().getOrganigram();
		if (orgaEls != null) {
			OrganigramElement selectedElement = null;
			for (OrganigramElement el : orgaEls) {
				if (el.getDirectoryId() == folderId) {
					selectedElement = el;
				} else {
					selectedElement = findElement(el.getChildren(), folderId);
				}

				if (selectedElement != null) {
					break;
				}
			}

			if (selectedElement != null) {
				return getUsersFromOrganigramme(selectedElement);
			}
		}
		return new ArrayList<User>();
	}

	private OrganigramElement findElement(List<OrganigramElement> elements, int folderId) {
		if (elements != null) {
			OrganigramElement selectedElement = null;
			for (OrganigramElement el : elements) {
				if (el.getDirectoryId() == folderId) {
					selectedElement = el;
				} else {
					selectedElement = findElement(el.getChildren(), folderId);
				}

				if (selectedElement != null) {
					return selectedElement;
				}
			}
		}
		return null;
	}

	private List<User> getUsersFromOrganigramme(OrganigramElement el) {
		List<User> users = new ArrayList<User>();
		if (el.getSecurities() != null) {
			for (OrganigramElementSecurity elSec : el.getSecurities()) {
				users.add(elSec.getUser());
			}
		}
		return users;
	}

	@Override
	public Documents saveDocument(Documents doc, List<Tasks> task, List<FormFieldValue> fieldValues, Tasks taskDelegated) throws Exception {
		// HttpServletRequest request = getThreadLocalRequest();
		// String url = request.getScheme() + "://" + request.getServerName() +
		// ":" + request.getServerPort() + request.getContextPath();

		doc = getSession().getAklaboxService().saveMailDocument(doc, task, fieldValues);

		// if (doc.getType().equals(DocumentUtils.OFFICE)) {
		// Thread th = new Thread(new ImageWriter(file, doc,
		// docVersion.getVersionNumber(), getSession(), url));
		// th.start();
		// }

		if (taskDelegated != null) {
			taskDelegated.setActive(false);
			getSession().getAklaboxService().updateTask(taskDelegated);
		}

		return doc;
	}

	@Override
	public List<Archiving> getAllArchiving() throws Exception {
		return getSession().getAklaboxService().getAllArchiving();
	}

	@Override
	public AkladematAdminEntity<? extends IAdminDematObject> getAkladematAdminEntityById(int id) throws Exception {
		return getSession().getAkladematService().getAkladematAdminEntityById(id);
	}

	@Override
	public AkladematAdminEntity<? extends IAdminDematObject> getAdminEntitybyDoc(int idDoc, DocType type) throws ServiceException {
		try {
			return getSession().getAkladematService().getAdminEntitybyDoc(idDoc, type);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get deeds: ", false);
		}
	}

	@Override
	public List<Classification> getDeedClassifications(boolean flatStructure) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getAkladematService().getDeedClassifications(flatStructure);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get deed classifications : ", false);
		}
	}

	@Override
	public boolean pushToIparapheur(int docId) throws ServiceException {
		CommonSession session = getSession();
		AkladematSettings settings;
		try {
			settings = session.getAkladematService().getSettings(AkladematTypeSettings.IPARAPHEUR);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to push to i-Parapheur : ", false);
		}
		if (settings == null || settings.getSettings() == null) {
			return false;
		}

		return true;
	}

	@Override
	public int[] getImageSize(String path) throws ServiceException {
		try {
			return getSession().getAklaflowService().getImageSize(path);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get image size : ", false);
		}
	}

	@Override
	public List<Log<? extends ILog>> getLogs(LogType type) throws ServiceException {
		try {
			return getSession().getAklaboxService().getActionLogs(type);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get logs : ", false);
		}
	}

	@Override
	public EntityUserHelper getUsersForEntity(Documents document, AkladematAdminEntity<? extends IAdminDematObject> entity, Integer financeServiceId) throws ServiceException {
		try {
			CommonSession session = getSession();

			EntityUserHelper entityUserHelper = new EntityUserHelper();
			if (entity.getObject() instanceof Chorus) {
				Chorus chorus = (Chorus) entity.getObject();

				Enterprise enterprise = session.getAklaboxService().getEnterpriseParent(document.getParentId(), bpm.document.management.core.model.IObject.ItemType.FOLDER);
				if (enterprise != null) {
					entityUserHelper.setManagers(getUserEnterprise(enterprise, TypeUser.MANAGER));
					entityUserHelper.setEnterpriseValidators(getUserEnterprise(enterprise, TypeUser.VALIDATOR));
				}

				if (financeServiceId != null) {
					Enterprise financeEnterprise = session.getAklaboxService().getEnterprise(financeServiceId);
					entityUserHelper.setFinanceUsers(getUserEnterprise(financeEnterprise, TypeUser.MANAGER));
				}

				// List<Integer> validatorsId = chorus.getValidators();
				// if (validatorsId != null) {
				// List<User> validators = new ArrayList<User>();
				// for (Integer userId : validatorsId) {
				// User user =
				// session.getAklaboxService().getUserInfoThroughId(userId +
				// "");
				// if (user != null) {
				// validators.add(user);
				// }
				// }
				// entityUserHelper.setValidators(validators);
				// }
			}

			return entityUserHelper;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get users for this bill.", true);
		}
	}

	@Override
	public List<User> getUserEnterprise(Enterprise selectedEnterprise, TypeUser typeUser) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getAklaboxService().getUserEnterprise(selectedEnterprise.getEnterpriseId(), typeUser);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get users of type " + typeUser.toString() + " for enterprise: " + e.getMessage());
		}

		// String usersAsString = null;
		// switch (typeUser) {
		// case ADMIN:
		// usersAsString = selectedEnterprise.getAdministrators();
		// break;
		// case MAILER:
		// usersAsString = selectedEnterprise.getMailList();
		// break;
		// case MANAGER:
		// usersAsString = selectedEnterprise.getManagers();
		// break;
		// case READER:
		// usersAsString = selectedEnterprise.getReaders();
		// break;
		// case SIGNER:
		// usersAsString = selectedEnterprise.getSignatories();
		// break;
		// case VALIDATOR:
		// usersAsString =
		// getFoldersPerEnterprise(selectedEnterprise.getEnterpriseId(),
		// true).get(0).getValidatedBy();
		// break;
		//
		// default:
		// break;
		// }
		//
		// if (usersAsString == null || usersAsString.isEmpty()) {
		// return null;
		// }
		//
		// List<User> users = new ArrayList<User>();
		// String[] usersArray = usersAsString.split(",");
		// try {
		// for (String email : usersArray) {
		// users.add(session.getAklaboxService().getUserInfo(email));
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// return users;
	}

	@Override
	public AkladematAdminEntity<? extends IAdminDematObject> addoreditAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> adminEntity) throws ServiceException {
		try {
			if (adminEntity.getId() == 0) {
				return getSession().getAkladematService().addAkladematAdminEntity(adminEntity);
			} else {
				getSession().getAkladematService().updateAkladematAdminEntity(adminEntity);
				return adminEntity;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to save this item (Demat)", false);
		}
	}

	@Override
	public void actionOnAkladematAdminEntity(AkladematAdminEntity<? extends IAdminDematObject> entity, Status action, ItemValidation validation, User currentUser, List<User> users, boolean fromDemo, String messageAsk) throws ServiceException {
		CommonSession session = getSession();

		try {

			if (validation != null) {
				HttpServletRequest request = getThreadLocalRequest();

				// Manage Validation
				try {
					IObject document = session.getAklaboxService().getDocById(entity.getIdDoc());

					switch (validation.getStatus()) {
					case ASK_INSTRUCTION:
					case INSTRUCTED:
					case ASK_VALIDATION:
						break;
					case VALIDATED:
					case UNVALIDATED:
						Tree folder = session.getAklaboxService().getDirById(document.getParentId());
						workFlowNotifier = new WorkFlowNotifier(getSession(), document);
						workFlowNotifier.sendFolderCondition(folder, currentUser.getEmail(), false, false, false, false, false, true, request, messageAsk);
//						User author = session.getAklaboxService().getUserInfoThroughId(validation.getAskUserId() + "");
//						NotificationHelper.notifyUser(request, session, session.getUser(), author, document, messageAsk);
						break;
					default:
						break;
					}

					session.getAklaboxService().manageItemValidation(validation, validation.getId() <= 0 ? ManagerAction.SAVE : ManagerAction.UPDATE);
				} catch (Exception e) {
					e.printStackTrace();
					throw new ServiceException("A problem occured: " + e.getMessage());
				}
			}

			session.getAkladematService().actionOnAkladematAdminEntity(entity, action, currentUser, users, fromDemo);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to update this bill", true);
		}
	}

	@Override
	public HashMap<Documents, AkladematAdminEntity<? extends IAdminDematObject>> getAdminEntities(List<Documents> docs) throws ServiceException {
		CommonSession session = getSession();

		HashMap<Documents, AkladematAdminEntity<? extends IAdminDematObject>> documentsWithEntities = new HashMap<Documents, AkladematAdminEntity<? extends IAdminDematObject>>();
		if (documentsWithEntities != null) {
			for (Documents doc : docs) {
				AkladematAdminEntity<? extends IAdminDematObject> entity = null;
				if (doc.getTypeSelected() != null && doc.getTypeSelected().getTypeId() > 0) {
					Integer acteTypeDocumentId = session.getActeTypeDocumentId();
					Integer chorusTypeDocumentId = session.getChorusTypeDocumentId();

					DocType docType = null;
					if (acteTypeDocumentId != null && doc.getTypeSelected().getTypeId() == acteTypeDocumentId) {
						docType = DocType.ACTE;
					} else if (chorusTypeDocumentId != null && doc.getTypeSelected().getTypeId() == chorusTypeDocumentId) {
						docType = DocType.CHORUS;
					}

					if (docType != null) {
						entity = getAdminEntitybyDoc(doc.getId(), docType);
					}
				}

				documentsWithEntities.put(doc, entity);
			}
		}
		return documentsWithEntities;
	}

	@Override
	public void manageEntities(List<AkladematAdminEntity<? extends IAdminDematObject>> selectedEntitites, Status action, boolean fromDemo) throws ServiceException {
		CommonSession session = getSession();
		if (selectedEntitites != null) {
			for (AkladematAdminEntity<? extends IAdminDematObject> entity : selectedEntitites) {
				actionOnAkladematAdminEntity(entity, action, null, session.getUser(), new ArrayList<User>(), fromDemo, null);
			}
		}
	}

	@Override
	public void addoreditAkladematAdminEntities(List<AkladematAdminEntity<? extends IAdminDematObject>> entities) throws ServiceException {
		if (entities != null) {
			for (AkladematAdminEntity<? extends IAdminDematObject> entity : entities) {
				addoreditAkladematAdminEntity(entity);
			}
		}
	}

	@Override
	public List<User> searchUsers(String filter) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getAklaboxService().searchUsers(filter);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<User>();
		}
	}

	@Override
	public void notifyEnterpriseAdmins(Enterprise enterprise, IObject item, String message) throws ServiceException {
		CommonSession session = getSession();
		NotificationHelper.notifyEnterpriseAdmins(getThreadLocalRequest(), session, enterprise, item, message);
	}

	@Override
	public HashMap<User, List<TypeUser>> getEnterpriseUsers(int folderId) throws ServiceException {
		CommonSession session = getSession();
		try {
			Enterprise enterprise = session.getAklaboxService().getEnterpriseParent(folderId, bpm.document.management.core.model.IObject.ItemType.FOLDER);

			HashMap<User, List<TypeUser>> enterpriseUsers = new LinkedHashMap<>();
			if (enterprise != null) {
				List<User> admins = getUserEnterprise(enterprise, TypeUser.ADMIN);
				List<User> readers = getUserEnterprise(enterprise, TypeUser.READER);
				List<User> mailing = getUserEnterprise(enterprise, TypeUser.MAILER);
				List<User> validators = getUserEnterprise(enterprise, TypeUser.VALIDATOR);
				List<User> archives = getUserEnterprise(enterprise, TypeUser.ARCHIVE);
				List<User> signatories = getUserEnterprise(enterprise, TypeUser.SIGNER);

				loadUserEnterprises(enterpriseUsers, admins, TypeUser.ADMIN);
				loadUserEnterprises(enterpriseUsers, readers, TypeUser.READER);
				loadUserEnterprises(enterpriseUsers, mailing, TypeUser.MAILER);
				loadUserEnterprises(enterpriseUsers, validators, TypeUser.VALIDATOR);
				loadUserEnterprises(enterpriseUsers, archives, TypeUser.ARCHIVE);
				loadUserEnterprises(enterpriseUsers, signatories, TypeUser.SIGNER);
			}
			return enterpriseUsers;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get user for this enterprise: ", false);
		}
	}

	private void loadUserEnterprises(HashMap<User, List<TypeUser>> enterpriseUsers, List<User> users, TypeUser type) {
		if (users != null) {
			for (User user : users) {
				User key = findKey(user, enterpriseUsers.keySet());

				List<TypeUser> types = new ArrayList<>();
				if (key == null) {
					enterpriseUsers.put(user, types);
				} else {
					types = enterpriseUsers.get(key);
				}
				types.add(type);
			}
		}
	}

	private User findKey(User user, Set<User> keySet) {
		if (keySet != null) {
			for (User key : keySet) {
				if (key.getUserId() == user.getUserId()) {
					return key;
				}
			}
		}
		return null;
	}

	@Override
	public void notifyUser(User notifyUser, IObject item, String message) throws ServiceException {
		CommonSession session = getSession();
		NotificationHelper.notifyUser(getThreadLocalRequest(), session, session.getUser(), notifyUser, item, message);
	}

	@Override
	public boolean hasForms(Documents document, LinkType linkType) throws ServiceException {
		try {
			return getSession().getAklaboxService().hasForms(document, linkType);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean checkIfFormsAreComplete(Documents document, LinkType linkType) throws ServiceException {
		try {
			return getSession().getAklaboxService().checkIfFormsAreCompleted(document, linkType);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void requestValidation(List<User> validators, Tree item, String messageReceive, int delay) throws ServiceException {
		HttpServletRequest request = getThreadLocalRequest();
		CommonSession session = getSession();

		try {
			Date dueDate = addDayToDate(new Date(), delay);
			
			for (User validator : validators) {
				ItemValidation validation = new ItemValidation(item.getId(), session.getUser().getUserId(), ItemValidation.ValidationStatus.ASK_VALIDATION, ItemType.MARKET);
				validation.setValidatorUserId(validator.getUserId());
				validation.setDueDate(dueDate);

				NotificationHelper.notifyUser(request, session, session.getUser(), validator, item, messageReceive);

				session.getAklaboxService().manageItemValidation(validation, ManagerAction.SAVE);

				Tasks taskValidation = new Tasks();
				taskValidation.setDueDate(dueDate);
				taskValidation.setTaskGiverEmail(session.getUser().getEmail());
				taskValidation.setStatus(TaskStatus.VALIDATION_MARKET);
				taskValidation.setUserEmail(validator.getEmail());
				taskValidation.setDocId(item.getId());
				taskValidation.setTaskStatus("New");
				taskValidation.setTaskTitle(item.getName());

				session.getAklaboxService().saveTask(taskValidation);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "A problem occured: ", false);
		}
	}

	@Override
	public void requestValidation(List<User> validators, AkladematAdminEntity<? extends IAdminDematObject> item, String messageReceive, int delay) throws ServiceException {
		HttpServletRequest request = getThreadLocalRequest();
		CommonSession session = getSession();
		User currentUser = session.getUser();

		try {
			IObject document = session.getAklaboxService().getDocById(item.getIdDoc());

			Date dueDate = addDayToDate(new Date(), delay);
			for (User validator : validators) {
				ItemValidation validation = new ItemValidation(item.getId(), currentUser.getUserId(), ItemValidation.ValidationStatus.ASK_VALIDATION, ItemType.DEMAT);
				validation.setValidatorUserId(validator.getUserId());
				validation.setDueDate(dueDate);

				NotificationHelper.notifyUser(request, session, session.getUser(), validator, document, messageReceive);

				session.getAklaboxService().manageItemValidation(validation, ManagerAction.SAVE);

				if (delay > 0) {
					Tasks taskValidation = new Tasks();
					taskValidation.setDueDate(dueDate);
					taskValidation.setTaskGiverEmail(session.getUser().getEmail());
					taskValidation.setStatus(TaskStatus.VALIDATION_DEMAT);
					taskValidation.setUserEmail(validator.getEmail());
					taskValidation.setDocId(item.getIdDoc());
					taskValidation.setTaskStatus("New");
					taskValidation.setTaskTitle(item.getName());

					session.getAklaboxService().saveTask(taskValidation);
				}
			}

			actionOnAkladematAdminEntity(item, Status.ASSIGNED, null, currentUser, null, false, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "A problem occured: ", false);
		}
	}

	private Date addDayToDate(Date date, int nbDays) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, nbDays);
		return c.getTime();
	}

	@Override
	public void validateItem(ItemValidation validation, Enterprise enterprise, Tree item, String messageAsk, String messageReceive) throws ServiceException {
		HttpServletRequest request = getThreadLocalRequest();
		CommonSession session = getSession();

		try {
			switch (validation.getStatus()) {
			case ASK_INSTRUCTION:
				NotificationHelper.notifyEnterpriseAdmins(request, session, enterprise, item, messageReceive);
				break;
			case INSTRUCTED:
				int instructionFolderId = enterprise.getInstructionFolderId();
				if (instructionFolderId <= 0) {
					throw new ServiceException("The instruction folder is not configured.");
				}
				item.setParentId(instructionFolderId);
				item.setActivateFolder(true);
				session.getAklaboxService().updateDirectory(item);

				User author = session.getAklaboxService().getUserInfoThroughId(validation.getAskUserId() + "");
				NotificationHelper.notifyUser(request, session, session.getUser(), author, item, messageAsk);
				break;
			case ASK_VALIDATION:
				User validator = session.getAklaboxService().getUserInfoThroughId(validation.getValidatorUserId() + "");
				NotificationHelper.notifyUser(request, session, session.getUser(), validator, item, messageReceive);
				break;
			case VALIDATED:
			case UNVALIDATED:
				author = session.getAklaboxService().getUserInfoThroughId(validation.getAskUserId() + "");
				NotificationHelper.notifyUser(request, session, session.getUser(), author, item, messageAsk);
				break;
			default:
				break;
			}

			session.getAklaboxService().manageItemValidation(validation, validation.getId() <= 0 ? ManagerAction.SAVE : ManagerAction.UPDATE);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "A problem occured: ", false);
		}
	}

	@Override
	public List<ItemValidation> getItemValidations(int itemId, ItemType type) throws ServiceException {
		try {
			List<ItemValidation> validations = getSession().getAklaboxService().getItemValidations(itemId, type);
			if (type == ItemType.DEMAT) {
				List<ItemValidation> filters = new ArrayList<>();
				for (ItemValidation validation : validations) {
					if (validation.getStatus() == ValidationStatus.ASK_VALIDATION) {
						filters.add(validation);
					}
				}
				return filters;
			}
			return validations;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get validations: ", false);
		}
	}

	@Override
	public Documents postProcessDocument(Enterprise enterpriseParent, int documentId) throws ServiceException {
		CommonSession session = getSession();
		try {
			return session.getDocumentService().postProcessDocument(enterpriseParent, documentId);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to post process the document: ", false);
		}
	}

	@Override
	public List<Versions> getVersions(Documents doc) throws ServiceException {
		try {
			return getSession().getAklaboxService().getVersions(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<DocPages> getPages(int docId, int versionNumber) throws ServiceException {
		try {
			Versions docVersion = new Versions();
			docVersion.setDocId(docId);
			docVersion.setVersionNumber(versionNumber);
			return getSession().getAklaboxService().getPages(docVersion);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public Versions getSpecificVersion(Versions version, String email, boolean isDownload) throws ServiceException {
		try {
			if (getSession().getAklaboxService().getSpecificVersion(version) != null) {
				// TODO: REFACTOR ENCRYPTION - Refactor the system someday
				// new
				// EncryptionTemplate().decryptForVersion(getSession().getAklaboxService().getSpecificVersion(version),
				// getSession());

				Documents doc = getDocInfo(version.getDocId());

				Tree folder = getDirectoryInfo(doc.getParentId());

				// if ((doc.getSecurityStatus().equals("Shared"))) {
				if (isDownload) {
					workFlowNotifier = new WorkFlowNotifier(getSession(), doc);
					workFlowNotifier.sendDocCondition(email, false, true, false, false, false);

					workFlowNotifier.sendFolderCondition(folder, email, false, false, false, false, true, false, getThreadLocalRequest(), null);
				}
				// }

				return getSession().getAklaboxService().getSpecificVersion(version);
			} else {
				throw new Exception("Version is null!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<OCRSearchResult> searchDocContent(String query, int docId, int versionNumber) throws ServiceException {
		try {
			Documents doc = getDocInfo(docId);
			String pdfPath = doc.getPdfPath();

			PDFTextExtractor textPosition = new PDFTextExtractor();
			List<OCRSearchResult> results = textPosition.searchLocation(pdfPath, query);

			return results;
		} catch (Throwable e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException((Exception)e, "", false);
		}
	}

	@Override
	public List<Comments> getComments(int docId, CommentStatus status) throws ServiceException {
		try {
			return getSession().getAklaboxService().getComments(docId, status);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void generateVideoThumb(int versionNumber, int sec, Documents doc) throws ServiceException {
		try {
			getSession().getAklaboxService().deleteDocPage(doc.getId(), versionNumber);
			new VideoThumbCreator(versionNumber, doc, getSession(), doc.getFilePath(), doc.getFilePath(), sec);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}

	}

	@Override
	public Documents getDocInfo(int docId) throws ServiceException {
		try {
			Documents doc = getSession().getAklaboxService().getDocInfo(docId);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public Tree getDirectoryInfo(int id) throws ServiceException {
		try {
			return getSession().getAklaboxService().getDirById(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void treatImage(TreatmentImageObject treatment, Documents doc) throws ServiceException {
		try {
			getSession().getAklaboxService().treatImage(treatment, doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void returnToOriginal(Documents doc) throws ServiceException {

		try {
			getSession().getAklaboxService().treatImage(null, doc);

		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void removeComment(Comments comment) throws ServiceException {
		try {
			getSession().getAklaboxService().removeComments(comment);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void updateComment(Comments comment) throws ServiceException {
		try {
			getSession().getAklaboxService().updateComment(comment);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void saveComment(Comments comment, String userType) throws ServiceException {
		try {
			getSession().getAklaboxService().saveComments(comment);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public User getUserInfo(String email) throws Exception {
		try {
			return getSession().getAklaboxService().getUserInfo(email);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public String searchCocktailEngagement(String numEj) throws Exception {
		try {
			return getSession().getAkladematService().searchCocktailEngagement(numEj);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public PermissionItem managePermissionItem(IObject item, PermissionItem permissionItem, ManagerAction action, ShareType shareType, List<Permission> permissions, boolean savePermission) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getAklaboxService().managePermissionItem(item, permissionItem, action, shareType, permissions, savePermission);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to manage item permission", false);
		}
	}

	@Override
	public List<IObject> getItems(Integer parentId, PermissionItem permissionParent, ItemTreeType treeItemType, IObject.ItemType type, Boolean isDeleted, Boolean isActivated, boolean checkPermission, boolean loadChilds) throws ServiceException {
		try {
			CommonSession session = getSession();
			int userId = session.getUser().getUserId();

			return session.getAklaboxService().getItems(userId, parentId, permissionParent, treeItemType, type, isDeleted, isActivated, checkPermission, loadChilds);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<Permission> getPermissions(int permissionItemId, ShareType type, boolean lightWeight) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getAklaboxService().getPermissions(permissionItemId, type, lightWeight);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get list of permissions", false);
		}
	}

	@Override
	public PermissionItem getPermissionItemByHash(String hash) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getAklaboxService().getPermissionItem(hash);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get the shared document with this hash.", false);
		}
	}

	@Override
	public List<Group> getGroups() throws Exception {
		return getSession().getAklaboxService().getGroups();
	}

	@Override
	public List<Group> getGroupsByUser(String email) throws Exception {
		return getSession().getAklaboxService().getGroupsByUser(email);
	}

	@Override
	public List<User> getUsersByGroup(List<Group> groups, Integer enterpriseId) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getAklaboxService().getUsersByGroup(groups, enterpriseId);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get the shared document with this hash.", false);
		}
	}

	@Override
	public IObject getItem(int itemId, IObject.ItemType type, ItemTreeType itemTreeType, Boolean isDeleted, boolean checkPermission, boolean loadParent, boolean lightWeight) throws ServiceException {
		CommonSession session = getSession();

		try {
			return session.getAklaboxService().getItem(itemId, type, itemTreeType, isDeleted, checkPermission, loadParent, lightWeight);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get the item with id: " + itemId, false);
		}
	}

	@Override
	public List<IObject> getItems(Tree parent, ItemTreeType itemTreeType) throws ServiceException {
		CommonSession session = getSession();

		User currentUser = getSession().getUser();
		Integer parentId = parent != null ? parent.getId() : null;
		PermissionItem permissionParent = parent != null ? parent.getPermissionItem() : null;

		try {
			return session.getAklaboxService().getItems(currentUser != null ? currentUser.getUserId() : null, parentId, permissionParent, itemTreeType, IObject.ItemType.ALL, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get items", false);
		}

		// switch (type) {
		// case ENTERPRISE:
		// break;
		// case MY_DOCUMENTS:
		// if (parentId == null) {
		// return getItems(currentUser.getEmail(), false);
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case PUBLIC:
		// if (parentId == null) {
		// return getPublicTree();
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case SHARED_BY_ME:
		// if (parentId == null) {
		// return getSharedByMe(currentUser.getEmail());
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case SHARED_WITH_ME:
		// if (parentId == null) {
		// return getUserShare(currentUser.getEmail());
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case STARRED:
		// if (parentId == null) {
		// List<IObject> result = new ArrayList<IObject>();
		//
		// List<Documents> documents =
		// getStarredDocuments(currentUser.getEmail());
		// if (documents != null) {
		// for (Documents doc : documents) {
		// result.add(doc);
		// }
		// }
		// return result;
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case ADDED_RECENTLY:
		// if (parentId == null) {
		// List<IObject> result = new ArrayList<IObject>();
		//
		// List<Documents> documents = getDocList();
		// if (documents != null) {
		// for (Documents d : documents) {
		// if (!d.isXakl()) {
		// if (d.getUserId() == currentUser.getUserId()) {
		// if (d.getCreationDate().getYear() == new Date().getYear() &&
		// d.getCreationDate().getMonth() == new Date().getMonth()) {
		// result.add(d);
		// }
		// }
		// }
		// }
		//
		// Collections.sort(result, new Comparator<IObject>() {
		// @Override
		// public int compare(IObject o1, IObject o2) {
		// return o2.getCreationDate().compareTo(o1.getCreationDate());
		// }
		// });
		// }
		// return result;
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case VIEWED_RECENTLY:
		// if (parentId == null) {
		// List<IObject> result = new ArrayList<IObject>();
		//
		// List<DocViews> docViewsResult =
		// getSession().getAklaboxService().getDocViews(currentUser.getEmail());
		//
		// List<DocViews> docViews = new ArrayList<DocViews>();
		// for (DocViews d : docViewsResult) {
		// if (d.getViewedDate().getYear() == new Date().getYear() &&
		// d.getViewedDate().getMonth() == new Date().getMonth()) {
		// docViews.add(d);
		// }
		// }
		//
		// Collections.sort(docViews, new Comparator<DocViews>() {
		// @Override
		// public int compare(DocViews o1, DocViews o2) {
		// return o1.getViewedDate().compareTo(o2.getViewedDate());
		// }
		// });
		//
		// for (DocViews d : docViews) {
		// result.add(getDocInfo(d.getDocId()));
		// }
		// return result;
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case MOST_VIEWED:
		// if (parentId == null) {
		// List<IObject> result = new ArrayList<IObject>();
		//
		// List<Documents> documents = getDocList();
		// List<DocViews> docViews =
		// getSession().getAklaboxService().getDocViews(currentUser.getEmail());
		//
		// for (Documents doc : documents) {
		// for (DocViews docView : docViews) {
		// if (doc.getId() == docView.getDocId()) {
		// result.add(doc);
		// }
		// }
		// }
		//
		// for (IObject item : result) {
		// Documents doc = (Documents) item;
		// for (DocViews dView : docViews) {
		// if (doc.getId() == dView.getDocId()) {
		// doc.setViewNumber(doc.getViewNumber() + 1);
		// }
		// }
		// }
		//
		// Collections.sort(result, new Comparator<IObject>() {
		// @Override
		// public int compare(IObject o1, IObject o2) {
		// if (o1 instanceof Documents && o2 instanceof Documents) {
		// return ((Documents) o2).getViewNumber() - ((Documents)
		// o1).getViewNumber();
		// }
		// return 0;
		// }
		// });
		//
		// return result;
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case VALIDATE_DOC:
		// if (parentId == null) {
		// List<IObject> result = new ArrayList<IObject>();
		//
		// List<Documents> documents = getDocList();
		// for (Documents doc : documents) {
		// if (!doc.isXakl() && doc.getSecurityStatus().equals("Enterprise") &&
		// doc.getUserValidator().contains(currentUser.getEmail())) {
		// result.add(doc);
		// }
		// }
		// return result;
		// }
		// else {
		// return getSubItems(parentId);
		// }
		// case RECYCLE:
		// return getSession().getAklaboxService().getItems(null, null, null,
		// ItemTreeType.RECYCLE, IObject.ItemType.ALL, true, null, false,
		// false);
		// default:
		// break;
		// }
		// return new ArrayList<IObject>();
	}

	@Override
	public void saveMailServers(List<MailServer> mailServers) throws ServiceException {
		try {
			getSession().getAklaboxService().saveMailServers(mailServers);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to save mail servers : ", false);
		}
	}

	@Override
	public List<MailServer> getMailServers() throws ServiceException {
		try {
			return getSession().getAklaboxService().getMailServers();
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get mail servers : ", false);
		}
	}

	@Override
	public List<AkladematAdminEntity<MailEntity>> getMailEntities(int mailServerId, MailFilter filter) throws ServiceException {
		try {
			return getSession().getAkladematService().getMailEntities(mailServerId, filter);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to browse mails : ", false);
		}
	}
	
	@Override
	public List<Form> getAllFormByType(FormType type) throws ServiceException {
		try {
			return getSession().getAklaboxService().getAllFormByType(type);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get forms : ", false);
		}
		
	}
	
	@Override
	public List<OrganigramElement> getOrganigram() throws Exception {
		try {
			return getSession().getAklaboxService().getOrganigram();
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get organigram : ", false);
		}
	}

	@Override
	public List<Form> getOrbeonForms() throws ServiceException {
		String orbeonUrl = ConfigurationManager.getProperty(ConfigurationAklaboxConstants.P_AKLABOX_ORBEON_URL);

		List<Form> forms = new ArrayList<Form>();
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(orbeonUrl + "/fr/service/persistence/form").openConnection();

			InputStream is = con.getInputStream();

			SAXReader reader = new SAXReader();
			Document document = reader.read(is);
			
			//the orbeon xml for the form list looks like this
			//<forms>
			//	<form>
			//		<xxx>
			//		<yyy>
			Element formsElem = document.getRootElement();
			for(Element formElem : (List<Element>)formsElem.elements("form")) {
				Form form = new Form();
				
				form.setFormName(formElem.element("title").getText());
				form.setOrbeonApp(formElem.element("application-name").getText());
				form.setOrbeonName(formElem.element("form-name").getText());
				
				forms.add(form);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return forms;
	}

	@Override
	public List<OrbeonFormSection> getOrbeonFormSection(Form selectedObject) throws ServiceException {
		
		try {
			return getSession().getAklaboxService().getOrbeonFormSections(selectedObject);
		} catch (Exception e) {
			throw ExceptionHelper.getClientException(e, "Unable to browse orbeon : ", false);
		}
	}

	@Override
	public List<Workflow> getAllWorkflows(Type type) throws ServiceException {
		try {
			return getSession().getAklaflowService().getAllWorkflowsByType(type);
		} catch (Exception e) {
			throw ExceptionHelper.getClientException(e, "Unable to browse orbeon : ", false);
		}
	}
	
	@Override
	public List<bpm.gwt.aklabox.commons.shared.Log> saveInstance(Instance instance, Workflow workflow, User user) throws Exception {
		//instance.setInstanceStatus(IInstanceStatus.STOPED);
		List<bpm.gwt.aklabox.commons.shared.Log> logs = new ArrayList<bpm.gwt.aklabox.commons.shared.Log>();
		try {
			for (WorkflowLog l : getSession().getAklaflowService().saveInstance(instance, workflow, user, getServletContext().getRealPath(""))) {
				bpm.gwt.aklabox.commons.shared.Log log = new bpm.gwt.aklabox.commons.shared.Log(l.getMessage(), l.getDate(), l.getType(), getSession().getAklaflowService().getActivity(l.getActivityId(), workflow));
				logs.add(log);
			}
			getSession().getAklaflowService().deployWorkflow(getWorkflow(instance.getWorkflowId()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		return logs;
	}
	
	@Override
	public Workflow getWorkflow(int workflowId) throws Exception {
		try {
			return getSession().getAklaflowService().getWorkflow(workflowId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public String getOrbeonUrl(Documents doc) throws Exception {
		User currentUser = getSession().getUser();
		OrbeonWorkflowInformation info = getSession().getAklaflowService().getOrbeonInformation(doc);
		
		Form form = info.getForm();
		
//		StringBuilder buf = new StringBuilder("/AklaBox/orbeonServlet");
		StringBuilder buf = new StringBuilder("/orbeonServlet");
		buf.append("?action=new");
		buf.append("&app=");
		buf.append(form.getOrbeonApp());
		buf.append("&name=");
		buf.append(form.getOrbeonName());
		buf.append("&workflowdoc=");
		buf.append(doc.getId());
		
		OrbeonFormInstance formInstance = getSession().getAklaboxService().getOrbeonFormInstance(form, doc);
		if(formInstance.getOrbeonInstanceId() != null && !formInstance.getOrbeonInstanceId().isEmpty()) {
			buf.append("&instance=");
			buf.append(formInstance.getOrbeonInstanceId());
		}
		
		
		
		return buf.toString();
	}
	
	@Override
	public List<Instance> getAllInstanceByUser() throws Exception {
		try {
			User user = getSession().getAklaboxService().getUserInfo(getSession().getUser().getEmail());
			if(user.getSuperUser()){
				return getSession().getAklaflowService().getAllInstances();
			} else {
				return getSession().getAklaflowService().getAllInstanceByUser(user.getUserId());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public List<bpm.gwt.aklabox.commons.shared.Log> updateInstance(Instance instance) throws Exception {
		Workflow workflow = getSession().getAklaflowService().getWorkflow(instance.getWorkflowId());
		List<bpm.gwt.aklabox.commons.shared.Log> logs = new ArrayList<bpm.gwt.aklabox.commons.shared.Log>();
		try {
			for (WorkflowLog l : getSession().getAklaflowService().updateInstance(instance, workflow, getSession().getAklaboxService().getUserInfo(getSession().getUser().getEmail()), getServletContext().getRealPath(""))) {
				bpm.gwt.aklabox.commons.shared.Log log = new bpm.gwt.aklabox.commons.shared.Log(l.getMessage(), l.getDate(), 
						l.getType(), getSession().getAklaflowService().getActivity(l.getActivityId(), ((l.getWorkflow() !=null)? l.getWorkflow() :workflow)));
				logs.add(log);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, e.getMessage(), false);
		}

		return logs;
	}

	@Override
	public Tree getAllAklaboxFiles(Activity activity, int versionNumber) throws Exception {
		int id = getSession().getAklaflowService().getAklaBoxFiles(activity, versionNumber).get(0).getFileId();
		return getSession().getAklaboxService().getDirById(id);
	}

	@Override
	public List<Documents> extractZip(Documents doc, int fileId) throws ServiceException {
		try {
			CommonSession session = getSession();
			session.getDocumentService().analyzeZip(doc.getId(), fileId);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public List<Documents> extractZipFromAklaFlow(UnzipFileActivity unzipFileActivity, int versionNumber) throws ServiceException {
		try {
			List<AklaBoxFiles> files = getSession().getAklaflowService().getAklaBoxFiles(unzipFileActivity, versionNumber);
			List<Documents> docs = new ArrayList<Documents>();
			for (AklaBoxFiles file : files) {
				docs.addAll(extractZip(getDocInfo(file.getFileId()), unzipFileActivity.getFileDestination()));
			}
			return docs;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}
	
	@Override
	public List<Activity> getOrbeonNextActivity(Workflow workflow, Instance instance) throws Exception {
		try {
			return getSession().getAklaflowService().getOrbeonNextActivity(workflow, instance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public List<User> getAllUsers() throws Exception {
		try {
			return getSession().getAklaboxService().getAllUsers();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public List<Activity> getActivitiesByWorkflow(Workflow workflow) throws Exception {
		try {
			return getSession().getAklaflowService().getAllWorkflowActivities(workflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public Instance getLastWorkflowInstance(Workflow workflow) throws Exception {
		try {
			return getSession().getAklaflowService().getLastInstance(workflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, e.getMessage(), false);
			
		}
	}

	@Override
	public List<City> getCities(int depId) throws Exception {
		return getSession().getAklaboxService().getCities(depId);
	}
	
	@Override
	public List<Departement> getDepartements(int countryId) throws Exception {
		return getSession().getAklaboxService().getDepartements(countryId);
	}

	@Override
	public List<TypeTask> getTaskTypes() throws Exception {
		
		return getSession().getAklaboxService().getTypeTask();
	}

	@Override
	public void saveFormField(FormField formField) throws Exception {
		getSession().getAklaboxService().saveFormField(formField);
	}

	@Override
	public void deleteFormField(FormField formField) throws Exception {
		getSession().getAklaboxService().deleteFormField(formField);
	}

	@Override
	public List<FormField> getAllFormField(int formId) throws Exception {
		return getSession().getAklaboxService().getAllFormFields(formId);
	}

	@Override
	@Deprecated
	public void saveFormFieldValue(int docId, List<FormFieldValue> values) throws Exception {
		for (FormFieldValue value : values) {
			value.setUserId(getSession().getAklaboxService().getUserInfo(getSession().getUser().getEmail()).getUserId());
		}
		getSession().getAklaboxService().saveFormFieldValue(docId, values);

		for (FormFieldValue value : values) {
			if (value.getType() == FormFieldType.CALENDAR_TYPE) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				FormField ff = getSession().getAklaboxService().getFormField(value.getFormFieldId());
				String[] val = value.getValue().split(";");
				String date = val[0];
				String email = val[1];

				Tasks task = new Tasks();
				task.setCreationDate(new Date());
				task.setDocId(value.getDocId());
				try {
					task.setDueDate(sdf.parse(date));
				} catch (Exception e) {
					e.printStackTrace();
				}
				task.setUserEmail(email);
				task.setTaskGiverEmail(getSession().getUser().getEmail());
				task.setTypeTaskId(ff.getTaskTypeId());

				getSession().getAklaboxService().saveTask(task);
			}
		}
	}

	@Override
	public void updateForm(Form form) throws Exception {
		getSession().getAklaboxService().updateForm(form);
	}

	@Override
	public FormFieldValue getFormFieldValueByDoc(int ffId, int userId, int docId) throws ServiceException {
		try {
			userId = getSession().getUser().getUserId();
			return getSession().getAklaboxService().getFormFieldValueByDoc(ffId, userId, docId);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "", false);
		}
	}

	@Override
	public void addListValue(LOV listOfValues, String value) throws Exception {
		getSession().getAklaboxService().addListValue(listOfValues, value);
		
	}

	@Override
	public Form getForm(int formId) throws Exception {
		try {
			return getSession().getAklaboxService().getForm(formId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public List<Country> getCountry() throws Exception {
		try {
			return getSession().getAklaboxService().getCountry();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public FileType getFileType(int fileTypeId) throws Exception {
		try {
			return getSession().getAklaboxService().getFileType(fileTypeId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	@Override
	public void sendDelegationNotification(int userId, Instance instance) throws Exception {
		try {
			getSession().getAklaflowService().sendOrbeonNotification(userId, instance);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void sendEmails(List<EmailInfo> emails) throws Exception {
		CommonSession session = getSession();
		session.getAklaboxService().sendEmails(emails);
	}

	@Override
	public void addTask(Documents documents, User u, Tasks t, String message) throws Exception {
		CommonSession session = getSession();
		getSession().getAklaboxService().saveTask(t);

		HttpServletRequest request = getThreadLocalRequest();
		NotificationHelper.notifyUser(request, session, session.getUser(), u, documents, message);
	}

	@Override
	public Instance getInstanceById(int id) throws Exception {
		CommonSession session = getSession();
		return session.getAklaflowService().getInstancebyId(id);
	}

	@Override
	public void sendArchiveOzwillo(HashMap<Archiving, AkladematAdminEntity<Archive>> entities) throws Exception {
		getSession().getAkladematService().sendArchiveOzwillo(entities);
	}
	
	@Override
	public List<String> getColumnsbyRequest(SourceConnection connection, String request) throws Exception {
		try {
			return getSession().getAklaboxService().getColumnsbyRequest(connection, request);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	@Override
	public List<FileType> getAllFileTypes(AklaboxConnection server) throws Exception {
		try {
			IVdmManager manager = null;
			if (server != null) {
				try {
					manager = buildManager(server);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				manager = getSession().getAklaboxService();
			}
			return manager.getAllFilesType();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
	
	private int getDynamicFolderForBill(AkLadExportObject entity) throws Exception{
		ScanDocumentType settings = entity.getPagesMeta().get(0).getSelectedType();
		if(settings != null && settings.getIdFolderParent() != 0){
			String num_eng = entity.getChorusMetadata().getFactureNumeroEngagement();
			Enterprise enterprise = getSession().getAklaboxService().getEnterpriseParent(settings.getIdFolderParent(), bpm.document.management.core.model.IObject.ItemType.FOLDER);
			int idRoot = (enterprise.getActiveFolderId() != 0)? enterprise.getActiveFolderId() : settings.getIdFolderParent();
			
			if(settings.getPatterns() != null && num_eng != null && !num_eng.isEmpty()){
				int id = getFolderFromRegex(num_eng, idRoot, settings.getPatternsAsList(), 0);
				return id;
			} else {
				//return settings.getIdFolderParent();
				return idRoot;
			}
		} else {
			throw new Exception("Default Folder Missing");
		}
				
		//return activity.getItemId() != null ? activity.getItemId() : 0;
	}
	
	private int getFolderFromRegex(String num_eng, int idRoot, List<String> patterns, int index) throws Exception{
		if(index == patterns.size()){
			return idRoot;
		}
		
		Pattern pat = Pattern.compile(patterns.get(index));
		Matcher m = pat.matcher(num_eng);
		if(m.matches()){
			String name = m.group(1);
			Tree t = getSession().getAklaboxService().getDirectoryByNameParent(name, idRoot);
			if(t.getId() == 0){
				Tree folder = getSession().getAklaboxService().getDirById(idRoot); //parent
				
				Tree child = new Tree();
				child.setName(name);
				child.setParentId(idRoot);
				child.setDocumentIndexed(folder.getDocumentIndexed());
				child.setThumbnailCreated(folder.getThumbnailCreated());
				child.setFileTypeSelected(null);
				child.setNotified(folder.isNotified());
				child.setIsDeleted(false);
				child.setUserId(folder.getUserId());
				child.setValidationStatus("YES");
				child.setImmediateValidation(true);
				
				t = getSession().getAklaboxService().saveDirectory(child);
			}
			return getFolderFromRegex(num_eng, t.getId(), patterns, index+1);
		} else {
			return idRoot;
		}
	}
	
	@Override
	public List<FormFieldValue> getFormValuesFromSource(int docId, Form form, List<FormFieldValue> filters) throws Exception {
		CommonSession session = getSession();
		Documents doc  = session.getAklaboxService().getDocInfo(docId);
		return getSession().getAklaboxService().getFormValuesFromDataSource(form, filters, doc);

	}
	
	@Override
	public Enterprise getEnterpriseParent(int folderId) throws ServiceException {
		CommonSession session = getSession();
		try {
			Enterprise enterprise = session.getAklaboxService().getEnterpriseParent(folderId, bpm.document.management.core.model.IObject.ItemType.FOLDER);
			return enterprise;
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to get enterprise: ", false);
		}
	}
	
	@Override
	public Enterprise getEnterpriseParent(int folderId, AklaboxConnection server) throws Exception {
		try {
			IVdmManager manager = null;
			if (server != null) {
				try {
					manager = buildManager(server);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				manager = getSession().getAklaboxService();
			}
			return manager.getEnterpriseParent(folderId, bpm.document.management.core.model.IObject.ItemType.FOLDER);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void rejectCocktail(AkladematAdminEntity<Chorus> entity, String com) throws Exception {
		try {
			getSession().getAkladematService().rejectCocktail(entity, com);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
