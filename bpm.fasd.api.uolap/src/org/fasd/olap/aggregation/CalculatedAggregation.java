package org.fasd.olap.aggregation;

public class CalculatedAggregation implements IMeasureAggregation {

	private String formula;
	
	private String level;

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@Override
	public String getFaXml() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("	<calculated-aggregation>\n");
		
		buf.append("				<formula>" + formula + "</formula>\n");
		buf.append("				<level>" + level + "</level>\n");
		
		buf.append("			</calculated-aggregation>\n");
		
		return buf.toString();
	}

	@Override
	public String getTreeLabel() {
		return "calculated";
	}
	
	
	
}
