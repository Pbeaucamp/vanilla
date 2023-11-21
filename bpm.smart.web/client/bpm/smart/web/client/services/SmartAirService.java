package bpm.smart.web.client.services;

import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.MirrorCran;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.SmartAdmin;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.core.model.UsersProjectsShares;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.ActivityLog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("SmartAirService")
public interface SmartAirService extends RemoteService {

	public static class Connect {
		private static SmartAirServiceAsync instance;

		public static SmartAirServiceAsync getInstance() {
			if (instance == null) {
				instance = (SmartAirServiceAsync) GWT.create(SmartAirService.class);
			}
			return instance;
		}
	}

	public List<AirProject> getProjects(int idUser) throws ServiceException;

	public void addOrEditProject(AirProject project) throws ServiceException;

	public void deleteProject(AirProject selectedProject) throws ServiceException;

	public String executeProject(AirProject project) throws ServiceException;

	public RScriptModel executeScript(RScriptModel box) throws ServiceException;
	
	public RScriptModel executeScript(RScriptModel box, List<Parameter> lovParameters) throws ServiceException;

	public List<MirrorCran> loadMirrors() throws ServiceException;

	public SmartAdmin getAdmin(int idUser) throws ServiceException;

	public void addOrEditAdmin(SmartAdmin admin) throws ServiceException;

	public void deleteAdmin(SmartAdmin admin) throws ServiceException;

	public List<StatDataColumn> getStatsDataset(List<Dataset> datasets) throws ServiceException;

	public String getAvatarIconUrl(String avatar) throws ServiceException;

	public int addorEditScript(RScript script) throws ServiceException;

	public int addorEditScriptModel(RScriptModel script) throws ServiceException;

	public void deleteRScript(RScript script) throws ServiceException;

	public void deleteRScriptModel(RScriptModel script) throws ServiceException;

	public List<RScriptModel> getLastScriptModels(List<RScript> scripts) throws ServiceException;

	public List<RScript> loadRScripts(int idProject) throws ServiceException;

	public List<RScript> loadAllRScripts() throws ServiceException;

	public void checkInScript(RScript script) throws ServiceException;

	public String checkOutScript(RScript script) throws ServiceException;

	public List<RScriptModel> getModelsbyScript(int id) throws ServiceException;

	public String getTempText(String url) throws ServiceException;

	public String exportToFile(String name, String content, String type) throws ServiceException;

	public void shareProject(int id, List<User> users) throws ServiceException;

	public List<User> getUsers() throws ServiceException;

	public List<UsersProjectsShares> getSharedProjectsUsersbyIdProject(int idProject) throws ServiceException;

	public String addSessionStream(byte[] stream, String type) throws ServiceException;

	public String zipProject(AirProject project, boolean allVersions) throws ServiceException;

	public RScriptModel addDatasetToR(Dataset dataset) throws ServiceException;

	public RScriptModel generateStatPlot(DataColumn col1, DataColumn col2, Dataset dataset) throws ServiceException;

	public RScriptModel renderMarkdown(String script, String name, List<String> outputs, List<Parameter> lovParameters) throws ServiceException;
	
	public List<String> getColumnDistinctValues(String datasetName, int columnIndex) throws ServiceException;

	public RScriptModel generateSummaryPlot(Dataset dataset) throws ServiceException;

	public List<DataColumn> getDatasetColumns(String datasetName) throws ServiceException;

	public ActivityLog executeActivity(Activity activity) throws Exception;

	public List<StatDataColumn> calculateRStats(List<DataColumn> columns, Dataset dataset) throws ServiceException;

	public List<StatDataColumn> createStatsDataset(Dataset dataset) throws ServiceException;

	public List<Dataset> getDatasetsbyProject(AirProject proj) throws ServiceException;

	public List<RScriptModel> addDatasetsToR(List<Dataset> datasets) throws ServiceException;

	public void deleteLinkedDatasets(Dataset dataset) throws ServiceException;

	public byte[] getScreenCapture(String content, String type) throws ServiceException;

	public String getUrlContent(String urlString) throws ServiceException;

}
