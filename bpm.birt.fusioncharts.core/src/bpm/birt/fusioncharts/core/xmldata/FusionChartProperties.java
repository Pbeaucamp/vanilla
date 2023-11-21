package bpm.birt.fusioncharts.core.xmldata;

import java.util.HashMap;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class FusionChartProperties {
	
	public static final String CUSTOM_PROPS = "CustomProps";
	
	//COSMETICS
	public static final String BG_COLOR = "bgColor";
	public static final String BG_ALPHA = "bgAlpha";
	public static final String SHOW_BORDER = "showBorder";
	public static final String BORDER_COLOR = "borderColor";
	public static final String BORDER_THICKNESS = "borderThickness";
	public static final String BORDER_ALPHA = "borderAlpha";
	
	//CANVAS
	public static final String CANVAS_BG_COLOR = "canvasbgColor";
	public static final String CANVAS_BG_ALPHA = "canvasbgAlpha";
	public static final String CANVAS_BORDER_COLOR = "canvasBorderColor";
	public static final String CANVAS_BORDER_THICKNESS = "canvasBorderThickness";
	public static final String CANVAS_BORDER_ALPHA = "canvasBorderAlpha";
	
	//TITLE AND AXIS NAMES
	public static final String SUBCAPTION = "subCaption";
	public static final String X_AXIS_NAME = "xAxisName";
	public static final String Y_AXIS_NAME = "yAxisName";
	public static final String ROTATE_Y_AXIS_NAME = "rotateYAxisName";
	
	//FONT
	public static final String OUT_BASE_FONT_SIZE = "outCnvbaseFontSize";
	public static final String OUT_BASE_FONT_COLOR = "outCnvbaseFontColor";
	
	//DATA PLOT
	public static final String PLOT_GRADIENT_COLOR = "plotGradientColor";
	
	//LABELS
	public static final String SHOW_LABELS = "showLabels";
	public static final String LABEL_DISPLAY = "labelDisplay"; // AUTO  WRAP  Rotate  Stagger
	public static final String SLANT_LABELS = "slantLabels";
	public static final String STAGGER_LINES = "staggerLines";

	//DATA VALUES
	public static final String SHOW_VALUES = "showValues";
	public static final String ROTATE_VALUES = "rotateValues";
	public static final String PLACE_VALUES_INSIDE = "placeValuesInside";
	public static final String BASE_FONT_SIZE = "baseFontSize";
	public static final String BASE_FONT_COLOR = "baseFontColor";
	
	//PALETTES
	public static final String PALETTE = "palette"; //1 to 5
	
	//TOOLTIP
	public static final String SHOW_TOOLTIP = "showToolTip";
	public static final String SHOW_TOOLTIP_SHADOW = "showToolTipShadow";
	public static final String TOOLTIP_BORDER_COLOR = "toolTipBorderColor";
	public static final String TOOLTIP_BG_COLOR = "toolTipBgColor";

	//MAP PADDING & MARGINS
	public static final String CHART_LEFT_MARGIN = "chartLeftMargin";
	public static final String CHART_RIGHT_MARGIN = "chartRightMargin";
	public static final String CHART_TOP_MARGIN = "chartTopMargin";
	public static final String CHART_BOTTOM_MARGIN = "chartBottomMargin";
	public static final String CAPTION_PADDING = "captionPadding";
	public static final String X_AXIS_NAME_PADDING = "xAxisNamePadding";
	public static final String Y_AXIS_NAME_PADDING = "yAxisNamePadding";
	public static final String Y_AXIS_VALUES_PADDING = "yAxisValuesPadding";
	public static final String LABEL_PADDING = "labelPadding";
	public static final String VALUE_PADDING = "valuePadding";
	
	//LEGEND
	public static final String SHOW_LEGEND = "showLegend";
	public static final String LEGEND_POSITION = "legendPosition";//RIGHT OR BOTTOM
	
	//NUMBER & DECIMALS
	public static final String DECIMALS = "decimals";
	public static final String FORCE_DECIMALS = "forceDecimals";
	public static final String FORMAT_NUMBER_SCALE = "formatNumberScale";
	public static final String DECIMAL_SEPARATOR = "decimalSeparator";
	public static final String THOUSAND_SEPARATOR = "thousandSeparator";
	public static final String NUMBER_PREFIX = "numberPrefix";
	public static final String NUMBER_SUFFIX = "numberSuffix";
	
	//PIE & DOUGHNUT
	public static final String SHOW_PERCENT_VALUES = "showPercentValues";
	public static final String SHOW_PERCENT_IN_TOOLTIP = "showPercentInTooltip";
	public static final String SLICING_DISTANCE = "slicingDistance";
	
	//STACKED CHART
	public static final String SHOW_SUM = "showSum";
	
	//STYLE
	public static final String CUSTOM_CAPTION_SIZE = "size";
	public static final String CUSTOM_CAPTION_COLOR = "color";
	private static final String MY_CUSTOM_CAPTION_STYLE = "myCaptionStyle";

	
	public static final String[] FUSION_CHART_PROPERTIES_STYLES = {
		CUSTOM_CAPTION_SIZE,
		CUSTOM_CAPTION_COLOR
	};
	
	public static final String[] FUSION_CHART_PROPERTIES = {
		BG_COLOR,
		BG_ALPHA,
		SHOW_BORDER,
		BORDER_COLOR,
		BORDER_THICKNESS,
		BORDER_ALPHA,
		CANVAS_BG_COLOR,
		CANVAS_BG_ALPHA,
		CANVAS_BORDER_COLOR,
		CANVAS_BORDER_THICKNESS,
		CANVAS_BORDER_ALPHA,
		SUBCAPTION,
		X_AXIS_NAME,
		Y_AXIS_NAME,
		ROTATE_Y_AXIS_NAME,
		OUT_BASE_FONT_SIZE,
		OUT_BASE_FONT_COLOR,
		PLOT_GRADIENT_COLOR,
		SHOW_LABELS,
		LABEL_DISPLAY,
		SLANT_LABELS,
		STAGGER_LINES,
		SHOW_VALUES,
		ROTATE_VALUES,
		PLACE_VALUES_INSIDE,
		BASE_FONT_SIZE,
		BASE_FONT_COLOR,
		PALETTE,
		SHOW_TOOLTIP,
		SHOW_TOOLTIP_SHADOW,
		TOOLTIP_BORDER_COLOR,
		TOOLTIP_BG_COLOR,
		CHART_LEFT_MARGIN,
		CHART_RIGHT_MARGIN,
		CHART_TOP_MARGIN,
		CHART_BOTTOM_MARGIN,
		CAPTION_PADDING,
		X_AXIS_NAME_PADDING,
		Y_AXIS_NAME_PADDING,
		Y_AXIS_VALUES_PADDING,
		LABEL_PADDING,
		VALUE_PADDING,
		SHOW_LEGEND,
		LEGEND_POSITION,
		DECIMALS,
		FORCE_DECIMALS,
		FORMAT_NUMBER_SCALE,
		DECIMAL_SEPARATOR,
		THOUSAND_SEPARATOR,
		NUMBER_PREFIX,
		NUMBER_SUFFIX,
		SHOW_PERCENT_VALUES,
		SHOW_PERCENT_IN_TOOLTIP,
		SLICING_DISTANCE,
		SHOW_SUM,
	};
	
	public static Properties buildDefaultProperties(){
		Properties props = new Properties();
		props.put(BG_COLOR, "");
		props.put(BG_ALPHA, "100");
		props.put(SHOW_BORDER, "1");
		props.put(BORDER_COLOR, "");
		props.put(BORDER_THICKNESS, "1");
		props.put(BORDER_ALPHA, "100");
		props.put(CANVAS_BG_COLOR, "");
		props.put(CANVAS_BG_ALPHA, "100");
		props.put(CANVAS_BORDER_COLOR, "");
		props.put(CANVAS_BORDER_THICKNESS, "1");
		props.put(CANVAS_BORDER_ALPHA, "100");
		props.put(SUBCAPTION, "");
		props.put(X_AXIS_NAME, "");
		props.put(Y_AXIS_NAME, "");
		props.put(ROTATE_Y_AXIS_NAME, "1");
		props.put(OUT_BASE_FONT_SIZE, "9");
		props.put(OUT_BASE_FONT_COLOR, "");
		props.put(PLOT_GRADIENT_COLOR, "");
		props.put(SHOW_LABELS, "1");
		props.put(LABEL_DISPLAY, "AUTO");
		props.put(SLANT_LABELS, "");
		props.put(STAGGER_LINES, "");
		props.put(SHOW_VALUES, "1");
		props.put(ROTATE_VALUES, "0");
		props.put(PLACE_VALUES_INSIDE, "0");
		props.put(BASE_FONT_SIZE, "9");
		props.put(BASE_FONT_COLOR, "");
		props.put(PALETTE, "1");
		props.put(SHOW_TOOLTIP, "1");
		props.put(SHOW_TOOLTIP_SHADOW, "1");
		props.put(TOOLTIP_BORDER_COLOR, "");
		props.put(TOOLTIP_BG_COLOR, "");
		props.put(CHART_LEFT_MARGIN, "");
		props.put(CHART_RIGHT_MARGIN, "");
		props.put(CHART_TOP_MARGIN, "");
		props.put(CHART_BOTTOM_MARGIN, "");
		props.put(CAPTION_PADDING, "");
		props.put(X_AXIS_NAME_PADDING, "");
		props.put(Y_AXIS_NAME_PADDING, "");
		props.put(Y_AXIS_VALUES_PADDING, "");
		props.put(LABEL_PADDING, "");
		props.put(VALUE_PADDING, "");
		props.put(SHOW_LEGEND, "1");
		props.put(LEGEND_POSITION, "RIGHT");
		props.put(DECIMALS, "2");
		props.put(FORCE_DECIMALS, "");
		props.put(FORMAT_NUMBER_SCALE, "");
		props.put(DECIMAL_SEPARATOR, ",");
		props.put(THOUSAND_SEPARATOR, "");
		props.put(NUMBER_PREFIX, "");
		props.put(NUMBER_SUFFIX, "");
		props.put(SHOW_PERCENT_VALUES, "0");
		props.put(SHOW_PERCENT_IN_TOOLTIP, "0");
		props.put(SLICING_DISTANCE, "15");
		props.put(SHOW_SUM, "0");
		props.put(CUSTOM_CAPTION_SIZE, "9");
		props.put(CUSTOM_CAPTION_COLOR, "");
		
		return props;
	}
	
	public static Properties buildPropertiesFromXml(String xml){
		try {
			Document doc = DocumentHelper.parseText(xml);
			Element root = doc.getRootElement();
			
			Properties props = new Properties();
			Element pr = root.element(CUSTOM_PROPS);
			if(pr != null){
				for(Object obj : pr.elements("property")){
					Element el = (Element)obj;
					props.put(el.element("name").getText(), el.element("value").getText());
				}
			}
			
			return props;
		} catch (Exception e) {
			e.printStackTrace();
			return buildDefaultProperties();
		}
	}
	
	public static String buildPropertiesXml(Properties props){
		StringBuilder builder = new StringBuilder("<root>\n");
		builder.append("    <" + CUSTOM_PROPS + ">\n");
		for(Object obj : props.keySet()){
			builder.append("        <property>\n");
			String propName = (String)obj;
			builder.append("            <name>" + propName + "</name>\n");
			builder.append("            <value>" + props.get(propName) + "</value>\n");
			builder.append("        </property>\n");
		}
		builder.append("    </" + CUSTOM_PROPS + ">\n");
		builder.append("</root>");
		
		return builder.toString();
	}
	
	public static String buildPropertiesXml(HashMap<String, String> props){
		StringBuilder builder = new StringBuilder("<root>\n");
		builder.append("    <" + CUSTOM_PROPS + ">\n");
		for(Object obj : props.keySet()){
			builder.append("        <property>\n");
			String propName = (String)obj;
			builder.append("            <name>" + propName + "</name>\n");
			builder.append("            <value>" + props.get(propName) + "</value>\n");
			builder.append("        </property>\n");
		}
		builder.append("    </" + CUSTOM_PROPS + ">\n");
		builder.append("</root>");
		
		return builder.toString();
	}
	
	public static String getPropertiesXmlForMap(Properties props){
		StringBuilder builder = new StringBuilder();
		for(String prop : FUSION_CHART_PROPERTIES){
			if(props.get(prop) != null && !props.get(prop).toString().isEmpty()){
				builder.append(" ");
				builder.append(prop);
				builder.append("='");
				builder.append(props.get(prop));
				builder.append("'");
			}
		}
		
		return builder.toString();
	}
	
	public static String getStylesXmlForChart(Properties props){
		boolean isThereStyle = false;
		StringBuilder builder = new StringBuilder();
		builder.append("<styles>");
		builder.append("    <definition>");
		builder.append("        <style name='" + MY_CUSTOM_CAPTION_STYLE + "' type='font' font='Arial'");
		for(String prop : FUSION_CHART_PROPERTIES_STYLES){
			if(props.get(prop) != null && !props.get(prop).toString().isEmpty()){
				isThereStyle = true;
				builder.append(" ");
				builder.append(prop);
				builder.append("='");
				builder.append(props.get(prop));
				builder.append("'");
			}
		}
		builder.append("/>");
		builder.append("    </definition>");
		builder.append("    <application>");
		builder.append("        <apply toObject='Caption' styles='" + MY_CUSTOM_CAPTION_STYLE + "' />");
		builder.append("     </application>");
		builder.append("</styles>");
		
		if(isThereStyle){
			return builder.toString();
		}
		else {
			return null;
		}
	}
}
