package bpm.sqldesigner.api.utils.compare.report.single;

import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.utils.compare.report.CompareTablesReport;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.CreateRequestFactory;
import bpm.sqldesigner.api.utils.migration.DropRequestFactory;

public class SingleTableReport extends CompareTablesReport {

	private Schema otherSchema;

	public SingleTableReport(Table table) {
		tableA = table;
		tableB = null;
	}

	@Override
	public int evaluateMatches() {
		return 0;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
		List<RequestStatement> listRequest = null;

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

		if (isTarget) {
			final Table table = new Table(tableA);
			table.setNotFullLoaded(false);

			table.setSchema(otherSchema);
			listRequest = CreateRequestFactory.createRequest(table);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					otherSchema.addTable(table);
				}
			};
		} else {
			listRequest = DropRequestFactory.createRequest(tableA);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					tableA.getSchema().removeTable(tableA.getName());
				}
			};
		}
		return listRequest;
	}

	public void setOtherSchema(Schema schema) {
		otherSchema = schema;
	}

}
