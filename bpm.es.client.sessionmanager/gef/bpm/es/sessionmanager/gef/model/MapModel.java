package bpm.es.sessionmanager.gef.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import bpm.es.sessionmanager.api.UserWrapper;
import bpm.es.sessionmanager.api.server.VanillaServer;

public class MapModel {
	
	//our
	private VanillaServer server;
	private List<UserWrapper> users; 
	
	//gef
	private ServerModel model;
	private List<UserModel> childs = new ArrayList<UserModel>();
	
	
	public MapModel(VanillaServer server, List<UserWrapper> users){
		this.server = server;
		
		model = new ServerModel(server);
		model.setParent(this);
		
//		if (leftMapping.getTable() != null) {
//			childs.add(new UserModel(leftMapping, leftMapping.getTable()));
//			childs.get(0).setX(50);
//			childs.get(0).setY(50);
//			childs.get(0).setParent(this);
//			
//			if (rightMapping.getTable() != null) {
//				childs.add(new UserModel(rightMapping, rightMapping.getTable()));
//				childs.get(1).setX(150);
//				childs.get(1).setY(50);
//				childs.get(1).setParent(this);
//			}
//		}
		
//		HashMap<String, DBColumn> maps = (HashMap<String, DBColumn>) leftMapping.getMappings().clone();
//		
//		for (String temp : leftMapping.getMappings().keySet()) {
//			maps.put(temp, leftMapping.getMappings().get(temp));
//		}
	
//		for (String colKey : maps.keySet()) {
//			FieldModel targetRight = findModel(childs.get(1), colKey);
//			FieldModel sourceLeft = findModel(childs.get(0), maps.get(colKey).getName());
//			
//			try {
//				Link lnk = new Link(sourceLeft, targetRight);
//			} catch (Exception e) {
//				
//				Log.error("error linking " + sourceLeft.getFieldName() + " to " + targetRight.getFieldName(), e);
//			}
//		}
	}
	
//	public FieldModel findModel(UserModel streamModel, String colName) {
//		for (FieldModel field : streamModel.getFields()) {
//			if (field.getModel().getName().equalsIgnoreCase(colName)) {
//				return field;
//			}
//		}
//		
//		return null;
//	}
		
//	public List<UserModel> getChildren(){
//		return childs;
//	}
	public List<Object> getChildren(){
		List<Object> children = new ArrayList<Object>();
		children.addAll(childs);
		children.add(model);
		
		return children;
	}
}
