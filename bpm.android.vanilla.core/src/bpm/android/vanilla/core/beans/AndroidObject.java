package bpm.android.vanilla.core.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AndroidObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int TYPE_DIRECTORY = 0;
	public static final int TYPE_FWR = 1;
	
	private int id;
	private String name;
	private int type;
	private int subtype;
	private int parentId;
	
	private AndroidObject parent;
	private List<AndroidObject> childs;
	
	public AndroidObject() {}
	
	public AndroidObject(int type) {
		this.type = type;
	}
	
	public AndroidObject(int id, String name, int type, int subType) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.subtype = subType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addChild(AndroidObject androidObject) {
		if(childs == null){
			childs = new ArrayList<AndroidObject>();
		}
		androidObject.setParent(this);
		this.childs.add(androidObject);
	}

	public List<AndroidObject> getChilds() {
		return childs;
	}

	public AndroidObject getParent() {
		return parent;
	}

	public void setParent(AndroidObject parent) {
		this.parent = parent;
		setParentId(parent.getId());
	}
	
	private void setParentId(int parentId){
		this.parentId = parentId;
	}

	public int getParentId() {
		if(parent == null){
			return parentId;
		}
		else {
			return parent.getId();
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}
}
