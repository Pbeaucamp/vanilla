package bpm.vanilla.api.runtime.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPChart;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.repository.FaApiHelper;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.api.core.IAPIManager.OLAPMethod;
import bpm.vanilla.api.core.exception.VanillaApiError;
import bpm.vanilla.api.core.exception.VanillaApiException;
import bpm.vanilla.api.runtime.dto.CubeObject;
import bpm.vanilla.api.runtime.dto.CubeParameter;
import bpm.vanilla.api.runtime.dto.CubePercentMeasure;
import bpm.vanilla.api.runtime.dto.CubeQuery;
import bpm.vanilla.api.runtime.dto.FasdItem;
import bpm.vanilla.api.runtime.dto.OLAPDirectory;
import bpm.vanilla.api.runtime.dto.RepositoryComponent;
import bpm.vanilla.api.runtime.utils.Constants;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;

public class OLAPService {
	private IVanillaSecurityManager vanillaSecurityManager;
	private IRepositoryManager repositoryManager;
	private IVanillaContext vanillaCtx;

	private enum OLAPAction {
		DRILL, FILTER, TREEVIEW_EXPAND, ADD_COL, ADD_ROW;
	}

	public OLAPService() {
		VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
		String url = config.getProperty(VanillaConfiguration.P_VANILLA_URL);
		String root = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
		String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);

		this.vanillaCtx = new BaseVanillaContext(url, root, password);
		IVanillaAPI vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		this.vanillaSecurityManager = vanillaApi.getVanillaSecurityManager();
		this.repositoryManager = vanillaApi.getVanillaRepositoryManager();

	}

	public String dispatchAction(String method, JSONArray parameters) throws Exception {

		OLAPMethod olapMethod = OLAPMethod.valueOf(method);
		switch (olapMethod) {
		case GET_CUBES:
			return getCubes(parameters);
		case EXECUTE_CUBE_QUERY:
			return executeCubeQuery(parameters);
		case GET_CUBE_VIEW:
			return getCubeView(parameters);
		case SAVE_CUBE_VIEW:
			return saveCubeView(parameters);
		case GET_DETAILS:
			return getDetails(parameters);
		case SEARCH_ON_DIMENSIONS:
			return searchOnDimensions(parameters);
		case GET_PARAMETERS:
			return getParameters(parameters);
		case SET_PARAMETERS:
			return setParameters(parameters);
		}
		throw new VanillaApiException(VanillaApiError.METHOD_DOES_NOT_EXIST);
	}

	// Get all cube names
	public String getCubes(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		RemoteRepositoryApi api = getApi(repositoryID, groupID);

		try {
			IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api, IRepositoryApi.FASD_TYPE);// filter
																													// FASD
																													// Items
			List<RepositoryComponent> repositoryBrowser = browseRepository(api, rep, (RepositoryDirectory) null);
			// Create and return the String JSON
			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			return mapper.writeValueAsString(repositoryBrowser);

		} catch (Exception e) {
			throw new VanillaApiException(VanillaApiError.UNABLE_GET_CUBES);
		}
	}

	// Execute cube query
	public String executeCubeQuery(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int fasdID = parameters.getInt(2);
		String cubeName = parameters.getString(3);
		String query = parameters.getString(4);

		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);
		RepositoryItem fasdItem = rep.getItem(fasdID);

		// Load the cube using a cube name
		OLAPCube cube = loadCube(api, fasdID, cubeName);

		// Create CubeQuery object
		ObjectMapper objectMapper = new ObjectMapper();
		CubeQuery cubeQuery = objectMapper.readValue(query, CubeQuery.class);

		// Build the mdx query
		doCubeQuery(cube, cubeQuery);

		// Execute the OLAPQuery to get results
		cube.doQuery();
		CubeObject cubeObj = loadCubeObject(api, fasdItem, cubeName, null, cube,false);

		// Create the String JSON
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		return mapper.writeValueAsString(cubeObj);
	}

	public String getCubeView(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int fasdID = parameters.getInt(2);
		String cubeName = parameters.getString(3);
		int viewID = parameters.getInt(4);

		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);
		RepositoryItem fasdItem = rep.getItem(fasdID);

		// Load the cube view
		OLAPCube cube = loadCube(api, fasdID, cubeName);
		RepositoryItem viewItem = null;
		if (viewID != -1) {
			viewItem = rep.getItem(viewID);
			loadCubeView(api, cube, viewItem);
		}

		// Create the mapper for json
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		// Execute the OLAPQuery to get results
		cube.doQuery();
		CubeObject cubeObj = loadCubeObject(api, fasdItem, cubeName, viewItem, cube,true);

		return mapper.writeValueAsString(cubeObj);
	}

	public String saveCubeView(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int fasdID = parameters.getInt(2);
		String cubeName = parameters.getString(3);
		int viewID = parameters.getInt(4);
		String viewName = parameters.getString(5);
		boolean export = parameters.getBoolean(6);
		String query = parameters.getString(7);

		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);
		RepositoryItem fasdItem = rep.getItem(fasdID);
		RepositoryDirectory fasdDirectory = rep.getDirectory(fasdItem.getDirectoryId());

		// Load the cube using a cube name
		OLAPCube cube = loadCube(api, fasdID, cubeName);
		RepositoryItem viewItem = rep.getItem(viewID);

		RepositoryCubeView repView = null;
		if (viewItem != null) {
			repView = loadCubeView(api, cube, viewItem);
		}

		// Create CubeQuery object
		ObjectMapper objectMapper = new ObjectMapper();
		CubeQuery cubeQuery = objectMapper.readValue(query, CubeQuery.class);

		// Build the mdx query
		doCubeQuery(cube, cubeQuery);

		// Execute the OLAPQuery to get results
		cube.doQuery();

		CubeObject cubeObj;
		
		String base64img = cubeQuery.getBase64img();
		if((base64img==null) || (!(base64img.startsWith("data:image/png;base64")))) {
			if(repView != null) {
				base64img = repView.getBase64img();
			}
			else {
				base64img = null;
			}
		}
		
		OLAPChart chart = cubeQuery.getChart();
		if(chart==null) {
			if(repView != null) {
				chart = repView.getChart();
			}
			else {
				chart = null;
			}
		}
		
		String viewXML = createViewXML(viewName, cubeName, fasdID, cube.getView().getXML(), base64img,chart);
		if (export) {
			// Create view item
			RepositoryItem newViewItem = api.getRepositoryService().addDirectoryItemWithDisplay(IRepositoryApi.FAV_TYPE, -1, fasdDirectory, viewName, "", "", "", viewXML, true);
			cubeObj = loadCubeObject(api, fasdItem, cubeName, newViewItem, cube,true);
		}
		else {
			// Update view item
			viewItem.setItemName(viewName);
			api.getRepositoryService().updateModel(viewItem, viewXML);
			cubeObj = loadCubeObject(api, fasdItem, cubeName, viewItem, cube,true);
		}

		// Create the String JSON
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		return mapper.writeValueAsString(cubeObj);
	}

	public String getDetails(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int fasdID = parameters.getInt(2);
		String cubeName = parameters.getString(3);
		String drillThroughSQL = parameters.getString(4);
		String query = parameters.getString(5);

		RemoteRepositoryApi api = getApi(repositoryID, groupID);

		// Load the cube using a cube name
		OLAPCube cube = loadCube(api, fasdID, cubeName);

		// Create CubeQuery object
		ObjectMapper objectMapper = new ObjectMapper();
		CubeQuery cubeQuery = objectMapper.readValue(query, CubeQuery.class);

		// Build the mdx query
		doCubeQuery(cube, cubeQuery);

		// Execute the OLAPQuery to get results
		ItemValue itDrillThrough = getItemValue(cube, drillThroughSQL);
		OLAPResult res = cube.drillthrough(itDrillThrough, 1);

		return getDetailsResultJSON(res).toString();
	}

	public String searchOnDimensions(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int fasdID = parameters.getInt(2);
		String cubeName = parameters.getString(3);
		String word = parameters.getString(4);

		RemoteRepositoryApi api = getApi(repositoryID, groupID);

		// Load the cube using a cube name
		OLAPCube cube = loadCube(api, fasdID, cubeName);

		List<String> dimMemberArray = new ArrayList<>();

		for (Dimension dim : cube.getDimensions()) {
			for (Hierarchy hiera : dim.getHierarchies()) {
				String defMem = hiera.getDefaultMember().getUniqueName();
				if (defMem.toLowerCase(Locale.ROOT).contains(word.toLowerCase(Locale.ROOT))) {
					dimMemberArray.add(defMem);
				}
				for (Level lvl : hiera.getLevel()) {
					dimMemberArray.addAll(cube.searchOnDimensions(word, lvl.getUniqueName()));
				}
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		return mapper.writeValueAsString(dimMemberArray);
	}

	public String getParameters(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int fasdID = parameters.getInt(2);
		String cubeName = parameters.getString(3);
		int viewID = parameters.getInt(4);

		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);

		// Load the cube using a cube name
		OLAPCube cube = loadCube(api, fasdID, cubeName);
		RepositoryItem viewItem = rep.getItem(viewID);
		loadCubeView(api, cube, viewItem);

		List<CubeParameter> cubeParameters = getCubeParameters(cube);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		return mapper.writeValueAsString(cubeParameters);
	}

	public String setParameters(JSONArray parameters) throws Exception {
		int repositoryID = parameters.getInt(0);
		int groupID = parameters.getInt(1);
		int fasdID = parameters.getInt(2);
		String cubeName = parameters.getString(3);
		int viewID = parameters.getInt(4);
		String sendedCubeParametersStr = parameters.getString(5);

		RemoteRepositoryApi api = getApi(repositoryID, groupID);
		IRepository rep = new bpm.vanilla.platform.core.repository.Repository(api);

		// Load the cube using a cube name
		OLAPCube cube = loadCube(api, fasdID, cubeName);
		RepositoryItem viewItem = rep.getItem(viewID);
		loadCubeView(api, cube, viewItem);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		List<CubeParameter> sendedCubeParameters = Arrays.asList(mapper.readValue(sendedCubeParametersStr, CubeParameter[].class));
		setCubeParameters(cube, sendedCubeParameters);
		cube.doQuery();

		// Update view item
		String newViewXML = createViewXML(viewItem.getName(), cubeName, fasdID, cube.getView().getXML(), null, null);
		api.getRepositoryService().updateModel(viewItem, newViewXML);

		List<CubeParameter> cubeParameters = getCubeParameters(cube);
		return mapper.writeValueAsString(cubeParameters);
	}

	// Create the RemoteRepositoryApi for the main methods(getCube() and
	// getCubes())
	private RemoteRepositoryApi getApi(int repositoryID, int groupID) {
		RemoteRepositoryApi api;

		try {
			Group group = null;
			try {
				group = vanillaSecurityManager.getGroupById(groupID);
			} catch (Exception e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
			}
			if (group == null) {
				throw new VanillaApiException(VanillaApiError.GROUP_NOT_FOUND);
			}

			Repository repo = null;
			try {
				repo = repositoryManager.getRepositoryById(repositoryID);
			} catch (Exception e) {
				e.printStackTrace();
				throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
			}

			IRepositoryContext ctx = new BaseRepositoryContext(vanillaCtx, group, repo);
			api = new RemoteRepositoryApi(ctx);

		} catch (Exception e1) {
			e1.printStackTrace();
			throw new VanillaApiException(VanillaApiError.REPOSITORY_NOT_FOUND);
		}
		return api;
	}

	// Browse repository
	private List<RepositoryComponent> browseRepository(RemoteRepositoryApi api, IRepository rep, RepositoryDirectory currentDir) {
		List<RepositoryComponent> repComponents = new ArrayList<>();

		try {
			for (RepositoryDirectory childDir : rep.getChildDirectories(currentDir)) {
				if (childDir.isShowed()) {
					List<RepositoryComponent> children = browseRepository(api, rep, childDir);
					repComponents.add(new OLAPDirectory(childDir, children));
				}
			}

			for (RepositoryItem it : rep.getItems((RepositoryDirectory) currentDir)) {

				if (it.isDisplay() && it.getName() != null) {
					String fasdxml = api.getRepositoryService().loadModel(it);
					repComponents.add(new FasdItem(it, fasdxml));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new VanillaApiException(VanillaApiError.FILE_NOT_FOUND);
		}

		return repComponents;

	}

	// Create an OLAPCube object
	private OLAPCube loadCube(RemoteRepositoryApi api, int fasdID, String cubeName) throws Exception {
		String vanUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		FaApiHelper fa = new FaApiHelper(vanUrl, new UnitedOlapLoader());

		int repID = api.getContext().getRepository().getId();
		Group group = api.getContext().getGroup();

		IObjectIdentifier identifier = new ObjectIdentifier(repID, fasdID);
		IRuntimeContext ctx = new RuntimeContext(vanillaCtx.getLogin(), vanillaCtx.getPassword(), group.getName(), group.getId());

		return fa.getCube(identifier, ctx, cubeName);
	}

	// Set a view for an OLAPCube
	private RepositoryCubeView loadCubeView(RemoteRepositoryApi api, OLAPCube cube, RepositoryItem viewItem) throws Exception {
		String fav = api.getRepositoryService().loadModel(viewItem);
		Document document = DocumentHelper.parseText(fav);
		Element root = document.getRootElement();
		String view = root.element("view").asXML();

		DigesterCubeView dig = new DigesterCubeView(view);
		RepositoryCubeView repcubeview = dig.getCubeView();
		cube.setView(repcubeview);
		return repcubeview;
	}

	private List<CubeParameter> getCubeParameters(OLAPCube cube) {
		List<CubeParameter> parameters = new ArrayList<>();

		for (Parameter p : cube.getParameters()) {
			parameters.add(new CubeParameter(cube, p));
		}

		return parameters;
	}
	
	private CubeObject loadCubeObject(RemoteRepositoryApi api, RepositoryItem fasdItem, String cubeName, RepositoryItem viewItem, OLAPCube cube, boolean showViews) throws Exception {
		String fasdxml = api.getRepositoryService().loadModel(fasdItem);

		String favxml = null;
		if (viewItem != null) {
			favxml = api.getRepositoryService().loadModel(viewItem);
		}

		HashMap<RepositoryItem, String> viewsxml = null;
		if(showViews) {
			viewsxml = new HashMap<>();
			for (RepositoryItem it : api.getRepositoryService().getCubeViews(cubeName, fasdItem)) {
				viewsxml.put(it, api.getRepositoryService().loadModel(it));
			}
		}

		return new CubeObject(fasdItem, fasdxml, cubeName, viewsxml, viewItem, favxml, cube);
	}

	
	
	// // Conditions on cells
	//
	// Verify if it is a dimension member(ex:All Time,2008...)
	private boolean isDimMember(Item it) {
		return (it instanceof ItemElement) && (!(((ItemElement) it).getDataMember().getUniqueName().contains("[Measures]")));
	}


	// Verify if its label is an empty string
	private boolean isEmpty(Item it) {
		return it instanceof ItemNull;
	}

	// Verify if its label is a value
	private boolean isValue(Item it) {
		return !(isEmpty(it) || (it instanceof ItemElement));
	}

	private OLAPMember findDimensionMember(OLAPCube cube, String uniqueName) throws Exception {
		OLAPMember member = cube.findOLAPMember(uniqueName);

		if (member == null) {
			for (Dimension dim : cube.getDimensions()) {
				for (Hierarchy h : dim.getHierarchies()) {
					String memUname = h.getDefaultMember().getUniqueName();
					if (uniqueName.startsWith(memUname)) {
						OLAPMember parentMember = h.getDefaultMember();
						boolean hasChild = (parentMember.getMembers().size() > 0) || (cube.addChilds(parentMember, parentMember.getHiera()));
						while ((!(parentMember.getUniqueName().equals(uniqueName))) && (hasChild)) {
							List<OLAPMember> members = new ArrayList<>();
							members.addAll(parentMember.getMembersVector());
							for (OLAPMember childMember : members) {
								if ((uniqueName.startsWith(childMember.getUniqueName())) || (parentMember.getUniqueName().equals(uniqueName))) {
									parentMember = childMember;
									hasChild = (parentMember.getMembers().size() > 0) || (cube.addChilds(parentMember, parentMember.getHiera()));
									break;
								}
							}
						}
						if (parentMember.getUniqueName().equals(uniqueName)) {
							member = parentMember;
						}
						break;
					}
				}
			}
		}
		return member;
	}

	// modify the cube result by a dimension member(identified by its Unique
	// Name)
	private void modifyCube(OLAPCube cube, String memberUniqueName, OLAPAction action) throws Exception {

		switch (action) {

		// expand a treeview member
		case TREEVIEW_EXPAND:
			OLAPMember member = findDimensionMember(cube, memberUniqueName);
			cube.addChilds(member, member.getHiera());
			break;

		// drill the cube
		case DRILL:
			OLAPResult res = cube.doQuery();
			ArrayList<ArrayList<Item>> rawContent = res.getRaw();
			for (ArrayList<Item> row : rawContent) {
				for (Item it : row) {
					if (isValue(it)) {
						break;
					}
					if ((isDimMember(it)) && (((ItemElement) it).getDataMember().getUniqueName().equals(memberUniqueName))) {

						ItemElement it_elem = (ItemElement) it;
						if (it_elem.isDrilled()) {
							cube.drillup(it_elem);
						}
						else {
							cube.drilldown(it_elem);
						}
						break;
					}
				}
			}
			break;

		// add a filter to the cube
		case FILTER:
			cube.getMdx().addWhere(memberUniqueName);
			break;

		// add a column to the cube
		case ADD_COL:
			if (cube.getMdx().canAddToCol(memberUniqueName)) {
				cube.getMdx().addcol(memberUniqueName);
			}
			break;

		// add a row to the cube
		case ADD_ROW:
			if (cube.getMdx().canAddToRow(memberUniqueName)) {
				cube.getMdx().addrow(memberUniqueName);
			}
			break;
		}

	}

	// do modifications according to the action(drill,add_filter...)
	private void doModifications(OLAPCube cube, List<String> modifications, OLAPAction action) throws Exception {

		if ((modifications != null) && (!modifications.isEmpty())) {
			for (String modification : modifications) {
				modifyCube(cube, modification, action);
			}
		}
	}

	private void setCubeParameters(OLAPCube cube, List<CubeParameter> cubeParameters) {

		cube.setParameters(new ArrayList<>());
		int i = 0;
		for (CubeParameter cubeParameter : cubeParameters) {
			cube.addParameter(cubeParameter.getName(), cubeParameter.getValue(), cubeParameter.getLevel());
			cube.getParameters().get(i).setUname(cubeParameter.getUname());
			;
			i++;
		}
	}

	private void setPercentMeasures(OLAPCube cube, List<CubePercentMeasure> percentMeasures) {
		for (CubePercentMeasure percentMeas : percentMeasures) {
			cube.addPercentMeasure(percentMeas.getMeasureName(), percentMeas.isShowMeasure());
		}
	}

	// build the mdx query
	private void doCubeQuery(OLAPCube cube, CubeQuery cubeQuery) throws Exception {
		cube.getMdx().clearAxes();// reset cols and rows
		doModifications(cube, cubeQuery.getCols(), OLAPAction.ADD_COL);
		doModifications(cube, cubeQuery.getRows(), OLAPAction.ADD_ROW);
		if (cubeQuery.isSwapAxes()) {
			doModifications(cube, cubeQuery.getMeasures(), OLAPAction.ADD_COL);
			cube.swapAxes();
		}
		else {
			doModifications(cube, cubeQuery.getMeasures(), OLAPAction.ADD_ROW);
		}
		doModifications(cube, cubeQuery.getDrills(), OLAPAction.DRILL);
		doModifications(cube, cubeQuery.getTreeviewExpand(), OLAPAction.TREEVIEW_EXPAND);
		doModifications(cube, cubeQuery.getFilters(), OLAPAction.FILTER);

		cube.setStateActive(cubeQuery.isActive());
		cube.setshowEmpty(cubeQuery.isShowEmpty());

		setPercentMeasures(cube, cubeQuery.getPercentMeasures());
		// setCubeParameters(cube,cubeQuery.parameters);
	}

	// create an XML for a view item
	private String createViewXML(String viewName, String cubeName, int fasdID, String xml, String base64img, OLAPChart chart) throws Exception {
		// get old XML
		Document old_document = DocumentHelper.parseText(xml);
		Element viewElement = old_document.getRootElement();
		
		//add base64image to xml
		if((base64img != null) && (base64img.startsWith("data:image/png;base64"))) {
			viewElement.remove(viewElement.element("base64img"));
			viewElement.addElement("base64img").setText(base64img);
		}
		
		//add chart object to xml
		if(chart != null) {
			viewElement.remove(viewElement.element("chart"));
			Element chartElement = DocumentHelper.parseText(chart.loadXML()).getRootElement();
			viewElement.add(chartElement);
		}

		// create new XML
		Document new_document = DocumentHelper.createDocument();
		Element root = new_document.addElement("fav");
		new_document.setRootElement(root);

		// put elements on XML
		root.addElement("name").addText(viewName);
		root.addElement("cubename").addText(cubeName);
		root.addElement("fasdid").addText(String.valueOf(fasdID));
		root.add(viewElement);

		return root.asXML();
	}

	// get item by drillthroughSQL
	private ItemValue getItemValue(OLAPCube cube, String drillThroughSQL) throws Exception {
		OLAPResult res = cube.doQuery();

		for (ArrayList<Item> row : res.getRaw()) {
			for (Item it : row) {
				if ((it instanceof ItemValue) && (((ItemValue) it).getDrillThroughSql().equals(drillThroughSQL))) {
					return (ItemValue) it;
				}
			}
		}

		return null;
	}

	// return a JSON of restructured details result
	private JSONObject getDetailsResultJSON(OLAPResult res) throws JSONException {
		JSONObject json = new JSONObject();

		ArrayList<ArrayList<Item>> raw = res.getRaw();

		ArrayList<String> propertyArray = new ArrayList<>();

		// Create headers for vuetify data tables
		JSONArray headers = new JSONArray();

		JSONObject headerID = new JSONObject();
		headerID.put(Constants.TEXT, "ID");
		headerID.put(Constants.VALUE, Constants.ID);
		headerID.put(Constants.ALIGN, "start");
		headers.put(headerID);

		for (Item it : raw.get(0)) {
			JSONObject header = new JSONObject();
			String propertytext = it.getLabel();
			String propertyvalue = it.getLabel().toLowerCase(Locale.ROOT);
			header.put(Constants.TEXT, propertytext);
			header.put(Constants.VALUE, propertyvalue);
			propertyArray.add(propertyvalue);
			headers.put(header);
		}
		json.put(Constants.HEADERS, headers);

		// Create details for vuetify data tables
		JSONArray data = new JSONArray();

		for (int i = 1; i < raw.size(); i++) {
			JSONObject line = new JSONObject();
			line.put(Constants.ID, i);
			for (int j = 0; j < raw.get(i).size(); j++) {
				String it_label = raw.get(i).get(j).getLabel();
				String property = propertyArray.get(j);
				line.put(property, it_label);
			}
			data.put(line);
		}
		json.put(Constants.DATA, data);

		return json;
	}

}
