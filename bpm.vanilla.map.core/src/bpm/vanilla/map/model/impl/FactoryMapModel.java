package bpm.vanilla.map.model.impl;

import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IAddressMapDefinitionRelation;
import bpm.vanilla.map.core.design.IAddressRelation;
import bpm.vanilla.map.core.design.IAddressZone;
import bpm.vanilla.map.core.design.IBuilding;
import bpm.vanilla.map.core.design.ICell;
import bpm.vanilla.map.core.design.IFactoryModelObject;
import bpm.vanilla.map.core.design.IBuildingFloor;
import bpm.vanilla.map.core.design.IImage;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.IMapDefinitionRelation;

public class FactoryMapModel implements IFactoryModelObject {

	@Override
	public IAddress createAdress() {
		return new Address();
	}

	@Override
	public IBuilding createBuilding() {
		return new Building();
	}

	@Override
	public ICell createCell() {
		return new Cell();
	}

	@Override
	public IBuildingFloor createFloor() {
		return new BuildingFloor();
	}

	@Override
	public IImage createImage() {
		return new Image();
	}

	@Override
	public IMapDefinition createMapDefinition() {
		return new MapDefinition();
	}

	@Override
	public IAddressRelation createAddressRelation() {
		return new AddressRelation();
	}

	@Override
	public IAddressZone createAddressZone() {
		return new AddressZone();
	}

	@Override
	public IMapDefinitionRelation createMapDefinitionRelation() {
		return new MapDefinitionRelation();
	}

	@Override
	public IAddressMapDefinitionRelation createAddressMapDefinitionRelation() {
		return new AddressMapDefinitionRelation();
	}

}
