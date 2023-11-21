package bpm.vanilla.platform.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

public class RuleColumnComparison implements IRule {

	private static final long serialVersionUID = 1L;

	private List<RuleColumnComparisonChild> childs;
	
	private boolean equalToValue;
	private String mainValue;

	//We keep this for retrocompatibility
	private String fieldName;
	private OperatorClassic operator;
	private String value;
	
	public RuleColumnComparison() { }
	
	public List<RuleColumnComparisonChild> getChilds() {
		if (childs == null && value != null && !value.isEmpty()) {
			childs = new ArrayList<RuleColumnComparisonChild>();
			childs.add(new RuleColumnComparisonChild(fieldName, operator, value));
		}
		return childs;
	}
	
	public void addChild(RuleColumnComparisonChild child) {
		if (childs == null) {
			this.childs = new ArrayList<RuleColumnComparisonChild>();
		}
		this.childs.add(child);
	}

	public void setChilds(List<RuleColumnComparisonChild> childs) {
		this.childs = childs;
	}
	
	public boolean isEqualToValue() {
		return equalToValue;
	}
	
	public void setEqualToValue(boolean equalToValue) {
		this.equalToValue = equalToValue;
	}
	
	public String getMainValue() {
		return mainValue;
	}
	
	public void setMainValue(String mainValue) {
		this.mainValue = mainValue;
	}
	
	@Override
	public boolean match(IRule rule) {
		// Not impletemented for now as the rule cannot exist in a schema
		return false;
	}
}
