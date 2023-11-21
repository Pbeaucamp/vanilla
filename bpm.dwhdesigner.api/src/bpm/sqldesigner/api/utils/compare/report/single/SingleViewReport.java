package bpm.sqldesigner.api.utils.compare.report.single;

import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.view.SQLView;
import bpm.sqldesigner.api.utils.compare.report.CompareViewsReport;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.CreateRequestFactory;
import bpm.sqldesigner.api.utils.migration.DropRequestFactory;

public class SingleViewReport extends CompareViewsReport {

	private Schema otherSchema;

	public SingleViewReport(SQLView view) {
		viewA = view;
		viewB = null;
	}

	@Override
	public int evaluateMatches() {
		return 0;
	}

	@Override
	public List<RequestStatement> getRequests(Node toNode, Node fromNode) {
		List<RequestStatement> listRequests;

		boolean isTarget = true;

		if (toNode instanceof DatabaseCluster) {
			isTarget = viewA.getClusterName().equals(toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = viewA.getSchema().getCatalog().equals(toNode);
		} else if (toNode instanceof Schema) {
			isTarget = viewA.getSchema().equals(toNode);
		} else if (toNode instanceof SQLView) {
			isTarget = viewA.equals(toNode);
		}

		if (isTarget) {
			final SQLView view = new SQLView(viewA);
			view.setNotFullLoaded(false);
			view.setSchema(otherSchema);
			listRequests = CreateRequestFactory.createRequest(view);
			applyChanges = new CanApplyChanges(){
				public void applyChanges() {
					otherSchema.addView(view);
				}
			};
		} else{
			listRequests = DropRequestFactory.createRequest(viewA);
			applyChanges = new CanApplyChanges(){
				public void applyChanges() {
					viewA.getSchema().removeView(viewA.getName());
				}
			};
		}
		return listRequests;
	}

	public void setOtherSchema(Schema otherSchema) {
		this.otherSchema = otherSchema;
	}
}
