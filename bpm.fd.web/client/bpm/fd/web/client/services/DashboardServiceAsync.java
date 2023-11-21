package bpm.fd.web.client.services;

import java.util.List;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.fd.core.Dashboard;
import bpm.fd.core.component.CubeElement;
import bpm.fm.api.model.Theme;
import bpm.gwt.commons.shared.repository.SaveItemInformations;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;
import bpm.vanilla.platform.core.repository.IDashboard;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.Template;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DashboardServiceAsync {
	
	public void initSession(AsyncCallback<Void> callback);

	public void save(SaveItemInformations itemInfos, Dashboard dashboard, boolean update, AsyncCallback<Integer> callback);
	
	public void preview(Dashboard dashboard, AsyncCallback<String> callback);
	
	public void openDashboard(RepositoryItem item, AsyncCallback<Dashboard> callback);

	public void getDefaultCss(AsyncCallback<String> callback);

	public void getDatasources(AsyncCallback<List<Datasource>> callback);

	public void getDatasets(Datasource datasource, AsyncCallback<List<Dataset>> callback);

	public void getRepositoryItemParameters(int itemId, AsyncCallback<List<Parameter>> callback);

	public void getVanillaMaps(AsyncCallback<List<MapVanilla>> callback);
	
	public void getCubeDimensionAndMeasures(int itemId, List<CubeElement> elements, AsyncCallback<List<CubeElement>> callback);
	
	public void getDashboardTemplates(AsyncCallback<List<Template<IDashboard>>> callback);
	
	public void getDashboardTemplate(int templateId, AsyncCallback<Template<IDashboard>> callback);
	
	public void addTemplate(Template<IDashboard> template, AsyncCallback<Void> callback);
	
	public void deleteTemplate(Template<IDashboard> template, AsyncCallback<Void> callback);
	
	public void getMetricThemes(AsyncCallback<List<Theme>> asyncCallback);

	public void getDataPreparations(AsyncCallback<List<DataPreparation>> asyncCallback);
}
