package bpm.vanilla.platform.core.beans.resources;

import java.util.HashSet;

public class RulePatternComparison implements IRule {

	private static final long serialVersionUID = 1L;
	
	private String regex;
	private boolean errorIfMatch = false;
	
	public RulePatternComparison() { }
	
	public String getRegex() {
		return regex;
	}
	
	public void setRegex(String regex) {
		this.regex = regex;
	}
	
	public boolean isErrorIfMatch() {
		return errorIfMatch;
	}
	
	public void setErrorIfMatch(boolean errorIfMatch) {
		this.errorIfMatch = errorIfMatch;
	}
	
	@Override
	public boolean match(IRule rule) {
		if (rule != null && rule instanceof RulePatternComparison) {
			if (getRegex() != null && ((RulePatternComparison) rule).getRegex() != null) {
				return getRegex().equals(((RulePatternComparison) rule).getRegex()) && isErrorIfMatch() == ((RulePatternComparison) rule).isErrorIfMatch();
			}
		}
		return false;
	}
}
