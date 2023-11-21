package bpm.gwt.commons.client.charts;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.I18N.LabelsConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author Marc
 * 
 * Please use {@link ChartRenderPanel}
 * 
 */
@Deprecated
public class FusionChartsPanel extends Composite {

	private static final String DIV_CONTAINER = "fusionChartsContainer";

	interface MyStyle extends CssResource {
		String btnImgSelected();
	}

	@UiField
	MyStyle style;

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

	private static FusionChartsPanelUiBinder uiBinder = GWT.create(FusionChartsPanelUiBinder.class);

	interface FusionChartsPanelUiBinder extends UiBinder<Widget, FusionChartsPanel> {
	}

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
	public FusionChartsPanel(ChartObject chartObject, String chartType, String defaultRenderer) {
		initWidget(uiBinder.createAndBindUi(this));

		container = DIV_CONTAINER + new Object().hashCode();

		fusionChartsContainer.getElement().setId(container);
		fusionChartsContainer.addStyleName("chartfd");

		this.chartObject = chartObject;
		this.chartType = chartType;
		this.defaultRenderer = defaultRenderer;
		if (chartType.equals(ChartObject.TYPE_SIMPLE)) {
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
		else if (chartType.equals(ChartObject.TYPE_MULTI)) {
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
		else if (chartType.equals(ChartObject.TYPE_GAUGE)) {
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

			setGaugeChartStyle(previous, false);
			setGaugeChartStyle(current, true);
			setGaugeChartStyle(next, false);

			mainPanel.add(previous);
			mainPanel.add(current);
			mainPanel.add(next);

		}

	}

	private void setGaugeChartStyle(HTMLPanel panel, boolean isCurrentValue) {
		panel.getElement().getStyle().setFloat(Float.LEFT);
		panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		panel.getElement().getStyle().setHeight(99, Unit.PCT);

		if (isCurrentValue) {
			panel.getElement().getStyle().setWidth(50, Unit.PCT);
		}
		else {
			panel.getElement().getStyle().setWidth(20, Unit.PCT);
		}
	}

	public FusionChartsPanel(ChartObject chartObject, String chartType, String defaultRenderer, Boolean buttons) {
		initWidget(uiBinder.createAndBindUi(this));

		container = DIV_CONTAINER + new Object().hashCode();

		fusionChartsContainer.getElement().setId(container);

		this.chartObject = chartObject;
		this.chartType = chartType;
		this.defaultRenderer = defaultRenderer;
		if (!buttons) {
			headerPanelMulti.removeFromParent();
			headerPanelSimple.removeFromParent();
			headerPanelGauge.removeFromParent();
			headerPanel.removeFromParent();

		}
		else {
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

			// images.add(imgStackedColumns);
			// images.add(imgStackedArea);
			// images.add(imgSteppedLine);
		}
	}

	private ClickHandler changeChartHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {

			for (Image img : images) {
				img.removeStyleName(style.btnImgSelected());
			}
			((Image) event.getSource()).addStyleName(style.btnImgSelected());

			if (event.getSource() == imgPie) {
				defaultRenderer = ChartObject.RENDERER_PIE;
				renderChart(ChartObject.RENDERER_PIE, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgDoughnut) {
				defaultRenderer = ChartObject.RENDERER_DONUT;
				renderChart(ChartObject.RENDERER_DONUT, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgBar) {
				defaultRenderer = ChartObject.RENDERER_BAR;
				renderChart(ChartObject.RENDERER_BAR, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgColumn) {
				defaultRenderer = ChartObject.RENDERER_COLUMN;
				renderChart(ChartObject.RENDERER_COLUMN, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgLine) {
				defaultRenderer = ChartObject.RENDERER_LINE;
				renderChart(ChartObject.RENDERER_LINE, generateXmlChart(), container, "100%", "100%");
			}

			else if (event.getSource() == imgArea) {
				defaultRenderer = ChartObject.RENDERER_AREA;
				renderChart(ChartObject.RENDERER_AREA, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgSpline) {
				defaultRenderer = ChartObject.RENDERER_SPLINE;
				renderChart(ChartObject.RENDERER_SPLINE, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgpareto) {
				defaultRenderer = ChartObject.RENDERER_PARETO;
				renderChart(ChartObject.RENDERER_PARETO, generateXmlChart(), container, "100%", "100%");
			}

			else if (event.getSource() == imgMultiLines) {
				defaultRenderer = ChartObject.RENDERER_MULTI_LINE;
				renderChart(ChartObject.RENDERER_MULTI_LINE, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgMultiColumns) {
				defaultRenderer = ChartObject.RENDERER_MULTI_COLUMN;
				renderChart(ChartObject.RENDERER_MULTI_COLUMN, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgMultiBars) {
				defaultRenderer = ChartObject.RENDERER_MULTI_BAR;
				renderChart(ChartObject.RENDERER_MULTI_BAR, generateXmlChart(), container, "100%", "100%");
			}

			else if (event.getSource() == imgMultiArea) {
				defaultRenderer = ChartObject.RENDERER_MULTI_AREA;
				renderChart(ChartObject.RENDERER_MULTI_AREA, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgStackedArea) {
				defaultRenderer = ChartObject.RENDERER_MULTI_STACKEDAREA;
				renderChart(ChartObject.RENDERER_MULTI_STACKEDAREA, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgStackedColumns) {
				defaultRenderer = ChartObject.RENDERER_MULTI_STACKEDCOLUMN;
				renderChart(ChartObject.RENDERER_MULTI_STACKEDCOLUMN, generateXmlChart(), container, "100%", "100%");
			}
			else if (event.getSource() == imgSteppedLine) {
				defaultRenderer = ChartObject.RENDERER_MULTI_STEPLINE;
				renderChart(ChartObject.RENDERER_MULTI_STEPLINE, generateXmlChart(), container, "100%", "100%");
			}

			// XXX
			else if (event.getSource() == imgAngGauge) {
				defaultRenderer = ChartObject.RENDERER_GAUGE_ANGULAR;
				createGauge();
			}
			else if (event.getSource() == imgLinGauge) {
				defaultRenderer = ChartObject.RENDERER_GAUGE_LINEAR;
				createGauge();
			}

		}
	};

	private final native void renderChart(String type, String xml, String div, String width, String height) /*-{
		$wnd.renderChart(type, xml, div, width, height);
	}-*/;

	/**
	 * Generate the chart element
	 * 
	 * @return
	 */
	private String generateXmlChart() {

		StringBuilder buf = new StringBuilder();
		buf.append("<chart");

		buf.append(" caption='");
		buf.append(chartObject.getTitle().replace("'", "\\'"));
		buf.append("'");

		buf.append(" xaxisname='");
		buf.append(chartObject.getxAxisName().replace("'", "\\'"));
		buf.append("'");

		buf.append(" yaxisname='");
		buf.append(chartObject.getyAxisName().replace("'", "\\'"));
		buf.append("'");

		if (chartObject.getColors() != null) {
			buf.append(" palettecolors='");
			for (String color : chartObject.getColors()) {
				buf.append(color + ",");
			}
			buf.deleteCharAt(buf.lastIndexOf(","));
			buf.append("'");
		}

		buf.append(" showaxislines='1' bgColor='#FFFFFF' bgAlpha='100' showBorder='0' exportEnabled='1'");

		buf.append(">\n");
		buf.append(generateXmlData());

		buf.append("</chart>");

		return buf.toString();
	}

	/**
	 * Generate the xml data
	 * 
	 * @param values
	 * @param level
	 * @return
	 */
	private String generateXmlData() {
		StringBuilder buf = new StringBuilder();

		if (chartType.equals(ChartObject.TYPE_MULTI)) {

			StringBuilder catBuf = new StringBuilder();
			StringBuilder valueBuf = new StringBuilder();

			catBuf.append("	<categories>\n");
			boolean first = true;

			for (ChartValue val : chartObject.getValues()) {
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
						valueBuf.append("		<set value='" + ((value.getValue() == null)? "" : value.getValue()) + "' color='" + value.getColor() + "'/>");
					else
						valueBuf.append("		<set value='" + ((value.getValue() == null)? "" : value.getValue()) + "'/>");
				}
				valueBuf.append("	</dataset>\n");
			}

			catBuf.append("	</categories>\n");
			buf.append(catBuf.toString());
			buf.append(valueBuf.toString());
		}
		else {
			for (ChartValue val : chartObject.getValues()) {
				ValueSimpleSerie value = (ValueSimpleSerie) val;

				buf.append("	<set ");
				buf.append("label='");
				buf.append(value.getCategory().replace("'", ""));
				buf.append("' ");

				buf.append("value='");
				buf.append(((value.getValue() == null)? "" : value.getValue()));
				buf.append("'/>\n");

			}
		}

		return buf.toString();
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (chartType.equals(ChartObject.TYPE_GAUGE)) {

			createGauge();

			// XXX
			// String xml = generateXmlGauge();
			// System.out.println(xml);
			// renderChart(defaultRenderer, xml, container, "700", "100%");
		}
		if (chartType.equals(ChartObject.TYPE_SCATTER_BUBBLE)) {

			String xml =generateXmlCluster();
			System.out.println(xml);
			renderChart(defaultRenderer, xml, container, "100%", "100%");
		}
		else {
			String xml = generateXmlChart();
			System.out.println(xml);
			renderChart(defaultRenderer, xml, container, "100%", "100%");
		}
	}

	private void createGauge() {
		ValueGauge previous = null;
		ValueGauge current = null;
		ValueGauge next = null;

		for (ChartValue val : chartObject.getValues()) {
			ValueGauge v = (ValueGauge) val;
			if (v.getPosition().equals("previous")) {
				previous = v;
			}
			else if (v.getPosition().equals("next")) {
				next = v;
			}
			else {
				current = v;
			}
		}

		String title = chartObject.getTitle();

		if (previous != null) {
			chartObject.setTitle(LabelsConstants.lblCnst.previous() + " " + title);
			String xml = generateXmlGauge(previous);
			renderChart(defaultRenderer, xml, DIV_CONTAINER + "_previous", "100%", "100%");
		}
		if (next != null) {
			chartObject.setTitle(LabelsConstants.lblCnst.next() + " " + title);
			String xml = generateXmlGauge(next);
			renderChart(defaultRenderer, xml, DIV_CONTAINER + "_next", "100%", "100%");
		}
		chartObject.setTitle(title);
		String xml = generateXmlGauge(current);
		renderChart(defaultRenderer, xml, DIV_CONTAINER + "_current", "100%", "100%");

	}

	public void renderCharts() {

	}

	private String generateXmlGauge(ValueGauge gaugeValue) {

		StringBuilder buf = new StringBuilder();

		// gauge options
		buf.append("<chart caption='" + chartObject.getTitle() + "' exportEnabled='1'");

		buf.append(" lowerlimit='" + gaugeValue.getMinimum() + "'");
		buf.append(" upperlimit='" + gaugeValue.getMaximum() + "'");

		if (defaultRenderer.equals(ChartObject.RENDERER_GAUGE_ANGULAR)) {
			buf.append(" manageresize='1' origw='400' origh='250' managevalueoverlapping='1' autoaligntickvalues='1'");
			buf.append(" bgcolor='FFFFFF' fillangle='45' majortmnumber='10' majortmheight='8'");
			buf.append(" showgaugeborder='0' gaugeouterradius='140' gaugeoriginx='205' gaugeoriginy='206' gaugeinnerradius='2' formatnumberscale='1'");
			buf.append(" decmials='2' tickmarkdecimals='1' pivotradius='10' showpivotborder='1' pivotbordercolor='000000'");
			buf.append(" pivotborderthickness='10' pivotfillmix='666666' tickvaluedistance='10' valuebelowpivot='1' showvalue='1' showborder='0'>\n");
		}

		else if (defaultRenderer.equals(ChartObject.RENDERER_GAUGE_LINEAR)) {
			buf.append(" manageresize='1' chartbottommargin='5' showtickmarks='1' chartBottomMargin='60' chartTopMargin='60'");
			buf.append(" tickvaluedistance='0' majortmnumber='5' majortmheight='4' minortmnumber='0' showtickvalues='1' decimals='0' ledgap='1'");
			buf.append(" ledsize='1' ledboxbgcolor='333333' ledbordercolor='666666' borderthickness='2' chartrightmargin='20'");
			buf.append(" bgAlpha='100' showBorder='0' bgcolor='FFFFFF'>\n");
		}

		// colorRanges
		buf.append("	<colorrange>\n");
		if (!gaugeValue.isIncrease()) {
			int minRed = (int) ((gaugeValue.getObjective() + (gaugeValue.getObjective() / 10) + gaugeValue.getMaximum()) / 2);
			buf.append("		<color minvalue='" + minRed + "' maxvalue='" + gaugeValue.getMaximum() + "' code='#e44a00' />\n");
			buf.append("		<color minvalue='" + gaugeValue.getObjective() + "' maxvalue='" + minRed + "' code='#f8bd19' />\n");
			buf.append("		<color minvalue='" + gaugeValue.getMinimum() + "' maxvalue='" + gaugeValue.getObjective() + "' code='#6baa01' />\n");
		}
		else {
			int maxRed = (int) ((gaugeValue.getObjective() - (gaugeValue.getObjective() / 10) + gaugeValue.getMinimum()) / 2);
			buf.append("		<color minvalue='" + gaugeValue.getMinimum() + "' maxvalue='" + maxRed + "' code='#e44a00' />\n");
			buf.append("		<color minvalue='" + maxRed + "' maxvalue='" + gaugeValue.getObjective() + "' code='#f8bd19' />\n");
			buf.append("		<color minvalue='" + gaugeValue.getObjective() + "' maxvalue='" + gaugeValue.getMaximum() + "' code='#6baa01' />\n");
		}
		buf.append("	</colorrange>\n");

		if (defaultRenderer.equals(ChartObject.RENDERER_GAUGE_ANGULAR)) {
			// points
			buf.append("	<dials>\n");
			buf.append("		<dial value='" + gaugeValue.getValue() + "' borderalpha='0' bgcolor='000000' basewidth='20' topwidth='1' radius='130' />\n");
			buf.append("	</dials>\n");

			// objective
			buf.append("	<trendpoints>\n");
			buf.append("		<point startvalue='" + gaugeValue.getObjective() + "' usemarker='1' markercolor='#F1f1f1' markerbordercolor='#666666' markerradius='10' markertooltext='Objective - " + gaugeValue.getObjective() + "' displayvalue='" + gaugeValue.getObjective() + "' color='#000000' thickness='2' alpha='100' />\n");
			buf.append("	</trendpoints>\n");
		}
		else if (defaultRenderer.equals(ChartObject.RENDERER_GAUGE_LINEAR)) {
			buf.append("	<value>" + gaugeValue.getValue() + "</value>\n");
		}

		buf.append("</chart>");

		return buf.toString();
	}
	
	
	private String generateXmlCluster() {

		StringBuilder buf = new StringBuilder();
		buf.append("<chart");

		buf.append(" caption='");
		buf.append(chartObject.getTitle().replace("'", "\\'"));
		buf.append("'");

		buf.append(" xaxisname='");
		buf.append(chartObject.getxAxisName().replace("'", "\\'"));
		buf.append("'");

		buf.append(" yaxisname='");
		buf.append(chartObject.getyAxisName().replace("'", "\\'"));
		buf.append("'");

		if (chartObject.getColors() != null) {
			buf.append(" palettecolors='");
			for (String color : chartObject.getColors()) {
				buf.append(color + ",");
			}
			buf.deleteCharAt(buf.lastIndexOf(","));
			buf.append("'");
		}

		buf.append(" showaxislines='1' showverticalline='1' bgColor='#FFFFFF' bgAlpha='100' showBorder='0' exportEnabled='1'");

		buf.append(">\n");	

		StringBuilder catBuf = new StringBuilder();
		StringBuilder valueBuf = new StringBuilder();
/*
		catBuf.append("	<categories>\n");
	//	boolean first = true;
		
		for (ChartValue val : chartObject.getValues()) {
			ValueMultiSerie serie = (ValueMultiSerie) val;
			catBuf.append("		<category x='" + serie.getCategory().replace("'", "") + "' label='" + serie.getCategory().replace("'", "") + "' showverticalline='1' />");
		}
		catBuf.append("	</categories>\n");
		*/
		for (ChartValue val : chartObject.getValues()) {	
			ValueMultiSerie serie = (ValueMultiSerie) val;
			
			valueBuf.append("	<dataset seriesname='" + serie.getSerieName().replace("'", "") + "'>\n");
			for (ChartValue value : serie.getSerieValues()) {
				ValuePointSerie m = (ValuePointSerie) value;

					if (value.getColor() != null)
						valueBuf.append("		<set x='" + m.getX()+ "' y='"+m.getY() + "' color='" + value.getColor() + "'/>");
					else
						valueBuf.append("		<set x='" + m.getX()+ "' y='"+m.getY() + "'/>");
			}
			valueBuf.append("	</dataset>\n");
		}

		
		buf.append(catBuf.toString());
		buf.append(valueBuf.toString());	

		buf.append("</chart>");

		return buf.toString();
	}


	public String getDefaultRenderer() {
		return defaultRenderer;
	}

	public ChartObject getChartObject() {
		return chartObject;
	}

	public String getChartType() {
		return chartType;
	}

	public String getImageData() {
		return getImageData(container);
	}

	private final native String getImageData(String div) /*-{
		return $wnd.getImageData(div);
	}-*/;
}
