package bpm.gateway.core.transformations.inputs.odaconsumer;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.gateway.core.transformations.inputs.OdaInput;

public class QueryHelper {
	public static IQuery buildquery(OdaInput input) throws Exception{

		IConnection c = ConnectionManager.openConnection(input).getOdaConnection();
		IQuery q = c.newQuery("");
		q.prepare(input.getQueryText());
		return q;
	}
}
