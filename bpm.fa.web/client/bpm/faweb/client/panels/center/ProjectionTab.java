package bpm.faweb.client.panels.center;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.RepositoryContentDialog;
import bpm.faweb.client.dnd.BinDragController;
import bpm.faweb.client.dnd.BinDropController;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.dnd.FilterDropController;
import bpm.faweb.client.dnd.RowColDropController;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.dialog.DialogCompareProjection;
import bpm.faweb.client.projection.dialog.DialogCreateProjection;
import bpm.faweb.client.projection.panel.ProjectionCubeView;
import bpm.faweb.client.projection.panel.UpdateProjectionDataPanel;
import bpm.faweb.client.services.CubeServices;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.FaWebFilterHTML;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ProjectionTab extends Tab implements HasFilter, ICubeViewer {

	private static final String CSS_DROP_FILTER_PANEL = "dropPanel";
	private static final String CSS_DROP_FILTER_PANEL_HEIGHT = "dropPanelHeight";
	private static final String CSS_IMG_BIN = "imgBin";
	private static final String CSS_MAIN_DROP_FILTER_PANEL = "filterMainPanel";
	private static final String CSS_PANEL_CLICK_FILTER = "panelClickFilter";
	private static final String CSS_IMG_FILTER_PROMPT = "imgFilterPrompt";
	private static final String CSS_LBL_FILTER = "labelFilter";
	private static final String CSS_RESOURCE = "resourceHtml";

	private static final int SIZE_FILTER_PROMPT_PANEL = 70;

	private static ProjectionTabUiBinder uiBinder = GWT.create(ProjectionTabUiBinder.class);

	interface ProjectionTabUiBinder extends UiBinder<Widget, ProjectionTab> {
	}

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent;

	@UiField
	Image imgRefresh, imgCube, imgSave, imgEdit, imgCompare, imgCompareBoth;

	private MainPanel mainPanel;
	private ProjectionCubeView projectionPanel;

	private HTMLPanel gridPanel;
	private SimplePanel simpleGridPanel;
	private AbsolutePanel rootFilterPanel;
	private AbsolutePanel dropFilterPanel;
	private HTMLPanel mainDropFilterPanel;
	private FocusPanel panelClickFilter;

	private Label lblFilter;

	private FilterDropController filterDC;
	private BinDragController filterDragController;
	private List<FaWebFilterHTML> filters = new ArrayList<FaWebFilterHTML>();
	private boolean isOpenFilter;

	private boolean isOn = true;
	
	private AbsolutePanel rowPanel;
	private AbsolutePanel colPanel;

	public ProjectionTab(TabManager tabManager, MainPanel mainPanel, FaWebDragController dragCtrl) {
		super(tabManager, "Projections", true);
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);

		dropFilterPanel = new AbsolutePanel();
		dropFilterPanel.addStyleName(CSS_DROP_FILTER_PANEL);
		dropFilterPanel.addStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		
		rowPanel = new AbsolutePanel();
		rowPanel.addStyleName(CSS_DROP_FILTER_PANEL);
		rowPanel.addStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		
		colPanel = new AbsolutePanel();
		colPanel.addStyleName(CSS_DROP_FILTER_PANEL);
		colPanel.addStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		
		RowColDropController rowDc = new RowColDropController(mainPanel, this, rowPanel, true, false);
		dragCtrl.registerDropController(rowDc);
		
		RowColDropController colDc = new RowColDropController(mainPanel, this, colPanel, true, true);
		dragCtrl.registerDropController(colDc);

		filterDC = new FilterDropController(mainPanel, this, dropFilterPanel, true);
		dragCtrl.registerDropController(filterDC);

		Image imgBin = new Image(FaWebImage.INSTANCE.empty_bin64());
		imgBin.addStyleName(CSS_IMG_BIN);

		rootFilterPanel = new AbsolutePanel();

		BinDropController binDC = new BinDropController(this, imgBin, false);
		filterDragController = new BinDragController(rootFilterPanel, false);
		filterDragController.registerDropController(binDC);
		
		Image imgRow = new Image(FaWebImage.INSTANCE.ajouter_ligne_24());
		imgRow.getElement().getStyle().setPosition(Position.ABSOLUTE);
		imgRow.getElement().getStyle().setLeft(5, Unit.PX);
		imgRow.getElement().getStyle().setTop(22, Unit.PX);
		Image imgCol = new Image(FaWebImage.INSTANCE.ajouter_colonne_24());
		imgCol.getElement().getStyle().setPosition(Position.ABSOLUTE);
		imgCol.getElement().getStyle().setLeft(5, Unit.PX);
		imgCol.getElement().getStyle().setTop(71, Unit.PX);
		Image imgFil = new Image(FaWebImage.INSTANCE.filtre_24());
		imgFil.getElement().getStyle().setPosition(Position.ABSOLUTE);
		imgFil.getElement().getStyle().setLeft(5, Unit.PX);
		imgFil.getElement().getStyle().setTop(120, Unit.PX);
		
		imgRow.setTitle(FreeAnalysisWeb.LBL.Rows());
		imgCol.setTitle(FreeAnalysisWeb.LBL.Cols());
		imgFil.setTitle(FreeAnalysisWeb.LBL.filter());

		mainDropFilterPanel = new HTMLPanel("");
		mainDropFilterPanel.addStyleName(CSS_MAIN_DROP_FILTER_PANEL);
		mainDropFilterPanel.setWidth("930px");
		mainDropFilterPanel.add(rowPanel);
		mainDropFilterPanel.add(colPanel);
		mainDropFilterPanel.add(dropFilterPanel);
		mainDropFilterPanel.add(imgBin);
		mainDropFilterPanel.add(imgRow);
		mainDropFilterPanel.add(imgCol);
		mainDropFilterPanel.add(imgFil);

		Image imgFilter = new Image(FaWebImage.INSTANCE.filtre_16());
		imgFilter.addStyleName(CSS_IMG_FILTER_PROMPT);

		lblFilter = new Label();
		lblFilter.addStyleName(CSS_LBL_FILTER);
		lblFilter.setText("Show Filter"/* Bpm_fwr.LBLW.ShowFilterPromptChoice() */);

		HTMLPanel panel = new HTMLPanel("");
		panel.add(imgFilter);
		panel.add(lblFilter);

		panelClickFilter = new FocusPanel();
		panelClickFilter.addStyleName(CSS_PANEL_CLICK_FILTER);
		panelClickFilter.addClickHandler(filterPromptClickHandler);
		panelClickFilter.add(panel);

		rootFilterPanel.add(mainDropFilterPanel);
		rootFilterPanel.add(panelClickFilter);
		DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", -(SIZE_FILTER_PROMPT_PANEL + 1) + "px");
		DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginBottom", "15px");

		simpleGridPanel = new SimplePanel();

		gridPanel = new HTMLPanel("");
		gridPanel.setSize("100%", "100%");
		gridPanel.setStyleName("gridPanelStyle");
		gridPanel.add(rootFilterPanel);
		gridPanel.add(simpleGridPanel);

		panelContent.setWidget(gridPanel);

		if (mainPanel.getActualProjection().getType().equals(Projection.TYPE_WHATIF)) {
		}
		else {
			imgCompare.removeFromParent();
			imgCompareBoth.removeFromParent();
		}
	}

	@UiHandler("imgRefresh")
	public void onRefreshClick(ClickEvent event) {
		if(projectionPanel != null) {
			mainPanel.showWaitPart(true);
	
			FaWebService.Connect.getInstance().refreshService(mainPanel.getKeySession(), true, new AsyncCallback<InfosReport>() {
				public void onSuccess(InfosReport result) {
					mainPanel.setGridFromRCP(result);
					mainPanel.getDisplayPanel().getCubeViewerTab().clearFilterPanel();
	
					FaWebService.Connect.getInstance().getGridCubeForActualQuery(mainPanel.getKeySession(), true, true, new AsyncCallback<GridCube>() {
						@Override
						public void onSuccess(GridCube result) {
	
							projectionPanel.getProjectionGrid().remove(1);
							projectionPanel.getProjectionGrid().add(new CubeView(mainPanel, ProjectionTab.this, result, false, true));
	
							mainPanel.showWaitPart(false);
						}
	
						@Override
						public void onFailure(Throwable caught) {
							mainPanel.showWaitPart(false);
	
						}
					});
	
				}
	
				public void onFailure(Throwable ex) {
					ex.printStackTrace();
					mainPanel.showWaitPart(false);
				}
			});
		}
	}
	
	@UiHandler("imgCube")
	public void onCubeClick(ClickEvent event) {
		if(projectionPanel != null) {
			if (projectionPanel.getPreviousValuePanel() != null) {
				projectionPanel.getPreviousValuePanel().setPreviousCubeView(projectionPanel);
				setUpdateProjectionView(projectionPanel.getPreviousValuePanel());
			}
	
			else {
				Window.alert(FreeAnalysisWeb.LBL.ProjectionNoDrillThrough());
			}
		}
	}

	@UiHandler("imgSave")
	public void onSaveClick(ClickEvent event) {
		mainPanel.showWaitPart(true);
		FaWebService.Connect.getInstance().getRepositories(mainPanel.getKeySession(), FaWebService.PROJECTION, new AsyncCallback<TreeParentDTO>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				mainPanel.showWaitPart(false);
			}

			public void onSuccess(TreeParentDTO result) {
				if (result != null && result instanceof TreeParentDTO) {
					RepositoryContentDialog dial = new RepositoryContentDialog(mainPanel, result, null);
					dial.center();
					mainPanel.showWaitPart(false);
				}
			}
		});
	}

	@UiHandler("imgEdit")
	public void onEditClick(ClickEvent event) {
		DialogCreateProjection dial = new DialogCreateProjection(mainPanel, mainPanel.getActualProjection());
		dial.center();
	}

	@UiHandler("imgCompare")
	public void onCompareClick(ClickEvent event) {
		if(projectionPanel != null) {
			projectionPanel.createCubeSeparator();
			projectionPanel.createOriginalGrid();
		}
	}

	@UiHandler("imgCompareBoth")
	public void onCompareBothClick(ClickEvent event) {
		if(projectionPanel != null) {
			DialogCompareProjection dial = new DialogCompareProjection(mainPanel, this, projectionPanel.getProjectionCube(), projectionPanel.getOriginCube());
			dial.center();
		}
	}
	
	@UiHandler("imgClose")
	public void onCloseClick(ClickEvent event) {
		close();
	}

	public void setProjectionView(ProjectionCubeView projectionCubeView) {
		this.projectionPanel = projectionCubeView;
		panelContent.setWidget(projectionCubeView);
	}

	public void setUpdateProjectionView(UpdateProjectionDataPanel previousValuePanel) {
		panelContent.setWidget(previousValuePanel);
	}

	public void addGrid(CubeView grid) {
		projectionPanel.setProjectionGrid(grid);
	}

	public ProjectionCubeView getProjectionCubeView() {
		return projectionPanel;
	}

	public CubeView getGrid() {
		return projectionPanel.getProjectionCube();
	}

	public void addFilters(List<String> filters) {
		for (String filter : filters) {
			if (!this.filters.contains(filter)) {
				addFilterItem(filter);
			}
		}
	}

	public void setFilters(List<String> filters) {
		this.filters.clear();
		for (String filter : filters) {
			addFilterItem(filter);
		}
	}

	public void addFilter(FaWebFilterHTML filter) {
		this.filters.add(filter);
	}

	public void clearFilters() {
		this.filters.clear();
		clearFilterPanel();
	}

	public void removeFilter(FaWebFilterHTML filter) {
		this.filters.remove(filter);
	}

	public void removeFilter(int index) {
		this.filters.remove(index);
	}

	@Override
	public List<FaWebFilterHTML> getFilters() {
		return filters;
	}

	@Override
	public void addFilterItem(String filter) {
//		dropFilterPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);

		FaWebFilterHTML filterItem = new FaWebFilterHTML(filter);
		filterItem.addStyleName(CSS_RESOURCE);
		dropFilterPanel.add(filterItem);
		filterDragController.makeDraggable(filterItem);

		addFilter(filterItem);
	}

	@Override
	public void trashFilter(Widget widget) {
		if (widget instanceof FaWebFilterHTML) {
			FaWebFilterHTML filter = (FaWebFilterHTML) widget;
			
			if(!filters.contains(filter)) {
				removeRowCol(filter);
				return;
			}
			
			CubeServices.removefilter(filter.getFilter(), mainPanel);
			removeFilter(filter);

			if (filters.isEmpty()) {
				dropFilterPanel.addStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
			}
		}

		widget.removeFromParent();
	}

	public void clearFilterPanel() {
		dropFilterPanel.clear();
		dropFilterPanel.addStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
	}

	public void addParams(List<String> params) {
		for (String param : params) {
			addFilterItem(param);
		}
	}

	public HasFilter getFilterPanel() {
		return filterDC.getFilter();
	}

	public void openCloseFilterPanel() {
		if (!isOpenFilter) {
			ClickEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), this.panelClickFilter);
		}
	}

	public void refreshGrid() {
		CubeView viewerCube = getGrid();
		int vRow = viewerCube.getRowCount();
		int pRow = mainPanel.getInfosReport().getGrid().getNbOfRow();
		if (vRow != pRow) {
			reloadProjectionGrid();
		}
		else {
			int vCol = viewerCube.getCellCount(0);
			int pCol = mainPanel.getInfosReport().getGrid().getNbOfCol();
			if (vCol != pCol) {
				reloadProjectionGrid();
			}
			else {
				if (mainPanel.getDisplayPanel().getCubeViewerTab().getFilters() != null && mainPanel.getDisplayPanel().getCubeViewerTab().getFilters().size() > 0) {
					reloadProjectionGrid();
				}
			}
		}
	}

	private void reloadProjectionGrid() {
		mainPanel.showWaitPart(true);
		boolean refresh = false;
		if (mainPanel.getActualProjection().getType().equals(Projection.TYPE_EXTRAPOLATION)) {
			refresh = true;
		}
		FaWebService.Connect.getInstance().getGridCubeForActualQuery(mainPanel.getKeySession(), true, refresh, new AsyncCallback<GridCube>() {
			@Override
			public void onSuccess(GridCube result) {
				addGrid(new CubeView(mainPanel, ProjectionTab.this, result, false, true));
				mainPanel.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				mainPanel.showWaitPart(false);
			}
		});
	}

	@Override
	public boolean isOn() {
		return isOn;
	}

	private ClickHandler filterPromptClickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Animation slideAnimation = new Animation() {

				@Override
				protected void onUpdate(double progress) {
					if (!isOpenFilter) {
						int filterPanelHeight = mainDropFilterPanel.getOffsetHeight();

						int progressValue = (int) (-(filterPanelHeight) * (1 - progress)) - 2;
						DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", progressValue + "px");
					}
					else {
						int filterPanelHeight = mainDropFilterPanel.getOffsetHeight();

						int progressValue = (int) (-(filterPanelHeight + 3) * (progress));
						DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", progressValue + "px");
					}
				}

				@Override
				protected void onComplete() {
					isOpenFilter = !isOpenFilter;
					if (isOpenFilter) {
						lblFilter.setText("Hide Filter");
					}
					else {
						lblFilter.setText("Show Filter");
					}
				};
			};
			slideAnimation.run(1000);
		}
	};

	@Override
	public boolean charging() {
		return false;
	}

	@Override
	public void addRow(String row) {
//		rowPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		FaWebFilterHTML filterItem = new FaWebFilterHTML(row);
		filterItem.addStyleName(CSS_RESOURCE);
		rowPanel.add(filterItem);
		filterDragController.makeDraggable(filterItem);
	}

	@Override
	public void addCol(String col) {
//		colPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		FaWebFilterHTML filterItem = new FaWebFilterHTML(col);
		filterItem.addStyleName(CSS_RESOURCE);
		colPanel.add(filterItem);
		filterDragController.makeDraggable(filterItem);
	}

	@Override
	public void addRowCol(AbsolutePanel dropTarget, String uname) {
//		rowPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
//		colPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		FaWebFilterHTML filterItem = new FaWebFilterHTML(uname);
		filterItem.addStyleName(CSS_RESOURCE);
		dropTarget.add(filterItem);
		filterDragController.makeDraggable(filterItem);
	}

	@Override
	public void setRowsCols(GridCube gc) {
//		rowPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
//		colPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		
		rowPanel.clear();
		colPanel.clear();
		
		for(String col : gc.getQueryCols()) {
			addCol(col);
		}
		for(String row : gc.getQueryRows()) {
			addRow(row);
		}
		
	}
	
	private void removeRowCol(FaWebFilterHTML filter) {
		List<String> lst = new ArrayList<String>();
		lst.add(filter.getFilter());
		CubeServices.remove(lst, mainPanel);
	}
}
