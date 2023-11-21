package bpm.vanilla.platform.core.beans.resources;

public abstract class RuleExclusionValue implements IRule {

	private static final long serialVersionUID = 1L;
	
	private boolean hasExclusionValue;
	private String exclusionValue;
	
	public void setHasExclusionValue(boolean hasExclusionValue) {
		this.hasExclusionValue = hasExclusionValue;
	}
	
	public boolean hasExclusionValue() {
		return hasExclusionValue;
	}
	
	public void setExclusionValue(String exclusionValue) {
		this.exclusionValue = exclusionValue;
	}
	
	public String getExclusionValue() {
		return exclusionValue;
	}
}
