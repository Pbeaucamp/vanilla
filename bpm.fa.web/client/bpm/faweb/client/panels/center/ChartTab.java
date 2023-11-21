package bpm.faweb.client.panels.center;

import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.FaWebDragController;
import bpm.faweb.client.panels.center.chart.ChartPanel;
import bpm.faweb.shared.ChartParameters;
import bpm.gwt.commons.client.charts.ChartRenderPanel;
import bpm.gwt.commons.client.panels.Tab;
import bpm.gwt.commons.client.panels.TabManager;
import bpm.gwt.commons.client.utils.VanillaCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ChartTab extends Tab {

	private static ChartTabUiBinder uiBinder = GWT.create(ChartTabUiBinder.class);

	interface ChartTabUiBinder extends UiBinder<Widget, ChartTab> {
	}

	interface MyStyle extends CssResource {
		String selected();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel panelToolbar;

	@UiField
	SimplePanel panelContent;

	@UiField
	Image btnClear;

	private MainPanel mainPanel;
	private ChartPanel chartPanel;

	public ChartTab(TabManager tabManager, MainPanel mainPanel, FaWebDragController dragCtrl) {
		super(tabManager, FreeAnalysisWeb.LBL.chartsViewer(), true);
		add(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		
		chartPanel = new ChartPanel(mainPanel, dragCtrl);
		panelContent.setWidget(chartPanel);

		panelToolbar.addStyleName(VanillaCSS.TAB_TOOLBAR);
	}

	@UiHandler("btnClear")
	public void onClear(ClickEvent e) {
		chartPanel.clearChartData();
	}

	public void openChart(String title, String type, String graphe, List<String> series, List<String> groups, List<String> filters, String measure) {
		chartPanel.clearChartData();
		this.chartPanel.openChart(title, type, graphe, series, groups, filters, measure);
	}

	public ChartParameters getChartParameters() {
		return chartPanel.getChartParameters();
	}

	public String getChartDatas() {
		return chartPanel.getImageChart();
	}

	public void clearChart() {
		chartPanel.clearChartData();
	}
	
	public void buildOverviewChart() {
		if (isChartAvailable()) {
			ChartRenderPanel chartRenderPanel = new ChartRenderPanel();
			chartRenderPanel.setSize("100%", "100%");
			chartRenderPanel.loadChart(chartPanel.getCurrentChart(), chartPanel.getCurrentType());
			
			mainPanel.getDisplayPanel().loadOverviewChart(chartRenderPanel);
		}
	}

	public boolean isChartAvailable() {
		return chartPanel != null && chartPanel.isComplete();
	}
}
