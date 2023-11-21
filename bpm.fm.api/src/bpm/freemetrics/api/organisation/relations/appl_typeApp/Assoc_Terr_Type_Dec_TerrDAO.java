package bpm.freemetrics.api.organisation.relations.appl_typeApp;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;



public class Assoc_Terr_Type_Dec_TerrDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Assoc_Terr_Type_Dec_Terr> findAll() {
		return getHibernateTemplate().find("from Assoc_Terr_Type_Dec_Terr");
	}

	@SuppressWarnings("unchecked")
	public Assoc_Terr_Type_Dec_Terr findByPrimaryKey(int key) {
		List<Assoc_Terr_Type_Dec_Terr> c = getHibernateTemplate().find("from Assoc_Terr_Type_Dec_Terr t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Assoc_Terr_Type_Dec_Terr d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Assoc_Terr_Type_Dec_Terr");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(Assoc_Terr_Type_Dec_Terr d) {
		getHibernateTemplate().delete(d);
		return findByPrimaryKey(d.getChild_TypeDecTerr_ID()) == null;

	}
	public void update(Assoc_Terr_Type_Dec_Terr d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<Assoc_Terr_Type_Dec_Terr> getChildsIdForAppId(int parentAppId) {
		return getHibernateTemplate().find("from Assoc_Terr_Type_Dec_Terr t where t.parent_App_ID=" +  parentAppId);
	}

	@SuppressWarnings("unchecked")
	public Assoc_Terr_Type_Dec_Terr getByChildAndParentId(int childId, int parentId) {
		List<Assoc_Terr_Type_Dec_Terr> c = getHibernateTemplate().find("from Assoc_Terr_Type_Dec_Terr t where t.child_TypeDecTerr_ID=" +childId+" AND t.parent_App_ID = "+parentId);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Assoc_Terr_Type_Dec_Terr> getByChildTypeDecomTerrId(int typDecId) {
		return getHibernateTemplate().find("from Assoc_Terr_Type_Dec_Terr t where t.child_TypeDecTerr_ID=" +typDecId);
	}


}
