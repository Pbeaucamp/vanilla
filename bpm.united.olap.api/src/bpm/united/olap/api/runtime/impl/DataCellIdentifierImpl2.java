package bpm.united.olap.api.runtime.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import bpm.united.olap.api.data.ExternalQueryIdentifier;
import bpm.united.olap.api.data.IExternalQueryIdentifier;
import bpm.united.olap.api.model.ElementDefinition;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.result.DrillThroughIdentifier;
import bpm.united.olap.api.result.impl.DrillThroughIdentifierImpl;
import bpm.united.olap.api.runtime.DataCellIdentifier2;

public class DataCellIdentifierImpl2 extends EObjectImpl implements DataCellIdentifier2 {

	private static long maxLength = 0;
	
	private static class ElementDefinitionComparator implements Comparator<ElementDefinition>{

		@Override
		public int compare(ElementDefinition arg0, ElementDefinition arg1) {
			return arg0.getUname().compareTo(arg1.getUname());
		}
		
	}
	
	private List<ElementDefinition> intersections = new ArrayList<ElementDefinition>();
	private List<Member> wheres = new ArrayList<Member>();
	private Measure measure;
	
	private String md5EffectiveQueryKey;
	
	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public void setMeasure(Measure measure) {
		this.measure = measure;
	}

	private String key;
	
	
	@Override
	public void dump() {
		
		
	}

	@Override
	public List<ElementDefinition> getIntersections() {
		return intersections;
	}

	@Override
	public String getKey() {
		if (key == null){
			ElementDefinitionComparator comparator = new ElementDefinitionComparator();
			List<ElementDefinition> intersCloned = new ArrayList<ElementDefinition>(intersections);
			Collections.sort(intersCloned, comparator);
			List<Member> wheresCloned = new ArrayList<Member>(wheres);
			Collections.sort(wheresCloned, comparator);
			
			StringBuilder b = new StringBuilder();
			for(ElementDefinition e : intersCloned){
				b.append(e.getUname());
			}
			b.append("wheres:");
			for(ElementDefinition e : wheresCloned){
				b.append(e.getUname());
			}
			b.append(measure.getUname());
			
			key = b.toString();
			
			if (key.length() > maxLength){
				maxLength = key.length();
			}
		}
		
		
		return key;
	}

	@Override
	public List<Member> getWhereMembers() {
		return wheres;
	}

	@Override
	public void setIntersections(List<ElementDefinition> intersections) {
		//perform a verification on the list elements, only measures and members are allowed
		for(ElementDefinition e : intersections){
			if (!((e instanceof Member) || (e instanceof Measure))){
				throw new RuntimeException("Trying to set DataCellIdentifier with a list containing a " + e.getClass() + " object -> only Member and Measure supported");
			}
		}
		
		
		this.intersections = intersections;
		
	}

	@Override
	public void setWhereMembers(List<Member> whereMembers) {
		this.wheres = whereMembers;
		
	}

	@Override
	public void addIntersection(ElementDefinition intersection) {
		this.intersections.add(intersection);
	}

	@Override
	public void addWhereMember(Member whereMember) {
		this.wheres.add(whereMember);
	}

	@Override
	public DrillThroughIdentifier getDrillThroughIdentifier() {
		DrillThroughIdentifier dtId = new DrillThroughIdentifierImpl();
		
		List<String> dtInters = new ArrayList<String>();
		for(ElementDefinition elem : intersections) {
			dtInters.add(elem.getUname());
		}
		dtId.setIntersections(dtInters);
		
		dtId.setMeasure(measure.getUname());
		
		List<String> dtWheres = new ArrayList<String>();
		for(Member mem : wheres) {
			dtWheres.add(mem.getUname());
		}
		dtId.setWheres(dtWheres);
		
		return dtId;
	}

	@Override
	public IExternalQueryIdentifier getExternalQueryIdentifier() {
		ExternalQueryIdentifier id = new ExternalQueryIdentifier();
		
		for(ElementDefinition elem : intersections) {
			if(elem instanceof Member) {
				Level lvl = ((Member)elem).getParentLevel();
				Member mem = ((Member)elem);
				
				while(lvl != null) {
					id.addSelectElement(lvl.getUname(), mem.getName());
					lvl = lvl.getParentLevel();
					mem = mem.getParentMember();
				}
			}
		}
		
		id.addSelectElement(measure.getUname(), "");
		
		for(Member mem : wheres) {
			id.addWhereElement(mem.getUname());
		}
		
		return id;
	}

	
}
