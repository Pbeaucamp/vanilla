package bpm.united.olap.api.runtime.model;

import bpm.united.olap.api.model.Member;


public interface IMemberChecker {

	public boolean validate(Member member, Object factTableIdentifier);
	
//	public List<> generateKey(List<Object> row);
}
