package bpm.sqldesigner.api.utils.compare.report;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;

public class CompareSchemasReport extends Report {

	public static final int FULLMATCHES = 100;

	protected Schema schemaA;
	protected Schema schemaB;

	private List<CompareTablesReport> tablesReports = new ArrayList<CompareTablesReport>();
	private List<Integer> tablesReportsScores = new ArrayList<Integer>();

	private List<CompareViewsReport> viewsReports = new ArrayList<CompareViewsReport>();
	private List<Integer> viewsReportsScores = new ArrayList<Integer>();

	private List<CompareProceduresReport> proceduresReports = new ArrayList<CompareProceduresReport>();
	private List<Integer> proceduresReportsScores = new ArrayList<Integer>();

	private static final int[] COEFS = { 1, // namesMatch
			3 // For each tablesReport
			, 3 // For each viewsReport
			, 3 // For each proceduresReport
	};
	public CompareSchemasReport() {

	}
	
	public CompareSchemasReport(Schema schemaA, Schema schemaB) {
		this.schemaA = schemaA;
		this.schemaB = schemaB;
	}

	@Override
	public int evaluateMatches() {
		score = 0;
		if (namesMatch)
			score += COEFS[0] * CompareTablesReport.FULLMATCHES;

		for (CompareTablesReport tablesReport : tablesReports) {
			int evaluation = tablesReport.evaluateMatches();
			tablesReportsScores.add(evaluation);

			score += evaluation * COEFS[1];
		}
		for (CompareViewsReport viewsReport : viewsReports) {
			int evaluation = viewsReport.evaluateMatches();
			viewsReportsScores.add(evaluation);

			score += evaluation * COEFS[2];
		}

		for (CompareProceduresReport proceduresReport : proceduresReports) {
			int evaluation = proceduresReport.evaluateMatches();
			proceduresReportsScores.add(evaluation);

			score += evaluation * COEFS[3];
		}
		int sum = COEFS[0] + COEFS[1] * tablesReports.size() + COEFS[2]
				* viewsReports.size() + COEFS[3] * proceduresReports.size();
		
		score = (score * FULLMATCHES) / (sum * CompareTablesReport.FULLMATCHES);

		return score;
	}

	public List<CompareTablesReport> getTablesReports() {
		return tablesReports;
	}

	public Schema getSchemaA() {
		return schemaA;
	}

	public Schema getSchemaB() {
		return schemaB;
	}

	public void addTablesReport(CompareTablesReport compareTables) {
		tablesReports.add(compareTables);
	}

	public void addViewsReport(CompareViewsReport compareViews) {
		viewsReports.add(compareViews);
	}

	public List<Integer> getTablesReportsScore() {
		return tablesReportsScores;
	}

	public void addProceduresReport(CompareProceduresReport procedureReport) {
		proceduresReports.add(procedureReport);
	}

	@Override
	public List<Report> getReportsChanges(List<Report> report) {
		
		if (score == -1)
			evaluateMatches();
		
		
		int x = 0;
		for (Integer currentScoreTable : tablesReportsScores) {
			if (currentScoreTable != CompareTablesReport.FULLMATCHES) {
				CompareTablesReport tablesReport = tablesReports.get(x);
				tablesReport.getReportsChanges(report);
				report.add(tablesReport);
			}
			x++;
		}
		x = 0;
		for (Integer currentScoreView : viewsReportsScores) {
			if (currentScoreView != CompareViewsReport.FULLMATCHES) {
				CompareViewsReport viewsReport = viewsReports.get(x);
				report.add(viewsReport);
			}
			x++;
		}
		x = 0;
		for (Integer currentScoreProcedure : proceduresReportsScores) {
			if (currentScoreProcedure != CompareProceduresReport.FULLMATCHES) {
				CompareProceduresReport proceduresReport = proceduresReports
						.get(x);
				report.add(proceduresReport);
			}
			x++;
		}
		return report;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
//		List<RequestStatement> listRequests = null;
//		RequestStatement request = null;
//
//		boolean isTarget = true;
//
//		if (toNode instanceof DatabaseCluster) {
//			isTarget = schemaA.getClusterName().equals(toNode.getName());
//		} else if (toNode instanceof Catalog) {
//			isTarget = schemaA.getCatalog().equals(toNode);
//		} else if (toNode instanceof Schema) {
//			isTarget = schemaA.equals(toNode);
//		}
//
//		if (isTarget)
//			request = NameUpdateRequestFactory.createRequest(schemaB, schemaA
//					.getName());
//		else
//			request = NameUpdateRequestFactory.createRequest(schemaA, schemaB
//					.getName());
//
//		listRequests.add(request);
//
//		return listRequests;
		
		return null;
	}
}
