package bpm.birep.admin.client.trees;

import bpm.vanilla.platform.core.beans.Role;



public class TreeRole extends TreeParent {

	private Role role;
	
	public TreeRole(Role role){
		super(role.getName());
		this.role = role;
	}
	
	public Role getRole(){
		return role;
	}
	
	public String toString(){
		return role.getName();
	}
}
