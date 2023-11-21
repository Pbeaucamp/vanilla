package bpm.vanilla.platform.core.beans;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import bpm.vanilla.platform.core.beans.IRuntimeState.ActivityResult;

@Entity
@Table(name = "rpy_item_instance")
public class ItemInstance implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
	@Column(name = "id")
	private int id;

	@Column(name = "item_id")
	private int itemId;

	@Column(name = "item_type")
	private int itemType;

	@Column(name = "group_id")
	private int groupId;

	@Column(name = "run_date")
	private Date runDate;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "result")
	private ActivityResult result;

	@Column(name = "duration")
	private Long duration;

	@Column(name = "`xml`", length = 10000000)
	private String stateXml;

	@Transient
	private IRuntimeState state;

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

	public int getItemType() {
		return itemType;
	}

	public void setItemType(int itemType) {
		this.itemType = itemType;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

	public ActivityResult getResult() {
		return result;
	}

	public void setResult(ActivityResult result) {
		this.result = result;
	}

	public Long getDuration() {
		return duration;
	}
	
	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getDurationAsString() {
		long duration = getDuration();
		if(duration == -1) {
			return "";
		}
		
		int milliseconds =  (int) duration % 1000;
		int seconds = (int) (duration / 1000) % 60 ;
		int minutes = (int) ((duration / (1000*60)) % 60);
		int hours   = (int) ((duration / (1000*60*60)) % 24);
		return (hours != 0 ? hours + "h" : "") + (minutes != 0 ? minutes + "m" : "") + seconds + "s " + milliseconds;
	}
	
	public String getStateXml() {
		return stateXml;
	}

	public void setStateXml(String stateXml) {
		this.stateXml = stateXml;
	}

	public IRuntimeState getState() {
		return state;
	}

	public void setState(IRuntimeState state) {
		this.state = state;
	}

}
