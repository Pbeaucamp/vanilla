package bpm.vanilla.map.core.design;

public interface IFactoryModelObject {
	public IAddress createAdress();
	public IBuilding createBuilding();
	public ICell createCell();
	public IBuildingFloor createFloor();
	public IImage createImage();
	public IMapDefinition createMapDefinition();
	public IAddressRelation createAddressRelation();
	public IAddressZone createAddressZone();
	public IMapDefinitionRelation createMapDefinitionRelation();
	public IAddressMapDefinitionRelation createAddressMapDefinitionRelation();
}	
