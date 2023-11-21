package bpm.gateway.core.transformations.inputs.odaconsumer;

import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.consumer.helper.OdaDriver;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;

public class Driver {
	private String dataSourceDriverId;
	private ExtensionManifest extensionManifest;
	private IDriver odaDriver;
	
	protected Driver(String dataSourceDriverId){
		this.dataSourceDriverId = dataSourceDriverId;
	}
	
	protected String getDataSourceDriverId(){
		return dataSourceDriverId;
	}
	
	protected ExtensionManifest getExtensionManifest() throws Exception{
		if (extensionManifest == null){
			extensionManifest = findExtensionManifest();
		}
		
		if (extensionManifest == null){
			throw new Exception("Unable to find Oda driver " + dataSourceDriverId);
		}
		
		return extensionManifest;
	}
	
	private ExtensionManifest findExtensionManifest() throws OdaException{
		ManifestExplorer explorer = ManifestExplorer.getInstance();
		ExtensionManifest ext = explorer.getExtensionManifest(dataSourceDriverId);
		
		return ext;
	}
	
	protected IDriver getDriverOda() throws Exception{
		if (odaDriver == null){
			odaDriver = new OdaDriver(getExtensionManifest());
		}
		
		return odaDriver;
	}
	
}
