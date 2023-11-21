package bpm.vanilla.platform.core.beans;


import java.util.Date;



public class VanillaSession {
	
	private User user;
	private Date createDate = new Date();
	private Date time = new Date();
	private long timeOut = 1000 * 60 * 60 * 2;
	private String uuid;
	private String cubeXml;
	
	public VanillaSession() {
	}
		
	public VanillaSession(User user, String uuid) {
		this.user = user;
		this.uuid = uuid;
	}

	public Date getCreationDate(){
		return createDate;
	}
	
	public long getTimeOut(){
		return timeOut;
	}

	public void refreshTime(){
		time = new Date();
	}
	
	public boolean isActive(){
		if (user != null && user.isSuperUser()){
			return true;
		}
		Date d = new Date();
		return d.getTime() - time.getTime() < timeOut;
	}
	
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}


	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}


	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}


	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCubeXml() {
		return cubeXml;
	}

	public void setCubeXml(String cubeXml) {
		this.cubeXml = cubeXml;
	}
	

}
