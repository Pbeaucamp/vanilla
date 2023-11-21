package bpm.smart.core.xstream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import bpm.smart.core.model.AirCube;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.MirrorCran;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.SmartAdmin;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.core.model.UsersProjectsShares;
import bpm.smart.core.xstream.internal.HttpCommunicator;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.ActivityLog;

import com.thoughtworks.xstream.XStream;

public class RemoteSmartManager implements ISmartManager {

//	public static class SmartManagerHttpCommunicator extends HttpCommunicator {
//		@Override
//		protected void writeAdditionalHttpHeader(HttpURLConnection sock) {
////			sock.setRequestProperty(IVanillaComponentIdentifier.P_COMPONENT_NATURE, VanillaComponentType.COMPONENT_SMART);
//		}
//		
//		@Override
//		public String executeAction(XmlAction action, String message, boolean isDispatching) throws Exception {
//			return sendMessage(ISmartManager.SMART_MANAGER_URL, message);
//		}
//	}
	
	private HttpCommunicator httpCommunicator = new HttpCommunicator();
	private static XStream xstream;
	static{
		xstream = new XStream();
	}
	
	public RemoteSmartManager(String runtimeUrl, String sessionId, Locale locale) {
		httpCommunicator.init(runtimeUrl, sessionId, locale);
	}
	
	private XmlArgumentsHolder createArguments(Object... arguments) {
		XmlArgumentsHolder args = new XmlArgumentsHolder();

		for (int i = 0; i < arguments.length; i++) {
			args.addArgument(arguments[i]);
		}
		return args;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<AirProject> getAllAirProjects() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ISmartManager.ActionType.GET_AIR_PROJECTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AirProject>)xstream.fromXML(xml);
	}
	
	@Override
	public void updateAirProject(AirProject project) throws Exception {
		XmlAction op = new XmlAction(createArguments(project), ISmartManager.ActionType.UPDATE_AIR_PROJECT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public int saveAirProject(AirProject project) throws Exception {
		XmlAction op = new XmlAction(createArguments(project), ISmartManager.ActionType.SAVE_AIR_PROJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (int)xstream.fromXML(xml);
	}
	
	@Override
	public void deleteAirProject(AirProject project) throws Exception {
		XmlAction op = new XmlAction(createArguments(project), ISmartManager.ActionType.DELETE_AIR_PROJECT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public String executeAirProject(AirProject project) throws Exception {
		XmlAction op = new XmlAction(createArguments(project), ISmartManager.ActionType.EXECUTE_AIR_PROJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}
	
	@Override
	public RScriptModel executeScriptR(RScriptModel box, List<Parameter> lovParameters) throws Exception {
		XmlAction op = new XmlAction(createArguments(box, lovParameters), ISmartManager.ActionType.EXECUTE_SCRIPT_R);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		RScriptModel rsm = (RScriptModel)xstream.fromXML(xml);
//		if(rsm.getOutputFiles() != null){
//			int nbfiles = rsm.getOutputFiles().length;
//			if(nbfiles > 0){
//				byte[][] decodeBytes = new byte[nbfiles][];
//				for(int i=0; i< nbfiles; i++){
//					decodeBytes[i] = Base64.decodeBase64(rsm.getOutputFiles()[i]);
//				}
//				rsm.setOutputFiles(decodeBytes);
//			}
//			
//		}
		
		return rsm;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<MirrorCran> loadMirrors() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ISmartManager.ActionType.LOAD_MIRRORS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MirrorCran>)xstream.fromXML(xml);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<SmartAdmin> getSmartAdminbyUser(int idUser) throws Exception {
		XmlAction op = new XmlAction(createArguments(idUser), ISmartManager.ActionType.GET_SMART_ADMIN_BY_USER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<SmartAdmin>)xstream.fromXML(xml);
	}
	
	@Override
	public void updateSmartAdmin(SmartAdmin admin) throws Exception {
		XmlAction op = new XmlAction(createArguments(admin), ISmartManager.ActionType.UPDATE_SMART_ADMIN);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public void saveSmartAdmin(SmartAdmin admin) throws Exception {
		XmlAction op = new XmlAction(createArguments(admin), ISmartManager.ActionType.SAVE_SMART_ADMIN);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
	
	@Override
	public void deleteSmartAdmin(SmartAdmin admin) throws Exception {
		XmlAction op = new XmlAction(createArguments(admin), ISmartManager.ActionType.DELETE_SMART_ADMIN);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<StatDataColumn> loadStatsDataset(List<Dataset> datasets) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasets), ISmartManager.ActionType.LOAD_STATS_DATASET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<StatDataColumn>)xstream.fromXML(xml);
	}

	private Object handleError(String responseMessage) throws Exception {
		if(responseMessage.isEmpty()) {
			return null;
		}
		Object o = xstream.fromXML(responseMessage);
		if(o != null && o instanceof VanillaException) {
			throw (VanillaException) o;
		}
		return o;
	}
	
	@Override
	public String addAvatarIcon(String name, InputStream extDocStream, String format, boolean addDate) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(extDocStream, bos, true, true);

		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(name, rawBytes, format, addDate), ISmartManager.ActionType.UPLOADICON);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		String result = (String) xstream.fromXML(xml);
		if (result.isEmpty()) {
			return null;
		}

		return result;
	}
	
	@Override
	public InputStream importIcon(String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(name), ISmartManager.ActionType.LOAD_EXT_DOC);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		ByteArrayInputStream bis = (ByteArrayInputStream) handleError(xml);
		return bis;
		
	}
	
	@Override
	public String getAvatarIconUrl(String filename) throws Exception {
		XmlAction op = new XmlAction(createArguments(filename), ISmartManager.ActionType.GET_AVATAR_URL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}

	@Override
	public int saveRScript(RScript script) throws Exception {
		XmlAction op = new XmlAction(createArguments(script), ISmartManager.ActionType.SAVE_R_SCRIPT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (int)xstream.fromXML(xml);
	}

	@Override
	public void deleteRScript(RScript script) throws Exception {
		XmlAction op = new XmlAction(createArguments(script), ISmartManager.ActionType.DELETE_R_SCRIPT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public int saveRScriptModel(RScriptModel scriptmodel) throws Exception {
		XmlAction op = new XmlAction(createArguments(scriptmodel), ISmartManager.ActionType.SAVE_R_SCRIPT_MODEL);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (int)xstream.fromXML(xml);
	}
	
	@Override
	public void updateRScriptModel(RScriptModel scriptmodel) throws Exception {
		XmlAction op = new XmlAction(createArguments(scriptmodel), ISmartManager.ActionType.UPDATE_R_SCRIPT_MODEL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void deleteRScriptModel(RScriptModel scriptmodel) throws Exception {
		XmlAction op = new XmlAction(createArguments(scriptmodel), ISmartManager.ActionType.DELETE_R_SCRIPT_MODEL);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RScriptModel> getLastScriptModels(List<RScript> scripts) throws Exception {
		XmlAction op = new XmlAction(createArguments(scripts), ISmartManager.ActionType.GET_LAST_SCRIPT_MODELS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RScriptModel>)xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RScript> getRScriptsbyProject(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ISmartManager.ActionType.GET_R_SCRIPTS_BY_PROJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RScript>)xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RScript> getAllScripts() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ISmartManager.ActionType.GET_ALL_SCRIPTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RScript>)xstream.fromXML(xml);
	}

	@Override
	public void checkInScript(RScript script) throws Exception {
		XmlAction op = new XmlAction(createArguments(script), ISmartManager.ActionType.CHECKIN_SCRIPT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public String checkOutScript(RScript script) throws Exception {
		XmlAction op = new XmlAction(createArguments(script), ISmartManager.ActionType.CHECKOUT_SCRIPT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RScriptModel> getRScriptModelsbyScript(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ISmartManager.ActionType.GET_R_SCRIPT_MODELS_BY_SCRIPT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RScriptModel>)xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AirProject> getVisibleProjects(int idUser) throws Exception {
		XmlAction op = new XmlAction(createArguments(idUser), ISmartManager.ActionType.GET_VISIBLE_AIR_PROJECTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AirProject>)xstream.fromXML(xml);
	}

	@Override
	public void shareProject(int id, List<User> users) throws Exception {
		XmlAction op = new XmlAction(createArguments(id, users), ISmartManager.ActionType.SHARE_PROJECT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<UsersProjectsShares> getSharedProjectsUsersbyIdProject(int id)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ISmartManager.ActionType.GET_USERS_PROJECTS_SHARES_BY_PROJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<UsersProjectsShares>)xstream.fromXML(xml);
	}

	@Override
	public InputStream zipAirProject(int idProject, boolean allVersions)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(idProject, allVersions), ISmartManager.ActionType.ZIP_PROJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (InputStream)xstream.fromXML(xml);
	}

	@Override
	public RScriptModel addDatasettoR(Dataset dataset)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), ISmartManager.ActionType.ADD_DATASET_TO_R);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RScriptModel)xstream.fromXML(xml);
	}

	@Override
	public boolean checkProjectNameExistence(String projectName)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(projectName), ISmartManager.ActionType.CHECK_NAME_PROJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (boolean)xstream.fromXML(xml);
	}

	@Override
	public void saveAirProjectWithElements(InputStream imporProjectStream, String alternativeName, int idUser)
			throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(imporProjectStream, bos, true, true);

		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());
		
		XmlAction op = new XmlAction(createArguments(rawBytes, alternativeName, idUser), ISmartManager.ActionType.SAVE_PROJECT_WITH_ELEMENTS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public RScriptModel generateStatRPlot(DataColumn col1, DataColumn col2,	Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(col1, col2, dataset), ISmartManager.ActionType.GENERATE_STAT_PLOT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RScriptModel)xstream.fromXML(xml);
	}

	@Override
	public RScriptModel renderMarkdown(String script, String name, List<String> outputs, List<Parameter> lovParameters)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(script, name, outputs, lovParameters), ISmartManager.ActionType.RENDER_MARKDOWN);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RScriptModel)xstream.fromXML(xml);
	}

	@Override
	public String uploadFile(String name, InputStream extDocStream)
			throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(extDocStream, bos, true, true);

		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(name, rawBytes), ISmartManager.ActionType.UPLOADFILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		String result = (String) xstream.fromXML(xml);
		if (result.isEmpty()) {
			return null;
		}

		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DataColumn> getDatasourceCsvMetadata(Datasource datasourceCsv)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(datasourceCsv), ISmartManager.ActionType.GET_CSV_METADATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DataColumn>)xstream.fromXML(xml);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<String> getColumnDistinctValues(String datasetName, int columnIndex) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasetName, columnIndex), ISmartManager.ActionType.GET_COLUMN_DISTINCT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<String>)xstream.fromXML(xml);
	}

	@Override
	public String generateCSVinR(Dataset dts) throws Exception {
		XmlAction op = new XmlAction(createArguments(dts), ISmartManager.ActionType.GENERATE_CSV_IN_R);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)handleError(xml);
	}

	@Override
	public void deleteFile(String path) throws Exception {
		XmlAction op = new XmlAction(createArguments(path), ISmartManager.ActionType.DELETE_FILE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void updateRScript(RScript script) throws Exception {
		XmlAction op = new XmlAction(createArguments(script), ISmartManager.ActionType.UPDATE_R_SCRIPT);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public RScriptModel generateSummaryRPlot(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), ISmartManager.ActionType.GENERATE_SUMMARY_PLOT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RScriptModel)handleError(xml);
	}

	@Override
	public List<DataColumn> getDatasetColumns(String datasetName) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasetName), ISmartManager.ActionType.GET_DATASET_COLUMNS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<DataColumn>)handleError(xml);
	}

	@Override
	public int saveAirCube(AirCube airCube) throws Exception {
		XmlAction op = new XmlAction(createArguments(airCube), ISmartManager.ActionType.SAVE_AIR_CUBE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (int)handleError(xml);
	}

	@Override
	public List<AirCube> getCubesbyDataset(int idDataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(idDataset), ISmartManager.ActionType.GET_CUBES_BY_DATASET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<AirCube>)handleError(xml);
	}

	@Override
	public ActivityLog executeActivity(Activity activity) throws Exception {
		XmlAction op = new XmlAction(createArguments(activity), ISmartManager.ActionType.EXECUTE_ACTIVITY);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (ActivityLog)handleError(xml);
	}

	@Override
	public List<StatDataColumn> calculateRStats(List<DataColumn> columns, Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(columns, dataset), ISmartManager.ActionType.CALCULATE_R_STATS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<StatDataColumn>)handleError(xml);
	}

	@Override
	public List<StatDataColumn> createStatsDataset(Dataset dts) throws Exception {
		XmlAction op = new XmlAction(createArguments(dts), ISmartManager.ActionType.CREATE_STATS_DATASET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<StatDataColumn>)handleError(xml);
	}

	@Override
	public List<Dataset> getDatasetsbyProject(AirProject proj) throws Exception {
		XmlAction op = new XmlAction(createArguments(proj), ISmartManager.ActionType.GET_DATASETS_BY_PROJECT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<Dataset>)handleError(xml);
	}

	@Override
	public List<RScriptModel> addDatasetstoR(List<Dataset> datasets) throws Exception {
		XmlAction op = new XmlAction(createArguments(datasets), ISmartManager.ActionType.ADD_DATASETS_TO_R);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<RScriptModel>)handleError(xml);
	}

	@Override
	public void deleteLinkedDatasets(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), ISmartManager.ActionType.DELETE_LINKED_DATASET);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public RScriptModel getLastModelbyScript(int idScript) throws Exception {
		XmlAction op = new XmlAction(createArguments(idScript), ISmartManager.ActionType.GET_LAST_MODEL_BY_SCRIPT);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RScriptModel)handleError(xml);
	}

	@Override
	public AirProject getAirProjectbyId(int idProject) throws Exception {
		XmlAction op = new XmlAction(createArguments(idProject), ISmartManager.ActionType.GET_PROJECT_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (AirProject)handleError(xml);
	}

	@Override
	public RScript getScriptbyId(int idScript) throws Exception {
		XmlAction op = new XmlAction(createArguments(idScript), ISmartManager.ActionType.GET_SCRIPT_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RScript)handleError(xml);
	}
	
	@Override
	public RScriptModel getScriptModelbyId(int idScript) throws Exception {
		XmlAction op = new XmlAction(createArguments(idScript), ISmartManager.ActionType.GET_SCRIPT_MODEL_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (RScriptModel)handleError(xml);
	}

	@Override
	public String addAirImage(String name, InputStream extDocStream, String format) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		IOWriter.write(extDocStream, bos, true, true);

		byte[] rawBytes = Base64.encodeBase64(bos.toByteArray());

		XmlAction op = new XmlAction(createArguments(name, rawBytes, format), ISmartManager.ActionType.ADD_AIR_IMAGE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		String result = (String) xstream.fromXML(xml);
		if (result.isEmpty()) {
			return null;
		}

		return result;
	}

	@Override
	public Map<String, List<Serializable>> getRDatasetData(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), ISmartManager.ActionType.GET_R_DATASET_DATA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Map<String, List<Serializable>>)handleError(xml);
	}

	@Override
	public void loadRDatasetTemp(Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset), ISmartManager.ActionType.LOAD_R_DATASET_TEMP);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public String loadRCsvFile(byte[] resultStream, boolean hasHeader, String sep) throws Exception {
		XmlAction op = new XmlAction(createArguments(resultStream, hasHeader, sep), ISmartManager.ActionType.LOAD_R_CSV_FILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)handleError(xml);
	}

	@Override
	public String loadRExcelFile(byte[] resultStream, boolean hasHeader, Dataset dataset) throws Exception {
		XmlAction op = new XmlAction(createArguments(resultStream, hasHeader, dataset), ISmartManager.ActionType.LOAD_R_EXCEL_FILE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (String)handleError(xml);
	}

	@Override
	public void addRDatasetTempFile(Dataset dataset, String request, String nameTempFile) throws Exception {
		XmlAction op = new XmlAction(createArguments(dataset, request, nameTempFile), ISmartManager.ActionType.ADD_R_DATASET_TEMPFILE);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}
}
