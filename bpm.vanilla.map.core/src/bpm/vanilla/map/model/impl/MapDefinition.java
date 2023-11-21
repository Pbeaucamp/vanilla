package bpm.vanilla.map.model.impl;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.IMapDefinitionRelation;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.kml.IKmlObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;

public class MapDefinition implements IMapDefinition{
	
	private Integer id;
	private String label;
	private String description;
	private String mapType;
	private Integer kmlObjectId;
	private IKmlObject kmlObject;
	private Long fusionMapObjectId;
	private IFusionMapObject fusionMapObject;
	private IMapDefinitionRelation relation;
	private boolean hasChild;
	private List<IMapDefinition> mapChilds = new ArrayList<IMapDefinition>();
	private Integer openLayersObjectId;
	private IOpenLayersMapObject openLayersMapObject;
	
	@Override
	public Integer getId() {
		return this.id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getMapType() {
		return mapType;
	}

	@Override
	public void setMapType(String mapType) {
		this.mapType = mapType;
	}
	
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public IKmlObject getKmlObject() {
		return kmlObject;
	}

	@Override
	public void setKmlObject(IKmlObject kmlObject) {
		this.kmlObject = kmlObject;
	}

	@Override
	public IFusionMapObject getFusionMapObject() {
		return fusionMapObject;
	}

	@Override
	public void setFusionMapObject(IFusionMapObject fusionMapObject) {
		this.fusionMapObject = fusionMapObject;
	}

	@Override
	public Long getFusionMapObjectId() {
		return fusionMapObjectId;
	}

	@Override
	public Integer getKmlObjectId() {
		return kmlObjectId;
	}

	@Override
	public void setFusionMapObjectId(Long fusionMapObjectId) {
		this.fusionMapObjectId = fusionMapObjectId;
	}

	@Override
	public void setKmlObjectId(Integer kmlObjectId) {
		this.kmlObjectId = kmlObjectId;
	}

	@Override
	public void addMapChild(IMapDefinition mapChild) {
		this.mapChilds.add(mapChild);
	}

	@Override
	public List<IMapDefinition> getMapChilds() {
		return mapChilds;
	}

	@Override
	public boolean hasChild() {
		return hasChild;
	}

	@Override
	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	@Override
	public void setMapChild(List<IMapDefinition> mapChilds) {
		this.mapChilds = mapChilds;
	}

	@Override
	public void setMapRelation(IMapDefinitionRelation relation) {
		this.relation = relation;
	}

	@Override
	public IMapDefinitionRelation getMapDefinitionRelation() {
		return relation;
	}

	public void setOpenLayersObjectId(Integer openLayersObjectId) {
		this.openLayersObjectId = openLayersObjectId;
	}

	public Integer getOpenLayersObjectId() {
		return openLayersObjectId;
	}

	@Override
	public IOpenLayersMapObject getOpenLayersMapObject() {
		return openLayersMapObject;
	}

	@Override
	public void setOpenLayersMapObject(IOpenLayersMapObject openLayersMapObject) {
		this.openLayersMapObject = openLayersMapObject;
	}
}
