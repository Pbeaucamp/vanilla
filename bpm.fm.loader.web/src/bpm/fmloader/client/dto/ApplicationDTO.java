package bpm.fmloader.client.dto;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ApplicationDTO implements IsSerializable {
	
	private int id;
	private String name;
	private ApplicationDTO parent;
	private List<ApplicationDTO> children;
	
	public ApplicationDTO() {
		super();
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
	public void setParent(ApplicationDTO parent) {
		this.parent = parent;
	}
	public ApplicationDTO getParent() {
		return parent;
	}
	public void setChildren(List<ApplicationDTO> children) {
		this.children = children;
	}
	public List<ApplicationDTO> getChildren() {
		return children;
	}
	public void addChild(ApplicationDTO app) {
		if(children == null) {
			children = new ArrayList<ApplicationDTO>();
		}
		children.add(app);
	}
}
