package bpm.workflow.commons.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.resources.Resource.TypeResource;

@Entity
@Table(name = "resource")
public class ResourceWrapper {

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column (name="id")
	private int id;

	@Column(name="type")
	private TypeResource type;
	
	@Column(name="model", length = 10000000)
	private String model;
	
	public ResourceWrapper() {}

	public ResourceWrapper(int id, TypeResource type, String model) {
		this.id = id;
		this.type = type;
		this.model = model;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public TypeResource getType() {
		return type;
	}
	
	public void setType(TypeResource type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
}
