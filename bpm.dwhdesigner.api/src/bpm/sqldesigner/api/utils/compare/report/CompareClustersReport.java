package bpm.sqldesigner.api.utils.compare.report;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.model.DatabaseCluster;

public class CompareClustersReport extends Report {

	public static final int FULLMATCHES = 100;

	private DatabaseCluster databaseClusterA;
	private DatabaseCluster databaseClusterB;

	private List<CompareCatalogsReport> catalogsReports = new ArrayList<CompareCatalogsReport>();
	private List<Integer> catalogsReportsScore = new ArrayList<Integer>();

	private static final int[] COEFS = { 0,// namesMatch
			3 // Each catalogsReport
	};

	public CompareClustersReport(DatabaseCluster catalogsListA,
			DatabaseCluster catalogsListB) {
		databaseClusterA = catalogsListA;
		databaseClusterB = catalogsListB;
	}

	@Override
	public int evaluateMatches() {
		score = 0;
		if (namesMatch)
			score += COEFS[0] * CompareCatalogsReport.FULLMATCHES;

		for (CompareCatalogsReport catalogsReport : catalogsReports) {
			int evaluation = catalogsReport.evaluateMatches();
			catalogsReportsScore.add(evaluation);

			score += evaluation * COEFS[1];
		}

		int sum = COEFS[0] + COEFS[1] * catalogsReports.size();

		score = (score * FULLMATCHES)
				/ (sum * CompareCatalogsReport.FULLMATCHES);
		return score;
	}

	public List<CompareCatalogsReport> getCatalogsReports() {
		return catalogsReports;
	}

	public void addCatalogsReport(CompareCatalogsReport catalogsReport) {
		catalogsReports.add(catalogsReport);
	}

	@Override
	public List<Report> getReportsChanges(List<Report> report) {

		if (score == -1)
			evaluateMatches();

		int i = 0;
		for (Integer currentScore : catalogsReportsScore) {
			if (currentScore != CompareCatalogsReport.FULLMATCHES) {
				CompareCatalogsReport catalogsReport = catalogsReports.get(i);

				catalogsReport.getReportsChanges(report);
			}
			i++;
		}
		return report;
	}

}
