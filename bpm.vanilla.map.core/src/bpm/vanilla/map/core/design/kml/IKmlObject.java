package bpm.vanilla.map.core.design.kml;

import java.util.List;

/**
 * This interface defines a GoogleMapSpécificationEntity which is represented by an id, a label
 * a folder, and an inputStream
 * 
 * @author SVI
 *
 */

public interface IKmlObject {
	
	public static final String KML_PATH = "/Kml/";
	
	public Integer getId();
	
	public String getKmlFileName();
	
	public void setId(Integer id);
	
	public void setKmlFileName(String kmlFileName);
	

	public List<IKmlSpecificationEntity> getSpecificationsEntities();
	
	public void addSpecificationEntity(IKmlSpecificationEntity entity);
	
	public void removeSpecificationEntity(IKmlSpecificationEntity entity);
}
