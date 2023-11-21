package bpm.smart.web.client.services;

import java.util.List;

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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SmartAirServiceAsync {

	void getProjects(int idUser, AsyncCallback<List<AirProject>> callback);

	void addOrEditProject(AirProject project, AsyncCallback<Void> callback);

	void deleteProject(AirProject selectedProject, AsyncCallback<Void> callback);

	void executeProject(AirProject project, AsyncCallback<String> callback);

	void executeScript(RScriptModel box, AsyncCallback<RScriptModel> callback);
	
	void executeScript(RScriptModel box, List<Parameter> lovParameters, AsyncCallback<RScriptModel> callback);

	void loadMirrors(AsyncCallback<List<MirrorCran>> asyncCallback);

	void getAdmin(int idUser, AsyncCallback<SmartAdmin> asyncCallback);

	void addOrEditAdmin(SmartAdmin admin, AsyncCallback<Void> asyncCallback);

	void deleteAdmin(SmartAdmin admin, AsyncCallback<Void> callback);

	void getStatsDataset(List<Dataset> datasets, AsyncCallback<List<StatDataColumn>> asyncCallback);

	void getAvatarIconUrl(String avatar, AsyncCallback<String> asyncCallback);

	void addorEditScript(RScript script, AsyncCallback<Integer> callback);

	void addorEditScriptModel(RScriptModel script, AsyncCallback<Integer> callback);

	void deleteRScript(RScript script, AsyncCallback<Void> callback);

	void deleteRScriptModel(RScriptModel script, AsyncCallback<Void> callback);

	void getLastScriptModels(List<RScript> scripts, AsyncCallback<List<RScriptModel>> callback);

	void loadRScripts(int idProject, AsyncCallback<List<RScript>> callback);

	void loadAllRScripts(AsyncCallback<List<RScript>> callback);

	void checkInScript(RScript script, AsyncCallback<Void> asyncCallback);

	void checkOutScript(RScript script, AsyncCallback<String> asyncCallback);

	void getModelsbyScript(int id, AsyncCallback<List<RScriptModel>> callback);

	void getTempText(String url, AsyncCallback<String> asyncCallback);

	void exportToFile(String name, String content, String type, AsyncCallback<String> callback);

	void shareProject(int id, List<User> users, AsyncCallback<Void> asyncCallback);

	void getUsers(AsyncCallback<List<User>> asyncCallback);

	void getSharedProjectsUsersbyIdProject(int idProject, AsyncCallback<List<UsersProjectsShares>> callback);

	void addSessionStream(byte[] stream, String type, AsyncCallback<String> asyncCallback);

	void zipProject(AirProject project, boolean allVersions, AsyncCallback<String> callback);

	void addDatasetToR(Dataset dataset, AsyncCallback<RScriptModel> callback);

	void generateStatPlot(DataColumn col1, DataColumn col2, Dataset dataset, AsyncCallback<RScriptModel> callback);

	void renderMarkdown(String script, String name, List<String> outputs, List<Parameter> lovParameters, AsyncCallback<RScriptModel> asyncCallback);

	void getColumnDistinctValues(String datasetName, int column, AsyncCallback<List<String>> callback);

	void getDatasetColumns(String datasetName, AsyncCallback<List<DataColumn>> asyncCallback);

	void generateSummaryPlot(Dataset dataset, AsyncCallback<RScriptModel> asyncCallback);

	void executeActivity(Activity activity, AsyncCallback<ActivityLog> callback);

	void calculateRStats(List<DataColumn> columns, Dataset dataset, AsyncCallback<List<StatDataColumn>> callback);

	void createStatsDataset(Dataset dataset, AsyncCallback<List<StatDataColumn>> callback);

	void getDatasetsbyProject(AirProject proj, AsyncCallback<List<Dataset>> callback);

	void addDatasetsToR(List<Dataset> datasets, AsyncCallback<List<RScriptModel>> callback);

	void deleteLinkedDatasets(Dataset dataset, AsyncCallback<Void> asyncCallback);

	void getScreenCapture(String content, String type, AsyncCallback<byte[]> callback);

	void getUrlContent(String urlString, AsyncCallback<String> callback);
}
