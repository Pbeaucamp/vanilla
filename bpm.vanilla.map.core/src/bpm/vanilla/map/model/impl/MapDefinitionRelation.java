package bpm.vanilla.map.model.impl;

import bpm.vanilla.map.core.design.IMapDefinitionRelation;

public class MapDefinitionRelation implements IMapDefinitionRelation{
	
	private Integer id;
	private Integer parentId;
	private Integer childId;
	
	public MapDefinitionRelation(){
		
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public Integer getParentId() {
		return parentId;
	}

	@Override
	public Integer getChildId() {
		return childId;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Override
	public void setChildId(Integer childId) {
		this.childId = childId;
	}
	
	
}
