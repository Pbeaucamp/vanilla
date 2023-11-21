package bpm.freematrix.reborn.web.client.main.home.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.utils.ActionResult;
import bpm.fm.api.model.utils.MetricValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class GanttChart extends Composite {

	private static GanttChartUiBinder uiBinder = GWT.create(GanttChartUiBinder.class);

	interface GanttChartUiBinder extends UiBinder<Widget, GanttChart> {
	}

	private MetricValue value;
	
//	@UiField
//	DateBox startDate, endDate;

	public GanttChart(MetricValue value) {
		initWidget(uiBinder.createAndBindUi(this));
		this.value = value;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		String xml = generateXmlChart();
		System.out.println(xml);
		renderChart("Gantt", xml, "fusionChartsContainer", "100%", "100%");
	}
	
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private String generateXmlChart() {


		StringBuilder buf = new StringBuilder();
		buf.append("<chart");
		
//		buf.append(" caption='");
//		buf.append(value.getMetric().getName().replace("'", "\\'"));
//		buf.append("'");
		
		buf.append(" dateformat='dd/mm/yyyy' "
				+ " showtasknames='1' tooltextbgcolor='F1F1F1' tooltextbordercolor='333333' "
				+ "palettethemecolor='333333' showborder='1'");
		
		String xmlData = generateXmlData();
		
		buf.append(">\n");
		
		buf.append(xmlData);
		
		buf.append("</chart>");
		
		String xml = buf.toString();
		
//		System.out.println(xml);
		
		return xml;
	}

	private Date minDate;
	private Date maxDate;
	
	private String periodicity;
	
	private String generateXmlData() {
		StringBuilder bufCat = new StringBuilder();
		StringBuilder bufProc = new StringBuilder();
		StringBuilder bufTask = new StringBuilder();
		
		//generate the gant data
		HashMap<MetricAction, ActionResult> actions = new LinkedHashMap<MetricAction, ActionResult>();
		
		//get all the actions
		fillActionMap(value, actions);
		
		//order the actions and find the min/max dates		
		List<Entry<MetricAction, ActionResult>> sorting = new ArrayList<Entry<MetricAction, ActionResult>>(actions.entrySet());
		
		Collections.sort(sorting, new Comparator<Entry<MetricAction, ActionResult>>() {
			@Override
			public int compare(Entry<MetricAction, ActionResult> o1, Entry<MetricAction, ActionResult> o2) {					
				
				if(o1.getKey().getStartDate().before(o2.getKey().getStartDate())) {
					return -1;
				}
				else if(o1.getKey().getStartDate().after(o2.getKey().getStartDate())) {
					return 1;
				}
				return 0;
			}
		});
		
		for(Entry<MetricAction, ActionResult> o1 : sorting) {
			//look for min/max dates
			if(minDate == null) {
				minDate = o1.getKey().getStartDate();
			}
			if(maxDate == null) {
				maxDate = o1.getKey().getEndDate();
			}
			
			if(o1.getKey().getStartDate().before(minDate)) {
				minDate = o1.getKey().getStartDate();
			}
			if(o1.getKey().getEndDate().after(maxDate)) {
				maxDate = o1.getKey().getEndDate();
			}	
		}
		
		minDate.setDate(1);
		maxDate.setDate(28);
		
		bufCat.append("<categories bgcolor='33bdda' basefont='Arial' basefontcolor='FFFFFF' basefontsize='12'>\n");
		bufCat.append("	<category start='" + dateFormat.format(minDate) + "' end='" + dateFormat.format(maxDate) + "' align='center' name='" + value.getMetric().getName() + "' fontcolor='ffffff' isbold='1' fontsize='16' />\n");
		bufCat.append("</categories>");
		
		bufCat.append("<categories bgcolor='33bdda' font='Arial' fontcolor='1288dd' fontsize='12'>\n");
		bufProc.append("<processes headertext='Actions' fontcolor='#000000' fontsize='11' isanimated='1' bgcolor='#6baa01' headervalign='bottom' headeralign='left' headerbgcolor='#6baa01' headerfontcolor='#ffffff' headerfontsize='16' align='left' isbold='1' bgalpha='25'>\n");
		bufTask.append("<tasks color='' alpha='' font='' fontcolor='' fontsize='' isanimated='1'>\n");
		
		DateTimeFormat monthNameFormat = DateTimeFormat.getFormat("MMMM");
		
		Date tempDate = new Date(minDate.getTime());
		
		while(true) {
			int i = tempDate.getMonth();
			Date date = new Date();
			date.setYear(tempDate.getYear());
			date.setDate(1);
			date.setMonth(i);
			String month = monthNameFormat.format(date);
			
			String start = dateFormat.format(date);
			date.setDate(28);
			String end = dateFormat.format(date);
			
			bufCat.append("	<category start='" + start + "' end='" + end + "' name='" + month + "' bgcolor='33bdda' font='Arial' fontcolor='#ffffff' fontsize='12'/>\n");
			tempDate.setMonth(tempDate.getMonth() + 1);
			if((tempDate.getYear() == maxDate.getYear() && tempDate.getMonth() > maxDate.getMonth()) || tempDate.getYear() > maxDate.getYear()) {
				break;
			}
		}
		
//		for(int i = minDate.getMonth() ; i <= maxDate.getMonth() ; i++) {
//
//		}
		for(Entry<MetricAction, ActionResult> entry : sorting) {
			
			Date startDate = entry.getKey().getStartDate();
			Date endDate = entry.getKey().getEndDate();
			
//			startDate.setDate(15);
//			endDate.setDate(15);
			
			String start = dateFormat.format(startDate);
			String end = dateFormat.format(endDate);
			
			bufProc.append("	<process name='" + entry.getKey().getName().replace("'", "&quot;") + "{br} "+ start + " -> " + end + "' id='" + entry.getKey().getName().replace("'", "&quot;") + "' />\n");
			bufTask.append("	<task name='' start='" + start + "' end='" + end + "' id='" + entry.getKey().getName().replace("'", "&quot;") + "1" + "' color='" + getColorForHealth(entry.getValue().getHealth()) + "' bordercolor='AFD8F8' />\n");
			
		}
		bufCat.append("</categories>\n");
		bufProc.append("</processes>\n");
		bufTask.append("</tasks>\n");
		
		StringBuilder buf = new StringBuilder();
		
		buf.append(bufCat.toString());
		buf.append(bufProc.toString());
		buf.append(bufTask.toString());
		
		return buf.toString();
	}

	private String red = "FF5858";
	private String green = "53E653";
	private String orange = "FF7F00";
	
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
	
	private void fillActionMap(MetricValue value2, HashMap<MetricAction, ActionResult> actions) {
		
		if(periodicity == null && value2.getMetric().getFactTable() instanceof FactTable) {
			periodicity = ((FactTable)value2.getMetric().getFactTable()).getPeriodicity();
		}
		
		for(MetricAction action : value2.getActionResults().keySet()) {
			actions.put(action, value2.getActionResults().get(action));
		}
		
		for(MetricValue val : value2.getChildren()) {
			fillActionMap(val, actions);
		}
		
	}

	private final native void renderChart(String type, String xml, String div, String width, String height) /*-{
		$wnd.renderChart(type, xml, div, width, height);
	}-*/;
}
