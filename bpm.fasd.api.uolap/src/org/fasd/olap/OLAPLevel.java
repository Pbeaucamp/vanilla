package org.fasd.olap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;

public class OLAPLevel extends OLAPElement {
	public static final String GEOLOCALIZABLE_PROPERTY_NAME = "geolocalizableProperty";
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	
	private boolean uniquemb = false;
	private OLAPHierarchy parent;
	private String desc = "";
	private boolean isReal = true;
	private int nb = -1;
	
	private String columnName = "";
	
	private String itemId = "";
	private String columnLabelId = "";
	private DataObjectItem item, label, itDesc, sort;
	
//	private DataObjectItem parentItem;
//	private String parentColumnId = "";
	
	private DataObject closureTable;
	private DataObjectItem closureParentCol, closureChildCol;
	private String closureTableId = "", closureParentId = "",closureChildId = "";
	private String nullParentValue = "";
	
	private String tableId = "";
	
	private String dialect = "";
	private String ordinalColumn = "";
	private DataObjectItem orderItem;
	private String orderItemId;
	
	
	private String sql = "";
	
	private String caption = "";
	private int approxRowCount = 0;
	private String type = "";
	private String levelType = "";
//	private String hideMemberIf = "";
	private String formatter = "";
	private String captionColumn = "";
	
	private HashMap<String, String> keyExpressions = new HashMap<String, String>();
	
	private boolean isOneColumnDate;
	private String dateColumnType;
	private String dateColumnPart;
	private String dateColumnPattern;
	private String dateColumnOrderPart;
	
	public boolean isOneColumnDate() {
		return isOneColumnDate;
	}

	public void setOneColumnDate(boolean isOneColumnDate) {
		this.isOneColumnDate = isOneColumnDate;
	}
	
	public void setOneColumnDate(String isOneColumnDate) {
		this.isOneColumnDate = Boolean.parseBoolean(isOneColumnDate);
	}
	
	public String getDateColumnType() {
		return dateColumnType;
	}

	public void setDateColumnType(String dateColumnType) {
		this.dateColumnType = dateColumnType;
	}

	public String getDateColumnPart() {
		return dateColumnPart;
	}

	public void setDateColumnPart(String dateColumnPart) {
		this.dateColumnPart = dateColumnPart;
	}

	/**
	 * @return the columnLabelId
	 */
	public String getColumnLabelId() {
		if(columnLabelId == null && label != null) {
			return label.getId();
		}
		return columnLabelId;
	}

	/**
	 * @param columnLabelId the columnLabelId to set
	 */
	public void setColumnLabelId(String columnLabelId) {
		this.columnLabelId = columnLabelId;
	}

	private List<Property> properties = new ArrayList<Property>();
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}
	
	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getNullParentValue() {
		return nullParentValue;
	}

	public void setNullParentValue(String nullParentValue) {
		this.nullParentValue = nullParentValue;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
//		update(this, desc);
	}

	public boolean isReal() {
		return isReal;
	}

	public void setReal(boolean isReal) {
		this.isReal = isReal;
//		update(this, isReal);
	}

	public int getNb() {
		return nb;
	}

	public void setNb(int nb) {
		this.nb = nb;
//		update(this, nb);
	}
	
	public void setNb(String nb){
		this.nb = Integer.parseInt(nb);
	}

	public OLAPHierarchy getParent() {
		return parent;
	}

	public void setParent(OLAPHierarchy parent) {
		this.parent = parent;
	}

	public OLAPLevel(String name) {
		super(name);		
		counter++;
		id = "o" + String.valueOf(counter);
	}
	
	public OLAPLevel() {
		super("");
		counter++;
		id = "o" + String.valueOf(counter);
	}



	public boolean isUniquemb() {
		return uniquemb;
	}

	public void setUniquemb(boolean uniquemb) {
		this.uniquemb = uniquemb;
//		update(this, uniquemb);
	}
	
	public void setUniquemb(String s){
		this.uniquemb = Boolean.parseBoolean(s);
//		update(this, uniquemb);
	}

	public String getFAXML() {
		String buf = "            <Level>\n";
		buf += "                <id>" + super.getId() + "</id>\n";
		buf += "                <name>" + getName() + "</name>\n";
		buf += "                <caption>" + caption + "</caption>\n";
		
		if (label != null){
			buf += "                <captionColumn>" + label.getId() + "</captionColumn>\n";
		}
		else if (captionColumn != null){
			buf += "                <captionColumn>" + captionColumn + "</captionColumn>\n";
		}
		buf += "                <formatter>" + formatter + "</formatter>\n";
		buf += "                <type>" + type + "</type>\n";
		buf += "                <levelType>" + levelType + "</levelType>\n";
//		buf += "                <hideMemberIf>" + hideMemberIf + "</hideMemberIf>\n";
		
		if (orderItem != null){
			buf += "                <ordinalItemId>" + orderItem.getId() + "</ordinalItemId>\n";
		}
		
		buf += "                <ordinalColumn>" + ordinalColumn + "</ordinalColumn>\n";
		buf += "                <level-nb>" + nb + "</level-nb>\n";
		
		if (approxRowCount > 0)
			buf += "                <approxRowCount>" + approxRowCount + "</approxRowCount>\n";
		buf += "                <hierarchy-parent-id>";
		if (parent != null)
			buf += parent.getId();
		buf += "</hierarchy-parent-id>\n";
		
		buf += "                <description>" + desc + "</description>\n";
		buf += "                <uniqueMembers>" + uniquemb + "</uniqueMembers>\n";
		buf += "                <dataobjectitem-code-id>";

		if (item != null)
			buf += item.getId();
		buf += "</dataobjectitem-code-id>\n";

//			buf += "                <parent-dataobjectitem-id>";
//			if (parentItem != null)
//				buf += parentItem.getId();
//			buf += "</parent-dataobjectitem-id>\n";
			
			buf += "                <closure-dataobject-id>";
			if (closureTable != null)
				buf += closureTable.getId() ;
			buf += "</closure-dataobject-id>\n";
			
			buf += "                <closure-parent-dataobjectitem-id>";
			if (closureParentCol != null)
				buf += closureParentCol.getId();
			buf += "</closure-parent-dataobjectitem-id>\n";
			
			buf += "                <closure-child-dataobjectitem-id>";
				if (closureChildCol != null)
					buf += closureChildCol.getId();
			buf += "</closure-child-dataobjectitem-id>\n";
			
			buf += "                <null-parent-value>" + nullParentValue + "</null-parent-value>\n";

		
			for(Property p : properties)
				if(p != null) {
					buf += p.getFAXML();
				}
		
			if(isOneColumnDate) {
				buf += "				<isOneColumnDate>" + isOneColumnDate + "</isOneColumnDate>\n";
				buf += "				<dateColumnType>" + dateColumnType + "</dateColumnType>\n";
				buf += "				<dateColumnPart>" + dateColumnPart + "</dateColumnPart>\n";
				if(dateColumnPattern != null && !dateColumnPattern.equals("")) {
					buf += "				<dateColumnPattern>" + dateColumnPattern + "</dateColumnPattern>\n";
				}
				if(dateColumnOrderPart != null) {
					buf += "				<dateColumnOrderPart>" + dateColumnOrderPart + "</dateColumnOrderPart>\n";
				}
			}
			
		buf += "            </Level>\n";
		
		return buf;
	}
	
	public String getXML(){
		
		
		String buf = "                <Level name=\"" + getName() + "\"" ;;
		
		if (!caption.trim().equals("")){
			buf += " caption=\"" + caption + "\"";
		}

		if (!captionColumn.trim().equals("")){
			buf += " captionColumn=\"" + captionColumn+ "\"" ;
		}
		else if (label != null){
			buf += " captionColumn=\"" + label.getOrigin()+ "\"";
		}
		if (!formatter.trim().equals("")){
			buf += " formatter=\"" + formatter + "\"";
		}
		if (!ordinalColumn.trim().equals("")){
			buf += " ordinalColumn=\"" + ordinalColumn + "\"";
		}
		else{
			if (orderItem != null){
				buf += " ordinalColumn=\"" + orderItem.getOrigin() + "\"";
			}
		}
		if (!type.trim().equals("")){
			buf += " type=\"" + type + "\"";
		}
		if (!levelType.trim().equals("")){
			buf += " levelType=\"" + levelType + "\"";
		}
//		if (!hideMemberIf.trim().equals("")){
//			buf += " hideMemberIf=\"" + hideMemberIf + "\"";
//		}
		
		if (item != null){
			buf += " table=\"" + item.getParent().getPhysicalName().substring(item.getParent().getPhysicalName().indexOf(".") + 1);
			if (item.getParent().getDataSource().getDriver().getSchemaName() != null && !item.getParent().getDataSource().getDriver().getSchemaName().trim().equals("")){
				buf += "\" schema=\"" + item.getParent().getDataSource().getDriver().getSchemaName();
			}
//			buf += ">\n";
			//buf += "\" column=\"" + item.getOrigin() + "\" uniqueMembers=\"" + uniquemb;
			if (approxRowCount > 0)
				buf +="\" approxRowCount=\"" + approxRowCount;
			
			if (item.getType().equals("physical"))
				buf += "\" column=\""+ item.getOrigin() +"\" uniqueMembers=\"" + uniquemb;
			if (item.getType().equals("calculated")){
				buf += "\" uniqueMembers=\"" + uniquemb + "\">\n";
				buf += "                    <KeyExpression>\n";
				buf += "                        <SQL dialect=\"generic\">" + item.getOrigin();
				buf += "</SQL>\n";
				buf += "                    </KeyExpression>\n";
				
				for(Property p : properties)
					buf += p.getXML();
				
				buf += "                </Level>\n";
			}
		}
		else{
			buf += parent.getTableName();
			
			if (!columnName.equals(""))
				buf += " table=\"" + parent.getTableName() +"\" column=\""+ columnName +"\" uniqueMembers=\"" + uniquemb;
			else{
				buf +="\" uniqueMembers=\""+uniquemb+"\">\n";
				buf += "                    <KeyExpression>\n";
				for(String s : keyExpressions.keySet()){
					buf += "                        <SQL dialect=\"" + s + "\">" + keyExpressions.get(s);
					buf += "</SQL>\n";
					
				}
				buf += "                    </KeyExpression>\n";
				
				for(Property p : properties)
					buf += p.getXML();
				
				buf += "                </Level>\n";
			}
			
		}
			
		
		if (isClosureNeeded()){
//			buf +=  "\" parentColumn=\"" ;
//			if (parentItem != null)
//				buf += parentItem.getOrigin();
			
			buf += "\" nullParentValue =\"" + nullParentValue +"\">\n";
			
			if (closureTable != null){
				buf += "                    <Closure parentColumn=\"";
				
				if (closureParentCol != null)
					buf +=  closureParentCol.getOrigin();
				
				buf += "\" childColumn=\"";
				if (closureChildCol != null)
					buf += closureChildCol.getOrigin();
				
				buf += "\">\n";
				buf += "                        <Table name=\"";
				buf += closureTable.getPhysicalName();
				
				buf += "\"/>\n";
				buf += "                     </Closure>\n";
				
				for(Property p : properties)
					buf += p.getXML();

			}
			buf+= "                   </Level>\n";
		}
		else{
			if (item != null && item.getType().equals("physical")){
				if (properties.size() < 1)
					buf += "\"/>\n";
				else{
					buf += "\">\n";
					for(Property p : properties)
						buf += p.getXML();
					buf+= "                   </Level>\n";
				}
			}
				 
		}
		
		return buf;
	}

	public DataObjectItem getItDesc() {
		return itDesc;
	}

	public void setItDesc(DataObjectItem itDesc) {
		this.itDesc = itDesc;
//		update(this, itDesc);
	}

	public DataObjectItem getItem() {
		return item;
	}

	public void setItem(DataObjectItem item) {
		this.item = item;
		if (item != null){
			itemId = item.getId();
		}
//		update(this, item);
		
//		if (parent != null && !isClosureNeeded()){
//			setParentItem(null);
//			setClosureChildCol(null);
//			setClosureParentCol(null);
//			setClosureTable(null);
//			//setNullParentValue("");
//		}
	}

	public DataObjectItem getLabel() {
		return label;
	}

	public void setLabel(DataObjectItem label) {
		this.label = label;
//		update(this, label.getId());
	}
	
	public Property getGeolocalizableProperty(){
		for(Property prop : properties){
			if(prop.getName().equals(GEOLOCALIZABLE_PROPERTY_NAME)){
				return prop;
			}
		}
		return null;
	}

	public void setGeolocalizableProperty(Property geolocalizableProperty){
		Property prop = getGeolocalizableProperty();
		if(prop != null){
			this.removeProperty(prop);
		}
		
		addProperty(geolocalizableProperty);
	}

	public DataObjectItem getSort() {
		return sort;
	}

	public void setSort(DataObjectItem sort) {
		this.sort = sort;
//		update(this, sort);
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public boolean isClosureNeeded(){
		try{
			for(OLAPRelation r : getParent().getParent().getParent().getParent().getRelations()){
				if (r.isUsingItem(item) && r.isReflexive()){
					return true;
				}
			}
			return !closureTableId.trim().equals("");
		}
		catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}
		
	}

	
//	public DataObjectItem getParentItem() {
//		return parentItem;
//	}

	public DataObjectItem getClosureChildCol() {
		return closureChildCol;
	}

	public void setClosureChildCol(DataObjectItem closureChildCol) {
		this.closureChildCol = closureChildCol;
		if (closureChildCol != null)
			closureChildId = closureChildCol.getId();
		else 
			closureChildId = "";
	}

	public DataObjectItem getClosureParentCol() {
		return closureParentCol;
	}

	public void setClosureParentCol(DataObjectItem closureParentCol) {
		this.closureParentCol = closureParentCol;
		if (closureParentCol != null)
			closureParentId = closureParentCol.getId();
		else
			closureParentId = "";
	}

	public DataObject getClosureTable() {
		return closureTable;
	}

	public void setClosureTable(DataObject closureTable) {
		this.closureTable = closureTable;
		if (closureTable != null) 
			closureTableId = closureTable.getId();
		else 
			closureTableId = "";
	}
//
//	public void setParentItem(DataObjectItem parentColumn) {
//		this.parentItem = parentColumn;
//		if (parentColumn == null){
//			closureChildCol = null;
//			closureParentCol = null;
//			closureTable = null;
//			//uniquemb = false;
//		}
//		else{
//			uniquemb = true;
//		}
//			//parentColumnId = parentColumn.getId();
//	//	else
//		//	parentColumnId = "";
//	}

//	public String getParentColumnId() {
//		return parentColumnId;
//	}
//
//	public void setParentColumnId(String parentColumnId) {
//		this.parentColumnId = parentColumnId;
//	}

	public String getClosureChildId() {
		return closureChildId;
	}

	public void setClosureChildId(String closureChildId) {
		this.closureChildId = closureChildId;
	}

	public String getClosureParentId() {
		return closureParentId;
	}

	public void setClosureParentId(String closureParentColId) {
		this.closureParentId = closureParentColId;
	}

	public String getClosureTableId() {
		return closureTableId;
	}

	public void setClosureTableId(String closureTableId) {
		this.closureTableId = closureTableId;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * just used to parse MOndrian file
	 * @return
	 */
	public String getDialect() {
		return dialect;
	}

	/**
	 * just used to parse MOndrian file
	 * @return
	 */
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	/**
	 * just used to parse MOndrian file
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * just used to parse MOndrian file
	 * @return
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public void addKeyExpression(String dialect, String sql){
		keyExpressions.put(dialect, sql);
		if (dialect.equals("generic"))
			this.setSql(sql);
	}

	public HashMap<String, String> getKeyExpressions() {
		return keyExpressions;
	}
	
	public List<Property> getProperties(){
		return properties;
	}
	
	public void addProperty(Property p){
		if (p!=null && !properties.contains(p))
			properties.add(p);
	}
	
	public void removeProperty(Property p){
		properties.remove(p);
	}

	public int getApproxRowCount() {
		return approxRowCount;
	}

	public void setApproxRowCount(int approxRowCount) {
		this.approxRowCount = approxRowCount;
	}
	public void setApproxRowCount(String approxRowCount) {
		this.approxRowCount = Integer.parseInt(approxRowCount);
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getOrdinalColumn() {
		return ordinalColumn;
	}

	public void setOrdinalColumn(String ordinalColumn) {
		this.ordinalColumn = ordinalColumn;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLevelType() {
		return levelType;
	}

	public void setLevelType(String levelType) {
		this.levelType = levelType;
	}

//	public String getHideMemberIf() {
//		return hideMemberIf;
//	}
//
//	public void setHideMemberIf(String hideMemberIf) {
//		this.hideMemberIf = hideMemberIf;
//	}

	public String getCaptionColumn() {
		return captionColumn;
	}

	public void setCaptionColumn(String captionColumn) {
		this.captionColumn = captionColumn;
	}

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}

	public DataObjectItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(DataObjectItem orderItem) {
		this.orderItem = orderItem;
	}

	public String getOrderItemId() {
		if(orderItemId == null && orderItem != null) {
			return orderItem.getId();
		}
		return orderItemId;
	}

	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}

	public void setDateColumnPattern(String dateColumnPattern) {
		this.dateColumnPattern = dateColumnPattern;
	}

	public String getDateColumnPattern() {
		return dateColumnPattern;
	}

	public void setDateColumnOrderPart(String dateColumnOrderPart) {
		this.dateColumnOrderPart = dateColumnOrderPart;
	}

	public String getDateColumnOrderPart() {
		return dateColumnOrderPart;
	}
	
}
