package bpm.fa.api.olap.xmla.parse;

import java.util.ArrayList;
import java.util.List;

import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.xmla.XMLAMember;

public class XMLAAxis {
	private ArrayList<XMLATuple> tuples = new ArrayList<XMLATuple>();
	private String name;
	
	public XMLAAxis() {
		
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void addTuple(XMLATuple tp) {
		tuples.add(tp);
	}
	
	public ArrayList<XMLATuple> getTuples() {
		return tuples;
	}
	
	/**
	 * 
	 * @return the number of rows/lines
	 */
	public int getNbTuple() {
		return tuples.size();
	}
	
	public int getMaxMembers() {
		int nb = 0;
		
		for (int i=0; i < tuples.size(); i++) {
			if (nb < tuples.get(i).getNbMembers()) 
				nb = tuples.get(i).getNbMembers();
		}
		
		return nb;
	}

	public List<XMLATuple> getChildTuples(OLAPMember m , XMLATuple current) {
		List<XMLATuple> l = new ArrayList<XMLATuple>();
		
		int begin = -1;
		
		for(int i = 0; i < tuples.size(); i++){//XMLATuple t : tuples){
			
			if (tuples.get(i) == current){
				begin = i + 1;
			}
			
			
			
			
			if (begin != -1){
				for(OLAPMember mb : tuples.get(i).getMembers()){
					if (((XMLAMember)m).getHieraName().equals(((XMLAMember)mb).getHieraName()) && mb.getLevelDepth() == m.getLevelDepth() + 1 && current != tuples.get(i)){
						l.add(tuples.get(i));
						break;
					}
					
				}
			}
				
		}
		
		return l;
	}
}
