package bpm.vanilla.map.model.kml.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.kml.IKmlSpecificationEntity;

public class KmlObject implements IKmlObject{
	
	private Integer id;
	private String kmlFileName;
	private List<IKmlSpecificationEntity> entities = new ArrayList<IKmlSpecificationEntity>();

	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public String getKmlFileName() {
		return kmlFileName;
	}

	@Override
	public void setKmlFileName(String kmlFileName) {
		this.kmlFileName = kmlFileName;
	}

	@Override
	public void addSpecificationEntity(IKmlSpecificationEntity entity) {
		this.entities.add(entity);
	}

	@Override
	public List<IKmlSpecificationEntity> getSpecificationsEntities() {
		return entities;
	}

	@Override
	public void removeSpecificationEntity(IKmlSpecificationEntity entity) {
		this.entities.remove(entity);
	}

}
