package bpm.freematrix.reborn.web.client.main.home.charts;

import java.util.List;

import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.fm.api.model.utils.MetricValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StrategyChart extends Composite {

	private static StrategyChartUiBinder uiBinder = GWT
			.create(StrategyChartUiBinder.class);

	interface StrategyChartUiBinder extends UiBinder<Widget, StrategyChart> {
	}

	private MetricValue value;
	private List<Observatory> observatories;

	public StrategyChart(MetricValue value) {
		initWidget(uiBinder.createAndBindUi(this));
		this.value = value;
	}

	public StrategyChart(List<Observatory> observatories) {
		initWidget(uiBinder.createAndBindUi(this));
		this.observatories = observatories;
	}

	private String generateXmlForObservatories() {
		StringBuilder buf = new StringBuilder();
		buf.append("<chart");
		
		buf.append(" caption='");
		buf.append("Strategy Map");
		buf.append("'");
		
		buf.append(" showaxislines='1' bgColor='#FFFFFF' bgAlpha='100' showBorder='0' exportEnabled='1'");		
		
		int maxThemes = 0;
		for(Observatory obs : observatories) {
			if(maxThemes < obs.getThemes().size()) {
				maxThemes = obs.getThemes().size();
			}
		}
		
		buf.append(" xaxisminvalue='0' xaxismaxvalue='1800' yaxisminvalue='0' yaxismaxvalue='800' bubblescale='3' is3d='1' numdivlines='0' showformbtn='0'");
		
		buf.append(">\n");
		
		buf.append("<dataset>");
	
		
		//generate the data
		int i = 1;
		for(Observatory obs : observatories) {
			buf.append("<set imageNode='1' labelAlign='TOP' x='10' y='" + (700 / observatories.size()) * i +"' width='1700' height='" + (740 / observatories.size()) +"' color='BBBBBB' id='" + obs.getName() + "' />");//label='" + obs.getName() + "'  />");
			int j = 1;
			for(Theme th : obs.getThemes()) {
				buf.append("<set x='" + (1500/ obs.getThemes().size()) * j + "' y='" + (700 / (observatories.size() - 1)) * i +"' width='" + (1500/ obs.getThemes().size()) + "' height='" + (420 / observatories.size()) +"' name='" + th.getName() + "' color='#cdffff' id='" + th.getName() + "' />");
				j++;
			}
			i++;
		}
		
		buf.append("</dataset>");
		buf.append("<labels>");
		i = 1;
		for(Observatory obs : observatories) {
			buf.append("<label color='FFFFFF' fontsize='24' x='10' y='" + (700 / observatories.size()) * i +"' text='" + obs.getName() + "'/>");
			i++;
		}
		buf.append("</labels>");
		
		buf.append("</chart>");
		
		String xml = buf.toString();
		
//		System.out.println(xml);
		
		return xml;
	}

	private final native void renderChart(String type, String xml, String div, String width, String height) /*-{
		$wnd.renderChart(type, xml, div, width, height);
	}-*/;
	
	
	@Override
	protected void onLoad() {
		super.onLoad();
		String xml = null;
		if(value != null) {
			xml  = generateXmlChart();
		}
		else {
			xml = generateXmlForObservatories();
		}
		System.out.println(xml);
		renderChart("DragNode", xml, "fusionChartsContainer", "100%", "100%");
	}

	private String generateXmlChart() {
		StringBuilder buf = new StringBuilder();
		buf.append("<chart");
		
		buf.append(" caption='");
		buf.append(value.getMetric().getName().replace("'", "\\'"));
		buf.append("'");
		
		buf.append(" showaxislines='1' bgColor='#FFFFFF' bgAlpha='100' showBorder='0' exportEnabled='1'");		
		
		String xmlData = generateXmlData();
		
		buf.append(" xaxisminvalue='0' xaxismaxvalue='" + (maxElements * 20 + 20) + "' yaxisminvalue='0' yaxismaxvalue='" + (((maxDepth - 2) * 30) + 30) + "' bubblescale='3' is3d='1' numdivlines='0' showformbtn='0'");
		
		buf.append(">\n");
		
		buf.append(xmlData);
		
		buf.append("</chart>");
		
		String xml = buf.toString();
		
//		System.out.println(xml);
		
		return xml;
	}

	int maxDepth = 2;
	int maxElements = 1;
	
	private String generateXmlData() {
		StringBuilder bufElements = new StringBuilder();
		StringBuilder bufLinks = new StringBuilder();
		
		bufElements.append("<dataset>");
		
		bufLinks.append("<connectors color='000000' stdthickness='8'>");
		
		generateMetricElement(value, bufElements, bufLinks, 2, 1, 1);
		bufElements.append("</dataset>");
		bufLinks.append("</connectors>");
		bufElements.append(bufLinks.toString());
		return bufElements.toString();
	}
	
	private String red = "FF5858";
	private String green = "53E653";
	private String orange = "FF7F00";
	
	private void generateMetricElement(MetricValue val, StringBuilder bufElements, StringBuilder bufLinks, int depth, int nbElements, int index) {
		if(depth > maxDepth) {
			maxDepth = depth;
		}
		if(nbElements > maxElements) {
			maxElements = nbElements;
		}
		
		int i = 1;
		for(MetricValue sub : val.getChildren()) {
			generateMetricElement(sub, bufElements, bufLinks, depth + 1, val.getChildren().size() + val.getActionResults().size(), i);
			i++;
			
			//the links between children and actions
			bufLinks.append(createLink(val.getMetric().getName(), sub.getMetric().getName()));
		}
		
		//this set
		String color = getColorForHealth(val.getHealth());

		bufElements.append("<set x='" + ((((((maxElements / nbElements) + 1) / 2) * index) * 30) - 15) + "' y='" + ((maxDepth - depth) * 30) + "' width='150' height='40' name='" + val.getMetric().getName() + "' color='" + color  + "' id='" + val.getMetric().getName() + "' />");
		
		//the actions sets
		int subSize = val.getChildren().size() + val.getActionResults().size();
		i = val.getChildren().size() + 1;
		for(MetricAction action : val.getActionResults().keySet()) {
			color = getColorForHealth(val.getActionResults().get(action).getHealth());

			bufElements.append("<set x='" + ((((((maxElements / subSize) + 1) / 2) * i) * 30) - 15) + "' y='" + (((maxDepth - depth) - 1) * 30) + "' width='150' height='40' name='" + action.getName() + " - " + val.getActionResults().get(action).getStatus() + "' color='" + color  + "' id='" + action.getName() + "' />");
			
			i++;
			//the links between children and actions
			bufLinks.append(createLink(val.getMetric().getName(), action.getName()));
		}
		
		

	}


	private String createLink(String name, String name2) {
		return "<connector strength='0.35' from='" + name + "' to='" + name2 + "' color='FFBC79' arrowatstart='0' arrowatend='1' />";
	}

	private String getColorForHealth(int health) {
		if(health > 0) {
			return green;
		}
		else if(health == 0) {
			return orange;
		}
		else {
			return red;
		}
	}
}