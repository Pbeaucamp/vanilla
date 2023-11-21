package bpm.fd.api.core.model.datas.odaconsumer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IDriver;

import bpm.fd.api.core.model.datas.DataSource;

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
			
			if (_d instanceof FdOdaDriver && (dataSource.getOdaDriverClassName() == null || "".equals(dataSource.getOdaDriverClassName()))){
				dataSource.setOdaDriverClassName(((FdOdaDriver)_d).getOdaDriverClassName());
			}
			else{
			}
			
		}
		return _d;
	}
	
	
}
