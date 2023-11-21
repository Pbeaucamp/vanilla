package bpm.fd.web.server;

import java.util.ArrayList;
import java.util.List;

import bpm.data.viz.core.RemoteDataVizComponent;
import bpm.data.viz.core.preparation.DataPreparation;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Element;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fd.core.Dashboard;
import bpm.fd.core.component.CubeElement;
import bpm.fd.core.component.CubeElement.CubeElementType;
import bpm.fd.web.client.services.DashboardService;
import bpm.fd.web.server.security.DashboardSession;
import bpm.fm.api.model.Theme;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSessionHelper;
import bpm.gwt.commons.server.security.CommonSessionManager;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.remote.core.design.fusionmap.impl.RemoteMapDefinitionService;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.beans.data.DatasourceType;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Template;
import bpm.vanilla.platform.core.repository.Template.TypeTemplate;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class DashboardServiceImpl extends RemoteServiceServlet implements DashboardService {

	private DashboardSession getSession() throws ServiceException {
		return CommonSessionHelper.getCurrentSession(getThreadLocalRequest(), DashboardSession.class);
	}

	@Override
	public void initSession() throws ServiceException {
		// We create an empty session
		try {
			String sessionId = CommonSessionManager.createSession(DashboardSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, getThreadLocalRequest());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to create a session.", e);
		}
	}

	@Override
	public Integer save(SaveItemInformations itemInfos, Dashboard dashboard, boolean update) throws ServiceException {
		DashboardSession session = getSession();
		try {
			return session.getDashboardManager().saveDashboard(itemInfos.getSelectedDirectory(), dashboard, update, itemInfos.getGroups());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save this dashboard : " + e.getMessage());
		}
	}

	@Override
	public String preview(Dashboard dashboard) throws ServiceException {
		DashboardSession session = getSession();
		try {
			return session.getDashboardManager().previewDashboard(dashboard);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to preview this dashboard : " + e.getMessage());
		}
	}

	@Override
	public Dashboard openDashboard(RepositoryItem item) throws ServiceException {
		DashboardSession session = getSession();

		try {
			return session.getDashboardManager().openDashboard(item.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to open the dashboard with id " + item.getId() + " : " + e.getMessage());
		}
	}

	@Override
	public String getDefaultCss() throws ServiceException {
		DashboardSession session = getSession();

		try {
			String css = session.getDashboardManager().getDefaultCssFile();
			return css;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the default CSS : " + e.getMessage());
		}
	}

	@Override
	public List<Datasource> getDatasources() throws ServiceException {
		DashboardSession session = getSession();
		try {
			List<DatasourceType> filterTypes = new ArrayList<DatasourceType>();
			filterTypes.add(DatasourceType.JDBC);
			filterTypes.add(DatasourceType.FMDT);
			return session.getVanillaApi().getVanillaPreferencesManager().getDatasources(filterTypes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve datasources : " + e.getMessage());
		}
	}
	
	@Override
	public List<Dataset> getDatasets(Datasource datasource) throws ServiceException {
		DashboardSession session = getSession();
		try {
			return session.getVanillaApi().getVanillaPreferencesManager().getDatasetByDatasource(datasource);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve datasources : " + e.getMessage());
		}
	}

	@Override
	public List<Parameter> getRepositoryItemParameters(int itemId) throws ServiceException {
		DashboardSession session = getSession();
		try {
			return session.getRepositoryConnection().getRepositoryService().getParameters(session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve parameters : " + e.getMessage());
		}
	}

	@Override
	public List<MapVanilla> getVanillaMaps() throws ServiceException {
		DashboardSession session = getSession();
		try {
			RemoteMapDefinitionService service = new RemoteMapDefinitionService();
			service.configure(session.getVanillaRuntimeUrl());

			return service.getAllMapsVanilla();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve maps : " + e.getMessage());
		}
	}

	@Override
	public List<CubeElement> getCubeDimensionAndMeasures(int itemId, List<CubeElement> currentElements) throws ServiceException {
		DashboardSession session = getSession();
		try {
			Group group = session.getCurrentGroup();

			RepositoryItem item = session.getRepositoryConnection().getRepositoryService().getDirectoryItem(itemId);
			String itemXML = session.getRepositoryConnection().getRepositoryService().loadModel(item);

			int startFasdId = itemXML.indexOf("<fasdid>") + 8;
			int endFasdId = itemXML.indexOf("</fasdid>");

			int startCubeName = itemXML.indexOf("<cubename>") + 10;
			int endCubeName = itemXML.indexOf("</cubename>");

			int fasdItemId = Integer.parseInt(itemXML.substring(startFasdId, endFasdId));
			String cubeName = itemXML.substring(startCubeName, endCubeName);

			IObjectIdentifier identifier = new ObjectIdentifier(session.getCurrentRepository().getId(), fasdItemId);
			IRuntimeContext ctx = new RuntimeContext(session.getUser().getLogin(), session.getUser().getPassword(), group.getName(), group.getId());

			OLAPCube cube = session.getFaApiHelper().getCube(identifier, ctx, cubeName);

			List<CubeElement> elements = new ArrayList<>();
			if (cube.getDimensions() != null) {
				for (Dimension dim : cube.getDimensions()) {
					boolean visible = findElement(dim, currentElements);
					elements.add(new CubeElement(CubeElementType.DIMENSION, dim.getName(), dim.getCaption(), dim.getUniqueName(), visible));
				}
			}

			if (cube.getMeasures() != null) {
				for (MeasureGroup measuregroup : cube.getMeasures()) {
					if (measuregroup.getMeasures() != null) {
						for (Measure mea : measuregroup.getMeasures()) {
							boolean visible = findElement(mea, currentElements);
							elements.add(new CubeElement(CubeElementType.MEASURE, mea.getName(), mea.getCaption(), mea.getUniqueName(), visible));
						}
					}
				}
			}
			return elements;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to retrieve maps : " + e.getMessage());
		}
	}

	private boolean findElement(Element element, List<CubeElement> currentElements) {
		if (currentElements == null || currentElements.isEmpty()) {
			return true;
		}

		for (CubeElement el : currentElements) {
			if (el.getUniqueName().equals(element.getUniqueName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Template<IDashboard>> getDashboardTemplates() throws ServiceException {
		DashboardSession session = getSession();
		try {
			return session.getRepositoryConnection().getRepositoryService().getTemplates(true, TypeTemplate.DASHBOARD);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get available templates : " + e.getMessage());
		}
	}

	@Override
	public Template<IDashboard> getDashboardTemplate(int templateId) throws ServiceException {
		DashboardSession session = getSession();
		try {
			return session.getRepositoryConnection().getRepositoryService().getTemplate(templateId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to load the selected template : " + e.getMessage());
		}
	}

	@Override
	public void addTemplate(Template<IDashboard> template) throws ServiceException {
		DashboardSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().addTemplate(template);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to save this template : " + e.getMessage());
		}
	}

	@Override
	public void deleteTemplate(Template<IDashboard> template) throws ServiceException {
		DashboardSession session = getSession();
		try {
			session.getRepositoryConnection().getRepositoryService().deleteTemplate(template);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to delete this template : " + e.getMessage());
		}
	}
	
	@Override
	public List<Theme> getMetricThemes() throws ServiceException {
		DashboardSession session = getSession();
		try {
			return session.getFmManager().getThemes();
		} catch (Exception e) {
			throw new ServiceException("Unable to retrieve maps : " + e.getMessage(), e);
		}
	}

	@Override
	public List<DataPreparation> getDataPreparations() throws ServiceException {
		DashboardSession session = getSession();
		RemoteDataVizComponent dataviz = new RemoteDataVizComponent(session.getRepositoryConnection());
		try {
			return dataviz.getDataPreparations();
		} catch(Exception e) {
			throw new ServiceException("Problem while retriving datapreparations", e);
		}
	}
}
