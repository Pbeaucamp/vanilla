package bpm.mdm.model.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import bpm.mdm.model.Entity;

public interface IEntitySerializer {

	public List<HashMap<String, Serializable>> loadEntity(Entity entity)throws Exception;
	public void saveEntity(Entity entity, List<HashMap<String, Serializable>> rows) throws Exception;
}
