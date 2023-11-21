package bpm.freematrix.reborn.web.client.main.home.information;

import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.MetricMap;
import bpm.freematrix.reborn.web.client.main.home.HomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class InformationTabs extends Composite  {

	private static InformationTabsUiBinder uiBinder = GWT
			.create(InformationTabsUiBinder.class);

	interface InformationTabsUiBinder extends UiBinder<Widget, InformationTabs> {
	}

	@UiField Button pieChart, details, evolution, gauge, map, gantt;//, strategy;
	
	private boolean isAxis;
	private InformationViewer viewer;
	private Object object;
	private Metric metric;
	private HomeView homeView;
	
	public InformationTabs(boolean isAxis, Object object, InformationViewer viewer, Metric metric, HomeView homeView) {
		initWidget(uiBinder.createAndBindUi(this));
		this.isAxis = isAxis;
		this.viewer = viewer;
		this.object = object;
		this.metric = metric;
		this.homeView = homeView;
		initTabs();
	}
	
	public InformationTabs(){}
	
	private void initTabs(){
		
		if(isAxis){
			details.removeFromParent();
			evolution.removeFromParent();
			gauge.removeFromParent();
			gantt.removeFromParent();
			try {
				
				if(object instanceof Level) {
					Level level = (Level) object;
					if(!(level.getGeoColumnId() != null && !level.getGeoColumnId().isEmpty() && level.getMapDatasetId() != null)) {
						map.removeFromParent();
					}
				}
				 
			} catch (Exception e) {
			}
			
			//strategy.removeFromParent();
		}else{
			pieChart.removeFromParent();
			map.removeFromParent();
		}
	}
	
	@UiHandler("pieChart")
	void onGeneratePieChart(ClickEvent e){
		viewer.generateAxisPie(homeView.getFilterDate(), (Level) object, metric);
	}
	
	@UiHandler("details")
	void onGenerateDetails(ClickEvent e){
		viewer.generateMetricDetail((Metric) object);
	}
	
	@UiHandler("gauge")
	void onGenerateGauge(ClickEvent e){
		viewer.generateMetricGauge(homeView.getMetricValue());
	}
	
	@UiHandler("evolution")
	void onGenerateEvolution(ClickEvent e){
		viewer.generateMetricEvolution(homeView.getFilterDate(), (Metric)object);
	}

	@UiHandler("collaboration")
	void onGenerateCollaboration(ClickEvent e){
		viewer.generateMetricCollaboration(homeView);
	}
	
//	@UiHandler("strategy")
//	void onGenerateStrategy(ClickEvent e){
//		viewer.generateMetricStrategy(homeView);
//	}
	
	@UiHandler("gantt")
	void onGenerateGantt(ClickEvent e){
		viewer.generateMetricGantt(homeView);
	}
	
	@UiHandler("map")
	void onGenerateMap(ClickEvent e){
		viewer.generateMetricMap(homeView, metric, (Level) object, homeView.getFilterDate());
	}
}
