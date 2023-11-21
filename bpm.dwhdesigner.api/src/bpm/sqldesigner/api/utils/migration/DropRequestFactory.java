package bpm.sqldesigner.api.utils.migration;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.database.DropData;
import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.procedure.SQLProcedure;
import bpm.sqldesigner.api.model.view.SQLView;

public class DropRequestFactory {
	public static List<RequestStatement> createRequest(Node node) {
		List<RequestStatement> listRequests = new ArrayList<RequestStatement>();
		if (node instanceof Column)
			listRequests.add(DropData.dropColumn((Column) node));
		else if (node instanceof Table)
			listRequests.add(DropData.dropTable((Table) node));
		else if (node instanceof Schema)
			listRequests.add(DropData.dropSchema((Schema) node));
		else if (node instanceof Catalog)
			listRequests.add(DropData.dropCatalog((Catalog) node));
		else if (node instanceof SQLProcedure) {
			RequestStatement procedure = DropData
					.dropProcedure((SQLProcedure) node);
			if (procedure != null)
				listRequests.add(procedure);
			else
				listRequests = null;
		} else if (node instanceof SQLView)
			listRequests.add(DropData.dropView((SQLView) node));
		return listRequests;
	}

}
