package bpm.data.viz.core.preparation;


public class PreparationRuleSort extends PreparationRule {

	private static final long serialVersionUID = 1L;

	public enum SortType {
		ASC,DESC;
	}
	
	public PreparationRuleSort() {
		type = RuleType.SORT;
		multiColumn = true;
	}
	
	private SortType sortType;

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getColumn() == null) ? 0 : getColumn().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PreparationRuleSort other = (PreparationRuleSort) obj;
		if(getColumn().equals(other.getColumn()))
			return true;
		return false;
	}
	
	

}
