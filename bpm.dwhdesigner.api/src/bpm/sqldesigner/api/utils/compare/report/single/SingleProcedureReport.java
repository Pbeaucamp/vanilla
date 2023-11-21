package bpm.sqldesigner.api.utils.compare.report.single;

import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.utils.compare.report.CompareProceduresReport;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.DropRequestFactory;

public class SingleProcedureReport extends CompareProceduresReport {

	public SingleProcedureReport(SQLProcedure procedure) {
		procedureA = procedure;
		procedureB = null;
	}

	@Override
	public int evaluateMatches() {
		return 0;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
		List<RequestStatement> listRequests = null;

		boolean isTarget = true;

		if (toNode instanceof DatabaseCluster) {
			isTarget = procedureA.getClusterName().equals(toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = procedureA.getSchema().getCatalog().equals(toNode);
		} else if (toNode instanceof Schema) {
			isTarget = procedureA.getSchema().equals(toNode);
		} else if (toNode instanceof SQLProcedure) {
			isTarget = procedureA.equals(toNode);
		}

		if (isTarget)
			listRequests = null;
		// TODO request = CreateRequestFactory.createRequest(procedureA);
		else {
			listRequests = DropRequestFactory.createRequest(procedureA);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					procedureA.getSchema()
							.removeProcedure(procedureA.getName());
				}
			};
		}

		return listRequests;
	}

}
