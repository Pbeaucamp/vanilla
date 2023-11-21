package bpm.sqldesigner.api.utils.compare.report;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.model.Catalog;

public class CompareCatalogsReport extends Report {

	public static final int FULLMATCHES = 100;

	protected Catalog catalogA;
	protected Catalog catalogB;

	private List<CompareSchemasReport> schemasReports = new ArrayList<CompareSchemasReport>();
	private List<Integer> schemasReportsScore = new ArrayList<Integer>();

	private static final int[] COEFS = { 1,// namesMatch
			3 // Each schemasReport
	};

	public CompareCatalogsReport() {

	}

	public CompareCatalogsReport(Catalog catalogA, Catalog catalogB) {
		this.catalogA = catalogA;
		this.catalogB = catalogB;
	}

	@Override
	public int evaluateMatches() {
		score = 0;
		if (namesMatch)
			score += COEFS[0] * CompareSchemasReport.FULLMATCHES;

		for (CompareSchemasReport schemaReport : schemasReports) {
			int evaluation = schemaReport.evaluateMatches();
			schemasReportsScore.add(evaluation);

			score += evaluation * COEFS[1];
		}

		return (score * FULLMATCHES)
				/ (getSum() * CompareSchemasReport.FULLMATCHES);
	}

	public Catalog getCatalogA() {
		return catalogA;
	}

	public int getSum() {
		return COEFS[0] + COEFS[1] * schemasReports.size();
	}

	public Catalog getCatalogB() {
		return catalogB;
	}

	public List<CompareSchemasReport> getSchemasReports() {
		return schemasReports;
	}

	public void addSchemasReport(CompareSchemasReport schemaReport) {
		schemasReports.add(schemaReport);
	}

	public List<Integer> getSchemasReportsScore() {
		return schemasReportsScore;
	}

	@Override
	public List<Report> getReportsChanges(List<Report> report) {

		if (score == -1)
			evaluateMatches();

		int j = 0;
		for (Integer currentScoreSchema : schemasReportsScore) {
			if (currentScoreSchema != CompareSchemasReport.FULLMATCHES) {
				CompareSchemasReport schemaReport = schemasReports.get(j);
				schemaReport.getReportsChanges(report);
			}
			j++;
		}

		report.add(this);
		return report;
	}
}
