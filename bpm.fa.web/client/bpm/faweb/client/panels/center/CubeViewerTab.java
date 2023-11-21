package bpm.faweb.client.panels.center;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.FreeAnalysisWeb.TypeDisplay;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.AddPromptsDialog;
import bpm.faweb.client.dialog.DashboardCreationDialog;
import bpm.faweb.client.dialog.FinishSaveDialog;
import bpm.faweb.client.dialog.ListSnapshotViewsDialog;
import bpm.faweb.client.dialog.MdxDialog;
import bpm.faweb.client.dialog.PercentDialog;
import bpm.faweb.client.dialog.SaveDialog;
import bpm.faweb.client.dialog.SearchDialog;
import bpm.faweb.client.dialog.SnapshotCreationDialog;
import bpm.faweb.client.dnd.BinDragController;
import bpm.faweb.client.dnd.BinDropController;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.dnd.FilterDropController;
import bpm.faweb.client.dnd.RowColDropController;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.listeners.CalculOnColListener;
import bpm.faweb.client.listeners.CalculOnRowListener;
import bpm.faweb.client.panels.CubeToolbar;
import bpm.faweb.client.panels.FaWebSelectConstantPanel;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.dialog.DialogSelectProjection;
import bpm.faweb.client.services.CubeServices;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.tree.FaWebTreeItem;
import bpm.faweb.client.utils.FaWebFilterHTML;
import bpm.faweb.client.utils.ForCalcul;
import bpm.faweb.shared.ChartParameters;
import bpm.faweb.shared.MapOptions;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.faweb.shared.infoscube.ItemView;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.InformationsDialog;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.popup.IShare;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.client.services.ReportingService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.gwt.commons.client.utils.ToolsGWT;
import bpm.gwt.commons.client.viewer.dialog.HistorizeOptionDialog;
import bpm.gwt.commons.client.viewer.dialog.MailShareDialog;
import bpm.gwt.commons.shared.InfoShare;
import bpm.gwt.commons.shared.InfoShareCube;
import bpm.gwt.commons.shared.repository.DocumentDefinitionDTO;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.utils.ExportResult;
import bpm.gwt.commons.shared.utils.TypeExport;
import bpm.gwt.commons.shared.utils.TypeShare;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CubeViewerTab extends Tab implements HasFilter, ICubeViewer, IShare {

	private static final String CSS_DROP_FILTER_PANEL = "dropPanel";
	private static final String CSS_DROP_FILTER_PANEL_HEIGHT = "dropPanelHeight";
	private static final String CSS_IMG_BIN = "imgBin";
	private static final String CSS_MAIN_DROP_FILTER_PANEL = "filterMainPanel";
	private static final String CSS_PANEL_CLICK_FILTER = "panelClickFilter";
	private static final String CSS_IMG_FILTER_PROMPT = "imgFilterPrompt";
	private static final String CSS_LBL_FILTER = "labelFilter";
	private static final String CSS_RESOURCE = "resourceHtml";

	private static final int SIZE_FILTER_PROMPT_PANEL_INITIAL = 162;

	private static CubeViewerTabUiBinder uiBinder = GWT.create(CubeViewerTabUiBinder.class);

	interface CubeViewerTabUiBinder extends UiBinder<Widget, CubeViewerTab> {
	}

	interface MyStyle extends CssResource {
		String gridPanel();

		String gridPanelTop();

		String gridPanelTopExpend();

		String panelContent();

		String panelContentViewer();
	}

	@UiField
	MyStyle style;

	@UiField
	SimplePanel panelContent;

	@UiField(provided = true)
	CubeToolbar toolbar;

	private MainPanel mainPanel;

	private HTMLPanel gridPanel;
	private SimplePanel simpleGridPanel;
	private AbsolutePanel rootFilterPanel;
	private AbsolutePanel dropFilterPanel;
	private HTMLPanel mainDropFilterPanel;

	private Label lblFilter;

	private BinDragController filterDragController;
	private List<FaWebFilterHTML> filters = new ArrayList<FaWebFilterHTML>();
	private boolean isOpenFilter;

	private FilterDropController filterDC;

	private FocusPanel panelClickFilter;

	private FaWebSelectConstantPanel selectDiv;
	private FaWebSelectConstantPanel selectMul;

	private AbsolutePanel rowPanel;
	private AbsolutePanel colPanel;

	public CubeViewerTab(TabManager tabManager, MainPanel mainPanel, FaWebDragController dragCtrl) {
		super(tabManager, FreeAnalysisWeb.LBL.cubeViewer(), true);
		this.mainPanel = mainPanel;

		toolbar = new CubeToolbar(this, mainPanel.getInfosUser(), true);
		add(uiBinder.createAndBindUi(this));

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

		rootFilterPanel = new AbsolutePanel();

		BinDropController binDC = new BinDropController(this, imgBin, false);
		filterDragController = new BinDragController(rootFilterPanel, false);
		filterDragController.registerDropController(binDC);

		mainDropFilterPanel = new HTMLPanel("");
		mainDropFilterPanel.addStyleName(CSS_MAIN_DROP_FILTER_PANEL);
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
		lblFilter.setText(FreeAnalysisWeb.LBL.OpenDefinitionPanel());

		HTMLPanel panel = new HTMLPanel("");
		panel.add(imgFilter);
		panel.add(lblFilter);

		panelClickFilter = new FocusPanel();
		panelClickFilter.addStyleName(CSS_PANEL_CLICK_FILTER);
		panelClickFilter.addClickHandler(filterPromptClickHandler);
		panelClickFilter.add(panel);

		rootFilterPanel.add(mainDropFilterPanel);
		rootFilterPanel.add(panelClickFilter);
		DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", -SIZE_FILTER_PROMPT_PANEL_INITIAL + "px");
		DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginBottom", "15px");

		simpleGridPanel = new SimplePanel();
		simpleGridPanel.addStyleName(style.gridPanel());
		simpleGridPanel.addStyleName(style.gridPanelTop());

		gridPanel = new HTMLPanel("");
		gridPanel.setSize("100%", "100%");
		gridPanel.setStyleName("gridPanelStyle");
		gridPanel.add(rootFilterPanel);
		gridPanel.add(simpleGridPanel);

		panelContent.setWidget(gridPanel);

		ClickHandler sumRowHandler = new CalculOnRowListener(mainPanel, ForCalcul.SUM);
		ClickHandler sumColHandler = new CalculOnColListener(mainPanel, ForCalcul.SUM);

		ClickHandler diffRowHandler = new CalculOnRowListener(mainPanel, ForCalcul.DIFF);
		ClickHandler diffColHandler = new CalculOnColListener(mainPanel, ForCalcul.DIFF);

		toolbar.setClickHandlers(sumRowHandler, sumColHandler, diffRowHandler, diffColHandler);
	}

	public void refresh() {
		FaWebService.Connect.getInstance().refreshService(mainPanel.getKeySession(), mainPanel.getDisplayPanel().isOnProjection(), new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				mainPanel.dateFunction = 0;
				mainPanel.setGridFromRCP(result);
				mainPanel.getDisplayPanel().getCubeViewerTab().clearFilterPanel();
				mainPanel.getDisplayPanel().getCubeViewerTab().clearFilters();
			}

			public void onFailure(Throwable ex) {
				ex.printStackTrace();
			}
		});
	}

	public void save() {
		mainPanel.showWaitPart(true);
		int type = FaWebService.FAV;

		FaWebService.Connect.getInstance().getRepositories(mainPanel.getKeySession(), type, new AsyncCallback<TreeParentDTO>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				mainPanel.showWaitPart(false);
			}

			public void onSuccess(TreeParentDTO result) {
				mainPanel.showWaitPart(false);

				if (result != null && result instanceof TreeParentDTO) {
					SaveDialog dial = new SaveDialog(mainPanel, null, null, false);
					dial.center();
				}
			}
		});
	}
	

	public void update() {
		if(mainPanel.getInfosReport().isView()) {
			mainPanel.showWaitPart(true);
			ChartParameters chartParams = null;
			if (mainPanel.getDisplayPanel().getChartTab() != null) {
				chartParams = mainPanel.getDisplayPanel().getChartTab().getChartParameters();
			}
			
			MapOptions mapOptions = null;
			if (mainPanel.getDisplayPanel().getMapTab() != null && mainPanel.getDisplayPanel().getMapTab().getMapOptions() != null) {
				mapOptions = mainPanel.getDisplayPanel().getMapTab().getMapOptions();
			}
			
			FaWebService.Connect.getInstance().updateView(mainPanel.getKeySession(), chartParams, mainPanel.getDisplayPanel().getGridHtml(), mainPanel.getCalculs(), mainPanel.getInfosReport(), mapOptions, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					mainPanel.showWaitPart(false);
					FinishSaveDialog dial = new FinishSaveDialog(FreeAnalysisWeb.LBL.updateKo(), false);
					dial.center();
				}

				@Override
				public void onSuccess(Void result) {
					mainPanel.showWaitPart(false);
					FinishSaveDialog dial = new FinishSaveDialog(FreeAnalysisWeb.LBL.updateOk(), true);
					dial.center();
					
				}
				
			});
		}
		else {
			InformationsDialog dial = new InformationsDialog("Informations", "Confirm", "Cancel", FreeAnalysisWeb.LBL.noViewOpen(), false);
			dial.center();
			mainPanel.showWaitPart(false);
		}
		
	}

	public void export() {
		// PreferencesDialog dial = new PreferencesDialog(false, mainPanel);
		// dial.center();
	}

	public void projection() {
		if (mainPanel.getInfosReport().isProjectionAllowed()) {
			DialogSelectProjection dial = new DialogSelectProjection(mainPanel);
			dial.center();
		}
		else {
			InformationsDialog dial = new InformationsDialog("Informations", "Confirm", "Cancel", "You are not allowed to make projections.", false);
			dial.center();
		}
	}

	public void dash() {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().setViewsService(mainPanel.getKeySession(), new AsyncCallback<List<ItemView>>() {
			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);

				caught.printStackTrace();
			}

			public void onSuccess(List<ItemView> views) {
				mainPanel.showWaitPart(false);

				DashboardCreationDialog dial = new DashboardCreationDialog(mainPanel, views);
				dial.center();
			}
		});
	}

	public void snapshot() {
		SnapshotCreationDialog dial = new SnapshotCreationDialog(mainPanel);
		dial.center();
	}

	public void listSnapshot() {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().getSnapshots(mainPanel.getKeySession(), new AsyncCallback<List<ItemView>>() {
			public void onSuccess(List<ItemView> result) {
				mainPanel.showWaitPart(false);

				ListSnapshotViewsDialog dial = new ListSnapshotViewsDialog(mainPanel, result);
				dial.center();
			}

			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();
			}
		});
	}

	public void undo() {
		mainPanel.showWaitPart(true);
		Object o = mainPanel.getMakemodif().Undo();
		mainPanel.setUndo(true);
		if (o != null) {
			mainPanel.setGridFromRCP(o);
		}
		else {
			mainPanel.showWaitPart(false);
		}
	}

	public void redo() {
		mainPanel.showWaitPart(true);
		Object o = mainPanel.getMakemodif().Redo();
		if (o != null) {
			mainPanel.setGridFromRCP(o);
		}
		else {
			mainPanel.showWaitPart(false);
		}
	}

	public void swap() {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().swapAxesService(mainPanel.getKeySession(), mainPanel.getDisplayPanel().isOnProjection(), new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				mainPanel.setGridFromRCP(result);
			}

			public void onFailure(Throwable ex) {
				mainPanel.showWaitPart(false);

				ex.printStackTrace();
			}
		});
	}

	public void percent() {
		PercentDialog dial = new PercentDialog(mainPanel);
		dial.center();
	}

	public void delete() {
		CubeServices.remove(mainPanel.getNavigationPanel().getItemSelected(), mainPanel);
		deselect();
		mainPanel.getNavigationPanel().clearItemSelected();
	}

	public void mdx() {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().mdxService(mainPanel.getKeySession(), new AsyncCallback<String>() {
			public void onSuccess(String result) {
				MdxDialog dial = new MdxDialog((result).replace("&amp;", "&"));
				dial.center();

				mainPanel.showWaitPart(false);
			}

			public void onFailure(Throwable ex) {
				ex.printStackTrace();

				mainPanel.showWaitPart(false);
			}
		});
	}

	public void onOff() {
		mainPanel.setGridFromLocal(this, mainPanel.isCalcul(), mainPanel.getInfosReport().getGrid());
	}

	public void totals() {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().setShowTotals(mainPanel.getKeySession(), new AsyncCallback<InfosReport>() {
			public void onSuccess(InfosReport result) {
				mainPanel.setGridFromRCP(result);
			}

			public void onFailure(Throwable caught) {
				mainPanel.showWaitPart(false);
				caught.printStackTrace();
			}
		});
	}

	public void onNull() {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().showEmptyService(mainPanel.getKeySession(), mainPanel.getDisplayPanel().isOnProjection(), new AsyncCallback<InfosReport>() {

			@Override
			public void onSuccess(InfosReport result) {
				if (result != null) {
					mainPanel.setGridFromRCP(result);
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "showEmpty service failed");
				}
				mainPanel.showWaitPart(false);
			}

			public void onFailure(Throwable ex) {
				mainPanel.showWaitPart(false);

				ex.printStackTrace();
			}
		});
	}

	public void properties() {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().propertiesService(mainPanel.getKeySession(), new AsyncCallback<InfosReport>() {

			@Override
			public void onSuccess(InfosReport infosReport) {
				if (infosReport != null) {
					mainPanel.setGridFromRCP(infosReport);
				}
				else {
					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "properties service failed");
				}

				mainPanel.showWaitPart(false);
			}

			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();

				mainPanel.showWaitPart(false);
			}
		});
	}

	public void addCol() {
		add(mainPanel.getNavigationPanel().getItemSelected(), true);
	}

	public void addRow() {
		add(mainPanel.getNavigationPanel().getItemSelected(), false);
	}

	public void search() {
		SearchDialog dial = new SearchDialog(mainPanel);
		dial.center();
	}

	public void filters() {
		List<String> filtersSelected = mainPanel.getNavigationPanel().getItemSelected();
		List<String> filters = new ArrayList<String>();
		LOOK: for (String f : filtersSelected) {
			for (FaWebFilterHTML fi : mainPanel.getDisplayPanel().getCubeViewerTab().getFilters()) {
				if (fi.getFilter().equals(f)) {
					continue LOOK;
				}
			}
			filters.add(f);
		}

		if (!filters.isEmpty()) {
			CubeServices.filter(filters, mainPanel, mainPanel.getDisplayPanel().getCubeViewerTab().getFilterPanel());
		}

		deselect();
		mainPanel.getDisplayPanel().getCubeViewerTab().openCloseFilterPanel();
	}

	public void prompts() {
		AddPromptsDialog dial = new AddPromptsDialog(mainPanel);
		dial.center();
	}

	public void calculator(boolean showCalculator) {
		mainPanel.setCalculs(new ArrayList<Calcul>());

		if (showCalculator) {
			mainPanel.setCalcul(true);
			mainPanel.setGridFromLocal(this, true, mainPanel.getInfosReport().getGrid());
		}
		else {
			mainPanel.setCalcul(false);
			mainPanel.setGridFromLocal(this, false, mainPanel.getInfosReport().getGrid());
		}
	}

	public void div(ClickEvent event) {
		if (selectDiv == null || !selectDiv.isDisplay()) {
			if (selectMul != null && selectMul.isDisplay()) {
				selectMul.setDisplay(false);
				selectMul.removeFromParent();
			}

			int left = ((Image) event.getSource()).getAbsoluteLeft() + ((Image) event.getSource()).getOffsetWidth() - 88;
			int top = ((Image) event.getSource()).getAbsoluteTop() + ((Image) event.getSource()).getOffsetHeight() + 4;

			selectDiv = new FaWebSelectConstantPanel(mainPanel, ForCalcul.DIV, left, top);
			selectDiv.setDisplay(true);
			RootPanel.get().add(selectDiv);
		}
		else {
			selectDiv.setDisplay(false);
			selectDiv.removeFromParent();
		}
	}

	public void multi(ClickEvent event) {
		if (selectMul == null || !selectMul.isDisplay()) {
			if (selectDiv != null && selectDiv.isDisplay()) {
				selectDiv.setDisplay(false);
				selectDiv.removeFromParent();
			}

			int left = ((Image) event.getSource()).getAbsoluteLeft() + ((Image) event.getSource()).getOffsetWidth() - 78;
			int top = ((Image) event.getSource()).getAbsoluteTop() + ((Image) event.getSource()).getOffsetHeight() + 4;

			selectMul = new FaWebSelectConstantPanel(mainPanel, ForCalcul.MUL, left, top);
			selectMul.setDisplay(true);
			RootPanel.get().add(selectMul);
		}
		else {
			selectMul.setDisplay(false);
			selectMul.removeFromParent();
		}
	}

	private void add(List<String> items, boolean col) {
		mainPanel.showWaitPart(true);

		FaWebService.Connect.getInstance().addService(mainPanel.getKeySession(), items, col, new AsyncCallback<InfosReport>() {

			@Override
			public void onSuccess(InfosReport result) {
				if (result != null) {
					mainPanel.setGridFromRCP(result);
					mainPanel.getNavigationPanel().clearItemSelected();
				}
				else {
					mainPanel.getNavigationPanel().clearItemSelected();

					MessageHelper.openMessageDialog(FreeAnalysisWeb.LBL.Error(), "add service failed");
				}
				mainPanel.showWaitPart(false);
				deselect();
			}

			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();

				mainPanel.getNavigationPanel().clearItemSelected();
				mainPanel.showWaitPart(false);
				deselect();
			}
		});
	}

	public void deselect() {
		mainPanel.getNavigationPanel().getItemSelected().clear();
		Tree t = mainPanel.getNavigationPanel().getDimensionPanel().getTree();
		for (int k = 0; k < t.getItemCount(); k++) {
			deselectAll(t.getItem(k));
		}

		Tree m = mainPanel.getNavigationPanel().getMeasurePanel().getTree();

		for (int j = 0; j < m.getItemCount(); j++) {
			deselectAll(m.getItem(j));
		}

	}

	private void deselectAll(TreeItem item) {

		for (int k = 0; k < item.getChildCount(); k++) {
			FaWebTreeItem curr = (FaWebTreeItem) item.getChild(k);
			HorizontalPanel hp = (HorizontalPanel) curr.getWidget();
			CheckBox cb = (CheckBox) hp.getWidget(0);
			if (cb.getValue()) {
				cb.setValue(false);
			}
			deselectAll(curr);
		}

	}

	@Override
	public void addGrid(CubeView grid) {
		simpleGridPanel.clear();
		simpleGridPanel.add(grid);
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
		// dropFilterPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);

		FaWebFilterHTML filterItem = new FaWebFilterHTML(filter);
		filterItem.showCaption();
		filterItem.addStyleName(CSS_RESOURCE);
		dropFilterPanel.add(filterItem);
		filterDragController.makeDraggable(filterItem);

		addFilter(filterItem);
	}

	public void addParams(List<String> params) {
		for (String param : params) {
			addFilterItem(param);
		}
	}

	@Override
	public void trashFilter(Widget widget) {
		if (widget instanceof FaWebFilterHTML) {
			FaWebFilterHTML filter = (FaWebFilterHTML) widget;

			if (!filters.contains(filter)) {
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

	@Override
	protected void onDetach() {
		filterDragController.unregisterDropControllers();
		super.onDetach();
	}

	private ClickHandler filterPromptClickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Animation slideAnimation = new Animation() {

				@Override
				protected void onUpdate(double progress) {
					if (!isOpenFilter) {
						int filterPanelHeight = mainDropFilterPanel.getOffsetHeight();

						int progressValue = (int) (-(filterPanelHeight) * (1 - progress)) - 3;
						DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", progressValue + "px");

						int progressValueTop = (int) (filterPanelHeight * progress) + 35;
						DOM.setStyleAttribute(simpleGridPanel.getElement(), "top", progressValueTop + "px");
					}
					else {
						int filterPanelHeight = mainDropFilterPanel.getOffsetHeight();

						int progressValue = (int) (-(filterPanelHeight + 3) * (progress));
						DOM.setStyleAttribute(rootFilterPanel.getElement(), "marginTop", progressValue + "px");

						int progressValueTop = (int) ((filterPanelHeight) * (1 - progress)) + 35;
						DOM.setStyleAttribute(simpleGridPanel.getElement(), "top", progressValueTop + "px");
					}
				}

				@Override
				protected void onComplete() {
					isOpenFilter = !isOpenFilter;
					if (isOpenFilter) {
						lblFilter.setText(FreeAnalysisWeb.LBL.CloseDefinitionPanel());
					}
					else {
						lblFilter.setText(FreeAnalysisWeb.LBL.OpenDefinitionPanel());
					}
				};
			};
			slideAnimation.run(1000);
		}
	};

	public String getGridHtml() {
		return simpleGridPanel.getElement().getInnerHTML();
	}

	public void clearFilterPanel() {
		dropFilterPanel.clear();
		dropFilterPanel.addStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
	}

	public HasFilter getFilterPanel() {
		return filterDC.getFilter();
	}

	public void openCloseFilterPanel() {
		if (!isOpenFilter) {
			// panelClickFilter.fireEvent(event)
			ClickEvent.fireNativeEvent(Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false), this.panelClickFilter);
		}
	}

	public CubeView getGrid() {
		return (CubeView) simpleGridPanel.getWidget();
	}

	public void refreshGrid() {
		CubeView viewerCube = getGrid();
		int vRow = viewerCube.getRowCount();
		int pRow = mainPanel.getInfosReport().getGrid().getNbOfRow();
		if (vRow != pRow) {
			reloadCubeGrid();
		}
		else {
			int vCol = viewerCube.getCellCount(0);
			int pCol = mainPanel.getInfosReport().getGrid().getNbOfCol();
			if (vCol != pCol) {
				reloadCubeGrid();
			}
		}
	}

	private void reloadCubeGrid() {
		mainPanel.showWaitPart(true);
		boolean refresh = true;
		if (mainPanel.getActualProjection().getType().equals(Projection.TYPE_WHATIF)) {
			refresh = false;
		}

		FaWebService.Connect.getInstance().getGridCubeForActualQuery(mainPanel.getKeySession(), false, refresh, new AsyncCallback<GridCube>() {

			@Override
			public void onSuccess(GridCube result) {
				addGrid(new CubeView(mainPanel, CubeViewerTab.this, result, false, false));
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
		if (mainPanel.getViewerToolbar() != null) {
			return mainPanel.getViewerToolbar().isOn();
		}
		else {
			return toolbar.isOn();
		}
	}

	@Override
	public boolean charging() {
		return false;
	}

	public void changeDisplay(TypeDisplay display) {
		toolbar.setVisible(false);

		panelContent.removeStyleName(style.panelContent());
		panelContent.addStyleName(style.panelContentViewer());
	}

	@Override
	public void addRow(String row) {
		// rowPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		FaWebFilterHTML filterItem = new FaWebFilterHTML(row);
		filterItem.addStyleName(CSS_RESOURCE);
		rowPanel.add(filterItem);
		filterDragController.makeDraggable(filterItem);
	}

	@Override
	public void addCol(String col) {
		// colPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		FaWebFilterHTML filterItem = new FaWebFilterHTML(col);
		filterItem.addStyleName(CSS_RESOURCE);
		colPanel.add(filterItem);
		filterDragController.makeDraggable(filterItem);
	}

	@Override
	public void addRowCol(AbsolutePanel dropTarget, String uname) {
		// rowPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		// colPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		FaWebFilterHTML filterItem = new FaWebFilterHTML(uname);
		filterItem.addStyleName(CSS_RESOURCE);
		dropTarget.add(filterItem);
		filterDragController.makeDraggable(filterItem);
	}

	@Override
	public void setRowsCols(GridCube gc) {
		// rowPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);
		// colPanel.removeStyleName(CSS_DROP_FILTER_PANEL_HEIGHT);

		rowPanel.clear();
		colPanel.clear();

		for (String col : gc.getQueryCols()) {
			addCol(col);
		}
		for (String row : gc.getQueryRows()) {
			addRow(row);
		}

	}

	private void removeRowCol(FaWebFilterHTML filter) {
		List<String> lst = new ArrayList<String>();
		lst.add(filter.getFilter());
		CubeServices.remove(lst, mainPanel);
	}

	@Override
	public void openShare(TypeShare typeShare) {
		switch (typeShare) {
		case AKLABOX:
			// AklaboxShareDialog dial = new
			// AklaboxShareDialog(itemInfo.getItem(), reportKey,
			// itemInfo.getOutputs());
			// dial.center();
			break;
		case EMAIL:
		case EXPORT:
			MailShareDialog mailDialog = new MailShareDialog(CubeViewerTab.this, mainPanel.getInfosReport().getCubeName(), mainPanel.getInfosUser().getAvailableGroups(), typeShare, TypeExport.CUBE, null);
			mailDialog.center();
			break;
//		case AIR:
//			AirShareDialog airDialog = new AirShareDialog(CubeViewerTab.this, mainPanel.getKeySession(), mainPanel.getInfosUser().getUser().getId());
//			airDialog.center();
//			break;

		default:
			break;
		}
	}

	@Override
	public void share(final InfoShare infoShare) {
		if (infoShare instanceof InfoShareCube) {
			String gridHtml = mainPanel.getDisplayPanel().getGridHtml();

			final InfoShareCube infoShareCube = (InfoShareCube) infoShare;

			String chartData = null;
			if (infoShareCube.exportChart() && mainPanel.getDisplayPanel().getChartTab().getChartDatas() != null) {
				chartData = mainPanel.getDisplayPanel().getChartTab().getChartDatas();
			}

			if (infoShare.getTypeExport() == TypeExport.CUBE && (infoShare.getTypeShare() == TypeShare.EMAIL || infoShare.getTypeShare() == TypeShare.EXPORT || infoShare.getTypeShare() == TypeShare.GED)) {
				showWaitPart(true);

				FaWebService.Connect.getInstance().exportCube(infoShareCube, chartData, gridHtml, new AsyncCallback<ExportResult>() {

					@Override
					public void onFailure(Throwable ex) {
						showWaitPart(false);

						ex.printStackTrace();

						ExceptionManager.getInstance().handleException(ex, "Export service failed");
					}

					@Override
					public void onSuccess(final ExportResult result) {
						showWaitPart(false);
						mainPanel.getDisplayPanel().selectPreviousTab();

						if (infoShare.getTypeShare() == TypeShare.EXPORT) {
							String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.PREVIEW_REPORT_SERVLET_ANALYSIS + "?" + CommonConstants.REPORT_HASHMAP_NAME + "=" + result.getReportName() + "&" + CommonConstants.REPORT_OUTPUT + "=" + infoShareCube.getFormat();
							ToolsGWT.doRedirect(fullUrl);
						}
						else if (infoShare.getTypeShare() == TypeShare.GED) {
							
							CommonService.Connect.getInstance().getItemById(mainPanel.getInfosReport().getFasdId(), new GwtCallbackWrapper<RepositoryItem>(CubeViewerTab.this, true, false) {
								
								@Override
								public void onSuccess(final RepositoryItem res) {
									
									ReportingService.Connect.getInstance().getAvailableDocuments(mainPanel.getInfosReport().getFasdId(), new GwtCallbackWrapper<List<DocumentDefinitionDTO>>(CubeViewerTab.this, false, true) {

										@Override
										public void onFailure(Throwable caught) {
											ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.FailedGetHisto());

											final List<String> formats = new ArrayList<String>();
											formats.add(infoShareCube.getFormat());

											PortailRepositoryItem it = new PortailRepositoryItem(res, IRepositoryApi.FASD);
											HistorizeOptionDialog dial = new HistorizeOptionDialog(CubeViewerTab.this, it, result.getReportName(), formats, mainPanel.getInfosUser().getAvailableGroups(), null);
											dial.center();
										}

										@Override
										public void onSuccess(final List<DocumentDefinitionDTO> histoDocs) {
											final List<String> formats = new ArrayList<String>();
											formats.add(infoShareCube.getFormat());
											
											PortailRepositoryItem it = new PortailRepositoryItem(res, IRepositoryApi.FASD);
											HistorizeOptionDialog dial = new HistorizeOptionDialog(CubeViewerTab.this, it, result.getReportName(), formats, mainPanel.getInfosUser().getAvailableGroups(), histoDocs);
											dial.center();
										}
									}.getAsyncCallback());
								}
							}.getAsyncCallback());
						}
						else if (infoShare.getTypeShare() == TypeShare.EMAIL) {
							MessageHelper.openMessageMailResult(LabelsConstants.lblCnst.Information(), result);
						}
					}
				});
			}
		}
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public void loadComment(CubeToolbar toolbar, Integer fasdId) {
		if (toolbar != null) {
			toolbar.loadComment(fasdId);
		}
		else {
			this.toolbar.loadComment(fasdId);
		}
	}

}
