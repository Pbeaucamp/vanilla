package org.fasd.utils.trees;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSourceConnection;

public class TreeSchema extends TreeParent{

	
	private boolean childrenLoaded = false;
	private DataSourceConnection dataSourceConnection;
	
	protected DataSourceConnection getConnection(){
		return dataSourceConnection;
	}
	
	public TreeSchema(DataSourceConnection dataSourceConnection, String name) {
		super(name);
		this.dataSourceConnection = dataSourceConnection;
	}
	
	@Override
	public boolean hasChildren() {
		if (!childrenLoaded){
			return true;
		}
		return super.hasChildren();
	}
	
	@Override
	public Object[] getChildren() {
		if (!childrenLoaded){
			
			try{
				for(DataObject o : dataSourceConnection.getTables(getName())){
					TreeTable tt = new TreeTable(o);
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
