package bpm.vanilla.map.model.openlayers.impl;

import java.util.List;

import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObjectProperty;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;

public class OpenLayersMapObject implements IOpenLayersMapObject {

	private Integer id;
	private String type;
	private String xml;
	private List<IOpenLayersMapObjectProperty> properties;
	private List<IOpenLayersMapSpecificationEntity> entities;
	private String name;
	
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getXml() {
		return xml;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setXml(String xml) {
		this.xml = xml;
	}

	@Override
	public List<IOpenLayersMapObjectProperty> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(List<IOpenLayersMapObjectProperty> properties) {
		this.properties = properties;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setEntities(List<IOpenLayersMapSpecificationEntity> entities) {
		this.entities = entities;
	}

	public List<IOpenLayersMapSpecificationEntity> getEntities() {
		return entities;
	}

}
