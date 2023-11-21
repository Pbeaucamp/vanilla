package bpm.vanilla.platform.core.beans.ged;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private int id;
	private String name;
	private Integer parentId;
	private List<Category> subCategories;
	
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
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public void setSubCategories(List<Category> subCategories) {
		this.subCategories = subCategories;
	}
	public List<Category> getSubCategories() {
		return subCategories;
	}
	public void addSubCategory(Category cat) {
		if(subCategories == null) {
			subCategories = new ArrayList<Category>();
		}
		subCategories.add(cat);
	}
	
}
