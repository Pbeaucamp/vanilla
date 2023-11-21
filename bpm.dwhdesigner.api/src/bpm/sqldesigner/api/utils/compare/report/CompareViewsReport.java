package bpm.sqldesigner.api.utils.compare.report;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.ValueUpdateRequestFactory;

public class CompareViewsReport extends Report {

	public static final int FULLMATCHES = 100;

	protected SQLView viewA;
	protected SQLView viewB;

	private boolean valuesMatch;

	private final static int[] COEFS = { 1, // namesMatch
			3 // valuesMatch
	};

	public CompareViewsReport(SQLView tableA, SQLView tableB) {
		viewA = tableA;
		viewB = tableB;
	}

	public CompareViewsReport() {
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

	public SQLView getViewA() {
		return viewA;
	}

	public SQLView getViewB() {
		return viewB;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {

		if (score == -1)
			evaluateMatches();

		RequestStatement request = null;
		boolean isTarget = true;

		if (toNode instanceof DatabaseCluster) {
			isTarget = viewA.getClusterName().equals(toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = viewA.getSchema().getCatalog().equals(toNode);
		} else if (toNode instanceof Schema) {
			isTarget = viewA.getName().equals(toNode);
		} else if (toNode instanceof SQLView) {
			isTarget = viewA.equals(toNode);
		}

		if (!valuesMatch) {
			if (isTarget) {
				request = ValueUpdateRequestFactory.createRequest(viewB, viewA
						.getValue());
				applyChanges = new CanApplyChanges() {

					public void applyChanges() {
						viewB.setValue(viewA.getValue());
					}
				};
			} else {
				request = ValueUpdateRequestFactory.createRequest(viewA, viewB
						.getValue());
				applyChanges = new CanApplyChanges() {

					public void applyChanges() {
						viewA.setValue(viewB.getValue());
					}
				};
			}
		}

		List<RequestStatement> listRequests = new ArrayList<RequestStatement>();
		listRequests.add(request);
		return listRequests;
	}

	public boolean getValuesMatch() {
		return valuesMatch;
	}

	public void setValuesMatch(boolean valuesMatch) {
		this.valuesMatch = valuesMatch;
	}

}
