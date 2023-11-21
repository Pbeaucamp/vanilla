package bpm.sqldesigner.api.utils.migration;

import java.util.ArrayList;
import java.util.List;

import bpm.sqldesigner.api.database.CreateData;
import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.model.Catalog;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;
import bpm.sqldesigner.api.model.view.SQLView;

public class CreateRequestFactory {

	public static List<RequestStatement> createRequest(Node node) {
		List<RequestStatement> listRequests = new ArrayList<RequestStatement>();
		if (node instanceof Column)
			listRequests.addAll(CreateData.createColumn((Column) node));
		else if (node instanceof Table)
			listRequests.add(CreateData.createTable((Table) node));
		else if (node instanceof Schema)
			listRequests.addAll(CreateData.createSchema((Schema) node));
		else if (node instanceof SQLView)
			listRequests.add(CreateData.createView((SQLView) node));
		else if (node instanceof Catalog)
			listRequests.addAll(CreateData.createCatalog((Catalog) node));

		return listRequests;
	}

}
