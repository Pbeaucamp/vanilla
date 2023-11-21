package bpm.es.sessionmanager.gef.model;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
	private List<FieldModel> fields = new ArrayList<FieldModel>();
	
	private MapModel parent;
	
	private int x, y;
	
	public UserModel(){

	}

	
	
	public String getName(){
		return "user";
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
