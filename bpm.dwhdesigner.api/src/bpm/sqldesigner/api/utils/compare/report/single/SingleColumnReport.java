package bpm.sqldesigner.api.utils.compare.report.single;

import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.utils.compare.report.CompareColumnsReport;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.CreateRequestFactory;
import bpm.sqldesigner.api.utils.migration.DropRequestFactory;

public class SingleColumnReport extends CompareColumnsReport {

	private Table otherTable;

	public SingleColumnReport(Column column) {
		columnA = column;
		columnB = null;
	}

	@Override
	public int evaluateMatches() {
		return 0;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
		List<RequestStatement> listRequests = null;

		boolean isTarget = true;
		Table fromTable = null;

		if (toNode instanceof DatabaseCluster) {
			isTarget = columnA.getClusterName().equals(toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = columnA.getTable().getSchema().getCatalog().equals(
					toNode);
		} else if (toNode instanceof Schema) {
			isTarget = columnA.getTable().getSchema().equals(toNode);

		} else if (toNode instanceof Table) {
			isTarget = columnA.getTable().equals(toNode);
		} else if (toNode instanceof Column) {
			isTarget = columnA.equals(toNode);
		}

		if (isTarget) {
			final Column column = new Column(columnA);
			column.setNotFullLoaded(false);

			column.setTable(otherTable);
			listRequests = CreateRequestFactory.createRequest(column);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					otherTable.addColumn(column);
				}
			};
		} else {
			listRequests = DropRequestFactory.createRequest(columnA);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					columnA.getTable().removeColumn(columnA.getName());
				}
			};
		}

		return listRequests;
	}

	public void setOtherTable(Table table) {
		otherTable = table;
	}
}
