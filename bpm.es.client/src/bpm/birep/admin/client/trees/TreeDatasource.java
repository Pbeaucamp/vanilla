package bpm.birep.admin.client.trees;

import bpm.vanilla.platform.core.repository.DataSource;

public class TreeDatasource extends TreeParent{
	private DataSource ds ;
	
	public TreeDatasource(DataSource ds){
		super(ds.getName());
		this.ds = ds;
	}
	
	public DataSource getDataSource(){
		return ds;
	}

	@Override
	public String toString() {
		return ds.getName();
	}
	
	
}
