package bpm.vanilla.platform.core.components;

import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;

/**
 * Configuration to Run something on an Object
 * @author ludo
 *
 */
public interface IRuntimeConfig {
	/**
	 * 
	 * @return The identifier of the Object(repId,itemId)
	 */
	public IObjectIdentifier getObjectIdentifier();
	
	/**
	 * 
	 * @return tha parameters with their selected values to use to run the object
	 */
	public List<VanillaGroupParameter> getParametersValues();
	
	/**
	 * 
	 * @return the GroupId used to ru this Objetc
	 */
	public Integer getVanillaGroupId();
}
