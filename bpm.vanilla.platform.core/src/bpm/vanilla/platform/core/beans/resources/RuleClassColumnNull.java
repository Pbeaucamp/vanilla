package bpm.vanilla.platform.core.beans.resources;

import java.util.HashSet;
import java.util.List;

public class RuleClassColumnNull implements IRule {

	private static final long serialVersionUID = 1L;

	private List<String> fieldNames;

	public RuleClassColumnNull() {
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	@Override
	public boolean match(IRule rule) {
		if (rule != null && rule instanceof RuleClassColumnNull) {
			if (getFieldNames() != null && ((RuleClassColumnNull) rule).getFieldNames() != null) {
				return new HashSet<String>(getFieldNames()).equals(new HashSet<String>(((RuleClassColumnNull) rule).getFieldNames()));
			}
		}
		return false;
	}
}
