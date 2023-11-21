package bpm.oda.driver.reader.model.dataset;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.oda.driver.reader.model.datasource.DataSource;
import bpm.oda.driver.reader.model.odaconsummer.ConnectionManager;


public class QueryHelper {
	public static IQuery buildquery(DataSource dataSource, DataSet dataSet) throws Exception{

		//IConnection c = ConnectionManager.openConnection(dataSource).getOdaConnection();
		IConnection c = ConnectionManager.buildConnection(dataSource).getOdaConnection();
		
		dataSet.setConnectionDataSet(c);
		dataSource.addOpenedConnection(c);
		
		IQuery q = c.newQuery("");
		
		q.prepare(dataSet.getQueryText());

		return q;
	}
}
