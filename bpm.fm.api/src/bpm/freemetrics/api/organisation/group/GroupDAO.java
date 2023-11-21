package bpm.freemetrics.api.organisation.group;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class GroupDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Group> findAll() {
		return getHibernateTemplate().find("from Group");
	}


	@SuppressWarnings("unchecked")
	public Group findByPrimaryKey(int key) {
		List<Group> c = getHibernateTemplate().find("from Group d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Group findForName(String name){
		List<Group> c = getHibernateTemplate().find("from Group d where d.name='" + name + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Group d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Group");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(Group d) {
		getHibernateTemplate().delete(d);

		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(Group d) {
		getHibernateTemplate().update(d);
	}


}
