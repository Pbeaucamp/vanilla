/*
 * Dynamic Jasper Playground: A GWT based web application created to show 
 * DynamicJasper features (http://sourceforge.net/projects/dynamicjasper)
 *
 * Copyright (C) 2007  FDV Solutions (http://www.fdvsolutions.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 *
 * License as published by the Free Software Foundation; either
 *
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *
 */

package bpm.fwr.api.beans.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.Constants.Locales;

@SuppressWarnings("serial")
public class Column implements Serializable {
	
	public enum TextAlignType {
		RIGHT, LEFT, CENTER
	};
	

	public enum Type {
		UNDEFINED, DIMENSION, MEASURE, GEO, DATE, PROPERTY;
		
		public List<SubType> getSubtypes() {
			List<SubType> subtypes = new ArrayList<SubType>();
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
					return new ArrayList<SubType>();
			}
		}
	}
	
	public enum SubType {
		CONTINENT, COUNTRY, COMMUNE, REGION, ADRESSE, POSTALCODE, ZONEID, GEOLOCAL,
		COMPLETE, YEAR, MONTH, DAY, WEEK, QUARTER,
		SUM, COUNT, AVG, DISTINCT, MIN, MAX,
		DIMENSION, UNDEFINED, PROPERTY;
		
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

	private String name;
	private String originName;
	private String customColumnName;
	
	private String javaClass;
//	private int type = IDataStreamElement.UNDEFINED_TYPE;
//	private int measureBehavior = IDataStreamElement.BEHAVIOR_SUM;
	//You can get the type by calling getParent on subType
	private SubType type;

	private HashMap<String, String> titles = new HashMap<String, String>();

	private int width = 85;
	private String format;
	private String textAlign = "left";
	private boolean isUnderline = false;
	private boolean isBold = false;
	private boolean isItalic = false;
	private boolean isSorted = false;
	private String sortType = "ASC";
	private boolean isCalculated = false;
	private String expression;
	private boolean isAgregate;
	private GroupAgregation groupAggregation;

	private DataSet datasetParent;
	
	// Metadata informations
	private int metadataId;
	private String metadataParent;
	private String datasourceTable;
	private String businessPackageParent;
	private String businessModelParent;
	private String businessTableParent;
	private List<String> involvedDatastreams = new ArrayList<String>();
	private String formula;
	
	private String description;

	public Column() {
		super();
	}

	public Column(String name, String originName, String businessTableParent, String javaClassName, String datasourceTable) {
		this.name = name;
		this.originName = originName;
		this.businessTableParent = businessTableParent;
		this.javaClass = javaClassName;
		this.datasourceTable = datasourceTable;
	}

	public String getName() {
		return name;
	}
	
	public String getOriginName() {
		return originName;
	}

	public String getBasicName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setOriginName(String originName) {
		this.originName = originName;
	}

	public String getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	public String getTitle() {
		String s = "";
		if (titles.keySet().contains(Locales.DEFAULT))
			return titles.get(Locales.DEFAULT);
		else {
			for (String _s : titles.keySet()) {
				s = titles.get(_s);
				break;
			}
			return s;
		}
	}

	public void setTitle(String title) {
		for (String s : this.titles.keySet()) {
			this.titles.put(s, this.titles.get(s));
		}
	}

	public boolean isNumeric() {
		// Class.forName() and instanceof are logically not supported...
		// this could be handled serverside
		if ((javaClass.equals("java.lang.Long")) || (javaClass.equals("java.lang.Integer")) 
				|| (javaClass.equals("java.lang.Float")) || (javaClass.equals("java.lang.Double")) 
				|| (javaClass.equals("java.lang.Number")) || (javaClass.equals("java.math.BigDecimal"))) {
			return true;
		}
		else
			return false;
	}

	public boolean isDate() {
		if (javaClass.equals("java.sql.Timestamp")) {
			return true;
		}
		else {
			return false;
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public HashMap<String, String> getTitles() {
		return titles;
	}

	public void setTitles(HashMap<String, String> title) {
		this.titles = title;
	}

	public void addLocaleTitle(String locale, String t) {
		titles.put(locale, t);
	}

	public boolean isCalculated() {
		return isCalculated;
	}

	public void setCalculated(boolean isCalculated) {
		this.isCalculated = isCalculated;
	}

	public void setCalculated(String isCalculated) {
		if (isCalculated.equalsIgnoreCase("true")) {
			this.isCalculated = true;
		}
		else {
			this.isCalculated = false;
		}
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return this.format;
	}

	public void setSorted(boolean isSorted) {
		this.isSorted = isSorted;
	}

	public void setSorted(String isSorted) {
		if (isSorted.equalsIgnoreCase("true")) {
			this.isSorted = true;
		}
		else {
			this.isSorted = false;
		}
	}

	public boolean isSorted() {
		return isSorted;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getSortType() {
		return sortType;
	}

	public String getTitle(String loc) {
		String ti = titles.get(loc);
		if (ti != null && !ti.equals("")) {
			return ti;
		}
		String na = "";
		if (name.contains(".")) {
			na = name.substring(name.indexOf(".") + 1);
		}
		else {
			na = name;
		}
		return na;
	}

	public String getDatasetRowExpr(String loc) {
		/*String ti = titles.get(loc);
		String na = "";
		if (ti == null || ti.equalsIgnoreCase("")) {
			ti = name;
			if (ti.contains("::")) {
				String ti2 = ti.substring(name.indexOf("::") + 2);
				if (ti2.contains("::")) {
					return ti2;
				}
				return ti;
			}
		}
		
		if (na.contains("::")) {
			if (na.substring(na.indexOf("::") + 2).contains("::")) {
				na = na.substring(na.indexOf("::") + 2);
			}
		}
		if (ti.contains("::")) {
			return ti;
		}
		return na + ti;*/
		return name;
	}

	/**
	 * You should use setTextAlign(TextAlignType textAlignType) for safe use
	 * However don't delete this method which is use by the digester
	 * 
	 * @param textAlign
	 */
	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public void setTextAlign(TextAlignType textAlignType) {
		if (textAlignType == TextAlignType.LEFT) {
			this.textAlign = "left";
		}
		else if (textAlignType == TextAlignType.RIGHT) {
			this.textAlign = "right";
		}
		else if (textAlignType == TextAlignType.CENTER) {
			this.textAlign = "center";
		}
	}

	public String getTextAlign() {
		return textAlign;
	}

	public void setUnderline(String isUnderline) {
		if (isUnderline.equalsIgnoreCase("true")) {
			this.isUnderline = true;
		}
		else {
			this.isUnderline = false;
		}
	}

	public void setUnderline(boolean isUnderline) {
		this.isUnderline = isUnderline;
	}

	public boolean isUnderline() {
		return isUnderline;
	}

	public void setBold(String isBold) {
		if (isBold.equalsIgnoreCase("true")) {
			this.isBold = true;
		}
		else {
			this.isBold = false;
		}
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
	}

	public boolean isBold() {
		return isBold;
	}

	public void setItalic(String isItalic) {
		if (isItalic.equalsIgnoreCase("true")) {
			this.isItalic = true;
		}
		else {
			this.isItalic = false;
		}
	}

	public void setItalic(boolean isItalic) {
		this.isItalic = isItalic;
	}

	public boolean isItalic() {
		return isItalic;
	}

//	public void setMeasureBehavior(String measureBehavior) {
//		this.measureBehavior = Integer.parseInt(measureBehavior);
//	}
//
//	public void setMeasureBehavior(int measureBehavior) {
//		this.measureBehavior = measureBehavior;
//	}
//
//	public int getMeasureBehavior() {
//		return measureBehavior;
//	}
	
	public SubType getType() {
		return type;
	}
	
	public void setType(SubType type) {
		this.type = type;
	}

	public void setCustomColumnName(String customColumnName, boolean addStyle) {
		if(addStyle){
			String text = customColumnName;
			if(customColumnName.contains("<span style=\"")){
				int index = customColumnName.indexOf("<span style=\"") + "style=\"".length();
				
				StringBuffer custom = new StringBuffer(customColumnName);
				custom.insert(index, "width: 100%; float: left; ");
				
				text = custom.toString();
			}
			this.customColumnName = text;
		}
		else {
			this.customColumnName = customColumnName;
		}
	}

	public String getCustomColumnName() {
		if (customColumnName == null || customColumnName.equals("") || customColumnName.equalsIgnoreCase("null") || customColumnName.equalsIgnoreCase("null")) {
			return null;
		}
		return customColumnName;
	}

	public void setGroupAggregation(GroupAgregation groupAggregation) {
		this.groupAggregation = groupAggregation;
	}

	public GroupAgregation getGroupAggregation() {
		return groupAggregation;
	}

	public void setAgregate(boolean isAgregate) {
		this.isAgregate = isAgregate;
	}

	public boolean isAgregate() {
		return isAgregate;
	}

	public int getMetadataId() {
		return metadataId;
	}

	public void setMetadataId(int metadataId) {
		this.metadataId = metadataId;
	}

	public String getMetadataParent() {
		return metadataParent;
	}

	public void setMetadataParent(String metadataParent) {
		this.metadataParent = metadataParent;
	}
	
	public String getDatasourceTable() {
		return datasourceTable;
	}
	
	public void setDatasourceTable(String datasourceTable) {
		this.datasourceTable = datasourceTable;
	}

	public String getBusinessPackageParent() {
		return businessPackageParent;
	}

	public void setBusinessPackageParent(String businessPackageParent) {
		this.businessPackageParent = businessPackageParent;
	}

	public String getBusinessModelParent() {
		return businessModelParent;
	}

	public void setBusinessModelParent(String businessModelParent) {
		this.businessModelParent = businessModelParent;
	}

	public String getBusinessTableParent() {
		return businessTableParent;
	}

	public void setBusinessTableParent(String businessTableParent) {
		this.businessTableParent = businessTableParent;
	}

//	public void setType(int type) {
//		this.type = type;
//	}
//
//	public int getType() {
//		return type;
//	}

	public void setDatasetParent(DataSet parentTable) {
		this.datasetParent = parentTable;
	}

	public DataSet getDatasetParent() {
		return datasetParent;
	}

	public Column getClone() {
		Column clone = new Column();
		clone.setName(name);
		clone.setOriginName(originName);
		clone.setDatasourceTable(datasourceTable);
		clone.setJavaClass(javaClass);
		clone.setTitles(titles);
		clone.setBusinessTableParent(businessTableParent);
		clone.setWidth(width);
		clone.setCalculated(isCalculated);
		clone.setExpression(expression);
		clone.setFormat(format);
		clone.setSorted(isSorted);
		clone.setSortType(sortType);
		clone.setUnderline(isUnderline);
		clone.setBold(isBold);
		clone.setItalic(isItalic);
		clone.setTextAlign(textAlign);
		clone.setType(type);
//		clone.setType(type);
//		clone.setMeasureBehavior(measureBehavior);
		clone.setCustomColumnName(customColumnName, false);
		clone.setGroupAggregation(groupAggregation);
		clone.setAgregate(isAgregate);
		clone.setDatasetParent(datasetParent);
		clone.setMetadataId(metadataId);
		clone.setMetadataParent(metadataParent);
		clone.setBusinessPackageParent(businessPackageParent);
		clone.setBusinessModelParent(businessModelParent);
		clone.setInvolvedDatastreams(involvedDatastreams);
		clone.setFormula(formula);
		clone.setDescription(description);
		return clone;
	}

	public List<String> getInvolvedDatastreams() {
		return involvedDatastreams ;
	}

	public void setInvolvedDatastreams(List<String> involvedDatastreams) {
		this.involvedDatastreams = involvedDatastreams;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getFormula() {
		return formula;
	}
	
	public String getAggregation() {
		return formula != null && formula.contains("(") ? formula.substring(0, formula.indexOf("(")) : null;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		if(description == null) {
			return name;
		}
		return description;
	}

	public boolean isDimension() {
		return getType().getParentType() == Type.DIMENSION || getType().getParentType() == Type.GEO || getType().getParentType() == Type.DATE;
	}

	public boolean isMeasure() {
		return getType().getParentType() == Type.MEASURE;
	}

	
}
