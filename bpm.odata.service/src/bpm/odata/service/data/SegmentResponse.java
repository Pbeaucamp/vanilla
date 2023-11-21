package bpm.odata.service.data;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;

public class SegmentResponse {

	private EdmEntitySet entitySet;
	
	private EntityCollection entityCollection;
	private Entity entity;
	
	public SegmentResponse(EdmEntitySet entitySet, EntityCollection entityCollection) {
		this.entitySet = entitySet;
		this.entityCollection = entityCollection;
	}
	
	public SegmentResponse(EdmEntitySet entitySet, Entity entity) {
		this.entitySet = entitySet;
		this.entity = entity;
	}
	
	public EdmEntitySet getEntitySet() {
		return entitySet;
	}
	
	public EntityCollection getEntityCollection() {
		return entityCollection;
	}
	
	public Entity getEntity() {
		return entity;
	}
}
