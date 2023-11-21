package bpm.smart.core.xstream;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import bpm.smart.core.model.AirCube;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.MirrorCran;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.SmartAdmin;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.core.model.UsersProjectsShares;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.ActivityLog;

public interface ISmartManager {

	public static final String SMART_MANAGER_URL = "/smartManager";

	public static enum ActionType implements IXmlActionType {

		GET_AIR_PROJECTS(Level.DEBUG), UPDATE_AIR_PROJECT(Level.INFO), SAVE_AIR_PROJECT(Level.INFO), DELETE_AIR_PROJECT(Level.INFO), 
		EXECUTE_AIR_PROJECT(Level.DEBUG), LOAD_MIRRORS(Level.DEBUG), GET_SMART_ADMIN_BY_USER(Level.DEBUG), UPDATE_SMART_ADMIN(Level.INFO), 
		SAVE_SMART_ADMIN(Level.INFO), DELETE_SMART_ADMIN(Level.INFO), EXECUTE_SCRIPT_R(Level.DEBUG), LOAD_STATS_DATASET(Level.DEBUG), UPLOADICON(Level.DEBUG), 
		LOAD_EXT_DOC(Level.DEBUG), GET_AVATAR_URL(Level.DEBUG), SAVE_R_SCRIPT(Level.INFO), UPDATE_R_SCRIPT(Level.INFO), DELETE_R_SCRIPT(Level.INFO), 
		SAVE_R_SCRIPT_MODEL(Level.INFO), UPDATE_R_SCRIPT_MODEL(Level.INFO), DELETE_R_SCRIPT_MODEL(Level.INFO), GET_LAST_SCRIPT_MODELS(Level.DEBUG), 
		GET_R_SCRIPTS_BY_PROJECT(Level.DEBUG), GET_ALL_SCRIPTS(Level.DEBUG), CHECKIN_SCRIPT(Level.INFO), CHECKOUT_SCRIPT(Level.INFO), 
		GET_R_SCRIPT_MODELS_BY_SCRIPT(Level.DEBUG), GET_VISIBLE_AIR_PROJECTS(Level.DEBUG), SHARE_PROJECT(Level.INFO), 
		GET_USERS_PROJECTS_SHARES_BY_PROJECT(Level.DEBUG), ZIP_PROJECT(Level.INFO), ADD_DATASET_TO_R(Level.INFO), CHECK_NAME_PROJECT(Level.DEBUG), 
		SAVE_PROJECT_WITH_ELEMENTS(Level.INFO), GENERATE_STAT_PLOT(Level.DEBUG), RENDER_MARKDOWN(Level.DEBUG), UPLOADFILE(Level.INFO), 
		GET_CSV_METADATA(Level.DEBUG), GET_COLUMN_DISTINCT(Level.DEBUG), GENERATE_CSV_IN_R(Level.INFO), DELETE_FILE(Level.INFO), 
		GENERATE_SUMMARY_PLOT(Level.DEBUG), GET_DATASET_COLUMNS(Level.DEBUG), SAVE_AIR_CUBE(Level.INFO), GET_CUBES_BY_DATASET(Level.DEBUG), 
		EXECUTE_ACTIVITY(Level.INFO), CALCULATE_R_STATS(Level.INFO), CREATE_STATS_DATASET(Level.INFO), GET_DATASETS_BY_PROJECT(Level.DEBUG),
		ADD_DATASETS_TO_R(Level.INFO), DELETE_LINKED_DATASET(Level.INFO), GET_PROJECT_BY_ID(Level.DEBUG), GET_SCRIPT_BY_ID(Level.DEBUG), 
		GET_LAST_MODEL_BY_SCRIPT(Level.DEBUG), GET_SCRIPT_MODEL_BY_ID(Level.DEBUG), ADD_AIR_IMAGE(Level.INFO), GET_R_DATASET_DATA(Level.DEBUG), 
		LOAD_R_DATASET_TEMP(Level.DEBUG), LOAD_R_CSV_FILE(Level.DEBUG), LOAD_R_EXCEL_FILE(Level.DEBUG), ADD_R_DATASET_TEMPFILE(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}

	public List<AirProject> getAllAirProjects() throws Exception;

	public void updateAirProject(AirProject project) throws Exception;

	public int saveAirProject(AirProject project) throws Exception;

	public void deleteAirProject(AirProject project) throws Exception;

	public String executeAirProject(AirProject project) throws Exception;

	public RScriptModel executeScriptR(RScriptModel box, List<Parameter> lovParameters) throws Exception;

	public List<MirrorCran> loadMirrors() throws Exception;

	public List<SmartAdmin> getSmartAdminbyUser(int idUser) throws Exception;

	public void updateSmartAdmin(SmartAdmin admin) throws Exception;

	public void saveSmartAdmin(SmartAdmin admin) throws Exception;

	public void deleteSmartAdmin(SmartAdmin admin) throws Exception;

	public List<StatDataColumn> loadStatsDataset(List<Dataset> datasets) throws Exception;

	public String addAvatarIcon(String name, InputStream extDocStream, String format, boolean addDate) throws Exception;

	public InputStream importIcon(String name) throws Exception;

	public String getAvatarIconUrl(String filename) throws Exception;

	public int saveRScript(RScript script) throws Exception;

	public void deleteRScript(RScript script) throws Exception;

	public int saveRScriptModel(RScriptModel scriptmodel) throws Exception;

	public void updateRScriptModel(RScriptModel scriptmodel) throws Exception;

	public void deleteRScriptModel(RScriptModel scriptmodel) throws Exception;

	public List<RScriptModel> getLastScriptModels(List<RScript> scripts) throws Exception;

	public List<RScript> getRScriptsbyProject(int id) throws Exception;

	public List<RScript> getAllScripts() throws Exception;

	public void checkInScript(RScript script) throws Exception;

	public String checkOutScript(RScript script) throws Exception;

	public List<RScriptModel> getRScriptModelsbyScript(int id) throws Exception;

	public List<AirProject> getVisibleProjects(int idUser) throws Exception;

	public void shareProject(int id, List<User> users) throws Exception;

	public List<UsersProjectsShares> getSharedProjectsUsersbyIdProject(int id) throws Exception;

	public InputStream zipAirProject(int idProject, boolean allVersions) throws Exception;

	public RScriptModel addDatasettoR(Dataset dataset) throws Exception;

	public boolean checkProjectNameExistence(String projectName) throws Exception;

	public void saveAirProjectWithElements(InputStream imporProjectStream, String alternativeName, int idUser) throws Exception;

	public RScriptModel generateStatRPlot(DataColumn col1, DataColumn col2, Dataset dataset) throws Exception;

	public RScriptModel renderMarkdown(String script, String name, List<String> outputs, List<Parameter> lovParameters) throws Exception;

	public String uploadFile(String name, InputStream extDocStream) throws Exception;

	public List<DataColumn> getDatasourceCsvMetadata(Datasource datasourceCsv) throws Exception;

	public List<String> getColumnDistinctValues(String datasetName, int columnIndex) throws Exception;

	public String generateCSVinR(Dataset dts) throws Exception;

	public void deleteFile(String path) throws Exception;

	public void updateRScript(RScript script) throws Exception;

	public RScriptModel generateSummaryRPlot(Dataset dataset) throws Exception;

	public List<DataColumn> getDatasetColumns(String datasetName) throws Exception;

	public int saveAirCube(AirCube airCube) throws Exception;

	public List<AirCube> getCubesbyDataset(int idDataset) throws Exception;

	public ActivityLog executeActivity(Activity activity) throws Exception;

	public List<StatDataColumn> calculateRStats(List<DataColumn> columns, Dataset dataset) throws Exception;

	public List<StatDataColumn> createStatsDataset(Dataset dts) throws Exception;

	public List<Dataset> getDatasetsbyProject(AirProject proj) throws Exception;

	public List<RScriptModel> addDatasetstoR(List<Dataset> datasets) throws Exception;

	public void deleteLinkedDatasets(Dataset dataset) throws Exception;

	public RScriptModel getLastModelbyScript(int idScript) throws Exception;

	public AirProject getAirProjectbyId(int idProject) throws Exception;

	public RScript getScriptbyId(int idScript) throws Exception;

	public RScriptModel getScriptModelbyId(int idModel) throws Exception;

	public String addAirImage(String name, InputStream extDocStream, String format) throws Exception;

	public Map<String, List<Serializable>> getRDatasetData(Dataset dataset) throws Exception;

	public void loadRDatasetTemp(Dataset dataset) throws Exception;

	public String loadRCsvFile(byte[] resultStream, boolean hasHeader, String sep) throws Exception;

	public String loadRExcelFile(byte[] resultStream, boolean hasHeader, Dataset dataset) throws Exception;

	public void addRDatasetTempFile(Dataset dataset, String request, String nameTempFile) throws Exception;
}
