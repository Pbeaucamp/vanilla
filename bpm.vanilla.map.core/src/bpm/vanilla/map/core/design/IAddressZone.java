package bpm.vanilla.map.core.design;

/**
 * 
 * This interface define a relation between an IAddress and an IKmlMapSpecification which can be a zone,
 * a point, or a line
 * 
 * @author Seb
 *
 */

public interface IAddressZone {

	/**
	 * 
	 * @return the IAddressZone Id
	 */
	public Integer getId();
	
	/**
	 * 
	 * @return the IAddress id
	 */
	public Integer getAddressId();

	/**
	 * 
	 * @return the IKmlMapSpecificationEntity id
	 */
	public Long getMapZoneId();

	public void setId(Integer id);
	
	public void setAddressId(Integer addressId);
	
	public void setMapZoneId(Long mapZoneId);
}
