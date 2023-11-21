package bpm.smart.runtime.r;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.thoughtworks.xstream.XStream;

import bpm.smart.core.model.AirProject;
import bpm.smart.core.model.RScript;
import bpm.smart.core.model.RScript.ScriptType;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.KmeansActivity;
import bpm.smart.core.model.workflow.activity.KmeansActivity.TypeAlgoKMeans;
import bpm.smart.core.xstream.RemoteAdminManager;
import bpm.smart.runtime.SmartManagerComponent;
import bpm.smart.runtime.security.AirSession;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.resources.ListOfValues;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.logging.IVanillaLogger;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.remote.IAdminManager;

public class RExternalServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private SmartManagerComponent component;
	private IVanillaLogger logger;

	public RExternalServlet(SmartManagerComponent smartManagerComponent, IVanillaLogger iVanillaLogger) {
		this.component = smartManagerComponent;
		this.logger = iVanillaLogger;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String action = req.getParameter("action");
			String user = req.getParameter("user");
			int groupId = Integer.parseInt(req.getParameter("group"));
			int repId = Integer.parseInt(req.getParameter("repo"));
			String object = req.getParameter("object");

			AirSession session = null;
			try {
				session = getSession(user, groupId, repId);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if (action.equals(ACTION.RUNRCODE.getAction())) {
				runRCode(object, session, resp);
			}

			else if (action.equals(ACTION.GETLASTRESULT.getAction())) {
				getLastResult(session, resp);
			}

			else if (action.equals(ACTION.KMEANS.getAction())) {
				kmeans(session, object, resp);
			}
			else if (action.equals(ACTION.GETALLSCRIPTS.getAction())) {
				getAllScripts(session, resp);
			}
			else if (action.equals(ACTION.RUNAIRSCRIPT.getAction())) {
				runAirScript(session, object, resp);
			}
			else if (action.equals(ACTION.GETALLLOVS.getAction())) {
				getAllLOVs(session, resp);
			}
			else if (action.equals(ACTION.GETREPOSITORYITEMS.getAction())) {
				loadRepositoryItems(session, object, resp, groupId, repId);
			}
			else if (action.equals(ACTION.SAVERSCRIPT.getAction())) {
				if (object == null || object.isEmpty()) {
					object = IOUtils.toString(req.getInputStream(), "UTF-8");
				}

				saveRScript(session, object, resp);
			}
			else if (action.equals(ACTION.GETALLPROJECTS.getAction())) {
				getAllProjects(session, resp);
			}
			else if (action.equals(ACTION.GETRSCRIPTCODE.getAction())) {
				String selectedProject = req.getParameter("project");
				String selectedScript = req.getParameter("script");
				getRScriptCode(session, selectedProject, selectedScript, resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void kmeans(AirSession session, String object, HttpServletResponse resp) throws Exception {
		Document doc = DocumentHelper.parseText(object);
		Element root = doc.getRootElement();

		String algo = root.elementText("algo");
		String nbCluster = root.elementText("nbCluster");
		String iterMax = root.elementText("iterMax");
		String nStart = root.elementText("nStart");
		String colX = root.elementText("colX");
		String colY = root.elementText("colY");

		String datasetName = "excelkmeans" + new Object().hashCode();
		String dataset = parseDataset(root.element("dataset"), datasetName);

		String datasetScript = dataset + "\n" + datasetName + " <- as.data.frame(" + datasetName + ")";

		RScriptModel box = new RScriptModel();
		box.setScript(datasetScript);
		box = session.getServiceManager().executeScriptR(box);

		KmeansActivity act = new KmeansActivity(datasetName);
		act.setDatasetName(datasetName);
		act.setIterMax(Integer.parseInt(iterMax));
		act.setNbClust(Integer.parseInt(nbCluster));
		act.setnStart(Integer.parseInt(nStart));
		act.setAlgoType(getAlgoType(algo));
		act.setxColumnName(colX);
		act.setyColumnName(colY);
		act.setWithGraph(false);

		ActivityLog log = session.getServiceManager().executeActivity(act);

		box = new RScriptModel();
		box.setScript("manual_result");
		session.setLastResult(box);

		resp.getWriter().write("success");
	}

	private TypeAlgoKMeans getAlgoType(String algo) {
		if (TypeAlgoKMeans.FORGY.getType().toLowerCase().equals(algo.toLowerCase())) {
			return TypeAlgoKMeans.FORGY;
		}
		else if (TypeAlgoKMeans.HARTIGAN_WONG.getType().toLowerCase().equals(algo.toLowerCase())) {
			return TypeAlgoKMeans.HARTIGAN_WONG;
		}
		else if (TypeAlgoKMeans.LLOYD.getType().toLowerCase().equals(algo.toLowerCase())) {
			return TypeAlgoKMeans.LLOYD;
		}
		else if (TypeAlgoKMeans.MACQUEEN.getType().toLowerCase().equals(algo.toLowerCase())) {
			return TypeAlgoKMeans.MACQUEEN;
		}
		return null;
	}

	private String parseVect(Element vectElement) {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		boolean firstRow = true;
		int rowCount = 0;
		int colCount = 0;
		for (Element row : (List<Element>) vectElement.elements("row")) {

			for (Element val : (List<Element>) row.elements("value")) {
				if (firstRow) {
					colCount++;
				}
				if (first) {
					first = false;
				}
				else {
					buf.append(",");
				}

				try {
					Double.parseDouble(val.getText());
					buf.append(val.getText());
				} catch (Exception e) {
					buf.append("\"" + val.getText() + "\"");
				}

			}

			if (firstRow) {
				firstRow = false;
			}
			rowCount++;
		}

		buf.append("),nrow = " + rowCount + ",ncol = " + colCount + ", byrow = TRUE");

		return "matrix(c(" + buf.toString() + ")";
	}

	private String parseDataset(Element datasetElement, String datasetName) {
		StringBuilder buf = new StringBuilder();
		StringBuilder colNames = new StringBuilder();
		boolean first = true;
		boolean columnRow = true;
		boolean firstRow = true;
		int rowCount = 0;
		int colCount = 0;
		for (Element row : (List<Element>) datasetElement.elements("row")) {
			if (columnRow) {
				columnRow = false;
				for (Element val : (List<Element>) row.elements("value")) {
					if (!colNames.toString().isEmpty()) {
						colNames.append(",");
					}
					colNames.append("\"" + val.getText() + "\"");
				}
				continue;
			}
			else {
				for (Element val : (List<Element>) row.elements("value")) {
					if (firstRow) {
						colCount++;
					}
					if (first) {
						first = false;
					}
					else {
						buf.append(",");
					}

					try {
						Double.parseDouble(val.getText());
						buf.append(val.getText());
					} catch (Exception e) {
						buf.append("\"" + val.getText() + "\"");
					}

				}
			}
			if (firstRow) {
				firstRow = false;
			}
			rowCount++;
		}

		buf.append("),nrow = " + rowCount + ",ncol = " + colCount + ", byrow = TRUE");

		return datasetName + " <- matrix(c(" + buf.toString() + ")\ncolnames(" + datasetName + ") <- c(" + colNames.toString() + ")";
	}

	private void getLastResult(AirSession session, HttpServletResponse resp) throws Exception {
		try {

			RScriptModel box = session.getLastResult();
			String[] lines = box.getScript().split("\n");
			String lastLine;
			if (lines.length == 0) {
				resp.getWriter().write("error " + "no result is available");
				return;
			}
			else if (lines.length == 1) {
				lastLine = lines[0];
			}
			else {
				lastLine = lines[lines.length - 1];
			}

			box.setOutputs(new String[] { session.getServiceManager().adaptRline(lastLine) });
			box = session.getServiceManager().executeScriptR(box);

			if (box.getOutputVars() == null || box.getOutputVars().size() == 0) {
				resp.getWriter().write("no result is printed");
				return;
			}
			Serializable result = box.getOutputVars().get(0);

			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("root");

			if (result instanceof List) {
				StringBuilder buf = new StringBuilder();
				Element resElem = root.addElement("result");
				generateListXml((List) result, resElem);

			}
			else {
				root.addElement("result").addText(result.toString());
			}

			root.addElement("status").addAttribute("type", "success");
			// resp.getWriter().write("success");

			if (box.getOutputFiles() != null && box.getOutputFiles().length > 0) {
				Element imgsbal = root.addElement("images");
				for (String img : box.getOutputFiles()) {
					imgsbal.addElement("img").setText(img);
				}
			}

			root.addElement("log").setText(box.getOutputLog().replace("&nbsp;", " "));

			resp.getWriter().write(doc.asXML());

		} catch (Exception e) {
			e.printStackTrace();

			resp.getWriter().write("error " + e.getMessage());
		}
	}

	private void runRCode(String object, AirSession session, HttpServletResponse resp) throws Exception {
		try {
			RScriptModel box = new RScriptModel();
			box.setScript(object);
			box = session.getServiceManager().executeScriptR(box);

			session.setLastResult(box);

			resp.getWriter().write("success");

		} catch (Exception e) {
			e.printStackTrace();

			resp.getWriter().write("error " + e.getMessage());
		}
	}

	private void generateListXml(List result, Element xml) {
		Element list = xml.addElement("list");
		for (Object o : result) {
			if (o instanceof List) {
				generateListXml((List) o, list);
			}
			else {
				if (o != null)
					list.addElement("value").addText(o.toString());
			}
		}
	}

	private AirSession getSession(String user, int groupId, int repId) throws Exception {
		List<AirSession> sessions = component.getSessionHolder().getSessions();
		for (AirSession session : sessions) {
			if (session.getUser().getLogin().equals(user)) {
				return session;
			}
		}

		IAdminManager admin = new RemoteAdminManager(ConfigurationManager.getProperty(VanillaConfiguration.P_VANILLA_URL), null, Locale.getDefault());

		IVanillaAPI api = new RemoteVanillaPlatform(ConfigurationManager.getInstance().getVanillaConfiguration());
		User userObject = api.getVanillaSecurityManager().getUserByLogin(user);

		String sessionId = admin.connect(userObject);
		return component.getSessionHolder().getSession(sessionId);
	}

	@SuppressWarnings("unchecked")
	private void getAllScripts(AirSession session, HttpServletResponse resp) throws Exception {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");

		List<Parameter> params = (List<Parameter>) session.getServiceManager().getResources(TypeResource.PARAMETER);
		List<ListOfValues> lovs = (List<ListOfValues>) session.getServiceManager().getResources(TypeResource.LOV);
		List<RScript> scripts = session.getServiceManager().getAllScripts();

		for (RScript script : scripts) {
			String projectName = session.getServiceManager().getAirProjectbyId(script.getIdProject()).getName();
			RScriptModel last = session.getServiceManager().getLastModelbyScript(script.getId());

			Element elem = root.addElement("script");
			elem.addElement("id").setText(last.getId() + "");
			elem.addElement("name").setText(script.getName());
			elem.addElement("projectName").setText(projectName);
			elem.addElement("type").setText(script.getScriptType());
			elem.addElement("code").setText(last.getScript());
			elem.addElement("dateVersion").setText(DateFormat.getDateInstance(SimpleDateFormat.SHORT).format(last.getDateVersion()));
			elem.addElement("comment").setText((script.getComment() == null) ? "" : script.getComment());

			Element elemParas = elem.addElement("parameters");
			for (Parameter param : params) {
				if (last.getScript().contains(param.getParameterName())) {
					Element elemPara = elemParas.addElement("parameter");
					elemPara.addElement("id").setText(param.getId() + "");
					elemPara.addElement("name").setText(param.getName());
					elemPara.addElement("description").setText(param.getQuestion());
					elemPara.addElement("type").setText(param.getParameterType().toString());
					if (param.getParameterType().equals(TypeParameter.SELECTION)) {
						Element lov = elemPara.addElement("lov");
						for (String val : param.getListOfValues().getValues()) {
							lov.addElement("value").setText(val);
						}
						// } else if(param.getParameterType().equals(TypeParameter.LOV)){
						// Element lovsElem = elemPara.addElement("lovs");
						// for(ListOfValues lov : lovs){
						// Element lovElem = lovsElem.addElement("lov");
						// for(String val : lov.getValues()){
						// lovElem.addElement("value").setText(val);
						// }
						// }
						//
					}

				}
			}
		}

		resp.getWriter().write(doc.asXML());
	}

	private void runAirScript(AirSession session, String object, HttpServletResponse resp) throws Exception {

		Document doc = DocumentHelper.parseText(object);
		Element root = doc.getRootElement();

		int id = Integer.parseInt(root.elementText("id"));
		RScriptModel box = session.getServiceManager().getScriptModelbyId(id);
		RScript script = session.getServiceManager().getScriptbyId(box.getIdScript());
		List<Parameter> lovParams = new ArrayList<>();
		List<String> mkdOutputs = new ArrayList<>();
		mkdOutputs.add("html");

		if (root.elementText("parameters") != null) {
			Element parameters = root.element("parameters");
			for (Object parameter : parameters.elements("parameter")) {
				int idParam = Integer.parseInt(((Element) parameter).elementText("idParam"));
				Parameter param = (Parameter) session.getServiceManager().getDao().getResourceById(idParam);

				String value;
				if (param.getParameterType().equals(TypeParameter.RANGE)) {
					// String vectName = "vect" + new Object().hashCode();
					value = parseVect(((Element) parameter).element("valueParam"));
					box.setScript(box.getScript().replace(param.getParameterName(), value));
				}
				else if (param.getParameterType().equals(TypeParameter.LOV)) {
					value = ((Element) parameter).elementText("valueParam");
					ListOfValues lov = (ListOfValues) session.getServiceManager().getDao().getResourceById(Integer.parseInt(value));
					param.setValueListOfValues(lov);
					lovParams.add(param);
				}
				else {
					value = ((Element) parameter).elementText("valueParam");
					box.setScript(box.getScript().replace(param.getParameterName(), value));
				}

			}
		}

		Document docRes = DocumentHelper.createDocument();
		Element rootRes = docRes.addElement("root");

		try {
			if (script.getScriptType().equals(ScriptType.R.name())) {
				box = session.getServiceManager().executeScriptR(box, lovParams);
			}
			else {
				box = session.getServiceManager().renderMarkdown(box.getScript(), script.getName(), mkdOutputs, lovParams);
			}

			session.setLastResult(box);
			rootRes.addElement("status").addAttribute("type", "success");
			// resp.getWriter().write("success");
			if (script.getScriptType().equals(ScriptType.R.name())) {
				if (box.getOutputFiles() != null && box.getOutputFiles().length > 0) {
					Element imgsbal = rootRes.addElement("images");
					for (String img : box.getOutputFiles()) {
						imgsbal.addElement("img").setText(img);
					}
				}
			}
			else {
				if (box.getOutputVarstoString() != null && box.getOutputVarstoString().size() > 0) {
					Element imgsbal = rootRes.addElement("images");
					for (String img : box.getOutputVarstoString()) {
						imgsbal.addElement("img").setText(new String(Base64.decodeBase64(img.getBytes())));
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			rootRes.addElement("status").addAttribute("type", "error").addAttribute("message", e.getMessage());
			// resp.getWriter().write("error " + e.getMessage());
		}
		rootRes.addElement("log").setText(box.getOutputLog().replace("&nbsp;", " "));

		resp.getWriter().write(docRes.asXML());
	}

	public enum ACTION {

		RUNRCODE("runRCode"), GETLASTRESULT("getLastResult"), GETVARIABLEVALUE("getVarValue"), KMEANS("kmeans"), GETALLSCRIPTS("getScripts"), RUNAIRSCRIPT("runAirScript"), GETALLLOVS("getAllLOVs"), GETREPOSITORYITEMS("loadRepositoryItems"), SAVERSCRIPT("saveRScript"), GETALLPROJECTS("getProjects"), GETRSCRIPTCODE("getRScriptCode");

		public String action;

		ACTION(String action) {
			this.action = action;
		}

		public String getAction() {
			return action;
		}
	}

	@SuppressWarnings("unchecked")
	private void getAllLOVs(AirSession session, HttpServletResponse resp) throws Exception {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");

		List<ListOfValues> lovs = (List<ListOfValues>) session.getServiceManager().getResources(TypeResource.LOV);

		for (ListOfValues lov : lovs) {
			Element lovElem = root.addElement("lov");
			lovElem.addElement("id").setText(lov.getId() + "");
			lovElem.addElement("name").setText(lov.getName());
			for (String val : lov.getValues()) {
				lovElem.addElement("value").setText(val);
			}
		}
		resp.getWriter().write(doc.asXML());
	}

	public void loadRepositoryItems(AirSession session, String object, HttpServletResponse resp, int groupId, int repId) throws Exception {
		Group group;
		Repository repo;
		IRepositoryApi sock;
		group = component.getVanillaApi().getVanillaSecurityManager().getGroupById(groupId);
		repo = component.getVanillaApi().getVanillaRepositoryManager().getRepositoryById(repId);

		sock = new RemoteRepositoryApi(new BaseRepositoryContext(component.getVanillaApi().getVanillaContext(), group, repo));

		Document doc = DocumentHelper.parseText(object);
		Element root = doc.getRootElement();

		int type = Integer.parseInt(root.elementText("itemType"));

		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(sock, type);

		List<RepositoryItem> repoItems = new ArrayList<RepositoryItem>();

		doc = DocumentHelper.createDocument();
		root = doc.addElement("root");

		findChilds(rep.getRootDirectories(), rep, root);

		// Collections.sort(repoItems, new Comparator<RepositoryItem>() {
		// @Override
		// public int compare(RepositoryItem o1, RepositoryItem o2) {
		// return o1.getItemName().compareTo(o2.getItemName());
		// }
		// });

		// doc = DocumentHelper.createDocument();
		// root = doc.addElement("root");
		// Element items = root.addElement("items");
		// for(RepositoryItem item : repoItems) {
		// Element elemItem = items.addElement("item");
		// elemItem.addElement("id").setText(item.getId()+"");
		// elemItem.addElement("name").setText(item.getItemName());
		// }

		resp.getWriter().write(doc.asXML());
	}

	private void findChilds(List<RepositoryDirectory> directories, IRepository rep, Element elemRoot) throws Exception {

		if (directories == null) {
			return;
		}

		for (RepositoryDirectory dir : directories) {
			Element elemDir = elemRoot.addElement("dir");
			elemDir.addElement("name").setText(dir.getName());
			elemDir.addElement("id").setText(dir.getId() + "");
			findChilds(rep.getChildDirectories(dir), rep, elemDir);

			Element items = elemDir.addElement("items");
			for (RepositoryItem item : rep.getItems(dir)) {
				Element elemItem = items.addElement("item");
				elemItem.addElement("id").setText(item.getId() + "");
				elemItem.addElement("name").setText(item.getItemName());
			}
		}

	}

	public void saveRScript(AirSession session, String object, HttpServletResponse resp) throws Exception {
		Document docRes = DocumentHelper.createDocument();
		Element rootRes = docRes.addElement("root");
		try {
			Document doc = DocumentHelper.parseText(object);
			Element root = doc.getRootElement();

			int id = (!root.elementText("id").equals("")) ? Integer.parseInt(root.elementText("id")) : 0;
			String name = root.elementText("name");
			String comment = root.elementText("comment");
			String project = root.elementText("project");
			String scriptType = root.elementText("type");
			String code = root.elementText("code");
			int idProject = 0;
			List<AirProject> projects = session.getServiceManager().getAllAirProjects();
			for (AirProject proj : projects) {
				if (proj.getName().equals(project)) {
					idProject = proj.getId();
					break;
				}
			}
			if (idProject == 0) {
				rootRes.addElement("status").addAttribute("type", "error").addAttribute("message", "no corresponding project");
				resp.getWriter().write(docRes.asXML());
				return;
			}

			int idScript;
			int idModel;
			if (id == 0) {
				RScript script = new RScript();
				script.setComment(comment);
				script.setIdProject(idProject);
				script.setName(name);
				script.setScriptType(scriptType);

				idScript = session.getServiceManager().saveRScript(script);

				RScriptModel model = new RScriptModel();
				model.setDateVersion(new Date());
				model.setIdScript(idScript);
				model.setNumVersion(1);
				model.setScript(code);

				idModel = session.getServiceManager().saveRScriptModel(model);
			}
			else {
				RScriptModel box = session.getServiceManager().getScriptModelbyId(id);
				RScript script = session.getServiceManager().getScriptbyId(box.getIdScript());

				script.setComment(comment);
				session.getServiceManager().updateRScript(script);

				RScriptModel model = new RScriptModel();
				model.setDateVersion(new Date());
				model.setIdScript(script.getId());
				model.setNumVersion(box.getNumVersion() + 1);
				model.setScript(code);

				idModel = session.getServiceManager().saveRScriptModel(model);

			}

			rootRes.addElement("status").addAttribute("type", "success");
			rootRes.addElement("scriptModel").addAttribute("id", idModel + "");

		} catch (Exception e) {
			rootRes.addElement("status").addAttribute("type", "error").addAttribute("message", e.getMessage());
			;
			e.printStackTrace();
		}

		resp.getWriter().write(docRes.asXML());
	}

	private void getAllProjects(AirSession session, HttpServletResponse resp) throws Exception {
//		buildFullLineXML(session, resp);

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");
		List<AirProject> projects = session.getServiceManager().getAllAirProjects();

		for (AirProject project : projects) {
			Element elem = root.addElement("projects");
			elem.addElement("projectId").setText(project.getId() + "");
			elem.addElement("projectName").setText(project.getName());
			// elem.addElement("subject").setText(project.getSubject() != null ? project.getSubject() : "");
			elem.addElement("author").setText(project.getAuthor() != null ? project.getAuthor() : "");
			elem.addElement("dateVersion").setText(project.getDate() != null ? DateFormat.getDateInstance(SimpleDateFormat.SHORT).format(project.getDate()) : "");

			List<RScript> scripts = session.getServiceManager().getRScriptsbyProject(project.getId());

			if (scripts != null && !scripts.isEmpty()) {
				Element elScripts = elem.addElement("scripts");
				for (RScript script : scripts) {
					Element elemScript = elScripts.addElement("script");
					elemScript.addElement("scriptId").setText(script.getId() + "");
					elemScript.addElement("scriptName").setText(script.getName());
					elemScript.addElement("type").setText(script.getScriptType());
					// elemScript.addElement("code").setText(last.getScript());
					// elemScript.addElement("dateVersion").setText(DateFormat.getDateInstance(SimpleDateFormat.SHORT).format(script.getDateVersion()));
					elemScript.addElement("comment").setText((script.getComment() == null) ? "" : script.getComment());
				}
			}
		}

		resp.getWriter().write(doc.asXML());
	}
	

	private void buildFullLineXML(AirSession session, HttpServletResponse resp) throws Exception {
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("root");
		
		List<AirProject> projects = session.getServiceManager().getAllAirProjects();

		for (AirProject project : projects) {
			List<RScript> scripts = session.getServiceManager().getRScriptsbyProject(project.getId());
			
			if (scripts != null && !scripts.isEmpty()) {
				
				for (RScript script : scripts) {
					Element elem = root.addElement("project");
					elem.addElement("projectId").setText(project.getId() + "");
					elem.addElement("projectName").setText(project.getName());
					elem.addElement("author").setText(project.getAuthor() != null ? project.getAuthor() : "");
					elem.addElement("dateVersion").setText(project.getDate() != null ? DateFormat.getDateInstance(SimpleDateFormat.SHORT).format(project.getDate()) : "");

					elem.addElement("scriptId").setText(script.getId() + "");
					elem.addElement("scriptName").setText(script.getName());
					elem.addElement("type").setText(script.getScriptType());
					elem.addElement("comment").setText((script.getComment() == null) ? "" : script.getComment());
				}
			}
		}

		resp.getWriter().write(doc.asXML());
	}

	private void getRScriptCode(AirSession session, String selectedProject, String selectedScript, HttpServletResponse resp) throws Exception {
		List<AirProject> projects = session.getServiceManager().getAllAirProjects();
		if (projects != null) {
			for (AirProject project : projects) {
				if (project.getName().equals(selectedProject)) {

					List<RScript> scripts = session.getServiceManager().getRScriptsbyProject(project.getId());

					if (scripts != null) {
						for (RScript script : scripts) {
							if (script.getName().equals(selectedScript)) {
								RScriptModel lastModel = session.getServiceManager().getLastModelbyScript(script.getId());
								String scriptCode = lastModel.getScript();

								resp.setContentType("text/plain; charset=UTF-8");
								resp.getWriter().write(scriptCode);
								return;
							}
						}
					}
				}
			}
		}

		return;
	}
}
