package bpm.metadata.jdbc.driver;

import java.io.Serializable;
import java.util.HashMap;

public class Column implements Serializable{
	
	public static final int DIMENSION_TYPE = 0;
	public static final int MEASURE_TYPE = 1;
	public static final int PROPERTY_TYPE = 2;
	public static final int UNDEFINED_TYPE = 3;
	
	public static final String[] TYPE_NAME = new String[]{"Dimension", "Measure", "Property", "Undefined"};
	public static final String[] MEASURE_DEFAULT_BEHAVIOR = new String[]{"SUM", "COUNT", "AVG", "MIN", "MAX"};
	
	public static final int BEHAVIOR_SUM = 0;
	public static final int BEHAVIOR_COUNT = 1;
	public static final int BEHAVIOR_AVG = 2;
	public static final int BEHAVIOR_MIN = 3;
	public static final int BEHAVIOR_MAX = 4;
	
	protected int measureDefaultBehavior;
	
	protected String name;
	protected String description = "";
	protected String fontName = "Arial";
	protected String textColor = "000000";
	protected String backgroundColor = "FFFFFF";
	
	protected String originName;
	protected int type = UNDEFINED_TYPE;
	
	private HashMap<String, Boolean> isVisible = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> grant = new HashMap<String, Boolean>();
	
	/**
	 * If custom security is set to false, all columns will be available and visible for all groups
	 */
	private boolean customSecurity = false;
	
	private boolean isKpi = false;
	private boolean indexable = false;
	public Column(int measureDefaultBehavior, String name, String description,
			String fontName, String textColor, String backgroundColor,
			String originName, int type, HashMap<String, Boolean> isVisible,
			HashMap<String, Boolean> grant) {
		super();
		this.measureDefaultBehavior = measureDefaultBehavior;
		this.name = name;
		this.description = description;
		this.fontName = fontName;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.originName = originName;
		this.type = type;
		this.isVisible = isVisible;
		this.grant = grant;
	}
	public static int getDimensionType() {
		return DIMENSION_TYPE;
	}
	public static int getMeasureType() {
		return MEASURE_TYPE;
	}
	public static int getPropertyType() {
		return PROPERTY_TYPE;
	}
	public static int getUndefinedType() {
		return UNDEFINED_TYPE;
	}
	public static String[] getTypeName() {
		return TYPE_NAME;
	}
	public static int getBehaviorSum() {
		return BEHAVIOR_SUM;
	}
	public static int getBehaviorCount() {
		return BEHAVIOR_COUNT;
	}
	public static int getBehaviorAvg() {
		return BEHAVIOR_AVG;
	}
	public static int getBehaviorMin() {
		return BEHAVIOR_MIN;
	}
	public static int getBehaviorMax() {
		return BEHAVIOR_MAX;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getFontName() {
		return fontName;
	}
	public String getTextColor() {
		return textColor;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public String getOriginName() {
		return originName;
	}
	public int getType() {
		return type;
	}
	public HashMap<String, Boolean> getIsVisible() {
		return isVisible;
	}
	public HashMap<String, Boolean> getGrant() {
		return grant;
	}
	public boolean isCustomSecurity() {
		return customSecurity;
	}
	public boolean isKpi() {
		return isKpi;
	}
	public boolean isIndexable() {
		return indexable;
	}
	
	
	

}
