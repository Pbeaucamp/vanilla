package bpm.fmloader.client.dto;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IndicatorValuesDTO extends DTO implements IsSerializable {
	private int id;
	private int assoId;
	private boolean valueNull = false;
	private boolean isNull = false;
	private boolean isModified = false;
	private boolean isAdded = false;
	private boolean isDeleted = false;
	
	private String objValue;
	private String seuilMinValue;
	private String seuilMaxValue;
	private String valMinValue;
	private String valMaxValue;
	
	private Date date;
	private Date nextDate;
	private CommentDTO comment;
	
	public IndicatorValuesDTO() {
		
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

	public boolean isValueNull() {
		return valueNull;
	}

	public void setValueNull(boolean valueNull) {
		this.valueNull = valueNull;
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

	public String getObjValue() {
		return objValue;
	}

	public void setObjValue(String objValue) {
		this.objValue = objValue;
	}

	public String getSeuilMinValue() {
		return seuilMinValue;
	}

	public void setSeuilMinValue(String seuilMinValue) {
		this.seuilMinValue = seuilMinValue;
	}

	public String getSeuilMaxValue() {
		return seuilMaxValue;
	}

	public void setSeuilMaxValue(String seuilMaxValue) {
		this.seuilMaxValue = seuilMaxValue;
	}

	public String getValMinValue() {
		return valMinValue;
	}

	public void setValMinValue(String valMinValue) {
		this.valMinValue = valMinValue;
	}

	public String getValMaxValue() {
		return valMaxValue;
	}

	public void setValMaxValue(String valMaxValue) {
		this.valMaxValue = valMaxValue;
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

	public void setComment(CommentDTO comment) {
		this.comment = comment;
	}

	public CommentDTO getComment() {
		return comment;
	}
	
	
}
