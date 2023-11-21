package bpm.faweb.server.tools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.fasd.olap.DrillCube;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.query.WhereClauseException;
import bpm.fa.api.olap.unitedolap.UnitedOlapServiceProvider;
import bpm.fa.api.repository.RepositoryCubeView;
import bpm.fa.api.utils.parse.DigesterCubeView;
import bpm.faweb.server.beans.InfosOlap;
import bpm.faweb.server.security.FaWebSession;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.ParameterDTO;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.ChartInfos;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.remote.services.RemoteServiceProvider;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.XStream;

public class FaWebActions {

	public static List<String> loadModel(int keySession, Group group, int modelId, FaWebSession session, String location, InfosOlap infos) throws IOException {
		List<String> cubeNames = new ArrayList<String>();

		try {
			Collection<String> cubes = writeRepository(keySession, group, modelId, session, location, infos);
			if(cubes != null) {
				for(String cubeName : cubes) {
					cubeNames.add(cubeName);
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		return cubeNames;
	}

	public static Collection<String> writeRepository(int keySession, Group group, int modelId, FaWebSession session, String location, InfosOlap infos) {
		location += File.separator + "resources";

		Collection<String> cubes = null;
		try {
			IVanillaContext vctx = new BaseVanillaContext(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());

			RemoteServiceProvider remote = new RemoteServiceProvider();
			remote.configure(vctx);

			UnitedOlapServiceProvider.getInstance().init(remote.getRuntimeProvider(), remote.getModelProvider());

			IRepository irep = session.getRepository();
			RepositoryItem item = irep.getItem(modelId);

			infos.setSelectedFasd(item);

			IRuntimeContext ctx = new RuntimeContext(session.getUser().getLogin(), session.getUser().getPassword(), group.getName(), group.getId());
			IObjectIdentifier identifier = new ObjectIdentifier(session.getCurrentRepository().getId(), modelId);
			cubes = session.getFaApiHelper(keySession).getCubeNames(identifier, ctx);

		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}

		return cubes;
	}

	public static List<Calcul> writeView(String viewKey, FaWebSession session, InfosOlap infos, HashMap<String, String> parameters) {
		infos.getFiltersByUname().clear();
		infos.getInfosReport().getWheres().clear();
		String view = null;

		IRepositoryApi sock = session.getRepositoryConnection();

		try {
			List<RepositoryItem> views = sock.getRepositoryService().getCubeViews(infos.getCubeName(), infos.getSelectedFasd());
			RepositoryItem icube = null;

			for(RepositoryItem ic : views) {
				if(ic.getItemName().equalsIgnoreCase(viewKey)) {
					icube = ic;
					break;
				}
			}

			String fav = sock.getRepositoryService().loadModel(icube);

			Document document = DocumentHelper.parseText(fav);
			Element root = document.getRootElement();

			view = root.element("view").asXML();

			DigesterCubeView dig = new DigesterCubeView(view);
			RepositoryCubeView repcubeview = dig.getCubeView();

			for(Parameter param : repcubeview.getParameters()) {
				param.setValue(parameters.get(param.getName()));
			}

			infos.setViewId(icube.getId());

			OLAPCube cube = infos.getCube();
			cube.setView(repcubeview);
			boolean projectionAllowed = RecupInfosCube.isProjectionAllowed(session, infos);

			infos.setProjectionAllowed(projectionAllowed);
			infos.setCube(cube);
			infos.setRes(cube.doQuery());
			infos.getInfosReport().setReportTitle(repcubeview.getReportTitle());
			infos.getInfosReport().getParameters().clear();
			for(Parameter param : repcubeview.getParameters()) {
				ParameterDTO paramDTO = new ParameterDTO();
				paramDTO.setName(param.getName());
				paramDTO.setLevel(param.getLevel());
				paramDTO.setValue(param.getUname());
				infos.getInfosReport().getParameters().add(paramDTO);
				infos.getFiltersByUname().put(param.getUname(), new ItemElement(param.getUname(), false));
			}

			for(String where : repcubeview.getWheres()) {
				infos.getInfosReport().getWheres().add(where);
				infos.getFiltersByUname().put(where, new ItemElement(where, false));
			}

			CalculsDigester digcalc = new CalculsDigester(IOUtils.toInputStream(view, "UTF-8"));
			List<Calcul> c = digcalc.getCalculs();

			if(c.size() > 0)
				return c;

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;

	}

	public static ChartInfos writeChart(String viewKey, FaWebSession session, InfosOlap infos) {
		String view = null;

		IRepositoryApi sock = session.getRepositoryConnection();

		try {
			List<RepositoryItem> views = sock.getRepositoryService().getCubeViews(infos.getCubeName(), infos.getSelectedFasd());
			RepositoryItem icube = null;

			for(RepositoryItem ic : views) {
				if(ic.getItemName().equalsIgnoreCase(viewKey)) {
					icube = ic;
					break;
				}
			}

			String fav = sock.getRepositoryService().loadModel(icube);

			Document document = DocumentHelper.parseText(fav);
			Element root = document.getRootElement();

			view = root.element("view").asXML();

			ChartDigester chartDig = new ChartDigester(IOUtils.toInputStream(view, "UTF-8"));

			ChartInfos chartInfos = chartDig.getChartInfos();

			infos.setChartInfos(chartInfos);

			return chartInfos;

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static MapOptions writeMap(String viewKey, FaWebSession session, InfosOlap infos) {
		String view = null;

		IRepositoryApi sock = session.getRepositoryConnection();

		try {
			List<RepositoryItem> views = sock.getRepositoryService().getCubeViews(infos.getCubeName(), infos.getSelectedFasd());
			RepositoryItem icube = null;

			for(RepositoryItem ic : views) {
				if(ic.getItemName().equalsIgnoreCase(viewKey)) {
					icube = ic;
					break;
				}
			}

			String fav = sock.getRepositoryService().loadModel(icube);

			Document document = DocumentHelper.parseText(fav);
			Element root = document.getRootElement();

			view = root.element("view").element("mapInfo") != null ? root.element("view").element("mapInfo").element("bpm.faweb.shared.MapOptions").asXML() : "";
			if (!view.isEmpty()) {
				return (MapOptions) new XStream().fromXML(view);
			}
			return null;

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static void setCube(int keySession, String cubeName, FaWebSession session, InfosOlap infos, String dimName, String memberName) throws Exception {

		if(infos != null && infos.getCube() != null) {
			try {
				infos.getCube().close();
			} catch(Exception e) {
//				e.printStackTrace();
			}
		}

		IVanillaContext vctx = new BaseVanillaContext(session.getVanillaRuntimeUrl(), session.getUser().getLogin(), session.getUser().getPassword());

		RemoteServiceProvider remote = new RemoteServiceProvider();
		remote.configure(vctx);

		UnitedOlapServiceProvider.getInstance().init(remote.getRuntimeProvider(), remote.getModelProvider());

		IRuntimeContext ctx = new RuntimeContext(session.getUser().getLogin(), session.getUser().getPassword(), session.getCurrentGroup().getName(), session.getCurrentGroup().getId());

		IObjectIdentifier identifier = new ObjectIdentifier(session.getCurrentRepository().getId(), session.getInfosOlap(keySession).getSelectedFasd().getId());

		OLAPCube temp_cube = session.getFaApiHelper(keySession).getCube(identifier, ctx, cubeName);

		try {
			Collection<String> names = session.getFaApiHelper(keySession).getCubeNames(identifier, ctx);
			for(String n : names) {
				if(!n.equals(cubeName)) {
					DrillCube d = new DrillCube(identifier.getDirectoryItemId(), n);
					d.setDrillName(n);
					temp_cube.getDrills().add(d);
				}
			}
		} catch(Exception e1) {
			e1.printStackTrace();
		}

		boolean projectionAllowed = RecupInfosCube.isProjectionAllowed(session, infos);

		infos.setProjectionAllowed(projectionAllowed);
		infos.setCube(temp_cube);
		infos.setCubeName(cubeName);

		if(memberName != null) {
			String[] filters = memberName.split("!separator!");
			HashMap<String, ItemElement> resFilters = new HashMap<String, ItemElement>();
			for(String f : filters) {
				try {

					boolean dimExists = false;
					for(Dimension d : temp_cube.getDimensions()) {
						if(f.startsWith(d.getHierarchies().iterator().next().getUniqueName())) {
							dimExists = true;
							break;
						}
					}
					if(dimExists) {
						temp_cube.getMdx().addWhere(f);
						resFilters.put(f, new ItemElement(f, false));
					}
				} catch(WhereClauseException e) {

				}
			}
			infos.setRes(temp_cube.doQuery());
			infos.setFiltersByUname(resFilters);
			List<String> wheres = new ArrayList<String>(resFilters.keySet());
			infos.getInfosReport().setWheres(wheres);
		}
		else {
			infos.setRes(temp_cube.doQuery());
		}

	}

	public static ItemElement filterItem(String uname, FaWebSession session, InfosOlap infos) throws Exception {
		boolean isCol = false;
		ArrayList<ArrayList<Item>> tp = infos.getCube().getLastResult().getRaw();
		for(ArrayList<Item> col : tp) {
			for(Item item : col) {
				if(item instanceof ItemElement) {
					ItemElement ite = (ItemElement) item;
					if(ite.getDataMember().getUniqueName().equals(uname)) {
						isCol = ite.isCol();
						break;
					}
				}
			}
		}

		ItemElement ite = new ItemElement(uname, isCol);

		infos.getCube().addWhere(ite);
		infos.setRes(infos.getCube().doQuery());

		return ite;
	}

	public static HashMap<Integer, Integer> getCustomSizesFromView(String viewKey, FaWebSession session, InfosOlap infos) {
		String view = null;

		IRepositoryApi sock = session.getRepositoryConnection();

		try {
			List<RepositoryItem> views = sock.getRepositoryService().getCubeViews(infos.getCubeName(), infos.getSelectedFasd());
			RepositoryItem icube = null;

			for(RepositoryItem ic : views) {
				if(ic.getItemName().equalsIgnoreCase(viewKey)) {
					icube = ic;
					break;
				}
			}

			String fav = sock.getRepositoryService().loadModel(icube);

			Document document = DocumentHelper.parseText(fav);
			Element root = document.getRootElement();

			view = root.element("view").asXML();

			CustomSizesDigester chartDig = new CustomSizesDigester(IOUtils.toInputStream(view, "UTF-8"));

			return chartDig.getSizes();

		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
