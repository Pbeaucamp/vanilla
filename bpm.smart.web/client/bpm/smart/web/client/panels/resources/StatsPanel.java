package bpm.smart.web.client.panels.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dataset.DatasetCubeDesignerPanel;
import bpm.gwt.commons.client.loading.CompositeWaitPanel;
import bpm.gwt.workflow.commons.client.workflow.properties.ColumnAirPanel;
import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.StatDataColumn;
import bpm.smart.core.model.workflow.activity.ChartOutputActivity;
import bpm.smart.core.model.workflow.activity.CorMatrixActivity;
import bpm.smart.core.model.workflow.activity.CorMatrixActivity.TypeCor;
import bpm.smart.core.model.workflow.activity.DecisionTreeActivity;
import bpm.smart.core.model.workflow.activity.DecisionTreeActivity.TypeRPart;
import bpm.smart.core.model.workflow.activity.HAClustActivity;
import bpm.smart.core.model.workflow.activity.HAClustActivity.TypeClust;
import bpm.smart.core.model.workflow.activity.HAClustActivity.TypeDist;
import bpm.smart.core.model.workflow.activity.KmeansActivity;
import bpm.smart.core.model.workflow.activity.KmeansActivity.TypeAlgoKMeans;
import bpm.smart.core.model.workflow.activity.SimpleLinearRegActivity;
import bpm.smart.web.client.MainPanel;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.services.SmartAirService;
import bpm.smart.web.client.utils.AlphanumComparator;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.workflow.commons.beans.ActivityLog;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class StatsPanel extends CompositeWaitPanel{

	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);
	
	private static StatsPanelUiBinder uiBinder = GWT.create(StatsPanelUiBinder.class);

	interface StatsPanelUiBinder extends UiBinder<Widget, StatsPanel> {
	}
	
	interface MyStyle extends CssResource {
		String popup();
		String mainPanel();
		String mainPanelFull();
		String childCell();
		String lstTypeView();
		String label();
		String chkbox();
		String txt();
		
		String btnActive();
		String imgExportCodeOn();
	}

	@UiField
	HTMLPanel panelContent, leftPanel, rightPanel, summaryPanel, statsPanel, algoPanel, cubePanel, browsePanel, paramPanel;

	@UiField
	Image panelGraph, btnFullWindow, btnRestoreWindow, btnCloseWindow, exportCode,
			btnBrowse, btnSummary, btnStats, btnAlgo, btnCube,
			btnLinReg, btnMatCor, btnDecTree, btnKMeans, btnHAC;

	@UiField
	SimplePanel grd1, grd2, framePanel, grdBrowse, panelSvg;

	@UiField
	ListBox /*lstTypeView,*/ lstRowNumber/*, lstTypeAlgo*/;
	
	@UiField
	Button btnAlgoLaunch;
	
	@UiField
	MyStyle style;
	
	private List<String> quantCols = new ArrayList<String>(Arrays.asList(new String[]{"numeric", "integer", "INT", "DOUBLE"}));
	private List<String> qualCols = new ArrayList<String>(Arrays.asList(new String[]{"factor", "character", "VARCHAR"}));

	private Dataset dataset;
	private List<StatDataColumn> datastats;
	private MainPanel mainPanel;
	private WorkspacePanel workspacePanel;

	private SingleSelectionModel<StatDataColumn> selectionModelStats1;
	private SingleSelectionModel<StatDataColumn> selectionModelStats2;
	private ListDataProvider<StatDataColumn> dataProviderStats1;
	private ListDataProvider<StatDataColumn> dataProviderStats2;
	private ListDataProvider<String> dataProviderBrowse;
	private ListHandler<StatDataColumn> sortHandlerStats1;
	private ListHandler<StatDataColumn> sortHandlerStats2;
	private List<ListHandler<String>> sortHandlersBrowse = new ArrayList<ListHandler<String>>();
	
	Set<Integer> showingDetails = new HashSet<Integer>();
	private DataGrid<StatDataColumn> datagridStats1;
	private DataGrid<StatDataColumn> datagridStats2;
	private DataGrid<String> datagridBrowse;
	private User user;
	private HTMLPanel oldPanel;
	
	private String lastgeneratedCode = null;
	private DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy hh:mm"); 
	
	private KeyPressHandler integerBox = new KeyPressHandler() {
		
		@Override
		public void onKeyPress(KeyPressEvent event) {
			TextBox sender = (TextBox) event.getSource();

		    int keyCode = event.getNativeEvent().getKeyCode();

		    if (!(Character.isDigit(event.getCharCode()))
		        && !(keyCode == KeyCodes.KEY_TAB)
		        && !(keyCode == KeyCodes.KEY_BACKSPACE)
		        && !(keyCode == KeyCodes.KEY_LEFT)
		        && !(keyCode == KeyCodes.KEY_RIGHT)) {
		      sender.cancelKey();
		    }
			
		}
	};
	
	private HandlerRegistration algoLaunchClick;
	private boolean allowGetStats = false;

	public StatsPanel(Dataset dataset, List<StatDataColumn> stats, User user, boolean isFullScreen, boolean isClosable, MainPanel mainPanel, WorkspacePanel workspacePanel) {
		initWidget(uiBinder.createAndBindUi(this));

		this.dataset = dataset;
		this.datastats = stats;
		this.mainPanel = mainPanel;
		this.workspacePanel = workspacePanel;
		this.user = user;
//		lstTypeView.addItem(lblCnst.ViewDataset(), "browse"); 
//		lstTypeView.addItem(lblCnst.Summary(), "summary"); 
//		lstTypeView.addItem(lblCnst.ShowStats(), "stats"); 
//		lstTypeView.addItem(lblCnst.SimpleMethods(), "algo");
//		lstTypeView.addItem(lblCnst.CubeViewer(), "cube");
//		lstTypeView.setSelectedIndex(0);
//		onChangeView(null);
		onBrowse(null);
		
		adaptSize(isClosable, isFullScreen);
		
		initJS(this);
		
		rightPanel.addDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				exportCode.addStyleName(style.imgExportCodeOn());
			}
		}, MouseOverEvent.getType());
		rightPanel.addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				exportCode.removeStyleName(style.imgExportCodeOn());
			}
		}, MouseOutEvent.getType());
	}
	
	private final native void initJS(StatsPanel panel) /*-{		
		var panel = panel;
		$wnd.clickDetail1 = function(id, index){
			
			panel.@bpm.smart.web.client.panels.resources.StatsPanel::handleDetailClick1(Ljava/lang/String;Ljava/lang/String;)(id.toString(), index.toString());
		};
		$wnd.clickDetail2 = function(id, index){
			
			panel.@bpm.smart.web.client.panels.resources.StatsPanel::handleDetailClick2(Ljava/lang/String;Ljava/lang/String;)(id.toString(), index.toString());
		};
		
	}-*/;
	
	@UiHandler("btnBrowse")
	public void onBrowse(ClickEvent event){
		initBrowsePanel();
		browsePanel.setVisible(true);
		summaryPanel.setVisible(false);
		statsPanel.setVisible(false);
		algoPanel.setVisible(false);
		cubePanel.setVisible(false);
		rightPanel.setVisible(false);
		
		btnBrowse.addStyleName(style.btnActive());
		btnSummary.removeStyleName(style.btnActive());
		btnStats.removeStyleName(style.btnActive());
		btnAlgo.removeStyleName(style.btnActive());
		btnCube.removeStyleName(style.btnActive());
	}
	@UiHandler("btnSummary")
	public void onSummary(ClickEvent event){
		initSummaryPanel();
		browsePanel.setVisible(false);
		summaryPanel.setVisible(true);
		statsPanel.setVisible(false);
		algoPanel.setVisible(false);
		cubePanel.setVisible(false);
		rightPanel.setVisible(true);
		panelGraph.setUrl("");
		panelGraph.setVisible(false);
		panelSvg.clear();
		panelSvg.setVisible(true);
		
		btnBrowse.removeStyleName(style.btnActive());
		btnSummary.addStyleName(style.btnActive());
		btnStats.removeStyleName(style.btnActive());
		btnAlgo.removeStyleName(style.btnActive());
		btnCube.removeStyleName(style.btnActive());
	}
	@UiHandler("btnStats")
	public void onStats(ClickEvent event){
		initStatsPanel();
		browsePanel.setVisible(false);
		summaryPanel.setVisible(false);
		statsPanel.setVisible(true);
		algoPanel.setVisible(false);
		cubePanel.setVisible(false);
		rightPanel.setVisible(true);
		panelGraph.setUrl("");
		panelGraph.setVisible(true);
		panelSvg.clear();
		panelSvg.setVisible(false);
		
		btnBrowse.removeStyleName(style.btnActive());
		btnSummary.removeStyleName(style.btnActive());
		btnStats.addStyleName(style.btnActive());
		btnAlgo.removeStyleName(style.btnActive());
		btnCube.removeStyleName(style.btnActive());
	}
	@UiHandler("btnAlgo")
	public void onAlgos(ClickEvent event){
		initAlgoPanel();
		browsePanel.setVisible(false);
		summaryPanel.setVisible(false);
		statsPanel.setVisible(false);
		algoPanel.setVisible(true);
		cubePanel.setVisible(false);
		rightPanel.setVisible(true);
		panelGraph.setUrl("");
		panelGraph.setVisible(true);
		panelSvg.clear();
		panelSvg.setVisible(false);
		
		btnBrowse.removeStyleName(style.btnActive());
		btnSummary.removeStyleName(style.btnActive());
		btnStats.removeStyleName(style.btnActive());
		btnAlgo.addStyleName(style.btnActive());
		btnCube.removeStyleName(style.btnActive());
	}
	@UiHandler("btnCube")
	public void onCube(ClickEvent event){
		initCubePanel();
		browsePanel.setVisible(false);
		summaryPanel.setVisible(false);
		statsPanel.setVisible(false);
		algoPanel.setVisible(false);
		cubePanel.setVisible(true);
		rightPanel.setVisible(false);
		
		btnBrowse.removeStyleName(style.btnActive());
		btnSummary.removeStyleName(style.btnActive());
		btnStats.removeStyleName(style.btnActive());
		btnAlgo.removeStyleName(style.btnActive());
		btnCube.addStyleName(style.btnActive());
	}

//	@UiHandler("lstTypeView")
//	public void onChangeView(ChangeEvent event) {
//		switch (lstTypeView.getValue(lstTypeView.getSelectedIndex())) {
//		case "browse":
//			initBrowsePanel();
//			browsePanel.setVisible(true);
//			summaryPanel.setVisible(false);
//			statsPanel.setVisible(false);
//			algoPanel.setVisible(false);
//			cubePanel.setVisible(false);
//			rightPanel.setVisible(false);
//			break;
//		case "summary":
//			initSummaryPanel();
//			browsePanel.setVisible(false);
//			summaryPanel.setVisible(true);
//			statsPanel.setVisible(false);
//			algoPanel.setVisible(false);
//			cubePanel.setVisible(false);
//			rightPanel.setVisible(true);
//			panelGraph.setUrl("");
//			panelGraph.setVisible(false);
//			panelSvg.clear();
//			panelSvg.setVisible(true);
//			break;
//		case "stats":
//			initStatsPanel();
//			browsePanel.setVisible(false);
//			summaryPanel.setVisible(false);
//			statsPanel.setVisible(true);
//			algoPanel.setVisible(false);
//			cubePanel.setVisible(false);
//			rightPanel.setVisible(true);
//			panelGraph.setUrl("");
//			panelGraph.setVisible(true);
//			panelSvg.clear();
//			panelSvg.setVisible(false);
//			break;
//		case "algo":
//			initAlgoPanel();
//			browsePanel.setVisible(false);
//			summaryPanel.setVisible(false);
//			statsPanel.setVisible(false);
//			algoPanel.setVisible(true);
//			cubePanel.setVisible(false);
//			rightPanel.setVisible(true);
//			panelGraph.setUrl("");
//			panelGraph.setVisible(true);
//			panelSvg.clear();
//			panelSvg.setVisible(false);
//			break;
//		case "cube":
//			initCubePanel();
//			browsePanel.setVisible(false);
//			summaryPanel.setVisible(false);
//			statsPanel.setVisible(false);
//			algoPanel.setVisible(false);
//			cubePanel.setVisible(true);
//			rightPanel.setVisible(false);
//			break;
//		default:
//			initSummaryPanel();
//			browsePanel.setVisible(true);
//			summaryPanel.setVisible(false);
//			statsPanel.setVisible(false);
//			algoPanel.setVisible(false);
//			cubePanel.setVisible(false);
//			rightPanel.setVisible(false);
//			panelGraph.setUrl("");
//			panelGraph.setVisible(true);
//			panelSvg.clear();
//			panelSvg.setVisible(false);
//			break;
//		}
//	}
	
	private void initBrowsePanel() {
		lstRowNumber.addItem("25");
		lstRowNumber.addItem("50");
		lstRowNumber.addItem("100");
		lstRowNumber.setSelectedIndex(2);
		if(datagridBrowse == null){
			RScriptModel box = new RScriptModel();
			String script = "n<-min("+ Integer.parseInt(lstRowNumber.getValue(lstRowNumber.getSelectedIndex())) +", nrow("+ dataset.getName() +"))\n"
					+ "manual_result<-c()\n"
					+ "for(i in 1:n){\n"
						+ "line<-''\n"
						+ "for(names in colnames("+ dataset.getName() +")){\n"
							+ "if(is.na("+ dataset.getName() +"[i,names]) | as.character("+ dataset.getName() +"[i,names]) == ''){\n"
								+ "line<- paste(line, 'NA', sep='_;_')\n"
							+"} else {\n"
								+ "line<- paste(line, "+ dataset.getName() +"[i,names], sep='_;_')\n"
							+ "}\n"
						+ "}\n"
						+ "line<-substr(line,4,nchar(line))\n"
						+ "manual_result[i] <- line\n"
					+ "}";
			box.setScript(script);
			box.setOutputs(new String[]{"manual_result"});

			SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

				@Override
				public void onSuccess(RScriptModel result) {
					showWaitPart(false);
					mainPanel.getLogPanel().addLog(result.getOutputLog());
					
					datagridBrowse = createGridDataBrowse(result.getOutputVarstoString().get(0));
					grdBrowse.add(datagridBrowse);
				}

				@Override
				public void onFailure(Throwable caught) {
					showWaitPart(false);
					caught.printStackTrace();

					ExceptionManager.getInstance().handleException(caught, caught.getMessage());
				}
			});
			
			
		}
	}
	
	private void initSummaryPanel() {
		showWaitPart(true);
		SmartAirService.Connect.getInstance().generateSummaryPlot(dataset, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				showWaitPart(false);
				if (result.getOutputFiles() != null) {
					framePanel.clear();
					framePanel.add(new HTML(result.getOutputVarstoString().get(0)));
					//panelGraph.setUrl(result.getOutputFiles()[0]);
					panelSvg.add(new HTML(result.getOutputFiles()[0]));

 
					StringBuffer genCode = new StringBuffer();
					genCode.append("\n### ").append("Summary Plot").append(" on ").append(dataset.getName());
					genCode.append(" - ").append(dtf.format(new Date())).append(" - ").append("\n");
					genCode.append(result.getScript());
					genCode.append("\n###\n");
					lastgeneratedCode = genCode.toString();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}

	private void initStatsPanel() {
		allowGetStats = false;
		if(datagridStats1 == null && datagridStats2 == null){
			this.datagridStats1 = createGridDataStats1();
			grd1.add(datagridStats1);
		
			this.datagridStats2 = createGridDataStats2();
			grd2.add(datagridStats2);
			
			if (datastats != null) {
				dataProviderStats1.setList(datastats);
				if (dataProviderStats1.getList().size() > 0) {
					selectionModelStats1.setSelected(dataProviderStats1.getList().get(0), true);
				}
				dataProviderStats2.setList(datastats);
				if (dataProviderStats2.getList().size() > 0) {
					selectionModelStats2.setSelected(dataProviderStats2.getList().get(0), true);
				}
				if(selectionModelStats1 != null && selectionModelStats2 != null){
					getStats();
					allowGetStats = true;
				}
					
			}
		} else {
			if (datastats != null) {
				dataProviderStats1.setList(datastats);
				if (dataProviderStats1.getList().size() > 0) {
					selectionModelStats1.setSelected(dataProviderStats1.getList().get(0), true);
				}
				dataProviderStats2.setList(datastats);
				if (dataProviderStats2.getList().size() > 0) {
					selectionModelStats2.setSelected(dataProviderStats2.getList().get(0), true);
				}
				if(selectionModelStats1 != null && selectionModelStats2 != null){
					getStats();
					allowGetStats = true;
				}
			}
		}
			
		
	}

	private void initAlgoPanel() {
//		lstTypeAlgo.addItem(lblCnst.ActivitySimpleLinearReg(), "slr"); 
//		lstTypeAlgo.addItem(lblCnst.ActivityCorrelationMatrix(), "cm"); 
//		lstTypeAlgo.addItem(lblCnst.ActivityDecisionTree(), "dt"); 
//		lstTypeAlgo.addItem(lblCnst.ActivityKMeans(), "km");
//		lstTypeAlgo.addItem(lblCnst.ActivityHACClustering(), "hac");
//		lstTypeAlgo.setSelectedIndex(0);
//		initAlgoParamPanel();
		onLinReg(null);
	}

	private void initCubePanel() {
		cubePanel.clear();
		if(dataset !=null)
			cubePanel.add(new DatasetCubeDesignerPanel(dataset));
	}

	private DataGrid<StatDataColumn> createGridDataStats1() {
		TextCell cell = new TextCell();
		final Column<StatDataColumn, String> nameColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {
				return object.getNameDatacolumn();
			}
		};
		nameColumn.setSortable(true);

		Column<StatDataColumn, String> minColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getMin();
			}
		};
		minColumn.setSortable(true);

		Column<StatDataColumn, String> maxColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getMax();
			}
		};
		maxColumn.setSortable(true);

		Column<StatDataColumn, String> averageColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getAverage();
			}
		};
		averageColumn.setSortable(true);

		Column<StatDataColumn, String> deviationColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getDeviation();
			}
		};
		deviationColumn.setSortable(true);

		// View stats.
	    SafeHtmlRenderer<String> anchorRenderer = new AbstractSafeHtmlRenderer<String>() {
	      @Override
	      public SafeHtml render(String object) {
	        SafeHtmlBuilder sb = new SafeHtmlBuilder();
	        sb.appendHtmlConstant("(<a href=\"javascript:;\">").appendEscaped(object)
	            .appendHtmlConstant("</a>)");
	        return sb.toSafeHtml();
	      }
	    };
	    
	    final Column<StatDataColumn, String> repartitionColumn = new Column<StatDataColumn, String>(new ClickableTextCell(anchorRenderer)) {
	      @Override
	      public String getValue(StatDataColumn object) {
	        if (showingDetails.contains(object.getId())) {
	          return lblCnst.HideStats();
	        } else {
	          return lblCnst.ShowStats();
	        }
	      }
	    };
		repartitionColumn.setSortable(true);

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<StatDataColumn> dataGrid1 = new DataGrid<StatDataColumn>(999);
		dataGrid1.setWidth("100%");
		dataGrid1.setHeight("100%");

		// Attention au label
		dataGrid1.addColumn(nameColumn, lblCnst.Name());
		dataGrid1.addColumn(minColumn, lblCnst.Min());
		dataGrid1.addColumn(maxColumn, lblCnst.Max());
		dataGrid1.addColumn(averageColumn, lblCnst.Mean());
		dataGrid1.addColumn(deviationColumn, lblCnst.Deviation());
		dataGrid1.addColumn(repartitionColumn, lblCnst.Distribution());

		// Resources resources = GWT.create(Resources.class);

		dataGrid1.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProviderStats1 = new ListDataProvider<StatDataColumn>();
		dataProviderStats1.addDataDisplay(dataGrid1);

		sortHandlerStats1 = new ListHandler<StatDataColumn>(new ArrayList<StatDataColumn>());
		sortHandlerStats1.setComparator(nameColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getNameDatacolumn().compareTo(m2.getNameDatacolumn());
			}
		});

		sortHandlerStats1.setComparator(minColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getMin().compareTo(m2.getMin());
			}
		});

		sortHandlerStats1.setComparator(maxColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getMax().compareTo(m2.getMax());
			}
		});

		sortHandlerStats1.setComparator(averageColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return Integer.parseInt(m2.getAverage()) - Integer.parseInt(m1.getAverage());
			}
		});

		sortHandlerStats1.setComparator(deviationColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getNameDatacolumn().compareTo(m2.getNameDatacolumn());
			}
		});
		
		

		dataGrid1.addColumnSortHandler(sortHandlerStats1);

		// Add a selection model so we can select cells.
		selectionModelStats1 = new SingleSelectionModel<StatDataColumn>();
		selectionModelStats1.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid1.setSelectionModel(selectionModelStats1);
		
		dataGrid1.setTableBuilder(new CustomTableBuilder(dataGrid1, 1));

		return dataGrid1;
	}

	private DataGrid<StatDataColumn> createGridDataStats2() {
		TextCell cell = new TextCell();
		final Column<StatDataColumn, String> nameColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {
				return object.getNameDatacolumn();
			}
		};
		nameColumn.setSortable(true);

		Column<StatDataColumn, String> minColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getMin();
			}
		};
		minColumn.setSortable(true);

		Column<StatDataColumn, String> maxColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getMax();
			}
		};
		maxColumn.setSortable(true);

		Column<StatDataColumn, String> averageColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getAverage();
			}
		};
		averageColumn.setSortable(true);

		Column<StatDataColumn, String> deviationColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return object.getDeviation();
			}
		};
		deviationColumn.setSortable(true);

		Column<StatDataColumn, String> repartitionColumn = new Column<StatDataColumn, String>(cell) {

			@Override
			public String getValue(StatDataColumn object) {

				return "";
			}
		};
		repartitionColumn.setSortable(true);

		// DataGrid.Resources resources = new CustomResources();
		DataGrid<StatDataColumn> dataGrid2 = new DataGrid<StatDataColumn>(999);
		dataGrid2.setWidth("100%");
		dataGrid2.setHeight("100%");

		// Attention au label
		dataGrid2.addColumn(nameColumn, lblCnst.Name());
		dataGrid2.addColumn(minColumn, lblCnst.Min());
		dataGrid2.addColumn(maxColumn, lblCnst.Max());
		dataGrid2.addColumn(averageColumn, lblCnst.Mean());
		dataGrid2.addColumn(deviationColumn, lblCnst.Deviation());
		dataGrid2.addColumn(repartitionColumn, lblCnst.Distribution());

		dataGrid2.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		dataProviderStats2 = new ListDataProvider<StatDataColumn>();
		dataProviderStats2.addDataDisplay(dataGrid2);

		sortHandlerStats2 = new ListHandler<StatDataColumn>(new ArrayList<StatDataColumn>());
		sortHandlerStats2.setComparator(nameColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getNameDatacolumn().compareTo(m2.getNameDatacolumn());
			}
		});

		sortHandlerStats2.setComparator(minColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getMin().compareTo(m2.getMin());
			}
		});

		sortHandlerStats2.setComparator(maxColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getMax().compareTo(m2.getMax());
			}
		});

		sortHandlerStats2.setComparator(averageColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return Integer.parseInt(m2.getAverage()) - Integer.parseInt(m1.getAverage());
			}
		});

		sortHandlerStats2.setComparator(deviationColumn, new Comparator<StatDataColumn>() {

			@Override
			public int compare(StatDataColumn m1, StatDataColumn m2) {
				return m1.getNameDatacolumn().compareTo(m2.getNameDatacolumn());
			}
		});

		dataGrid2.addColumnSortHandler(sortHandlerStats2);

		// Add a selection model so we can select cells.
		selectionModelStats2 = new SingleSelectionModel<StatDataColumn>();
		selectionModelStats2.addSelectionChangeHandler(selectionChangeHandler);
		dataGrid2.setSelectionModel(selectionModelStats2);
		
		dataGrid2.setTableBuilder(new CustomTableBuilder(dataGrid2, 2));

		return dataGrid2;
	}
	
	/**
	   * Renders the data rows that display each contact in the table.
	   */
	class CustomTableBuilder extends AbstractCellTableBuilder<StatDataColumn> {
		
		private final String childCell = " " + style.childCell();
		private final String rowStyle;
		private final String selectedRowStyle;
		private final String cellStyle;
		private final String selectedCellStyle;
		private DataGrid<StatDataColumn> datagrid;
		private int numDatagrid;
		
		//@SuppressWarnings("deprecation")
		public CustomTableBuilder(DataGrid<StatDataColumn> datagrid,  int numDatagrid) {
		  super(datagrid);
		  this.datagrid = datagrid;
		  this.numDatagrid = numDatagrid;
		  // Cache styles for faster access.
		  com.google.gwt.user.cellview.client.AbstractCellTable.Style style = datagrid.getResources().style();
		  rowStyle = style.evenRow();
		  selectedRowStyle = " " + style.selectedRow();
		  cellStyle = style.cell() + " " + style.evenRowCell();
		  selectedCellStyle = " " + style.selectedRowCell();
		}
		
		//@SuppressWarnings("deprecation")
		@Override
		public void buildRowImpl(StatDataColumn rowValue, int absRowIndex ) {
		  buildStatRow(rowValue, absRowIndex);
		
		  if(showingDetails.contains(rowValue.getIdDatacolumn())){
	    	  for(String col : rowValue.getRepartition()){
	    		  buildRepRow(col, absRowIndex);
	    	  }
	      }
		}
		
		private void buildStatRow(StatDataColumn rowValue, int absRowIndex) {
			// Calculate the row styles.
			  SelectionModel<? super StatDataColumn> selectionModel = datagrid.getSelectionModel();
			  boolean isSelected =
			      (selectionModel == null || rowValue == null) ? false : selectionModel
			          .isSelected(rowValue);
			  StringBuilder trClasses = new StringBuilder(rowStyle);
			  if (isSelected) {
			    trClasses.append(selectedRowStyle);
			  }
			
			  // Calculate the cell styles.
			  String cellStyles = cellStyle;
			  if (isSelected) {
			    cellStyles += selectedCellStyle;
			  }
			
			  TableRowBuilder row = startRow();
			  row.className(trClasses.toString());
			  
			  
			  // Name column.
			  TableCellBuilder td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text(rowValue.getNameDatacolumn());
			  
			  td.endTD();
			
			  // Min column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text(rowValue.getMin()).endTD();
			
			  // Max column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text(rowValue.getMax()).endTD();
			  
			  // Mean column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text(rowValue.getAverage()).endTD();
			  
			  // Dev column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text(rowValue.getDeviation()).endTD();
			  
			  // Rep column.
//			  td = row.startTD();
//			  td.className(cellStyles);
//			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
//			  td.text("").endTD();
//			  
//			  
			  td = row.startTD();
			  td.className(cellStyles);
			  
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  if(rowValue.getRepartition().get(0).equals("NA")){
				  td.text("");
			  } else {
				  if(showingDetails.contains(rowValue.getIdDatacolumn())){
					  td.startSpan().text("(").endSpan();
					  td.startAnchor().attribute("onclick","clickDetail" + numDatagrid + "(" + rowValue.getIdDatacolumn() + "," + absRowIndex + ");").text(lblCnst.HideDetails()).endAnchor();
					  td.startSpan().text(")").endSpan();
				  }else {
					  td.startSpan().text("(").endSpan();
					  td.startAnchor().attribute("onclick","clickDetail" + numDatagrid + "(" + rowValue.getIdDatacolumn() + "," + absRowIndex + ");").text(lblCnst.ShowDetails()).endAnchor();
					  td.startSpan().text(")").endSpan();
				  }
			  }
			  
			 
			  td.endTD();
			
			  row.endTR();
			}
		
		private void buildRepRow(String rowValue, int absRowIndex) {
			// Calculate the row styles.
			  StringBuilder trClasses = new StringBuilder(rowStyle);
			  
			  // Calculate the cell styles.
			  String cellStyles = cellStyle;
			  cellStyles += childCell;
			
			  TableRowBuilder row = startRow();
			  row.className(trClasses.toString());
			    
			 
			  // Name column.
			  TableCellBuilder td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text(rowValue.split(":")[0]);
			  
			  td.endTD();
			
			  // Min column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text("").endTD();
			
			  // Max column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text("").endTD();
			  
			  // Mean column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text("").endTD();
			  
			  // Dev column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text("").endTD();
			  
			  // Rep column.
			  td = row.startTD();
			  td.className(cellStyles);
			  td.style().outlineStyle(OutlineStyle.NONE).endStyle();
			  td.text(rowValue.split(":")[1]).endTD();
			
			  row.endTR();
			}
	}
	



	private Handler selectionChangeHandler = new Handler() {

		@Override
		public void onSelectionChange(SelectionChangeEvent event) {
			if (selectionModelStats1.getSelectedObject() != null && selectionModelStats2.getSelectedObject() != null && allowGetStats) {
				getStats();
			}

		}
	};

	private void getStats() {
		String col1Name = selectionModelStats1.getSelectedObject().getNameDatacolumn();
		String col2Name = selectionModelStats2.getSelectedObject().getNameDatacolumn();

		
		ChartOutputActivity activity = new ChartOutputActivity("co");
		activity.setDatasetName(dataset.getName());
		activity.setxColumnName(col1Name);
		activity.setyColumnName(col2Name);
		showWaitPart(true);
		SmartAirService.Connect.getInstance().executeActivity(activity, new AsyncCallback<ActivityLog>() {
			
			@Override
			public void onSuccess(ActivityLog result) {
				showWaitPart(false);
				panelGraph.setUrl(result.getOutputs().get(0).getPath());

				StringBuffer genCode = new StringBuffer();
				genCode.append("\n### ").append(LabelsConstants.lblCnst.ActivityChart()).append(" on ").append(dataset.getName());
				genCode.append(" - ").append(dtf.format(new Date())).append(" - ").append("\n");
				genCode.append(result.getScriptR());
				genCode.append("\n###\n");
				lastgeneratedCode = genCode.toString();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}

	private Set<StatDataColumn> getStatsbyDataset(Dataset dataset) {

		Set<StatDataColumn> result = new HashSet<StatDataColumn>();
		for (StatDataColumn stat : datastats) {
			for (DataColumn col : dataset.getMetacolumns()) {
				if (stat.getIdDatacolumn() == col.getId()) {
					result.add(stat);
				}
			}
		}
		return result;
	}
	
	@UiHandler("btnFullWindow")
	public void onClickFull(ClickEvent event) {
		oldPanel = (HTMLPanel) this.getParent();
		adaptSize(false, true);
		popupizer();
	}
	
	public void popupizer() {
		SimplePanel wid = new SimplePanel(this); 
		PopupPanel dial = new PopupPanel();
		dial.add(wid);
		dial.setStyleName(style.popup());
		dial.center();
	}
	
	@UiHandler("btnRestoreWindow")
	public void onClickRestore(ClickEvent event) {
		adaptSize(false, false);
		
		this.getParent().getParent().asWidget().removeFromParent();
		oldPanel.add(this);
	}
	
	@UiHandler("btnCloseWindow")
	public void onClickClose(ClickEvent event) {
		this.getParent().getParent().asWidget().removeFromParent();
	}
	
	public void adaptSize(boolean isClosable, boolean isFull){
		if(isClosable){
			btnCloseWindow.setVisible(true);
			if(isFull){
				btnRestoreWindow.setVisible(false);
				btnFullWindow.setVisible(false);
				panelContent.setStyleName(style.mainPanelFull());
				
			} else {
				btnRestoreWindow.setVisible(false);
				btnFullWindow.setVisible(false);
				panelContent.setStyleName(style.mainPanel());
			}
		} else {
			btnCloseWindow.setVisible(false);
			if(isFull){
				btnFullWindow.setVisible(false);
				btnRestoreWindow.setVisible(true);
				panelContent.setStyleName(style.mainPanelFull());
				
			} else {
				btnFullWindow.setVisible(true);
				btnRestoreWindow.setVisible(false);
				panelContent.setStyleName(style.mainPanel());
			}
		}
		
	}
	
	public void handleDetailClick1(String id, String index){
		 if (showingDetails.contains(Integer.parseInt(id))) {
			 showingDetails.remove(Integer.parseInt(id));
	        } else {
	        	showingDetails.add(Integer.parseInt(id));
	        }

	        // Redraw the modified row.
	        datagridStats1.redrawRow(Integer.parseInt(index));
	}
	
	public void handleDetailClick2(String id, String index){
		 if (showingDetails.contains(Integer.parseInt(id))) {
			 showingDetails.remove(Integer.parseInt(id));
	        } else {
	        	showingDetails.add(Integer.parseInt(id));
	        }

	        // Redraw the modified row.
	        datagridStats2.redrawRow(Integer.parseInt(index));
	}
	
	@SuppressWarnings("unchecked")
	private DataGrid<String> createGridDataBrowse(String provider) {
		// DataGrid.Resources resources = new CustomResources();
		DataGrid<String> dataGrid = new DataGrid<String>(100);
		dataGrid.setWidth("100%");
		dataGrid.setHeight("100%");
		
		dataProviderBrowse = new ListDataProvider<String>();
		dataProviderBrowse.addDataDisplay(dataGrid);
		
		if (datastats != null) {
			List<String> data = new ArrayList<>();
			for(String row : new ArrayList<String>(Arrays.asList(provider.split("\t")))){
				if(row.split("_;_").length == dataset.getMetacolumns().size()){
					data.add(row);
				}
			}
			dataProviderBrowse.setList(data);
		}
		
		TextCell cell = new TextCell();
		int i = 0;
		for(i=0; i<dataset.getMetacolumns().size(); i++){
			final int j = i;
			Column<String, String> col = new Column<String, String>(cell) {

				@Override
				public String getValue(String object) {
					return object.split("_;_")[j].trim();
				}
			};
			col.setSortable(true);
			dataGrid.addColumn(col, dataset.getMetacolumns().get(j).getColumnLabel());
			ListHandler<String> sortHandler = new ListHandler<String>(new ArrayList<String>());
			sortHandler.setComparator(col, new AlphanumComparator() {

				@Override
				public int compare(Object m1, Object m2) {
					return super.compare(((String)m1).split("_;_")[j].trim(),((String)m2).split("_;_")[j].trim());
				}
			});
			sortHandler.setList(dataProviderBrowse.getList());
			sortHandlersBrowse.add(sortHandler);
			dataGrid.addColumnSortHandler(sortHandler);
		}
		
		dataGrid.setEmptyTableWidget(new Label(lblCnst.NoResult()));

		

		// Add a selection model so we can select cells.
		SingleSelectionModel<String> selectionModel = new SingleSelectionModel<String>();
		dataGrid.setSelectionModel(selectionModel);
		

		return dataGrid;
	}
	
	@UiHandler("lstRowNumber")
	public void onChangeRowNumber(ChangeEvent event) {
		RScriptModel box = new RScriptModel();
		String script = "n<-min("+ Integer.parseInt(lstRowNumber.getValue(lstRowNumber.getSelectedIndex())) +", nrow("+ dataset.getName() +"))\n"
				+ "manual_result<-c()\n"
				+ "for(i in 1:n){\n"
					+ "line<-''\n"
					+ "for(names in colnames("+ dataset.getName() +")){\n"
						+ "if(is.na("+ dataset.getName() +"[i,names]) | as.character("+ dataset.getName() +"[i,names]) == ''){\n"
							+ "line<- paste(line, 'NA', sep='_;_')\n"
						+"} else {\n"
							+ "line<- paste(line, "+ dataset.getName() +"[i,names], sep='_;_')\n"
						+ "}\n"
					+ "}\n"
					+ "line<-substr(line,4,nchar(line))\n"
					+ "manual_result[i] <- line\n"
				+ "}";
		box.setScript(script);
		box.setOutputs(new String[]{"manual_result"});

		SmartAirService.Connect.getInstance().executeScript(box, new AsyncCallback<RScriptModel>() {

			@Override
			public void onSuccess(RScriptModel result) {
				showWaitPart(false);
				mainPanel.getLogPanel().addLog(result.getOutputLog());
				
				dataProviderBrowse.setList(new ArrayList<String>(Arrays.asList(result.getOutputVarstoString().get(0).split("\t"))));
				for(ListHandler<String> handler : sortHandlersBrowse){
					handler.setList(dataProviderBrowse.getList());
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				showWaitPart(false);
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
		});
	}
	
	@UiHandler("btnLinReg")
	public void onLinReg(ClickEvent event){
		initAlgoParamPanel("slr");
	}
	@UiHandler("btnMatCor")
	public void onMatCor(ClickEvent event){
		initAlgoParamPanel("cm");
	}
	@UiHandler("btnDecTree")
	public void onDecTree(ClickEvent event){
		initAlgoParamPanel("dt");
	}
	@UiHandler("btnKMeans")
	public void onKMeans(ClickEvent event){
		initAlgoParamPanel("km");
	}
	@UiHandler("btnHAC")
	public void onHAC(ClickEvent event){
		initAlgoParamPanel("hac");
	}

	
	public void initAlgoParamPanel(String param){
		paramPanel.clear();
		if(algoLaunchClick != null) algoLaunchClick.removeHandler();
		switch (param){
		case "slr" :
			createSLRParamPanel();
			btnLinReg.addStyleName(style.btnActive());
			btnMatCor.removeStyleName(style.btnActive());
			btnDecTree.removeStyleName(style.btnActive());
			btnKMeans.removeStyleName(style.btnActive());
			btnHAC.removeStyleName(style.btnActive());
			break;
		case "cm" :
			createCMParamPanel();
			btnLinReg.removeStyleName(style.btnActive());
			btnMatCor.addStyleName(style.btnActive());
			btnDecTree.removeStyleName(style.btnActive());
			btnKMeans.removeStyleName(style.btnActive());
			btnHAC.removeStyleName(style.btnActive());
			break;
		case "dt" :
			createDTParamPanel();
			btnLinReg.removeStyleName(style.btnActive());
			btnMatCor.removeStyleName(style.btnActive());
			btnDecTree.addStyleName(style.btnActive());
			btnKMeans.removeStyleName(style.btnActive());
			btnHAC.removeStyleName(style.btnActive());
			break;
		case "km" :
			createKMParamPanel();
			btnLinReg.removeStyleName(style.btnActive());
			btnMatCor.removeStyleName(style.btnActive());
			btnDecTree.removeStyleName(style.btnActive());
			btnKMeans.addStyleName(style.btnActive());
			btnHAC.removeStyleName(style.btnActive());
			break;
		case "hac" :
			createHACParamPanel();
			btnLinReg.removeStyleName(style.btnActive());
			btnMatCor.removeStyleName(style.btnActive());
			btnDecTree.removeStyleName(style.btnActive());
			btnKMeans.removeStyleName(style.btnActive());
			btnHAC.addStyleName(style.btnActive());
			break;
		default : 
			
		}
	}

	private void createHACParamPanel() {
		final ListBox lstDist = new ListBox();
		final ListBox lstCLust = new ListBox();
		final CheckBox chkRotate = new CheckBox(lblCnst.Rotate());
		chkRotate.addStyleName(style.chkbox());
		for (TypeDist dist : TypeDist.values()) {
			lstDist.addItem(dist.toString(), dist.toString());
		}
		for (TypeClust clust : TypeClust.values()) {
			lstCLust.addItem(clust.toString(), clust.toString());
		}
		lstDist.setStyleName(style.lstTypeView());
		lstCLust.setStyleName(style.lstTypeView());
		Label lblDist = new Label(lblCnst.DistanceMeasure());
		lblDist.addStyleName(style.label());
		paramPanel.add(lblDist);
		paramPanel.add(lstDist);
		Label lblClust = new Label(lblCnst.AgglomerationMethod());
		lblClust.addStyleName(style.label());
		paramPanel.add(lblClust);
		paramPanel.add(lstCLust);
		paramPanel.add(chkRotate);
		
		algoLaunchClick = btnAlgoLaunch.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				HAClustActivity activity = new HAClustActivity("hac");
				activity.setDatasetName(dataset.getName());

				activity.setDistType(TypeDist.valueOf(lstDist.getValue(lstDist.getSelectedIndex())));
				activity.setClustType(TypeClust.valueOf(lstCLust.getValue(lstCLust.getSelectedIndex())));
				
				activity.setRotate(chkRotate.getValue());
				showWaitPart(true);
				SmartAirService.Connect.getInstance().executeActivity(activity, new AsyncCallback<ActivityLog>() {
					
					@Override
					public void onSuccess(ActivityLog result) {
						showWaitPart(false);
						panelGraph.setUrl(result.getOutputs().get(0).getPath());

						StringBuffer genCode = new StringBuffer();
						genCode.append("\n### ").append(LabelsConstants.lblCnst.ActivityHACClustering()).append(" on ").append(dataset.getName());
						genCode.append(" - ").append(dtf.format(new Date())).append(" - ").append("\n");
						genCode.append(result.getScriptR());
						genCode.append("\n###\n");
						lastgeneratedCode = genCode.toString();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					}
				});
			}
		});
	}

	private void createKMParamPanel() {
		Label lblCol1 = new Label(lblCnst.Column());
		Label lblCol2 = new Label(lblCnst.Column());
		Label lblAlgo = new Label(lblCnst.Algorithm());
		Label lblNbClust = new Label(lblCnst.Centers());
		Label lblIterMax = new Label(lblCnst.IterMax());
		Label lblNStart = new Label(lblCnst.NStart());
		final ListBox lstcol1 = new ListBox();
		final ListBox lstcol2 = new ListBox();
		final ListBox lstAlgo = new ListBox();
		final TextBox txtnbclust = new TextBox();
		final TextBox txtitermax = new TextBox();
		final TextBox txtnstart = new TextBox();
		txtnbclust.addKeyPressHandler(integerBox);
		txtitermax.addKeyPressHandler(integerBox);
		txtnstart.addKeyPressHandler(integerBox);
		List<StatDataColumn> cols = datastats;
		for(StatDataColumn col : cols){
			if(!col.getMax().equals("NA")){
				lstcol1.addItem(col.getNameDatacolumn());
				lstcol2.addItem(col.getNameDatacolumn());
			}
		}
		for (TypeAlgoKMeans dist : TypeAlgoKMeans.values()) {
			lstAlgo.addItem(dist.toString(), dist.toString());
		}
		
		lblCol1.addStyleName(style.label());
		lblCol2.addStyleName(style.label());
		lblAlgo.addStyleName(style.label());
		lblNbClust.addStyleName(style.label());
		lblIterMax.addStyleName(style.label());
		lblNStart.addStyleName(style.label());
		lstcol1.setStyleName(style.lstTypeView());
		lstcol2.setStyleName(style.lstTypeView());
		lstAlgo.setStyleName(style.lstTypeView());
		txtnbclust.addStyleName(style.txt());
		txtitermax.addStyleName(style.txt());
		txtnstart.addStyleName(style.txt());
		
		paramPanel.add(lblCol1);
		paramPanel.add(lstcol1);
		paramPanel.add(lblCol2);
		paramPanel.add(lstcol2);
		paramPanel.add(lblAlgo);
		paramPanel.add(lstAlgo);
		paramPanel.add(lblNbClust);
		paramPanel.add(txtnbclust);
		paramPanel.add(lblIterMax);
		paramPanel.add(txtitermax);
		paramPanel.add(lblNStart);
		paramPanel.add(txtnstart);
		
		algoLaunchClick = btnAlgoLaunch.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				KmeansActivity activity = new KmeansActivity("km");
				activity.setDatasetName(dataset.getName());
				activity.setxColumnName(lstcol1.getValue(lstcol1.getSelectedIndex()));
				activity.setyColumnName(lstcol2.getValue(lstcol2.getSelectedIndex()));
				activity.setAlgoType(TypeAlgoKMeans.valueOf(lstAlgo.getValue(lstAlgo.getSelectedIndex())));
				activity.setNbClust(Integer.parseInt(txtnbclust.getValue()));
				activity.setIterMax(Integer.parseInt(txtitermax.getValue()));
				activity.setnStart(Integer.parseInt(txtnstart.getValue()));
				
				activity.setWithGraph(true);
				showWaitPart(true);
				SmartAirService.Connect.getInstance().executeActivity(activity, new AsyncCallback<ActivityLog>() {
					
					@Override
					public void onSuccess(ActivityLog result) {
						showWaitPart(false);
						panelGraph.setUrl(result.getOutputs().get(0).getPath());

						StringBuffer genCode = new StringBuffer();
						genCode.append("\n### ").append(LabelsConstants.lblCnst.ActivityKMeans()).append(" on ").append(dataset.getName());
						genCode.append(" - ").append(dtf.format(new Date())).append(" - ").append("\n");
						genCode.append(result.getScriptR());
						genCode.append("\n###\n");
						lastgeneratedCode = genCode.toString();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					}
				});
			}
		});
		
	}

	private void createDTParamPanel() {
		Label lblMethod = new Label(lblCnst.Method());
		Label lblNumColumns = new Label(lblCnst.NumColumns());
		Label lblNonNumColumns = new Label(lblCnst.NonNumColumns());
		final ListBox lstMethod = new ListBox();
		final ListBox lstXCol = new ListBox();
		final ColumnAirPanel airpanel = new ColumnAirPanel(true);
		
		for (TypeRPart met : TypeRPart.values()) {
			lstMethod.addItem(met.toString(), met.toString());
		}
		List<DataColumn> cols = new ArrayList<DataColumn>(dataset.getMetacolumns());
		List<DataColumn> numCols = new ArrayList<DataColumn>();
		for(DataColumn col : cols){
			if(quantCols.contains(col.getColumnTypeName())){
				numCols.add(col);
			}
			if(qualCols.contains(col.getColumnTypeName())){
				lstXCol.addItem(col.getColumnLabel());
			}
		}
		airpanel.init(numCols, null);
		
		lstMethod.setStyleName(style.lstTypeView());
		lstXCol.setStyleName(style.lstTypeView());
		lblMethod.addStyleName(style.label());
		lblNumColumns.addStyleName(style.label());
		lblNonNumColumns.addStyleName(style.label());
		
		paramPanel.add(lblMethod);
		paramPanel.add(lstMethod);
		paramPanel.add(lblNonNumColumns);
		paramPanel.add(lstXCol);
		paramPanel.add(lblNumColumns);
		paramPanel.add(airpanel);
		
		algoLaunchClick = btnAlgoLaunch.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				DecisionTreeActivity activity = new DecisionTreeActivity("dt");
				activity.setDatasetName(dataset.getName());
				
				List<String> colnames = new ArrayList<String>();
				for(DataColumn col : airpanel.getSelectedColumns()){
					colnames.add(col.getColumnLabel());
				}
				activity.setNumColnames(colnames);
				colnames = new ArrayList<String>();
				for(DataColumn col : airpanel.getAllColumns()){
					colnames.add(col.getColumnLabel());
				}
				activity.setyColumnNames(colnames);
				activity.setxColumnName(lstXCol.getValue(lstXCol.getSelectedIndex()));
				activity.setRpartType(TypeRPart.valueOf(lstMethod.getValue(lstMethod.getSelectedIndex())));
				
				activity.setWithGraph(true);
				showWaitPart(true);
				SmartAirService.Connect.getInstance().executeActivity(activity, new AsyncCallback<ActivityLog>() {
					
					@Override
					public void onSuccess(ActivityLog result) {
						showWaitPart(false);
						panelGraph.setUrl(result.getOutputs().get(0).getPath());

						StringBuffer genCode = new StringBuffer();
						genCode.append("\n### ").append(LabelsConstants.lblCnst.ActivityDecisionTree()).append(" on ").append(dataset.getName());
						genCode.append(" - ").append(dtf.format(new Date())).append(" - ").append("\n");
						genCode.append(result.getScriptR());
						genCode.append("\n###\n");
						lastgeneratedCode = genCode.toString();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					}
				});
			}
		});
		
	}

	private void createCMParamPanel() {
		Label lblMethod = new Label(lblCnst.CorrelationCoef());
		Label lblNumColumns = new Label(lblCnst.NumColumns());
		final ListBox lstMethod = new ListBox();
		final ColumnAirPanel airpanel = new ColumnAirPanel(true);
		
		for (TypeCor met : TypeCor.values()) {
			lstMethod.addItem(met.toString(), met.toString());
		}
		List<DataColumn> cols = new ArrayList<DataColumn>(dataset.getMetacolumns());
		List<DataColumn> numCols = new ArrayList<DataColumn>();
		for(DataColumn col : cols){
			if(quantCols.contains(col.getColumnTypeName())){
				numCols.add(col);
			}
		}
		airpanel.init(numCols, null);
		
		lstMethod.setStyleName(style.lstTypeView());
		lblMethod.addStyleName(style.label());
		lblNumColumns.addStyleName(style.label());
		
		paramPanel.add(lblMethod);
		paramPanel.add(lstMethod);
		paramPanel.add(lblNumColumns);
		paramPanel.add(airpanel);
		
		algoLaunchClick = btnAlgoLaunch.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CorMatrixActivity activity = new CorMatrixActivity("cm");
				activity.setDatasetName(dataset.getName());
				List<String> colnames = new ArrayList<String>();
				for(DataColumn col : airpanel.getSelectedColumns()){
					colnames.add(col.getColumnLabel());
				}
				activity.setColnames(colnames);
				activity.setCorType(TypeCor.valueOf(lstMethod.getValue(lstMethod.getSelectedIndex())));
				
				activity.setWithGraph(true);
				showWaitPart(true);
				SmartAirService.Connect.getInstance().executeActivity(activity, new AsyncCallback<ActivityLog>() {
					
					@Override
					public void onSuccess(ActivityLog result) {
						showWaitPart(false);
						panelGraph.setUrl(result.getOutputs().get(0).getPath());
 
						StringBuffer genCode = new StringBuffer();
						genCode.append("\n### ").append(LabelsConstants.lblCnst.ActivityCorrelationMatrix()).append(" on ").append(dataset.getName());
						genCode.append(" - ").append(dtf.format(new Date())).append(" - ").append("\n");
						genCode.append(result.getScriptR());
						genCode.append("\n###\n");
						lastgeneratedCode = genCode.toString();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					}
				});
			}
		});
		
	}

	private void createSLRParamPanel() {
		final ListBox lstcol1 = new ListBox();
		final ListBox lstcol2 = new ListBox();
		List<StatDataColumn> cols = datastats;
		for(StatDataColumn col : cols){
			if(!col.getMax().equals("NA")){
				lstcol1.addItem(col.getNameDatacolumn());
				lstcol2.addItem(col.getNameDatacolumn());
			}
		}
		lstcol1.setStyleName(style.lstTypeView());
		lstcol2.setStyleName(style.lstTypeView());
		paramPanel.add(lstcol1);
		paramPanel.add(lstcol2);
		
		algoLaunchClick = btnAlgoLaunch.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				SimpleLinearRegActivity activity = new SimpleLinearRegActivity("slr");
				activity.setDatasetName(dataset.getName());
				activity.setxColumnName(lstcol1.getValue(lstcol1.getSelectedIndex()));
				activity.setyColumnName(lstcol2.getValue(lstcol2.getSelectedIndex()));
				
				activity.setWithGraph(true);
				showWaitPart(true);
				SmartAirService.Connect.getInstance().executeActivity(activity, new AsyncCallback<ActivityLog>() {
					
					@Override
					public void onSuccess(ActivityLog result) {
						showWaitPart(false);
						panelGraph.setUrl(result.getOutputs().get(0).getPath());
						
						StringBuffer genCode = new StringBuffer();
						genCode.append("\n### ").append(LabelsConstants.lblCnst.ActivitySimpleLinearReg()).append(" on ").append(dataset.getName());
						genCode.append(" - ").append(dtf.format(new Date())).append(" - ").append("\n");
						genCode.append(result.getScriptR());
						genCode.append("\n###\n");
						lastgeneratedCode = genCode.toString();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						showWaitPart(false);
						caught.printStackTrace();

						ExceptionManager.getInstance().handleException(caught, caught.getMessage());
					}
				});
			}
		});
	}
	
//	@UiHandler("lstTypeAlgo")
//	public void onChangeAlgo(ChangeEvent event) {
//		initAlgoParamPanel();
//	}
	
	@UiHandler("exportCode")
	public void onExportCode(ClickEvent event){
		
		workspacePanel.writeGeneratedCode(lastgeneratedCode);
	}
}
