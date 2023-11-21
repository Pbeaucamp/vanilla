package bpm.freematrix.reborn.web.client.main.home.information;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.freematrix.reborn.web.client.dialog.WaitDialog;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.main.home.HomeView;
import bpm.freematrix.reborn.web.client.main.home.application.details.DetailsApplications;
import bpm.freematrix.reborn.web.client.main.home.charts.CollaborationPanel;
import bpm.freematrix.reborn.web.client.main.home.charts.Details;
import bpm.freematrix.reborn.web.client.main.home.charts.GanttChart;
import bpm.freematrix.reborn.web.client.main.home.charts.StrategyChart;
import bpm.freematrix.reborn.web.client.main.map.MapPanel;
import bpm.freematrix.reborn.web.client.services.MetricService;
import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.charts.ChartJsPanel;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ChartValue;
import bpm.gwt.commons.client.charts.FusionChartsPanel;
import bpm.gwt.commons.client.charts.ValueGauge;
import bpm.gwt.commons.client.charts.ValueMultiSerie;
import bpm.gwt.commons.client.charts.ValueSimpleSerie;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class InformationViewer extends Composite {

	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd-MM-yyyy");
	
	private static InformationViewerUiBinder uiBinder = GWT
			.create(InformationViewerUiBinder.class);

	interface InformationViewerUiBinder extends
			UiBinder<Widget, InformationViewer> {
	}
	
	@UiField HTMLPanel graphPanel;

	private Level level;
	
	public InformationViewer() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	
	public void generateApplicationDetails(Date date, Axis axis, Metric metric ){
		graphPanel.clear();
		if(metric==null){
			Window.alert(LabelConstants.lblCnst.selectMetricFirst());
		}else{
			graphPanel.add(new DetailsApplications(date, axis, metric));
		}
	}
	
	public void generateAxisPie(Date date, final Level level, final Metric metric){
		graphPanel.clear();
		if(metric==null){
			Window.alert(LabelConstants.lblCnst.selectMetricFirst());
		}else{
			WaitDialog.showWaitPart(true);
			
			MetricService.Connection.getInstance().getValuesByDateAxisMetric(date, level, metric.getId(), FreeMatrixHeader.getInstance().getSelectedGroup(), new AsyncCallback<List<MetricValue>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					Window.alert(caught.getMessage());
					WaitDialog.showWaitPart(false);
				}

				@Override
				public void onSuccess(List<MetricValue> result) {
					try {
						
						ChartObject object = new ChartObject();
						object.setTitle(LabelConstants.lblCnst.repartition() + " : " + metric.getName());
						object.setHeight(100);
						object.setHeightUnit(Unit.PCT);
						object.setWidth(100);
						object.setHeightUnit(Unit.PCT);
						
						object.setxAxisName(level.getName());
						object.setyAxisName(LabelConstants.lblCnst.Value());
						
						int levelIndex = level.getParent().getChildren().indexOf(level);
						
						List<ChartValue> values = new ArrayList<ChartValue>();
						for(MetricValue v : result) {
							ValueSimpleSerie val = new ValueSimpleSerie();
							val.setCategory(v.getAxis().get(levelIndex).getLabel().replace("'", ""));
							val.setValue(v.getValue());
							values.add(val);
						}
						
						object.setValues(values);
						
						ChartJsPanel panel = new ChartJsPanel(object, ChartObject.TYPE_MULTI, ChartObject.RENDERER_PIE);
//						FusionChartsPanel panel = new FusionChartsPanel(object, ChartObject.TYPE_SIMPLE, ChartObject.RENDERER_PIE);
						graphPanel.add(panel);
						
//						panel.renderCharts();
					} catch (Exception e) {
						e.printStackTrace();
						Window.alert(e.getMessage());
						WaitDialog.showWaitPart(false);
					}
					WaitDialog.showWaitPart(false);
				}
				
			});
			
			setLevel(level);
		}
	}
	


	
	public void generateMetricDetail(Metric metric){
		graphPanel.clear();
		graphPanel.add(new Details(metric));
	}
	
	
	public void generateMetricGauge(MetricValue metricValue){
		graphPanel.clear();
		
		WaitDialog.showWaitPart(true);
		
		MetricService.Connection.getInstance().getGaugeValues(metricValue, FreeMatrixHeader.getInstance().getSelectedGroup(), new AsyncCallback<HashMap<String, MetricValue>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(HashMap<String, MetricValue> result) {
				try {
					ChartObject object = new ChartObject();
					object.setTitle(result.get("current").getMetric().getName());
					object.setHeight(100);
					object.setHeightUnit(Unit.PCT);
					object.setWidth(700);
					object.setHeightUnit(Unit.PX);
					List<ChartValue> values = new ArrayList<ChartValue>();
					
					for(String pos : result.keySet()) {
						
						MetricValue v = result.get(pos);
						
						ValueGauge val = new ValueGauge();
						val.setPosition(pos);
						val.setIncrease(v.getMetric().getDirection().equals(Metric.DIRECTIONS.get(Metric.DIRECTION_TOP)));
						val.setMaximum(v.getMaximum());
						val.setValue(v.getValue());
						val.setMinimum(v.getMinimum());
						val.setObjective(v.getObjective());
						values.add(val);
					}
					object.setValues(values);
					
					FusionChartsPanel panel = new FusionChartsPanel(object, ChartObject.TYPE_GAUGE, ChartObject.RENDERER_GAUGE_ANGULAR);
					graphPanel.add(panel);
					
					panel.renderCharts();
					
					WaitDialog.showWaitPart(false);
				} catch (Exception e) {
					WaitDialog.showWaitPart(false);
					e.printStackTrace();
				}
			}


		});
		

	}
	
	public void generateMetricMap(HomeView homeView, Metric metric, Level object, Date filterDate) {
		graphPanel.clear();
		
		WaitDialog.showWaitPart(true);
	
		MetricService.Connection.getInstance().getMapValues(filterDate, metric.getId(), object, FreeMatrixHeader.getInstance().getSelectedGroup(), new AsyncCallback<List<MapZoneValue>>() {
	
			@Override
			public void onFailure(Throwable caught) {
				WaitDialog.showWaitPart(false);
				ExceptionManager.getInstance().handleException(caught, caught.getMessage());
			}
	
			@Override
			public void onSuccess(List<MapZoneValue> result) {
				
				try {
					MapPanel map = new MapPanel(result);
					
					graphPanel.add(map);
					WaitDialog.showWaitPart(false);
				} catch (Exception e) {
					e.printStackTrace();
					WaitDialog.showWaitPart(false);
				}
				
				
			}
		});
		
	}
	
	public void generateMetricEvolution(Date date, final Metric metric){
		
		if(metric.getFactTable() instanceof FactTable) {
			if(((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_HOURLY)) {
				dateFormat = DateTimeFormat.getFormat("dd-MM-yyyy hh:mm");
			}
			else if(((FactTable)metric.getFactTable()).getPeriodicity().equals(FactTable.PERIODICITY_MINUTE)) {
				dateFormat = DateTimeFormat.getFormat("dd-MM-yyyy hh:mm");
			}
		}
		
		graphPanel.clear();
		WaitDialog.showWaitPart(true);
		MetricService.Connection.getInstance().getValueAndPreviousByMetricAndDate(date, metric.getId(), FreeMatrixHeader.getInstance().getSelectedGroup(), FreeMatrixHeader.getInstance().getSelectedTheme(), new AsyncCallback<List<MetricValue>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				Window.alert(caught.getMessage());
				WaitDialog.showWaitPart(false);
			}

			@Override
			public void onSuccess(List<MetricValue> result) {
				try {
					ChartObject object = new ChartObject();
					object.setTitle(metric.getName());
					object.setHeight(100);
					object.setHeightUnit(Unit.PCT);
					object.setWidth(100);
					object.setHeightUnit(Unit.PCT);
					
					object.setxAxisName("Date");
					object.setyAxisName(LabelConstants.lblCnst.Value());
					
					List<ChartValue> values = new ArrayList<ChartValue>();
					
					ValueMultiSerie serieVal = new ValueMultiSerie();
					serieVal.setSerieName(LabelConstants.lblCnst.Value());
					for(MetricValue v : result) {
						ValueSimpleSerie val = new ValueSimpleSerie();
						val.setCategory(dateFormat.format(v.getDate()));
						val.setValue(v.getValue());
						serieVal.addValue(val);
					}
					
					ValueMultiSerie serieObj = new ValueMultiSerie();
					serieObj.setSerieName(LabelConstants.lblCnst.Objective());
					for(MetricValue v : result) {
						ValueSimpleSerie val = new ValueSimpleSerie();
						val.setCategory(dateFormat.format(v.getDate()));
						val.setValue(v.getObjective());
						serieObj.addValue(val);
					}
					
					values.add(serieVal);
					values.add(serieObj);
					object.setValues(values);
					
					ChartJsPanel panel = new ChartJsPanel(object, ChartObject.TYPE_MULTI, ChartObject.RENDERER_MULTI_LINE);
//					FusionChartsPanel panel = new FusionChartsPanel(object, ChartObject.TYPE_MULTI, ChartObject.RENDERER_MULTI_LINE);

					graphPanel.add(panel);
					
//					panel.renderCharts();
				} catch (Exception e) {
					e.printStackTrace();
					Window.alert(e.getMessage());
					WaitDialog.showWaitPart(false);
				}
				WaitDialog.showWaitPart(false);
			}
			
		});
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}


	public void generateMetricCollaboration(HomeView homeView) {
		graphPanel.clear();
		graphPanel.add(new CollaborationPanel(homeView.getMetricValue()));
	}


	public void generateMetricStrategy(HomeView homeView) {
		graphPanel.clear();
		
		StrategyChart chart = new StrategyChart(homeView.getMetricValue());
		graphPanel.add(chart);
		
	}


	public void generateMetricGantt(HomeView homeView) {
		graphPanel.clear();
		
		GanttChart chart = new GanttChart(homeView.getMetricValue());
		graphPanel.add(chart);
	}

	
}
