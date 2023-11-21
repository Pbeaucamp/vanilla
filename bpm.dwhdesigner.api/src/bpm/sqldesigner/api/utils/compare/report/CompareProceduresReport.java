package bpm.sqldesigner.api.utils.compare.report;

import bpm.sqldesigner.api.model.procedure.SQLProcedure;

public class CompareProceduresReport extends Report {

	public static final int FULLMATCHES = 100;

	protected SQLProcedure procedureA;
	protected SQLProcedure procedureB;

	private boolean valuesMatch;

	private final static int[] COEFS = { 1, // namesMatch
			3 // valuesMatch
	};

	public CompareProceduresReport(SQLProcedure tableA, SQLProcedure tableB) {
		procedureA = tableA;
		procedureB = tableB;
	}

	public CompareProceduresReport() {
	}

	@Override
	public int evaluateMatches() {
		score = 0;
		if (namesMatch)
			score += COEFS[0];
		if (valuesMatch)
			score += COEFS[1];

		return (score * FULLMATCHES) / getSum();
	}

	public int getSum() {
		return COEFS[0] + COEFS[1];
	}

	public SQLProcedure getProcedureA() {
		return procedureA;
	}

	public SQLProcedure getProcedureB() {
		return procedureB;
	}

	public boolean getValuesMatch() {
		return valuesMatch;
	}

	public void setValuesMatch(boolean valuesMatch) {
		this.valuesMatch = valuesMatch;
	}

}
