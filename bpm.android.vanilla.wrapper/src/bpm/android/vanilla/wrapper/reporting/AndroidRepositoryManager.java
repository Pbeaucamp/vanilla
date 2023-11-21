package bpm.android.vanilla.wrapper.reporting;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;

import com.thoughtworks.xstream.XStream;

import bpm.android.vanilla.core.IAndroidRepositoryManager;
import bpm.android.vanilla.core.beans.AndroidCube;
import bpm.android.vanilla.core.beans.AndroidCubeView;
import bpm.android.vanilla.core.beans.AndroidItem;
import bpm.android.vanilla.core.beans.Parameter;
import bpm.android.vanilla.core.beans.cube.AndroidDimension;
import bpm.android.vanilla.core.beans.cube.AndroidMeasureGroup;
import bpm.android.vanilla.core.beans.cube.HierarchyAndCol;
import bpm.android.vanilla.core.beans.cube.MeasureAndCol;
import bpm.android.vanilla.core.beans.report.AndroidDocumentDefinition;
import bpm.android.vanilla.core.beans.report.DashboardRunItem;
import bpm.android.vanilla.core.beans.report.IRunItem;
import bpm.android.vanilla.core.beans.report.ReportRunItem;
import bpm.android.vanilla.wrapper.tools.CubeInfos;
import bpm.android.vanilla.wrapper.tools.SessionContent;
import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.api.repository.FaApiHelper;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.disco.DiscoPackage;
import bpm.vanilla.platform.core.beans.disco.DiscoReportConfiguration;
import bpm.vanilla.platform.core.beans.ged.ComProperties;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.components.ReportingComponent;
import bpm.vanilla.platform.core.components.ged.GedLoadRuntimeConfig;
import bpm.vanilla.platform.core.components.ged.GedSearchRuntimeConfig;
import bpm.vanilla.platform.core.components.report.ReportRuntimeConfig;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.impl.RuntimeConfiguration;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.remote.impl.components.RemoteFdRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteReportRuntime;
import bpm.vanilla.platform.core.remote.impl.components.RemoteVanillaParameterComponent;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class AndroidRepositoryManager implements IAndroidRepositoryManager {

	private enum TypeAction {
		FIRST_TIME, INIT, SWAP, HIDE, DRILL, CHANGE_DIM_OR_MEA, OPEN_VIEW;
	}

	private RemoteRepositoryApi sock;
	private SessionContent session;
	private IVanillaAPI vanillaApi;

	public AndroidRepositoryManager(SessionContent session) {
		this.session = session;
	}

	public void setRepositoryContext(IVanillaContext vanillaCtx, Group group, Repository repository) {
		vanillaApi = new RemoteVanillaPlatform(vanillaCtx);

		IRepositoryContext repositoryCtx = new BaseRepositoryContext(vanillaCtx, group, repository);
		sock = new RemoteRepositoryApi(repositoryCtx);
	}

	@Override
	public List<AndroidItem> getRepositoryContent() throws Exception {
		IRepository repositoryContent = new bpm.vanilla.platform.core.repository.Repository(sock);
		List<RepositoryDirectory> dirs = repositoryContent.getRootDirectories();

		List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		getChilds(items, dirs, repositoryContent);

		List<AndroidItem> androidItems = new ArrayList<AndroidItem>();
		for (RepositoryItem item : items) {
			AndroidItem androidItem = null;
			if (item.getType() == IRepositoryApi.FASD_TYPE) {
				List<String> cubeNames = getCubeNames(item);
				androidItem = new AndroidCube(item.getId(), item.getName(), item.getType(), item.getSubtype(), cubeNames);
			}
			else if (item.getType() == IRepositoryApi.CUST_TYPE || item.getType() == IRepositoryApi.FWR_TYPE || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FD_DICO_TYPE || item.getType() == IRepositoryApi.DISCONNECTED_PCKG) {

				List<Parameter> parameters = getWrapsParameters(item.getId(), item.getType());
				androidItem = new AndroidItem(item.getId(), item.getName(), item.getType(), item.getSubtype(), parameters);
			}
			androidItems.add(androidItem);
		}
		return androidItems;
	}

	@Override
	public AndroidCube loadCubeView(AndroidCube selectedCube, AndroidCubeView androidView) throws Exception {
		CubeInfos cubeInfos = session.getCubeInfos();
		OLAPCube olapCube = loadCubeView(cubeInfos, androidView);

		List<Dimension> dimsAvailable = getDimensions(olapCube);
		List<MeasureGroup> measAvailable = getMeasures(olapCube);

		List<String> cols = getCols(olapCube);
		List<String> rows = getRows(olapCube);

		for (Dimension dim : dimsAvailable) {

			AndroidDimension androidDim = new AndroidDimension(dim.getUniqueName(), dim.getName());
			for (Hierarchy hier : dim.getHierarchies()) {
				// Check if the hier is load by default and if it is in row or
				// in column
				boolean isLoad = false;
				boolean isCol = false;
				for (String col : cols) {
					if (col.contains(hier.getUniqueName())) {
						isLoad = true;
						isCol = true;
						break;
					}
				}
				if (isLoad == false)
					for (String row : rows) {
						if (row.contains(hier.getUniqueName())) {
							isLoad = true;
							break;
						}
					}

				HierarchyAndCol androidHier = new HierarchyAndCol(hier.getUniqueName(), hier.getName(), isCol, isLoad, androidDim);
				androidDim.addHierarchyAndCol(androidHier);
			}

			selectedCube.addDimension(androidDim);
		}

		for (MeasureGroup meaGroup : measAvailable) {

			AndroidMeasureGroup androidMeaGroup = new AndroidMeasureGroup(meaGroup.getUniqueName(), meaGroup.getName());
			for (Measure mea : meaGroup.getMeasures()) {
				boolean isLoad = false;
				boolean isCol = false;
				for (String col : cols) {
					if (col.contains(mea.getUniqueName())) {
						isLoad = true;
						isCol = true;
						break;
					}
				}
				if (isLoad == false)
					for (String row : rows) {
						if (row.contains(mea.getUniqueName())) {
							isLoad = true;
							break;
						}
					}

				MeasureAndCol androidMea = new MeasureAndCol(mea.getUniqueName(), mea.getName(), isCol, isLoad, androidMeaGroup);
				androidMeaGroup.addMeasuresAndCol(androidMea);
			}

			selectedCube.addMeasure(androidMeaGroup);
		}

		selectedCube.setHtml(getXml(cubeInfos, "", TypeAction.FIRST_TIME));

		return selectedCube;
	}

	@Override
	public AndroidCube loadCube(AndroidCube selectedCube, boolean isIphone) throws Exception {
		RepositoryItem fasdItem = sock.getRepositoryService().getDirectoryItem(selectedCube.getId());
		OLAPCube olapCube = loadCubeSelected(selectedCube.getId(), selectedCube.getSelectedCubeName());

		CubeInfos cubeInfos = new CubeInfos(olapCube, selectedCube.getSelectedCubeName(), isIphone);
		session.setCubeInfos(cubeInfos);

		HashMap<RepositoryItem, byte[]> views = getCubeViews(selectedCube.getSelectedCubeName(), fasdItem);

		List<Dimension> dimsAvailable = getDimensions(olapCube);
		List<MeasureGroup> measAvailable = getMeasures(olapCube);

		List<String> cols = getCols(olapCube);
		List<String> rows = getRows(olapCube);

		for (RepositoryItem view : views.keySet()) {
			String img = "";
			for (byte b : views.get(view)) {
				img += b + ":";
			}
			img = img.substring(0, img.length() - 1);

			AndroidCubeView androidView = new AndroidCubeView(view.getId(), view.getItemName(), img);
			selectedCube.addView(androidView);
		}

		for (Dimension dim : dimsAvailable) {

			AndroidDimension androidDim = new AndroidDimension(dim.getUniqueName(), dim.getName());
			for (Hierarchy hier : dim.getHierarchies()) {
				// Check if the hier is load by default and if it is in row or
				// in column
				boolean isLoad = false;
				boolean isCol = false;
				for (String col : cols) {
					if (col.contains(hier.getUniqueName())) {
						isLoad = true;
						isCol = true;
						break;
					}
				}
				if (isLoad == false)
					for (String row : rows) {
						if (row.contains(hier.getUniqueName())) {
							isLoad = true;
							break;
						}
					}

				HierarchyAndCol androidHier = new HierarchyAndCol(hier.getUniqueName(), hier.getName(), isCol, isLoad, androidDim);
				androidDim.addHierarchyAndCol(androidHier);
			}

			selectedCube.addDimension(androidDim);
		}

		for (MeasureGroup meaGroup : measAvailable) {

			AndroidMeasureGroup androidMeaGroup = new AndroidMeasureGroup(meaGroup.getUniqueName(), meaGroup.getName());
			for (Measure mea : meaGroup.getMeasures()) {
				boolean isLoad = false;
				boolean isCol = false;
				for (String col : cols) {
					if (col.contains(mea.getUniqueName())) {
						isLoad = true;
						isCol = true;
						break;
					}
				}
				if (isLoad == false)
					for (String row : rows) {
						if (row.contains(mea.getUniqueName())) {
							isLoad = true;
							break;
						}
					}

				MeasureAndCol androidMea = new MeasureAndCol(mea.getUniqueName(), mea.getName(), isCol, isLoad, androidMeaGroup);
				androidMeaGroup.addMeasuresAndCol(androidMea);
			}

			selectedCube.addMeasure(androidMeaGroup);
		}

		selectedCube.setHtml(getXml(cubeInfos, "", TypeAction.FIRST_TIME));

		return selectedCube;
	}

	@Override
	public AndroidCube changeDimensions(AndroidCube cube, List<HierarchyAndCol> selectedHiers) throws Exception {
		CubeInfos cubeInfos = session.getCubeInfos();
		OLAPCube faCube = cubeInfos.getFaCube();

		List<ItemElement> elToRemove = new ArrayList<ItemElement>();
		for (HierarchyAndCol hAndCol : selectedHiers) {
			Hierarchy h = findHierarchy(faCube, hAndCol.getUniqueName(), hAndCol.getDimension().getUniqueName());
			boolean hierAsCol = faCube.getMdx().colHasHiera(h.getUniqueName());
			boolean hierAsRow = faCube.getMdx().rowHasHiera(h.getUniqueName());

			boolean isCol = hAndCol.getIsCol();
			if (hAndCol.isToAdd()) {
				faCube.add(new ItemElement(h.getDefaultMember().getUniqueName(), isCol));
			}

			if (hAndCol.isToRemove()) {
				elToRemove.add(new ItemElement(h.getDefaultMember().getUniqueName(), hierAsCol));
			}

			if (!hAndCol.isToAdd() && !hAndCol.isToRemove() && ((hierAsCol && !isCol) || (hierAsRow && isCol))) {
				faCube.remove(new ItemElement(h.getDefaultMember().getUniqueName(), hierAsCol));
				faCube.add(new ItemElement(h.getDefaultMember().getUniqueName(), isCol));
			}
		}

		for (ItemElement el : elToRemove) {
			faCube.remove(el);
		}

		String html = getXml(cubeInfos, "", TypeAction.CHANGE_DIM_OR_MEA);

		if (cube != null) {
			loadCube(cube, cubeInfos);
			cube.setHtml(html);
		}
		else {
			cube = new AndroidCube();
			cube.setHtml(html);
		}

		return cube;
	}

	@Override
	public AndroidCube changeMeasures(AndroidCube cube, List<MeasureAndCol> selectedMeas) throws Exception {
		CubeInfos cubeInfos = session.getCubeInfos();
		OLAPCube faCube = cubeInfos.getFaCube();

		List<ItemElement> elToAdd = new ArrayList<ItemElement>();
		List<ItemElement> elToRemove = new ArrayList<ItemElement>();
		for (MeasureAndCol mAndCol : selectedMeas) {
			Measure m = findMeasure(faCube, mAndCol.getUniqueName(), mAndCol.getMeasureGroup().getUniqueName());
			boolean isCol = mAndCol.getIsCol();
			
			if (mAndCol.isToAdd()) {
				elToAdd.add(new ItemElement(m.getUniqueName(), isCol));
			}

			if (mAndCol.isToRemove()) {
				elToRemove.add(new ItemElement(m.getUniqueName(), isCol));
			}
			
			if (mAndCol.changeOrientation()) {
				elToRemove.add(new ItemElement(m.getUniqueName(), !isCol));
				elToAdd.add(new ItemElement(m.getUniqueName(), isCol));
			}
		}

		for (ItemElement el : elToRemove) {
			faCube.remove(el);
		}

		for (ItemElement el : elToAdd) {
			faCube.add(el);
		}

		String html = getXml(cubeInfos, "", TypeAction.CHANGE_DIM_OR_MEA);
		if (cube != null) {
			loadCube(cube, cubeInfos);
			cube.setHtml(html);
		}
		else {
			cube = new AndroidCube();
			cube.setHtml(html);
		}

		return cube;
	}

	@Override
	public String swapCube() throws Exception {
		CubeInfos cubeInfos = session.getCubeInfos();

		return getXml(cubeInfos, "", TypeAction.SWAP);
	}

	@Override
	public AndroidCube initCube(AndroidCube cube) throws Exception {
		CubeInfos cubeInfos = session.getCubeInfos();

		String html = getXml(cubeInfos, "", TypeAction.INIT);
		loadCube(cube, cubeInfos);
		cube.setHtml(html);

		return cube;
	}

	private void loadCube(AndroidCube cube, CubeInfos cubeInfos) {
		cube.getDimensions().clear();
		cube.getMeasures().clear();

		List<Dimension> dimsAvailable = getDimensions(cubeInfos.getFaCube());
		List<MeasureGroup> measAvailable = getMeasures(cubeInfos.getFaCube());

		List<String> cols = getCols(cubeInfos.getFaCube());
		List<String> rows = getRows(cubeInfos.getFaCube());

		for (Dimension dim : dimsAvailable) {

			AndroidDimension androidDim = new AndroidDimension(dim.getUniqueName(), dim.getName());
			for (Hierarchy hier : dim.getHierarchies()) {
				// Check if the hier is load by default and if it is in row or
				// in column
				boolean isLoad = false;
				boolean isCol = false;
				for (String col : cols) {
					if (col.contains(hier.getUniqueName())) {
						isLoad = true;
						isCol = true;
						break;
					}
				}
				if (isLoad == false)
					for (String row : rows) {
						if (row.contains(hier.getUniqueName())) {
							isLoad = true;
							break;
						}
					}

				HierarchyAndCol androidHier = new HierarchyAndCol(hier.getUniqueName(), hier.getName(), isCol, isLoad, androidDim);
				androidDim.addHierarchyAndCol(androidHier);
			}

			cube.addDimension(androidDim);
		}

		for (MeasureGroup meaGroup : measAvailable) {

			AndroidMeasureGroup androidMeaGroup = new AndroidMeasureGroup(meaGroup.getUniqueName(), meaGroup.getName());
			for (Measure mea : meaGroup.getMeasures()) {
				boolean isLoad = false;
				boolean isCol = false;
				for (String col : cols) {
					if (col.contains(mea.getUniqueName())) {
						isLoad = true;
						isCol = true;
						break;
					}
				}
				if (isLoad == false)
					for (String row : rows) {
						if (row.contains(mea.getUniqueName())) {
							isLoad = true;
							break;
						}
					}

				MeasureAndCol androidMea = new MeasureAndCol(mea.getUniqueName(), mea.getName(), isCol, isLoad, androidMeaGroup);
				androidMeaGroup.addMeasuresAndCol(androidMea);
			}

			cube.addMeasure(androidMeaGroup);
		}
	}

	@Override
	public String hideNull() throws Exception {
		CubeInfos cubeInfos = session.getCubeInfos();

		return getXml(cubeInfos, "", TypeAction.HIDE);
	}

	@Override
	public String drillCube(String drillUniqueName) throws Exception {
		CubeInfos cubeInfos = session.getCubeInfos();

		return getXml(cubeInfos, drillUniqueName, TypeAction.DRILL);
	}

	@Override
	public IRunItem getDashboardUrl(AndroidItem item) throws Exception {
		IRepositoryContext ctx = session.getRepositoryContext();

		RemoteFdRuntime fd = new RemoteFdRuntime(ctx.getVanillaContext().getVanillaUrl(), ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword());

		ObjectIdentifier identifier = new ObjectIdentifier(ctx.getRepository().getId(), item.getId());
		IRuntimeConfig config = new RuntimeConfiguration(ctx.getGroup().getId(), identifier, new ArrayList<VanillaGroupParameter>());

		String previewType = "text/" + "html";
		return new DashboardRunItem(item.getId(), previewType, fd.deployDashboard(config));
	}

	private Hierarchy findHierarchy(OLAPCube faCube, String uniqueName, String dimensionName) {
		List<Dimension> dims = getDimensions(faCube);

		for (Dimension dim : dims) {
			if (dim.getUniqueName().equals(dimensionName)) {
				for (Hierarchy hier : dim.getHierarchies()) {
					if (hier.getUniqueName().equals(uniqueName)) {
						return hier;
					}
				}
			}
		}
		return null;
	}

	private Measure findMeasure(OLAPCube faCube, String uniqueName, String dimensionName) {
		List<MeasureGroup> measGroup = getMeasures(faCube);

		for (MeasureGroup mgroup : measGroup) {
			if (mgroup.getUniqueName().equals(dimensionName)) {
				for (Measure mea : mgroup.getMeasures()) {
					if (mea.getUniqueName().equals(uniqueName)) {
						return mea;
					}
				}
			}
		}
		return null;
	}

	private OLAPCube loadCubeSelected(int selectedItemId, String selectedCubeName) throws Exception {
		IRepositoryContext repositoryContext = session.getRepositoryContext();

		String vanUrl = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL);
		FaApiHelper fa = new FaApiHelper(vanUrl, new UnitedOlapLoader());

		IObjectIdentifier id = new ObjectIdentifier(repositoryContext.getRepository().getId(), selectedItemId);
		IRuntimeContext ctx = new RuntimeContext(repositoryContext.getVanillaContext().getLogin(), repositoryContext.getVanillaContext().getPassword(), repositoryContext.getGroup().getName(), repositoryContext.getGroup().getId());
		fa.getCubeNames(id, ctx);
		return fa.getCube(id, ctx, selectedCubeName);
	}

	private List<String> getCols(OLAPCube faCube) {
		List<String> cols = new ArrayList<String>();
		for (String col : faCube.getMdx().getCols()) {
			cols.add(col);
		}
		return cols;
	}

	private List<String> getRows(OLAPCube faCube) {
		List<String> rows = new ArrayList<String>();
		for (String row : faCube.getMdx().getRows()) {
			rows.add(row);
		}
		return rows;
	}

	// We load the dimensions of the cube
	private List<Dimension> getDimensions(OLAPCube faCube) {
		List<Dimension> dims = new ArrayList<Dimension>();
		for (Dimension d : faCube.getDimensions()) {
			dims.add(d);
		}

		return dims;
	}

	// We load the Measures of the cube
	private List<MeasureGroup> getMeasures(OLAPCube faCube) {
		List<MeasureGroup> measGroup = new ArrayList<MeasureGroup>();
		for (MeasureGroup g : faCube.getMeasures()) {
			measGroup.add(g);
		}
		return measGroup;
	}

	private HashMap<RepositoryItem, byte[]> getCubeViews(String selectedCubeName, RepositoryItem repositoryItem) {
		HashMap<RepositoryItem, byte[]> lstCubeViews = new HashMap<RepositoryItem, byte[]>();
		try {
			lstCubeViews = sock.getRepositoryService().getCubeViewsWithImageBytes(selectedCubeName, repositoryItem);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lstCubeViews;
	}

	private OLAPCube loadCubeView(CubeInfos cubeInfos, AndroidCubeView androidView) throws Exception {
		RepositoryItem selectedView = sock.getRepositoryService().getDirectoryItem(androidView.getId());
		String fav = sock.getRepositoryService().loadModel(selectedView);

		Document document = DocumentHelper.parseText(fav);
		Element root = document.getRootElement();

		String view = root.element("view").asXML();

		DigesterCubeView dig = new DigesterCubeView(view);
		RepositoryCubeView repcubeview = dig.getCubeView();

		cubeInfos.getFaCube().setView(repcubeview);
		return cubeInfos.getFaCube();
	}

	private String getXml(CubeInfos cubeInfos, String uniqueName, TypeAction action) throws Exception {
		OLAPCube faCube = cubeInfos.getFaCube();

		OLAPResult res = cubeInfos.getResult();

		switch (action) {
		case FIRST_TIME:
			res = faCube.doQuery();
			break;
		case DRILL:
			if (!uniqueName.equals("")) {
				ArrayList<ArrayList<Item>> rawContentDrillDown = res.getRaw();
				for (ArrayList<Item> rowDrillDown : rawContentDrillDown) {
					for (Item i : rowDrillDown) {
						if (i instanceof ItemElement) {
							if (((ItemElement) i).getDataMember().getUniqueName().equals(uniqueName)) {
								if (((ItemElement) i).isDrilled()) {
									faCube.drillup((ItemElement) i);
								}
								else {
									faCube.drilldown((ItemElement) i);
								}
							}
						}
					}
				}

				res = faCube.doQuery();
			}
			break;
		case HIDE:
			if (faCube.getShowEmpty()) {
				faCube.setshowEmpty(false);
			}
			else {
				faCube.setshowEmpty(true);
			}
			res = faCube.doQuery();
			break;
		case INIT:
			faCube.restore();
			res = faCube.doQuery();
			break;
		case SWAP:
			faCube.swapAxes();
			res = faCube.doQuery();
			break;
		case CHANGE_DIM_OR_MEA:
			res = faCube.doQuery();
			break;
		default:
			break;
		}

		if (res != null) {
			cubeInfos.setResult(res);

			ArrayList<ArrayList<Item>> rawContent = res.getRaw();

			StringBuffer html = new StringBuffer();
			html.append("<html>\n");
			html.append("    <head>\n");
			html.append("        <title>Cube Explorer</title>\n");
			html.append("        <style type=\"text/css\">");
			html.append("            th{");
			html.append("              width:130;");
			html.append("            }");
			html.append("        </style>");
			html.append("    </head>\n");
			html.append("    <body>\n");
			html.append("        <TABLE BORDER=\"1\" STYLE=\"border-collapse: collapse;\">\n");
			html.append("            <CAPTION>" + cubeInfos.getSelectedCubeName() + "</CAPTION>\n");
			for (ArrayList<Item> row : rawContent) {
				html.append("             <TR>\n");
				for (Item i : row) {
					if (i instanceof ItemElement || i instanceof ItemNull) {
						if (i instanceof ItemElement) {
							if (!((ItemElement) i).getDataMember().getUniqueName().contains("[Measures]")) {
								if (!cubeInfos.isIphone()) {
									html.append("             <TH BGCOLOR=\"#c3daf9\" id=\"" + ((ItemElement) i).getDataMember().getUniqueName() + "\" onclick=\"window.drill.clickOnCell(this.id)\">" + i.getLabel() + "</TH>\n");
								}
								else {
									html.append("             <TH BGCOLOR=\"#c3daf9\" id=\"" + ((ItemElement) i).getDataMember().getUniqueName() + "\" onclick='window.location = \"call?cmd=getLocation&param=" + ((ItemElement) i).getDataMember().getUniqueName() + "\"'>" + i.getLabel() + "</TH>\n");
								}
							}
							else {
								html.append("             <TH BGCOLOR=\"#c3daf9\">" + i.getLabel() + "</TH>\n");
							}
						}
						else {
							html.append("             <TH BGCOLOR=\"#c3daf9\">" + i.getLabel() + "</TH>\n");
						}
					}
					else {
						html.append("         <TD ALIGN=\"center\" >" + i.getLabel() + "</TD>\n");
					}
				}
				html.append("         </TR>\n");
			}
			html.append("        </TABLE>\n");
			html.append("    </body>\n");
			html.append("</html>\n");

			return html.toString();
		}
		return null;
	}

	private void getChilds(List<RepositoryItem> items, List<RepositoryDirectory> dirs, IRepository repositoryContent) {
		try {
			for (RepositoryDirectory dir : dirs) {

				List<RepositoryDirectory> childDirs = repositoryContent.getChildDirectories(dir);
				if (childDirs != null && !childDirs.isEmpty()) {
					getChilds(items, childDirs, repositoryContent);
				}
				for (RepositoryItem item : repositoryContent.getItems(dir)) {
					if (item.isAndroidSupported()) {
						items.add(item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> getCubeNames(RepositoryItem selectedItem) {
		List<String> cubeNames = new ArrayList<String>();
		String xml = "";

		try {
			xml = sock.getRepositoryService().loadModel(selectedItem);
		} catch (Exception e) {
			e.printStackTrace();
		}

		DigesterFasd dig = null;
		try {
			dig = new DigesterFasd(IOUtils.toInputStream(xml));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		FAModel faModel = dig.getFAModel();
		List<ICube> cubes = faModel.getCubes();
		for (ICube cube : cubes) {
			cubeNames.add(cube.getName());
		}
		return cubeNames;
	}

	@Override
	public List<Parameter> getParameterValues(AndroidItem item, Parameter selectedParameter) throws Exception {
		if (selectedParameter.hasParent()) {
			List<Parameter> params = new ArrayList<Parameter>();
			params.add(getDependantParameter(item, selectedParameter.getParamParent(), selectedParameter));
			return params;
		}
		else {
			return getWrapsParameters(item.getId(), item.getType());
		}
	}

	private List<Parameter> getWrapsParameters(int selectedItemId, int itemType) throws Exception {
		IRepositoryContext repositoryContext = session.getRepositoryContext();

		RemoteVanillaParameterComponent remoteParams = new RemoteVanillaParameterComponent(repositoryContext.getVanillaContext());

		IObjectIdentifier id = new ObjectIdentifier(repositoryContext.getRepository().getId(), selectedItemId);

		IRuntimeConfig config = null;
		if (itemType == IRepositoryApi.FWR_TYPE || itemType == IRepositoryApi.CUST_TYPE) {
			config = new ReportRuntimeConfig(id, new ArrayList<VanillaGroupParameter>(), repositoryContext.getGroup().getId());
		}
		else {
			config = new RuntimeConfiguration(repositoryContext.getGroup().getId(), id, new ArrayList<VanillaGroupParameter>());
		}
		List<VanillaGroupParameter> params = remoteParams.getParameters(config);

		List<Parameter> androidParams = new ArrayList<Parameter>();
		for (VanillaGroupParameter grpParam : params) {

			Parameter parent = null;
			for (VanillaParameter vanParam : grpParam.getParameters()) {
				Parameter par = new Parameter();
				par.setParamName(vanParam.getName());
				if (vanParam.getValues() != null && !vanParam.getValues().isEmpty()) {
					par.setValues(vanParam.getValues());
				}

				if (parent != null) {
					parent.setParamChild(par);
					par.setParamParent(parent);
				}

				parent = par;

				androidParams.add(par);
			}
		}

		return androidParams;
	}

	private Parameter getDependantParameter(AndroidItem selectedItem, Parameter paramParent, Parameter selectedParam) throws Exception {
		IRepositoryContext repositoryContext = session.getRepositoryContext();

		RemoteVanillaParameterComponent remoteParams = new RemoteVanillaParameterComponent(repositoryContext.getVanillaContext());

		IObjectIdentifier id = new ObjectIdentifier(repositoryContext.getRepository().getId(), selectedItem.getId());

		IRuntimeConfig config = null;
		if (selectedItem.getType() == IRepositoryApi.FWR_TYPE || selectedItem.getType() == IRepositoryApi.CUST_TYPE) {
			config = new ReportRuntimeConfig(id, new ArrayList<VanillaGroupParameter>(), repositoryContext.getGroup().getId());
		}
		else {
			config = new RuntimeConfiguration(repositoryContext.getGroup().getId(), id, new ArrayList<VanillaGroupParameter>());
		}
		List<VanillaGroupParameter> params = remoteParams.getParameters(config);

		for (VanillaGroupParameter grpParam : params) {

			if (grpParam != null) {
				for (int i = 0; i < grpParam.getParameters().size(); i++) {
					if (grpParam.getParameters().get(i).getName().equals(paramParent.getParamName())) {
						grpParam.getParameters().get(i).addSelectedValue(paramParent.getSelectedValue());
					}
					else if (grpParam.getParameters().get(i).getName().equals(selectedParam.getParamName())) {
						selectedParam.setValues(executeRequest(repositoryContext, grpParam.getParameters().get(i), selectedItem.getId(), params));
						return selectedParam;
					}
				}
			}
		}

		return null;
	}

	private LinkedHashMap<String, String> executeRequest(IRepositoryContext repositoryContext, VanillaParameter p, int itemId, List<VanillaGroupParameter> params) throws Exception {

		RemoteVanillaParameterComponent paramRemote = new RemoteVanillaParameterComponent(repositoryContext.getVanillaContext());

		IObjectIdentifier id = new ObjectIdentifier(repositoryContext.getRepository().getId(), itemId);
		ReportRuntimeConfig config = new ReportRuntimeConfig(id, params, repositoryContext.getGroup().getId());
		VanillaParameter paramVals = paramRemote.getReportParameterValues(config, p.getName());

		return paramVals.getValues();
	}

	@Override
	public ReportRunItem getItemLastView(AndroidItem item) throws Exception {
		IRepositoryContext repositoryContext = session.getRepositoryContext();

		IObjectIdentifier obj = new ObjectIdentifier(repositoryContext.getRepository().getId(), item.getId());

		ReportHistoricComponent historic = new RemoteHistoricReportComponent(repositoryContext.getVanillaContext());
		List<GedDocument> histos = historic.getReportHistoric(obj, repositoryContext.getGroup().getId());

		DocumentVersion latest = null;
		if (histos != null && !histos.isEmpty()) {
			for (GedDocument histo : histos) {
				if (histo.getDocumentVersions() != null) {
					for (DocumentVersion doc : histo.getDocumentVersions()) {
						if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date())) && latest == null) {
							latest = doc;
						}
						else if ((doc.getPeremptionDate() == null || doc.getPeremptionDate().after(new Date())) && latest.getModificationDate().before(doc.getModificationDate())) {
							latest = doc;
						}
					}
				}
			}
		}

		String previewType = "text/" + "html";

		if (latest != null) {
			InputStream loadedDoc = historic.loadHistorizedDocument(latest.getId());
			String html = getPreviewStreamAsString(loadedDoc);
			loadedDoc.close();
			return new ReportRunItem(-1, previewType, html);
		}

		return new ReportRunItem(-1, previewType, "<html><head></head><body><h1>There is no historic for this report</h1></body></html>");
	}

	@Override
	public List<AndroidDocumentDefinition> searchInGed(String search) throws Exception {
		IRepositoryContext repositoryContext = session.getRepositoryContext();

		String[] wordSplit = search.split(" ");
		List<String> words = new ArrayList<String>();
		for (String str : wordSplit) {
			words.add(str);
		}

		RemoteGedComponent ged = new RemoteGedComponent(repositoryContext.getVanillaContext());
		GedSearchRuntimeConfig config = new GedSearchRuntimeConfig();
		config.addGroupId(repositoryContext.getGroup().getId());
		config.setKeywords(words);
		config.setAllOccurences(false);
		config.setProperties(new ComProperties());

		List<AndroidDocumentDefinition> androidDocs = new ArrayList<AndroidDocumentDefinition>();

		List<GedDocument> docs = ged.search(config);
		if (docs != null) {
			for (GedDocument d : docs) {
				DocumentVersion docVersion = d.getLastVersion();
				if (docVersion != null && docVersion.getFormat().equals("html")) {
					androidDocs.add(new AndroidDocumentDefinition(d.getId(), d.getName(), docVersion.getVersion() + "", docVersion.getSummary()));
				}
			}
		}
		return androidDocs;
	}

	@Override
	public String loadItemInGed(AndroidDocumentDefinition definition) throws Exception {
		IRepositoryContext repositoryContext = session.getRepositoryContext();

		GedDocument doc = new GedDocument();
		doc.setId(definition.getId());

		RemoteGedComponent ged = new RemoteGedComponent(repositoryContext.getVanillaContext());

		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(repositoryContext.getVanillaContext().getLogin());

		GedLoadRuntimeConfig config = new GedLoadRuntimeConfig(doc, user.getId());
		InputStream is = ged.loadGedDocument(config);

		String req = IOUtils.toString(is, "UTF-8");
		return req;
	}

	@Override
	public ReportRunItem runItem(AndroidItem selectedItem, String outputFormat) throws Exception {
		IRepositoryContext repositoryContext = session.getRepositoryContext();

		RemoteReportRuntime remote = new RemoteReportRuntime(repositoryContext.getVanillaContext());

		ReportRuntimeConfig runtimeConfig = new ReportRuntimeConfig();
		runtimeConfig.setObjectIdentifier(new ObjectIdentifier(repositoryContext.getRepository().getId(), selectedItem.getId()));
		runtimeConfig.setOutputFormat(outputFormat);
		runtimeConfig.setVanillaGroupId(repositoryContext.getGroup().getId());

		List<VanillaGroupParameter> parameters = new ArrayList<VanillaGroupParameter>();
		for (Parameter para : selectedItem.getParameters()) {

			VanillaGroupParameter grpP = new VanillaGroupParameter();
			VanillaParameter ppp = new VanillaParameter(para.getParamName(), para.getSelectedValue());
			grpP.addParameter(ppp);

			parameters.add(grpP);
		}

		runtimeConfig.setParameters(parameters);

		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(repositoryContext.getVanillaContext().getLogin());
		InputStream previewStream = remote.runReport(runtimeConfig, user);

		String html = getPreviewStreamAsString(previewStream);
		String previewType = "text/" + outputFormat;

		previewStream.close();

		return new ReportRunItem(selectedItem.getId(), previewType, html);
	}

	public String getPreviewStreamAsString(InputStream previewStream) throws IOException {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(previewStream, "UTF-8"));

		StringBuffer stringBuffer = new StringBuffer();
		String ligneCodeHTML = bufferedReader.readLine();
		while (ligneCodeHTML != null) {
			stringBuffer.append(ligneCodeHTML);
			stringBuffer.append("\n");
			ligneCodeHTML = bufferedReader.readLine();
		}

		String html = stringBuffer.toString();
		int start = html.indexOf("<html>");
		String xml = html.substring(start);

		return xml;
	}

	public InputStream generateDiscoPackage(int directoryItemId) throws Exception {
		ReportingComponent reportingComponent = new RemoteReportRuntime(sock.getContext().getVanillaContext());
		FaApiHelper fa = new FaApiHelper(sock.getContext().getVanillaContext().getVanillaUrl(), new UnitedOlapLoader());
		
		User user = vanillaApi.getVanillaSecurityManager().getUserByLogin(sock.getContext().getVanillaContext().getLogin());

		bpm.vanilla.platform.core.beans.Repository rep = vanillaApi.getVanillaRepositoryManager().getRepositoryById(sock.getContext().getRepository().getId());

		VanillaConfiguration vconf = ConfigurationManager.getInstance().getVanillaConfiguration();

		Group group = new Group();
		group.setId(sock.getContext().getGroup().getId());

		IRepositoryApi repositoryApi = new RemoteRepositoryApi(new BaseRepositoryContext(new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getVanillaServerUrl(), vconf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN), vconf.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD)), group, rep));

		RepositoryItem item = repositoryApi.getRepositoryService().getDirectoryItem(directoryItemId);
		String modelXml = repositoryApi.getRepositoryService().loadModel(item);

		DiscoPackage discoPackage = null;
		try {
			discoPackage = (DiscoPackage) new XStream().fromXML(modelXml);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to deserialize the disco package.", e);
		}

		if (discoPackage != null) {

			ByteArrayOutputStream dest = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

			for (DiscoReportConfiguration config : discoPackage.getConfigs()) {

				RepositoryItem itemToRun = config.getItem();

				if (itemToRun.getType() == IRepositoryApi.CUST_TYPE || itemToRun.getType() == IRepositoryApi.FWR_TYPE) {

					for (String format : config.getSelectedFormats()) {

						IObjectIdentifier identifier = new ObjectIdentifier(rep.getId(), config.getItem().getId());

						ReportRuntimeConfig runtimeConfig = new ReportRuntimeConfig(identifier, config.getSelectedParameters(), config.getGroup().getId());
						runtimeConfig.setOutputFormat(format);

						try {
							InputStream is = reportingComponent.runReport(runtimeConfig, user);

							byte data[] = new byte[2048];
							ZipEntry entry = new ZipEntry(config.getItem().getItemName() + "." + format.toLowerCase());
							out.putNextEntry(entry);
							int count;
							while ((count = is.read(data, 0, 2048)) != -1) {
								out.write(data, 0, count);
							}
							is.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else if (itemToRun.getType() == IRepositoryApi.FAV_TYPE) {
					String fav = sock.getRepositoryService().loadModel(itemToRun);

					Document document = DocumentHelper.parseText(fav);
					Element root = document.getRootElement();

					String view = root.element("view").asXML();
					
					int fasdId = Integer.parseInt(view.substring(view.indexOf("<fasdid>") + 10, view.indexOf("</fasdid>")));
					String cubeName = view.substring(view.indexOf("<cubename>") + 10, view.indexOf("</cubename>"));

					DigesterCubeView dig = new DigesterCubeView(view);
					RepositoryCubeView repcubeview =  dig.getCubeView();
					
					IObjectIdentifier id = new ObjectIdentifier(sock.getContext().getRepository().getId(), fasdId);
					IRuntimeContext ctx = new RuntimeContext(user.getLogin(), user.getPassword(), config.getGroup().getName(), config.getGroup().getId());
					
					OLAPCube faCube = fa.getCube(id, ctx, cubeName);
					faCube.setView(repcubeview);
					
					String cubeHtml = loadCubeView(cubeName, faCube.doQuery());

					try {
						InputStream is = IOUtils.toInputStream(cubeHtml, "UTF-8");
						
						byte data[] = new byte[2048];
						ZipEntry entry = new ZipEntry(config.getItem().getItemName() + ".html");
						out.putNextEntry(entry);
						int count;
						while ((count = is.read(data, 0, 2048)) != -1) {
							out.write(data, 0, count);
						}
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					throw new Exception("This item is not supported for this package.");
				}
			}
			out.flush();
			out.close();
			return new ByteArrayInputStream(dest.toByteArray());
		}
		return null;
	}
	
	private String loadCubeView(String cubeName, OLAPResult res) {
		ArrayList<ArrayList<Item>> rawContent = res.getRaw();
		
		StringBuffer html = new StringBuffer();
		html.append("<html>\n");
		html.append("    <head>\n");
		html.append("        <title>Cube Explorer</title>\n");
		html.append("        <style type=\"text/css\">");
		html.append("            th{");
		html.append("              width:130;");
		html.append("            }");
		html.append("        </style>");
		html.append("    </head>\n");
		html.append("    <body>\n");
		html.append("        <TABLE BORDER=\"1\" STYLE=\"border-collapse: collapse;\">\n");
		html.append("            <CAPTION>" + cubeName + "</CAPTION>\n");
		for(ArrayList<Item> row : rawContent){
			html.append("             <TR>\n");
			for(Item i : row){
				if(i instanceof ItemElement || i instanceof ItemNull){
					if(i instanceof ItemElement){
						if(!((ItemElement) i).getDataMember().getUniqueName().contains("[Measures]")){
							html.append("             <TH BGCOLOR=\"#c3daf9\" id=\"" + ((ItemElement)i).getDataMember().getUniqueName() + "\">" + i.getLabel() + "</TH>\n");
						}
						else {
							html.append("             <TH BGCOLOR=\"#c3daf9\">" + i.getLabel() + "</TH>\n");
						}
					}
					else{
						html.append("             <TH BGCOLOR=\"#c3daf9\">" + i.getLabel() + "</TH>\n");
					}
				}
				else{
					html.append("         <TD ALIGN=\"center\" >" + i.getLabel() + "</TD>\n");
				}
			}
			html.append("         </TR>\n");
		}
		html.append("        </TABLE>\n");
		html.append("    </body>\n");
		html.append("</html>\n");
		return html.toString();
	}
}
