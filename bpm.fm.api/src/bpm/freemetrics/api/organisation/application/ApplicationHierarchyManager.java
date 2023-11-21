package bpm.freemetrics.api.organisation.application;

import java.util.List;

public class ApplicationHierarchyManager {

	private ApplicationHierarchyDAO dao;
	
	public ApplicationHierarchyManager(){}

	public void setDao(ApplicationHierarchyDAO dao) {
		this.dao = dao;
	}

	public ApplicationHierarchyDAO getDao() {
		return dao;
	}
	
	public List<ApplicationHierarchy> findAll() {
		return dao.findAll();
	}
	
	public List<Integer> findRootApps(ApplicationManager appMgr) {
		return dao.findRootApps(appMgr);
	}
	
	public ApplicationHierarchy findById(int id) {
		return dao.findByPrimaryKey(id);
	}
	
	public List<ApplicationHierarchy> getApplicationChildrenByParentId(int parentid) {
		return dao.getApplicationChildrenByParentId(parentid);
	}
	
	public void delete(ApplicationHierarchy a) {
		dao.delete(a);
	}
	
	public int save(ApplicationHierarchy a) {
		return dao.save(a);
	}
	
	public void update(ApplicationHierarchy a) {
		dao.update(a);
	}

	public void deleteByParentId(int id) {
		dao.deleteByParentId(id);
	}

	public void deleteByChildId(int id) {
		dao.deleteByChildId(id);
	}
}
