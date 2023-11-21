package bpm.freemetrics.api.features.actions;

import java.util.List;

public class ActionManager {
	private ActionDAO dao;

	public ActionManager() {
		super();
	}

	public void setDao(ActionDAO d) {
		this.dao = d;
	}

	public ActionDAO getDao() {
		return dao;
	}

	public List<Action> getActions() {
		return dao.findAll();
	}

	public Action getActionById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addAction(Action d) throws Exception{
	
		if (dao.getActionByName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This Action name is already used");
		}

	}

	public void delAction(Action d) {
		dao.delete(d);
	}

	public boolean updateAction(Action d) throws Exception {

		if (dao.findByPrimaryKey(d.getId())!= null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This Action doesnt exists");
		}
	}

	public boolean deleteActionById(int acId) {
		Action act = dao.findByPrimaryKey(acId);

			if(act != null){
				dao.delete(act);
			}else {
				return true;
			}
			
		return dao.findByPrimaryKey(acId) == null;
	}

	public Action getActionByName(String name) {
		return dao.getActionByName(name);
	}
}
