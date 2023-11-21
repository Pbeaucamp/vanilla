package bpm.vanilla.platform.core;


import java.io.InputStream;
import java.util.HashMap;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

public interface IExcelManager {

	public static enum ActionType implements IXmlActionType{
		GET_DRIVERS(Level.DEBUG), GET_COLUMNTYPE(Level.DEBUG), GET_TABLES(Level.DEBUG), GET_COLUMNS(Level.DEBUG), TESTCONNECT_SERVER(Level.DEBUG), CREATE_TABLE(Level.DEBUG), ADD_CONTRACT(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public String getListDriver() throws Exception;
	
	public String getColumnType(HashMap<String, String> documentParameters)throws Exception;
	
	public String getListTables(HashMap<String, String> documentParameters) throws Exception;
	
	public String getListColumns(HashMap<String, String> documentParameters) throws Exception;
	
	public String createTable (String xmlTable, HashMap<String, String> documentParameters) throws Exception;
	
	public String testConnectionDatabase(HashMap<String, String> documentParameters) throws Exception;
	
	public String loaderExcel(String name, String file, InputStream in, IRepositoryContext ctx, HashMap<String, String> documentParameters) throws Exception;
	
}
