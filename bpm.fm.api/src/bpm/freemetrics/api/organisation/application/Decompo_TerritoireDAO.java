package bpm.freemetrics.api.organisation.application;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class Decompo_TerritoireDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Decompo_Territoire> findAll() {
		return getHibernateTemplate().find("from Decompo_Territoire");
	}

	@SuppressWarnings("unchecked")
	public Decompo_Territoire findByPrimaryKey(int key) {
		List<Decompo_Territoire> c = getHibernateTemplate().find("from Decompo_Territoire t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Decompo_Territoire d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Decompo_Territoire");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);

		return id;
	}

	public boolean delete(Decompo_Territoire d) {
		getHibernateTemplate().delete(d);

		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(Decompo_Territoire d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public Decompo_Territoire getDecompo_TerritoireForParentAndChildId(
			int parentId, int childId) {
		List<Decompo_Territoire> c = getHibernateTemplate().find("from Decompo_Territoire t where t.parent_Assoc_Terr_TypDecTerr_ID=" +parentId+" AND t.child_App_ID =" +childId);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Decompo_Territoire> getForParentId(int id) {
		return getHibernateTemplate().find("from Decompo_Territoire t where t.parent_Assoc_Terr_TypDecTerr_ID=" +  id);

	}

	@SuppressWarnings("unchecked")
	public List<Decompo_Territoire> getForChildAppId(int applId) {
		return getHibernateTemplate().find("from Decompo_Territoire t where t.child_App_ID=" +  applId);
	}


}
