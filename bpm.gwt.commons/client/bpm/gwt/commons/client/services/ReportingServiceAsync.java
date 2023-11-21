package bpm.gwt.commons.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import bpm.document.management.core.model.User;
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

public interface ReportingServiceAsync {

	public void runReport(LaunchReportInformations itemInfo, AsyncCallback<String> callback);

	public void burstReport(LaunchReportInformations itemInfo, List<Group> groups, String htmlText, boolean sendMail, boolean addToGed, String gedName, AsyncCallback<String> callback);

	public void saveReportParameterConfig(List<VanillaGroupParameter> groupParams, PortailRepositoryItem dto, String configName, String configDesc, AsyncCallback<Void> callback);

	public void getParameters(LaunchReportInformations itemInfo, AsyncCallback<List<VanillaGroupParameter>> callback);

	public void getParameterValues(LaunchReportInformations itemInfo, List<VanillaGroupParameter> currentParams, String parameterName, AsyncCallback<VanillaParameter> callback);

	public void getLaunchReportInformations(PortailRepositoryItem item, Group selectedGroup, AsyncCallback<LaunchReportInformations> callback);
	
	public void getLaunchReportInformationsForDisco(PortailRepositoryItem item, AsyncCallback<LaunchReportInformations> callback);

	public void runDashboard(LaunchReportInformations itemInfo, AsyncCallback<String> callback);

	public void exportDashboard(String uuid, String url, String format, List<String> selectedFolders, boolean isLandscape, AsyncCallback<String> callback);

	public void runWorkflow(LaunchReportInformations itemInfo, AsyncCallback<String> callback);

	public void runGateway(LaunchReportInformations itemInfo, AsyncCallback<String> callback);
	
	public void getForwardUrlForCubes(int fasdDirItemId, String cubeName, String viewName, AsyncCallback<String> callback);
	
	public void getDocumentUrl(int itemId, AsyncCallback<DisplayItem> callback);

	public void openGedDocument(PortailRepositoryItem item, AsyncCallback<DisplayItem> asyncCallback);

	public void getExternalUrl(int id, AsyncCallback<String> asyncCallback);
	
	public void getAvailableGroupsForHisto(int itemId, AsyncCallback<List<Group>> callback);
	
	public void getAvailableDocuments(int itemId, AsyncCallback<List<DocumentDefinitionDTO>> callback);

	public void historize(boolean histoForMe, Integer histoId, String reportName, List<Integer> groupIds, String gedName, String outputFormat, 
			boolean isCommentable, PortailRepositoryItem dto, Date peremptionDate, AsyncCallback<Void> callback);

	public void shareAklabox(String reportKey, String name, String dashboardUrl, List<String> selectedFormats, List<String> selectedFolders, TypeShareAklabox type, List<User> selectedUsers, 
			List<bpm.document.management.core.model.Group> selectedGroups, int selectedDirectoryId, boolean exportDashboard, boolean isLandscape, AsyncCallback<Void> asyncCallback);
	
	public void getAvailableGroupsForRun(int itemId, boolean isDirectory, AsyncCallback<List<Group>> callback);

	public void getAvailableFolders(String uuid, AsyncCallback<List<String>> callback);

	public void sendEmail(InfoShareMail infoShare, AsyncCallback<ExportResult> callback);

	public void runReports(LaunchReportInformations itemInfo, AsyncCallback<LaunchReportInformations> callback);

	public void addComments(RepositoryItem item, Validation validation, List<CommentValue> commentValues, AsyncCallback<CommentsInformations> callback);

	public void modifyComments(RepositoryItem item, Validation validation, List<CommentValue> commentValues, boolean isLastCommentUnvalidate, AsyncCallback<CommentsInformations> callback);

	public void validate(RepositoryItem item, Validation validation, boolean validate, AsyncCallback<CommentsInformations> callback);

	public void addModifyOrDeleteComment(CommentValue comment, AsyncCallback<List<CommentValue>> callback);

	public void executeMarkdown(LaunchReportInformations item, String selectedFormat, AsyncCallback<RScriptModel> callback);
	
	public void getForwardUrlForKpi(int themeId, String selectedDate, AsyncCallback<String> callback);

	public void getBackgroundReports(int limit, AsyncCallback<List<IRepositoryObject>> callback);

	public void deleteReportBackground(ReportBackground report, AsyncCallback<Void> callback);

	public void updateReportBackground(ReportBackground item, AsyncCallback<Void> callback);


	public void getForwardUrlForKpiMap(PortailRepositoryItem item, AsyncCallback<String> asyncCallback);

	public void getCmisRepositories(CmisInformations cmisInfos, String properties, AsyncCallback<CmisInformations> callback);

	public void getCmisFolders(CmisInformations cmisInfos, String folderId, AsyncCallback<List<CmisItem>> callback);
	
	public void shareCmis(InfoShareCmis infoShare, AsyncCallback<ExportResult> callback);

	public void getForwardUrlForDataPreparation(int itemId, AsyncCallback<String> callback);
	
	public void getValidation(RepositoryItem item, AsyncCallback<CommentsInformations> callback);

	public void getValidations(boolean includeInactive, AsyncCallback<List<Validation>> callback);
	
	public void setReportValidation(RepositoryItem item, Validation validation, boolean launchValidationProcess, AsyncCallback<Void> callback);
	
	public void getPendingItemsToComment(AsyncCallback<List<PortailRepositoryItem>> callback);

	public void manageValidationCircuit(ValidationCircuit circuit, AsyncCallback<ValidationCircuit> callback);

	public void deleteValidationCircuit(ValidationCircuit circuit, AsyncCallback<ValidationCircuit> callback);

	public void getValidationCircuits(AsyncCallback<List<ValidationCircuit>> callback);

	public void changeValidationNextActor(RepositoryItem item, Validation validation, bpm.vanilla.platform.core.beans.User user, AsyncCallback<Void> callback);
}
