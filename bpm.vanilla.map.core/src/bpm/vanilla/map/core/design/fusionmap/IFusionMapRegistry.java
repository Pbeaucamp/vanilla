package bpm.vanilla.map.core.design.fusionmap;

import java.io.InputStream;
import java.util.List;

/**
 * Provide a way to manage the FusionMapObject within the platform.
 * Its implementors are responsible for providing the IFusionMapObject regsitered,
 * storing them in a storage system, modifying them or deleting them
 * 
 * @author ludo
 *
 */
public interface IFusionMapRegistry {

	public List<IFusionMapObject> getFusionMapObjects() throws Exception;
	
	public IFusionMapObject addFusionMapObject(IFusionMapObject fusionMap, InputStream swfInputStream) throws Exception;
	
	public void removeFusionMapObject(IFusionMapObject fusionMap) throws Exception;
	
	public void removeFusionMapObject(long fusionMapObjectId) throws Exception;
	
	public IFusionMapObject getFusionMapObject(long fusionMapObjectId) throws Exception;
	
	public String getMapFolderLocation();
	
	public void configure(Object object);
	
	public List<IFusionMapObject> getFusionMapObjects(String type) throws Exception;
}
