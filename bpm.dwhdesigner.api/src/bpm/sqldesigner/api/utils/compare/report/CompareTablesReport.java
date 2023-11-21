package bpm.sqldesigner.api.utils.compare.report;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;

public class CompareTablesReport extends Report {

	public static final int FULLMATCHES = 100;

	protected Table tableA;
	protected Table tableB;

	private List<CompareColumnsReport> columnsReports = new ArrayList<CompareColumnsReport>();
	private List<Integer> columnsReportsScore = new ArrayList<Integer>();

	private final static int[] COEFS = { 1, // namesMatch
			3 // Each columnsReport
	};

	public CompareTablesReport(Table tableA, Table tableB) {
		this.tableA = tableA;
		this.tableB = tableB;
	}

	public CompareTablesReport() {
	}

	public void addColumnsReport(CompareColumnsReport columnsReport) {
		columnsReports.add(columnsReport);
	}

	@Override
	public int evaluateMatches() {
		score = 0;
		if (namesMatch)
			score += COEFS[0] * CompareColumnsReport.FULLMATCHES;

		for (CompareColumnsReport columnsReport : columnsReports) {
			int evaluation = columnsReport.evaluateMatches();
			columnsReportsScore.add(evaluation);
			score += evaluation * COEFS[1];
		}

		int sum = COEFS[0] + COEFS[1] * columnsReports.size();
		score = (score * FULLMATCHES) / (sum * CompareColumnsReport.FULLMATCHES);
		return score;
	}

	public Table getTableA() {
		return tableA;
	}

	public Table getTableB() {
		return tableB;
	}

	public List<Integer> getColumnsReportsScore() {
		return columnsReportsScore;
	}

	public List<CompareColumnsReport> getColumnsColumnsReports() {
		return columnsReports;
	}

	@Override
	public List<Report> getReportsChanges(List<Report> report) {
		if (score == -1)
			evaluateMatches();
		
		
		int y = 0;
		for (Integer currentScoreColumn : columnsReportsScore) {
			if (currentScoreColumn != CompareColumnsReport.FULLMATCHES) {
				CompareColumnsReport columnReport = columnsReports.get(y);
				report.add(columnReport);
			}
			y++;
		}
//		if (!namesMatch) {
//			report.add(this);
//		}
		return report;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
		RequestStatement request = null;
		boolean isTarget = true;

		if (toNode instanceof DatabaseCluster) {
			isTarget = tableA.getClusterName().equals(toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = tableA.getSchema().getCatalog().equals(toNode);
		} else if (toNode instanceof Schema) {
			isTarget = tableA.getSchema().equals(toNode);
		} else if (toNode instanceof Table) {
			isTarget = tableA.equals(toNode);
		}

//		if (!namesMatch) {
//			if (isTarget)
//				request = NameUpdateRequestFactory.createRequest(tableB, tableA
//						.getName());
//			else
//				request = NameUpdateRequestFactory.createRequest(tableA, tableB
//						.getName());
//		}

		List<RequestStatement> listRequests = new ArrayList<RequestStatement>();
//		listRequests.add(request);
		return listRequests;
	}

}
