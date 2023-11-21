package bpm.fd.api.core.model.datas.odaconsumer;

import org.eclipse.datatools.connectivity.oda.IDriver;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;

public class Driver {
	private String dataSourceDriverId, odaDriverClassName;
	private ExtensionManifest extensionManifest;
	private IDriver odaDriver;
	
	protected Driver(String dataSourceDriverId, String odaDriverClassName){
		this.dataSourceDriverId = dataSourceDriverId;
		this.odaDriverClassName = odaDriverClassName;
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
			try{
				odaDriver = new FdOdaDriver(getExtensionManifest());
			}catch(Exception e){				
				odaDriver = (IDriver)Class.forName(odaDriverClassName).newInstance();				
			}			
		}		
		return odaDriver;
	}
	
}
