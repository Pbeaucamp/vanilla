package bpm.vanilla.map.core.design;

/**
 * 
 * This interface define a relation between an IAddress and an IMapDefinition which correspond to a map
 * 
 * @author SVI
 *
 */

public interface IAddressMapDefinitionRelation {

	/**
	 * 
	 * @return the IAddressMapDefinition Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the IAddress id
	 */
	public Integer getAddressId();

	/**
	 * 
	 * @return the IMapDefinition id
	 */
	public Integer getMapDefinitionId();

	public void setId(Integer id);
	
	public void setAddressId(Integer addressId);
	
	public void setMapDefinitionId(Integer mapDefinitionId);
}
