package bpm.smart.runtime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;

import bpm.smart.core.model.AirCube;
import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.SmartAdmin;
import bpm.smart.core.xstream.ISmartManager;
import bpm.smart.runtime.security.AirSession;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.xstream.IXmlActionType;
import bpm.vanilla.platform.core.xstream.XmlAction;
import bpm.vanilla.platform.core.xstream.XmlArgumentsHolder;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.remote.internal.RemoteConstants;

import com.thoughtworks.xstream.XStream;

public class SmartManagerServlet extends HttpServlet {

	private static final long serialVersionUID = -2932618062804864382L;
	
	private IVanillaLogger logger;
	private SmartManagerComponent rootComponent;
	
	private XStream xstream;
	
	
	public SmartManagerServlet(SmartManagerComponent componentProvider, IVanillaLogger logger){
		this.logger = logger;
		this.rootComponent = componentProvider;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Initializing SmartManagerServlet...");
		xstream = new XStream();
		xstream.alias("action", XmlAction.class);
		xstream.alias("actionType", IXmlActionType.class, ISmartManager.ActionType.class);
		xstream.alias("argumentsHolder", XmlArgumentsHolder.class);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			String sessionId = req.getHeader(RemoteConstants.HTTP_HEADER_SESSION_ID);
			AirSession session = rootComponent.getSessionHolder().getSession(sessionId);
			
			if(session == null){
				throw new Exception("The session is not initialized. Please try to connect to a vanilla instance.");
			}
			
			ISmartManager component = session.getServiceManager();
			
			resp.setCharacterEncoding("UTF-8");
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			if (action.getActionType() == null){
				throw new Exception("XmlAction has no actionType");
			}
			
			if (!(action.getActionType() instanceof IXmlActionType)){
				throw new Exception("ActionType not a IAndroidRepositoryManager");
			}

			ISmartManager.ActionType type = (ISmartManager.ActionType)action.getActionType();
			
			Object actionResult = null;
			Date startDate = new Date();
			
			try{
				switch (type) {
					case GET_AIR_PROJECTS:
						actionResult = component.getAllAirProjects();
						break;
					case UPDATE_AIR_PROJECT:
						component.updateAirProject((AirProject) args.getArguments().get(0));
						break;
					case SAVE_AIR_PROJECT:
						actionResult = component.saveAirProject((AirProject) args.getArguments().get(0));
						break;
					case DELETE_AIR_PROJECT:
						component.deleteAirProject((AirProject) args.getArguments().get(0));
						break;
					case EXECUTE_AIR_PROJECT:
						actionResult = component.executeAirProject((AirProject) args.getArguments().get(0));
						break;
					case EXECUTE_SCRIPT_R:
						actionResult = component.executeScriptR((RScriptModel) args.getArguments().get(0), (List<Parameter>) args.getArguments().get(1));
						break;
					case LOAD_MIRRORS:
						actionResult = component.loadMirrors();
						break;
					case GET_SMART_ADMIN_BY_USER:
						actionResult = component.getSmartAdminbyUser((int) args.getArguments().get(0));
						break;
					case UPDATE_SMART_ADMIN:
						component.updateSmartAdmin((SmartAdmin) args.getArguments().get(0));
						break;
					case SAVE_SMART_ADMIN:
						component.saveSmartAdmin((SmartAdmin) args.getArguments().get(0));
						break;
					case DELETE_SMART_ADMIN:
						component.deleteSmartAdmin((SmartAdmin) args.getArguments().get(0));
						break;
					case LOAD_STATS_DATASET:
						actionResult = component.loadStatsDataset((List<Dataset>) args.getArguments().get(0));
						break;
					case UPLOADICON:
						actionResult = component.addAvatarIcon((String) args.getArguments().get(0), decode64((byte[]) args.getArguments().get(1)), (String) args.getArguments().get(2), (boolean) args.getArguments().get(3));
						break;
					case LOAD_EXT_DOC:
						actionResult = component.importIcon((String) args.getArguments().get(0));
						break;
					case GET_AVATAR_URL:
						actionResult = component.getAvatarIconUrl((String) args.getArguments().get(0));
						break;
					case DELETE_R_SCRIPT:
						component.deleteRScript((RScript) args.getArguments().get(0));
						break;
					case DELETE_R_SCRIPT_MODEL:
						component.deleteRScriptModel((RScriptModel) args.getArguments().get(0));
						break;
					case GET_LAST_SCRIPT_MODELS:
						actionResult = component.getLastScriptModels((List<RScript>) args.getArguments().get(0));
						break;
					case SAVE_R_SCRIPT:
						actionResult = component.saveRScript((RScript) args.getArguments().get(0));
						break;
					case SAVE_R_SCRIPT_MODEL:
						actionResult = component.saveRScriptModel((RScriptModel) args.getArguments().get(0));
						break;
					case UPDATE_R_SCRIPT_MODEL:
						component.updateRScriptModel((RScriptModel) args.getArguments().get(0));
						break;
					case GET_R_SCRIPTS_BY_PROJECT:
						actionResult = component.getRScriptsbyProject((int) args.getArguments().get(0));
						break;
					case GET_ALL_SCRIPTS:
						actionResult = component.getAllScripts();
						break;
					case CHECKIN_SCRIPT:
						component.checkInScript((RScript) args.getArguments().get(0));
						break;
					case CHECKOUT_SCRIPT:
						actionResult = component.checkOutScript((RScript) args.getArguments().get(0));
						break;
					case GET_R_SCRIPT_MODELS_BY_SCRIPT:
						actionResult = component.getRScriptModelsbyScript((int) args.getArguments().get(0));
						break;
					case GET_VISIBLE_AIR_PROJECTS:
						actionResult = component.getVisibleProjects((int) args.getArguments().get(0));
						break;
					case SHARE_PROJECT:
						component.shareProject((int) args.getArguments().get(0), (List<User>) args.getArguments().get(1));
						break;
					case GET_USERS_PROJECTS_SHARES_BY_PROJECT:
						actionResult = component.getSharedProjectsUsersbyIdProject((int) args.getArguments().get(0));
						break;
					case ZIP_PROJECT:
						actionResult = component.zipAirProject((int) args.getArguments().get(0), (boolean) args.getArguments().get(1));
						break;
					case ADD_DATASET_TO_R:
						actionResult = component.addDatasettoR((Dataset) args.getArguments().get(0));
						break;
					case CHECK_NAME_PROJECT:
						actionResult = component.checkProjectNameExistence((String) args.getArguments().get(0));
						break;
					case SAVE_PROJECT_WITH_ELEMENTS:
						component.saveAirProjectWithElements(decode64((byte[]) args.getArguments().get(0)), (String) args.getArguments().get(1), (int) args.getArguments().get(2));
						break;
					case GENERATE_STAT_PLOT:
						actionResult = component.generateStatRPlot((DataColumn) args.getArguments().get(0), (DataColumn) args.getArguments().get(1), (Dataset) args.getArguments().get(2));
						break;
					case RENDER_MARKDOWN:
						actionResult = component.renderMarkdown((String) args.getArguments().get(0), (String) args.getArguments().get(1), (List<String>) args.getArguments().get(2), (List<Parameter>) args.getArguments().get(3));
						break;
					case UPLOADFILE:
						actionResult = component.uploadFile((String) args.getArguments().get(0), decode64((byte[]) args.getArguments().get(1)));
						break;
					case GET_CSV_METADATA:
						actionResult = component.getDatasourceCsvMetadata((Datasource) args.getArguments().get(0));
						break;
					case GET_COLUMN_DISTINCT:
						actionResult = component.getColumnDistinctValues((String) args.getArguments().get(0), (Integer) args.getArguments().get(1));
						break;
					case GENERATE_CSV_IN_R:
						actionResult = component.generateCSVinR((Dataset) args.getArguments().get(0));
						break;
					case DELETE_FILE:
						component.deleteFile((String) args.getArguments().get(0));
						break;
					case UPDATE_R_SCRIPT:
						component.updateRScript((RScript) args.getArguments().get(0));
						break;
					case GENERATE_SUMMARY_PLOT:
						actionResult = component.generateSummaryRPlot((Dataset) args.getArguments().get(0));
						break;
					case GET_DATASET_COLUMNS:
						actionResult = component.getDatasetColumns((String) args.getArguments().get(0));
						break;
					case SAVE_AIR_CUBE:
						actionResult = component.saveAirCube((AirCube) args.getArguments().get(0));
						break;
					case GET_CUBES_BY_DATASET:
						actionResult = component.getCubesbyDataset((int) args.getArguments().get(0));
						break;
					case EXECUTE_ACTIVITY:
						actionResult = component.executeActivity((Activity) args.getArguments().get(0));
						break;
					case CALCULATE_R_STATS:
						actionResult = component.calculateRStats((List<DataColumn>) args.getArguments().get(0), (Dataset)args.getArguments().get(1));
						break;
					case CREATE_STATS_DATASET:
						actionResult = component.createStatsDataset((Dataset)args.getArguments().get(0));
						break;
					case GET_DATASETS_BY_PROJECT:
						actionResult = component.getDatasetsbyProject((AirProject)args.getArguments().get(0));
						break;
					case ADD_DATASETS_TO_R:
						actionResult = component.addDatasetstoR((List<Dataset>)args.getArguments().get(0));
						break;
					case DELETE_LINKED_DATASET:
						component.deleteLinkedDatasets((Dataset)args.getArguments().get(0));
						break;
					case GET_LAST_MODEL_BY_SCRIPT:
						actionResult = component.getLastModelbyScript((int)args.getArguments().get(0));
						break;
					case GET_PROJECT_BY_ID:
						actionResult = component.getAirProjectbyId((int)args.getArguments().get(0));
						break;
					case GET_SCRIPT_BY_ID:
						actionResult = component.getScriptbyId((int)args.getArguments().get(0));
						break;
					case GET_SCRIPT_MODEL_BY_ID:
						actionResult = component.getScriptModelbyId((int)args.getArguments().get(0));
						break;
					case ADD_AIR_IMAGE:
						actionResult = component.addAirImage((String) args.getArguments().get(0), decode64((byte[]) args.getArguments().get(1)), (String) args.getArguments().get(2));
						break;
					case GET_R_DATASET_DATA:
						actionResult = component.getRDatasetData((Dataset)args.getArguments().get(0));
						break;
					case ADD_R_DATASET_TEMPFILE:
						component.addRDatasetTempFile((Dataset)args.getArguments().get(0), (String)args.getArguments().get(1), (String)args.getArguments().get(2));
						break;
					case LOAD_R_CSV_FILE:
						actionResult = component.loadRCsvFile((byte[])args.getArguments().get(0), (boolean)args.getArguments().get(1), (String)args.getArguments().get(2));
						break;
					case LOAD_R_DATASET_TEMP:
						component.loadRDatasetTemp((Dataset)args.getArguments().get(0));
						break;
					case LOAD_R_EXCEL_FILE:
						actionResult = component.loadRExcelFile((byte[])args.getArguments().get(0), (boolean)args.getArguments().get(1), (Dataset)args.getArguments().get(2));
						break;
				}
			}catch(Exception ex){
				throw new Exception("Operation " + type.name() + " failed - " + ex.getMessage(), ex);
			}
			
			Date endDate = new Date();
			
			logger.trace("Execution time for : " + type.toString() + " -> " + (endDate.getTime() - startDate.getTime()) + " with arguments -> " + args.toString());
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			resp.getWriter().close();
			
		}catch(Exception ex){
			logger.error(ex.getMessage(), ex);
			
			resp.getWriter().write("<error>" + ex.getMessage() + "</error>");
			resp.getWriter().close();
		}
	}
	
	private InputStream decode64(byte[] raw64) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(raw64));

//		String fName = "ext_" + new Object().hashCode() + name + "." + format;
//		String basePath = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.vanillafiles.path");
//		String relPatth = "/external_documents/project_icons" + fName;
//
//		File file = new File(basePath + "/" + relPatth);
//		FileOutputStream fileOutputStream = new FileOutputStream(file);
//		IOWriter.write(bis, fileOutputStream, true, true);
//
//		Element e = DocumentHelper.createElement("project_icon");
//		e.addElement("name").setText(name);
//		if (file.getAbsolutePath().contains(".")) {
//			format = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf('.')).toLowerCase();
//		}
//		e.addElement("type").setText(format);
//		e.addElement("path").setText(relPatth);
//
//		return e.asXML();
		
		return (InputStream) bis;
	}
}
