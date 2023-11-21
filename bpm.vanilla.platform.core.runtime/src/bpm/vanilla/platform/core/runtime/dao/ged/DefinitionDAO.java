package bpm.vanilla.platform.core.runtime.dao.ged;

import java.util.List;

import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DefinitionDAO extends HibernateDaoSupport {

	public List<Definition> findAll() {
		return getHibernateTemplate().find("from Definition");
	}

	public Definition findByPrimaryKey(int key) {
		List<Definition> c = getHibernateTemplate().find("from Definition d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return (Definition)c.get(0);
		}
		else{
			return null;
		}
	}
	
	public Definition findByName(String name) {
		List<Definition> c = getHibernateTemplate().find("from Definition d where d.name='" +  name + "'");
		if (c != null && c.size() > 0){
			return (Definition)c.get(0);
		}
		else{
			return null;
		}
	}

	public int save(Definition d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(Definition d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(Definition d) {
		getHibernateTemplate().update(d);
	}

	public List<Definition> getCustomDefinitions() {
		return getHibernateTemplate().find("from Definition d where d.custom='1'");
	}
	
	public List<Definition> getNoSystemDefinitions() {
		return getHibernateTemplate().find("from Definition d where d.system='0'");
	}


}
