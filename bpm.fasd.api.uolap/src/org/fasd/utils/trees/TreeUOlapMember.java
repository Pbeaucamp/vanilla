package org.fasd.utils.trees;

public class TreeUOlapMember extends TreeParent {

	private String uname;
	
	public TreeUOlapMember(String name, String uname) {
		super(name);
		this.uname = uname;
	}
	
	public String getUname() {
		return uname;
	}

}
