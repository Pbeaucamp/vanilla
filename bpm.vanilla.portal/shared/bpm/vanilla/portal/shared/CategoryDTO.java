package bpm.vanilla.portal.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CategoryDTO implements IsSerializable {
	private int id;
	private Integer parentId;
	private String name;
	
	public CategoryDTO() {
		super();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
