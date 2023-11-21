package bpm.fd.api.core.model.datas;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import bpm.fd.api.core.model.datas.odaconsumer.ConnectionManager;
import bpm.vanilla.platform.core.beans.Group;

public class QueryHelper {
	/**
	 * 
	 * @param dataSource
	 * @param dataSet
	 * @return an already prepared IQuery
	 * @throws Exception
	 * 
	 * TODO: this method should be changed by sending a context object for the IQuery.setAppContext
	 * to allow to set the Vanilla Group, as it is not the case,
	 * users MUST call this method and call another prepare on the IQuery before
	 * executing it or set parameters 
	 */
	public static IQuery buildquery(DataSource dataSource, DataSet dataSet) throws Exception{

		IConnection c = ConnectionManager.openConnection(dataSource).getOdaConnection();
		
		dataSource.addOpenedConnection(c);
		IQuery q = null;
		try {
			q = c.newQuery(dataSet.getOdaExtensionDataSetId());
		} catch(Exception e) {
//			if(c.getClass().getName().equals("org.eclipse.birt.report.data.oda.jdbc")) {
				c.close();
				c = ConnectionManager.openConnection(dataSource).getOdaConnection();
				q = c.newQuery("");
//			}
//			else {
//				e.printStackTrace();
//				throw e;
//			}
		}

		q.prepare(dataSet.getQueryText());
		return q;
	}
	
	public static IQuery buildquery(DataSource dataSource, DataSet dataSet, Group group) throws Exception {
		IConnection c = ConnectionManager.openConnection(dataSource).getOdaConnection();
		
		dataSource.addOpenedConnection(c);
		IQuery q = null;
		try {
			q = c.newQuery(dataSet.getOdaExtensionDataSetId());
		} catch(Exception e) {
//			if(c.getClass().getName().equals("org.eclipse.birt.report.data.oda.jdbc")) {
				c.close();
				c = ConnectionManager.openConnection(dataSource).getOdaConnection();
				q = c.newQuery("");
//			}
//			else {
//				e.printStackTrace();
//				throw e;
//			}
		}
		q.setAppContext(group);
		q.prepare(dataSet.getQueryText());
		return q;
	}
}
