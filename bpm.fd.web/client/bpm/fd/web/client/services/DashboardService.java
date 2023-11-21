package bpm.fd.web.client.services;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.fd.core.Dashboard;
import bpm.fd.core.component.CubeElement;
import bpm.fm.api.model.Theme;
import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Template;

@RemoteServiceRelativePath("dashboardService")
public interface DashboardService extends RemoteService {
	
	public static class Connect{
		private static DashboardServiceAsync instance;
		
		public static DashboardServiceAsync getInstance(){
			if(instance == null){
				instance = (DashboardServiceAsync) GWT.create(DashboardService.class);
			}
			return instance;
		}
	}
	
	public void initSession() throws ServiceException;
	
	public Integer save(SaveItemInformations itemInfos, Dashboard dashboard, boolean update) throws ServiceException;
	
	public String preview(Dashboard dashboard) throws ServiceException;
	
	public Dashboard openDashboard(RepositoryItem item) throws ServiceException;
	
	public String getDefaultCss() throws ServiceException;
	
	public List<Datasource> getDatasources() throws ServiceException;

	public List<Dataset> getDatasets(Datasource datasource) throws ServiceException;
	
	public List<Parameter> getRepositoryItemParameters(int itemId) throws ServiceException;
	
	public List<MapVanilla> getVanillaMaps() throws ServiceException;
	
	public List<CubeElement> getCubeDimensionAndMeasures(int itemId, List<CubeElement> elements) throws ServiceException;
	
	public List<Template<IDashboard>> getDashboardTemplates() throws ServiceException;
	
	public Template<IDashboard> getDashboardTemplate(int templateId) throws ServiceException;
	
	public void addTemplate(Template<IDashboard> template) throws ServiceException;
	
	public void deleteTemplate(Template<IDashboard> template) throws ServiceException;
	
	public List<Theme> getMetricThemes() throws ServiceException;

	public List<DataPreparation> getDataPreparations() throws ServiceException;
}
