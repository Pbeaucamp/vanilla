package bpm.android.vanilla.core.beans.cube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AndroidDimension implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String uniqueName;
	private String name;
	private List<HierarchyAndCol> hierarchiesAndCol;

	public AndroidDimension() {
	}

	public AndroidDimension(String uniqueName, String name) {
		this.uniqueName = uniqueName;
		this.name = name;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getName() {
		return name;
	}

	public List<HierarchyAndCol> getHierarchyAndCol() {
		return hierarchiesAndCol;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHierarchyAndCol(List<HierarchyAndCol> hierarchiesAndCol) {
		this.hierarchiesAndCol = hierarchiesAndCol;
	}
	
	public void addHierarchyAndCol(HierarchyAndCol hierarchyAndCol) {
		if(hierarchiesAndCol == null) {
			this.hierarchiesAndCol = new ArrayList<HierarchyAndCol>();
		}
		this.hierarchiesAndCol.add(hierarchyAndCol);
	}
}