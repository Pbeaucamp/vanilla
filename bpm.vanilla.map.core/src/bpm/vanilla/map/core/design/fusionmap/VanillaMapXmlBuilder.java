package bpm.vanilla.map.core.design.fusionmap;

import java.util.List;

public class VanillaMapXmlBuilder {
	private final static String MAP_TAG = "bpmmap";
	
	private final static String DATA_TAG = "data";
	
	private final static String ENTITY_TAG = "zone";
	private final static String ENTITY_ID = "id";
	private final static String ENTITY_VALUE = "value";
	private final static String ENTITY_LINK = "link";
	
	private final static String COLOR_RANGES_TAG = "colorranges";
	private final static String COLOR_RANGE_TAG = "range";
	
	private final static String COLOR_TAG = "color";
	private final static String COLOR_MIN = "min";
	private final static String COLOR_MAX = "max";
	private final static String COLOR_NAME = "name";
	
	private final static String ACTIONS_TAG = "actions";
	private final static String ACTION_TAG = "action";
	private final static String ACTION_ID = "id";
	private final static String ACTION_TYPE = "type";
	private final static String ACTION_SUBTYPE = "subtype";
	
	private List<ColorRange> colorRange;
	private String unit;
	private String parameters;
	private StringBuffer buf = new StringBuffer();
	
	public VanillaMapXmlBuilder(String unit, List<ColorRange> colorRange){
		this.colorRange = colorRange;
		this.unit = unit;
		if (unit == null){
			this.unit = "";
		}
		buildXmlStart();
	}
	
	/**
	 * That constructor is used when we want to had few parameters to configure the map object
	 * @param unit
	 * @param colorRange
	 */
	public VanillaMapXmlBuilder(String unit, List<ColorRange> colorRange, String parameters){
		this.colorRange = colorRange;
		this.unit = unit;
		this.parameters = parameters;
		if (unit == null){
			this.unit = "";
		}
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
		buf.append(tag(MAP_TAG, false));
		
		buf.append(tag(COLOR_RANGES_TAG, false));
		
		for(ColorRange c : colorRange){
			buf.append(tag(COLOR_RANGE_TAG, true, 
					attribute(COLOR_MIN, c.getMin()+ ""),
					attribute(COLOR_MAX, c.getMax()+ ""),
					attribute(COLOR_NAME, c.getName()+ ""),
					attribute(COLOR_TAG, "0x"+c.getHex()+ "")));
		}
		buf.append(untag(COLOR_RANGES_TAG));
		
		buf.append(tag(ACTIONS_TAG, false));
		buf.append(tag(ACTION_TAG, true, 
				attribute(ACTION_ID,"all"),
				attribute(ACTION_TYPE, "hover"),
				attribute(ACTION_SUBTYPE, "tooltip")));
		buf.append(untag(ACTIONS_TAG));
		
		buf.append(tag(DATA_TAG, false));
	}
	
	private String link(String linkName, String link){
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
