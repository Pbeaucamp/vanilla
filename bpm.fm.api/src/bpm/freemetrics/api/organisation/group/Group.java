package bpm.freemetrics.api.organisation.group;

import java.util.Date;
import java.util.List;

import bpm.freemetrics.api.organisation.application.Application;

public class Group {

	private int id;

    private String name = "";
    private String comment,description;
    private Date date;
    private String image;
    private String custom;
    private int typeCollectivite;
    
    private List<Application> children;
    
    public Group() {
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public int getTypeCollectivite() {
		return typeCollectivite;
	}

	public void setTypeCollectivite(int typeCollectivite) {
		this.typeCollectivite = typeCollectivite;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public void setChildren(List<Application> children) {
		this.children = children;
	}

	public List<Application> getChildren() {
		return children;
	}
    
    
    
}
