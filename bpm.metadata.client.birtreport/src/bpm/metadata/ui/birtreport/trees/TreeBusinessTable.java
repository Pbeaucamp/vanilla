package bpm.metadata.ui.birtreport.trees;

import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;

public class TreeBusinessTable extends TreeParent {

	private IBusinessTable table;
	private boolean getChild = true;
	
	public TreeBusinessTable(IBusinessTable table, String groupName) {
		super(table.getName());
		this.table= table;
		buildChild(groupName);
	}

	public TreeBusinessTable(IBusinessTable table, boolean getChild, String groupName) {
		super(table.getName());
		this.table= table;
		this.getChild = getChild;
		if (getChild){
			buildChild(groupName);
		}
	}
	
	@Override
	public String toString() {
		return table.getName();
	}
	
	public IBusinessTable getTable(){
		return table;
	}

	private void buildChild(String groupName){
		for(IDataStreamElement e : table.getColumnsOrdered(groupName)){
			addChild(new TreeDataStreamElement(e));
		}
		
		for(IBusinessTable t : table.getChilds(groupName)){
			if (groupName.equals("none") || t.isGrantedFor(groupName)){
				addChild(new TreeBusinessTable(t, groupName));
			}
			
		}
		
	}

	public void refresh(String groupName) {
		this.removeAll();
		
		if (getChild){
			buildChild(groupName);
		}
		
		
	}
}
