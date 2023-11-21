package org.fasd.utils.trees;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;

public class TreeTable extends TreeParent {
	private boolean childrenLoaded = false;
	DataObject tab;
	
	public void setLoaded(){
		
		this.childrenLoaded = true;
	}
	
	@Override
	public boolean hasChildren() {
		if (!childrenLoaded){
			return true;
		}
		return super.hasChildren();
	}
	
	public TreeTable(DataObject tab) {
		super(tab.getName());
		this.tab = tab;
	}
	public DataObject getTable() {
		return tab;
	}
	
	@Override
	public Object[] getChildren() {
		if (!childrenLoaded){

				try{
					
					for(DataObjectItem o : tab.getColumns()){
						TreeColumn tt = new TreeColumn(o);
						addChild(tt);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			
			
			childrenLoaded = true;
		}
		return super.getChildren();
	}
	
}
