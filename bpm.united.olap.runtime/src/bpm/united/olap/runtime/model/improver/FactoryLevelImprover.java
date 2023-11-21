package bpm.united.olap.runtime.model.improver;

import bpm.vanilla.platform.core.beans.data.OdaInput;

public class FactoryLevelImprover {

	public static String JDBC = "org.eclipse.birt.report.data.oda.jdbc";
	public static String FMDT = "bpm.metadata.birt.oda.runtime";
	
	public static LevelImprover createLevelImprover(long timeout, OdaInput input){
		if (input.getOdaExtensionDataSourceId().equals(JDBC)){
			return new JDBCLevelImprover(timeout);
		}
		else if (input.getOdaExtensionDataSourceId().equals(FMDT)){
			return new FMDTLevelImprover(timeout);
		}
		return new DefaultLevelImprover(timeout);
		
	}
	
	
	public static DegeneratedHierarchyLevelImprover getDegeneratedHierarchyImprover(OdaInput input){
		if (input.getOdaExtensionDataSourceId().equals(JDBC)){
			return new JDBCDegeneratedImprover();
		}
		else if (input.getOdaExtensionDataSourceId().equals(FMDT)){
			return new FMDTDegeneratedImprover();
		}
		
		return new DefaultDegeneratedHierarchyImprover();
	}
}
