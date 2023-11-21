package bpm.oda.driver.reader.model.odaconsummer;

import java.util.Locale;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.consumer.helper.OdaDriver;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;

public class ReadertOdaDriver extends OdaDriver {

	public ReadertOdaDriver(String driverClassName, Locale locale, ClassLoader driverClassloader, boolean switchContextClassloader) throws OdaException {
		super(driverClassName, locale, driverClassloader, switchContextClassloader);
		
	}

	public ReadertOdaDriver(ExtensionManifest driverConfig) throws OdaException {
		super(driverConfig);
		
	}

	public ReadertOdaDriver(String odaDataSourceId) throws OdaException {
		super(odaDataSourceId);
		
	}

	
	/**
	 * 
	 * @return the WrappedObject className
	 */
	public String getOdaDriverClassName(){
		return getObject().getClass().getName();
	}
}
