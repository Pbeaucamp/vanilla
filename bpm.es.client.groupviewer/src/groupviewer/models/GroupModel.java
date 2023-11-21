package groupviewer.models;

import java.util.ArrayList;

import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.User;

public class GroupModel {

	private Group group;
	private ArrayList<User> bmpUserList;
	
	
	public GroupModel(Group group) {
		this.group = group;
		bmpUserList = new ArrayList<User>();
	}
	

	public GroupModel(Group group, ArrayList<User> userList) {
		this(group);
		this.bmpUserList = userList;
	}

	public String getGroupName() {
		return this.group.getName();
	}
	
	public int getGroupID()
	{
		return this.group.getId();
	}
	
	public void addUser (User user){
		if (!bmpUserList.contains(user))
			bmpUserList.add(user);
	}
	
	public void removeUser (User user){
		if (bmpUserList.contains(user)){
			bmpUserList.remove(user);
		}
	}
	
	public ArrayList<User> getUserList (){
		return this.bmpUserList;
	}
	
	public int getSizeOfGroup(){
		return this.bmpUserList.size();
	}


	public Group getGroup() {
		return this.group;	
	}
}
