package bpm.vanilla.map.model.fusionmap.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;

public class FusionMapObject implements IFusionMapObject{


	
	private List<IFusionMapSpecificationEntity> entities = new ArrayList<IFusionMapSpecificationEntity>();
	private String description;
	private long id;
	private String name;
	private String swfFileName;
	private String type;
	
	@Override
	public void addSpecificationEntity(IFusionMapSpecificationEntity entity) {
		if (entity == null || getSpecificationsEntities().contains(entity)){
			return;
		}
		entities.add(entity);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<IFusionMapSpecificationEntity> getSpecificationsEntities() {
		return new ArrayList<IFusionMapSpecificationEntity>(entities);
	}

	@Override
	public String getSwfFileName() {
		return swfFileName;
	}

	@Override
	public void removeSpecificationEntity(IFusionMapSpecificationEntity entity) {
		entities.remove(entity);
		
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		
	}

	@Override
	public void setId(long id) {
		this.id = id;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	@Override
	public void setSwfFileName(String swfFileName) {
		this.swfFileName = swfFileName;
		
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

}
