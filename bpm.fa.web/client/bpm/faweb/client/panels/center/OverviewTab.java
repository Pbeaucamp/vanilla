package bpm.faweb.client.panels.center;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dialog.SaveDialog;
import bpm.faweb.client.panels.center.grid.CubeView;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.client.utils.ForCalcul;
import bpm.faweb.shared.GroupChart;
import bpm.faweb.shared.TreeParentDTO;
import bpm.faweb.shared.infoscube.Calcul;
import bpm.faweb.shared.infoscube.GridCube;
import bpm.gwt.commons.client.charts.ChartRenderPanel;
import bpm.gwt.commons.client.charts.FusionChartsPanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.utils.VanillaCSS;
import bpm.gwt.commons.shared.utils.TypeShare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OverviewTab extends Tab implements ICubeViewer {
	
	private static OverviewTabUiBinder uiBinder = GWT.create(OverviewTabUiBinder.class);

	interface OverviewTabUiBinder extends UiBinder<Widget, OverviewTab> {
	}

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent;
	
	private MainPanel mainCompParent;

	private VerticalPanel overviewPanel;
	private CubeView gridOverview;
	private SimplePanel panelChart;

	public OverviewTab(TabManager tabManager, MainPanel mainCompParent) {
		super(tabManager, FreeAnalysisWeb.LBL.Overview(), true);
		add(uiBinder.createAndBindUi(this));
		this.mainCompParent = mainCompParent;

		overviewPanel = new VerticalPanel();
		overviewPanel.setSize("100%", "100%");
		overviewPanel.setSpacing(10);
		
		panelContent.setWidget(overviewPanel);
		
		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}

	public void loadOverview() {
		loadOverview(false, null, null, null, null);
	}

	public void loadOverviewAfterLoad(String title, String chartType, String graphe, List<GroupChart> groupsChart) {
		loadOverview(true, title, chartType, graphe, groupsChart);
	}
	
	private void loadOverview(boolean afterLoad, String title, String chartType, String graphe, List<GroupChart> groupsChart) {
		showWaitPart(true);
		overviewPanel.clear();
		
		panelChart = new SimplePanel();
		overviewPanel.add(panelChart);
		
		if(afterLoad) {
			if (mainCompParent.getDisplayPanel().isChartAvailable()) {
				mainCompParent.getDisplayPanel().buildOverviewChart();
			}
		}
		else {
			if (mainCompParent.getDisplayPanel().isChartAvailable()) {
				mainCompParent.getDisplayPanel().buildOverviewChart();
			}
		}
		
		if (mainCompParent.isCalcul()) {
			gridOverview = new CubeView(mainCompParent, this, mainCompParent.getInfosReport().getGrid(), true, false);
			overviewPanel.add(gridOverview);
			List<Calcul> calculs = mainCompParent.getCalculs();
			for (Calcul c : calculs) {
				if (c.getOperator() <= 1) {
					ForCalcul.associativeCalcul(mainCompParent, c, gridOverview);
				}
				else {
					ForCalcul.distributiveCalcul(mainCompParent, c, gridOverview);
				}
			}
		}
		else {
			CubeView gridOverview = new CubeView(mainCompParent, this, mainCompParent.getInfosReport().getGrid(), false, false);
			overviewPanel.add(gridOverview);
		}
		
		showWaitPart(false);
	}
	
	public void loadGrid(GridCube gc, boolean isCalcul) {
		CubeView gridOverview = new CubeView(mainCompParent, this, gc, isCalcul, false);

		overviewPanel.clear();

		if (mainCompParent.getDisplayPanel().isChartAvailable()) {
			panelChart = new SimplePanel();
			overviewPanel.add(panelChart);
			
			mainCompParent.getDisplayPanel().buildOverviewChart();
		}
		
		overviewPanel.add(gridOverview);
	}

	public void loadOverviewChart(Composite chart) {
		panelChart.clear();
		panelChart.setHeight("400px");
		panelChart.add(chart);
		((ChartRenderPanel)chart).refresh();;
	}
	
	public void clearPanel(){
		overviewPanel.clear();
	}

	public CubeView getGridOverview() {
		return gridOverview;
	}
	
	@UiHandler("imgSave")
	public void onSaveClick(ClickEvent event) {
		mainCompParent.showWaitPart(true);
		int type = FaWebService.FAV;

		FaWebService.Connect.getInstance().getRepositories(mainCompParent.getKeySession(), type, new AsyncCallback<TreeParentDTO>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				mainCompParent.showWaitPart(false);
			}

			public void onSuccess(TreeParentDTO result) {
				mainCompParent.showWaitPart(false);
				
				if (result != null && result instanceof TreeParentDTO) {
					SaveDialog dial = new SaveDialog(mainCompParent, null, null, false);
					dial.center();
				}
			}
		});
	}
	
	@UiHandler("imgExport")
	public void onExportClick(ClickEvent event) {
		mainCompParent.getDisplayPanel().getCubeViewerTab().openShare(TypeShare.EXPORT);
	}

	@Override
	public boolean isOn() {
		return true;
	}

	@Override
	public void addGrid(CubeView grid) { }
}
