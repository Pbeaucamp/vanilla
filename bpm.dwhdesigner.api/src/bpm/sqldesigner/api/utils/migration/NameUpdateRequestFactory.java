package bpm.sqldesigner.api.utils.migration;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.database.UpdateData;
import bpm.sqldesigner.api.exception.UpdateColumnException;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.Schema;
import bpm.sqldesigner.api.model.Table;

public class NameUpdateRequestFactory {

	public static RequestStatement createRequest(Node node, String newName) {
		RequestStatement requestStatement = null;
		if (node instanceof Column) {
			try {
				requestStatement = UpdateData.updateColumnName((Column) node,
						newName, null);
			} catch (UpdateColumnException e) {
				return null;
			}
		}

		else if (node instanceof Table)
			requestStatement = UpdateData.updateTableName((Table) node,
					newName, null);
		else if (node instanceof Schema)
			requestStatement = UpdateData.updateSchemaName((Schema) node,
					newName, null);
		return requestStatement;
	}
}
