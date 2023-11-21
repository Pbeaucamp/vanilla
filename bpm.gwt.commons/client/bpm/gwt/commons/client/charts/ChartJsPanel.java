package bpm.gwt.commons.client.charts;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ChartJsPanel extends Composite {

	private static final String DIV_CONTAINER = "fusionChartsContainer";

//	interface MyStyle extends CssResource {
//		String btnImgSelected();
//	}
//
//	@UiField
//	MyStyle style;

	@UiField
	Image imgPie, imgDoughnut, imgBar, imgColumn, imgLine, imgArea, imgSpline, imgpareto;

	@UiField
	Image imgMultiBars, imgMultiColumns, imgMultiLines, imgMultiArea;

	@UiField
	Image imgStackedColumns, imgStackedArea, imgSteppedLine;

	@UiField
	Image imgAngGauge, imgLinGauge;

	@UiField
	HTMLPanel mainPanel, headerPanelSimple, headerPanelMulti, headerPanelGauge, headerPanel, fusionChartsContainer;

	private ChartObject chartObject;
	private String chartType;
	private String defaultRenderer;

	private String container;

	private List<Image> images = new ArrayList<Image>();

	String[] colors = { "green", "yellow", "blue", "red", "pink", "cyan", "orange", "lime", "grape", "indigo", "teal", "violet" };

	private static ChartJsPanelUiBinder uiBinder = GWT.create(ChartJsPanelUiBinder.class);

	interface ChartJsPanelUiBinder extends UiBinder<Widget, ChartJsPanel> {}

	public ChartJsPanel(ChartObject chartObject, String chartType, String defaultRenderer) {
		initWidget(uiBinder.createAndBindUi(this));

		container = "fusionChartsContainer";

		// fusionChartsContainer.getElement().setId(container);

		this.chartObject = chartObject;
		this.chartType = chartType;
		this.defaultRenderer = defaultRenderer;
		if(chartType.equals(ChartObject.TYPE_SIMPLE)) {
			headerPanelMulti.removeFromParent();
			headerPanelGauge.removeFromParent();

			imgBar.addClickHandler(changeChartHandler);
			imgPie.addClickHandler(changeChartHandler);
			imgDoughnut.addClickHandler(changeChartHandler);
			imgColumn.addClickHandler(changeChartHandler);
			imgLine.addClickHandler(changeChartHandler);
			imgArea.addClickHandler(changeChartHandler);
			imgSpline.addClickHandler(changeChartHandler);
			imgpareto.addClickHandler(changeChartHandler);

			images.add(imgBar);
			images.add(imgPie);
			images.add(imgDoughnut);
			images.add(imgColumn);
			images.add(imgLine);
			images.add(imgArea);
			images.add(imgSpline);
			images.add(imgpareto);

		}
		else if(chartType.equals(ChartObject.TYPE_MULTI)) {
			headerPanelSimple.removeFromParent();
			headerPanelGauge.removeFromParent();

			imgMultiBars.addClickHandler(changeChartHandler);
			imgMultiColumns.addClickHandler(changeChartHandler);
			imgMultiLines.addClickHandler(changeChartHandler);
			imgMultiArea.addClickHandler(changeChartHandler);

			imgStackedColumns.addClickHandler(changeChartHandler);
			imgStackedArea.addClickHandler(changeChartHandler);
			imgSteppedLine.addClickHandler(changeChartHandler);

			images.add(imgMultiBars);
			images.add(imgMultiLines);
			images.add(imgMultiColumns);
			images.add(imgMultiArea);

			images.add(imgStackedColumns);
			images.add(imgStackedArea);
			images.add(imgSteppedLine);
		}
		else if(chartType.equals(ChartObject.TYPE_GAUGE)) {
			headerPanelSimple.removeFromParent();
			headerPanelMulti.removeFromParent();

			imgAngGauge.addClickHandler(changeChartHandler);
			imgLinGauge.addClickHandler(changeChartHandler);

			images.add(imgAngGauge);
			images.add(imgLinGauge);

			// XXX
			fusionChartsContainer.removeFromParent();

			HTMLPanel previous = new HTMLPanel("");
			HTMLPanel current = new HTMLPanel("");
			HTMLPanel next = new HTMLPanel("");

			previous.getElement().setId(DIV_CONTAINER + "_previous");
			current.getElement().setId(DIV_CONTAINER + "_current");
			next.getElement().setId(DIV_CONTAINER + "_next");

			// setGaugeChartStyle(previous, false);
			// setGaugeChartStyle(current, true);
			// setGaugeChartStyle(next, false);

			mainPanel.add(previous);
			mainPanel.add(current);
			mainPanel.add(next);

		}

	}

	@Override
	protected void onLoad() {
		try {
			renderChart(generateJson(defaultRenderer), container, "100%", "100%");
		} catch(Exception e) {
			e.printStackTrace();
		}
		super.onLoad();
	}

	private ClickHandler changeChartHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {

			try {
//				for(Image img : images) {
//					img.removeStyleName(style.btnImgSelected());
//				}
//				((Image) event.getSource()).addStyleName(style.btnImgSelected());

				if(event.getSource() == imgPie) {
					defaultRenderer = ChartObject.RENDERER_PIE;
					renderChart(generateJson(ChartObject.RENDERER_PIE), container, "100%", "100%");
				}
				else if(event.getSource() == imgDoughnut) {
					defaultRenderer = ChartObject.RENDERER_DONUT;
					renderChart(generateJson(ChartObject.RENDERER_DONUT), container, "100%", "100%");
				}
				else if(event.getSource() == imgBar) {
					defaultRenderer = ChartObject.RENDERER_BAR;
					renderChart(generateJson(ChartObject.RENDERER_BAR), container, "100%", "100%");
				}
				else if(event.getSource() == imgColumn) {
					defaultRenderer = ChartObject.RENDERER_COLUMN;
					renderChart(generateJson(ChartObject.RENDERER_COLUMN), container, "100%", "100%");
				}
				else if(event.getSource() == imgLine) {
					defaultRenderer = ChartObject.RENDERER_LINE;
					renderChart(generateJson(ChartObject.RENDERER_LINE), container, "100%", "100%");
				}

				else if(event.getSource() == imgArea) {
					defaultRenderer = ChartObject.RENDERER_AREA;
					renderChart(generateJson(ChartObject.RENDERER_AREA), container, "100%", "100%");
				}
				else if(event.getSource() == imgSpline) {
					defaultRenderer = ChartObject.RENDERER_SPLINE;
					renderChart(generateJson(ChartObject.RENDERER_SPLINE), container, "100%", "100%");
				}
				else if(event.getSource() == imgpareto) {
					defaultRenderer = ChartObject.RENDERER_PARETO;
					renderChart(generateJson(ChartObject.RENDERER_PARETO), container, "100%", "100%");
				}

				else if(event.getSource() == imgMultiLines) {
					defaultRenderer = ChartObject.RENDERER_MULTI_LINE;
					renderChart(generateJson(ChartObject.RENDERER_MULTI_LINE), container, "100%", "100%");
				}
				else if(event.getSource() == imgMultiColumns) {
					defaultRenderer = ChartObject.RENDERER_MULTI_COLUMN;
					renderChart(generateJson(ChartObject.RENDERER_MULTI_COLUMN), container, "100%", "100%");
				}
				else if(event.getSource() == imgMultiBars) {
					defaultRenderer = ChartObject.RENDERER_MULTI_BAR;
					renderChart(generateJson(ChartObject.RENDERER_MULTI_BAR), container, "100%", "100%");
				}

				else if(event.getSource() == imgMultiArea) {
					defaultRenderer = ChartObject.RENDERER_MULTI_AREA;
					renderChart(generateJson(ChartObject.RENDERER_MULTI_AREA), container, "100%", "100%");
				}
				else if(event.getSource() == imgStackedArea) {
					defaultRenderer = ChartObject.RENDERER_MULTI_STACKEDAREA;
					renderChart(generateJson(ChartObject.RENDERER_MULTI_STACKEDAREA), container, "100%", "100%");
				}
				else if(event.getSource() == imgStackedColumns) {
					defaultRenderer = ChartObject.RENDERER_MULTI_STACKEDCOLUMN;
					renderChart(generateJson(ChartObject.RENDERER_MULTI_STACKEDCOLUMN), container, "100%", "100%");
				}
				else if(event.getSource() == imgSteppedLine) {
					defaultRenderer = ChartObject.RENDERER_MULTI_STEPLINE;
					renderChart(generateJson(ChartObject.RENDERER_MULTI_STEPLINE), container, "100%", "100%");
				}

				// XXX
				// else if (event.getSource() == imgAngGauge) {
				// defaultRenderer = ChartObject.RENDERER_GAUGE_ANGULAR;
				// createGauge();
				// }
				// else if (event.getSource() == imgLinGauge) {
				// defaultRenderer = ChartObject.RENDERER_GAUGE_LINEAR;
				// createGauge();
				// }
			} catch(Exception e) {
				e.printStackTrace();
			}

		}
	};

	private final native void renderChart(String json, String div, String width, String height) /*-{
		$wnd.renderChartJs(json, div, width, height);
	}-*/;

	protected String generateJson(String chartType) throws Exception {
		JSONObject root = new JSONObject();
		root.put("type", getType(chartType));
		root.put("data", getData(chartType));
		root.put("options", getOptions(chartType));
		String json = root.toString();
		System.out.println(json);
		return json;
	}

	private JSONString getType(String chartType) {
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
			else if (chartType.equals(ChartObject.RENDERER_DONUT)
					|| chartType.equals(ChartObject.RENDERER_PIE)) {
				return new JSONString("pie");
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

	private JSONObject getOptions(String type) {
		JSONObject options = new JSONObject();
		options.put("responsive", JSONBoolean.getInstance(true));
		options.put("maintainAspectRatio", JSONBoolean.getInstance(false));
		if(chartObject.getTitle() != null && !chartObject.getTitle().isEmpty()) {
			JSONObject titleObj = new JSONObject();
			titleObj.put("display", JSONBoolean.getInstance(true));
			titleObj.put("text", new JSONString(chartObject.getTitle()));
			options.put("title", titleObj);
		}
		if(!type.equals(ChartObject.RENDERER_PIE) && !type.equals(ChartObject.RENDERER_DONUT)) {
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
		for(ChartValue value : chartObject.getValues()) {
			if(chartObject.isMultiSeries()) {
//				labels.set(i, new JSONString(((ValueMultiSerie) value).getSerieName() != null ? ((ValueMultiSerie) value).getSerieName() : ""));
			}
			else {
				labels.set(i, new JSONString(value.getCategory() != null ? value.getCategory() : ""));
			}
			i++;
		}

		JSONArray datasets = new JSONArray();
		if(chartObject.isMultiSeries()) {
			int j = 0;
			for(ChartValue multiVal : chartObject.getValues()) {
				JSONObject dataset = new JSONObject();
				JSONArray dataArray = new JSONArray();
				JSONArray colorArray = new JSONArray();
				i = 0;
				for(ChartValue value : ((ValueMultiSerie) multiVal).getSerieValues()) {
					dataArray.set(i, new JSONNumber(((ValueSimpleSerie) value).getValue()));
					colorArray.set(i, new JSONString(j < 12 ? colors[j] : colors[j%12]));
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
			for(ChartValue value : chartObject.getValues()) {

				dataArray.set(i, new JSONNumber(((ValueSimpleSerie) value).getValue()));
				colorArray.set(i, new JSONString(i < 12 ? colors[i] : colors[i%12]));
				i++;
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

}
