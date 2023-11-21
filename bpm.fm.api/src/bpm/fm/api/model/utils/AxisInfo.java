package bpm.fm.api.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fm.api.model.Axis;

public class AxisInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Axis axis;
	
	private List<LevelMember> members = new ArrayList<LevelMember>();
	
	/**
	 * This store the last levelMembers
	 * it's used to easily find the right member for a given key
	 */
	private HashMap<String, LevelMember> memberKeys = new HashMap<String, LevelMember>();

	public Axis getAxis() {
		return axis;
	}

	public void setAxis(Axis axis) {
		this.axis = axis;
	}

	public List<LevelMember> getMembers() {
		return members;
	}

	public void setMembers(List<LevelMember> members) {
		this.members = members;
	}

	public void addMember(LevelMember member) {
		this.members.add(member);
	}

	public HashMap<String, LevelMember> getMemberKeys() {
		return memberKeys;
	}

	public void setMemberKeys(HashMap<String, LevelMember> memberKeys) {
		this.memberKeys = memberKeys;
	}
	
	public void addMemberKey(String key, LevelMember member) {
		this.memberKeys.put(key, member);
	}
}
