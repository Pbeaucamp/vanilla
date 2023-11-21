package bpm.birep.admin.client.trees;

import bpm.vanilla.platform.core.beans.User;


public class TreeUser extends TreeParent {

	private User user;
	
	public TreeUser(User user){
		super(user.getName());
		this.user = user;
	}
	
	public User getUser(){
		return user;
	}
	
	public String toString(){
		return user.getName();
	}
}
