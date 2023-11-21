package bpm.sqldesigner.api.utils.compare.report.single;

import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.utils.compare.report.CompareSchemasReport;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.CreateRequestFactory;
import bpm.sqldesigner.api.utils.migration.DropRequestFactory;

public class SingleSchemaReport extends CompareSchemasReport {

	private Catalog otherCatalog;

	public SingleSchemaReport(Schema schema) {
		schemaA = schema;
		schemaB = null;
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
			isTarget = schemaA.getClusterName().equals(toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = schemaA.getCatalog().equals(toNode);
		} else if (toNode instanceof Schema) {
			isTarget = schemaA.equals(toNode);
		}

		if (isTarget) {
			final Schema schema = new Schema(schemaA);
			schema.setNotFullLoaded(false);

			schema.setCatalog(otherCatalog);
			listRequests = CreateRequestFactory.createRequest(schema);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					otherCatalog.addSchema(schema);
				}
			};
		} else {
			listRequests = DropRequestFactory.createRequest(schemaA);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					otherCatalog.removeSchema(schemaA.getName());
				}
			};
		}

		return listRequests;
	}

	public void setOtherCatalog(Catalog otherCatalog) {
		this.otherCatalog = otherCatalog;
	}
}
