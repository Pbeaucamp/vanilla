package bpm.dataprovider.odainput.consumer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IDriver;

import bpm.vanilla.platform.core.beans.data.OdaInput;

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
	
	public static IDriver getOdaDriver(OdaInput input) throws Exception{
		
		DriverManager manager = getInstance();
		
		for(Driver d : manager.loadedDrivers){
			if (d.getDataSourceDriverId() !=  null && d.getDataSourceDriverId().equals(input.getOdaExtensionDataSourceId())){
				return d.getDriverOda();
			}
		}
		Driver d = new Driver(input.getOdaExtensionDataSourceId());
		manager.loadedDrivers.add(d);
		
		IDriver _d = d.getDriverOda();
		
		
		return _d;
	}
	
	
}
