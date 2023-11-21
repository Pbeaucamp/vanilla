package bpm.mdm.model.runtime.exception;

import java.util.List;

import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;

public class IndexRebuildException extends Exception{
	private String entityUuid;
	private List<Row> primaryKeyErrorRows;
	
	public IndexRebuildException(Entity entity, List<Row> primaryKeyErrorRows){
		super("Unable to rebuild Entity " + entity.getName() + " Index because of Invalid primaryKeys");
		this.entityUuid = entity.getUuid();
		this.primaryKeyErrorRows = primaryKeyErrorRows;
	}

	/**
	 * @return the entityUuid
	 */
	public String getEntityUuid() {
		return entityUuid;
	}

	/**
	 * @return the primaryKeyErrorRows
	 */
	public List<Row> getPrimaryKeyErrorRows() {
		return primaryKeyErrorRows;
	}
	
	
}
