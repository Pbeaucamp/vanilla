package bpm.fmloader.client.dto;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ValuesDTO extends DTO implements IsSerializable {

	private int id;
	private int assoId;
	private String value;
	private boolean valueNull = false;
	private boolean isNull = false;
	private boolean isModified = false;
	private boolean isAdded = false;
	private boolean isDeleted = false;
	private CommentDTO comment;
	private Date date;
	private Date nextDate;
	
	public ValuesDTO() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAssoId() {
		return assoId;
	}

	public void setAssoId(int assoId) {
		this.assoId = assoId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public boolean isModified() {
		return isModified;
	}

	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}

	public boolean isValueNull() {
		return valueNull;
	}

	public void setValueNull(boolean valueNull) {
		this.valueNull = valueNull;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setComment(CommentDTO comment) {
		this.comment = comment;
	}

	public CommentDTO getComment() {
		return comment;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}

	public Date getNextDate() {
		return nextDate;
	}
	
	
	
}
