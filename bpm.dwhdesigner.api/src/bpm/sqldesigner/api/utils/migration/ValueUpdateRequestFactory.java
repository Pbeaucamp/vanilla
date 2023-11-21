package bpm.sqldesigner.api.utils.migration;

import bpm.sqldesigner.api.database.RequestStatement;
import bpm.sqldesigner.api.database.UpdateData;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.api.model.view.SQLView;

public class ValueUpdateRequestFactory {
	public static RequestStatement createRequest(Node node, String valueString) {
		RequestStatement requestStatement = null;
		if (node instanceof SQLView)
			requestStatement = UpdateData.updateViewValue((SQLView) node,
					valueString);
		return requestStatement;
	}
}
