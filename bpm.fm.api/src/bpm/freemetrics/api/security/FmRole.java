package bpm.freemetrics.api.security;

import java.util.Date;

public class FmRole {
	private int id;
    private String name;
    private String comment;
    private Date creationDate;
    private Integer type;
    private String image;
    private String custom;
    private String grants;

    public FmRole() {
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	/**
	 * @return the grants
	 */
	public String getGrants() {
		return grants;
	}

	/**
	 * @param grants the grants to set
	 */
	public void setGrants(String grants) {
		this.grants = grants;
	}
    
}
