package bpm.vanilla.map.core.design;

/**
 * 
 * This interface define a relation between two Address (one child and one daddy)
 * 
 * @author Seb
 *
 */

public interface IAddressRelation {

	/**
	 * 
	 * @return the IAddress Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the IAddress MapDefinition Id
	 */
	public Integer getParentId();

	/**
	 * 
	 * @return the MapDefinition associated to the IAddress
	 */
	public Integer getChildId();

	public void setId(Integer id);
	
	public void setParentId(Integer parentId);
	
	public void setChildId(Integer childId);
}
