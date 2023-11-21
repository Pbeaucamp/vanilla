package bpm.united.olap.api.model.aggregation;

public class CalculatedAggregation implements ILevelAggregation {

	private String level;
	private String formula;
	
	@Override
	public String getLevel() {
		return level;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
