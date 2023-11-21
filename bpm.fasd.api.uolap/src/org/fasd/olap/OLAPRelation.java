package org.fasd.olap;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;

public class OLAPRelation extends OLAPElement {
	private static int counter = 0;
	public static void resetCounter(){
		counter =0;
	}
	
	private DataObjectItem leftObjectItem;
	private DataObjectItem rightObjectItem;
	
	private String operator = "=";
	private String leftItemId = "";
	private String rightItemId = "";
	private String leftObjectId = "";
	private String rightObjectId = "";
	
	public OLAPRelation(){
		super("");
		counter++;
		id = "l" + String.valueOf(counter);
	}
	
	
	public DataObjectItem getLeftObjectItem() {
		return leftObjectItem;
	}
	
	public void setLeftObjectItem(DataObjectItem leftObjectItem) {
		this.leftObjectItem = leftObjectItem;
//		update(this,leftObjectItem);
	}
	
	public DataObjectItem getRightObjectItem() {
		return rightObjectItem;
	}
	
	public void setRightObjectItem(DataObjectItem rightObjectItem) {
		this.rightObjectItem = rightObjectItem;
//		update(this, rightObjectItem);
	}
	
	public DataObject getRightObject(){
		return rightObjectItem.getParent();
	}
	
	public DataObject getLeftObject(){
		return leftObjectItem.getParent();
	}
	
	public void setId(String id) {
		this.id = id;
		int i = Integer.parseInt(id.substring(1));
		if (i > counter){
			counter = i + 1;
		}
	}

	public String getName(){
		if (super.getName().equals(""))
			return "[" + getLeftObject().getName() + "." +leftObjectItem.getName() + "] = [" + getRightObject().getName() + "." + rightObjectItem.getName() + "]";
		else return super.getName();
	}
	
	public String getFAXML(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("        <object-relation-item>\n");
		buf.append("             <id>" + getId() + "</id>\n");
		buf.append("             <name>" + getName() + "</name>\n");
		buf.append("             <left-object-id>");
		if (leftObjectItem != null)
			buf.append(getLeftObject().getId());
		buf.append("</left-object-id>\n");
		
		buf.append("             <right-object-id>");
		if (rightObjectItem != null)
			buf.append(getRightObject().getId());
		buf.append("</right-object-id>\n");
		
		buf.append("             <relation-formula>\n");
		
		buf.append("                 <left-object-column-id>");
		if (leftObjectItem != null)
			buf.append(leftObjectItem.getId());
		buf.append( "</left-object-column-id>\n");
		
		buf.append("                 <right-object-column-id>");
		if (rightObjectItem != null)
			buf.append(rightObjectItem.getId());
		buf.append("</right-object-column-id>\n");
		
		buf.append("                 <object-columns-relation>" + operator + "</object-columns-relation>\n");
		buf.append("             </relation-formula>\n");
		buf.append("        </object-relation-item>\n");
		
		return buf.toString();
	}


	public String getLeftItemId() {
		return leftItemId;
	}


	public void setLeftItemId(String leftItemId) {
		this.leftItemId = leftItemId;
	}


	public String getLeftObjectId() {
		return leftObjectId;
	}


	public void setLeftObjectId(String leftObjectId) {
		this.leftObjectId = leftObjectId;
	}


	public String getOperator() {
		return operator;
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}


	public String getRightItemId() {
		return rightItemId;
	}


	public void setRightItemId(String rightItemId) {
		this.rightItemId = rightItemId;
	}


	public String getRightObjectId() {
		return rightObjectId;
	}


	public void setRightObjectId(String rightObjectId) {
		this.rightObjectId = rightObjectId;
	}


	public boolean isUsingTable(DataObject tab) {
		if (leftObjectItem == null || rightObjectItem == null)
			return false;
		
		return (leftObjectItem.getParent() == tab) || (rightObjectItem.getParent() == tab); 
	}
	
	public boolean isReflexive(){
	
		if(leftObjectItem == null || rightObjectItem == null) {
			return false;
		}	
	
		return leftObjectItem.getParent() == rightObjectItem.getParent();
			
	}


	public boolean isUsingItem(DataObjectItem item) {
		return (leftObjectItem == item) || (rightObjectItem == item);
	}

}
