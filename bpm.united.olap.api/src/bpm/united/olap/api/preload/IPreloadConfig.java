package bpm.united.olap.api.preload;

/**
 * use to perform some preloading when deploying a cube on the VanillaServer
 * @author ludo
 *
 */
public interface IPreloadConfig {
	
	public Integer getLevelNumber(String hierarchyUniqueName);
	
	public void setHierarchyLevel(String hierarchyUniqueName, Integer levelNumber);

	public String getXml();
	
	
}
