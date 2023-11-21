package bpm.metadata.birt.oda.ui.trees;

import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
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
		if (table != null && table instanceof AbstractBusinessTable){
			return ((AbstractBusinessTable)table).getOuputName();
		}
		return table.getName();
	}
	
	public IBusinessTable getTable(){
		return table;
	}

	private void buildChild(String groupName){
		
		
		if (table instanceof UnitedOlapBusinessTable){
			IDataSource ds = ((UnitedOlapBusinessTable)table).getDataSources().get(0);
			
			
			
			for(IDataStreamElement t : ((UnitedOlapBusinessTable) table).getColumns()){
				addChild(new TreeDataStreamElement(t));
			}
		}
		else{
			
			for(IBusinessTable t : table.getChilds(groupName)){
				if (t.isGrantedFor(groupName)){
					addChild(new TreeBusinessTable(t, groupName));
				}
				
			}
			for(IDataStreamElement e : table.getColumnsOrdered(groupName)){
				addChild(new TreeDataStreamElement(e));
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
