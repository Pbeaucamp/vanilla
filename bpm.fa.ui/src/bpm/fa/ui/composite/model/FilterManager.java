package bpm.fa.ui.composite.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;

public class FilterManager {
	private OLAPCube olapCube;
	private HashMap<Hierarchy, List<OLAPMember>> filters = new HashMap<Hierarchy, List<OLAPMember>>();
	private HashMap<String, Boolean> enabled = new HashMap<String, Boolean>();
	private HashMap<Hierarchy, Boolean> enabledHiera = new HashMap<Hierarchy, Boolean>(); 
	
	public FilterManager(OLAPCube cube){
		this.olapCube = cube;
	}
	
	public void enable(Level lvl, boolean enabled){
		this.enabled.put(lvl.getUniqueName(), enabled);
		
	}
	
	
	public boolean isEnabled(){
		
		for(Hierarchy h : enabledHiera.keySet()){
			if (enabledHiera.get(h)){
				for(String s : enabled.keySet()){
					if (s.startsWith(h.getUniqueName()) && enabled.get(s)){
						return true;
					}
				}
				
			}
		}
		
		
		return false;
	}
		
	public void removeFilter(OLAPMember mb){
		if (filters.get(mb.getHiera()) == null){
			return;
		}
		filters.get(mb.getHiera()).remove(mb);
	}
	public void addFilter(OLAPMember mb){
		if (filters.get(mb.getHiera()) == null){
			filters.put(mb.getHiera(), new ArrayList<OLAPMember>());
		}
		if (filters.get(mb.getHiera()).contains(mb)){
			return;
		}
		filters.get(mb.getHiera()).add(mb);
		
		for(Level l : mb.getHiera().getLevel()){
			if (enabled.get(l.getUniqueName()) == null){
				enabled.put(l.getUniqueName(), false);
			}
		}
	}

	public List<Hierarchy> getFilteredHierarchy(){
		List<Hierarchy> l = new ArrayList<Hierarchy>();
		
		for(Hierarchy h : filters.keySet()){
			if (!filters.get(h).isEmpty()){
				l.add(h);
			}
		}
		return l;
	}

	public String getFilterText(Hierarchy h){
		List l = filters.get(h);
		if ( l== null || l.isEmpty()){
			return "All members";
		}
		else{
			return l.size() + " members selected";
		}
	}
	
	public List<OLAPMember> getFiltered(Hierarchy h, Level lvl){
		List<OLAPMember> l = new ArrayList<OLAPMember>();
		
		if (filters.get(h) == null){
			filters.put(h, new ArrayList<OLAPMember>());
		}
		
		List accpeted = filters.get(h);
		
//		if (enabled.get(lvl)){
			for(OLAPMember m : getOLAPMembers(h, lvl)){
				if (accpeted.contains(m)){
					l.add(m);
				}
			}
//		}
		
		
		return l;
	}

	public List<OLAPMember> getOLAPMembers(Hierarchy hierarchy, Level l) {
		return olapCube.getLastResult().getLevelMembers(hierarchy, l);
	}



	public List<OLAPMember> getFiltered() {
		List<OLAPMember> l = new ArrayList<OLAPMember>();
		
		for(Hierarchy h : filters.keySet()){
			if (filters.get(h) != null){
				
				for(OLAPMember m : filters.get(h)){
					if (enabled.get(m.getLevel().getUniqueName())){
						l.add(m);
					}
				}
				
			}
		}

		return l;
	}

	public void enable(Hierarchy h, boolean enabled) {
		enabledHiera.put(h, enabled);
		
		
	}

	public void setLevelFilters(Hierarchy hiera, Level lvl,	List<OLAPMember> checked) {
		if (filters.get(hiera) == null){
			filters.put(hiera, new ArrayList<OLAPMember>());
		}
		
		List<OLAPMember> toRemove = new ArrayList<OLAPMember>();
		for(OLAPMember m : filters.get(hiera)){
			if (m.getLevel().getUniqueName().equals(lvl.getUniqueName())){
				toRemove.add(m);
			}
		}
		 filters.get(hiera).removeAll(toRemove);
		 filters.get(hiera).addAll(checked);
	}
	
}
