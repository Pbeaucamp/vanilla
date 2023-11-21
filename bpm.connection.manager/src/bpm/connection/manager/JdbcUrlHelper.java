package bpm.connection.manager;

import java.util.Collection;

import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;

public class JdbcUrlHelper {

	public static String getJdbcUrl(String host, String port, String database, String driverClass) throws Exception {
		String url = "", urlPrefix= "";
		Collection<DriverInfo> drivers = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getDriversInfo();
		for(DriverInfo info : drivers){
			if(info.getClassName().equals(driverClass)){
				urlPrefix = info.getUrlPrefix();
				break;
			}
		}
//		DriverInfo driverInfo = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(datasource.getDriver());
//		String urlPrefix = driverInfo.getUrlPrefix();
		
		if (driverClass.equals(ListDriver.MS_ACCESS) || driverClass.equals(ListDriver.MS_XLS)){
			url = urlPrefix + database ;
			if (!url.trim().endsWith(";")){
				url = url.trim() + ";";
			}
		}
		else {
			if (port == null || "".equals(port)){
				url = urlPrefix + host  + "/" + database;
			}
			else {
				url = urlPrefix + host + ":" + port + "/" + database;
			}
			
			if (driverClass.contains("oracle")){
				url = url.replace("/", ":");
			}
			
		}
		return url;
	}
	
}
