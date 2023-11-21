package org.fasd.olap;

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;



public class Joint {
	private String leftKey = "";
	private String rightKey = "";
	private String rightAlias = "";
	private String leftAlias ="";
	
	private String tableName ="";
	
	private DataObjectItem leftCol, rightCol;
	private DataObject table = new DataObject();
	public static List<DataObject> list = new ArrayList<DataObject>();
	public static List<OLAPRelation> rels = new ArrayList<OLAPRelation>();
	
	
	private List<Joint> childs = new ArrayList<Joint> ();
	public Joint(){
		System.out.println(">>create Join");
	}
	public String getLeftAlias() {
		return leftAlias;
	}
	public void setLeftAlias(String leftAlias) {
		this.leftAlias = leftAlias;
	}
	public void addChild(Joint j){
		childs.add(j);
		System.out.println("Joint >>addChild");
	}
	public String getLeftKey() {
		return leftKey;
	}
	public void setLeftKey(String leftKey) {
		this.leftKey = leftKey;
	}
	public String getRightAlias() {
		return rightAlias;
	}
	public void setRightAlias(String rightAlias) {
		this.rightAlias = rightAlias;
	}
	public List<Joint> getChilds(){
		return childs;
	}
	public String getRightKey() {
		return rightKey;
	}
	public void setRightKey(String rightKey) {
		this.rightKey = rightKey;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void buildTables(){
		if (!tableName.equals("")){
			
			table.setName(tableName);
			table.setDataObjectType("dimension");
			list.add(table);
		}
		
		else{
			//left
			leftCol = new DataObjectItem();
			leftCol.setOrigin(leftKey);
			leftCol.setName(leftKey);
			childs.get(0).table.addDataObjectItem(leftCol);
			childs.get(0).buildTables();
			
			//right
			rightCol = new DataObjectItem();
			rightCol.setOrigin(rightKey);
			rightCol.setName(rightKey);
			childs.get(1).table.addDataObjectItem(rightCol);
			childs.get(1).buildTables();
			
			if (!rightAlias.equals("")){
				DataObject tr = findTable(rightAlias);
				if (tr == null){
					tr = new DataObject();
					tr.setDataObjectType("dimension");
					tr.setName(rightAlias);
					list.add(tr);
				}
				tr.addDataObjectItem(rightCol);
			}
			if (!leftAlias.equals("")){
				DataObject tl = findTable(leftAlias);
				if (tl == null){
					tl = new DataObject();
					tl.setDataObjectType("dimension");
					tl.setName(leftAlias);
					list.add(tl);
				}
				tl.addDataObjectItem(leftCol);
			}
		}
	}
	
	public void buildAllRelations(){
		buildRelation();
		for(Joint j : childs)
			j.buildRelation();
	}
	
	private void buildRelation(){
		if (!tableName.equals(""))
			return;
		
		if (leftAlias.equals("") && rightAlias.equals("")){
			OLAPRelation r = new OLAPRelation();
			r.setLeftObjectItem(findItem(childs.get(0).table, leftKey));
			r.setRightObjectItem(findItem(childs.get(1).table, rightKey));
			rels.add(r);
		}
		
		else{
			if(leftAlias.equals("")){
				OLAPRelation r = new OLAPRelation();
				r.setLeftObjectItem(findItem(childs.get(0).table, leftKey));
				r.setRightObjectItem(findItem(findTable(rightAlias), rightKey));
				rels.add(r);
			}
			if(rightAlias.equals("")){
				OLAPRelation r = new OLAPRelation();
				r.setLeftObjectItem(findItem(childs.get(1).table, rightKey));
				r.setRightObjectItem(findItem(findTable(leftAlias), leftKey));
				rels.add(r);
			}
			
		}
		
	}
	
	public DataObject findTable(String name){
		for(DataObject o : list){
			if (o.getName().equals(name))
				return o;
		}
		return null;
	}
	
		
	private DataObjectItem findItem(DataObject o,String name){
		for(DataObjectItem i : o.getColumns()){
			if (i.getName().equals(name)){
				return i;
			}
		}
	
		return null;
	}
}
