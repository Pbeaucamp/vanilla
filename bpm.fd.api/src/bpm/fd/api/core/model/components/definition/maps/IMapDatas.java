package bpm.fd.api.core.model.components.definition.maps;

import bpm.fd.api.core.model.components.definition.IComponentDatas;

/**
 * Only 2 values needed, one for the field position in the DataSet
 * that will reprensent the field giving the internal Id for the zone within the map
 * The other the index representing the value to Use.
 * 
 *  
 * @author ludo
 *
 */
public interface IMapDatas extends IComponentDatas{

//	/**
//	 * @return the zoneFieldIndex
//	 */
//	public Integer getZoneIdFieldIndex();
	
	
	/**
	 * @return the valueFieldIndex
	 */
	public Integer getValueFieldIndex();
}
