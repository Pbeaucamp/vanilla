package bpm.freematrix.reborn.web.client.main.home;

import java.util.Date;

import bpm.fm.api.model.Metric;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.main.FreeMatrixMain;
import bpm.freematrix.reborn.web.client.main.home.application.ApplicationPanel;
import bpm.freematrix.reborn.web.client.main.home.information.InformationPanel;
import bpm.freematrix.reborn.web.client.main.home.metrics.MetricsPanel;
import bpm.gwt.commons.client.panels.Splitter2;
import bpm.vanilla.platform.core.beans.Group;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class HomeView extends Composite {

	private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);

	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}

	interface MyStyle extends CssResource {
		String splitter();
	}

	@UiField
	MyStyle style;
	
	@UiField HTMLPanel applicationPanel, dashboardPanel, infoPanel, rightPanel;
	
	@UiField
	SimplePanel panelSplitter;

	private FreeMatrixMain mainParent;
	
	private Metric metric;
	private MetricValue metricValue;
	private Date filterDate;
	private InformationPanel informationPanel;
	
	private MetricsPanel metricPanel;
	private ApplicationPanel axisPanel;
	
	public HomeView(FreeMatrixMain freeMatrixMain) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainParent = freeMatrixMain;
		
		metricPanel = new MetricsPanel(this);
		axisPanel = new ApplicationPanel(this);
		
		applicationPanel.add(axisPanel);
		dashboardPanel.add(metricPanel);
		
		panelSplitter.setWidget(new Splitter2(dashboardPanel, infoPanel, style.splitter()));
		
	}

	public void generateInformationPanel(boolean isAxis, Object object, MetricValue metricValue){
		this.metricValue = metricValue;
		informationPanel = new InformationPanel(isAxis, object, metric, this);
		infoPanel.clear();
		//infoPanel.add(panelSplitter);
		infoPanel.add(informationPanel);
		
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public Date getFilterDate() {
		return filterDate;
	}

	public void setFilterDate(Date filterDate) {
		this.filterDate = filterDate;
	}
	
	public MetricValue getMetricValue(){
		return metricValue;
	}

	public void filterChange(Group group, Observatory obs, Theme theme) {
		metricPanel.generateDashboard();
		axisPanel.createAxisTree();
	}
	
	public void collapseApplicationPanel(boolean isCollapse){
		axisPanel.changeSize(isCollapse);
		if(isCollapse){
			rightPanel.getElement().getStyle().setLeft(305, Unit.PX );
		} else {
			rightPanel.getElement().getStyle().setLeft(45, Unit.PX );
		}
		
	}
	
	public FreeMatrixMain getMainParent() {
		return mainParent;
	}
	
	
// POUR BOUTONS PLEIN ECRAN
//	
//	
//	public void changeRightPanelView(String click){
//		
//		if(click.equals("fullDashboardCLick")){
//			metricPanel.changeDashboardSize("DashboardView");
//			if(infoPanel.getWidgetCount()>0){
//				informationPanel.changeInfoSize("DashboardView");
//			}
//			infoPanel.setHeight("1%");
//		} else if (click.equals("fullInfoCLick")){
//			metricPanel.changeDashboardSize("InfoView");
//			if(infoPanel.getWidgetCount()>0){
//				informationPanel.changeInfoSize("InfoView");
//			}
//			infoPanel.setHeight("100%");
//			float fixedHeight = infoPanel.getOffsetHeight()-40;
//			float panelHeight = rightPanel.getOffsetHeight();
//			float responsiveHeight = (float) (fixedHeight/panelHeight *100);  //hauteur en pourcentage
//			int finalHeight = (int) responsiveHeight - 2; //pour etre sur
//			infoPanel.setHeight(finalHeight + "%");
//		} else if(click.equals("restoreDashboardClick") || click.equals("restoreInfoClick")) {
//			metricPanel.changeDashboardSize("MiddleView");
//			if(infoPanel.getWidgetCount()>0){
//				informationPanel.changeInfoSize("MiddleView");
//			}
//			infoPanel.setHeight("50%");
//		} else {
//			metricPanel.changeDashboardSize("MiddleView");
//			if(infoPanel.getWidgetCount()>0){
//				informationPanel.changeInfoSize("MiddleView");
//			}
//			infoPanel.setHeight("50%");
//		}
//		
//	}
	
}
