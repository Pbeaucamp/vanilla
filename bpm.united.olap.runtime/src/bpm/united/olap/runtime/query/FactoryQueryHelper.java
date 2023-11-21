package bpm.united.olap.runtime.query;

import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class FactoryQueryHelper {

	public static String JDBC = "org.eclipse.birt.report.data.oda.jdbc";
	public static String FMDT = "bpm.metadata.birt.oda.runtime";
	
	
	public static IQueryHelper getQueryHelper(IVanillaLogger logger, IRuntimeContext runtimeContext, String odaType) {
		
		if(odaType.equals(JDBC)) {
			return new JDBCQueryHelper(logger, runtimeContext);
		}
		else if(odaType.equals(FMDT)) {
			return new FMDTQueryHelper(logger, runtimeContext);
		}
		return new QueryHelper(logger, runtimeContext);
	}
	
}
