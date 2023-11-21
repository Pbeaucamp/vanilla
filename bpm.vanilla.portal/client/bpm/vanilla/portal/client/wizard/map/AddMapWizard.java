package bpm.vanilla.portal.client.wizard.map;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.wizard.GwtWizard;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.mdm.model.supplier.Contract;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapDatasourceKML;
import bpm.vanilla.map.core.design.MapDatasourceWFS;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.portal.client.panels.center.MapDesignerPanel;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class AddMapWizard extends GwtWizard implements IWait {

	private MapDesignerPanel MapDesignerPanel;

	private IGwtPage currentPage;

	private AddMapDefinitionPage definitionPage;
	private AddMapParamPage paramPage;
	private AddMapDataSourcePage dataSourcePage;
	private AddMapDataSetPage dataSetPage;
	private static MapDataSource currentDataSource;

	private boolean edit = false;
	private MapVanilla selectedMap;
	private MapDataSource selectedMapDataSource;
	private List<MapDataSet> dtSList;

	private String type = "SQL";

	public AddMapWizard(MapDesignerPanel MapDesignerPanel) {
		super(ToolsGWT.lblCnst.AddANewMap());
		this.edit = false;
		this.MapDesignerPanel = MapDesignerPanel;

		definitionPage = new AddMapDefinitionPage(this, 0);

		setCurrentPage(definitionPage);
	}

	public AddMapWizard(MapDesignerPanel MapDesignerPanel, MapVanilla selectedMap) {
		super(ToolsGWT.lblCnst.Edit());
		this.edit = true;
		this.MapDesignerPanel = MapDesignerPanel;
		this.selectedMap = selectedMap;
		

		definitionPage = new AddMapDefinitionPage(this, 0);
		definitionPage.setName(selectedMap.getName());
		definitionPage.setDescription(selectedMap.getDescription());
		if(selectedMap.getDataSetList().get(0).getDataSource() instanceof MapDatasourceKML) {
			type = "KML";
		}
		else if(selectedMap.getDataSetList().get(0).getDataSource() instanceof MapDatasourceWFS) {
			type = "WFS";
		}
		definitionPage.setType(type);

		paramPage = new AddMapParamPage(this, 1, selectedMap);

		dataSourcePage = new AddMapDataSourcePage(this, 2);

		if(selectedMap.getDataSetList() != null && !selectedMap.getDataSetList().isEmpty()) {
			dataSourcePage.setSelectedDataSource(selectedMap.getDataSetList().get(0).getDataSource().getNomDataSource());
		}
		else {
			dataSourcePage = null;
		}

		if(dataSourcePage != null) {

			dataSetPage = new AddMapDataSetPage(this, 3, new MapDataSource());
			dataSetPage.setDataSetList(selectedMap.getDataSetList());
		}

		setCurrentPage(definitionPage);
	}

	@Override
	public boolean canFinish() {
		try {
			return definitionPage.isComplete() && paramPage.isComplete() && dataSourcePage.isComplete() && type.equals("SQL") ? dataSetPage.isComplete() : true;
		} catch(Exception e) {
			return false;
		}
	}

	@Override
	public void updateBtn() {
		setBtnBackState(currentPage.canGoBack() ? true : false);
		setBtnNextState(currentPage.canGoFurther() ? true : false);
		setBtnFinishState(canFinish() ? true : false);
	}

	@Override
	public void setCurrentPage(IGwtPage page) {
		if(page instanceof AddMapDefinitionPage)
			setContentPanel((AddMapDefinitionPage) page);
		else if(page instanceof AddMapParamPage)
			setContentPanel((AddMapParamPage) page);
		else if(page instanceof AddMapDataSourcePage)
			setContentPanel((AddMapDataSourcePage) page);
		else if(page instanceof AddMapDataSetPage)
			setContentPanel((AddMapDataSetPage) page);
		currentPage = page;
		updateBtn();

	}

	@Override
	protected void onClickFinish() {

		MapVanilla map = getCurrentMapVanilla();
		MapDesignerPanel.addMap(map);

		AddMapWizard.this.hide();
	}

	@Override
	protected void onNextClick() {
		if(currentPage instanceof AddMapDefinitionPage) {
			if(edit) {
				if(paramPage != null) {
					setCurrentPage(paramPage);
				}
				else {
					setCurrentPage(definitionPage);
				}
			}
			else {
				if(paramPage == null) {
					paramPage = new AddMapParamPage(this, 1, null);
				}
				setCurrentPage(paramPage);
			}
		}
		else if(currentPage instanceof AddMapParamPage) {
			if(dataSourcePage == null) {
				dataSourcePage = new AddMapDataSourcePage(this, 2);

				loadDrivers();
				loadDataSource();
			}
			setCurrentPage(dataSourcePage);
		}
		else if(currentPage instanceof AddMapDataSourcePage) {
			if(dataSetPage == null) {
				currentDataSource = new MapDataSource(dataSourcePage.getNomDataSource(), dataSourcePage.getUrl(), dataSourcePage.getDriver(), dataSourcePage.getLogin(), dataSourcePage.getMdp());
				dataSetPage = new AddMapDataSetPage(this, 3, currentDataSource);
			}
			else if(selectedMap != null) {
				currentDataSource = new MapDataSource(dataSourcePage.getNomDataSource(), dataSourcePage.getUrl(), dataSourcePage.getDriver(), dataSourcePage.getLogin(), dataSourcePage.getMdp());
				// dataSetPage.setSelectedColumns(selectedMap.getDataSet().getLatitude(), selectedMap.getDataSet().getLongitude(), selectedMap.getDataSet().getIdZone()); A reporter dans l'ouverture de mapdatasetdialog
			}
			setCurrentPage(dataSetPage);
		}
	}

	@Override
	protected void onBackClick() {
		if(currentPage instanceof AddMapParamPage) {
			setCurrentPage(definitionPage);
		}
		else if(currentPage instanceof AddMapDataSourcePage) {
			setCurrentPage(paramPage);
		}
		else if(currentPage instanceof AddMapDataSetPage) {
			setCurrentPage(dataSourcePage);
		}
	}

	private void loadDrivers() {
		showWaitPart(true);
		BiPortalService.Connect.getInstance().getLesDrivers(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadDrivers());
			}

			@Override
			public void onSuccess(List<String> result) {
				showWaitPart(false);
				List<String> lesDrivers = result;
				for(String driver : lesDrivers) {
					dataSourcePage.setDriver(driver, CommonConstants.jdbcLabels.get(driver));
				}

			}
		});
	}

	private void loadDataSource() {
		showWaitPart(true);
		BiPortalService.Connect.getInstance().getMapsDataSource(new AsyncCallback<List<MapDataSource>>() {

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);

				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, ToolsGWT.lblCnst.UnableToLoadDataSource());
			}

			@Override
			public void onSuccess(List<MapDataSource> result) {
				showWaitPart(false);
				List<MapDataSource> lesDataSource = result;
				for(MapDataSource dtS : lesDataSource) {
					dataSourcePage.setDataSource(dtS.getNomDataSource());
				}

			}
		});
	}

	public static MapDataSource getCurrentMapDataSource() {
		return currentDataSource;
	}

	public MapVanilla getCurrentMapVanilla() {

		MapDataSource dataSource;
		String name = definitionPage.getName().trim(), description = definitionPage.getDescription().trim(), projection = paramPage.getProjection().trim();
		int zoom = -1;
		zoom = paramPage.getZoom();

		Double originLat = paramPage.getOriginLat();
		Double originLong = paramPage.getOriginLong();
		Double boundLeft = paramPage.getBoundLeft();
		Double boundBottom = paramPage.getBoundBottom();
		Double boundRight = paramPage.getBoundRight();
		Double boundTop = paramPage.getBoundTop();

		/* construire la liste de dataset */
		// String query = dataSetPage.getQuery(), latitude = dataSetPage.getLatitude(), longitude = dataSetPage.getLongitude(),
		// idZone = dataSetPage.getIdZone();

		// verifier si data source existant ou pas
		String nomDataSource, url, driver, login, mdp;
		if(type.equals("SQL")) {
			if(!dataSourcePage.lstDataSource.getItemText(dataSourcePage.lstDataSource.getSelectedIndex()).equals(ToolsGWT.lblCnst.chooseMapDataSource())) {

				nomDataSource = dataSourcePage.lstDataSource.getItemText(dataSourcePage.lstDataSource.getSelectedIndex());
				dataSource = null;
			}
			else {
				nomDataSource = dataSourcePage.getNomDataSource();
				url = dataSourcePage.getUrl();
				driver = dataSourcePage.getDriver();
				login = dataSourcePage.getLogin();
				mdp = dataSourcePage.getMdp();

				dataSource = new MapDataSource(nomDataSource, url, driver, login, mdp);
			}

			if(edit) {
				// Verifier si le dataSource existe deja

				if(dataSource == null) {
					dataSource = new MapDataSource(nomDataSource, "", "", "", "");
				}

				List<MapDataSet> selectedMapDataSetList = selectedMap.getDataSetList();
				dtSList = new ArrayList<MapDataSet>(dataSetPage.getMapDataSetList());
				if(selectedMap.getDataSetList() != null && !selectedMap.getDataSetList().isEmpty()) {
					selectedMapDataSource = selectedMap.getDataSetList().get(0).getDataSource();

					if(selectedMapDataSource.getNomDataSource().equals(dataSource.getNomDataSource())) {
						for(MapDataSet row : dtSList) {
							row.setDataSource(selectedMapDataSource);
						}
					}
				}

				for(MapDataSet dtS : dtSList) {
					dtS.setIdMapVanilla(selectedMap.getId());
				}

				selectedMap.setName(name);
				selectedMap.setDescription(description);
				selectedMap.setZoom(zoom);
				selectedMap.setOriginLat(originLat);
				selectedMap.setOriginLong(originLong);
				selectedMap.setBoundLeft(boundLeft);
				selectedMap.setBoundBottom(boundBottom);
				selectedMap.setBoundRight(boundRight);
				selectedMap.setBoundTop(boundTop);
				selectedMap.setProjection(projection);
				selectedMap.setDataSetList(dtSList);

				return selectedMap;
			}
			else {
				if(dataSource == null) {
					dataSource = new MapDataSource(nomDataSource, "", "", "", "");
				}

				dtSList = new ArrayList<MapDataSet>(dataSetPage.getMapDataSetList());
				MapVanilla map = new MapVanilla(name, description, zoom, originLat, originLong, boundLeft, boundBottom, boundRight, boundTop, projection, dtSList);

				return map;
			}
		}

		else if(type.equals("KML")) {

			Contract c = dataSourcePage.contractListBox.getSelectedObject();
			MapDatasourceKML kml = new MapDatasourceKML();
			kml.setContractId(c.getId());
			kml.setNomDataSource(c.getName());
			MapDataSet dataset = new MapDataSet();
			dataset.setName(c.getName());
			dataset.setDataSource(kml);
			dtSList = new ArrayList<MapDataSet>();
			dtSList.add(dataset);
			MapVanilla map;

			map = new MapVanilla(name, description, zoom, originLat, originLong, boundLeft, boundBottom, boundRight, boundTop, projection, dtSList);
			map.setType("KML");
			if(edit) {
				map.setId(selectedMap.getId());
				map.getDataSetList().get(0).setIdMapVanilla(selectedMap.getId());
			}
			return map;
		}
		
		else {
			MapDatasourceWFS wfs = new MapDatasourceWFS();
			wfs.setUrl(dataSourcePage.getWfsUrl());
			wfs.setLayer(dataSourcePage.getWfsLayer());
			wfs.setField(dataSourcePage.getField());
			wfs.setNomDataSource(name);
			MapDataSet dataset = new MapDataSet();
			dataset.setName(name);
			dataset.setDataSource(wfs);
			dtSList = new ArrayList<MapDataSet>();
			dtSList.add(dataset);
			MapVanilla map;

			map = new MapVanilla(name, description, zoom, originLat, originLong, boundLeft, boundBottom, boundRight, boundTop, projection, dtSList);
			map.setType("WFS");
			if(edit) {
				map.setId(selectedMap.getId());
				map.getDataSetList().get(0).setIdMapVanilla(selectedMap.getId());
			}
			return map;
		}

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		dataSourcePage = new AddMapDataSourcePage(this, 2);
	}
	
	public MapVanilla getEditedMap() {
		return selectedMap;
	}
}
