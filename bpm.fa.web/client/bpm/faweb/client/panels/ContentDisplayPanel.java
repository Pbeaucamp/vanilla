package bpm.faweb.client.panels;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb.TypeDisplay;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.MapDialog;
import bpm.faweb.client.dnd.DraggableGridItem;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.panels.center.ChartTab;
import bpm.faweb.client.panels.center.CubeViewerTab;
import bpm.faweb.client.panels.center.DrillTab;
import bpm.faweb.client.panels.center.DrillThroughTab;
import bpm.faweb.client.panels.center.MapTab;
import bpm.faweb.client.panels.center.MultipleAddTab;
import bpm.faweb.client.panels.center.OverviewTab;
import bpm.faweb.client.panels.center.ProjectionTab;
import bpm.faweb.client.projection.panel.ProjectionCubeView;
import bpm.faweb.client.reporter.ReporterModeDialog;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.MapValues;
import bpm.faweb.shared.infoscube.MapInfo;
import bpm.gwt.commons.client.panels.AbstractTabHeader;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.analysis.DrillInformations;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.platform.core.repository.IRepositoryObject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ContentDisplayPanel extends Composite implements TabManager {

	private static final double DEFAULT_PERCENTAGE = 9.1;
	private static final double MIN_DEFAULT_PERCENTAGE = 0.5;

	private static ContentDisplayPanelUiBinder uiBinder = GWT.create(ContentDisplayPanelUiBinder.class);

	interface ContentDisplayPanelUiBinder extends UiBinder<Widget, ContentDisplayPanel> {
	}

	interface MyStyle extends CssResource {
		String displayNone();

		String contentPanelPositionNormal();

		String contentPanelPositionViewer();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelRightToolbar;

	@UiField
	HTMLPanel tabHeaderPanel, contentPanel, rightToolbar;

	@UiField
	Image btnDrillThroug, btnProjection, btnMap;

	private MainPanel mainPanel;
	private FaWebDragController dragCtrl;

	private CubeViewerTab cubeViewerTab;
	private DrillThroughTab drillThroughTab;
	private ChartTab chartTab;
	private DrillTab drillTab;
	private OverviewTab overviewTab;
	private MultipleAddTab multipleAddTab;
	private ProjectionTab projectionTab;
	private ReporterModeDialog reporterTab;
	private MapTab mapTab;

	private AbstractTabHeader selectedBtn;
	private Tab selectedPanel;

	private AbstractTabHeader previousBtn;
	private Tab previousPanel;

	private List<AbstractTabHeader> openTabs = new ArrayList<AbstractTabHeader>();

	public ContentDisplayPanel(MainPanel mainPanel, FaWebDragController dragCtrl) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.dragCtrl = dragCtrl;

		selectCubeViewer();

		rightToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);

		btnDrillThroug.addStyleName(style.displayNone());
		btnProjection.addStyleName(style.displayNone());
		btnMap.addStyleName(style.displayNone());
	}

	public void changeTab(Tab selectedPanel) {
		savePreviousTab();

		if (selectedBtn != null) {
			this.selectedBtn.setSelected(false);
		}

		if (this.selectedPanel != null) {
			this.selectedPanel.addStyleName(style.displayNone());
		}

		AbstractTabHeader header = selectedPanel.buildTabHeader();
		if (!header.isOpen()) {
			tabHeaderPanel.add(header);
			openTabs.add(header);

			updateSize(openTabs);
		}

		this.selectedBtn = header;
		this.selectedBtn.setSelected(true);

		this.selectedPanel = selectedPanel;
		this.selectedPanel.removeStyleName(style.displayNone());

		if (!selectedPanel.isOpen()) {
			header.setOpen(true);
			this.contentPanel.add(selectedPanel);
		}
		if (selectedPanel instanceof OverviewTab)
			overviewTab.loadOverview();
	}

	private void updateSize(List<AbstractTabHeader> openTabs) {
		double percentage = calcPercentage(openTabs.size());

		for (AbstractTabHeader tabHeader : openTabs) {
			tabHeader.applySize(percentage);
		}
	}

	private double calcPercentage(int tabNumber) {
		if (tabNumber <= 10) {
			return DEFAULT_PERCENTAGE;
		}
		else {
			double value = (100 / tabNumber) - (0.05 * tabNumber);
			return value < MIN_DEFAULT_PERCENTAGE ? MIN_DEFAULT_PERCENTAGE : value;
		}
	}

	@Override
	public void selectTab(AbstractTabHeader tabHeader) {
		changeTab(tabHeader.getTab());
	}

	// public HTMLPanel getRightToolbarPanel() {
	// return rightToolbarPanel;
	// }
	//
	// public SimplePanel getContentPanel() {
	// return contentPanel;
	// }

	@Override
	public void closeTab(AbstractTabHeader tabHeader) {
		int index = openTabs.indexOf(tabHeader);
		openTabs.remove(index);
		tabHeaderPanel.remove(tabHeader);
		contentPanel.remove(tabHeader.getTab());

		updateSize(openTabs);

		if (selectedBtn == tabHeader && !openTabs.isEmpty()) {
			if (index > 0) {
				changeTab(openTabs.get(index - 1).getTab());
			}
			else {
				changeTab(openTabs.get(0).getTab());
			}
		}
	}

	@UiHandler("btnGrid")
	public void onAccueilClick(ClickEvent event) {
		selectCubeViewer();
	}

	public void selectCubeViewer() {
		if (cubeViewerTab == null) {
			cubeViewerTab = new CubeViewerTab(this, mainPanel, dragCtrl);
		}

		changeTab(cubeViewerTab);
	}

	@UiHandler("btnDrillThroug")
	public void onContentClick(ClickEvent event) {
		if (drillThroughTab == null) {
			drillThroughTab = new DrillThroughTab(this, mainPanel);
		}

		changeTab(drillThroughTab);
	}

	@UiHandler("btnChart")
	public void onViewerClick(ClickEvent event) {
		selectChartTab();
	}

	public void selectChartTab() {
		if (chartTab == null) {
			chartTab = new ChartTab(this, mainPanel, dragCtrl);
		}

		changeTab(chartTab);

		// chartTab.refrechSelectChartType();
	}

	@UiHandler("btnOverview")
	public void onTaskClick(ClickEvent event) {
		if (overviewTab == null) {
			overviewTab = new OverviewTab(this, mainPanel);
		}

		changeTab(overviewTab);

		// overviewTab.loadOverview();
	}

	@UiHandler("btnDrill")
	public void onDrillClick(ClickEvent event) {
		if (drillTab == null) {
			drillTab = new DrillTab(this, mainPanel);
		}

		changeTab(drillTab);
	}
	
	public void clearDrill() {
		if (drillTab != null) {
			if (selectedPanel == drillTab)
				drillTab.close();
			drillTab = null;
		}
	}

	@UiHandler("btnMultipleAdd")
	public void onHistoClick(ClickEvent event) {
		if (multipleAddTab == null) {
			multipleAddTab = new MultipleAddTab(this, mainPanel);
		}

		changeTab(multipleAddTab);

		multipleAddTab.loadMultipleAddPanel();
	}

	@UiHandler("btnReporter")
	public void onReporterClick(ClickEvent event) {
		if (reporterTab == null) {
			reporterTab = new ReporterModeDialog(this, mainPanel, dragCtrl);
		}

		changeTab(reporterTab);
	}

	@UiHandler("btnProjection")
	public void onProjectionClick(ClickEvent event) {
		if (projectionTab == null) {
			projectionTab = new ProjectionTab(this, mainPanel, dragCtrl);
		}

		changeTab(projectionTab);
	}

	@UiHandler("btnMap")
	public void onMapClick(ClickEvent event) {
		DraggableGridItem item = mainPanel.getDraggableItem();
		
		final String uname = item.getUname();
		final String element = item.getName();

		chooseMap(uname, element, false);
	}
	
	public void chooseMap(final String uname, final String element, final boolean loadWithoutChoose) {
		FaWebService.Connect.getInstance().getMapInfo(uname, element, new GwtCallbackWrapper<MapInfo>(mainPanel, true, true) {
			@Override
			public void onSuccess(MapInfo mapInfo) {
				//For now we reload every time the dialog
				if (loadWithoutChoose) {
					MapOptions mapOptions = mainPanel.getMapOptions();
					
					loadMap(mapInfo, mapOptions);
				}
				else {
					MapInformation mapInformation = mainPanel.getMapOptions() != null ? mainPanel.getMapOptions().getMapInfo() : null;
					List<List<String>> colors = mainPanel.getMapOptions() != null ? mainPanel.getMapOptions().getColors() : null;
				
					MapDialog dial = new MapDialog(mainPanel, uname, element, mapInfo, mapInformation, colors);
					dial.center();
				}
			}
		}.getAsyncCallback());
	}
	
	public void loadMap(final MapInfo mapInfo, final MapOptions mapOptions) {
		mainPanel.setMapOptions(mapOptions);
		
		String uname = mapOptions.getUname();
		String selectedMeasure = mapOptions.getSelectedMeasure();
		String selectedDimension = mapOptions.getSelectedMeasure();
		int datasetId = mapOptions.getDatasetId();
		List<List<String>> colors = mapOptions.getColors();

		FaWebService.Connect.getInstance().getOsmValues(MainPanel.getInstance().getKeySession(), uname, selectedMeasure, datasetId, selectedDimension, colors, new GwtCallbackWrapper<List<MapValues>>(mainPanel, true, true) {

			@Override
			public void onSuccess(List<MapValues> result) {
				showMap(mapInfo, mapOptions, result);
			}
		}.getAsyncCallback());
	}
	
	private void showMap(MapInfo mapInfo, MapOptions mapOptions, List<MapValues> values) {
		this.mapTab = new MapTab(this, mainPanel, mapInfo, mapOptions, values);

		changeTab(mapTab);
	}
	
	public MapTab getMapTab() {
		return mapTab;
	}

	public void setThroughGrid(DrillInformations drillInfo) {
		if (drillThroughTab == null) {
			drillThroughTab = new DrillThroughTab(this, mainPanel);
		}

		btnDrillThroug.removeStyleName(style.displayNone());

		changeTab(drillThroughTab);

		drillThroughTab.loadThroughPanel(drillInfo);
	}

	public void loadProjection() {
		if (projectionTab == null) {
			projectionTab = new ProjectionTab(this, mainPanel, dragCtrl);
		}

		btnProjection.removeStyleName(style.displayNone());

		changeTab(projectionTab);

		ProjectionCubeView panel = new ProjectionCubeView(mainPanel, projectionTab, null);
		projectionTab.setProjectionView(panel);
	}

//	public void loadDrillMap(String mapName, String name, String uname, List<String> mapAvailables) {
//		MapTab mapTab = new MapTab(this, mainPanel, mapName, uname, name, mainPanel.getMeasureDisplay(), mainPanel.getDimensions(), mapAvailables);
//
//		btnMap.removeStyleName(style.displayNone());
//
//		changeTab(mapTab);
//	}
//
//	public void setMap(String mapName, String uname, String name, List<String> measureDisplay, List<String> dimensions, List<String> mapAvailables) {
//		MapTab mapTab = new MapTab(this, mainPanel, mapName, uname, name, measureDisplay, dimensions, mapAvailables);
//
//		btnMap.removeStyleName(style.displayNone());
//
//		changeTab(mapTab);
//	}

	public void savePreviousTab() {
		this.previousBtn = selectedBtn;
		this.previousPanel = selectedPanel;
	}

	public void selectPreviousTab() {
		if (previousBtn != null && previousPanel != null) {
			changeTab(previousPanel);
		}

		this.previousBtn = null;
		this.previousPanel = null;
	}

	// public void closeTab() {
	// if (selectedBtn != null) {
	// selectedBtn.removeFromParent();
	// }
	//
	// selectPreviousTab();
	// }

	public CubeViewerTab getCubeViewerTab() {
		return cubeViewerTab;
	}

	public OverviewTab getOverviewTab() {
		return overviewTab;
	}

	public ChartTab getChartTab() {
		return chartTab;
	}

	public ProjectionTab getProjectionTab() {
		return projectionTab;
	}

	public ProjectionCubeView getProjectionCubeView() {
		return projectionTab.getProjectionCubeView();
	}

	public void loadOverviewChart(Composite chart) {
		overviewTab.loadOverviewChart(chart);
	}

	public void openChart(String title, String type, String renderer, List<String> series, List<String> groups, List<String> filters, String measure) {
		chartTab.openChart(title, type, renderer, series, groups, filters, measure);
	}

	public void openChartAfterLoad(String title, String chartType, String graphe, List<String> series, List<String> groups, List<String> filters, String measure, List<GroupChart> groupsChart) {
		if (chartTab == null)
			chartTab = new ChartTab(this, mainPanel, dragCtrl);

		chartTab.openChart(title, chartType, graphe, series, groups, filters, measure);
		if (overviewTab != null) {
			overviewTab.loadOverviewAfterLoad(title, chartType, graphe, groupsChart);
		}
	}

	public void deleteChart() {
		if (chartTab != null)
			chartTab.clearChart();
		if (overviewTab != null)
			overviewTab.clearPanel();

	}

	public boolean isMultipleAddVisible() {
		if (multipleAddTab != null && selectedPanel != null && selectedPanel == multipleAddTab) {
			return true;
		}
		return false;
	}

	public boolean isOverviewVisible() {
		if (overviewTab != null && selectedPanel != null && selectedPanel == overviewTab) {
			return true;
		}
		return false;
	}

	public boolean isOnProjection() {
		if (projectionTab != null && selectedPanel != null && selectedPanel == projectionTab) {
			return true;
		}
		return false;
	}

	public String getGridHtml() {
		return cubeViewerTab.getGridHtml();
	}

	public void showWaitPart(boolean visible) {
		if (selectedPanel != null) {
			selectedPanel.showWaitPart(visible);
		}
	}

	public void setDisabledMap(boolean isGeolocalizable, DraggableGridItem item) {
		if (isGeolocalizable) {
			checkMapDefinition(item);
		}
		else if (btnMap.isAttached()) {
			btnMap.removeFromParent();
		}
	}

	private void checkMapDefinition(final DraggableGridItem item) {
		mainPanel.setDraggableItem(item);

		FaWebService.Connect.getInstance().getMapDefinitions(mainPanel.getKeySession(), item.getName(), new AsyncCallback<List<List<String>>>() {

			@Override
			public void onSuccess(List<List<String>> result) {
				if (!result.isEmpty()) {
					btnMap.removeStyleName(style.displayNone());
				}
				else {
					if (btnMap.isAttached()) {
						btnMap.removeFromParent();
					}
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				MessageHelper.openMessageError("An error happend during map load.", caught);
			}
		});
	}

	public void changeDisplay(TypeDisplay display, boolean tools) {
		switch (display) {
		case DASHBOARD:
			if (!tools) {
				hideToolbar();
			}
			break;
		case VIEWER:
			hideToolbar();

			tabHeaderPanel.setVisible(false);
			contentPanel.removeStyleName(style.contentPanelPositionNormal());
			contentPanel.addStyleName(style.contentPanelPositionViewer());

			if (cubeViewerTab != null) {
				cubeViewerTab.changeDisplay(display);
			}

			contentPanel.getElement().getStyle().setRight(0, Unit.PX);
			break;

		default:
			break;
		}
	}

	private void hideToolbar() {
		panelRightToolbar.setVisible(false);

		rightToolbar.getElement().getStyle().setDisplay(Display.NONE);
		contentPanel.getElement().getStyle().setRight(5, Unit.PX);
	}

	@Override
	public void openViewer(IRepositoryObject item) { }

	@Override
	public int getIndex(AbstractTabHeader tabHeader) {
		return -1;
	}

	@Override
	public void updatePosition(String tabId, int index) { }

	@Override
	public void postProcess() { }
	
	public boolean isChartAvailable() {
		if (chartTab != null) {
			return chartTab.isChartAvailable();
		}
		else {
			return false;
		}
	}

	public void buildOverviewChart() {
		chartTab.buildOverviewChart();
	}
}
