package bpm.faweb.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.realityforge.gwt.keycloak.Keycloak;

import bpm.faweb.client.FreeAnalysisWeb.TypeDisplay;
import bpm.faweb.client.dialog.PromptDialog;
import bpm.faweb.client.dnd.DraggableGridItem;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.history.CommandDo;
import bpm.faweb.client.history.CommandRedo;
import bpm.faweb.client.history.CommandUndo;
import bpm.faweb.client.history.Make;
import bpm.faweb.client.history.ModificationHistory;
import bpm.faweb.client.panels.ContentDisplayPanel;
import bpm.faweb.client.panels.CubeToolbar;
import bpm.faweb.client.panels.TopToolbar;
import bpm.faweb.client.panels.center.HasFilter;
import bpm.faweb.client.panels.center.ICubeViewer;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.client.panels.navigation.NavigationPanel;
import bpm.faweb.client.popup.DimTreePopup;
import bpm.faweb.client.popup.GridPopup;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.data.DataField;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.client.utils.ForCalcul;
import bpm.faweb.client.wizards.OpenCubeDialog;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.ParameterDTO;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.ChartInfos;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemHier;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.comment.CommentPanel;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT.TypeCollaboration;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.InfoUser;
import bpm.gwt.commons.shared.analysis.DrillInformations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends CompositeWaitPanel {

	private static final int CLASSIC_NAVIGATION_PANEL = 350;
	private static final int CLASSIC_NAVIGATION = 345;

	private static final int NAVIGATION_COLLAPSE = 35;
	private static final int NAVIGATION_PANEL_COLLAPSE = 30;

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	interface MyStyle extends CssResource {
		String panelAbsolute();
	}

	@UiField
	MyStyle style;

	@UiField
	AbsolutePanel draggablePanel;

	@UiField
	DockLayoutPanel dockPanel;

	@UiField
	SimplePanel panelToolbar, leftPanel, panelCenter;

	private static MainPanel instance;
	private InfoUser infoUser;
	private InfosReport infosReport = null;

	private int keySession;

	private NavigationPanel navigationPanel;
	private ContentDisplayPanel contentDisplay;
	private CubeToolbar toolbar;

	private FaWebDragController dragCtrl;
	private Projection actualProjection;
	private CubeView grid;
	private PopupPanel gridPopup;
	private PopupPanel treePopup;
	private ItemHier timeHiera;

	private ModificationHistory mh;
	private Make makemodif;

	private HashMap<String, String> allMeasuresAdded = new HashMap<String, String>();
	private List<String> selectedRow = new ArrayList<String>();
	private List<String> selectedCol = new ArrayList<String>();
	private List<Calcul> calculs = new ArrayList<Calcul>();
	private List<String> selectedFilters = new ArrayList<String>();

	private boolean isUndo = false;
	private boolean calcul = false;
	private boolean loadCalcul = false;
	private boolean before;

	// FusionMap
	private List<String> measureDisplay;
	private List<String> dimensions;
	private DraggableGridItem draggableItem;
	private TopToolbar topToolbar;
	private MapOptions mapOptions;
	
	public int dateFunction = 0;
	public int dateFunctionMonth = 0;

	public static MainPanel getInstance() {
		return instance;
	}

	public MainPanel(int keySession, TypeDisplay display, String cubeName, InfoUser infoUser, Integer fasdId, String viewName, String dimName, String memberName, boolean dimPanel, String[] unames, boolean tools, boolean faweb, String sessionId, Keycloak keycloak) {
		initWidget(uiBinder.createAndBindUi(this));
		instance = this;

		this.keySession = keySession;
		this.infoUser = infoUser;

		dragCtrl = new FaWebDragController(this, draggablePanel, false);

		buildTopToolbar(keycloak);

		if (unames != null) {
			buildLeftPanel(cubeName, unames);
		}
		else {
			buildLeftPanel(cubeName, null);
		}

		buildCenterPanel();

		this.addStyleName(VanillaCSS.BODY_BACKGROUND);
		this.addStyleName(style.panelAbsolute());

		mh = new ModificationHistory(this);
		CommandDo d = new CommandDo(mh);
		CommandUndo u = new CommandUndo(mh);
		CommandRedo r = new CommandRedo(mh);
		makemodif = new Make(u, r, d);

		if(faweb){
			openCubeFromFmdtWeb(sessionId);
		}
		else if (fasdId != null && cubeName != null && !cubeName.isEmpty() && viewName != null && !viewName.isEmpty()) {
			gettingViewParameters(fasdId, cubeName, viewName);
		}
		else if (fasdId != null && cubeName != null && !cubeName.isEmpty()) {
			openCubeFromPortal(fasdId, cubeName, dimName, memberName);
		}
		else if (fasdId != null && fasdId > 0) {
			showOpenDialog(fasdId);
		}

		changeDisplay(display, dimPanel, tools);
	}
	
	private void changeDisplay(TypeDisplay display, boolean dimPanel, boolean tools) {
		switch (display) {
		case DISCO:
			panelToolbar.setVisible(false);
			dockPanel.setWidgetSize(panelToolbar, 5);
			break;
		case DASHBOARD:
			panelToolbar.setVisible(false);
			dockPanel.setWidgetSize(panelToolbar, 5);
			if (!dimPanel) {
				leftPanel.setVisible(false);
				dockPanel.setWidgetSize(leftPanel, 5);
			}
			if(!tools) {
				createToolbar();
			}
			break;
		case VIEWER:
			createToolbar();
			break;

		default:
			break;
		}
		
		contentDisplay.changeDisplay(display, tools);
		navigationPanel.changeDisplay(display);
	}

	private void createToolbar() {
		this.toolbar = new CubeToolbar(contentDisplay.getCubeViewerTab(), infoUser, false);
		panelToolbar.setWidget(toolbar);
		
		dockPanel.setWidgetSize(panelToolbar, 40);
	}
	
	public CubeToolbar getViewerToolbar() {
		return toolbar;
	}

	private void buildTopToolbar(Keycloak keycloak) {
		topToolbar = new TopToolbar(this, infoUser, keycloak);
		panelToolbar.setWidget(topToolbar);
	}

	private void buildLeftPanel(String cubeName, String[] unames) {
		navigationPanel = new NavigationPanel(this, cubeName, unames);
		leftPanel.setWidget(navigationPanel);
	}

	private void buildCenterPanel() {
		this.contentDisplay = new ContentDisplayPanel(this, dragCtrl);
		panelCenter.setWidget(contentDisplay);
	}

	public int getKeySession() {
		return keySession;
	}

	public ContentDisplayPanel getDisplayPanel() {
		return contentDisplay;
	}

	public NavigationPanel getNavigationPanel() {
		return navigationPanel;
	}

	public void collapseNavigationPanel(boolean isCollapse) {
		if (isCollapse) {
			dockPanel.setWidgetSize(leftPanel, CLASSIC_NAVIGATION_PANEL);
			navigationPanel.adaptSize(CLASSIC_NAVIGATION, isCollapse);
		}
		else {
			dockPanel.setWidgetSize(leftPanel, NAVIGATION_COLLAPSE);
			navigationPanel.adaptSize(NAVIGATION_PANEL_COLLAPSE, isCollapse);
		}
	}

	public void setThroughGrid(DrillInformations drillInfo) {
		contentDisplay.setThroughGrid(drillInfo);
	}

	public ICubeViewer setGridFromRCP(Object result) {
		deleteChart();
		
		this.selectedRow = new ArrayList<String>();
		this.selectedCol = new ArrayList<String>();

		boolean calledByUndo = isUndo;

		if (result instanceof GridCube) {
			makemodif.do_(result);
		}
		else {
			makemodif.do_(((InfosReport) result).getGrid());
		}

		final ICubeViewer cubeViewer = getDisplayPanel().isOnProjection() ? getDisplayPanel().getProjectionTab() : getDisplayPanel().getCubeViewerTab();
		
		GridCube gc = null;
		if (result instanceof GridCube) {
			gc = (GridCube) result;
			getInfosReport().setGrid(gc);
		}
		else {
			gc = ((InfosReport) result).getGrid();
			setInfosReport((InfosReport) result);
		}

		if (gc.getCalculs() != null && gc.getCalculs().size() > 0) {
			calcul = true;
			loadCalcul = true;
			ForCalcul.activatedMenu(this, calcul);
		}
		else {
			loadCalcul = false;
		}

		setGridFromLocal(cubeViewer, calcul, gc);

		if (!calledByUndo) {
			showTabWaitPart(false);
		}
		
		return cubeViewer;
	}

	public CubeView setGridFromLocal(ICubeViewer cubeViewer, boolean calcul, GridCube gc) {
		grid = new CubeView(this, cubeViewer, gc, calcul, contentDisplay.isOnProjection());

		((HasFilter)cubeViewer).setRowsCols(gc);
		
		if (calcul && loadCalcul) {
			List<Calcul> calculs = gc.getCalculs();

			for (Iterator<Calcul> it = calculs.iterator(); it.hasNext();) {
				Calcul c = it.next();
				if (c.getOperator() <= 1) {
					ForCalcul.associativeCalcul(this, c, grid);

				}
				else {
					ForCalcul.distributiveCalcul(this, c, grid);
				}
			}
			getCalculs().addAll(calculs);
		}

		cubeViewer.addGrid(grid);

		if (contentDisplay.isOverviewVisible()) {
			contentDisplay.getOverviewTab().loadGrid(gc, false);
		}
		showWaitPart(false);
		return grid;
	}

	public void drill(int row, int cell, Projection projection) {

		FaWebService.Connect.getInstance().drillService(keySession, row, cell, projection, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				if (result != null) {
					setInfosReport(result);
					if (getInfosReport().isDimchange()) {
						FaWebTreeItem theone = null;
						String currMb = getInfosReport().getMembrerdrill();

						List<FaWebTreeItem> l = navigationPanel.getDimensionPanel().getExtremite();
						for (FaWebTreeItem fwti : l) {
							if (fwti.getItemDim().getUname().equals(currMb)) {
								theone = fwti;
							}
						}

						try {
							if (theone == null) {
								theone = navigationPanel.getDimensionPanel().findRootItem(currMb);
							}

							navigationPanel.getDimensionPanel().addNext(getInfosReport().getNewmembers(), theone);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					setGridFromRCP(result);
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "drill unauthorised on measure");
					showTabWaitPart(false);
				}

			}

			public void onFailure(Throwable ex) {
				showWaitPart(false);

				ex.printStackTrace();
			}
		});
	}

	public void drillAll(int row, int cell, Projection projection, boolean isDrillDown) {

		FaWebService.Connect.getInstance().drillAllService(keySession, row, cell, projection, isDrillDown, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				if (result != null) {
					setInfosReport(result);
					if (getInfosReport().isDimchange()) {
						FaWebTreeItem theone = null;
						String currMb = getInfosReport().getMembrerdrill();

						List<FaWebTreeItem> l = navigationPanel.getDimensionPanel().getExtremite();
						for (FaWebTreeItem fwti : l) {
							if (fwti.getItemDim().getUname().equals(currMb)) {
								theone = fwti;
							}
						}

						try {
							if (theone == null) {
								theone = navigationPanel.getDimensionPanel().findRootItem(currMb);
							}

							navigationPanel.getDimensionPanel().addNext(getInfosReport().getNewmembers(), theone);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					setGridFromRCP(result);
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "drill unauthorised on measure");
					showTabWaitPart(false);
				}

			}

			public void onFailure(Throwable ex) {
				showWaitPart(false);

				ex.printStackTrace();
			}
		});
	}

	public void drillthrough(final int row, final int cell) {
		if (contentDisplay.isOnProjection()) {
			FaWebService.Connect.getInstance().drillThroughProjection(keySession, row, cell, actualProjection, new AsyncCallback<List<List<DataField>>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();

					showTabWaitPart(false);
				}

				@Override
				public void onSuccess(List<List<DataField>> result) {
					showTabWaitPart(false);

					contentDisplay.getProjectionCubeView().setDrillThrough(result, row, cell);
				}
			});
		}
		else {
			FaWebService.Connect.getInstance().drillThroughService(keySession, row, cell, null, new AsyncCallback<DrillInformations>() {
				public void onSuccess(DrillInformations drillInfo) {
					showTabWaitPart(false);

					setThroughGrid(drillInfo);
				}

				public void onFailure(Throwable ex) {
					ex.printStackTrace();

					ExceptionManager.getInstance().handleException(ex, "Unable to drill through");

					showTabWaitPart(false);
				}
			});
		}
	}

	public Make getMakemodif() {
		return makemodif;
	}

	public ModificationHistory getMh() {
		return mh;
	}

	public void setMh(ModificationHistory mh) {
		this.mh = mh;
	}

	public boolean isBefore() {
		return before;
	}

	public void setBefore(boolean before) {
		this.before = before;
	}

	public InfosReport getInfosReport() {
		return infosReport;
	}

	public void setInfosReport(InfosReport curr) {
		this.infosReport = curr;
		if (curr.getActualProjection() != null) {
			this.actualProjection = curr.getActualProjection();
		}
	}

	public void move(int startRow, int startCol, int row, int col, boolean before) {
		FaWebService.Connect.getInstance().moveService(keySession, startRow, startCol, row, col, before, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				if (result != null) {
					setGridFromRCP(result);
				}
			}

			public void onFailure(Throwable ex) {
				ex.printStackTrace();
			}
		});
	}

	public void openChart(String title, String type, String renderer, List<String> series, List<String> groups, List<String> filters, String measure) {
		contentDisplay.openChart(title, type, renderer, series, groups, filters, measure);
	}

	public void openChartAfterLoad(String title, String chartType, String graphe, List<String> series, List<String> groups, List<String> filters, String measure, List<GroupChart> groupsChart) {
		contentDisplay.openChartAfterLoad(title, chartType, graphe, series, groups, filters, measure, groupsChart);
	}
	
	public void deleteChart() {
		contentDisplay.deleteChart();
	}

	public void setSelectedRow(List<String> selectedRow) {
		this.selectedRow = selectedRow;
	}

	public List<String> getSelectedRow() {
		return selectedRow;
	}

	public void addSelectedRow(String selectedRow) {
		this.selectedRow.add(selectedRow);
	}

	public void removeSelectedRow(String selectedRowToRemove) {
		this.selectedRow.remove(selectedRowToRemove);
	}

	public void setSelectedCol(List<String> selectedCol) {
		this.selectedCol = selectedCol;
	}

	public List<String> getSelectedCol() {
		return selectedCol;
	}

	public void addSelectedCol(String selectedCol) {
		this.selectedCol.add(selectedCol);
	}

	public void removeSelectedCol(String selectedColToRemove) {
		this.selectedCol.remove(selectedColToRemove);
	}

	public void deselect() {
		getDisplayPanel().getCubeViewerTab().deselect();
	}

	public void showOpenDialog(int fasdId) {
		OpenCubeDialog dial = new OpenCubeDialog(this, keySession, fasdId);
		dial.center();
	}

	public void showNewGridPopup(String uname, DraggableGridItem dragItem, int posX, int posY) {
		gridPopup = new GridPopup(this, uname, dragItem);
		gridPopup.setPopupPosition(posX, posY);
		gridPopup.show();
	}

	public void hideGridPopup() {
		if (gridPopup != null && gridPopup.isShowing()) {
			gridPopup.hide();
		}
	}

	public void showNewTreePopup(String uname, int posX, int posY) {
		treePopup = new DimTreePopup(this, uname);
		treePopup.setPopupPosition(posX, posY);
		treePopup.show();
	}

	public void hideTreePopup() {
		if (treePopup != null && treePopup.isShowing()) {
			treePopup.hide();
		}
	}

	public void setCalcul(boolean calcul) {
		this.calcul = calcul;
	}

	public boolean isCalcul() {
		return calcul;
	}
	
	public MapOptions getMapOptions() {
		return mapOptions;
	}
	
	public void setMapOptions(MapOptions mapOptions) {
		this.mapOptions = mapOptions;
	}

	public void setMeasureDisplay(List<String> measureDisplay) {
		this.measureDisplay = measureDisplay;
	}

	public List<String> getMeasureDisplay() {
		return measureDisplay;
	}

	public void setDimensions(List<String> dimensions) {
		this.dimensions = dimensions;
	}

	public List<String> getDimensions() {
		return dimensions;
	}

	public HashMap<String, String> getAllMeasureAdded() {
		return allMeasuresAdded;
	}

	public void clearDragSelection() {
		this.dragCtrl.clearSelection();
	}

	public FaWebDragController getDragController() {
		return dragCtrl;
	}

	public CubeView getGrid() {
		return grid;
	}

	public void putMeasureToAllMeasuresAdded(String uname, String label) {
		this.allMeasuresAdded.put(uname, label);
	}

	public void showCommentsPart() {
		showWaitPart(true);

		FaWebService.Connect.getInstance().getCurrentCubeItemId(keySession, new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				showWaitPart(false);
			}

			@Override
			public void onSuccess(Integer directoryItemId) {
				showWaitPart(false);

				CommentPanel commentsPanel = new CommentPanel(MainPanel.this, directoryItemId, TypeCollaboration.ITEM_NOTE, infoUser.getGroup(), infoUser.getAvailableGroups());
				commentsPanel.showCommentPart(true);
				commentsPanel.show();
				commentsPanel.setPopupPosition(Window.getClientWidth() - 332, 55);
			}
		});
	}

	public InfoUser getInfosUser() {
		return infoUser;
	}

	public void openCubeFromPortal(final int fasdId, final String cubeName, String dimName, String memberName) {
		showWaitPart(true);

		FaWebService.Connect.getInstance().openCubeFromPortal(keySession, fasdId, cubeName, dimName, memberName, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				setInfosReport(result);

				try {
					contentDisplay.getCubeViewerTab().clearFilters();
				} catch (Exception e) {
					e.printStackTrace();
				}

				getNavigationPanel().refreshDataPanels(cubeName);

				setGridFromRCP(result);
				
				contentDisplay.getCubeViewerTab().loadComment(toolbar, fasdId);

				if (getInfosReport().getWheres() != null && getInfosReport().getWheres().size() > 0) {
					contentDisplay.getCubeViewerTab().addParams(getInfosReport().getWheres());
				}

				showWaitPart(false);
			}

			public void onFailure(Throwable ex) {
				showWaitPart(false);

				ex.printStackTrace();
			}
		});
	}

	public void openViewFromPortal(final int fasdId, final String cubeName, String viewName, HashMap<String, String> parameters) {
		showWaitPart(true);

		FaWebService.Connect.getInstance().openViewFromPortal(keySession, fasdId, cubeName, viewName, parameters, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				try {
					setMh(new ModificationHistory(MainPanel.this));
				} catch (Exception e) {
					e.printStackTrace();
				}

				setInfosReport(result);
				setGridFromRCP(result);

				ChartInfos chart = getInfosReport().getChartInfos();
				if (chart != null) {
					final List<String> groups = chart.getChartGroups();
					final List<String> datas = chart.getChartDatas();
					final List<String> filters = chart.getChartFilters();
					final String measureRecup = chart.getMeasure();
					
					final String title = chart.getTitle();
					final String chartType = chart.getType();
					final String graphe = chart.getRenderer();

					if ((groups != null && !groups.isEmpty()) || (datas != null && !datas.isEmpty())) {
						FaWebService.Connect.getInstance().executeQuery(keySession, groups, datas, filters, measureRecup, new AsyncCallback<List<GroupChart>>() {

							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							public void onSuccess(List<GroupChart> groupsChart) {
								if (groupsChart != null && !groupsChart.isEmpty()) {
									openChartAfterLoad(title, chartType, graphe, datas, groups, filters, measureRecup, groupsChart);
								}
							}

						});
					}
				}

				contentDisplay.getCubeViewerTab().clearFilters();

				if (getInfosReport().getParameters() != null && getInfosReport().getParameters().size() > 0) {
					List<String> params = new ArrayList<String>();
					for (ParameterDTO param : getInfosReport().getParameters()) {
						params.add(param.getValue());
					}
					contentDisplay.getCubeViewerTab().addParams(params);
				}

				if (getInfosReport().getWheres() != null && getInfosReport().getWheres().size() > 0) {
					contentDisplay.getCubeViewerTab().addParams(getInfosReport().getWheres());
				}
				
				contentDisplay.getCubeViewerTab().loadComment(toolbar, fasdId);

				getNavigationPanel().refreshDataPanels(cubeName);

				showWaitPart(false);
			}

			public void onFailure(Throwable ex) {
				showWaitPart(false);

				ex.printStackTrace();
			}
		});
	}

	private void gettingViewParameters(final int fasdId, final String cubeName, final String viewName) {
		FaWebService.Connect.getInstance().getParametersForView(keySession, viewName, cubeName, fasdId, true, new AsyncCallback<HashMap<ParameterDTO, List<String>>>() {

			public void onSuccess(HashMap<ParameterDTO, List<String>> result) {
				final HashMap<String, String> params = new HashMap<String, String>();
				if (result != null && result.size() > 0) {
					final List<ParameterDTO> temp = new ArrayList<ParameterDTO>();
					temp.addAll(result.keySet());
					for (ParameterDTO param : result.keySet()) {
						final PromptDialog dial = new PromptDialog(MainPanel.this, param, params, result);
						dial.center();
						dial.addCloseHandler(new CloseHandler<PopupPanel>() {
							public void onClose(CloseEvent<PopupPanel> event) {
								temp.remove(dial.getParameter());
								if (temp.isEmpty()) {
									openViewFromPortal(fasdId, cubeName, viewName, params);
								}
							}
						});
					}
				}
				else {
					openViewFromPortal(fasdId, cubeName, viewName, params);
				}
			}

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}

	public void loadProjectionGrid(Projection proj) {
		this.actualProjection = proj;
		showWaitPart(true);
		FaWebService.Connect.getInstance().createNewProjection(keySession, proj, new AsyncCallback<InfosReport>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				showWaitPart(false);
			}

			@Override
			public void onSuccess(InfosReport result) {
				infosReport = result;

				contentDisplay.loadProjection();

				showWaitPart(false);
			}
		});

	}

	public Projection getActualProjection() {
		return actualProjection;
	}

	public void setActualProjection(Projection proj) {
		actualProjection = proj;
	}

	public void setTimeHiera(ItemHier timeHiera) {
		this.timeHiera = timeHiera;
	}

	public ItemHier getTimeHiera() {
		return timeHiera;
	}

	public List<Calcul> getCalculs() {
		return calculs;
	}

	public void setCalculs(List<Calcul> calculs) {
		this.calculs = calculs;
	}

	public void addCalcul(Calcul calcul) {
		this.calculs.add(calcul);
	}

	public List<String> getSelectedFilters() {
		return selectedFilters;
	}

	public void setUndo(boolean isUndo) {
		this.isUndo = isUndo;
	}

	public void showTabWaitPart(boolean visible) {
		getDisplayPanel().showWaitPart(visible);
	}

	public DraggableGridItem getDraggableItem() {
		return draggableItem;
	}

	public void setDraggableItem(DraggableGridItem draggableItem) {
		this.draggableItem = draggableItem;
	}

	public TopToolbar getTopToolbar() {
		return topToolbar;
	}

	public void setTopToolbar(TopToolbar topToolbar) {
		this.topToolbar = topToolbar;
	}
	
	private void openCubeFromFmdtWeb(String sessionId){
		FaWebService.Connect.getInstance().openCubeFromFMDTWeb (keySession, sessionId, new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				setInfosReport(result);

				try {
					contentDisplay.getCubeViewerTab().clearFilters();
				} catch (Exception e) {
					e.printStackTrace();
				}

				getNavigationPanel().refreshDataPanels(result.getCubeName());

				setGridFromRCP(result);
				
			//	contentDisplay.getCubeViewerTab().loadComment(toolbar, fasdId);

				if (getInfosReport().getWheres() != null && getInfosReport().getWheres().size() > 0) {
					contentDisplay.getCubeViewerTab().addParams(getInfosReport().getWheres());
				}

				showWaitPart(false);
			}

			public void onFailure(Throwable ex) {
				showWaitPart(false);

				ex.printStackTrace();
			}
		});
	}

}
