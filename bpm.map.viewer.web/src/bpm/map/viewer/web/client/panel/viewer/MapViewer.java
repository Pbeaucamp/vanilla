package bpm.map.viewer.web.client.panel.viewer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.ComplexMap;
import bpm.fm.api.model.ComplexMapLevel;
import bpm.fm.api.model.ComplexMapMetric;
import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.LevelMember;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.RepositoryDirectoryDialog;
import bpm.gwt.commons.client.dialog.SelectLayersDialog;
import bpm.gwt.commons.client.free.metrics.DateTimePickerDialog;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.map.viewer.web.client.Bpm_map_viewer_web;
import bpm.map.viewer.web.client.Bpm_map_viewer_web.TypeDisplay;
import bpm.map.viewer.web.client.UserSession;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.dialogs.ExportMapDial;
import bpm.map.viewer.web.client.dialogs.ManageMapFilters;
import bpm.map.viewer.web.client.panel.ViewerPanel;
import bpm.map.viewer.web.client.services.MapViewerService;
import bpm.map.viewer.web.client.utils.LevelTreeItem;
import bpm.vanilla.map.core.design.MapInformation;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.KpiMapBean;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class MapViewer extends Composite{
	private static MapPanelUiBinder uiBinder = GWT.create(MapPanelUiBinder.class);
	
	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);

	interface MapPanelUiBinder extends UiBinder<Widget, MapViewer> {
	}
	
	interface MyStyle extends CssResource {

		String layersMenu();
	}
	
	
	
	@UiField
	HTMLPanel axisPanel, metricsPanel, mapPanel, toolbarMap, centerPanel, valeurPanel, evolutionPanel, calendarPanel,
				filtersPanel, filtersContent;
	
	@UiField
	SimplePanel metricsDataGrid;
	
	@UiField
	Label lblMaps, lblmetricsPanel, lblaxisPanel ,lblDateTitle, lblDateStartTitle, lblDateEndTitle, lblfilterPanel;
	
	@UiField
	Tree treeAxis;
	
	@UiField
	Button filterButton;
	
	@UiField
	ListBox lstMaps;
	
	@UiField
	Image btnBackground, btnLoad, btnMapExport, btnRepExport, imgFilterCollapse, imgFilterExpand;
	
	@UiField
	RadioButton rdCalendarValeur, rdCalendarEvolution;
	
	@UiField
	MyStyle style;
	
	private ViewerPanel viewer;
	private MapPanel mapContent;
	private List<ComplexMap> mapsList;
	private ComplexMap selectedMap;
	private Level selectedLevel;
	private Metric selectedMetric;
//	private List<Axis> axis;
	private List<ComplexMapLevel> levels = new ArrayList<ComplexMapLevel>();
	private List<ComplexMapMetric> metrics = new ArrayList<ComplexMapMetric>();
	
	private ListDataProvider<ComplexMapMetric> dataProviderMetric;
	private ListHandler<ComplexMapMetric> sortHandlerMetric;
	private MultiSelectionModel<ComplexMapMetric> selectionModelMetric;
	
	private DateTimePickerDialog datePickerDialog;
	private DateTimePickerDialog datePickerStartDialog;
	private DateTimePickerDialog datePickerEndDialog;
	
	private ManageMapFilters managemapfilters;
//	private ManageDataObservatory managedataobservatory;
	private List<LevelMember> filterLevelMembers = new ArrayList<LevelMember>();
	
	private String webappUrl;
	
	private MenuBar layersMenu;
	
	private Date startDate;
	private Date endDate;
	
	public MapViewer(ViewerPanel viewer, TypeDisplay display) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.viewer = viewer;
		this.mapsList = this.viewer.getMapsList();
		this.webappUrl = UserSession.getInstance().getWebappUrl();
		
		buildToolbar(display);
		metricsDataGrid.add(MetricGridData());
		buildFloatMetrics();
		
		buildFloatAxis();
		buildFloatCalendars();
		setLabels();
		

//		//selectiondefault
//		for(int i = 0; i<treeAxis.getItemCount(); i++){
//			TreeItem axe = treeAxis.getItem(i);
//			for(int j=0; j<axe.getChildCount(); j++){
//				
//				LevelTreeItem item = (LevelTreeItem) axe.getChild(j);
//
//				if(item.getLevel().getLevel().getName().equals("Commune")){
//					item.setCheckBoxState(true);
//					levels.add(item.getLevel());
//				}
//			}
//		}
		
//		selectionModelMetric.setSelected(selectedMap.getComplexMetrics().get(0), true);
		
		loadMap(new ArrayList<ComplexObjectViewer>(), getLayers());
		
		createLayersMenu();
		
		setCalendarPanel(0);
		rdCalendarValeur.setValue(true);
		
		lblfilterPanel.setText(bpm.gwt.commons.client.I18N.LabelsConstants.lblCnst.Filters());
		onExpandClick(null);
		
		Timer t = new Timer() {
			
			@Override
			public void run() {
				//load map elements from portal
				if(Bpm_map_viewer_web.get().getProps().get("bpm.kpi.levels") != null) {
					String[] ids = Bpm_map_viewer_web.get().getProps().get("bpm.kpi.levels").split(",");
					
					for(String id : ids) {
						for(ComplexMapLevel l : selectedMap.getComplexLevels()) {
							if(l.getId() == Integer.parseInt(id)) {
								levels.add(l);
								for(int i = 0 ; i < treeAxis.getItemCount() ; i++) {
									TreeItem axis = treeAxis.getItem(i);
									for(int j = 0 ; i < axis.getChildCount() ; j++) {
										try {
											LevelTreeItem lti = (LevelTreeItem) axis.getChild(j);
											if(lti.getLevel().getId() == l.getId()) {
												lti.setCheckBoxState(true);
												break;
											}
										} catch (Exception e) {
										}
									}
								}
								break;
							}
						}
					}
						
					
					if(Bpm_map_viewer_web.get().getProps().get("bpm.kpi.metrics") != null) {
						ids = Bpm_map_viewer_web.get().getProps().get("bpm.kpi.metrics").split(",");
						
						for(String id : ids) {
							for(ComplexMapMetric l : selectedMap.getComplexMetrics()) {
								if(l.getId() == Integer.parseInt(id)) {
									metrics.add(l);
									selectionModelMetric.setSelected(l, true);
									break;
								}
							}
						}
					}
					if(Bpm_map_viewer_web.get().getProps().get("bpm.kpi.startdate") != null) {
						startDate = DateTimeFormat.getFormat("yyyy-MM-dd").parse(Bpm_map_viewer_web.get().getProps().get("bpm.kpi.startdate"));
					}
					if(Bpm_map_viewer_web.get().getProps().get("bpm.kpi.enddate") != null) {
						endDate = DateTimeFormat.getFormat("yyyy-MM-dd").parse(Bpm_map_viewer_web.get().getProps().get("bpm.kpi.enddate"));
					}
				
					generateMetricMap(metrics, levels, startDate);
					
				}
			}
		};
		t.schedule(5000);
		

	}

	@UiHandler("lstMaps")
	public void onDataSetChange(ChangeEvent event) {
		selectedMap = mapsList.get(lstMaps.getSelectedIndex());
		
		buildFloatMetrics();
		buildFloatAxis();
		buildFloatCalendars();
	}
	
	@UiHandler("btnLayers")
	public void onLayers(ClickEvent event) {
		MapInformation mapInformation = selectedMap.getMapInformation();
		
		final SelectLayersDialog dial = new SelectLayersDialog(mapInformation);
		dial.center();
		dial.addCloseHandler(new CloseHandler<PopupPanel>() {
			
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (dial.isConfirm()) {
					MapInformation mapInformation = dial.getMapInformation();
					selectedMap.setMapInformation(mapInformation);
					
					saveAndRefreshMap(selectedMap);
				}
			}
		});
	}

	protected void saveAndRefreshMap(ComplexMap map) {
		MapViewerService.Connect.getInstance().addOrEditMap(map, new GwtCallbackWrapper<Void>(viewer, true, true) {

			@Override
			public void onSuccess(Void result) {
				updateMap();
			}
		}.getAsyncCallback());
	}

	private void buildToolbar(TypeDisplay display) {
		
		if(mapsList.size() > 0){
			for(ComplexMap map : mapsList){
				lstMaps.addItem(map.getName());
			}
			if(Bpm_map_viewer_web.get().getIdMap() != 0){
				for(ComplexMap map : mapsList){
					if(map.getId() == Bpm_map_viewer_web.get().getIdMap()){
						lstMaps.setItemSelected(mapsList.indexOf(map), true);
						selectedMap = map;
						break;
					}
					
				}
				
			} else {
				lstMaps.setItemSelected(0, true);
				selectedMap = mapsList.get(lstMaps.getSelectedIndex());
			}
		
			
			filterButton.setEnabled(true);
			btnMapExport.setVisible(true);
			btnRepExport.setVisible(true);
			
		} else {
			lstMaps.addItem(lblCnst.NoMap());
			
			lstMaps.setItemSelected(0, true);
			selectedMap = null;
			
			filterButton.setEnabled(false);
			btnMapExport.setVisible(false);
			btnRepExport.setVisible(false);
		}
		
		if(display == TypeDisplay.VIEWER){
			lstMaps.setVisible(false);
			lblMaps.setVisible(false);
			btnRepExport.setVisible(false);
		}
	}

	private void setLabels() {
		lblaxisPanel.setText(lblCnst.AxisName());
		lblmetricsPanel.setText(lblCnst.MetricName());
		lblMaps.setText(lblCnst.MapList());
		
	}

	private void buildFloatAxis() {
		if(mapsList.size() > 0){
			List<Axis> axis = selectedMap.getAxis();
			treeAxis.clear();
			for (Axis axe : axis) {
				TreeItem item = new TreeItem();
				item.setHTML(axe.getName());
				treeAxis.addItem(item);
				for(ComplexMapLevel cpx : selectedMap.getComplexLevels()){
					if(cpx.getIdAxis() == axe.getId()){
						boolean filtered = false;
						for(LevelMember lev : filterLevelMembers){
							if(lev.getLevel().getId() == cpx.getIdLevel()){
								filtered = true;
								break;
							}
						}
						LevelTreeItem branch = new LevelTreeItem(this, cpx, false, filtered);
						item.addItem(branch);
					}
				}
	//			for(Level level : axe.getChildren()){
	//				LevelTreeItem branch = new LevelTreeItem(this, level, false);
	//				item.addItem(branch);
	//			}
			}
		} else {
			treeAxis.clear();
			treeAxis.addTextItem(lblCnst.NoResult());
		}
		
		
		
	}

	private void buildFloatMetrics() {
		if(mapsList.size() > 0){
			dataProviderMetric.setList(new ArrayList<ComplexMapMetric>());
			dataProviderMetric.setList(selectedMap.getComplexMetrics());
		} else {
			dataProviderMetric.setList(new ArrayList<ComplexMapMetric>());
		}
	}
	
	private void buildFloatCalendars() {
		Date initialDate = UserSession.getInstance().getDefaultDate();
		initText(initialDate);
		//homeView.setFilterDate(initialDate);
		datePickerDialog = new DateTimePickerDialog(initialDate, new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Date date = datePickerDialog.getSelectedDate();
				if(levels.size() > 0 && viewer.getActionDate() == ViewerPanel.ActionDate.VALEUR){
					generateMetricMap(metrics, levels, date);
				}
				String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(date);
				lblDateTitle.setText(dateString);
			}
		});
		
		datePickerStartDialog = new DateTimePickerDialog(initialDate, new ClickHandler() {
					
			@Override
			public void onClick(ClickEvent event) {
				Date date = datePickerStartDialog.getSelectedDate();
//				if(levels.size() > 0 && actionDate == ActionDate.VALEUR){
//					generateMetricMap(metrics, levels, date, datePickerEndDialog.getSelectedDate());
//				}
				String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(date);
				lblDateStartTitle.setText(dateString);
			}
		});
		
		datePickerEndDialog = new DateTimePickerDialog(initialDate, new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Date date = datePickerEndDialog.getSelectedDate();
//				if(levels.size() > 0 && actionDate == ActionDate.VALEUR){
//					generateMetricMap(metrics, levels, datePickerStartDialog.getSelectedDate(), date);
//				}
				String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(date);
				lblDateEndTitle.setText(dateString);
			}
		});
		
//		datePickerDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
//			
//			@Override
//			public void onClose(CloseEvent<PopupPanel> event) {
//				Date date = datePickerDialog.getSelectedDate();
//				if(levels.size() > 0 && metrics.size() > 0){
//					generateMetricMap(metrics, levels, date);
//				}
//				String dateString = DateTimeFormat.getFormat("dd MMMM yyyy HH:mm").format(date);
//				lblDateTitle.setText(dateString);
//				//homeView.setFilterDate(date);
//			}
//		});
	}
	
	@UiHandler("lblDatePicker")
	void onChooseDate(ClickEvent e){
		datePickerDialog.center();
//		popup.setAutoHideEnabled(true);
//		popup.setAnimationEnabled(true);
//		popup.setGlassEnabled(true);
//		popup.center();
	}
	

	@UiHandler("lblDateStartPicker")
	void onChooseStartDate(ClickEvent e){
		datePickerStartDialog.center();
	}
	
	@UiHandler("lblDateEndPicker")
	void onChooseEndDate(ClickEvent e){
		datePickerEndDialog.center();
	}
	
	@UiHandler("filterButton")
	void onFilterClick(ClickEvent e){
		ClickHandler okFilterHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				updateFilters(managemapfilters.getFilteredElements());
				managemapfilters.hide();
				if(levels.size() > 0){
					if(viewer.getActionDate() == ViewerPanel.ActionDate.VALEUR){
						generateMetricMap(metrics, levels, datePickerDialog.getSelectedDate());
					} else {
						generateMetricMap(metrics, levels, datePickerStartDialog.getSelectedDate(), datePickerEndDialog.getSelectedDate());
					}
					
				}
			}
		};
		managemapfilters = new ManageMapFilters(this, okFilterHandler);
		managemapfilters.center();

	}
	
//	@UiHandler("fileButton")
//	void onFileClick(ClickEvent e){
//		managedataobservatory.center();
//	}
	@UiHandler("btnMapExport")
	void onExportClick(ClickEvent e){
		ExportMapDial dial = new ExportMapDial(this);
		dial.center();
	}
	
	@UiHandler("btnRepExport")
	void onRepExport(ClickEvent event) {
		KpiMapBean bean = new KpiMapBean();
		bean.setMapId(selectedMap.getId());
		
		List<Integer> levelIds = new ArrayList<Integer>();
		List<Integer> metricsIds = new ArrayList<Integer>();
		
		for(ComplexMapLevel l : levels) {
			levelIds.add(l.getId());
		}
		
		for(ComplexMapMetric l : metrics) {
			metricsIds.add(l.getId());
		}
		
		bean.setStartDate(startDate);
		bean.setEndDate(endDate);
		bean.setLevelIds(levelIds);
		bean.setMetricsIds(metricsIds);
		
		RepositoryDirectoryDialog dial = new RepositoryDirectoryDialog(IRepositoryApi.KPI_MAP, UserSession.getInstance().getGroups(), bean);
		dial.center();
	}
	
	public void generateMetricMap(List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, Date filterDate) {
		this.startDate = filterDate;
		viewer.showWaitPart(true);
//		// pour l'instant
//		for(ComplexMapLevel lev : levels){
//			if(lev.getLevel().getName().equals("Commune")){
//				levels.clear();
//				levels.add(lev);
//				break;
//			}
//		}
		
		metrics = new ArrayList<ComplexMapMetric>(dataProviderMetric.getList());
		
		MapViewerService.Connect.getInstance().getMultiMapValues(filterDate, null, metrics, levels, filterLevelMembers, -1, new AsyncCallback<List<ComplexObjectViewer>>() {
		
			@Override
			public void onFailure(Throwable caught) {
				viewer.showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
	
			@Override
			public void onSuccess(List<ComplexObjectViewer> result) {
//				if(result.size() > 0)
//					result = result.subList(0, 30);
				viewer.updateMapZoneValue(result);
				try {
					loadMap(result, getLayers());
					viewer.showWaitPart(false);
				} catch (Exception e) {
					e.printStackTrace();
					viewer.showWaitPart(false);
				}
				
				
			}
		});
		
	}
	
	public void generateMetricMap(List<ComplexMapMetric> metrics, List<ComplexMapLevel> levels, Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		viewer.showWaitPart(true);
		
		metrics = new ArrayList<ComplexMapMetric>(dataProviderMetric.getList());
		
		MapViewerService.Connect.getInstance().getMultiMapValues(startDate, endDate, metrics, levels, filterLevelMembers, -1, new AsyncCallback<List<ComplexObjectViewer>>() {
		
			@Override
			public void onFailure(Throwable caught) {
				viewer.showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
	
			@Override
			public void onSuccess(List<ComplexObjectViewer> result) {
//				if(result.size() > 0)
//					result = result.subList(0, 30);
				viewer.updateMapZoneValue(result);
				try {
					loadMap(result, getLayers());
					viewer.showWaitPart(false);
				} catch (Exception e) {
					e.printStackTrace();
					viewer.showWaitPart(false);
				}
				
				
			}
		});
		
	}
	
	private MapInformation getLayers() {
		return selectedMap != null ? selectedMap.getMapInformation() : null;
	}
	
	private CellTable<ComplexMapMetric> MetricGridData() {

		TextCell cell = new TextCell();
		
		ImageCell imageCell = new ImageCell() {
		    @Override
		    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		    	if (value != null) {
		    	      // The template will sanitize the URI.
		    	      sb.appendHtmlConstant("<img src = '"+value+"' height = '18px' width = '18px' />");
		    	}
		     }
		};

		ImageCell colorCell = new ImageCell() {
		    @Override
		    public void render(com.google.gwt.cell.client.Cell.Context context, String value, SafeHtmlBuilder sb) {
		    	if (value != null) {
		    	      // The template will sanitize the URI.
		    	      sb.appendHtmlConstant("<div style = 'background:"+value+"; height:18px; width:30px' />");
		    	}
		     }
		};
		Column<ComplexMapMetric, String> colorColumn = new Column<ComplexMapMetric, String>(colorCell){

			@Override
			public String getValue(ComplexMapMetric object) {
				if(object.getRepresentation().equals("Bulles")){
					return '#' + object.getColor();
				} else {
					return "linear-gradient(to bottom right, red , yellow, green)";
				}
				
			}
			
		};
		Column<ComplexMapMetric, Boolean> checkboxColumn = new Column<ComplexMapMetric, Boolean>(
				new CheckboxCell(true, true)) {

			@Override
			public Boolean getValue(ComplexMapMetric object) {
				return selectionModelMetric.isSelected(object);
			}
		};
		checkboxColumn.setFieldUpdater(new FieldUpdater<ComplexMapMetric, Boolean>() {
			
			@Override
			public void update(int index, ComplexMapMetric object, Boolean value) {
				
				selectionModelMetric.setSelected(object, value);	
			}
		});
		Column<ComplexMapMetric, String> iconColumn = new Column<ComplexMapMetric, String>(imageCell){

			@Override
			public String getValue(ComplexMapMetric object) {
				String url = object.getIconUrl();
				if (url.contains("webapps")) {
					url = url.substring(url.indexOf("webapps") + "webapps".length(), url.length());
				}
				url = webappUrl + url.replace("\\", "/");
				//return GWT.getHostPageBaseURL() + "icons-set/" + object.getIconUrl();
				return url;
			}
			
		};
		Column<ComplexMapMetric, String> nameColumn = new Column<ComplexMapMetric, String>(cell) {

			@Override
			public String getValue(ComplexMapMetric object) {
				return object.getMetric().getName();
			}
		};
		nameColumn.setSortable(true);

		// DataGrid.Resources resources = new CustomResources();
		CellTable<ComplexMapMetric> dataGrid = new CellTable<ComplexMapMetric>(12);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");

		dataGrid.addColumn(colorColumn, "");
		dataGrid.addColumn(checkboxColumn, "");
		dataGrid.addColumn(iconColumn, "");
		dataGrid.addColumn(nameColumn, "");
		dataGrid.setColumnWidth(colorColumn, 20.0, Unit.PCT);
		dataGrid.setColumnWidth(checkboxColumn, 10.0, Unit.PCT);
		dataGrid.setColumnWidth(iconColumn, 10.0, Unit.PCT);
		dataGrid.setColumnWidth(nameColumn, 60.0, Unit.PCT);

		//dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProviderMetric = new ListDataProvider<ComplexMapMetric>();
		dataProviderMetric.addDataDisplay(dataGrid);

		sortHandlerMetric = new ListHandler<ComplexMapMetric>(new ArrayList<ComplexMapMetric>());
		sortHandlerMetric.setComparator(nameColumn, new Comparator<ComplexMapMetric>() {

			@Override
			public int compare(ComplexMapMetric m1, ComplexMapMetric m2) {
				return m1.getMetric().getName().compareTo(m2.getMetric().getName());
			}
		});

		dataGrid.addColumnSortHandler(sortHandlerMetric);

		// Add a selection model so we can select cells.
		selectionModelMetric = new MultiSelectionModel<ComplexMapMetric>();
		selectionModelMetric
				.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid.setSelectionModel(selectionModelMetric);
		
		dataGrid.setEmptyTableWidget(new HTML(lblCnst.NoResult()));

		return dataGrid;
	}

	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			metrics.clear();
			for(ComplexMapMetric cpx : selectionModelMetric.getSelectedSet()){
				metrics.add(cpx);
			}
			
			
//			if(levels.size() > 0 && metrics.size() > 0){
//				//generateMetricMap(metrics, levels, datePickerDialog.getSelectedDate());
//				List<MapZoneValue> values = viewer.getMapValues(levels, metrics);
//				loadMap(values);
//				
//			}
			updateMap();
//			Set<ComplexMapMetric> set = selectionModelMetric.getSelectedSet();
//			selectedMetric = new ArrayList<ComplexMapMetric>(set).get(0).getMetric();
		}
	};

	public void onLevelSelection(boolean checked, ComplexMapLevel level) {
/*      COMPORTEMENT POUR PLUSIEURS LEVELS AT THE SAME TIME	  //TODO
		if(checked){  //on ajoute le level selectionne a la liste deja presente
			levels.add(level);
		}else {   //on vide la liste et parcourt la liste pour retrouver les selectionnes
			levels.clear();
			for(int i = 0; i<treeAxis.getItemCount(); i++){
				TreeItem axe = treeAxis.getItem(i);
				for(int j=0; j<axe.getChildCount(); j++){
					
					LevelTreeItem item = (LevelTreeItem) axe.getChild(j);

					if(item.getCheckBoxState()){
						levels.add(item.getLevel());
					}
				}
			}
		}
*/
/*		COMPORTEMENT POUR UN UNIQUE LEVEL : POUR L'INSTANT */
		if(checked){  //on ajoute le level selectionne a la liste deja presente
			levels.clear();
			treeClearSelection(level);
			levels.add(level);
		}else {   //on vide la liste et parcourt la liste pour retrouver les selectionnes
			levels.clear();
			treeClearSelection(level);
		}
		if(levels.size() > 0){
			if(viewer.getActionDate() == ViewerPanel.ActionDate.VALEUR){
				generateMetricMap(metrics, levels, datePickerDialog.getSelectedDate());
			} else {
				generateMetricMap(metrics, levels, datePickerStartDialog.getSelectedDate(), datePickerEndDialog.getSelectedDate());
			}
			
		}
		//selectedLevel = levels.get(0).getLevel();
	}
	
	private void initText(Date initialDate){
		lblDateTitle.setText(DateTimeFormat.getFormat("dd MMMM yyyy").format(initialDate));
		lblDateStartTitle.setText(DateTimeFormat.getFormat("dd MMMM yyyy").format(initialDate));
		lblDateEndTitle.setText(DateTimeFormat.getFormat("dd MMMM yyyy").format(initialDate));
	}

	public Level getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(Level selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public Metric getSelectedMetric() {
		return selectedMetric;
	}

	public void setSelectedMetric(Metric selectedMetric) {
		this.selectedMetric = selectedMetric;
	}

	public ComplexMap getSelectedMap() {
		return selectedMap;
	}

	public void setSelectedMap(ComplexMap selectedMap) {
		this.selectedMap = selectedMap;
	}
	
	public List<LevelMember> getFilterLevelMembers() {
		return filterLevelMembers;
	}

	public void setFilterLevelMembers(List<LevelMember> filterLevelMembers) {
		this.filterLevelMembers = filterLevelMembers;
	}

	public void loadMap(List<ComplexObjectViewer> result, MapInformation mapInformation){
		mapPanel.clear();
		mapContent = new MapPanel(this, result, metrics, mapInformation);
		
		mapPanel.add(mapContent);
	}

	public void updateMap(){
		if(mapContent == null){
			return;
		} else {
			mapContent.updateDisplayByMetrics(metrics);
		}
			
	}

	public void updateFilters(List<LevelMember> filteredElements) {
		filterLevelMembers = filteredElements;
		buildFloatAxis();
		
		
	}

	public void updateSize() {
		if(mapContent != null)
			mapContent.updateSize();
	}
	
	public void treeClearSelection(ComplexMapLevel level) {
		for(int i = 0; i<treeAxis.getItemCount(); i++){
			TreeItem axe = treeAxis.getItem(i);
			for(int j=0; j<axe.getChildCount(); j++){
				
				LevelTreeItem item = (LevelTreeItem) axe.getChild(j);
				if(item.getLevel().getId() != level.getId())
					item.setCheckBoxState(false);
			}
		}
	}
	
	@UiHandler("btnBackground")
	public void onLayersClick(ClickEvent event) {
		layersMenu.setVisible(!layersMenu.isVisible());
	}
	
	private void createLayersMenu() {

		MenuItem none = new MenuItem(SafeHtmlUtils.fromString("<" + lblCnst.NoBackMap() + ">"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("none");
			}
		});
		MenuItem osm = new MenuItem(SafeHtmlUtils.fromString("OSM"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("osm");
			}
		});
		MenuItem watercolor = new MenuItem(SafeHtmlUtils.fromString("WaterColor"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("watercolor");
			}
		});
		MenuItem toner = new MenuItem(SafeHtmlUtils.fromString("Toner"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("toner");
			}
		});
		MenuItem toner_lite = new MenuItem(SafeHtmlUtils.fromString("Toner-Lite"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("toner-lite");
			}
		});
		MenuItem osm_quest = new MenuItem(SafeHtmlUtils.fromString("MapQuest OSM"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("osm-quest");
			}
		});
		MenuItem sat_quest = new MenuItem(SafeHtmlUtils.fromString("MapQuest Satellite"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("sat-quest");
			}
		});
		MenuItem hyb_quest = new MenuItem(SafeHtmlUtils.fromString("MapQuest Hybrid"), new Command() {
			@Override
			public void execute() {
				mapContent.changeTileMap("hyb-quest");
			}
		});

		// Make a new menu bar, adding a few cascading menus to it.
		layersMenu = new MenuBar(true);
		layersMenu.addItem(none);
		layersMenu.addItem(osm);
		layersMenu.addItem(watercolor);
		layersMenu.addItem(toner);
		layersMenu.addItem(toner_lite);
		layersMenu.addItem(osm_quest);
		layersMenu.addItem(sat_quest);
		layersMenu.addItem(hyb_quest);

		layersMenu.addStyleName(style.layersMenu());
		// Add it to the root panel.
		centerPanel.add(layersMenu);
		layersMenu.setVisible(false);
		layersMenu.addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				layersMenu.setVisible(false);

			}
		}, MouseOutEvent.getType());
	}
	
	@UiHandler("rdCalendarValeur")
	public void onValeurClick(ClickEvent event) {
		if(rdCalendarValeur.getValue()){
			setCalendarPanel(0);
		}
	}
	
	@UiHandler("rdCalendarEvolution")
	public void onEvolutionClick(ClickEvent event) {
		if(rdCalendarEvolution.getValue()){
			setCalendarPanel(1);
		}
	}

	private void setCalendarPanel(int item) {
		if(item == 0){
			rdCalendarEvolution.setValue(false);
			valeurPanel.setVisible(true);
			evolutionPanel.setVisible(false);
			viewer.setActionDate(ViewerPanel.ActionDate.VALEUR);
			calendarPanel.setWidth("100%");
		} else {
			rdCalendarValeur.setValue(false);
			valeurPanel.setVisible(false);
			evolutionPanel.setVisible(true);
			viewer.setActionDate(ViewerPanel.ActionDate.EVOLUTION);
			calendarPanel.setWidth("575px");
		}
		
	}

//	public ManageDataObservatory getManagedataobservatory() {
//		return managedataobservatory;
//	}
//
//	public void setManagedataobservatory(ManageDataObservatory managedataobservatory) {
//		this.managedataobservatory = managedataobservatory;
//	}

	@UiHandler("btnLoad")
	public void onLoadClick(ClickEvent e){
		if(levels.size() > 0 && viewer.getActionDate() == ViewerPanel.ActionDate.EVOLUTION){
			generateMetricMap(metrics, levels, datePickerStartDialog.getSelectedDate(), datePickerEndDialog.getSelectedDate());
		}
		
	}

	public ViewerPanel getViewer() {
		return viewer;
	}
	
	public void setViewer(ViewerPanel viewer) {
		this.viewer = viewer;
	}

	public MapPanel getMapContent() {
		return mapContent;
	}
	
	@UiHandler("imgFilterCollapse")
	public void onCollapseClick(ClickEvent event) {
		filtersPanel.setWidth("150px");
		filtersPanel.setHeight("36px");
		filtersContent.setVisible(false);
		imgFilterCollapse.setVisible(false);
		imgFilterExpand.setVisible(true);
		lblfilterPanel.setVisible(true);
	}

	@UiHandler("imgFilterExpand")
	public void onExpandClick(ClickEvent event) {
		filtersPanel.setWidth("30%");
		filtersPanel.getElement().getStyle().clearHeight();
		filtersContent.setVisible(true);
		imgFilterCollapse.setVisible(true);
		imgFilterExpand.setVisible(false);
		lblfilterPanel.setVisible(false);
	}
}
