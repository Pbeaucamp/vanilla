package org.fasd.inport.mondrian.beans;

import java.util.ArrayList;
import java.util.List;

public class HierarchyGrantBean {
	private String hiera = "";
	private String topLevel = "";
	private String bottomLevel = "";
	private String access = "";
	private List<MemberGrantBean> members = new ArrayList<MemberGrantBean>();
	
	public void addMember(MemberGrantBean m){
		members.add(m);
	}

	public String getBottomLevel() {
		return bottomLevel;
	}

	public void setBottomLevel(String bottomLevel) {
		this.bottomLevel = bottomLevel;
	}

	public String getHiera() {
		return hiera;
	}

	public void setHiera(String hiera) {
		this.hiera = hiera;
	}

	public String getTopLevel() {
		return topLevel;
	}

	public void setTopLevel(String topLevel) {
		this.topLevel = topLevel;
	}

	public List<MemberGrantBean> getMembers() {
		return members;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

}
