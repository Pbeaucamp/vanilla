package bpm.vanilla.map.remote.core.design.fusionmap.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.vanilla.map.core.communication.xml.XmlAction;
import bpm.vanilla.map.core.communication.xml.XmlAction.ActionType;
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
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.map.core.design.IZoneTerritoryMapping;
import bpm.vanilla.map.core.design.MapDataSet;
import bpm.vanilla.map.core.design.MapDataSource;
import bpm.vanilla.map.core.design.MapLayer;
import bpm.vanilla.map.core.design.MapServer;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.core.design.ZoneMetadataMapping;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.remote.internal.HttpCommunicator;

import com.thoughtworks.xstream.XStream;

public class RemoteMapDefinitionService implements IMapDefinitionService{
	private HttpCommunicator httpCommunicator;
	private XStream xstream;
	
	public RemoteMapDefinitionService(){
		httpCommunicator = new HttpCommunicator("");
		init();
	}
	
	public void configure(Object config){
		setVanillaRuntimeUrl((String)config);
	}
	
	public void setVanillaRuntimeUrl(String vanillaRuntimeUrl){
		synchronized (httpCommunicator) {
			httpCommunicator.setUrl(vanillaRuntimeUrl);
		}
		
	}
	
	private void init(){
		xstream = new XStream();
	}
	
	
	private XmlArgumentsHolder createArguments(Object...  arguments){
		XmlArgumentsHolder args = new XmlArgumentsHolder();
		
		for(int i = 0; i < arguments.length; i++){
			args.addArgument(arguments[i]);
		}
		return args;
	}

	@Override
	public void delete(IAddress address) throws Exception {
		XmlAction op = new XmlAction(createArguments(address), ActionType.DEF_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public void delete(IBuilding building) throws Exception {
		XmlAction op = new XmlAction(createArguments(building), ActionType.DEF_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public void delete(IBuildingFloor floor) throws Exception {
		XmlAction op = new XmlAction(createArguments(floor), ActionType.DEF_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public void delete(ICell cell) throws Exception {
		XmlAction op = new XmlAction(createArguments(cell), ActionType.DEF_DELETE);
		
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public void delete(IMapDefinition mapDefinition) throws Exception {
		XmlAction op = new XmlAction(createArguments(mapDefinition), ActionType.DEF_DELETE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public IAddress getAddress(int addressId) throws Exception {
		XmlAction op = new XmlAction(createArguments(addressId), ActionType.GET_ADDRESS);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IAddress)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IAddress> getAllAddress() throws Exception{
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_ADDRESS);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IBuilding> getAllBuilding() throws Exception{
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_BUILDING);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			if(xml == null || xml.equals("")){
				return new ArrayList<IBuilding>();
			}
			else{
				return (List)xstream.fromXML(xml);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<ICell> getAllCell() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_CELL);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IBuildingFloor> getAllFloor() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_FLOOR);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IImage> getAllImage() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_IMAGE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IMapDefinition> getAllMapDefinition() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_MAP_DEFINITION);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			if(xml == null || xml.equals("")){
				return new ArrayList<IMapDefinition>();
			}
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IBuilding getBuilding(int buildingId) throws Exception {
		XmlAction op = new XmlAction(createArguments(buildingId), ActionType.GET_BUILDING);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IBuilding)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IBuilding> getBuildingsForAddress(int addressId) throws Exception {
		XmlAction op = new XmlAction(createArguments("addressId", addressId), ActionType.GET_BUILDING);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ICell getCell(int cellId) throws Exception {
		XmlAction op = new XmlAction(createArguments(cellId), ActionType.GET_CELL);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (ICell)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<ICell> getCellsForBuilding(int buildingId) throws Exception {
		XmlAction op = new XmlAction(createArguments("buildingId", buildingId), ActionType.GET_CELL);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<ICell> getCellsForFloor(int floorId) throws Exception {
		XmlAction op = new XmlAction(createArguments("floorId", floorId), ActionType.GET_CELL);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IBuildingFloor getFloor(int floorId) throws Exception {
		XmlAction op = new XmlAction(createArguments(floorId), ActionType.GET_FLOOR);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IBuildingFloor)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IBuildingFloor> getFloorsForBuilding(int buildingId) throws Exception {
		XmlAction op = new XmlAction(createArguments("buildingId", buildingId), ActionType.GET_FLOOR);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IImage getImage(int imageId) throws Exception {
		XmlAction op = new XmlAction(createArguments(imageId), ActionType.GET_IMAGE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IImage)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IMapDefinition getMapDefinition(int mapDefinitionId)	throws Exception {
		XmlAction op = new XmlAction(createArguments(mapDefinitionId), ActionType.GET_MAP_DEFINITION);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IMapDefinition)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IMapDefinition> getMapDefinitionFromAddressId(int addressId) throws Exception {
		XmlAction op = new XmlAction(createArguments("addressId", addressId), ActionType.GET_MAP_DEFINITION);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IAddress saveAddress(IAddress address) throws Exception {
		XmlAction op = new XmlAction(createArguments(address), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IAddress)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public IBuilding saveBuilding(IBuilding building) throws Exception {
		XmlAction op = new XmlAction(createArguments(building), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IBuilding)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public ICell saveCell(ICell cell) throws Exception {
		XmlAction op = new XmlAction(createArguments(cell), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (ICell)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public IBuildingFloor saveFloor(IBuildingFloor floor) throws Exception {
		XmlAction op = new XmlAction(createArguments(floor), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IBuildingFloor)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IImage saveImage(IImage img) throws Exception {
		XmlAction op = new XmlAction(createArguments(img), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IImage)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IMapDefinition saveMapDefinition(IMapDefinition mapDef) throws Exception {
		XmlAction op = new XmlAction(createArguments(mapDef), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IMapDefinition)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public void update(IAddress address) throws Exception {
		XmlAction op = new XmlAction(createArguments(address), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public void update(IBuilding building) throws Exception {
		XmlAction op = new XmlAction(createArguments(building), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public void update(IBuildingFloor floor) throws Exception {
		XmlAction op = new XmlAction(createArguments(floor), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public void update(ICell cell) throws Exception {
		XmlAction op = new XmlAction(createArguments(cell), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public void update(IMapDefinition mapDefinition) throws Exception {
		XmlAction op = new XmlAction(createArguments(mapDefinition), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public void delete(IAddressRelation relation) throws Exception {
		XmlAction op = new XmlAction(createArguments(relation), ActionType.DEF_DELETE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<IAddress> getAddressChild(IAddress addressParent)
			throws Exception {
		XmlAction op = new XmlAction(createArguments("child", addressParent), ActionType.GET_ADDRESS);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IAddress> getAddressParent() throws Exception {
		XmlAction op = new XmlAction(createArguments("parent", ""), ActionType.GET_ADDRESS);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IAddressRelation saveAddressRelation(IAddressRelation relation) throws Exception {
		XmlAction op = new XmlAction(createArguments(relation), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IAddressRelation)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void update(IAddressRelation relation) throws Exception {
		XmlAction op = new XmlAction(createArguments(relation), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void delete(IAddressZone addressZoneRelation) throws Exception {
		XmlAction op = new XmlAction(createArguments(addressZoneRelation), ActionType.DEF_DELETE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public IAddressZone saveAddressZone(IAddressZone addressZoneRelation)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(addressZoneRelation), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IAddressZone)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void update(IAddressZone addressZoneRelation) throws Exception {
		XmlAction op = new XmlAction(createArguments(addressZoneRelation), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IAddressZone getAddressZoneByZoneAndAddressId(int addressId,
			long zoneId) throws Exception {
		XmlAction op = new XmlAction(createArguments(addressId, zoneId), ActionType.GET_ADDRESS_ZONE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IAddressZone)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IAddress getAddressByLabel(String label) throws Exception {
		XmlAction op = new XmlAction(createArguments("label", label), ActionType.GET_ADDRESS);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			if(xml.equals("") || xml == null){
				return null;
			}
			return (IAddress)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IMapDefinition> getMapDefinitionChild(
			IMapDefinition mapDefinitionParent) throws Exception {
		XmlAction op = new XmlAction(createArguments("child", mapDefinitionParent), ActionType.GET_MAP_DEFINITION);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IMapDefinition> getMapDefinitionParent() throws Exception {
		XmlAction op = new XmlAction(createArguments("parent", ""), ActionType.GET_MAP_DEFINITION);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void delete(IMapDefinitionRelation relation) throws Exception {
		XmlAction op = new XmlAction(createArguments(relation), ActionType.DEF_DELETE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public IMapDefinitionRelation saveMapDefinitionRelation(
			IMapDefinitionRelation relation) throws Exception {
		XmlAction op = new XmlAction(createArguments(relation), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IMapDefinitionRelation)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void update(IMapDefinitionRelation relation) throws Exception {
		XmlAction op = new XmlAction(createArguments(relation), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void delete(IAddressMapDefinitionRelation addressMapDefinitionRelation)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(addressMapDefinitionRelation), ActionType.DEF_DELETE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public IAddressMapDefinitionRelation saveAddressMapDefinitionRelation(
			IAddressMapDefinitionRelation addressMapDefinitionRelation)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(addressMapDefinitionRelation), ActionType.DEF_SAVE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IAddressMapDefinitionRelation)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void update(IAddressMapDefinitionRelation addressMapDefinitionRelation)
			throws Exception {
		XmlAction op = new XmlAction(createArguments(addressMapDefinitionRelation), ActionType.DEF_UPDATE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public IAddressMapDefinitionRelation getAddressMapRelationByMapIdAndAddressId(
			int addressId, int mapId) throws Exception {
		XmlAction op = new XmlAction(createArguments(addressId, mapId), ActionType.GET_ADDRESS_MAP_DEFINITION_RELATION);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (IAddressMapDefinitionRelation)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<IZoneTerritoryMapping> getZoneTerritoryMappingByMapId(int mapId) throws Exception {
		XmlAction op = new XmlAction(createArguments(mapId), ActionType.GET_ZTMAPPING);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (List<IZoneTerritoryMapping>)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void saveZoneTerritoryMapping(IZoneTerritoryMapping zoneTerritoryMapping) throws Exception {
		XmlAction op = new XmlAction(createArguments(zoneTerritoryMapping), ActionType.SAVE_ZTMAPPING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public void saveZoneTerritoryMappings(List<IZoneTerritoryMapping> zoneTerritoryMapping) throws Exception {
		XmlAction op = new XmlAction(createArguments(zoneTerritoryMapping), ActionType.SAVE_ZTMAPPING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));;
		
	}

	@Override
	public void deleteZoneTerritoryMapping(IZoneTerritoryMapping zoneTerritoryMapping) throws Exception {
		XmlAction op = new XmlAction(createArguments(zoneTerritoryMapping), ActionType.DEL_ZTMAPPING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));;
	}

	@Override
	public void deleteZoneTerritoryMappings(List<IZoneTerritoryMapping> zoneTerritoryMapping) throws Exception {
		XmlAction op = new XmlAction(createArguments(zoneTerritoryMapping), ActionType.DEL_ZTMAPPING);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));;
	}

	@Override
	public IOpenLayersMapObject getOpenLayersMapObject(Integer id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ActionType.GET_OLMO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (IOpenLayersMapObject) xstream.fromXML(xml);
	}

	@Override
	public void saveOpenLayersMapObject(IOpenLayersMapObject openLayersMapObject) throws Exception {
		XmlAction op = new XmlAction(createArguments(openLayersMapObject), ActionType.SAVE_OLMO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<IOpenLayersMapObject> getOpenLayersMapObjects() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_OLMO);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IOpenLayersMapObject>) xstream.fromXML(xml);
	}

	@Override
	public void deleteOpenLayersMap(IOpenLayersMapObject openLayersMap) throws Exception {
		XmlAction op = new XmlAction(createArguments(openLayersMap), ActionType.DEL_OLMO);
		httpCommunicator.executeAction(op, xstream.toXML(op));
	}

	@Override
	public List<IMapDefinition> getMapDefinition(MapType type) throws Exception {
		XmlAction op = new XmlAction(createArguments(type), ActionType.GET_MAP_BY_TYPE);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<IMapDefinition>) xstream.fromXML(xml);
	}

	@Override
	public List<MapVanilla> getMapVanillaById(Integer id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ActionType.GET_MAP_VANILLA_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapVanilla>) xstream.fromXML(xml);
	}

	@Override
	public List<MapVanilla> getAllMapsVanilla() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_MAPS_VANILLA_LIST);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			if(xml == null || xml.equals("")){
				return new ArrayList<MapVanilla>();
			}
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public MapVanilla saveMapVanilla(MapVanilla map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), ActionType.SAVE_MAP_VANILLA);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (MapVanilla)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void deleteMapVanilla(MapVanilla map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), ActionType.DELETE_MAP_VANILLA);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		
	}

	@Override
	public void updateMapVanilla(MapVanilla map) throws Exception {
		XmlAction op = new XmlAction(createArguments(map), ActionType.UPDATE_MAP_VANILLA);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
	}

	@Override
	public List<MapDataSource> getAllMapsDataSource() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_MAPS_DATASOURCE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			if(xml == null || xml.equals("")){
				return new ArrayList<MapDataSource>();
			}
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public MapDataSet saveMapDataSet(MapDataSet dtS) throws Exception {
		XmlAction op = new XmlAction(createArguments(dtS), ActionType.SAVE_MAP_DATASET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			return (MapDataSet)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<MapDataSet> getAllMapsDataSet() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_MAPS_DATASET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
			if(xml == null || xml.equals("")){
				return new ArrayList<MapDataSet>();
			}
			return (List)xstream.fromXML(xml);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public List<MapDataSource> getMapDataSourceByName(String name) throws Exception {
		XmlAction op = new XmlAction(createArguments(name), ActionType.GET_MAP_DATASOURCE_BY_NAME);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapDataSource>) xstream.fromXML(xml);
	}

	@Override
	public void deleteMapDataSet(MapDataSet dtS) throws Exception {
		XmlAction op = new XmlAction(createArguments(dtS), ActionType.DELETE_MAP_DATASET);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
	}
/*
	@Override
	public List<MapDataSet> getMapDataSetById(Integer id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ActionType.GET_MAP_DATASOURCE_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapDataSet>) xstream.fromXML(xml);
	}
*/	
	@Override
	public List<MapDataSet> getMapDataSetByMapVanillaId(Integer id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ActionType.GET_MAP_DATASET_BY_MAPVANILLA_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapDataSet>) xstream.fromXML(xml);
	}

	@Override
	public List<MapDataSource> getMapDataSourceById(Integer id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ActionType.GET_MAP_DATASOURCE_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapDataSource>) xstream.fromXML(xml);
	}

	@Override
	public void updateMapDataSource(MapDataSource dtS) throws Exception {
		XmlAction op = new XmlAction(createArguments(dtS), ActionType.UPDATE_MAP_DATASOURCE);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void updateMapDataSet(MapDataSet dtS) throws Exception {
		XmlAction op = new XmlAction(createArguments(dtS), ActionType.UPDATE_MAP_DATASET);
		try {
			String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public List<MapDataSet> getMapDataSetById(Integer id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ActionType.GET_MAPDATASET_BY_ID);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapDataSet>) xstream.fromXML(xml);
	}

	@Override
	public List<MapServer> getMapServers() throws Exception {
		XmlAction op = new XmlAction(createArguments(), ActionType.GET_MAP_SERVERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapServer>) xstream.fromXML(xml);
	}

	@Override
	public List<MapLayer> getLayers(MapServer server) throws Exception {
		XmlAction op = new XmlAction(createArguments(server), ActionType.GET_MAP_LAYERS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapLayer>) xstream.fromXML(xml);
	}

	@Override
	public MapServer manageMapServer(MapServer server, ManageAction action) throws Exception {
		XmlAction op = new XmlAction(createArguments(server, action), ActionType.MANAGE_MAP_SERVER);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (MapServer) xstream.fromXML(xml);
	}

	@Override
	public List<MapServer> getArcgisServices(MapServer server) throws Exception {
		XmlAction op = new XmlAction(createArguments(server), ActionType.GET_ARCGIS_SERVICES);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (List<MapServer>) xstream.fromXML(xml);
	}

	@Override
	public Map<String, Map<Integer, ZoneMetadataMapping>> getMetadataMappings(int id) throws Exception {
		XmlAction op = new XmlAction(createArguments(id), ActionType.GET_METADATA_MAPPINGS);
		String xml = httpCommunicator.executeAction(op, xstream.toXML(op));
		return (Map<String, Map<Integer, ZoneMetadataMapping>>) xstream.fromXML(xml);
	}
	@Override
	public void saveMetadataMappingsFromZones(List<MapZone> zones, MapVanilla map) throws Exception {
		XmlAction op = new XmlAction(createArguments(zones, map), ActionType.SAVE_METADATA_MAPPINGS);
		try {
			httpCommunicator.executeAction(op, xstream.toXML(op));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
