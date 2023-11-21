package bpm.birt.fusioncharts.core.reportitem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.extension.IBaseResultSet;
import org.eclipse.birt.report.engine.extension.IQueryResultSet;
import org.eclipse.birt.report.engine.extension.ReportItemPresentationBase;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.extension.ExtendedElementException;

import bpm.birt.fusioncharts.core.SwfProvider;
import bpm.birt.fusioncharts.core.chart.AvailableChart;
import bpm.birt.fusioncharts.core.model.IChart;
import bpm.birt.fusioncharts.core.xmldata.FusionChartProperties;

/**
 * Author : Charles Martin Company : BPM-Conseil
 */
public class FusionchartsPresentationImpl extends ReportItemPresentationBase {

	private FusionchartsItem chartItem;

	private IChart selectedChart;
	private String chartName;
	private String chartTitle;
	private String chartId;
	private int useRoundEdges;
	private int chartType;
	private int width;
	private int height;
	private String parameters;

	private List<Serie> series;
	private String exprX;
	private String styles;

	// Is there aggregations on value
	private boolean group;

	public void setModelObject(ExtendedItemHandle modelHandle) {
		try {
			chartItem = (FusionchartsItem) modelHandle.getReportItem();
		} catch (ExtendedElementException e) {
			e.printStackTrace();
		}
	}

	public int getOutputType() {
		return OUTPUT_AS_HTML_TEXT;
	}

	@Override
	public Object onRowSets(IBaseResultSet[] results) throws BirtException {
		if (chartItem == null) {
			throw new NullPointerException();
		}

		String chartDataXml = chartItem.getXml();
		String vanillaRuntimeUrl = chartItem.getVanillaRuntimeUrl();

		if (!vanillaRuntimeUrl.endsWith("/")) {
			vanillaRuntimeUrl = vanillaRuntimeUrl + "/";
		}

		// We prepare the xml parameters for the map
		decodeXml(chartDataXml);
		setSelectedChart(chartType);

		Properties props = chartItem.getCustomProperties();
		String propertiesXml = FusionChartProperties.getPropertiesXmlForMap(props);
		styles = FusionChartProperties.getStylesXmlForChart(props);
		parameters = parameters + " " + propertiesXml;

		String fusionChartsJsPath = SwfProvider.getInstance().getFusionChartsJS();
		String jqueryJsPath = SwfProvider.getInstance().getJQueryJS();
		String fusionChartHCPath = SwfProvider.getInstance().getFusionChartsHCJS();
		String fusionChartHCChartPath = SwfProvider.getInstance().getFusionChartsHCChartJS();
		String chartPath = SwfProvider.getInstance().getSwfUrl(chartName);

		if (fusionChartsJsPath == null || chartPath == null) {
			jqueryJsPath = vanillaRuntimeUrl + "Charts/jquery.min.js";
			fusionChartsJsPath = vanillaRuntimeUrl + "Charts/FusionCharts.js";
			fusionChartHCPath = vanillaRuntimeUrl + "Charts/FusionCharts.HC.js";
			fusionChartHCChartPath = vanillaRuntimeUrl + "Charts/FusionCharts.HC.Charts.js";
			chartPath = vanillaRuntimeUrl + "Charts/" + chartName;
		}

		String chartXml = null;
		if (selectedChart.getType() == IChart.DRAG_NODE) {
			chartXml = buildDragNodeXml(results);

		}
		else {
			chartXml = buildMapXml(results);
		}
		if (chartId == null || chartId.equals("null")) {
			chartId = "DragNode" + new Object().hashCode();
		}

		StringBuffer html = new StringBuffer();
		html.append("<html>\n");
		html.append("    <head>\n");
		html.append("        <title>Test Map</title>\n");
		html.append("        <meta content=\"text/html;charset=UTF-8\" http-equiv=\"Content-Type\">\n");
		html.append("        <script language=\"JavaScript\" src=\"" + jqueryJsPath + "\"></script>\n");
		html.append("        <script language=\"JavaScript\" src=\"" + fusionChartsJsPath + "\"></script>\n");
		html.append("        <script language=\"JavaScript\" src=\"" + fusionChartHCPath + "\"></script>\n");
		html.append("        <script language=\"JavaScript\" src=\"" + fusionChartHCChartPath + "\"></script>\n");

		// XXX
		html.append("		<script language=\"JavaScript\" src=\"" + vanillaRuntimeUrl + "Charts/FusionCharts.HC.PowerCharts.js" + "\"></script>\n");

		html.append("        <script type='text/javascript'>FusionCharts.setCurrentRenderer('javascript');</script>\n");
		html.append("    </head>\n");
		html.append("    <body>\n");
		html.append("    	<div id=\"" + chartId + "\">\n");
		html.append("        	FusionCharts.\n");
		html.append("        </div>\n");
		html.append("        <script type=\"text/javascript\">\n");
		html.append("        	 var chart = new FusionCharts(\"" + chartPath + "\", \"myChartId" + new Object().hashCode() + "\", \"" + width + "\", \"" + height + "\", \"0\", \"0\");\n");
		html.append("            chart.setTransparent(false);\n");
		html.append("            chart.setXMLData(\"" + chartXml + "\");\n");
		html.append("            chart.render(\"" + chartId + "\");\n");
		html.append("        </script>\n");
		html.append("     </body>\n");
		html.append("</html>");

		return html.toString();
	}

	private class DragNodeValue {
		private String id;
		private String parent;
		private String value;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public DragNodeValue(String id, String parent, String value) {
			super();
			this.id = id;
			this.parent = parent;
			this.value = value;
		}

	}

	private String buildDragNodeXml(IBaseResultSet[] results) throws BirtException {

		Serie id = series.get(0);
		Serie parent = series.get(1);

		List<DragNodeValue> values = new ArrayList<DragNodeValue>();
		
		while (((IQueryResultSet) results[0]).next()) {

			String idVal = results[0].evaluate(id.getExpr()).toString();
			String parentVal = results[0].evaluate(parent.getExpr()).toString();
			String value = results[0].evaluate(exprX).toString();
			
			values.add(new DragNodeValue(idVal, parentVal, value));
			
		}
		
		//order the list		
		Collections.sort(values, new Comparator<DragNodeValue>() {
			@Override
			public int compare(DragNodeValue o1, DragNodeValue o2) {
				
				if(o1.getParent() == null) {
					return -1;
				}
				if(o2.getParent() == null) {
					return 1;
				}
				
				if(o1.getId().equals(o2.getParent())) {
					return -1;
				}
				if(o2.getId().equals(o1.getParent())) {
					return 1;
				}
				
				return 0;
			}
		});
		
		//create the chart
		StringBuilder buf = new StringBuilder();
		buf.append("<chart");
		
		buf.append(" caption='");
		buf.append("");
		buf.append("'");
		
		buf.append(" showaxislines='1' bgColor='#FFFFFF' bgAlpha='100' showBorder='0' exportEnabled='1'");	
		
		buf.append(" xaxisminvalue='0' xaxismaxvalue='700' yaxisminvalue='0' yaxismaxvalue='130' bubblescale='3' is3d='1' numdivlines='0' showformbtn='0'");
		
		buf.append(">");
		
		buf.append("<dataset>");
		
		StringBuilder bufLinks = new StringBuilder();
		bufLinks.append("<connectors color='000000' stdthickness='8'>");
	
		HashMap<String, Integer> itemLevels = new HashMap<String, Integer>();
		HashMap<Integer, Integer> itemNumbers = new HashMap<Integer, Integer>();
		
		for(DragNodeValue val : values) {
			Integer itemLevel = 0;
			if(val.getParent() != null && !val.getParent().isEmpty()) {
				itemLevel = itemLevels.get(val.getParent()) + 1;	
				bufLinks.append("<connector strength='0.35' from='" + val.getId() + "' to='" + val.getParent() + "' color='FFBC79' arrowatstart='0' arrowatend='0' />");
			}
			if(itemNumbers.get(itemLevel) == null) {
				itemNumbers.put(itemLevel, 0);
			}
			else {
				itemNumbers.put(itemLevel, itemNumbers.get(itemLevel) + 1);
			}
			Integer number = itemNumbers.get(itemLevel);
			itemLevels.put(val.getId(), itemLevel);
			
			buf.append("<set x='" + ((250 * number) + 100) + "' y='" + (110 - (20 * itemLevel)) +  "' width='180' height='40' name='" + val.getValue() + "' id='" + val.getId() + "'/>");
		}
		
		buf.append("</dataset>");
		bufLinks.append("</connectors>");
		buf.append(bufLinks.toString());
		buf.append("</chart>");

		return buf.toString();

	}

	private String buildMapXml(IBaseResultSet[] results) throws BirtException {

		List<Object> expressionValueX = new ArrayList<Object>();

		while (((IQueryResultSet) results[0]).next()) {
			for (Serie serie : series) {
				serie.addResult(results[0].evaluate(serie.getExpr()));
			}
			expressionValueX.add(results[0].evaluate(exprX));
		}

		String mapXml = "";
		mapXml += "<chart caption='" + chartTitle + "' " + "useRoundEdges='" + useRoundEdges + "'" + parameters + ">";

		if (styles != null && !styles.isEmpty()) {
			mapXml += styles;
		}

		if (group) {
			for (Serie serie : series) {
				List<String> keyMap = new ArrayList<String>();
				HashMap<String, String> groupValue = new HashMap<String, String>();
				boolean first = true;
				boolean isDouble = true;
				if (serie.getAgg().equalsIgnoreCase("Sum")) {
					for (int i = 0; i < serie.getResults().size(); i++) {
						if (keyMap.contains(expressionValueX.get(i).toString())) {
							if (first) {
								try {
									Double.parseDouble(groupValue.get(expressionValueX.get(i).toString()));
								} catch (Exception e) {
									isDouble = false;
									first = false;
								}
								first = false;
							}
							if (isDouble) {
								Double value = Double.parseDouble(groupValue.get(expressionValueX.get(i).toString()));
								value += Double.parseDouble(serie.getResults().get(i).toString());
								groupValue.put(expressionValueX.get(i).toString(), String.valueOf(value));
							}
							else {
								int value = Integer.parseInt(groupValue.get(expressionValueX.get(i).toString()));
								value += Integer.parseInt(serie.getResults().get(i).toString());
								groupValue.put(expressionValueX.get(i).toString(), String.valueOf(value));
							}
						}
						else {
							keyMap.add(expressionValueX.get(i).toString());
							groupValue.put(expressionValueX.get(i).toString(), serie.getResults().get(i).toString());
						}
					}
				}
				else if (serie.getAgg().equalsIgnoreCase("Count")) {
					for (int i = 0; i < serie.getResults().size(); i++) {
						if (keyMap.contains(expressionValueX.get(i).toString())) {
							int count = Integer.parseInt(groupValue.get(expressionValueX.get(i).toString()));
							count++;
							groupValue.put(expressionValueX.get(i).toString(), String.valueOf(count));
						}
						else {
							keyMap.add(expressionValueX.get(i).toString());
							groupValue.put(expressionValueX.get(i).toString(), String.valueOf(1));
						}
					}
				}
				serie.setKeyMap(keyMap);
				serie.setGroupValue(groupValue);
			}

			List<String> keyMap = series.get(0).getKeyMap();
			if (!selectedChart.isMultiSeries()) {
				HashMap<String, String> groupValue = series.get(0).getGroupValue();
				for (int i = 0; i < keyMap.size(); i++) {
					mapXml += "    <set label='" + keyMap.get(i) + "' value='" + groupValue.get(keyMap.get(i)) + "' />";
				}
			}
			else {
				mapXml += "		<categories>";
				for (int i = 0; i < keyMap.size(); i++) {
					mapXml += "		  <category  label='" + keyMap.get(i) + "' />";
				}
				mapXml += "		</categories>";
				for (Serie serie : series) {
					HashMap<String, String> groupValue = serie.getGroupValue();
					mapXml += "     <dataset seriesName='" + serie.getName() + "'>";
					for (int i = 0; i < keyMap.size(); i++) {
						mapXml += "		  <set value='" + groupValue.get(keyMap.get(i)) + "' />";
					}
					mapXml += "		</dataset>";
				}
			}
		}
		else {
			if (!selectedChart.isMultiSeries()) {
				for (Serie serie : series) {
					for (int i = 0; i < serie.getResults().size(); i++) {
						mapXml += "    <set label='" + expressionValueX.get(i).toString() + "' value='" + serie.getResults().get(i).toString() + "' />";
					}
				}
			}
			else {
				mapXml += "		<categories>";
				for (int i = 0; i < expressionValueX.size(); i++) {
					mapXml += "		  <category  label='" + expressionValueX.get(i).toString() + "' />";
				}
				mapXml += "		</categories>";
				for (Serie serie : series) {
					mapXml += "     <dataset seriesName='" + serie.getName() + "' color='" + serie.getColor() + "'>";
					for (int i = 0; i < serie.getResults().size(); i++) {
						mapXml += "		  <set value='" + serie.getResults().get(i).toString() + "' />";
					}
					mapXml += "		</dataset>";
				}
			}
		}

		mapXml += "</chart>";

		return mapXml;
	}

	private void setSelectedChart(int type) {
		switch (type) {
		case IChart.COLUMN:
			selectedChart = findChart(AvailableChart.AVAILABLE_CHARTS_COLUMN);
			break;
		case IChart.BAR:
			selectedChart = findChart(AvailableChart.AVAILABLE_CHARTS_BAR);
			break;
		case IChart.LINE:
			selectedChart = findChart(AvailableChart.AVAILABLE_CHARTS_LINE);
			break;
		case IChart.PIE:
			selectedChart = findChart(AvailableChart.AVAILABLE_CHARTS_PIE);
			break;
		case IChart.DOUGHNUT:
			selectedChart = findChart(AvailableChart.AVAILABLE_CHARTS_DOUGHNUT);
			break;
		case IChart.RADAR:
			selectedChart = findChart(AvailableChart.AVAILABLE_CHARTS_RADAR);
			break;
		case IChart.PARETO:
			selectedChart = findChart(AvailableChart.AVAILABLE_CHARTS_PARETO);
			break;
		case IChart.DRAG_NODE:
			selectedChart = findChart(AvailableChart.AVAILABLE_DRAG_NODE);
			break;
		default:
			break;
		}
	}

	private IChart findChart(IChart[] charts) {
		for (IChart chart : charts) {
			if (chart.getChartName().equals(chartName)) {
				return chart;
			}
		}
		return null;
	}

	private void decodeXml(String xml) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(xml);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		for (Element e : (List<Element>) document.getRootElement().elements("chart")) {
			try {
				if (e.element("chartname") != null) {

					Element d = e.element("chartname");
					if (d != null) {
						this.chartName = d.getStringValue();
					}
				}
				if (e.element("title") != null) {

					Element d = e.element("title");
					if (d != null) {
						this.chartTitle = d.getStringValue();
					}
				}
				if (e.element("id") != null) {

					Element d = e.element("id");
					if (d != null) {
						this.chartId = d.getStringValue();
					}
				}
				if (e.element("type") != null) {

					Element d = e.element("type");
					if (d != null) {
						this.chartType = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("glassstyle") != null) {

					Element d = e.element("glassstyle");
					if (d != null) {
						this.useRoundEdges = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("width") != null) {

					Element d = e.element("width");
					if (d != null) {
						this.width = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("height") != null) {

					Element d = e.element("height");
					if (d != null) {
						this.height = Integer.parseInt(d.getStringValue());
					}
				}
				if (e.element("parameters") != null) {

					Element d = e.element("parameters");
					if (d != null) {
						this.parameters = d.getStringValue();
					}
				}
				if (e.element("series") != null) {
					series = new ArrayList<Serie>();
					for (Element g : (List<Element>) e.elements("series")) {
						if (g.element("serie") != null) {
							for (Element h : (List<Element>) g.elements("serie")) {
								Serie tempSerie = new Serie();
								if (h.element("name") != null) {

									Element d = h.element("name");
									if (d != null) {
										tempSerie.setName(d.getStringValue());
									}
								}
								if (h.element("color") != null) {

									Element d = h.element("color");
									if (d != null) {
										tempSerie.setColor(d.getStringValue());
									}
								}
								if (h.element("expy") != null) {

									Element d = h.element("expy");
									if (d != null) {
										tempSerie.setExpr(d.getStringValue());
									}
								}
								if (h.element("agg") != null) {

									Element d = h.element("agg");
									if (d != null) {
										tempSerie.setAgg(d.getStringValue());
									}
								}
								series.add(tempSerie);
							}
						}
					}
				}
				if (e.element("expx") != null) {

					Element d = e.element("expx");
					if (d != null) {
						this.exprX = d.getStringValue();
					}
				}
				if (e.element("group") != null) {

					Element d = e.element("group");
					if (d != null) {
						this.group = Boolean.parseBoolean(d.getStringValue());
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

		}
	}

	private class Serie {
		private String name;
		private String color;
		private String expr;
		private String agg;
		private List<Object> results = new ArrayList<Object>();
		private List<String> keyMap = new ArrayList<String>();
		private HashMap<String, String> groupValue = new HashMap<String, String>();

		public Serie() {
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}

		public void setExpr(String expr) {
			this.expr = expr;
		}

		public String getExpr() {
			return expr;
		}

		public void setAgg(String agg) {
			this.agg = agg;
		}

		public String getAgg() {
			return agg;
		}

		public List<Object> getResults() {
			return results;
		}

		public void addResult(Object result) {
			results.add(result);
		}

		public void setKeyMap(List<String> keyMap) {
			this.keyMap = keyMap;
		}

		public List<String> getKeyMap() {
			return keyMap;
		}

		public void setGroupValue(HashMap<String, String> groupValue) {
			this.groupValue = groupValue;
		}

		public HashMap<String, String> getGroupValue() {
			return groupValue;
		}
	}
}
