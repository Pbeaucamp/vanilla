package bpm.oda.driver.reader.model.odaconsummer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IDriver;

import bpm.oda.driver.reader.model.datasource.DataSource;

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
	
	public static IDriver getOdaDriver(DataSource dataSource) throws Exception{
		
		DriverManager manager = getInstance();
		
		for(Driver d : manager.loadedDrivers){
			if (d.getDataSourceDriverId().equals(dataSource.getOdaExtensionDataSourceId())){
				return d.getDriverOda();
			}
		}
		Driver d = new Driver(dataSource.getOdaExtensionDataSourceId(), dataSource.getOdaDriverClassName());
		manager.loadedDrivers.add(d);
		
		IDriver _d = d.getDriverOda();
		
		if (_d != null){
			// no choice......
			
			if (_d instanceof ReadertOdaDriver && (dataSource.getOdaDriverClassName() == null || "".equals(dataSource.getOdaDriverClassName()))){
				dataSource.setOdaDriverClassName(((ReadertOdaDriver)_d).getOdaDriverClassName());
			}
			else{
			}
			
		}
		return _d;
	}
	
	
}
