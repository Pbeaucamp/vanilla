package bpm.vanilla.map.core.communication.xml;


/**
 * a Simple class to represent an Action to be performed by the server side of the vanilla forms
 * 
 * The server side is implemented by the plugin bpm.forms.runtime and contains Servlets
 * Each servlets will received orders by the form of an serialized XmlAction and rebuild the XmAction object from teh XML
 * 
 * @author ludo
 *
 */
public class XmlAction {
	/**
	 * The type of action for the servlets
	 * @author ludo
	 *
	 */
	public enum ActionType{
		DEF_DELETE, GET_ADDRESS, GET_BUILDING, GET_CELL, GET_FLOOR, GET_IMAGE, GET_MAP_DEFINITION, 
		GET_ADDRESS_ZONE, GET_ADDRESS_MAP_DEFINITION_RELATION, DEF_SAVE, DEF_UPDATE, 
		FUSION_MAP_DELETE, FUSION_MAP_GET, FUSION_MAP_SAVE, 
		KML_DELETE, KML_GET, KML_SAVE,
		KML_GENERATE, GET_ZTMAPPING, SAVE_ZTMAPPING, DEL_ZTMAPPING,
		GET_OLMO, SAVE_OLMO, DEL_OLMO,
		GET_MAPS, GET_MAP_BY_ID, GET_MAP_ENTITIES, ADD_MAP, ADD_MAP_SHAPE, UPDATE_MAP, REMOVE_MAP,
		SAVE_SHAPE, GET_ENTITY_COORDINATES, GET_MAP_BY_TYPE, GET_MAP_BY_DEFINITION_ID,
		GET_MAPS_VANILLA_LIST, GET_MAP_VANILLA_BY_ID, SAVE_MAP_VANILLA, UPDATE_MAP_VANILLA, DELETE_MAP_VANILLA,
		SAVE_MAP_DATASOURCE, GET_MAPS_DATASOURCE, GET_MAP_DATASOURCE_BY_NAME, SAVE_MAP_DATASET, GET_MAPS_DATASET,
		DELETE_MAP_DATASET,GET_MAP_DATASET_BY_ID,GET_MAP_DATASOURCE_BY_ID,
		UPDATE_MAP_DATASOURCE,UPDATE_MAP_DATASET, GET_MAPDATASET_BY_ID, GET_MAP_DATASET_BY_MAPVANILLA_ID,
		GET_MAP_SERVERS, GET_MAP_LAYERS, MANAGE_MAP_SERVER, GET_ARCGIS_SERVICES, GET_METADATA_MAPPINGS, SAVE_METADATA_MAPPINGS
	}
	
	private XmlArgumentsHolder arguments;
	private ActionType actionType;
	
	/**
	 * 
	 * @param arguments : object will contains all the parameters objects for the action
	 * @param actionType : the action type
	 */
	public XmlAction(XmlArgumentsHolder arguments, ActionType actionType) {
		super();
		this.arguments = arguments;
		this.actionType = actionType;
	}
	/**
	 * 
	 * @return the arguments
	 */
	public XmlArgumentsHolder getArguments() {
		return arguments;
	}
	
	/**
	 * 
	 * @param arguments 
	 * set the argumnets of the action
	 */
	public void setArguments(XmlArgumentsHolder arguments) {
		this.arguments = arguments;
	}
	/**
	 * 
	 * @return the ActionType 
	 */
	public ActionType getActionType() {
		return actionType;
	}
	
	/**
	 * 
	 * @param actionType
	 */
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	
	
	
}
