package bpm.vanilla.platform.core.beans.fmdt;

import java.util.ArrayList;
import java.util.List;

public class FmdtDimension extends FmdtData {

	private String hierarchyName = "Default_Hierarchy";
	private List<FmdtData> levels = new ArrayList<FmdtData>();

	public FmdtDimension() {

	}

	public FmdtDimension(String name, String description) {
		super(name, name, description);
	}

	public FmdtDimension(List<FmdtData> levels, String name, String description) {
		super(name, name, description);
		this.levels = levels;
	}

	public String getHierarchyName() {
		return hierarchyName;
	}

	public void setHierarchyName(String hierarchyName) {
		this.hierarchyName = hierarchyName;
	}

	public List<FmdtData> getLevels() {
		return levels;
	}

	public void setLevels(List<FmdtData> levels) {
		this.levels = levels;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmdtDimension other = (FmdtDimension) obj;
		if (hierarchyName == null) {
			if (other.hierarchyName != null)
				return false;
		}
		else if (!hierarchyName.equals(other.hierarchyName))
			return false;
		if (levels == null) {
			if (other.levels != null)
				return false;
		}
		else if (!levels.equals(other.levels))
			return false;
		return true;
	}

}
