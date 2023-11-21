package bpm.vanilla.platform.core.runtime.dao.ged;

import java.util.List;

import bpm.vanilla.platform.core.beans.ged.Category;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class CategoryDAO extends HibernateDaoSupport {
	
	public List<Category> findAll() {
		List<Category> result = getHibernateTemplate().find("from Category d where d.parentId is null");
		for(Category cat : result) {
			cat.setSubCategories(findChilds(cat.getId()));
		}
		return result;
	}

	public Category findByPrimaryKey(int key) {
		List<Category> c = getHibernateTemplate().find("from Category d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return (Category)c.get(0);
		}
		else{
			return null;
		}
	}

	public int save(Category d) {
		return (Integer) getHibernateTemplate().save(d);
	}
	
	public void update(Category c) {
		getHibernateTemplate().update(c);
	}
	
	public void delete(Category c) {
		getHibernateTemplate().delete(c);
	}

	public List<Category> findChilds(int id) {
		List<Category> result = getHibernateTemplate().find("from Category d where d.parentId=" +  id);
		for(Category cat : result) {
			cat.setSubCategories(findChilds(cat.getId()));
		}
		return result;
	}

}
