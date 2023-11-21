package bpm.mdm.model.supplier;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MdmDirectory implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private Integer parentId = 0;
	
	private String name;
	private String description;
	
	private Date dateCreation = new Date();
	
	private List<MdmDirectory> childs;

	public MdmDirectory() {
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public List<MdmDirectory> getChilds() {
		return childs;
	}
	
	public void setChilds(List<MdmDirectory> childs) {
		this.childs = childs;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
