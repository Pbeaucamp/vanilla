package bpm.android.vanilla.core.beans.cube;

import java.io.Serializable;

public class HierarchyAndCol implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String uniqueName;
	private String name;
	private boolean isCol;
	private boolean isLoad;
	private boolean toAdd;
	private boolean toRemove;
	private AndroidDimension dim;
	
	public HierarchyAndCol(){}
	
	public HierarchyAndCol(String uniqueName, String name, boolean isCol, boolean isLoad, AndroidDimension dim){
		this.uniqueName = uniqueName;
		this.name = name;
		this.isCol = isCol;
		this.isLoad = isLoad;
		this.dim = dim;
	}
	
	public String getUniqueName(){
		return uniqueName;
	}
	
	public String getName(){
		return name;
	}
	
	public Boolean getIsCol(){
		return isCol;
	}
	
	public AndroidDimension getDimension(){
		return dim;
	}
	
	public void setUniqueName(String uniqueName){
		this.uniqueName = uniqueName;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setIsCol(boolean isCol){
		this.isCol = isCol;
	}
	
	public void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

	public boolean isLoad() {
		return isLoad;
	}

	public void setToAdd(boolean toAdd) {
		this.toAdd = toAdd;
	}

	public boolean isToAdd() {
		return toAdd;
	}

	public void setToRemove(boolean toRemove) {
		this.toRemove = toRemove;
	}

	public boolean isToRemove() {
		return toRemove;
	}

	public void setDimension(AndroidDimension dim){
		this.dim = dim;
	}
}