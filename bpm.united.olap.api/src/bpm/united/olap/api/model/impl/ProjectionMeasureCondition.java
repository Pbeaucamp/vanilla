package bpm.united.olap.api.model.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bpm.united.olap.api.model.Member;

public class ProjectionMeasureCondition implements Serializable {

	private List<Member> members;
	private String formula;
	
	public List<Member> getMembers() {
		return members;
	}
	
	public void setMembers(List<Member> members) {
		this.members = members;
	}
	
	public String getFormula() {
		return formula;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}

	public void addMember(Member m) {
		if(members == null) {
			members = new ArrayList<Member>();
		}
		this.members.add(m);
	}
	
	
	
}
