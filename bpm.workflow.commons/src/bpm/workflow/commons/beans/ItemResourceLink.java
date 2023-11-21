package bpm.workflow.commons.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "item_resource_link")
public class ItemResourceLink {
	
	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name="id")
	private int id;

	@Column(name="itemId")
	private int itemId;

	@Column(name="resourceId")
	private int resourceId;
	
	public ItemResourceLink() { }
	
	public ItemResourceLink(int itemId, int resourceId) {
		this.itemId = itemId;
		this.resourceId = resourceId;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	public int getResourceId() {
		return resourceId;
	}
	
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}
}
