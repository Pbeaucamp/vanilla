package bpm.fa.api.olap.xmla.parse;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.xmla.XMLAMember;

public class XMLATuple {
	private ArrayList<OLAPMember> mbs = new ArrayList<OLAPMember>();
	
	public XMLATuple() {
		
	}
	
	public void addMember(OLAPMember mb) {
		mbs.add(mb);
	}
	
	public ArrayList<OLAPMember> getMembers() {
		return mbs;
	}
	
	public int getNbMembers() {
		return mbs.size();
	}
	
	public List<OLAPMember> getMemberForHiera(String hiera){
		List<OLAPMember> l = new ArrayList<OLAPMember>();
		
		for(OLAPMember m : mbs){
			if (((XMLAMember)m).getHieraName().equals(hiera)){
				l.add(m);
			}
		}
		
		return l;
	}
}
