package bpm.vanilla.map.dao.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;
import org.json.JSONArray;
import org.json.JSONObject;

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
import bpm.vanilla.map.core.design.MapServer.TypeServer;
import bpm.vanilla.map.core.design.MapVanilla;
import bpm.vanilla.map.core.design.MapZone;
import bpm.vanilla.map.core.design.ZoneMetadataMapping;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;
import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.kml.IKmlSpecificationEntity;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObjectProperty;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;
import bpm.vanilla.map.wrapper.servlets.CommunicatorHelper;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DatabaseMapDefinitionService extends HibernateDaoSupport implements IMapDefinitionService {

	private static final String TYPE_MAP_SERVER = "MapServer";
	private static final String WMS = "WMSServer";
	private static final String WMTS = "WMTS/1.0.0/WMTSCapabilities.xml";

	private static final String PUBLIC = "public";

	/**
	 * save the given Address in database
	 * 
	 * @param address
	 * @throws Exception
	 * 
	 */
	@Override
	public IAddress saveAddress(IAddress address) throws Exception {
		if (address == null) {
			throw new Exception("Cannot save a null IAddress");
		}

		int id = (Integer) getHibernateTemplate().save(address);
		address.setId(id);

		IAddressRelation relation = address.getAddressRelation();
		if (relation != null) {
			relation.setChildId(id);
			getHibernateTemplate().save(relation);
		}

		return address;
	}

	/**
	 * save the given Building in database
	 * 
	 * @param building
	 * @throws Exception
	 */
	@Override
	public IBuilding saveBuilding(IBuilding building) throws Exception {
		if (building == null) {
			throw new Exception("Cannot save a null IBuilding");
		}

		IImage img = building.getImage();
		if (img != null) {
			img = saveImage(img);
			building.setImageId(img.getId());
		}

		Integer buildingId = (Integer) getHibernateTemplate().save(building);

		for (IBuildingFloor floor : building.getFloors()) {
			floor.setBuildingId(buildingId);
			saveFloor(floor);
			for (ICell cell : floor.getCells()) {
				cell.setFloorId(floor.getId());
				cell.setBuildingId(buildingId);
			}
		}
		building.setId(buildingId);
		return building;
	}

	/**
	 * save the given Cell in database
	 * 
	 * @param cell
	 * @throws Exception
	 */
	@Override
	public ICell saveCell(ICell cell) throws Exception {
		if (cell == null) {
			throw new Exception("Cannot save a null ICell");
		}

		IImage img = cell.getImage();
		if (img != null) {
			saveImage(img);
			cell.setImageId(img.getId());
		}

		Integer id = (Integer) getHibernateTemplate().save(cell);
		cell.setId(id);
		return cell;
	}

	/**
	 * save the given Floor in database
	 * 
	 * @param floor
	 * @throws Exception
	 */
	@Override
	public IBuildingFloor saveFloor(IBuildingFloor floor) throws Exception {
		if (floor == null) {
			throw new Exception("Cannot save a null IFloor");
		}

		IImage img = floor.getImage();
		if (img != null) {
			saveImage(img);
			floor.setImageId(img.getId());
		}

		Integer floorId = (Integer) getHibernateTemplate().save(floor);
		for (ICell cell : floor.getCells()) {
			cell.setFloorId(floorId);
			cell.setBuildingId(floor.getBuildingId());
			saveCell(cell);
		}
		floor.setId(floorId);

		return floor;
	}

	@Override
	public IImage saveImage(IImage img) throws Exception {
		Integer imageId = (Integer) getHibernateTemplate().save(img);
		img.setId(imageId);
		return img;
	}

	@Override
	public IMapDefinition saveMapDefinition(IMapDefinition mapDef) throws Exception {
		if (mapDef.getFusionMapObject() != null && mapDef.getKmlObjectId() == null) {
			mapDef.setFusionMapObjectId(mapDef.getFusionMapObject().getId());
		}

		Integer mapDefId = (Integer) getHibernateTemplate().save(mapDef);
		mapDef.setId(mapDefId);

		IMapDefinitionRelation relation = mapDef.getMapDefinitionRelation();
		if (relation != null) {
			relation.setChildId(mapDefId);
			getHibernateTemplate().save(relation);
		}

		return mapDef;

	}

	@Override
	public IAddress getAddress(int addressId) throws Exception {
		List<IAddress> l = (List<IAddress>) getHibernateTemplate().find("from Address where id = " + addressId);

		if (l.isEmpty()) {
			return null;
		}
		rebuildAddress(l.get(0));
		return (IAddress) l.get(0);
	}

	@Override
	public List<IAddress> getAddressParent() throws Exception {
		List<IAddress> l = getHibernateTemplate().find("from Address");
		List<IAddressRelation> relations = getHibernateTemplate().find("from AddressRelation");

		if (l.isEmpty()) {
			return null;
		}

		List<IAddress> listAddress = new ArrayList<IAddress>();
		for (IAddress address : l) {
			boolean found = false;
			for (IAddressRelation relation : relations) {
				if (relation.getChildId().equals(address.getId())) {
					found = true;
					break;
				}
				if (relation.getParentId().equals(address.getId())) {
					address.setHasChild(true);
				}
			}
			if (!found) {
				rebuildAddress(address);
				listAddress.add(address);
			}
		}

		return listAddress;
	}

	@Override
	public List<IAddress> getAddressChild(IAddress addressParent) throws Exception {
		List<IAddressRelation> relations = getHibernateTemplate().find("from AddressRelation where parentId=" + addressParent.getId());

		if (relations.isEmpty()) {
			return null;
		}

		List<IAddress> addressChilds = new ArrayList<IAddress>();
		for (IAddressRelation relation : relations) {
			List<IAddress> addresses = getHibernateTemplate().find("from Address where id=" + relation.getChildId());
			if (!addresses.isEmpty()) {
				rebuildAddressChild(addresses.get(0));
				addressChilds.add(addresses.get(0));
			}
		}

		addressParent.setAddressChild(addressChilds);

		return addressChilds;
	}

	public void rebuildAddress(IAddress address) {
		// We rebuild the relation between this address and her maps
		List<IAddressMapDefinitionRelation> addressMapDefinitionRelations = (List<IAddressMapDefinitionRelation>) getHibernateTemplate().find("from AddressMapDefinitionRelation where addressId=" + address.getId());
		if (addressMapDefinitionRelations != null) {
			for (IAddressMapDefinitionRelation addressMap : addressMapDefinitionRelations) {
				List<IMapDefinition> mapDefinitions = (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition where id=" + addressMap.getMapDefinitionId());
				if (!mapDefinitions.isEmpty()) {
					rebuildMapDefinition(mapDefinitions.get(0));
					address.addMap(mapDefinitions.get(0));
				}
			}
		}

		// We rebuild the relation between this address and her parent
		List<IAddressRelation> relations = (List<IAddressRelation>) getHibernateTemplate().find("from AddressRelation where childId=" + address.getId());
		if (!relations.isEmpty()) {
			address.setAddressRelation(relations.get(0));
		}

		List<IAddressZone> addressZones = (List<IAddressZone>) getHibernateTemplate().find("from AddressZone where addressId=" + address.getId());
		if (addressZones != null) {
			for (IAddressZone addressZone : addressZones) {
				List<IFusionMapSpecificationEntity> entities = (List<IFusionMapSpecificationEntity>) getHibernateTemplate().find("from FusionMapSpecificationEntity where id=" + addressZone.getMapZoneId());
				if (!entities.isEmpty()) {
					address.addAddressZones(entities.get(0));
				}
			}
		}
	}

	public void rebuildAddressChild(IAddress address) {
		// We rebuild the relation between this address and her maps
		List<IAddressMapDefinitionRelation> addressMapDefinitionRelations = (List<IAddressMapDefinitionRelation>) getHibernateTemplate().find("from AddressMapDefinitionRelation where addressId=" + address.getId());
		if (addressMapDefinitionRelations != null) {
			for (IAddressMapDefinitionRelation addressMap : addressMapDefinitionRelations) {
				List<IMapDefinition> mapDefinitions = (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition where id=" + addressMap.getMapDefinitionId());
				if (!mapDefinitions.isEmpty()) {
					address.addMap(mapDefinitions.get(0));
				}
			}
		}

		// We rebuild the relation between this address and her parent
		List<IAddressRelation> relations = (List<IAddressRelation>) getHibernateTemplate().find("from AddressRelation where childId=" + address.getId());
		if (!relations.isEmpty()) {
			address.setAddressRelation(relations.get(0));
		}

		List<IAddressRelation> allRelations = getHibernateTemplate().find("from AddressRelation");
		for (IAddressRelation relation : allRelations) {
			if (relation.getParentId().equals(address.getId())) {
				address.setHasChild(true);
			}
		}

		List<IAddressZone> addressZones = (List<IAddressZone>) getHibernateTemplate().find("from AddressZone where addressId=" + address.getId());
		if (addressZones != null) {
			for (IAddressZone addressZone : addressZones) {
				List<IFusionMapSpecificationEntity> entities = (List<IFusionMapSpecificationEntity>) getHibernateTemplate().find("from FusionMapSpecificationEntity where id=" + addressZone.getMapZoneId());
				if (!entities.isEmpty()) {
					address.addAddressZones(entities.get(0));
				}
			}
		}
	}

	@Override
	public IBuilding getBuilding(int buildingId) throws Exception {
		List<IBuilding> l = getHibernateTemplate().find("from Building where id = " + buildingId);

		if (l.isEmpty()) {
			return null;
		}
		rebuildBuilding(l.get(0));
		return (IBuilding) l.get(0);
	}

	@Override
	public List<IBuilding> getAllBuilding() {
		List<IBuilding> l = (List<IBuilding>) getHibernateTemplate().find("from Building");

		if (l.isEmpty()) {
			return null;
		}
		for (IBuilding building : l) {
			rebuildBuilding(building);
		}

		return (List<IBuilding>) l;
	}

	@Override
	public List<IBuilding> getBuildingsForAddress(int addressId) {
		List l = getHibernateTemplate().find("from Building where addressId = " + addressId);

		if (l.isEmpty()) {
			return null;
		}
		return (List<IBuilding>) l;
	}

	@Override
	public ICell getCell(int cellId) throws Exception {
		List<ICell> l = getHibernateTemplate().find("from Cell where id = " + cellId);

		if (l.isEmpty()) {
			return null;
		}
		rebuildCell(l.get(0));
		return (ICell) l.get(0);
	}

	@Override
	public List<ICell> getAllCell() {
		List l = getHibernateTemplate().find("from Cell");

		if (l.isEmpty()) {
			return null;
		}
		return (List<ICell>) l;
	}

	@Override
	public List<ICell> getCellsForBuilding(int buildingId) {
		List l = getHibernateTemplate().find("from Cell where buildingId = " + buildingId);

		if (l.isEmpty()) {
			return null;
		}
		return (List<ICell>) l;
	}

	@Override
	public List<ICell> getCellsForFloor(int floorId) {
		List l = getHibernateTemplate().find("from Cell where floorId = " + floorId);

		if (l.isEmpty()) {
			return null;
		}
		return (List<ICell>) l;
	}

	@Override
	public IBuildingFloor getFloor(int floorId) throws Exception {
		List<IBuildingFloor> l = getHibernateTemplate().find("from BuildingFloor where id = " + floorId);

		if (l.isEmpty()) {
			return null;
		}
		rebuildFloor(l.get(0));
		return (IBuildingFloor) l.get(0);
	}

	@Override
	public List<IBuildingFloor> getAllFloor() {
		List l = getHibernateTemplate().find("from BuildingFloor");

		if (l.isEmpty()) {
			return null;
		}
		return (List<IBuildingFloor>) l;
	}

	@Override
	public List<IBuildingFloor> getFloorsForBuilding(int buildingId) {
		List l = getHibernateTemplate().find("from BuildingFloor where buildingId = " + buildingId);

		if (l.isEmpty()) {
			return null;
		}
		return (List<IBuildingFloor>) l;
	}

	@Override
	public IImage getImage(int imageId) throws Exception {

		return null;
	}

	@Override
	public List<IImage> getAllImage() {

		return null;
	}

	@Override
	public IMapDefinition getMapDefinition(int mapDefinitionId) throws Exception {
		List<IMapDefinition> l = getHibernateTemplate().find("from MapDefinition where id = " + mapDefinitionId);

		if (l.isEmpty()) {
			return null;
		}

		IMapDefinition mapDef = l.get(0);
		rebuildMapDefinition(mapDef);

		return mapDef;
	}

	@Override
	public List<IMapDefinition> getAllMapDefinition() {
		List<IMapDefinition> l = (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition");

		if (l.isEmpty()) {
			return null;
		}

		for (IMapDefinition mapDef : l) {
			rebuildMapDefinition(mapDef);
		}

		return (List<IMapDefinition>) l;
	}

	public void rebuildMapDefinition(IMapDefinition mapDef) {
		// We rebuild the relation between this address and her parent
		List<IMapDefinitionRelation> relations = (List<IMapDefinitionRelation>) getHibernateTemplate().find("from MapDefinitionRelation where childId=" + mapDef.getId());
		if (!relations.isEmpty()) {
			mapDef.setMapRelation(relations.get(0));
		}

		if (mapDef.getFusionMapObjectId() != null) {
			List<IFusionMapObject> l = getHibernateTemplate().find("from FusionMapObject where id=" + mapDef.getFusionMapObjectId());

			if (!l.isEmpty()) {
				IFusionMapObject fusionMap = l.get(0);

				List<IFusionMapSpecificationEntity> entities = getHibernateTemplate().find("from FusionMapSpecificationEntity where fusionMapObjectId=" + fusionMap.getId());
				for (IFusionMapSpecificationEntity entity : entities) {
					fusionMap.addSpecificationEntity(entity);
				}

				mapDef.setFusionMapObject(fusionMap);
			}
		}
		if (mapDef.getKmlObjectId() != null) {
			List<IKmlObject> l = getHibernateTemplate().find("from KmlObject where id=" + mapDef.getKmlObjectId());

			if (!l.isEmpty()) {
				IKmlObject kmlObject = l.get(0);

				List<IKmlSpecificationEntity> entities = getHibernateTemplate().find("from KmlSpecificationEntity where kmlObjectId=" + kmlObject.getId());
				for (IKmlSpecificationEntity entity : entities) {
					kmlObject.addSpecificationEntity(entity);
				}

				mapDef.setKmlObject(kmlObject);
			}
		}
		if (mapDef.getOpenLayersObjectId() != null) {
			try {
				IOpenLayersMapObject obj = getOpenLayersMapObject(mapDef.getOpenLayersObjectId());
				mapDef.setOpenLayersMapObject(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void rebuildBuilding(IBuilding building) {
		for (IBuildingFloor floor : (List<IBuildingFloor>) getHibernateTemplate().find("from BuildingFloor where buildingId=" + building.getId())) {
			rebuildFloor(floor);
			building.addFloor(floor);
		}

		for (ICell cell : (List<ICell>) getHibernateTemplate().find("from Cell where buildingId=" + building.getId())) {
			rebuildCell(cell);
			building.addCell(cell);
		}

		List<IImage> l = (List<IImage>) getHibernateTemplate().find("from Image where id=" + building.getImageId());
		if (!l.isEmpty()) {
			building.setImage(l.get(0));
		}
	}

	public void rebuildFloor(IBuildingFloor floor) {
		for (ICell cell : (List<ICell>) getHibernateTemplate().find("from Cell where floorId=" + floor.getId())) {
			rebuildCell(cell);
			floor.addCell(cell);
		}

		List<IImage> l = (List<IImage>) getHibernateTemplate().find("from Image where id=" + floor.getImageId());
		if (!l.isEmpty()) {
			floor.setImage(l.get(0));
		}
	}

	public void rebuildCell(ICell cell) {
		List<IImage> l = (List<IImage>) getHibernateTemplate().find("from Image where id=" + cell.getImageId());
		if (!l.isEmpty()) {
			cell.setImage(l.get(0));
		}
	}

	@Override
	public void update(IAddress address) throws Exception {
		getHibernateTemplate().update(address);

		IAddressRelation relation = address.getAddressRelation();
		if (relation != null) {
			getHibernateTemplate().saveOrUpdate(relation);
		}
	}

	@Override
	public void update(IBuilding building) throws Exception {
		int buildingId = building.getId();

		IBuilding storedBuilding = getBuilding(buildingId);

		for (IBuildingFloor f : storedBuilding.getFloors()) {
			boolean found = false;
			for (IBuildingFloor flTmp : building.getFloors()) {
				if (f.getId() == flTmp.getId()) {
					found = true;
					break;
				}
			}
			if (!found) {
				int nbFloors = building.getNbFloors();
				building.setNbFloors(nbFloors - 1);
				building.removeFloor(f);
				delete(f);
			}
		}

		for (IBuildingFloor f : building.getFloors()) {
			if (f.getId() != null && f.getId() > 0) {
				update(f);
			}
			else {
				f.setBuildingId(buildingId);
				saveFloor(f);
			}
		}

		for (ICell c : storedBuilding.getCells()) {
			boolean found = false;
			for (ICell ceTmp : building.getCells()) {
				if (c.getId().equals(ceTmp.getId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				delete(c);
			}
		}

		IImage img = building.getImage();
		if (img != null) {
			if (building.getImageId() != null && building.getImageId() > 0) {
				getHibernateTemplate().update(img);
			}
			else {
				saveImage(img);
				building.setImageId(img.getId());
			}
		}

		getHibernateTemplate().update(building);
	}

	@Override
	public void update(ICell cell) throws Exception {
		IImage img = cell.getImage();
		if (img != null) {
			if (cell.getImageId() != null && cell.getImageId() > 0) {
				getHibernateTemplate().update(img);
			}
			else {
				saveImage(img);
				cell.setImageId(img.getId());
			}
		}

		getHibernateTemplate().update(cell);
	}

	@Override
	public void update(IBuildingFloor floor) throws Exception {
		int floorId = floor.getId();

		IBuildingFloor storedFloor = getFloor(floorId);

		for (ICell c : storedFloor.getCells()) {
			boolean found = false;
			for (ICell celTmp : floor.getCells()) {
				if (c.getId().equals(celTmp.getId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				floor.removeCell(c);
				delete(c);
			}
		}

		for (ICell c : floor.getCells()) {
			if (c.getId() != null && c.getId() > 0) {
				update(c);
			}
			else {
				c.setFloorId(floorId);
				c.setBuildingId(floor.getBuildingId());
				saveCell(c);
			}
		}

		IImage img = floor.getImage();
		if (img != null) {
			if (floor.getImageId() != null && floor.getImageId() > 0) {
				getHibernateTemplate().update(img);
			}
			else {
				saveImage(img);
				floor.setImageId(img.getId());
			}
		}

		getHibernateTemplate().update(floor);
	}

	@Override
	public void update(IMapDefinition mapDefinition) throws Exception {
		getHibernateTemplate().update(mapDefinition);

		IMapDefinitionRelation relation = mapDefinition.getMapDefinitionRelation();
		if (relation != null) {
			getHibernateTemplate().saveOrUpdate(relation);
		}
	}

	@Override
	public void delete(IAddress address) throws Exception {
		List<IAddressRelation> relationsParent = (List<IAddressRelation>) getHibernateTemplate().find("from AddressRelation where parentId=" + address.getId());
		List<IAddressRelation> relationsChild = (List<IAddressRelation>) getHibernateTemplate().find("from AddressRelation where childId=" + address.getId());
		if (!relationsParent.isEmpty()) {
			for (IAddressRelation relation : relationsParent) {
				getHibernateTemplate().delete(relation);
			}
		}
		if (!relationsChild.isEmpty()) {
			for (IAddressRelation relation : relationsChild) {
				getHibernateTemplate().delete(relation);
			}
		}

		getHibernateTemplate().delete(address);
	}

	@Override
	public void delete(IBuilding building) throws Exception {
		for (IBuildingFloor f : building.getFloors()) {
			for (ICell c : f.getCells()) {
				if (c.getImage() != null) {
					getHibernateTemplate().delete(c.getImage());
				}
				getHibernateTemplate().delete(c);
			}

			if (f.getImage() != null) {
				getHibernateTemplate().delete(f.getImage());
			}
			getHibernateTemplate().delete(f);
		}

		if (building.getImage() != null) {
			getHibernateTemplate().delete(building.getImage());
		}

		getHibernateTemplate().delete(building);
	}

	@Override
	public void delete(ICell cell) throws Exception {
		if (cell.getImage() != null) {
			getHibernateTemplate().delete(cell.getImage());
		}
		getHibernateTemplate().delete(cell);
	}

	@Override
	public void delete(IBuildingFloor floor) throws Exception {
		for (ICell c : floor.getCells()) {
			if (c.getImage() != null) {
				getHibernateTemplate().delete(c.getImage());
			}
			getHibernateTemplate().delete(c);
		}
		getHibernateTemplate().delete(floor);
	}

	@Override
	public void delete(IMapDefinition mapDefinition) throws Exception {
		List<IMapDefinitionRelation> relationsParent = (List<IMapDefinitionRelation>) getHibernateTemplate().find("from MapDefinitionRelation where parentId=" + mapDefinition.getId());
		List<IMapDefinitionRelation> relationsChild = (List<IMapDefinitionRelation>) getHibernateTemplate().find("from MapDefinitionRelation where childId=" + mapDefinition.getId());
		if (!relationsParent.isEmpty()) {
			for (IMapDefinitionRelation relation : relationsParent) {
				getHibernateTemplate().delete(relation);
			}
		}

		if (!relationsChild.isEmpty()) {
			for (IMapDefinitionRelation relation : relationsChild) {
				getHibernateTemplate().delete(relation);
			}
		}

		getHibernateTemplate().delete(mapDefinition);
	}

	@Override
	public void delete(IAddressRelation relation) throws Exception {
		getHibernateTemplate().delete(relation);
	}

	@Override
	public IAddressRelation saveAddressRelation(IAddressRelation relation) throws Exception {
		if (relation == null) {
			throw new Exception("Cannot save a null IAddressRelation");
		}

		int id = (Integer) getHibernateTemplate().save(relation);
		relation.setId(id);

		return relation;
	}

	@Override
	public void update(IAddressRelation relation) throws Exception {
		getHibernateTemplate().update(relation);
	}

	@Override
	public List<IAddress> getAllAddress() throws Exception {
		List<IAddress> l = (List<IAddress>) getHibernateTemplate().find("from Address");

		if (l.isEmpty()) {
			return null;
		}
		return l;
	}

	@Override
	public List<IMapDefinition> getMapDefinitionFromAddressId(int addressId) throws Exception {

		return null;
	}

	/**
	 * We don't need that here, that's why it is not implemented
	 */
	@Override
	public void configure(Object config) {

	}

	@Override
	public void delete(IAddressZone addressZoneRelation) throws Exception {
		getHibernateTemplate().delete(addressZoneRelation);
	}

	@Override
	public IAddressZone saveAddressZone(IAddressZone addressZoneRelation) throws Exception {
		if (addressZoneRelation == null) {
			throw new Exception("Cannot save a null IAddressZone");
		}

		int id = (Integer) getHibernateTemplate().save(addressZoneRelation);
		addressZoneRelation.setId(id);

		return addressZoneRelation;
	}

	@Override
	public void update(IAddressZone addressZoneRelation) throws Exception {
		getHibernateTemplate().update(addressZoneRelation);
	}

	@Override
	public IAddressZone getAddressZoneByZoneAndAddressId(int addressId, long zoneId) throws Exception {
		List<IAddressZone> l = (List<IAddressZone>) getHibernateTemplate().find("from AddressZone where addressId=" + addressId + " and mapZoneId=" + zoneId);

		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	@Override
	public IAddress getAddressByLabel(String label) throws Exception {
		List<IAddress> l = (List<IAddress>) getHibernateTemplate().find("from Address where label='" + label + "'");

		if (l.isEmpty()) {
			return null;
		}

		rebuildAddress(l.get(0));
		return l.get(0);
	}

	@Override
	public List<IMapDefinition> getMapDefinitionParent() throws Exception {
		List<IMapDefinition> l = getHibernateTemplate().find("from MapDefinition");
		List<IMapDefinitionRelation> relations = getHibernateTemplate().find("from MapDefinitionRelation");

		if (l.isEmpty()) {
			return null;
		}

		List<IMapDefinition> listMapDefinitions = new ArrayList<IMapDefinition>();
		for (IMapDefinition mapDefinition : l) {
			boolean found = false;
			for (IMapDefinitionRelation relation : relations) {
				if (relation.getChildId().equals(mapDefinition.getId())) {
					found = true;
					break;
				}
				if (relation.getParentId().equals(mapDefinition.getId())) {
					mapDefinition.setHasChild(true);
				}
			}
			if (!found) {
				rebuildMapDefinition(mapDefinition);
				listMapDefinitions.add(mapDefinition);
			}
		}

		return listMapDefinitions;
	}

	@Override
	public List<IMapDefinition> getMapDefinitionChild(IMapDefinition mapDefinitionParent) throws Exception {
		List<IMapDefinitionRelation> relations = getHibernateTemplate().find("from MapDefinitionRelation where parentId=" + mapDefinitionParent.getId());

		if (relations.isEmpty()) {
			return null;
		}

		List<IMapDefinition> mapDefinitionChilds = new ArrayList<IMapDefinition>();
		for (IMapDefinitionRelation relation : relations) {
			List<IMapDefinition> mapDefinitions = getHibernateTemplate().find("from MapDefinition where id=" + relation.getChildId());
			if (!mapDefinitions.isEmpty()) {
				rebuildMapDefinitionChild(mapDefinitions.get(0));
				mapDefinitionChilds.add(mapDefinitions.get(0));
			}
		}

		mapDefinitionParent.setMapChild(mapDefinitionChilds);

		return mapDefinitionChilds;
	}

	public void rebuildMapDefinitionChild(IMapDefinition mapDefinitionChild) {
		// We rebuild the relation between this address and her parent
		List<IMapDefinitionRelation> relations = (List<IMapDefinitionRelation>) getHibernateTemplate().find("from MapDefinitionRelation where childId=" + mapDefinitionChild.getId());
		if (!relations.isEmpty()) {
			mapDefinitionChild.setMapRelation(relations.get(0));
		}

		List<IMapDefinitionRelation> allRelations = getHibernateTemplate().find("from MapDefinitionRelation");
		for (IMapDefinitionRelation relation : allRelations) {
			if (relation.getParentId().equals(mapDefinitionChild.getId())) {
				mapDefinitionChild.setHasChild(true);
			}
		}

		if (mapDefinitionChild.getFusionMapObjectId() != null) {
			List<IFusionMapObject> l = getHibernateTemplate().find("from FusionMapObject where id=" + mapDefinitionChild.getFusionMapObjectId());

			if (!l.isEmpty()) {
				IFusionMapObject fusionMap = l.get(0);

				List<IFusionMapSpecificationEntity> entities = getHibernateTemplate().find("from FusionMapSpecificationEntity where fusionMapObjectId=" + fusionMap.getId());
				for (IFusionMapSpecificationEntity entity : entities) {
					fusionMap.addSpecificationEntity(entity);
				}

				mapDefinitionChild.setFusionMapObject(fusionMap);
			}
		}
		if (mapDefinitionChild.getKmlObjectId() != null) {
			List<IKmlObject> l = getHibernateTemplate().find("from KmlObject where id=" + mapDefinitionChild.getKmlObjectId());

			if (!l.isEmpty()) {
				IKmlObject kmlObject = l.get(0);

				List<IKmlSpecificationEntity> entities = getHibernateTemplate().find("from KmlSpecificationEntity where kmlObjectId=" + kmlObject.getId());
				for (IKmlSpecificationEntity entity : entities) {
					kmlObject.addSpecificationEntity(entity);
				}

				mapDefinitionChild.setKmlObject(kmlObject);
			}
		}
	}

	@Override
	public void delete(IMapDefinitionRelation relation) throws Exception {
		getHibernateTemplate().delete(relation);
	}

	@Override
	public IMapDefinitionRelation saveMapDefinitionRelation(IMapDefinitionRelation relation) throws Exception {
		if (relation == null) {
			throw new Exception("Cannot save a null IMapDefinitionRelation");
		}

		int id = (Integer) getHibernateTemplate().save(relation);
		relation.setId(id);

		return relation;
	}

	@Override
	public void update(IMapDefinitionRelation relation) throws Exception {
		getHibernateTemplate().update(relation);
	}

	@Override
	public void delete(IAddressMapDefinitionRelation addressMapDefinitionRelation) throws Exception {
		getHibernateTemplate().delete(addressMapDefinitionRelation);
	}

	@Override
	public IAddressMapDefinitionRelation saveAddressMapDefinitionRelation(IAddressMapDefinitionRelation addressMapDefinitionRelation) throws Exception {
		if (addressMapDefinitionRelation == null) {
			throw new Exception("Cannot save a null IAddressMapDefinitionRelation");
		}

		int id = (Integer) getHibernateTemplate().save(addressMapDefinitionRelation);
		addressMapDefinitionRelation.setId(id);

		return addressMapDefinitionRelation;
	}

	@Override
	public void update(IAddressMapDefinitionRelation addressMapDefinitionRelation) throws Exception {
		getHibernateTemplate().delete(addressMapDefinitionRelation);
	}

	@Override
	public IAddressMapDefinitionRelation getAddressMapRelationByMapIdAndAddressId(int addressId, int mapId) throws Exception {
		List<IAddressMapDefinitionRelation> l = (List<IAddressMapDefinitionRelation>) getHibernateTemplate().find("from AddressMapDefinitionRelation where addressId=" + addressId + " and mapDefinitionId=" + mapId);

		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	@Override
	public List<IZoneTerritoryMapping> getZoneTerritoryMappingByMapId(int mapId) {

		List<IZoneTerritoryMapping> mappings = getHibernateTemplate().find("from ZoneTerritoryMapping where ZTM_map_id = '" + mapId + "'");

		return mappings;
	}

	@Override
	public void saveZoneTerritoryMapping(IZoneTerritoryMapping zoneTerritoryMapping) {
		getHibernateTemplate().saveOrUpdate(zoneTerritoryMapping);
	}

	@Override
	public void saveZoneTerritoryMappings(List<IZoneTerritoryMapping> zoneTerritoryMapping) throws Exception {
		for (IZoneTerritoryMapping ma : zoneTerritoryMapping) {
			getHibernateTemplate().saveOrUpdate(ma);
		}
	}

	@Override
	public void deleteZoneTerritoryMapping(IZoneTerritoryMapping zoneTerritoryMapping) throws Exception {
		try {
			getHibernateTemplate().delete(zoneTerritoryMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteZoneTerritoryMappings(List<IZoneTerritoryMapping> zoneTerritoryMapping) throws Exception {
		try {
			for (IZoneTerritoryMapping ma : zoneTerritoryMapping) {
				getHibernateTemplate().delete(ma);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IOpenLayersMapObject getOpenLayersMapObject(Integer id) throws Exception {

		IOpenLayersMapObject obj = (IOpenLayersMapObject) getHibernateTemplate().find("from OpenLayersMapObject where id = '" + id + "'").get(0);

		try {
			List<IOpenLayersMapObjectProperty> properties = getHibernateTemplate().find("from OpenLayersMapObjectProperty where openLayersMapObjectId = '" + id + "'");
			obj.setProperties(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			List<IOpenLayersMapSpecificationEntity> entities = getHibernateTemplate().find("from OpenLayersMapSpecificationEntity where openLayersMapObjectId = '" + id + "'");
			obj.setEntities(entities);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public void saveOpenLayersMapObject(IOpenLayersMapObject openLayersMapObject) throws Exception {

		int id = -1;

		if (openLayersMapObject.getId() != null) {
			getHibernateTemplate().update(openLayersMapObject);
			id = openLayersMapObject.getId();

			// deleteObjectEntitiesAndProperties(openLayersMapObject);
		}
		else {
			id = (Integer) getHibernateTemplate().save(openLayersMapObject);
		}

		List<IOpenLayersMapObjectProperty> properties = openLayersMapObject.getProperties();
		if (properties != null && properties.size() > 0) {
			for (IOpenLayersMapObjectProperty prop : properties) {
				prop.setOpenLayersMapObjectId(id);
				getHibernateTemplate().saveOrUpdate(prop);
			}
		}

		List<IOpenLayersMapSpecificationEntity> entities = openLayersMapObject.getEntities();
		if (entities != null && entities.size() > 0) {
			for (IOpenLayersMapSpecificationEntity ent : entities) {
				ent.setOpenLayersMapObjectId(id);
				getHibernateTemplate().saveOrUpdate(ent);
			}
		}
	}

	private void deleteObjectEntitiesAndProperties(IOpenLayersMapObject openLayersMapObject) {
		getHibernateTemplate().deleteAll(openLayersMapObject.getEntities());
		getHibernateTemplate().deleteAll(openLayersMapObject.getProperties());
	}

	@Override
	public List<IOpenLayersMapObject> getOpenLayersMapObjects() throws Exception {
		List<IOpenLayersMapObject> maps = getHibernateTemplate().find("from OpenLayersMapObject");
		for (IOpenLayersMapObject map : maps) {
			try {
				List<IOpenLayersMapObjectProperty> properties = getHibernateTemplate().find("from OpenLayersMapObjectProperty where openLayersMapObjectId = '" + map.getId() + "'");
				map.setProperties(properties);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				List<IOpenLayersMapSpecificationEntity> entities = getHibernateTemplate().find("from OpenLayersMapSpecificationEntity where openLayersMapObjectId = '" + map.getId() + "'");
				map.setEntities(entities);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return maps;
	}

	@Override
	public void deleteOpenLayersMap(IOpenLayersMapObject openLayersMap) throws Exception {
		deleteObjectEntitiesAndProperties(openLayersMap);
		getHibernateTemplate().delete(openLayersMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IMapDefinition> getMapDefinition(MapType type) {
		switch (type) {
		case FUSION_MAP:
			return (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition where mapType = " + IMapDefinition.MAP_TYPE_FUSION);
		case OPEN_GIS:
			return (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition where mapType = '" + IMapDefinition.MAP_TYPE_OPEN_GIS + "'");
		case CLASSIC:
			return (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition where mapType = " + IMapDefinition.MAP_TYPE_CLASSIC);
		case FREEMETRICS:
			return (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition where mapType = " + IMapDefinition.MAP_TYPE_FM);
		case OPEN_LAYER:
			return (List<IMapDefinition>) getHibernateTemplate().find("from MapDefinition where mapType = " + IMapDefinition.MAP_TYPE_OPEN_LAYER);
		default:
			return new ArrayList<IMapDefinition>();
		}
	}

	@Override
	public List<MapVanilla> getMapVanillaById(Integer id) throws Exception {
		List<MapVanilla> map = (List<MapVanilla>) getHibernateTemplate().find("from MapVanilla where id=" + id);
		if (map == null || map.isEmpty()) {
			return new ArrayList<MapVanilla>();
		}

		map.get(0).setDataSetList(getMapDataSetByMapVanillaId(map.get(0).getId()));
		List<MapDataSet> dataSetList = map.get(0).getDataSetList();
		MapDataSource dataSource = getMapDataSourceById(dataSetList.get(0).getIdDataSource(), map.get(0).getType()).get(0);
		if (dataSetList.size() > 0) {
			for (MapDataSet dataSet : dataSetList) {
				dataSet.setDataSource(dataSource);
			}
		}
		return map;
	}

	@Override
	public List<MapVanilla> getAllMapsVanilla() throws Exception {
		List<MapVanilla> listMapsVanilla = (List<MapVanilla>) getHibernateTemplate().find("from MapVanilla");

		MapDataSource dataSource;
		// MapDataSet dataSet;
		List<MapDataSet> dataSetList;
		for (MapVanilla map : listMapsVanilla) {
			// dataSet = getMapDataSetById(map.getIdDataSet()).get(0);
			// dataSource =
			// getMapDataSourceById(dataSet.getIdDataSource()).get(0);
			// dataSet.setDataSource(dataSource);
			// map.setDataSet(dataSet);
			try {
				dataSetList = getMapDataSetByMapVanillaId(map.getId());
				if (dataSetList.size() > 0) {
					dataSource = getMapDataSourceById(dataSetList.get(0).getIdDataSource(), map.getType()).get(0);
					for (MapDataSet dataSet : dataSetList) {
						dataSet.setDataSource(dataSource);
						map.setDataSetList(dataSetList);
					}
				}
			} catch(Exception e) {
//				e.printStackTrace();
			}

		}
		return (List<MapVanilla>) listMapsVanilla;
	}

	@Override
	public MapVanilla saveMapVanilla(MapVanilla map) throws Exception {
		if (map == null) {
			throw new Exception("Cannot save a null MapVanilla");

		}

		// MapDataSet dtS = saveMapDataSet(map.getDataSet());
		// map.setDataSet(dtS);
		// map.setIdDataSet(dtS.getId());
		List<MapDataSet> dataSetList = map.getDataSetList();
		// for(MapDataSet dtS : dataSetList) {
		// dtS = saveMapDataSet(dtS);
		// }
		// map.setDataSetList(dataSetList);
		Integer mapVanillaId = (Integer) getHibernateTemplate().save(map);
		map.setId(mapVanillaId);
		for (MapDataSet dtS : dataSetList) {
			dtS.setIdMapVanilla(mapVanillaId);
			dtS = saveMapDataSet(dtS);
		}

		return map;

	}

	@Override
	public void deleteMapVanilla(MapVanilla map) throws Exception {

		// ///ajouté par kevin///////
		List<MapDataSet> dataSetList = getMapDataSetByMapVanillaId(map.getId());
		for (MapDataSet dtS : dataSetList) {
			deleteMapDataSet(dtS);
		}
		// //////////////////////////
		List<MapVanilla> lesMaps = (List<MapVanilla>) getHibernateTemplate().find("from MapVanilla where id=" + map.getId());
		getHibernateTemplate().delete(map);
	}

	@Override
	public void updateMapVanilla(MapVanilla map) throws Exception {
		if (map != null) {

			List<MapDataSet> dataSetListBase = getMapDataSetByMapVanillaId(map.getId());
			List<MapDataSet> dataSetListNew = map.getDataSetList();
			List<MapDataSet> RemoveList = new ArrayList<MapDataSet>();
			for (MapDataSet dtS : dataSetListNew) {
				dtS = saveMapDataSet(dtS);
				updateMapDataSet(dtS);
			}

			BASE: for (MapDataSet dtSBase : dataSetListBase) {
				for (MapDataSet dtS : dataSetListNew) {
					if (dtSBase.getId() == dtS.getId()) {
						continue BASE;
					}
				}
				RemoveList.add(dtSBase);
			}
			for (MapDataSet dtS : RemoveList) {
				deleteMapDataSet(dtS);
			}

			// getHibernateTemplate().update(map.getDataSetList());
			if (map.getDataSetList() != null && !map.getDataSetList().isEmpty()) {
				getHibernateTemplate().update(map.getDataSetList().get(0).getDataSource());
			}
			getHibernateTemplate().update(map);
		}
		else {
			throw new Exception("Cannot update a null MapVanilla");
		}
	}

	@Override
	public List<MapDataSource> getAllMapsDataSource() throws Exception {
		List<MapDataSource> listeMapsDataSource = (List<MapDataSource>) getHibernateTemplate().find("from MapDataSource");
		return (List<MapDataSource>) listeMapsDataSource;
	}

	@Override
	public List<MapDataSet> getAllMapsDataSet() throws Exception {
		List<MapDataSet> listeMapsDataSet = (List<MapDataSet>) getHibernateTemplate().find("from MapDataSet");
		return (List<MapDataSet>) listeMapsDataSet;
	}

	@Override
	public MapDataSet saveMapDataSet(MapDataSet dtS) throws Exception {
		boolean existDatasource = false;
		boolean existDataSet = false;
		if (dtS == null) {
			throw new Exception("Cannot save a null MapDataSet");

		}

		List<MapDataSource> listMapsDataSource = getAllMapsDataSource();
		List<MapDataSet> listMapDataSet = getAllMapsDataSet();

		for (MapDataSource dataSource : listMapsDataSource) {
			if (dataSource.getNomDataSource().equals(dtS.getDataSource().getNomDataSource())) {
				existDatasource = true;
				dtS.setDataSource(dataSource);
				break;
			}
		}

		for (MapDataSet dataSet : listMapDataSet) {
			if (dataSet.getId() == dtS.getId()) {
				existDataSet = true;
			}

		}

		if (dtS.getParentName() != null) {
			MapDataSet parent = (MapDataSet) getHibernateTemplate().find("from MapDataSet where name = '" + dtS.getParentName() + "'").get(0);
			dtS.setParentId(parent.getId());
		}
		
		int maxId = 0;
		for(MapDataSource ds : listMapsDataSource) {
			if(ds.getId() > maxId) {
				maxId = ds.getId();
			}
		}
		
		

		if (existDatasource && existDataSet) {
			dtS.setIdDataSource(dtS.getDataSource().getId());
		}
		else if (!existDatasource && existDataSet) {
			dtS.getDataSource().setId(maxId + 1);
			Integer mapDataSourceId = (Integer) getHibernateTemplate().save(dtS.getDataSource());
			dtS.getDataSource().setId(mapDataSourceId);
			dtS.setIdDataSource(mapDataSourceId);
		}
		else if (existDatasource && !existDataSet) {
			dtS.setIdDataSource(dtS.getDataSource().getId());
			Integer mapDataSetId = (Integer) getHibernateTemplate().save(dtS);
			dtS.setId(mapDataSetId);
		}
		else {
			dtS.getDataSource().setId(maxId + 1);
			Integer mapDataSourceId = (Integer) getHibernateTemplate().save(dtS.getDataSource());
			dtS.getDataSource().setId(mapDataSourceId);
			dtS.setIdDataSource(mapDataSourceId);

			Integer mapDataSetId = (Integer) getHibernateTemplate().save(dtS);
			dtS.setId(mapDataSetId);
		}
		return dtS;
	}

	@Override
	public List<MapDataSource> getMapDataSourceByName(String name) throws Exception {
		List<MapDataSource> dataSource = (List<MapDataSource>) getHibernateTemplate().find("from MapDataSource where nomDataSource='" + name + "'");
		return dataSource;
	}

	@Override
	public void deleteMapDataSet(MapDataSet dtS) throws Exception {

		List<MapDataSet> lesMapDataSet = (List<MapDataSet>) getHibernateTemplate().find("from MapDataSet where id=" + dtS.getId());
		getHibernateTemplate().delete(dtS);
	}

	/*
	 * @Override public List<MapDataSet> getMapDataSetById(Integer id) throws
	 * Exception { List<MapDataSet> dtS =
	 * (List<MapDataSet>)getHibernateTemplate().find("from MapDataSet where id="
	 * + id); return dtS; }
	 */

	@Override
	public List<MapDataSet> getMapDataSetByMapVanillaId(Integer id) throws Exception {
		List<MapDataSet> dtS = (List<MapDataSet>) getHibernateTemplate().find("from MapDataSet where idMapVanilla=" + id);
		return dtS;
	}

	@Override
	public List<MapDataSource> getMapDataSourceById(Integer id) throws Exception {
		return getMapDataSourceById(id, null);
	}
	
	public List<MapDataSource> getMapDataSourceById(Integer id, String type) throws Exception {
		if(type != null && type.equals("KML")) {
			List<MapDataSource> dtS = (List<MapDataSource>) getHibernateTemplate().find("from MapDatasourceKML where id=" + id);
			return dtS;
		}
		else if(type != null && type.equals("WFS")) {
			List<MapDataSource> dtS = (List<MapDataSource>) getHibernateTemplate().find("from MapDatasourceWFS where id=" + id);
			return dtS;
		}
		List<MapDataSource> dtS = (List<MapDataSource>) getHibernateTemplate().find("from MapDataSource where id=" + id);
		return dtS;
	}

	@Override
	public void updateMapDataSource(MapDataSource dtS) throws Exception {
		if (dtS != null) {
			getHibernateTemplate().update(dtS);
		}
		else {
			throw new Exception("Cannot update a null MapDataSource");
		}
	}

	@Override
	public void updateMapDataSet(MapDataSet dtS) throws Exception {
		if (dtS.getParentName() != null) {
			MapDataSet parent = (MapDataSet) getHibernateTemplate().find("from MapDataSet where name = '" + dtS.getParentName() + "'").get(0);
			dtS.setParentId(parent.getId());
		}
		if (dtS != null) {
			getHibernateTemplate().update(dtS);
		}
		else {
			throw new Exception("Cannot update a null MapDataSet");
		}
	}

	@Override
	public List<MapDataSet> getMapDataSetById(Integer id) throws Exception {
		List<MapDataSet> dtS = (List<MapDataSet>) getHibernateTemplate().find("from MapDataSet where id=" + id);
		MapVanilla v = getMapVanillaById(dtS.get(0).getIdMapVanilla()).get(0);
		for(MapDataSet ds : v.getDataSetList()) {
			if(ds.getId() == id) {
				dtS.clear();
				dtS.add(ds);
				return dtS;
			}
		}
//		MapDataSource dataSource = getMapDataSourceById(dtS.get(0).getIdDataSource()).get(0);
//		dtS.get(0).setDataSource(dataSource);
//		return dtS;
		return dtS;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<MapServer> getMapServers() throws Exception {
		List<MapServer> servers = (List<MapServer>) getHibernateTemplate().find("FROM MapServer");
		return servers;
	}

	@Override
	public MapServer manageMapServer(MapServer server, ManageAction action) throws Exception {
		switch (action) {
		case ADD:
			Integer id = (Integer) getHibernateTemplate().save(server);
			server.setId(id);
			break;
		case EDIT:
			getHibernateTemplate().saveOrUpdate(server);
			break;
		case REMOVE:
			getHibernateTemplate().delete(server);
			break;
		default:
			break;
		}
		return server;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<MapLayer> getLayers(MapServer server) throws Exception {
		List<MapLayer> mapLayers = new ArrayList<MapLayer>();

		String mapServerUrl = server.getUrl();
		if (server.getType() == TypeServer.WFS) {
			String parameters = mapServerUrl.toLowerCase().contains("getcapabilities") ? "" : "?SERVICE=WFS&REQUEST=GetCapabilities&VERSION=1.1.0";

			String serverCapabilities = CommunicatorHelper.sendGetMessage(mapServerUrl, parameters);

			Document document = org.dom4j.DocumentHelper.parseText(serverCapabilities);
			Element xmlElement = document.getRootElement();

			HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
			nameSpaceMap.put("wfs", xmlElement.getNamespaceURI());

			XPath xpath = new Dom4jXPath("//wfs:FeatureTypeList/wfs:FeatureType");
			SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext(nameSpaceMap);
			xpath.setNamespaceContext(nmsCtx);

			List<Node> layers = xpath.selectNodes(document);
			for (Node com : layers) {
				Element elem = (Element) com;

				Element elName = elem.element("Name");
				Element elTitle = elem.element("Title");

				String name = elName != null ? elName.getText() : null;
				String title = elTitle != null ? elTitle.getText() : null;
				String url = buildWFSUrl();

				if (name != null && !name.isEmpty()) {
					mapLayers.add(new MapLayer(name, title, url, server));
				}
			}
		}
		else {
			String parameters = mapServerUrl.toLowerCase().contains("getcapabilities") ? "" : "?SERVICE=WMS&REQUEST=GetCapabilities&VERSION=1.3.0";

			String serverCapabilities = CommunicatorHelper.sendGetMessage(mapServerUrl, parameters);

			Document document = org.dom4j.DocumentHelper.parseText(serverCapabilities);
			Element xmlElement = document.getRootElement();

			HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
			nameSpaceMap.put("wms", xmlElement.getNamespaceURI());

			XPath xpath = new Dom4jXPath("//wms:Layer/wms:Layer");
			SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext(nameSpaceMap);
			xpath.setNamespaceContext(nmsCtx);

			List<Node> layers = xpath.selectNodes(document);
			for (Node com : layers) {
				Element elem = (Element) com;

				Element elName = elem.element("Name");
				Element elTitle = elem.element("Title");

				String name = elName != null ? elName.getText() : null;
				String title = elTitle != null ? elTitle.getText() : null;

				if (name != null && !name.isEmpty()) {
					mapLayers.add(new MapLayer(name, title, server));
				}
			}
		}
		return mapLayers;
	}

	private String buildWFSUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MapServer> getArcgisServices(MapServer server) throws Exception {
		List<MapServer> mapServers = new ArrayList<MapServer>();
		
		if (server.getType() == TypeServer.ARCGIS) {

			String mapServerUrl = server.getUrl().endsWith("/") ? server.getUrl() : server.getUrl() + "/";
			String format = "?f=pjson";

			String serverPublicServices = CommunicatorHelper.sendGetMessage(mapServerUrl, format);

			JSONObject jsonObject = new JSONObject(serverPublicServices);
			JSONArray servicesJson = !jsonObject.isNull("services") ? jsonObject.getJSONArray("services") : null;
			if (servicesJson != null) {
				for (int i = 0; i < servicesJson.length(); i++) {
					try {
						JSONObject jsonPackage = (JSONObject) servicesJson.get(i);
						String name = !jsonPackage.isNull("name") ? jsonPackage.getString("name") : null;
						String type = !jsonPackage.isNull("type") ? jsonPackage.getString("type") : null;

						if (type.equalsIgnoreCase(TYPE_MAP_SERVER)) {
							if (name.contains(PUBLIC)) {
								name = name.substring(name.indexOf(PUBLIC) + PUBLIC.length() + 1);
							}

							String servicesHTML = CommunicatorHelper.sendGetMessage(mapServerUrl, "/" + name + "/" + TYPE_MAP_SERVER);

							// // We test if it support WMS
							Pattern pattern = Pattern.compile("<a\\s*href=\"(\\S*)\"\\s*>WMS<\\/a>");
							Matcher matcher = pattern.matcher(servicesHTML);
							if (matcher.find()) {
								String wmsUrl = matcher.group(1);

								mapServers.add(new MapServer(name, wmsUrl, TypeServer.WMS));
							}

							pattern = Pattern.compile("<a\\s*href=\"(\\S*)\"\\s*>WMTS<\\/a>");
							matcher = pattern.matcher(servicesHTML);
							if (matcher.find()) {
								String wmtsParameters = matcher.group(1);
								String wmtsUrl = mapServerUrl;

								String[] parameters = wmtsParameters.split("/");
								for (int j = 0; j < parameters.length; j++) {
									String param = parameters[j];
									if (!mapServerUrl.toLowerCase().contains(param.toLowerCase())) {
										wmtsUrl = wmtsUrl + wmtsParameters.substring(wmtsParameters.indexOf(param));
										break;
									}
								}
								
								MapServer wmtsServer = getWmtsInformations(new MapServer("", wmtsUrl));
								if (wmtsServer != null) {
									mapServers.add(wmtsServer);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		else if (server.getType() == TypeServer.WMTS) {
			MapServer wmtsServer = getWmtsInformations(new MapServer("", server.getUrl()));
			if (wmtsServer != null) {
				mapServers.add(wmtsServer);
			}
		}

		return mapServers;
	}

	public MapServer getWmtsInformations(MapServer server) {
		try {

			String mapServerUrl = server.getUrl();
			String parameters = mapServerUrl.toLowerCase().contains("wmtscapabilities") ? "" : "/1.0.0/WMTSCapabilities.xml";
			
			String wmtsXml = CommunicatorHelper.sendGetMessage(mapServerUrl, parameters);

			Document document = org.dom4j.DocumentHelper.parseText(wmtsXml);
			Element xmlElement = document.getRootElement();

			HashMap<String, String> nameSpaceMap = new HashMap<String, String>();
			nameSpaceMap.put("wmts", xmlElement.getNamespaceURI());

			XPath xpath = new Dom4jXPath("//wmts:Contents");
			SimpleNamespaceContext nmsCtx = new SimpleNamespaceContext(nameSpaceMap);
			xpath.setNamespaceContext(nmsCtx);

			List<Node> contents = xpath.selectNodes(document);
			for (Node com : contents) {
				Element elem = (Element) com;

				// We need to construct the URL
				String wmtsName = "";
				String wmtsUrl = "";
				String styleIdentifier = "";
				String tyleMatrixIdentifier = "";
				
				// Step one : We get the style
				Element layerElement = elem.element("Layer");
				if (layerElement != null) {
					
					Element elTitle = layerElement.element("Title");
					wmtsName = elTitle != null ? elTitle.getText() : null;

					// Step one : We get the resource
					List<Element> resourceElements = layerElement.elements("ResourceURL");
					if (resourceElements != null && !resourceElements.isEmpty()) {

						// We take the first resource which is a image
						for (Element item : resourceElements) {
							String resourceFormat = (String) (item.attribute("format") != null ? item.attribute("format").getData() : null);
							String resourceTemplate = (String) (item.attribute("template") != null ? item.attribute("template").getData() : null);

							if (resourceFormat != null && (resourceFormat.toLowerCase().contains("png") || resourceFormat.toLowerCase().contains("jpg"))) {
								wmtsUrl = resourceTemplate;

								break;
							}
						}
					}

					// Step two : We get the style
					List<Element> styleElements = layerElement.elements("Style");
					if (styleElements != null && !styleElements.isEmpty()) {
	
						// We take the first resource which is a image
						for (Element item : styleElements) {
							Element elIdentifier = item.element("Identifier");
	
							styleIdentifier = elIdentifier != null ? elIdentifier.getText() : null;
							break;
						}
					}
				}
				
				// Step three : We get the TileMatrixSet
				List<Element> tileMatrixElements = elem.elements("TileMatrixSet");
				if (tileMatrixElements != null && !tileMatrixElements.isEmpty()) {

					// We take the first resource which is a image
					for (Element item : tileMatrixElements) {
						Element elIdentifier = item.element("Identifier");

						tyleMatrixIdentifier = elIdentifier != null ? elIdentifier.getText() : null;
						break;
					}
				}
				
				if (wmtsUrl != null && !wmtsUrl.isEmpty()) {
					wmtsUrl = wmtsUrl.replace("{Style}", styleIdentifier);
					wmtsUrl = wmtsUrl.replace("{TileMatrixSet}", tyleMatrixIdentifier);
					wmtsUrl = wmtsUrl.replace("{TileMatrix}", "{z}");
					wmtsUrl = wmtsUrl.replace("{TileRow}", "{y}");
					wmtsUrl = wmtsUrl.replace("{TileCol}", "{x}");
					
					return new MapServer(wmtsName, wmtsUrl, TypeServer.WMTS);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Map<String, Map<Integer, ZoneMetadataMapping>> getMetadataMappings(int id) throws Exception {
		List<ZoneMetadataMapping> dtS = (List<ZoneMetadataMapping>) getHibernateTemplate().find("from ZoneMetadataMapping where datasetId=" + id);
		Map<String, Map<Integer, ZoneMetadataMapping>> mappings = new HashMap<String, Map<Integer,ZoneMetadataMapping>>();
		
		for(ZoneMetadataMapping m : dtS) {
			if(mappings.get(m.getZoneId()) == null) {
				mappings.put(m.getZoneId(), new HashMap<Integer, ZoneMetadataMapping>());
			}
			mappings.get(m.getZoneId()).put(m.getMetadataId(), m);
		}
		
		return mappings;
	}

	@Override
	public void saveMetadataMappingsFromZones(List<MapZone> zones, MapVanilla map) throws Exception {
		List<ZoneMetadataMapping> mappings = new ArrayList<ZoneMetadataMapping>();
		for(MapZone z : zones) {
			mappings.addAll(z.getMetadataMappings().values());
		}
		getHibernateTemplate().saveOrUpdateAll(mappings);
	}
}
