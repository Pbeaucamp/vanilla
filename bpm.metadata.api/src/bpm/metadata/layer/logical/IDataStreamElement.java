package bpm.metadata.layer.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.ISecurizable;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.misc.IGroup;
import bpm.metadata.query.Ordonable;
import bpm.vanilla.platform.core.beans.data.D4CTypes;

import com.thoughtworks.xstream.XStream;

public abstract class IDataStreamElement implements Ordonable, ISecurizable {

	public static final int DIMENSION_TYPE = 0;
	public static final int MEASURE_TYPE = 1;
	public static final int PROPERTY_TYPE = 2;
	public static final int UNDEFINED_TYPE = 3;
	public static final int COUNTRY_TYPE = 4;
	public static final int CITY_TYPE = 5;

	public static final String[] TYPE_NAME = new String[] { "Dimension", "Measure", "Property", "Undefined", "Country", "City" };
	public static final String[] MEASURE_DEFAULT_BEHAVIOR = new String[] { "SUM", "COUNT", "AVG", "MIN", "MAX" };

	public static final int BEHAVIOR_SUM = 0;
	public static final int BEHAVIOR_COUNT = 1;
	public static final int BEHAVIOR_AVG = 2;
	public static final int BEHAVIOR_MIN = 3;
	public static final int BEHAVIOR_MAX = 4;

	// protected int measureDefaultBehavior;

	protected String name;
	protected String description = "";
	protected String fontName = "Arial";
	protected String textColor = "000000";
	protected String backgroundColor = "FFFFFF";
	protected IDataStream table;
	protected IColumn origin;
	
	protected D4CTypes d4cTypes = new D4CTypes();

	protected String originName;
	protected SubType type = SubType.UNDEFINED;
	protected String parentDimension;

	private HashMap<String, Boolean> isVisible = new HashMap<String, Boolean>();
	private HashMap<String, Boolean> grant = new HashMap<String, Boolean>();

	/**
	 * If custom security is set to false, all columns will be available and visible for all groups
	 */
	private boolean customSecurity = false;

	private boolean isKpi = false;
	private boolean indexable = false;

	// private boolean isVisible = true;

	public boolean isKpi() {
		return isKpi;
	}

	public void setIsKpi(boolean value) {
		this.isKpi = value;
	}

	public void setIsKpi(String value) {
		this.isKpi = Boolean.parseBoolean(value);
	}

	// /**
	// * set the default behavior of used this column is flagged as Measure
	// * (in report, OLAP , ////)
	// */
	// public void setDefaultMeasureBehavior(int behavior){
	// this.measureDefaultBehavior = behavior;
	//
	// }

	// /**
	// * only used by Digester use the same metho with int parameter if you need it
	// * @param behavior
	// */
	// public void setDefaultMeasureBehavior(String behavior){
	// this.measureDefaultBehavior = Integer.parseInt(behavior);
	// }
	//
	// public int getDefaultMeasureBehavior(){
	// return this.measureDefaultBehavior;
	// }

	/**
	 * the type can be dimension or measure use the constants
	 * 
	 * @return
	 */
	public void setType(SubType i) {
		type = i;
	}

	/**
	 * the type can be dimension or measure use the constants
	 * 
	 * @return
	 */
	public SubType getType() {
		return type;
	}

	/**
	 * the type can be dimension or measure
	 * 
	 * don't use it, it just for digester
	 */
	public void setType(String i) {
		try {
			int t = Integer.parseInt(i);
			switch(t) {
				case DIMENSION_TYPE:
					type = SubType.DIMENSION;
					break;
				case MEASURE_TYPE:
					type = SubType.SUM;
					break;

				default:
					type = SubType.UNDEFINED;
					break;
			}
			return;
		} catch(Exception e) {
			// e.printStackTrace();
		}
		try {
			type = SubType.valueOf(i);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected HashMap<Locale, String> outputName = new HashMap<Locale, String>();

	// public String getOuputName(Locale l){
	// for(Locale k : outputName.keySet()){
	// if (k != null && k.getLanguage() != null && l != null && k.getLanguage().equals(l.getLanguage()) && outputName.get(k) != null){
	// return outputName.get(k);
	// }
	// }
	//
	// String s = getName();
	//
	// if (s.contains(".")){
	// return s.substring(s.indexOf('.') + 1);
	// }
	// return s;
	//
	// // return getOutputName();
	// }

	public String getOuputName(Locale l) {
		for(Locale k : outputName.keySet()) {
			if(k == l || k.getLanguage().equals(l.getLanguage())) {
				if(outputName.get(k) != null && !outputName.get(k).equals("")) {
					return outputName.get(k);
				}
				break;
			}
		}

		if(!outputName.keySet().isEmpty()) {
			for(Locale k : outputName.keySet()) {
				return outputName.get(k);
			}
		}

		String s = getName();

		if(s.contains(".")) {
			return s.substring(s.indexOf('.') + 1);
		}
		return s;
	}

	public String getOutputName() {
		return getOuputName();
	}

	public String getOuputName() {

		return getOuputName(Locale.getDefault());
	}

	public void setOutputName(Locale l, String value) {

		for(Locale k : outputName.keySet()) {
			if(l.getLanguage().equals(k.getLanguage())) {
				outputName.put(new Locale(l.getLanguage()), value);
				return;
			}
		}
		outputName.put(new Locale(l.getLanguage()), value);

	}

	public void setOutputName(String country, String language, String value) {
		setOutputName(new Locale(language), value);
	}

	public boolean isVisibleFor(String groupName) {
		Boolean b = isVisible.get(groupName);
		if(b != null) {
			return b;
		}
		if("none".equals(groupName)) {
			return true;
		}
		return !customSecurity;
	}

	public boolean isGrantedFor(String groupName) {

		if(groupName == null || groupName.equals("none")) {
			return true;
		}
		for(String s : grant.keySet()) {
			if(groupName.equals(s)) {
				return grant.get(s);
			}
		}

		return !customSecurity;
	}

	public void setCustomSecurity(String customSecurity) {
		this.customSecurity = Boolean.parseBoolean(customSecurity);
	}

	public void setCustomSecurity(boolean customSecurity) {
		this.customSecurity = customSecurity;
	}

	public void setVisible(String groupName, boolean value) {
		this.customSecurity = true;

		for(String k : isVisible.keySet()) {
			if(k.equals(groupName)) {
				this.isVisible.put(k, value);
				return;
			}
		}
		this.isVisible.put(groupName, value);
	}

	/**
	 * 
	 * This method is just left for old Metadata model. However it will not be used for new model
	 * 
	 * @param groupName
	 * @param value
	 */
	@Deprecated
	public void setVisible(String groupName, String value) {
		setVisible(groupName, Boolean.parseBoolean(value));
	}

	public void setGroupsVisible(String groups) {
		if(!groups.isEmpty()) {
			String[] arrayGroups = groups.split(",");
			for(String gr : arrayGroups) {
				setVisible(gr, true);
			}
		}
	}

	public void setGranted(String groupName, boolean value) {
		this.customSecurity = true;

		for(String k : grant.keySet()) {
			if(k.equals(groupName)) {
				this.grant.put(k, value);
				return;
			}
		}
		this.grant.put(groupName, value);
	}

	/**
	 * 
	 * This method is just left for old Metadata model. However it will not be used for new model
	 * 
	 * @param groupName
	 * @param value
	 */
	@Deprecated
	public void setGranted(String groupName, String value) {
		setGranted(groupName, Boolean.parseBoolean(value));
	}

	public void setGroupsGranted(String groups) {
		if(!groups.isEmpty()) {
			String[] arrayGroups = groups.split(",");
			for(String gr : arrayGroups) {
				setGranted(gr, true);
			}
		}
	}

	// public void setVisible(String isVisible) {
	// this.isVisible = Boolean.parseBoolean(isVisible);
	// }

	protected IDataStreamElement() {}

	protected IDataStreamElement(IColumn origin) {
		this.name = origin.getName();
		this.origin = origin;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setName(String name) {
		this.name = name.replace(" ", "");
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}

	/**
	 * return column name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * return the column Description
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @return the FontName for the output
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * 
	 * @return the textColor code (RGB = rrggbb)
	 */
	public String getTextColor() {
		if(textColor.length() < 6) {
			return "000000";
		}
		return textColor;
	}

	// /**
	// *
	// * @return the backGroundColor code (RGB = rrggbb)
	// */
	// public String getBackGroundColor(){
	// return backgroundColor;
	// }

	/**
	 * XXX:dunno
	 * 
	 * @return
	 */
	public int getModelDescriptor() {
		// TODO
		return 0;
	}

	/**
	 * 
	 * @return true if this column is a calcul
	 */
	public abstract boolean isCalculated();

	/**
	 * do nut use from client
	 * 
	 * @return
	 */
	public abstract IColumn getOrigin();

	public boolean isHiddenFor(IGroup group) {
		// TODO
		return false;
	}

	/**
	 * 
	 * @param table
	 *            container
	 */
	public void setDataStream(IDataStream table) {
		this.table = table;
	}

	public IDataStream getDataStream() {
		return table;
	}

	/**
	 * return all distincts values from the DataBase
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract List<String> getDistinctValues() throws Exception;

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("                <dataStreamElement>\n");
		buf.append("                     <name>" + name + "</name>\n");
		buf.append("                     <description>" + description + "</description>\n");
		buf.append("                     <fontName>" + fontName + "</fontName>\n");
		buf.append("                     <textColor>" + textColor + "</textColor>\n");
		buf.append("                     <backgroundColor>" + backgroundColor + "</backgroundColor>\n");
		buf.append("                     <type>" + type + "</type>\n");
		buf.append("                     <isKpi>" + isKpi + "</isKpi>\n");
		buf.append("                     <indexable>" + indexable + "</indexable>\n");
		if(type == SubType.DIMENSION) {
			buf.append("                     <parentDimension>" + parentDimension + "</parentDimension>\n");
		}

		buf.append("                     <customSecurity>" + customSecurity + "</customSecurity>\n");
		buf.append("					<d4cTypes><![CDATA[" + new XStream().toXML(d4cTypes) + "]]></d4cTypes>");
		// buf.append("                     <isVisible>" + isVisible + "</isVisible>\n");

		if(isVisible != null && !isVisible.isEmpty()) {
			buf.append("                     <visibleGroups>");
			boolean first = true;
			for(String s : isVisible.keySet()) {
				if(isVisible.get(s)) {
					if(first) {
						first = false;
					}
					else {
						buf.append(",");
					}
					buf.append(s);
				}
			}
			buf.append("</visibleGroups>\n");
		}

		if(grant != null && !grant.isEmpty()) {
			buf.append("                     <groupNames>");
			boolean first = true;
			for(String s : grant.keySet()) {
				if(grant.get(s)) {
					if(first) {
						first = false;
					}
					else {
						buf.append(",");
					}
					buf.append(s);
				}
			}
			buf.append("</groupNames>\n");
		}

		if(origin == null) {
			buf.append("                     <origin>" + originName + "</origin>\n");
		}
		else {
			// FIXME : what is this ?????????????????????????????
			// if (origin instanceof MemberOlap){
			// buf.append("                     <origin>" + ((MemberOlap)origin).getUniqueName() + "</origin>\n");
			// }
			// else if (origin instanceof MeasureOlap){
			// buf.append("                     <origin>" + ((MeasureOlap)origin).getUniqueName() + "</origin>\n");
			// }
			// else{
			buf.append("                     <origin>" + origin.getName() + "</origin>\n");
			// }
		}

		for(Locale l : outputName.keySet()) {
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}

		buf.append("                </dataStreamElement>\n");

		return buf.toString();
	}
	
	public void setD4CTypes(String types) {
		d4cTypes = (D4CTypes) new XStream().fromXML(types);
	}

	/**
	 * juste used by digester
	 * 
	 * @param originName
	 */
	public void setOriginName(String originName) {
		this.originName = originName;
	}

	/**
	 * just used by digester
	 * 
	 * @return
	 */
	public String getOriginName() {
		return originName;
	}

	public void setOrigin(IColumn column) {
		if(column != null) {
			this.originName = column.getName();
		}
		this.origin = column;

	}

	public abstract String getJavaClassName() throws Exception;

	public HashMap<String, Boolean> getVisibility() {
		return isVisible;
	}

	public HashMap<String, Boolean> getGrants() {
		return grant;
	}

	public boolean isIndexable() {
		return indexable;
	}

	public void setIndexable(boolean indexable) {
		this.indexable = indexable;
	}

	public void setIndexable(String indexable) {
		try {
			this.indexable = new Boolean(indexable);
		} catch(Exception e) {
			this.indexable = false;
		}
	}

	public String getParentDimension() {
		return parentDimension != null ? parentDimension : "";
	}

	public void setParentDimension(String parentDimension) {
		this.parentDimension = parentDimension;
	}

	public D4CTypes getD4cTypes() {
		return d4cTypes;
	}

	public void setD4cTypes(D4CTypes d4cTypes) {
		this.d4cTypes = d4cTypes;
	}

	public abstract List<String> getDistinctValues(HashMap<String, String> parentValues) throws Exception;

	public enum Type {
		UNDEFINED, DIMENSION, MEASURE, GEO, DATE, PROPERTY;

		public List<SubType> getSubtypes() {
			List<SubType> subtypes = new ArrayList<>();
			switch(this) {
				case DATE:
					subtypes.add(SubType.COMPLETE);
					subtypes.add(SubType.YEAR);
					subtypes.add(SubType.QUARTER);
					subtypes.add(SubType.MONTH);
					subtypes.add(SubType.WEEK);
					subtypes.add(SubType.DAY);
					return subtypes;
				case DIMENSION:
					subtypes.add(SubType.DIMENSION);
					return subtypes;
				case GEO:
					subtypes.add(SubType.CONTINENT);
					subtypes.add(SubType.COUNTRY);
					subtypes.add(SubType.REGION);
					subtypes.add(SubType.COMMUNE);
					subtypes.add(SubType.POSTALCODE);
					subtypes.add(SubType.ADRESSE);
					subtypes.add(SubType.ZONEID);
					subtypes.add(SubType.GEOLOCAL);
					subtypes.add(SubType.LATITUDE);
					subtypes.add(SubType.LONGITUDE);
					return subtypes;
				case MEASURE:
					subtypes.add(SubType.SUM);
					subtypes.add(SubType.AVG);
					subtypes.add(SubType.COUNT);
					subtypes.add(SubType.DISTINCT);
					subtypes.add(SubType.MAX);
					subtypes.add(SubType.MIN);
					return subtypes;
				case UNDEFINED:
					subtypes.add(SubType.UNDEFINED);
					return subtypes;
				case PROPERTY:
					subtypes.add(SubType.PROPERTY);
					return subtypes;

				default:
					return new ArrayList<>();
			}
		}
	}

	public enum SubType {
		CONTINENT, COUNTRY, COMMUNE, REGION, ADRESSE, POSTALCODE, ZONEID, GEOLOCAL, LATITUDE, LONGITUDE, COMPLETE, YEAR, MONTH, DAY, WEEK, QUARTER, SUM, COUNT, AVG, DISTINCT, MIN, MAX, DIMENSION, UNDEFINED, PROPERTY;

		public Type getParentType() {
			switch(this) {
				case COUNTRY:
				case COMMUNE:
				case ADRESSE:
				case POSTALCODE:
				case REGION:
				case CONTINENT:
				case ZONEID:
				case GEOLOCAL:
				case LATITUDE:
				case LONGITUDE:
					return Type.GEO;
				case COMPLETE:
				case DAY:
				case MONTH:
				case QUARTER:
				case YEAR:
				case WEEK:
					return Type.DATE;
				case AVG:
				case COUNT:
				case DISTINCT:
				case MAX:
				case MIN:
				case SUM:
					return Type.MEASURE;
				case DIMENSION:
					return Type.DIMENSION;
				case PROPERTY:
					return Type.PROPERTY;
			}
			return Type.UNDEFINED;
		}
	}
}
