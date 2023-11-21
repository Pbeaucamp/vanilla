package bpm.norparena.ui.menu.client.trees;

import bpm.vanilla.platform.core.beans.Group;


public class TreeGroup extends TreeParent {

	private Group group;
	
	public TreeGroup(Group group){
		super(group.getName());
		this.group = group;
	}
	
	public Group getGroup(){
		return group;
	}
	
	public String toString(){
		return group.getName();
	}
}
