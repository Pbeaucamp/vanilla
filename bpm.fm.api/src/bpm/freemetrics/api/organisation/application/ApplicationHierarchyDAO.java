package bpm.freemetrics.api.organisation.application;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ApplicationHierarchyDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<ApplicationHierarchy> findAll() {
		return getHibernateTemplate().find("from ApplicationHierarchy");
	}


	@SuppressWarnings("unchecked")
	public ApplicationHierarchy findByPrimaryKey(int key) {
		List<ApplicationHierarchy> c = getHibernateTemplate().find("from ApplicationHierarchy d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	public int save(ApplicationHierarchy d) {
//		getHibernateTemplate().flush();
//		List<Integer> res = getHibernateTemplate().find("select max(id) from ApplicationHierarchy");
//		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
//		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(ApplicationHierarchy d) {
		getHibernateTemplate().delete(d);
	}
	public void update(ApplicationHierarchy d) {
		getHibernateTemplate().update(d);
	}
	
	public List<ApplicationHierarchy> getApplicationChildrenByParentId(int parentid) {
		return getHibernateTemplate().find("from ApplicationHierarchy where parentId = " + parentid);
	}


	public void deleteByParentId(int id) {
		List<ApplicationHierarchy> hieras = getHibernateTemplate().find("from ApplicationHierarchy where parentId = " + id);
		if(hieras != null && hieras.size() > 0) {
			for(ApplicationHierarchy h : hieras) {
				delete(h);
			}
		}
	}


	public void deleteByChildId(int id) {
		List<ApplicationHierarchy> hieras = getHibernateTemplate().find("from ApplicationHierarchy where childId = " + id);
		if(hieras != null && hieras.size() > 0) {
			for(ApplicationHierarchy h : hieras) {
				delete(h);
			}
		}
	}


	public List<Integer> findRootApps(ApplicationManager appMgr) {
		
		List<Integer> ids = new ArrayList<Integer>();
		
		
		
		List<Integer> lst = appMgr.getDao().getHibernateTemplate().find("select id from Application");
		
		List<Integer> children = getHibernateTemplate().find("select distinct childId from ApplicationHierarchy");
		
		for(Integer i : lst) {
			if(!children.contains(i)) {
				ids.add(i);
			}
		}
		
		return ids;
	}
}
