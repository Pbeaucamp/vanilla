package bpm.gwt.commons.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import bpm.document.management.core.model.User;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.InfoShareCmis;
import bpm.gwt.commons.shared.InfoShareMail;
import bpm.gwt.commons.shared.cmis.CmisInformations;
import bpm.gwt.commons.shared.cmis.CmisItem;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeShareAklabox;
import bpm.gwt.commons.shared.viewer.CommentsInformations;
import bpm.gwt.commons.shared.viewer.DisplayItem;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.smart.core.model.RScriptModel;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.comments.CommentValue;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.validation.Validation;
import bpm.vanilla.platform.core.beans.validation.ValidationCircuit;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.ReportBackground;
import bpm.vanilla.platform.core.repository.RepositoryItem;

@RemoteServiceRelativePath("ReportingService")
public interface ReportingService extends RemoteService {
	public static class Connect {
		private static ReportingServiceAsync instance;

		public static ReportingServiceAsync getInstance() {
			if (instance == null) {
				instance = (ReportingServiceAsync) GWT.create(ReportingService.class);
			}
			return instance;
		}
	}

	public String runReport(LaunchReportInformations itemInfo) throws ServiceException;

	public String burstReport(LaunchReportInformations itemInfo, List<Group> groups, String htmlText, boolean sendMail, boolean addToGed, String gedName) throws ServiceException;

	public void saveReportParameterConfig(List<VanillaGroupParameter> groupParams, PortailRepositoryItem dto, String configName, String configDesc) throws ServiceException;

	public List<VanillaGroupParameter> getParameters(LaunchReportInformations itemInfo) throws ServiceException;

	public VanillaParameter getParameterValues(LaunchReportInformations itemInfo, List<VanillaGroupParameter> currentParams, String parameterName) throws ServiceException;

	public LaunchReportInformations getLaunchReportInformations(PortailRepositoryItem item, Group selectedGroup) throws ServiceException;
	
	public LaunchReportInformations getLaunchReportInformationsForDisco(PortailRepositoryItem item) throws ServiceException;
	
	public String runDashboard(LaunchReportInformations itemInfo) throws ServiceException;
	
	public String exportDashboard(String uuid, String url, String format, List<String> selectedFolders, boolean isLandscape) throws ServiceException;
	
	public String runWorkflow(LaunchReportInformations itemInfo) throws ServiceException;
	
	public String runGateway(LaunchReportInformations itemInfo) throws ServiceException;

	public String getForwardUrlForCubes(int fasdDirItemId, String cubeName, String viewName) throws ServiceException;
	
	public DisplayItem getDocumentUrl(int itemId) throws ServiceException;
	
	public DisplayItem openGedDocument(PortailRepositoryItem item) throws ServiceException;
	
	public String getExternalUrl(int itemId) throws ServiceException;
	
	public List<Group> getAvailableGroupsForHisto(int itemId) throws ServiceException;

	public List<DocumentDefinitionDTO> getAvailableDocuments(int itemId) throws ServiceException;
	
	public void historize(boolean histoForMe, Integer histoId, String reportName, List<Integer> groupIds, String gedName, String outputFormat, 
			boolean isCommentable, PortailRepositoryItem dto, Date peremptionDate) throws ServiceException;
	
	public void shareAklabox(String reportKey, String name, String dashboardUrl, List<String> selectedFormats, List<String> selectedFolders, TypeShareAklabox type, List<User> selectedUsers, 
			List<bpm.document.management.core.model.Group> selectedGroups, int selectedDirectoryId, boolean exportDashboard, boolean isLandscape) throws ServiceException;
	
	public List<Group> getAvailableGroupsForRun(int itemId, boolean isDirectory) throws ServiceException;

	public List<String> getAvailableFolders(String uuid) throws ServiceException;

	public ExportResult sendEmail(InfoShareMail infoShare) throws ServiceException;

	public LaunchReportInformations runReports(LaunchReportInformations itemInfo) throws ServiceException;

	public CommentsInformations addComments(RepositoryItem item, Validation validation, List<CommentValue> commentValues) throws ServiceException;

	public CommentsInformations modifyComments(RepositoryItem item, Validation validation, List<CommentValue> commentValues, boolean isLastCommentUnvalidate) throws ServiceException;

	public CommentsInformations validate(RepositoryItem item, Validation validation, boolean validate) throws ServiceException;
	
	public List<CommentValue> addModifyOrDeleteComment(CommentValue comment) throws ServiceException;

	public RScriptModel executeMarkdown(LaunchReportInformations item, String selectedFormat) throws Exception;
	
	public String getForwardUrlForKpi(int themeId, String selectedDate) throws ServiceException;

	public List<IRepositoryObject> getBackgroundReports(int limit) throws ServiceException;

	public void deleteReportBackground(ReportBackground report) throws ServiceException;

	public void updateReportBackground(ReportBackground item) throws ServiceException;

	public String getForwardUrlForKpiMap(PortailRepositoryItem item) throws ServiceException;

	public CmisInformations getCmisRepositories(CmisInformations cmisInfos, String properties) throws ServiceException;

	public List<CmisItem> getCmisFolders(CmisInformations cmisInfos, String folderId) throws ServiceException;

	public ExportResult shareCmis(InfoShareCmis infoShare) throws ServiceException;

	public String getForwardUrlForDataPreparation(int itemId) throws ServiceException;
	
	public CommentsInformations getValidation(RepositoryItem item) throws ServiceException;
	
	public void setReportValidation(RepositoryItem item, Validation validation, boolean launchValidationProcess) throws ServiceException;
	
	public List<PortailRepositoryItem> getPendingItemsToComment() throws ServiceException;
	
	public List<Validation> getValidations(boolean includeInactive) throws ServiceException;

	public ValidationCircuit manageValidationCircuit(ValidationCircuit circuit) throws ServiceException;
	
	public ValidationCircuit deleteValidationCircuit(ValidationCircuit circuit) throws ServiceException;

	public List<ValidationCircuit> getValidationCircuits() throws ServiceException;

	public void changeValidationNextActor(RepositoryItem item, Validation validation, bpm.vanilla.platform.core.beans.User user) throws ServiceException;
}
