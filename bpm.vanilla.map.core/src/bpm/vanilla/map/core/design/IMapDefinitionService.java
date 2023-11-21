package bpm.vanilla.map.core.design;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;

public interface IMapDefinitionService {
	
	public enum MapType {
		OPEN_GIS,
		FUSION_MAP,
		OPEN_LAYER,
		FREEMETRICS,
		CLASSIC
	}
	
	public enum ManageAction {
		ADD,
		EDIT,
		REMOVE
	}
	
	public void configure(Object config);
	
	public IAddress getAddress(int addressId) throws Exception;
	public void delete(IAddress address) throws Exception;
	public List<IAddress> getAddressChild(IAddress addressParent) throws Exception;
	public List<IAddress> getAddressParent() throws Exception;
	public List<IAddress> getAllAddress() throws Exception;
	public IAddress getAddressByLabel(String label) throws Exception;
	public IAddress saveAddress(IAddress address) throws Exception;
	public void update(IAddress address) throws Exception;
	public List<IBuilding> getBuildingsForAddress(int addressId) throws Exception;
	
	
	public void delete(IBuilding building) throws Exception;
	public IBuilding getBuilding(int buildingId) throws Exception;
	public List<IBuilding> getAllBuilding() throws Exception;
	public IBuilding saveBuilding(IBuilding building) throws Exception;
	public void update(IBuilding building) throws Exception;
	public List<IBuildingFloor> getFloorsForBuilding(int buildingId) throws Exception;
	public List<ICell> getCellsForBuilding(int buildingId) throws Exception;
	
	
	public void delete(IBuildingFloor floor) throws Exception;
	public IBuildingFloor getFloor(int floorId) throws Exception;
	public List<IBuildingFloor> getAllFloor() throws Exception;
	public IBuildingFloor saveFloor(IBuildingFloor floor) throws Exception;
	public void update(IBuildingFloor floor) throws Exception;
	public List<ICell> getCellsForFloor(int floorId) throws Exception;
	

	public void delete(ICell cell) throws Exception;
	public ICell getCell(int cellId) throws Exception;
	public List<ICell> getAllCell() throws Exception;
	public ICell saveCell(ICell cell) throws Exception;
	public void update(ICell cell) throws Exception;
	

	public IImage getImage(int imageId) throws Exception;
	public List<IImage> getAllImage() throws Exception;
	public IImage saveImage(IImage img) throws Exception;
	
	
	public IMapDefinition getMapDefinition(int mapDefinitionId) throws Exception;
	public List<IMapDefinition> getAllMapDefinition() throws Exception;
	public List<IMapDefinition> getMapDefinitionChild(IMapDefinition mapDefinitionParent) throws Exception;
	public List<IMapDefinition> getMapDefinitionParent() throws Exception;
	@Deprecated
	public List<IMapDefinition> getMapDefinitionFromAddressId(int addressId) throws Exception;
	public IMapDefinition saveMapDefinition(IMapDefinition mapDef) throws Exception;
	public List<IMapDefinition> getMapDefinition(MapType type) throws Exception;
	public void update(IMapDefinition mapDefinition) throws Exception;
	public void delete(IMapDefinition mapDefinition) throws Exception;

	
	
	public IAddressRelation saveAddressRelation(IAddressRelation relation) throws Exception;
	public void update(IAddressRelation relation) throws Exception;
	public void delete(IAddressRelation relation) throws Exception;
	

	public IMapDefinitionRelation saveMapDefinitionRelation(IMapDefinitionRelation relation) throws Exception;
	public void update(IMapDefinitionRelation relation) throws Exception;
	public void delete(IMapDefinitionRelation relation) throws Exception;
	
	
	public IAddressZone saveAddressZone(IAddressZone addressZoneRelation) throws Exception;
	public void update(IAddressZone addressZoneRelation) throws Exception;
	public void delete(IAddressZone addressZoneRelation) throws Exception;
	public IAddressZone getAddressZoneByZoneAndAddressId(int addressId, long zoneId) throws Exception;
	
	
	public IAddressMapDefinitionRelation saveAddressMapDefinitionRelation(IAddressMapDefinitionRelation addressMapDefinitionRelation) throws Exception;
	public void update(IAddressMapDefinitionRelation addressMapDefinitionRelation) throws Exception;
	public void delete(IAddressMapDefinitionRelation addressMapDefinitionRelation) throws Exception;
	public IAddressMapDefinitionRelation getAddressMapRelationByMapIdAndAddressId(int addressId, int mapId) throws Exception;
	
	
	public void saveZoneTerritoryMapping(IZoneTerritoryMapping zoneTerritoryMapping) throws Exception;
	public void saveZoneTerritoryMappings(List<IZoneTerritoryMapping> zoneTerritoryMapping) throws Exception;
	public List<IZoneTerritoryMapping> getZoneTerritoryMappingByMapId(int mapId) throws Exception;
	public void deleteZoneTerritoryMapping(IZoneTerritoryMapping zoneTerritoryMapping) throws Exception;
	public void deleteZoneTerritoryMappings(List<IZoneTerritoryMapping> zoneTerritoryMapping) throws Exception;
	
	
	public IOpenLayersMapObject getOpenLayersMapObject(Integer id) throws Exception;
	public void saveOpenLayersMapObject(IOpenLayersMapObject openLayersMapObject) throws Exception;
	public List<IOpenLayersMapObject> getOpenLayersMapObjects() throws Exception;
	public void deleteOpenLayersMap(IOpenLayersMapObject openLayersMap) throws Exception;
	
	public List<MapVanilla> getMapVanillaById(Integer id) throws Exception;
	public List<MapVanilla> getAllMapsVanilla() throws Exception;
	public MapVanilla saveMapVanilla(MapVanilla map) throws Exception;
	public void deleteMapVanilla(MapVanilla map) throws Exception;
	public void updateMapVanilla(MapVanilla map) throws Exception;
	
	public List<MapDataSource> getAllMapsDataSource() throws Exception;
	public List<MapDataSource> getMapDataSourceByName(String name) throws Exception;
	public List<MapDataSource> getMapDataSourceById(Integer id) throws Exception; 
	public void updateMapDataSource(MapDataSource dtS) throws Exception;
	
	public MapDataSet saveMapDataSet(MapDataSet dtS) throws Exception;
	public List<MapDataSet> getAllMapsDataSet() throws Exception;
	public void deleteMapDataSet(MapDataSet dtS) throws Exception;
	//public List<MapDataSet> getMapDataSetById(Integer id) throws Exception;
	public List<MapDataSet> getMapDataSetByMapVanillaId(Integer id) throws Exception;
	public void updateMapDataSet(MapDataSet dtS) throws Exception;

	public List<MapDataSet> getMapDataSetById(Integer id) throws Exception;
	
	public List<MapServer> getMapServers() throws Exception;
	public MapServer manageMapServer(MapServer server, ManageAction action) throws Exception;
	public List<MapLayer> getLayers(MapServer server) throws Exception;
	public List<MapServer> getArcgisServices(MapServer server) throws Exception;

	public Map<String, Map<Integer, ZoneMetadataMapping>> getMetadataMappings(int id) throws Exception;
	public void saveMetadataMappingsFromZones(List<MapZone> zones, MapVanilla map) throws Exception;
}
