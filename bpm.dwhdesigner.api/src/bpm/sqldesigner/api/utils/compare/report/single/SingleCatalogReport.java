package bpm.sqldesigner.api.utils.compare.report.single;

import java.util.List;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.DatabaseCluster;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.utils.compare.report.CompareCatalogsReport;
import bpm.sqldesigner.api.utils.migration.CanApplyChanges;
import bpm.sqldesigner.api.utils.migration.CreateRequestFactory;
import bpm.sqldesigner.api.utils.migration.DropRequestFactory;

public class SingleCatalogReport extends CompareCatalogsReport {

	private DatabaseCluster otherCluster;

	public SingleCatalogReport(Catalog catalog) {
		catalogA = catalog;
		catalogB = null;
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
			isTarget = catalogA.getDatabaseCluster().getName().equals(
					toNode.getName());
		} else if (toNode instanceof Catalog) {
			isTarget = catalogA.equals(toNode);
		}

		if (isTarget) {
			final Catalog catalog = new Catalog(catalogA);
			catalog.setDatabaseCluster(otherCluster);
			catalog.setNotFullLoaded(false);

			listRequests = CreateRequestFactory.createRequest(catalog);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					otherCluster.addCatalog(catalog);
				}
			};
		} else {
			listRequests = DropRequestFactory.createRequest(catalogA);
			applyChanges = new CanApplyChanges() {
				public void applyChanges() {
					catalogA.getCluster().removeCatalog(catalogA);
				}
			};
		}

		return listRequests;
	}

	public void setOtherCluster(DatabaseCluster cluster) {
		otherCluster = cluster;
	}
}
