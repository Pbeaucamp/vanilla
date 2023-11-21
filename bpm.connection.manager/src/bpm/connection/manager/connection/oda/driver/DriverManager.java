package bpm.connection.manager.connection.oda.driver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IDriver;

public class DriverManager {
	private static DriverManager instance;
	
	private List<Driver> loadedDrivers = new ArrayList<Driver>();
	private DriverManager(){}
	
	
	private static DriverManager getInstance(){
		if (instance == null){
			instance = new DriverManager();
		}
		return instance;
	}
	
	public static IDriver getOdaDriver(String datasourceId) throws Exception{
		
		DriverManager manager = getInstance();
		
		for(Driver d : manager.loadedDrivers){
			if (d.getDataSourceDriverId() !=  null && d.getDataSourceDriverId().equals(datasourceId)){
				return d.getDriverOda();
			}
		}
		Driver d = new Driver(datasourceId);
		manager.loadedDrivers.add(d);
		
		IDriver _d = d.getDriverOda();
		
		
		return _d;
	}
	
	
}
