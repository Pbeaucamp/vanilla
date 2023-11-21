package bpm.vanilla.map.core.design.fusionmap;

import java.util.List;

public class FusionMapXmlBuilder {
	private final static String MAP_TAG = "map";
	private final static String UNIT = "numberSuffix";
	private final static String FONT_SIZE = "baseFontSize";
	private final static String FILL_ALPHE = "fillAlpha";
	private final static String HOVER_COLOR = "hoverColor";
	
	
	private final static String COLOR_RANGE_TAG = "colorRange";
	
	private final static String COLOR_TAG = "color";
	private final static String COLOR_MIN = "minValue";
	private final static String COLOR_MAX = "maxValue";
	private final static String COLOR_DISPLAY = "displayValue";
	private final static String COLOR_HEXA = "color";
	
	private final static String ENTITY_TAG = "entity";
	private final static String ENTITY_ID = "id";
	private final static String ENTITY_VALUE = "value";
	private final static String ENTITY_LINK = "link";
	
	private final static String DATA_TAG = "data";
	
	
	
	private List<ColorRange> colorRange;
	private String unit;
	private String parameters;
	private boolean isShowLabels;
	private boolean useIsShowLabels = true;
	private StringBuffer buf = new StringBuffer();
	
	public FusionMapXmlBuilder(String unit, List<ColorRange> colorRange, boolean isShowLabels){
		this.colorRange = colorRange;
		this.unit = unit;
		if (unit == null){
			this.unit = "";
		}
		this.isShowLabels = isShowLabels;
		buildXmlStart();
	}
	
	/**
	 * That constructor is used when we want to had few parameters to configure the map object
	 * @param unit
	 * @param colorRange
	 */
	public FusionMapXmlBuilder(String unit, List<ColorRange> colorRange, String parameters){
		this.colorRange = colorRange;
		this.unit = unit;
		this.parameters = parameters;
		if (unit == null){
			this.unit = "";
		}
		useIsShowLabels = false;
		buildXmlStart();
	}
	
	public void addEntity(String identifier, String value){
		buf.append(tag(ENTITY_TAG ,true,
				attribute(ENTITY_ID, identifier),
				attribute(ENTITY_VALUE, value)));
		
	}
	
	public void addEntity(String identifier, String value, String link){
		buf.append(tag(ENTITY_TAG, true, 
				attribute(ENTITY_ID, identifier),
				attribute(ENTITY_VALUE, value),
				link(ENTITY_LINK, link)));
	}
	 
	
	public String close(){
		buf.append(untag(DATA_TAG));
		buf.append(untag(MAP_TAG));
		
		return buf.toString();
	}
	
	private void buildXmlStart(){
		if(parameters != null){
			if(useIsShowLabels){
				buf.append(tag(MAP_TAG, false,
						attribute(UNIT, unit),
						attribute(FONT_SIZE, "9"),
						attribute(FILL_ALPHE, "70"),
						attribute(HOVER_COLOR, "639ACE"),
						attribute("showLabels", isShowLabels ? "1" : "0"),
						" ",
						parameters));
			}
			else {
				buf.append(tag(MAP_TAG, false,
						attribute(UNIT, unit),
						" ",
						parameters));
			}
		}
		else {
			if(useIsShowLabels){
				buf.append(tag(MAP_TAG, false,
						attribute(UNIT, unit),
						attribute(FONT_SIZE, "9"),
						attribute(FILL_ALPHE, "70"),
						attribute(HOVER_COLOR, "639ACE"),
						attribute("showLabels", isShowLabels ? "1" : "0")));
			}
			else {
				buf.append(tag(MAP_TAG, false,
						attribute(UNIT, unit)));
			}
		}
		buf.append(tag(COLOR_RANGE_TAG, false));
		
		for(ColorRange c : colorRange){
			buf.append(tag(COLOR_TAG, true, 
					attribute(COLOR_MIN, c.getMin()+ ""),
					attribute(COLOR_MAX, c.getMax()+ ""),
					attribute(COLOR_DISPLAY, c.getName()+ ""),
					attribute(COLOR_HEXA, c.getHex()+ "")));
			
		}
		
		buf.append(untag(COLOR_RANGE_TAG));
		buf.append(tag(DATA_TAG, false));
	}
	
	protected String link(String linkName, String link){
		StringBuilder builder = new StringBuilder(linkName);
		builder.insert(0, " ");

		builder.append("='");
		builder.append(link);
		builder.append("'");
		return builder.toString();
	}
	
	private  String attribute(String attributeName, String value){
		StringBuilder builder = new StringBuilder(attributeName);
		builder.insert(0, " ");

		builder.append("='");
		builder.append(value);
		builder.append("'");
		return builder.toString();
	}
	private  String tag(String s, boolean end, String... attribute){
		StringBuilder builder = new StringBuilder(s);
		builder.insert(0, "<");
		for(String a : attribute){
			builder.append(a);
		}
		if (end){
			builder.append(" />");
		}
		else{
			builder.append(">");
		}
//		builder.append("\n");
		return builder.toString();
	}
	
	private  String untag(String s){
		StringBuilder builder = new StringBuilder(s);
		builder.insert(0, "</");
		builder.append(">");
		return builder.toString();
	}
	
}
