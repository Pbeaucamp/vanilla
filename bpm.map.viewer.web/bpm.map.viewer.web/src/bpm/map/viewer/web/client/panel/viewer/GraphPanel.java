package bpm.map.viewer.web.client.panel.viewer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.fm.api.model.ComplexObjectViewer;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Metric;
import bpm.fm.api.model.utils.MapZoneValue;
import bpm.fm.api.model.utils.MetricValue;
import bpm.gwt.commons.client.charts.ChartObject;
import bpm.gwt.commons.client.charts.ChartValue;
import bpm.gwt.commons.client.charts.FusionChartsPanel;
import bpm.gwt.commons.client.charts.ValueMultiSerie;
import bpm.gwt.commons.client.charts.ValueSimpleSerie;
import bpm.map.viewer.web.client.I18N.LabelsConstants;
import bpm.map.viewer.web.client.panel.ViewerPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.core.java.util.Collections;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class GraphPanel extends Composite {

	private static GraphPanelUiBinder uiBinder = GWT.create(GraphPanelUiBinder.class);
	
	public static LabelsConstants lblCnst = (LabelsConstants) GWT
			.create(LabelsConstants.class);

	interface GraphPanelUiBinder extends UiBinder<Widget, GraphPanel> {
	}
	
	@UiField
	SimplePanel graphPanel;
	
	@UiField
	ListBox lstData;
	
	private ViewerPanel viewer;
	private List<Level> levels;
	private List<Metric> metrics = new ArrayList<Metric>();
	private Metric selectedMetric;
	private List<MapZoneValue> mapzonevalues = new ArrayList<MapZoneValue>();
	private List<ComplexObjectViewer> viewers;
	private String graphType;

	public GraphPanel(ViewerPanel viewer, List<Level> levels, List<Metric> metrics, List<ComplexObjectViewer> viewers) {
		initWidget(uiBinder.createAndBindUi(this));
		this.viewer = viewer;
		this.levels = levels; 
		this.metrics = metrics;
		this.viewers = viewers;
		if(viewer.getActionDate() == ViewerPanel.ActionDate.VALEUR){
			this.graphType = ChartObject.RENDERER_PIE;
		} else {
			this.graphType = ChartObject.RENDERER_MULTI_COLUMN;
		}
		
		
		for(Metric met: metrics){
			lstData.addItem(met.getName());
		}
		lstData.setItemSelected(0, true);
		this.selectedMetric = metrics.get(lstData.getSelectedIndex());
		
		loadGraph();
	}
	
	
	public void loadGraph(){
		graphPanel.clear();
		mapzonevalues.clear();
		try {
			for(ComplexObjectViewer cov : viewers){
				if(cov.getMetric().getId() == selectedMetric.getId()){
					mapzonevalues.addAll(cov.getMapvalues());
				}
			}
			
			List<MetricValue> result = new ArrayList<MetricValue>();
			
			ChartObject object = new ChartObject();
			object.setTitle(lblCnst.repartition() + " : " + lstData.getValue(lstData.getSelectedIndex()));
			object.setHeight(100);
			object.setHeightUnit(Unit.PCT);
			object.setWidth(100);
			object.setHeightUnit(Unit.PCT);
			
			object.setxAxisName(levels.get(0).getName());
			object.setyAxisName("Value");
			
			int levelIndex = levels.get(0).getParent().getChildren().indexOf(levels.get(0));
			
			if(viewer.getActionDate() == ViewerPanel.ActionDate.VALEUR){
				for(MapZoneValue mzv : mapzonevalues){
					result.add(mzv.getValues().get(0));
				}
				
				List<ChartValue> values = new ArrayList<ChartValue>();
				for(MetricValue v : result) {
					ValueSimpleSerie val = new ValueSimpleSerie();
					val.setCategory(v.getAxis().get(levelIndex).getLabel().replace("'", ""));
					val.setValue(v.getValue());
					values.add(val);
				}
				
				object.setValues(values);
				
				FusionChartsPanel panel = new FusionChartsPanel(object, ChartObject.TYPE_SIMPLE, graphType);
				graphPanel.add(panel);
				
				panel.renderCharts();
				
				graphType = panel.getDefaultRenderer();
				
			} else {
				Set<Date> datesSet = new HashSet<Date>();
				
				for(MapZoneValue mzv : mapzonevalues){
					for(MetricValue mv : mzv.getValues()){
						datesSet.add(mv.getDate());
					}
				}
				List<Date> dates = new ArrayList<Date>(datesSet);
				java.util.Collections.sort(dates);
				
				boolean hasValue = false;
				List<ChartValue> values = new ArrayList<ChartValue>();
				for(Date date : dates){
					ValueMultiSerie val = new ValueMultiSerie();
					val.setSerieName(DateTimeFormat.getFormat("dd MMMM yyyy").format(date));
					for(MapZoneValue mzv : mapzonevalues){
						hasValue = false;
						for(MetricValue mv : mzv.getValues()){
							if(mv.getDate().equals(date)){
								ValueSimpleSerie sval = new ValueSimpleSerie();
								sval.setCategory(mv.getAxis().get(levelIndex).getLabel().replace("'", ""));
								sval.setValue(mv.getValue());
								val.addValue(sval);
								hasValue = true;
							}
						}
						if(!hasValue){
							ValueSimpleSerie sval = new ValueSimpleSerie();
							sval.setCategory(mzv.getValues().get(0).getAxis().get(levelIndex).getLabel().replace("'", ""));
							sval.setValue(null);
							val.addValue(sval);
						}
					}
					values.add(val);
				}
//				for(MapZoneValue mzv : mapzonevalues){
//					ValueMultiSerie val = new ValueMultiSerie();
//					//val.setCategory(mzv.getValues().get(0).getAxis().get(levelIndex).getLabel().replace("'", ""));
//					val.setSerieName(mzv.getValues().get(0).getAxis().get(levelIndex).getLabel().replace("'", ""));
//					for(MetricValue mv : mzv.getValues()){
//						ValueSimpleSerie sval = new ValueSimpleSerie();
//						sval.setCategory(mv.getAxis().get(levelIndex).getLabel().replace("'", ""));
//						sval.setValue(mv.getValue());
//						val.addValue(sval);
//						
//					}
//					
//					values.add(val);
//				}
				
				object.setValues(values);
				
				FusionChartsPanel panel = new FusionChartsPanel(object, ChartObject.TYPE_MULTI, graphType);
				graphPanel.add(panel);
				
				panel.renderCharts();
				
				graphType = panel.getDefaultRenderer();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Window.alert(e.getMessage());
			viewer.showWaitPart(false);
		}
		viewer.showWaitPart(false);
	}
	
	@UiHandler("lstData")
	public void onDataChange(ChangeEvent event) {
		selectedMetric = metrics.get(lstData.getSelectedIndex());
		loadGraph();
	}
}
