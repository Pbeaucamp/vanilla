package bpm.freemetrics.api.organisation.application;

public class ApplicationHierarchy {

	private Integer id;
	private Integer parentId;
	private Integer childId;
	
	public ApplicationHierarchy(){}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getParentId() {
		return parentId;
	}
	
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public Integer getChildId() {
		return childId;
	}
	
	public void setChildId(Integer childId) {
		this.childId = childId;
	}
}
