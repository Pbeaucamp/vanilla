package bpm.gwt.commons.client.charts;

import bpm.vanilla.platform.core.beans.chart.ChartType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ChartRenderPanel extends Composite {

	private static final String DIV_CONTAINER = "fusionChartsContainer";//"ChartContainer_";

	private static ChartRenderPanelUiBinder uiBinder = GWT.create(ChartRenderPanelUiBinder.class);

	interface ChartRenderPanelUiBinder extends UiBinder<Widget, ChartRenderPanel> {
	}

	@UiField
	HTMLPanel contentPanel;

	String[] colors = { "green", "yellow", "blue", "red", "pink", "cyan", "orange", "lime", "indigo", "teal", "violet" };
	
	private ChartObject item;
	private ChartType type;
	private String containerId;

	/**
	 * Initialize the chart panel
	 * 
	 * @param chartObject
	 *            the chart definition
	 * @param chartType
	 *            the chart type (constants on ChartObject)
	 * @param defaultRenderer
	 *            can be null, the default chart renderer (constants on
	 *            ChartObject)
	 */
	public ChartRenderPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		this.containerId = DIV_CONTAINER + new Object().hashCode();
		contentPanel.getElement().setId(containerId.replace("fusionChartsContainer", "contentPanel"));
		contentPanel.getElement().getElementsByTagName("canvas").getItem(0).setId(containerId);
	}
	
	public void refresh() {
		loadChart(item, type);
	}

	public void loadChart(ChartObject item, ChartType type) {
		this.item = item;
		this.type = type;
		
		String json = "";
		if (type.getSubType().equals(ChartObject.TYPE_SCATTER_BUBBLE)) {
			json = buildScatterChartXML(item);
		}
		else {
//			if (type.getValue().equals(ChartObject.RENDERER_RADAR)) {
//				json = buildRadarChartXML(item, type);
//			}
//			else {
				try {
					json = generateJson(type.getValue());
				} catch(Exception e) {
					e.printStackTrace();
				}
						//buildChartXML(item, type);
//			}
		}
//		System.out.println(xml);
		renderChart(json, containerId, "100%", "100%");
	}

	private final native void renderChart(String json, String div, String width, String height) /*-{
		$wnd.renderChartJs(json, div, width, height);
	}-*/;
	
	protected String generateJson(String chartType) throws Exception {
		JSONObject root = new JSONObject();
		root.put("type", getTypeChartjs(chartType));
		root.put("data", getData(chartType));
		root.put("options", getOptions());
		String json = root.toString();
		System.out.println(json);
		return json;
	}

	private JSONString getTypeChartjs(String chartType) {
		if (chartType != null) {
			if (chartType.equals(ChartObject.RENDERER_AREA)
					|| chartType.equals(ChartObject.RENDERER_LINE)
					|| chartType.equals(ChartObject.RENDERER_MULTI_AREA)
					|| chartType.equals(ChartObject.RENDERER_MULTI_LINE)
					|| chartType.equals(ChartObject.RENDERER_SPLINE)) {
				return new JSONString("line");
			}
			else if (chartType.equals(ChartObject.RENDERER_BAR)
					|| chartType.equals(ChartObject.RENDERER_MULTI_BAR)) {
				return new JSONString("horizontalBar");
			}
			else if (chartType.equals(ChartObject.RENDERER_COLUMN)
					|| chartType.equals(ChartObject.RENDERER_MULTI_COLUMN)) {
				return new JSONString("bar");
			}
			else if (chartType.equals(ChartObject.RENDERER_PIE)) {
				return new JSONString("pie");
			}
			else if (chartType.equals(ChartObject.RENDERER_DONUT)) {
				return new JSONString("doughnut");
			}
			else if (chartType.equals(ChartObject.RENDERER_FUNNEL)) {
				return new JSONString("bar");
			}
			else if (chartType.equals(ChartObject.RENDERER_RADAR)) {
				return new JSONString("radar");
			}
		}
		return new JSONString("horizontalBar");
	}

	private JSONObject getOptions() {
		JSONObject options = new JSONObject();
		options.put("responsive", JSONBoolean.getInstance(true));
		options.put("maintainAspectRatio", JSONBoolean.getInstance(false));
		
		if(item.getTitle() != null && !item.getTitle().isEmpty()) {
			JSONObject titleObj = new JSONObject();
			titleObj.put("display", JSONBoolean.getInstance(true));
			titleObj.put("text", new JSONString(item.getTitle()));
			options.put("title", titleObj);
		}
		if(!type.getValue().equals(ChartObject.RENDERER_PIE) && !type.getValue().equals(ChartObject.RENDERER_DONUT)) {
			JSONObject obj = new JSONObject();
			obj.put("display", JSONBoolean.getInstance(false));
			options.put("legend", obj);
		}
		
		return options;
	}

	private JSONObject getData(String chartType) throws Exception {
		JSONObject data = new JSONObject();
		JSONArray labels = new JSONArray();

		int i = 0;
		for(ChartValue value : item.getValues()) {
			if(item.isMultiSeries()) {
//				labels.set(i, new JSONString(((ValueMultiSerie) value).getSerieName() != null ? ((ValueMultiSerie) value).getSerieName() : ""));
			}
			else {
				labels.set(i, new JSONString(value.getCategory() != null ? value.getCategory() : ""));
			}
			i++;
		}

		JSONArray datasets = new JSONArray();
		if(item.isMultiSeries()) {
			int j = 0;
			for(ChartValue multiVal : item.getValues()) {
				JSONObject dataset = new JSONObject();
				JSONArray dataArray = new JSONArray();
				JSONArray colorArray = new JSONArray();
				i = 0;
				for(ChartValue value : ((ValueMultiSerie) multiVal).getSerieValues()) {
					dataArray.set(i, new JSONNumber(((ValueSimpleSerie) value).getValue()));
					colorArray.set(i, new JSONString(j < 11 ? colors[j] : colors[j%11]));
					i++;

				}
				dataset.put("label", new JSONString(((ValueMultiSerie) multiVal).getSerieName()));
				dataset.put("data", dataArray);
				dataset.put("backgroundColor", colorArray);
				dataset.put("fill", JSONBoolean.getInstance(false));
				datasets.set(j, dataset);
				j++;
			}
		}
		else {
			JSONObject dataset = new JSONObject();
			JSONArray dataArray = new JSONArray();
			JSONArray colorArray = new JSONArray();
			i = 0;
			for(ChartValue value : item.getValues()) {
				if (((ValueSimpleSerie) value).getValue() != null) {
					dataArray.set(i, ((ValueSimpleSerie) value).getValue() != null ? new JSONNumber(((ValueSimpleSerie) value).getValue()) : null);
					colorArray.set(i, new JSONString(i < 11 ? colors[i] : colors[i%11]));
					i++;
				}
			}
			dataset.put("data", dataArray);
			dataset.put("backgroundColor", colorArray);
			dataset.put("fill", JSONBoolean.getInstance(false));
			datasets.set(0, dataset);
		}
		data.put("labels", labels);

		data.put("datasets", datasets);

		return data;
	}

	private String buildChartXML(ChartObject item, ChartType type) {

		//XXX chartjs
		
		
		
//		return generateJson(type.getValue());
		
		StringBuilder buf = new StringBuilder();
		buf.append("<chart");

		buf.append(" caption='");
		buf.append(item.getTitle().replace("'", "\\'"));
		buf.append("'");

		buf.append(" xaxisname='");
		buf.append(item.getxAxisName().replace("'", "\\'"));
		buf.append("'");

		buf.append(" yaxisname='");
		buf.append(item.getyAxisName().replace("'", "\\'"));
		buf.append("'");

		if (item.getColors() != null) {
			buf.append(" palettecolors='");
			for (String color : item.getColors()) {
				buf.append(color + ",");
			}
			buf.deleteCharAt(buf.lastIndexOf(","));
			buf.append("'");
		}

		buf.append(" showaxislines='1' bgColor='#FFFFFF' bgAlpha='100' showBorder='0' exportEnabled='1'");

		buf.append(">\n");
		buf.append(buildChartDataXML(item, type));

		buf.append("</chart>");

		return buf.toString();
	}

	private String buildChartDataXML(ChartObject item, ChartType type) {
		StringBuilder buf = new StringBuilder();

		if (type.getSubType().equals(ChartObject.TYPE_MULTI)) {

			StringBuilder catBuf = new StringBuilder();
			StringBuilder valueBuf = new StringBuilder();

			catBuf.append("	<categories>\n");
			boolean first = true;

			for (ChartValue val : item.getValues()) {
				ValueMultiSerie serie = (ValueMultiSerie) val;
				if (first) {
					first = false;
					for (ChartValue v : serie.getSerieValues()) {
						ValueSimpleSerie value = (ValueSimpleSerie) v;
						catBuf.append("		<category label='" + value.getCategory().replace("'", "") + "'/>");
					}
				}

				valueBuf.append("	<dataset seriesname='" + serie.getSerieName().replace("'", "") + "'>\n");
				for (ChartValue v : serie.getSerieValues()) {
					ValueSimpleSerie value = (ValueSimpleSerie) v;
					if (value.getColor() != null)
						valueBuf.append("		<set value='" + ((value.getValue() == null) ? "" : value.getValue()) + "' color='" + value.getColor() + "'/>");
					else
						valueBuf.append("		<set value='" + ((value.getValue() == null) ? "" : value.getValue()) + "'/>");
				}
				valueBuf.append("	</dataset>\n");
			}

			catBuf.append("	</categories>\n");
			buf.append(catBuf.toString());
			buf.append(valueBuf.toString());
		}
		else if (type.getValue().equals(ChartObject.RENDERER_RADAR)) {
			StringBuilder catBuf = new StringBuilder();
			StringBuilder valueBuf = new StringBuilder();

			catBuf.append("    <categories>\n");
			valueBuf.append("    <dataset seriesname='Test'>\n");
			for (ChartValue val : item.getValues()) {
				ValueSimpleSerie value = (ValueSimpleSerie) val;

				catBuf.append("	    <category ");
				catBuf.append("label='");
				catBuf.append(value.getCategory().replace("'", ""));
				catBuf.append("'/>\n");

				valueBuf.append("	    <set value='");
				valueBuf.append(((value.getValue() == null) ? "" : value.getValue()));
				valueBuf.append("'/>\n");
			}

			catBuf.append("    </categories>\n");
			valueBuf.append("    </dataset>\n");
			buf.append(catBuf.toString());
			buf.append(valueBuf.toString());
		}
		else {
			for (ChartValue val : item.getValues()) {
				ValueSimpleSerie value = (ValueSimpleSerie) val;

				buf.append("	<set ");
				buf.append("label='");
				buf.append(value.getCategory().replace("'", ""));
				buf.append("' ");

				buf.append("value='");
				buf.append(((value.getValue() == null) ? "" : value.getValue()));
				buf.append("'/>\n");

			}
		}

		return buf.toString();
	}

	private String buildScatterChartXML(ChartObject item) {
		StringBuilder buf = new StringBuilder();
		buf.append("<chart");

		buf.append(" caption='");
		buf.append(item.getTitle().replace("'", "\\'"));
		buf.append("'");

		buf.append(" xaxisname='");
		buf.append(item.getxAxisName().replace("'", "\\'"));
		buf.append("'");

		buf.append(" yaxisname='");
		buf.append(item.getyAxisName().replace("'", "\\'"));
		buf.append("'");

		if (item.getColors() != null) {
			buf.append(" palettecolors='");
			for (String color : item.getColors()) {
				buf.append(color + ",");
			}
			buf.deleteCharAt(buf.lastIndexOf(","));
			buf.append("'");
		}

		buf.append(" showaxislines='1' showverticalline='1' bgColor='#FFFFFF' bgAlpha='100' showBorder='0' exportEnabled='1'");

		buf.append(">\n");

		StringBuilder catBuf = new StringBuilder();
		StringBuilder valueBuf = new StringBuilder();
		for (ChartValue val : item.getValues()) {
			ValueMultiSerie serie = (ValueMultiSerie) val;

			valueBuf.append("	<dataset seriesname='" + serie.getSerieName().replace("'", "") + "'>\n");
			for (ChartValue value : serie.getSerieValues()) {
				ValuePointSerie m = (ValuePointSerie) value;

				if (value.getColor() != null)
					valueBuf.append("		<set x='" + m.getX() + "' y='" + m.getY() + "' color='" + value.getColor() + "'/>");
				else
					valueBuf.append("		<set x='" + m.getX() + "' y='" + m.getY() + "'/>");
			}
			valueBuf.append("	</dataset>\n");
		}

		buf.append(catBuf.toString());
		buf.append(valueBuf.toString());

		buf.append("</chart>");

		return buf.toString();
	}

	private String buildRadarChartXML(ChartObject item, ChartType type) {
		StringBuilder buf = new StringBuilder();
		buf.append("<chart");

		buf.append(" caption='");
		buf.append(item.getTitle().replace("'", "\\'"));
		buf.append("'");

		buf.append(" showaxislines='1' radarfillcolor='#ffffff' bgAlpha='100' showBorder='0' exportEnabled='1'");
		buf.append(">\n");

		buf.append(buildChartDataXML(item, type));
		buf.append("</chart>");

		return buf.toString();
	}

	public String getImageData() {
		return getImageData(containerId);
	}
	
	public ChartObject getItem() {
		return item;
	}

	private final native String getImageData(String div) /*-{
		return $wnd.getImageData(div);
	}-*/;
}
