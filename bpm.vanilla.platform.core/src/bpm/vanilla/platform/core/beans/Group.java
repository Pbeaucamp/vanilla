package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * represent a Group Group are applyed to defined what the users can see
 * 
 * @author LCA
 * 
 */
public class Group implements Serializable {
	
	private static final long serialVersionUID = -492594183802520933L;
	
	private Integer id;
	private String name = "";
	private String comment = "";
	private Date creation = new Date();
	private String image = "";
	private String custom1 = "";
	private Integer parentId;
	private Integer maxSupportedWeightFmdt = 100;

	public Group() {

	}

	/**
	 * used to evaluate FMDT Query, if the Weight o the FMDT is > at this value,
	 * the Query wont be able to be run
	 * 
	 * @return the maxSupportedWeightFmdt
	 */
	public Integer getMaxSupportedWeightFmdt() {
		if (maxSupportedWeightFmdt == null) {
			return 100;
		}
		return maxSupportedWeightFmdt;
	}

	/**
	 * @param maxSupportedWeightFmdt
	 *            the maxSupportedWeightFmdt to set
	 */
	public void setMaxSupportedWeightFmdt(Integer maxSupportedWeightFmdt) {
		if (maxSupportedWeightFmdt != null) {
			this.maxSupportedWeightFmdt = maxSupportedWeightFmdt;
		}

	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		try {
			this.parentId = Integer.parseInt(parentId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(String id) {
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void setId(Integer id) {
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

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public void setCreation(String date) {
		this.creation = new Date();
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCustom1() {
		return custom1;
	}

	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Group) {
			return id == ((Group)obj).getId();
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
