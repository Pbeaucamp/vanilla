package bpm.vanilla.workplace.core.model;

public class Dependency {
	
	private Integer oldId;
	private Integer newId;
	
	public Dependency() { }

	public Dependency(Integer oldId) {
		this.setOldId(oldId);
	}

	public void setOldId(Integer oldId) {
		this.oldId = oldId;
	}

	public Integer getOldId() {
		return oldId;
	}

	public void setNewId(Integer newId) {
		this.newId = newId;
	}

	public Integer getNewId() {
		return newId;
	}
}
