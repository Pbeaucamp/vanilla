package bpm.vanilla.platform.core.beans.fmdt;

import java.util.ArrayList;
import java.util.List;

public class FmdtAggregate extends FmdtData {

	private static final long serialVersionUID = 1L;

	private String function = null;
	private String col;
	private String table = null;
	private String outputName;
	private boolean basedOnFormula = false;
	private String operator;

	private List<String> formulaDataStreamInvolved = new ArrayList<String>();

	public FmdtAggregate() {
		super();
	}

	public FmdtAggregate(String function, String col, String table, String outputName, String description, boolean basedOnFormula, String operator, List<String> formulaDataStreamInvolved) {
		super(outputName, outputName, description);
		this.function = function;
		this.col = col;
		this.table = table;
		this.outputName = outputName;
		this.basedOnFormula = basedOnFormula;
		this.operator = operator;
		this.formulaDataStreamInvolved = formulaDataStreamInvolved;
	}

	public FmdtAggregate(String col, String table, String outputName, String description, String operator) {
		super(outputName, outputName, description);
		this.col = col;
		this.table = table;
		this.outputName = outputName;
		this.operator = operator;
	}

	public FmdtAggregate(String function, String outputName, String description, String operator, List<String> formulaDataStreamInvolved) {
		super(outputName, outputName, description);
		this.function = function;
		this.outputName = outputName;
		this.basedOnFormula = true;
		this.operator = operator;
		this.formulaDataStreamInvolved = formulaDataStreamInvolved;
	}

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
		setLabel(outputName);
	}

	public boolean isBasedOnFormula() {
		return basedOnFormula;
	}

	public void setBasedOnFormula(boolean basedOnFormula) {
		this.basedOnFormula = basedOnFormula;
	}

	public List<String> getFormulaData() {
		return formulaDataStreamInvolved;
	}

	public void setFormulaData(List<String> formulaDataStreamInvolved) {
		this.formulaDataStreamInvolved = formulaDataStreamInvolved;
	}

	public void addFormulaData(String data) {
		this.formulaDataStreamInvolved.add(data);
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FmdtAggregate other = (FmdtAggregate) obj;
		if (basedOnFormula != other.basedOnFormula)
			return false;
		if (col == null) {
			if (other.col != null)
				return false;
		}
		else if (!col.equals(other.col))
			return false;
		if (formulaDataStreamInvolved == null) {
			if (other.formulaDataStreamInvolved != null)
				return false;
		}
		else if (!formulaDataStreamInvolved.equals(other.formulaDataStreamInvolved))
			return false;
		if (function == null) {
			if (other.function != null)
				return false;
		}
		else if (!function.equals(other.function))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		}
		else if (!operator.equals(other.operator))
			return false;
		if (outputName == null) {
			if (other.outputName != null)
				return false;
		}
		else if (!outputName.equals(other.outputName))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		}
		else if (!table.equals(other.table))
			return false;
		return true;
	}

}
