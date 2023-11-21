package bpm.united.olap.runtime.query;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
/**
 * An Helper class to run all Oda IQuery.
 * 
 * Using it will allow to make a second try if a run failed because of a bad fetchSize parameter
 * 
 * @author ludo
 *
 */
public class OdaQueryRunner {

	/**
	 * all ODA queries should be run from this method.
	 * It allows to run the query, but if the query fails, it try to run it by changing
	 * the fetchSizeRow property(some database like Vertica fail to run query because of this property)
	 * 
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public static IResultSet runQuery(IQuery query) throws Exception{
		
		IResultSet rs = null;
		
//		Logger.getLogger(OdaQueryRunner.class).info("Run query : " + query.getEffectiveQueryText());
		
		try{
			rs = query.executeQuery();
		}catch(Exception ex){
			Logger.getLogger(OdaQueryRunner.class).warn("Failed to run Query because " + ex.getMessage() + ". Try to set rowFtechSize at 0");
			query.setProperty("rowFetchSize", "0");
			rs = query.executeQuery();
		}
		
		return rs;
	}
}
