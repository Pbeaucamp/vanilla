package bpm.mdm.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.Model;

/**
 * Allow to detect which entities must have their index rebuilt
 * @author ludo
 *
 */
public class ModelUpdateChecker {
	
	private Model model;
	
	public ModelUpdateChecker(Model model){
		this.model = model;
	}
	/**
	 * 
	 * @param newModel : the  newModel submited
	 * @return
	 */
	public HashMap<Entity, Entity> listIndexEntityRebuild(Model newModel){
		HashMap<Entity, Entity> l = new HashMap<Entity, Entity>();
		for(Entity e : model.getEntities()){
			Entity newEntity = null;
			
			for(Entity n : newModel.getEntities()){
				if (e.getUuid().equals(n.getUuid())){
					newEntity = n;
					break;
				}
			}
			
			if (newEntity != null){
				if (needIndexUpdate(e, newEntity)){
					l.put(e, newEntity);
				}
			}
		}
		return l;
	}
	
	private boolean needIndexUpdate(Entity entity, Entity newEntity) {
		List<Attribute> pk = entity.getAttributesId();
		List<Attribute> newPk = newEntity.getAttributesId();
		
		List<Attribute> newAttribs = new ArrayList<Attribute>();
		
		
		for(Attribute a : newPk){
			
			Attribute found = null;
			for(Attribute _a : pk){
				if (_a.getUuid().equals(a.getUuid())){
					found = _a;
					break;
				}
			}
			if (found == null){
				newAttribs.add(a);
			}
		}
		
		List<Attribute> deprecAttribs = new ArrayList<Attribute>();
		for(Attribute a : pk){
			
			Attribute found = null;
			for(Attribute _a : newPk){
				if (_a.getUuid().equals(a.getUuid())){
					found = _a;
					break;
				}
			}
			if (found == null){
				newAttribs.add(a);
			}
		}
		
		if (deprecAttribs.isEmpty() && newAttribs.isEmpty()){
			return false;
		}
		return true;
	}
}
