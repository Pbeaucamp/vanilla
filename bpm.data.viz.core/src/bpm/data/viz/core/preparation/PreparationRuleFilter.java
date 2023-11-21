package bpm.data.viz.core.preparation;


public class PreparationRuleFilter extends PreparationRule {

	private static final long serialVersionUID = 1L;
	
	private String filter;

	public PreparationRuleFilter() {
		type = RuleType.FILTER;
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	
}
