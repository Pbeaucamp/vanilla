package bpm.metadata.layer.logical.sql;

import bpm.metadata.layer.physical.sql.SQLConnection;

public class FactorySQLDataSource {

	private static FactorySQLDataSource  instance = null;
	
	private FactorySQLDataSource(){}
	
	public static FactorySQLDataSource getInstance(){
		if (instance == null){
			instance = new FactorySQLDataSource();
		}
		
		return instance;
	}
	
	/**
	 * create a logical DataSource from an SQLConnection
	 * all tables, views and aliases are imported
	 * @param sock
	 * @return
	 * @throws Exception
	 */
	public SQLDataSource createDataSource(SQLConnection sock) throws Exception{
		SQLDataSource ds = new SQLDataSource(sock);
		
//		for(ITable t : sock.connect()){
//			SQLDataStream table = new SQLDataStream((SQLTable)t);
//			ds.add(table);
//		}
		
		return ds;
	}
}
