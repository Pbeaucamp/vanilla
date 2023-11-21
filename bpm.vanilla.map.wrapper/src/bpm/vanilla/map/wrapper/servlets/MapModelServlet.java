package bpm.vanilla.map.wrapper.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlArgumentsHolder;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IAddressMapDefinitionRelation;
import bpm.vanilla.map.core.design.IAddressRelation;
import bpm.vanilla.map.core.design.IAddressZone;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.IBuildingFloor;
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IImage;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.IMapDefinitionRelation;
import bpm.vanilla.map.core.design.IMapDefinitionService.ManageAction;
import bpm.vanilla.map.core.design.IMapDefinitionService.MapType;
import bpm.vanilla.map.core.design.IZoneTerritoryMapping;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.wrapper.VanillaMapComponent;

import com.thoughtworks.xstream.XStream;

public class MapModelServlet extends HttpServlet{

	private XStream xstream;
	private VanillaMapComponent component;
	
	public MapModelServlet(VanillaMapComponent component){
		this.component = component;
	}
	
	@Override
	public void init() throws ServletException {
		xstream = new XStream();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			
			resp.setCharacterEncoding("UTF-8"); 
			XmlAction action = (XmlAction)xstream.fromXML(req.getInputStream());
			
			XmlArgumentsHolder args = action.getArguments();
			
			
			Object actionResult = null;
			
			switch(action.getActionType()){
			case GET_ADDRESS:
				actionResult = getAddresses(args);
				break;
			case GET_BUILDING:
				actionResult = getBuildings(args);
				break;
			case GET_CELL:
				actionResult = getCells(args);
				break;
			case GET_FLOOR:
				actionResult = getFloors(args);
				break;
			case GET_IMAGE:
				actionResult = getImages(args);
				break;
			case GET_MAP_DEFINITION:
				actionResult = getMapDefinitions(args);
				break;
			case GET_ADDRESS_ZONE:
				actionResult = getMapZone(args);
				break;
			case GET_ADDRESS_MAP_DEFINITION_RELATION:
				actionResult = getAddressMapRelation(args);
				break;
			case DEF_DELETE:
				delete(args);
				break;
			case DEF_SAVE:
				actionResult = save(args);
				break;
			case DEF_UPDATE:
				update(args);
				break;
			case GET_ZTMAPPING:
				actionResult = getZoneTerritoryMapping(args);
				break;
			case SAVE_ZTMAPPING:
				saveZoneTerritoryMapping(args);
				break;
			case DEL_ZTMAPPING:
				deleteZoneTerritoryMapping(args);
				break;
			case GET_OLMO:
				actionResult = getOpenLayersMapObject(args);
				break;
			case SAVE_OLMO:
				saveOpenLayersMapObject(args);
				break;
			case DEL_OLMO:
				deleteOpenLayersMapObject(args);
				break;
			case GET_MAP_BY_TYPE:
				actionResult = getMapByType(args);
				break;
			case GET_MAPS_VANILLA_LIST:
				actionResult =  getAllMapsVanilla(args);
				break;
			case GET_MAP_VANILLA_BY_ID:
				actionResult = getMapVanillaById(args);
				break;
			case SAVE_MAP_VANILLA:
				actionResult = saveMapVanilla(args);
				break;
			case UPDATE_MAP_VANILLA:
				update(args);
				break;
			case DELETE_MAP_VANILLA:
				deleteMapVanilla(args);
				break;
			case GET_MAPS_DATASOURCE:
				actionResult =  getAllMapsDataSource(args);
				break;
			case GET_MAP_DATASOURCE_BY_NAME:
				actionResult = getMapDataSourceByName(args);
				break;
			case SAVE_MAP_DATASET:
				actionResult = saveMapDataSet(args);
				break;
			case GET_MAPS_DATASET:
				actionResult =  getAllMapsDataSet(args);
				break;
			case DELETE_MAP_DATASET:
				deleteMapDataSet(args);
				break;
			case GET_MAP_DATASET_BY_ID:
				actionResult = getMapDataSetById(args);
				break;
			case GET_MAP_DATASOURCE_BY_ID:
				actionResult = getMapDataSourceById(args);
				break;
			case UPDATE_MAP_DATASOURCE:
				update(args);
				break;
			case UPDATE_MAP_DATASET:
				update(args);
				break;
			case GET_MAPDATASET_BY_ID:
				actionResult = getMapDataSetById(args);
				break;
			case GET_MAP_DATASET_BY_MAPVANILLA_ID:
				actionResult = getMapDataSetByMapVanillaId(args);
				break;
			case GET_MAP_SERVERS:
				actionResult = getMapServers(args);
				break;
			case GET_MAP_LAYERS:
				actionResult = getMapLayers(args);
				break;
			case MANAGE_MAP_SERVER:
				actionResult = manageMapServer(args);
				break;
			case GET_ARCGIS_SERVICES:
				actionResult = component.getMapDefinitionDao().getArcgisServices((MapServer) args.getArguments().get(0));
				break;
			case GET_METADATA_MAPPINGS:
				actionResult = component.getMapDefinitionDao().getMetadataMappings((Integer) args.getArguments().get(0));
				break;
				
			case SAVE_METADATA_MAPPINGS:
				component.getMapDefinitionDao().saveMetadataMappingsFromZones((List<MapZone>) args.getArguments().get(0), (MapVanilla) args.getArguments().get(1));
				break;
				
			default:
				throw new Exception("Unknown action " + action.getActionType().name());
			}
			
			if (actionResult != null){
				xstream.toXML(actionResult, resp.getWriter());
			}
			
			
			resp.getWriter().close();
		}catch(Exception ex){
			ex.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
			component.getLogger().error("An error occured", ex);

		}
	}

	private Object getMapDataSourceById(XmlArgumentsHolder args) throws Exception {
		Integer dataSourceId = (Integer)args.getArguments().get(0);
		return component.getMapDefinitionDao().getMapDataSourceById(dataSourceId);
	}

	private Object getMapDataSetById(XmlArgumentsHolder args) throws Exception {
		Integer dataSetId = (Integer)args.getArguments().get(0);
		return component.getMapDefinitionDao().getMapDataSetById(dataSetId);
	}
	
	private Object getMapDataSetByMapVanillaId(XmlArgumentsHolder args) throws Exception {
		Integer MapId = (Integer)args.getArguments().get(0);
		return component.getMapDefinitionDao().getMapDataSetByMapVanillaId(MapId);
	}

	private void deleteMapDataSet(XmlArgumentsHolder args) throws Exception {
		MapDataSet dtS = (MapDataSet) args.getArguments().get(0);
		component.getMapDefinitionDao().deleteMapDataSet(dtS);
	}

	private Object getMapDataSourceByName(XmlArgumentsHolder args) throws Exception {
		String dataSourceName = (String)args.getArguments().get(0);
		return component.getMapDefinitionDao().getMapDataSourceByName(dataSourceName);
	}

	private Object getAllMapsDataSet(XmlArgumentsHolder args) throws Exception {
		if (args.getArguments().size() == 0) {
			return component.getMapDefinitionDao().getAllMapsDataSet();
		}
		else {
			throw new Exception("Number of parameter not expected.");
		}
	}

	private Object saveMapDataSet(XmlArgumentsHolder args) throws Exception {
		MapDataSet dtS = (MapDataSet) args.getArguments().get(0);
		return component.getMapDefinitionDao().saveMapDataSet(dtS);
	}

	private Object getAllMapsDataSource(XmlArgumentsHolder args) throws Exception {
		if (args.getArguments().size() == 0) {
			return component.getMapDefinitionDao().getAllMapsDataSource();
		}
		else {
			throw new Exception("Number of parameter not expected.");
		}
	}

	private void deleteMapVanilla(XmlArgumentsHolder args) throws Exception {
		MapVanilla map = (MapVanilla) args.getArguments().get(0);
		component.getMapDefinitionDao().deleteMapVanilla(map);
		
	}

	private MapVanilla saveMapVanilla(XmlArgumentsHolder args) throws Exception {
		MapVanilla map = (MapVanilla) args.getArguments().get(0);
		return component.getMapDefinitionDao().saveMapVanilla(map);
	}

	private Object getMapVanillaById(XmlArgumentsHolder args) throws Exception {
		Integer mapId = (Integer)args.getArguments().get(0);
		return component.getMapDefinitionDao().getMapVanillaById(mapId);
	}

	private Object getAllMapsVanilla(XmlArgumentsHolder args) throws Exception {
		if (args.getArguments().size() == 0) {
			return component.getMapDefinitionDao().getAllMapsVanilla();
		}
		else {
			throw new Exception("Number of parameter not expected.");
		}
	}

	private List<IMapDefinition> getMapByType(XmlArgumentsHolder args) throws Exception {
		MapType type = (MapType)args.getArguments().get(0);
		return component.getMapDefinitionDao().getMapDefinition(type);
	}

	private void deleteOpenLayersMapObject(XmlArgumentsHolder args) throws Exception {
		IOpenLayersMapObject obj = (IOpenLayersMapObject) args.getArguments().get(0);
		component.getMapDefinitionDao().deleteOpenLayersMap(obj);
	}

	private void saveOpenLayersMapObject(XmlArgumentsHolder args) throws Exception {
		IOpenLayersMapObject obj = (IOpenLayersMapObject) args.getArguments().get(0);
		component.getMapDefinitionDao().saveOpenLayersMapObject(obj);
	}

	private Object getOpenLayersMapObject(XmlArgumentsHolder args) throws Exception {
		if(args.getArguments() != null && args.getArguments().size() > 0) {
			Integer id = (Integer) args.getArguments().get(0);
			return component.getMapDefinitionDao().getOpenLayersMapObject(id);
		}
		else {
			return component.getMapDefinitionDao().getOpenLayersMapObjects();
		}
	}

	private void deleteZoneTerritoryMapping(XmlArgumentsHolder args) throws Exception {
		Object o = args.getArguments().get(0);
		if(o instanceof List) {
			List<IZoneTerritoryMapping> mapping = (List<IZoneTerritoryMapping>) o;
			component.getMapDefinitionDao().deleteZoneTerritoryMappings(mapping);
		}
		else {
			IZoneTerritoryMapping mapping = (IZoneTerritoryMapping) o;
			component.getMapDefinitionDao().deleteZoneTerritoryMapping(mapping);
		}
	}

	private void saveZoneTerritoryMapping(XmlArgumentsHolder args) throws Exception {
		Object o = args.getArguments().get(0);
		if(o instanceof List) {
			List<IZoneTerritoryMapping> mapping = (List<IZoneTerritoryMapping>) o;
			component.getMapDefinitionDao().saveZoneTerritoryMappings(mapping);
		}
		else {
			IZoneTerritoryMapping mapping = (IZoneTerritoryMapping) o;
			component.getMapDefinitionDao().saveZoneTerritoryMapping(mapping);
		}
	}

	private Object getZoneTerritoryMapping(XmlArgumentsHolder args) throws Exception {
		int mapId = Integer.parseInt(args.getArguments().get(0).toString());
		return component.getMapDefinitionDao().getZoneTerritoryMappingByMapId(mapId);
	}

	private Object getBuildings(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			return component.getMapDefinitionDao().getAllBuilding();
		}
		else if (args.getArguments().size() == 1){
			return component.getMapDefinitionDao().getBuilding((Integer)args.getArguments().get(0));
		}
		else if (args.getArguments().size() == 2){
			return component.getMapDefinitionDao().getBuildingsForAddress((Integer)args.getArguments().get(1));
		}
		throw new Exception("Number of parameter not expected.");
	}

	private Object getAddresses(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			return component.getMapDefinitionDao().getAllAddress();
		}
		else if (args.getArguments().size() == 1){
			return component.getMapDefinitionDao().getAddress((Integer)args.getArguments().get(0));
		}
		else if (args.getArguments().size() == 2){
			String s = (String)args.getArguments().get(0);
			if(s.equals("parent")){
				return component.getMapDefinitionDao().getAddressParent();
			}
			else if(s.equals("child")){
				return component.getMapDefinitionDao().getAddressChild((IAddress)args.getArguments().get(1));
			}
			else if(s.equals("label")){
				return component.getMapDefinitionDao().getAddressByLabel((String)args.getArguments().get(1));
			}
		}
		
		throw new Exception("Number of parameter not expected.");
		
	}
	
	private Object getCells(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			return component.getMapDefinitionDao().getAllCell();
		}
		else if (args.getArguments().size() == 1){
			return component.getMapDefinitionDao().getCell((Integer)args.getArguments().get(0));
		}
		else if (args.getArguments().size() == 2){
			String s = (String)args.getArguments().get(0);
			
			if (s.equals("floorId")){
				return component.getMapDefinitionDao().getCellsForFloor((Integer)args.getArguments().get(1));
			}
			else if (s.equals("buildingId")){
				return component.getMapDefinitionDao().getCellsForBuilding((Integer)args.getArguments().get(1));
			}
		}
		
		throw new Exception("Number of parameter not expected.");
		
	}
	
	private Object getFloors(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			return component.getMapDefinitionDao().getAllFloor();
		}
		else if (args.getArguments().size() == 1){
			return component.getMapDefinitionDao().getFloor((Integer)args.getArguments().get(0));
		}
		else if (args.getArguments().size() == 2){
			String s = (String)args.getArguments().get(0);
			
			if (s.equals("buildingId")){
				return component.getMapDefinitionDao().getFloorsForBuilding((Integer)args.getArguments().get(1));
			}
		}
		
		throw new Exception("Number of parameter not expected.");
	}
	
	private Object getImages(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			return component.getMapDefinitionDao().getAllImage();
		}
		else if (args.getArguments().size() == 1){
			return component.getMapDefinitionDao().getImage((Integer)args.getArguments().get(0));
		}
		
		throw new Exception("Number of parameter not expected.");
	}
	
	private Object getMapDefinitions(XmlArgumentsHolder args) throws Exception{
		if (args.getArguments().size() == 0){
			return component.getMapDefinitionDao().getAllMapDefinition();
		}
		else if (args.getArguments().size() == 1){
			return component.getMapDefinitionDao().getMapDefinition((Integer)args.getArguments().get(0));
		}
		else if (args.getArguments().size() == 2){
			String s = (String)args.getArguments().get(0);
			if(s.equals("parent")){
				return component.getMapDefinitionDao().getMapDefinitionParent();
			}
			else if(s.equals("child")){
				return component.getMapDefinitionDao().getMapDefinitionChild((IMapDefinition)args.getArguments().get(1));
			}
			else if(s.equals("addressId")){
				return component.getMapDefinitionDao().getMapDefinitionFromAddressId((Integer)args.getArguments().get(1));
			}
			return component.getMapDefinitionDao().getMapDefinitionFromAddressId((Integer)args.getArguments().get(1));
		}
		
		throw new Exception("Number of parameter not expected.");
	}
	
	private Object getMapZone(XmlArgumentsHolder args) throws Exception{
		return component.getMapDefinitionDao().getAddressZoneByZoneAndAddressId((Integer)args.getArguments().get(0), (Long)args.getArguments().get(1));
	}
	
	private Object getAddressMapRelation(XmlArgumentsHolder args) throws Exception{
		return component.getMapDefinitionDao().getAddressMapRelationByMapIdAndAddressId((Integer)args.getArguments().get(0), (Integer)args.getArguments().get(1));
	}
	
	private void delete(XmlArgumentsHolder args) throws Exception{
		Object arg = args.getArguments().get(0);
		if (arg instanceof IAddress){
			component.getMapDefinitionDao().delete((IAddress)arg);
		}
		else if (arg instanceof IMapDefinition){
			component.getMapDefinitionDao().delete((IMapDefinition)arg);
		}
		
		else if (arg instanceof IBuildingFloor){
			component.getMapDefinitionDao().delete((IBuildingFloor)arg);
		}
		else if (arg instanceof ICell){
			component.getMapDefinitionDao().delete((ICell)arg);
		}
		else if (arg instanceof IBuilding){
			component.getMapDefinitionDao().delete((IBuilding)arg);
		}
		else if (arg instanceof IAddressRelation){
			component.getMapDefinitionDao().delete((IAddressRelation)arg);
		}
		else if (arg instanceof IAddressZone){
			component.getMapDefinitionDao().delete((IAddressZone)arg);
		}
		else if (arg instanceof IMapDefinitionRelation){
			component.getMapDefinitionDao().delete((IMapDefinitionRelation)arg);
		}
		else if (arg instanceof IAddressMapDefinitionRelation){
			component.getMapDefinitionDao().delete((IAddressMapDefinitionRelation)arg);
		}
		else {
			throw new Exception("Cannot delete such object " + arg.getClass().getName());
		}
	}
	
	private Object save(XmlArgumentsHolder args) throws Exception{
		Object arg = args.getArguments().get(0);
		if (arg instanceof IAddress){
			 return component.getMapDefinitionDao().saveAddress((IAddress)arg);
		}
		else if (arg instanceof IMapDefinition){
			return component.getMapDefinitionDao().saveMapDefinition((IMapDefinition)arg);
		}
		
		else if (arg instanceof IBuildingFloor){
			return component.getMapDefinitionDao().saveFloor((IBuildingFloor)arg);
		}
		else if (arg instanceof ICell){
			return component.getMapDefinitionDao().saveCell((ICell)arg);
		}
		else if (arg instanceof IImage){
			return component.getMapDefinitionDao().saveImage((IImage)arg);
		}
		else if (arg instanceof IBuilding){
			return component.getMapDefinitionDao().saveBuilding((IBuilding)arg);
		}
		else if (arg instanceof IAddressRelation){
			return component.getMapDefinitionDao().saveAddressRelation((IAddressRelation)arg);
		}
		else if (arg instanceof IAddressZone){
			return component.getMapDefinitionDao().saveAddressZone((IAddressZone)arg);
		}
		else if (arg instanceof IMapDefinitionRelation){
			return component.getMapDefinitionDao().saveMapDefinitionRelation((IMapDefinitionRelation)arg);
		}
		else if (arg instanceof IAddressMapDefinitionRelation){
			return component.getMapDefinitionDao().saveAddressMapDefinitionRelation((IAddressMapDefinitionRelation)arg);
		}
		else {
			throw new Exception("Cannot save such object " + arg.getClass().getName());
		}
	}
	
	private void update(XmlArgumentsHolder args) throws Exception{
		Object arg = args.getArguments().get(0);
		if (arg instanceof IAddress){
			component.getMapDefinitionDao().update((IAddress)arg);
		}
		else if (arg instanceof IMapDefinition){
			component.getMapDefinitionDao().update((IMapDefinition)arg);
		}
		
		else if (arg instanceof IBuildingFloor){
			component.getMapDefinitionDao().update((IBuildingFloor)arg);
		}
		else if (arg instanceof ICell){
			component.getMapDefinitionDao().update((ICell)arg);
		}
		else if (arg instanceof IBuilding){
			component.getMapDefinitionDao().update((IBuilding)arg);
		}
		else if (arg instanceof IAddressRelation){
			component.getMapDefinitionDao().update((IAddressRelation)arg);
		}
		else if (arg instanceof IAddressZone){
			component.getMapDefinitionDao().update((IAddressZone)arg);
		}
		else if (arg instanceof IMapDefinitionRelation){
			component.getMapDefinitionDao().update((IMapDefinitionRelation)arg);
		}
		else if (arg instanceof IAddressMapDefinitionRelation){
			component.getMapDefinitionDao().update((IAddressMapDefinitionRelation)arg);
		}
		else if(arg instanceof MapVanilla){
			component.getMapDefinitionDao().updateMapVanilla((MapVanilla)arg);
		}
		else if(arg instanceof MapDataSource) {
			component.getMapDefinitionDao().updateMapDataSource((MapDataSource)arg);
		}
		else if(arg instanceof MapDataSet) {
			component.getMapDefinitionDao().updateMapDataSet((MapDataSet)arg);
		}
		else {
			throw new Exception("Cannot save such object " + arg.getClass().getName());
		}
	}

	private List<MapServer> getMapServers(XmlArgumentsHolder args) throws Exception {
		return component.getMapDefinitionDao().getMapServers();
	}

	private Object getMapLayers(XmlArgumentsHolder args) throws Exception {
		MapServer mapServer = (MapServer) args.getArguments().get(0);
		return component.getMapDefinitionDao().getLayers(mapServer);
	}

	private MapServer manageMapServer(XmlArgumentsHolder args) throws Exception {
		MapServer mapServer = (MapServer) args.getArguments().get(0);
		ManageAction action = (ManageAction) args.getArguments().get(1);
		return component.getMapDefinitionDao().manageMapServer(mapServer, action);
	}
}
