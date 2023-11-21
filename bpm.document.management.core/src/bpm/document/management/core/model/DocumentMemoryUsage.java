package bpm.document.management.core.model;

import java.io.Serializable;

public class DocumentMemoryUsage implements Serializable {

	private static final long serialVersionUID = 1L;

	private long memoryId = 0;
	private String user = "";
	private Integer officeMemory;
	private Integer audioMemory;
	private Integer videoMemory;
	private Integer imageMemory;
	private Integer otherMemory;
	private Integer zipMemory;
	private Integer textMemory;
	private Integer xaklMemory;
	private Integer totalMemory;
	private Integer usedMemory;
	private boolean alertLevelOne = false;
	private boolean alertLevelTwo = false;
	private boolean alertLevelThree = false;
	private boolean alertLevelFour = false;

	public long getMemoryId() {
		return memoryId;
	}

	public void setMemoryId(long memoryId) {
		this.memoryId = memoryId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isAlertLevelOne() {
		return alertLevelOne;
	}

	public void setAlertLevelOne(boolean alertLevelOne) {
		this.alertLevelOne = alertLevelOne;
	}

	public boolean isAlertLevelTwo() {
		return alertLevelTwo;
	}

	public void setAlertLevelTwo(boolean alertLevelTwo) {
		this.alertLevelTwo = alertLevelTwo;
	}

	public boolean isAlertLevelThree() {
		return alertLevelThree;
	}

	public void setAlertLevelThree(boolean alertLevelThree) {
		this.alertLevelThree = alertLevelThree;
	}

	public boolean isAlertLevelFour() {
		return alertLevelFour;
	}

	public void setAlertLevelFour(boolean alertLevelFour) {
		this.alertLevelFour = alertLevelFour;
	}

	public Integer getOfficeMemory() {
		return officeMemory;
	}

	public void setOfficeMemory(Integer officeMemory) {
		this.officeMemory = officeMemory;
	}

	public Integer getAudioMemory() {
		return audioMemory;
	}

	public void setAudioMemory(Integer audioMemory) {
		this.audioMemory = audioMemory;
	}

	public Integer getVideoMemory() {
		return videoMemory;
	}

	public void setVideoMemory(Integer videoMemory) {
		this.videoMemory = videoMemory;
	}

	public Integer getImageMemory() {
		return imageMemory;
	}

	public void setImageMemory(Integer imageMemory) {
		this.imageMemory = imageMemory;
	}

	public Integer getOtherMemory() {
		return otherMemory;
	}

	public void setOtherMemory(Integer otherMemory) {
		this.otherMemory = otherMemory;
	}

	public Integer getZipMemory() {
		return zipMemory;
	}

	public void setZipMemory(Integer zipMemory) {
		this.zipMemory = zipMemory;
	}

	public Integer getTextMemory() {
		return textMemory;
	}

	public void setTextMemory(Integer textMemory) {
		this.textMemory = textMemory;
	}

	public Integer getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(Integer totalMemory) {
		this.totalMemory = totalMemory;
	}

	public Integer getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(Integer usedMemory) {
		this.usedMemory = usedMemory;
	}

	public Integer getXaklMemory() {
		return xaklMemory != null ? xaklMemory : 0;
	}

	public void setXaklMemory(Integer xaklMemory) {
		this.xaklMemory = xaklMemory;
	}
}
