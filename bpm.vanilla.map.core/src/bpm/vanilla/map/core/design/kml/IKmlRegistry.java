package bpm.vanilla.map.core.design.kml;

import java.io.InputStream;
import java.util.List;

/**
 * This interface provide a way to manage a GoogleMapObject within the platform.
 * Its implementors are responsible for providing the IGoogleMapObject registered,
 * storing them in a storage system, modifying them or deleting them
 * 
 * @author SVI
 *
 */

public interface IKmlRegistry {

	public List<IKmlObject> getKmlObjects() throws Exception;
	
	public IKmlObject addKmlObject(IKmlObject kmlObject, InputStream kmlInputStream) throws Exception;
	
	public void removeKmlObject(IKmlObject kmlObject) throws Exception;
	
	public void removeKmlObject(Integer kmlObjectId) throws Exception;
	
	public IKmlObject getKmlObject(Integer kmlObjectId) throws Exception;
	
	public String getKmlFolderLocation();
	
	public void configure(Object config);
	
	
}
