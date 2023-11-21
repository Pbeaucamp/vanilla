package bpm.freemetrics.api.organisation.group;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {
	private GroupDAO dao;

	public GroupManager() {
		super();
	}

	public void setDao(GroupDAO d) {
		this.dao = d;
	}

	public GroupDAO getDao() {
		return dao;
	}

	public List<Group> getGroups() {
		return dao.findAll();
	}

	public Group getGroupById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addGroup(Group d) throws Exception{
		if (dao.findForName(d.getName())  == null){
			return dao.save(d);
		}
		else{
			throw new Exception("This group name is already used");
		}

	}

	public void deleteGroup(Group d) {
		dao.delete(d);
	}

	public void updateGroup(Group d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This group doesnt exists");
		}
	}

	public List<Integer> getGroupIds() {

		List<Integer> res = new ArrayList<Integer>();
		List<Group> c = dao.findAll();
		for (Group group : c) {
			res.add(group.getId());
		}
		return res;
	}

	public boolean deleteGroupById(int id) {

		Group g = getGroupById(id);
		if(g == null)
			return true;

		return dao.delete(g);
	}

	public Group getGroupByName(String groupName) {
		return dao.findForName(groupName);
	}

}
