package groupviewer.models;

import bpm.vanilla.platform.core.beans.User;

public class UserModel {
	
	// TODO remplacer le parent id par le model parent ?
	private int parentID;
	private User userData;
	
	public UserModel (User user){
		this.userData = user;
		//this.groupID = groupId;
	}
	
	public int getUserID(){
		return userData.getId();
	}
	
	public int getParentID(){
		return this.parentID;
	}
	
	public void setParentId(int parent){
		this.parentID = parent;
	}
	
	/*
	 * Name
	 */
	public String getName() {
		return userData.getName();
	}
	/*
	 * Surname
	 */
	public String getSurname() {
		return userData.getSurname();
	}

	public void setUserData(User userData) {
		this.userData = userData;
	}

	public User getUserData() {
		return userData;
	}
	
}
