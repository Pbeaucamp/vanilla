package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.Date;

public class Theme {

	private int id,ownerId;
	private String name,code,comment,description;//,code_insee,codelocalisation_x,codelocalisation_y;
	private Date creationDate;

	public Theme() {
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

	/**
	 * @return the ownerId
	 */
	public int getOwnerId() {
		return ownerId;
	}

	/**
	 * @param ownerId the ownerId to set
	 */
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

//	/**
//	 * @return the code_insee
//	 */
//	public String getCode_insee() {
//		return code_insee;
//	}
//
//	/**
//	 * @param code_insee the code_insee to set
//	 */
//	public void setCode_insee(String code_insee) {
//		this.code_insee = code_insee;
//	}
//
//	/**
//	 * @return the codelocalisation_x
//	 */
//	public String getCodelocalisation_x() {
//		return codelocalisation_x;
//	}
//
//	/**
//	 * @param codelocalisation_x the codelocalisation_x to set
//	 */
//	public void setCodelocalisation_x(String codelocalisation_x) {
//		this.codelocalisation_x = codelocalisation_x;
//	}
//
//	/**
//	 * @return the codelocalisation_y
//	 */
//	public String getCodelocalisation_y() {
//		return codelocalisation_y;
//	}
//
//	/**
//	 * @param codelocalisation_y the codelocalisation_y to set
//	 */
//	public void setCodelocalisation_y(String codelocalisation_y) {
//		this.codelocalisation_y = codelocalisation_y;
//	}
}
