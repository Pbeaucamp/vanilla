package bpm.sqldesigner.api.utils.migration;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.database.UpdateData;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.Node;

public class TypeUpdateRequestFactory {

	public static RequestStatement createRequest(Node node, String typeString) {
		RequestStatement requestStatement = null;
		if (node instanceof Column)
			requestStatement = UpdateData.updateColumnType((Column) node,
					typeString);
		return requestStatement;
	}

}
