package bpm.es.sessionmanager.gef.model;

import java.util.ArrayList;
import java.util.List;

import bpm.es.sessionmanager.api.server.VanillaServer;

public class ServerModel {
	
	private VanillaServer server;
	
	private List<FieldModel> fields = new ArrayList<FieldModel>();
	
	private MapModel parent;
	
	private int x, y;
	
	public ServerModel(VanillaServer server){
		this.server = server;
		
//		List<DBColumn> columns = table.getColumns();
//		
//		if (columns.isEmpty()) {
//			//might not have been initialized
//			SQLProvider provider = (SQLProvider) Activator.getDefault().getCurrentProject().getSecurityProvider();
//			try {
//				columns = provider.getColumns(table);
//			} catch (Exception e) {
//				Log.error("internal error, could not retrieve columns for table " + table.getName(), e);
//			}
//		}
//		
//		for(DBColumn col : columns) {
//			FieldModel f = new FieldModel(mapping, col);
//			f.setParent(this);
//			fields.add(f);
//		}
	}

//	
//	
	public String getServerHost() {
		return server.getHost();
	}
	
	public String getName(){
		return server.getName();
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public List<FieldModel> getFields() {
		return fields;
	}

	public MapModel getParent() {
		return parent;
	}
	
	public void setParent(MapModel parent){
		this.parent = parent;
	}
	
}
