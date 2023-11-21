package bpm.freemetrics.api.features.infos;

import java.util.Calendar;
import java.util.Date;

public class Unit {

	private int id = 0;

    private String unUnitCategory;
    private String name;
    private String unUnitFormat;
    private Date   unCreationDate = Calendar.getInstance().getTime();
    private String unComment;
    
	/**
	 * @return the unUnitCategory
	 */
	public String getUnUnitCategory() {
		return unUnitCategory;
	}

	/**
	 * @param unUnitCategory the unUnitCategory to set
	 */
	public void setUnUnitCategory(String unUnitCategory) {
		this.unUnitCategory = unUnitCategory;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the unUnitFormat
	 */
	public String getUnUnitFormat() {
		return unUnitFormat;
	}

	/**
	 * @param unUnitFormat the unUnitFormat to set
	 */
	public void setUnUnitFormat(String unUnitFormat) {
		this.unUnitFormat = unUnitFormat;
	}

	/**
	 * @return the unCreationDate
	 */
	public Date getUnCreationDate() {
		return unCreationDate;
	}

	/**
	 * @param unCreationDate the unCreationDate to set
	 */
	public void setUnCreationDate(Date unCreationDate) {
		this.unCreationDate = unCreationDate;
	}

	/**
	 * @return the unComment
	 */
	public String getUnComment() {
		return unComment;
	}

	/**
	 * @param unComment the unComment to set
	 */
	public void setUnComment(String unComment) {
		this.unComment = unComment;
	}

	public Unit() {
    	super();
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
