package bpm.data.viz.core.preparation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.data.viz.core.preparation.PreparationRule.RuleType;




public class PreparationRuleAffecter extends PreparationRule implements Serializable {
	


	private static final long serialVersionUID = 1L;

	private List<Plage> groups = new ArrayList<>();
	private HashMap<Serializable, Serializable> mappings = new HashMap<>();
	
	public PreparationRuleAffecter() {
		type = RuleType.AFFECTER;
	}

	
	
	public static class Plage implements Serializable{
		
		private static final long serialVersionUID = 1L;

		
		Integer Min=0;
		
		Integer Max=0;
		
		
	

		 public Plage() {
		
		}

		public Integer getMin() {
			return Min;
		}

		public void setMin(Integer min) {
			Min = min;
		}

		public Integer getMax() {
			return Max;
		}

		public void setMax(Integer max) {
			Max = max;
		}

	
		
		
		
	}
	public HashMap<Serializable, Serializable> getMappings() {
		return mappings;
	}

	public void setMappings(HashMap<Serializable, Serializable> mappings) {
		this.mappings = mappings;
	}

	public List<Plage> getGroups() {
		return groups;
	}

	public void setGroups(List<Plage> groups) {
		this.groups = groups;
	}
	
	public List<String> getGroupsAsString() {
		return new ArrayList(groups);
	


	}
}
