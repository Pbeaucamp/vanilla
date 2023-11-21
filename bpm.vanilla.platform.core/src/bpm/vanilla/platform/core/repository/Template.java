package bpm.vanilla.platform.core.repository;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.VanillaImage;

@Entity
@Table (name = "rpy_templates")
public class Template<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum TypeTemplate {
		DASHBOARD, WEB_REPORT
	}

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;

	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private TypeTemplate type;

	@Column(name = "date_creation")
	private Date dateCreation;

	@Column(name = "creator_id")
	private Integer creatorId;
	
	@Transient
	private Integer imageId;
	
	@Column(name = "model", length = 10000000)
	private String model;

	@Transient
	private VanillaImage image;
	
	@Transient
	private T item;
	
	public Template() { }
	
	public Template(String name, TypeTemplate type) {
		this.name = name;
		this.type = type;
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

	public TypeTemplate getType() {
		return type;
	}

	public void setType(TypeTemplate type) {
		this.type = type;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}
	
	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	@Access(javax.persistence.AccessType.PROPERTY)
	@Column(name = "image_id")
	public Integer getImageId() {
		if (image != null) {
			return image.getId();
		}
		else {
			return imageId;
		}
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public void setImage(VanillaImage image) {
		this.image = image;
	}
	
	public VanillaImage getImage() {
		return image;
	}

	public void setItem(T item) {
		this.item = item;
	}
	
	public T getItem() {
		return item;
	}
}
