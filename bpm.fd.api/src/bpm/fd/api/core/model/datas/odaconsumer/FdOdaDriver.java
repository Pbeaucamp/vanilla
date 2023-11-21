package bpm.fd.api.core.model.datas.odaconsumer;

import java.util.Locale;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.consumer.helper.OdaDriver;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;

public class FdOdaDriver extends OdaDriver {

	public FdOdaDriver(String driverClassName, Locale locale, ClassLoader driverClassloader, boolean switchContextClassloader) throws OdaException {
		super(driverClassName, locale, driverClassloader, switchContextClassloader);
		
	}

	public FdOdaDriver(ExtensionManifest driverConfig) throws OdaException {
		super(driverConfig);
		
	}

	public FdOdaDriver(String odaDataSourceId) throws OdaException {
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
