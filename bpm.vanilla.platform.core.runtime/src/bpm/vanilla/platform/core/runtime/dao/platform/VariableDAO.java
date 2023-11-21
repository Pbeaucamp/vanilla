package bpm.vanilla.platform.core.runtime.dao.platform;


import java.util.List;

import bpm.vanilla.platform.core.beans.Variable;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class VariableDAO extends HibernateDaoSupport {
	
	public List<Variable> findAll() {
		return getHibernateTemplate().find("from Variable");
	}

	public List<Variable> findByPrimaryKey(Integer key) {
		return (List<Variable>) getHibernateTemplate().find("from Variable where id=" + key);
	}

	public Integer save(Variable d) {
		int id = (Integer)getHibernateTemplate().save(d);
		d.setId(id);
		return id;
	}

	public void delete(Variable d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Variable d) {
		getHibernateTemplate().update(d);
	}

	public Variable findByName(String variableName) {
		List<Variable> l = (List<Variable>) getHibernateTemplate().find("from Variable where name='" + variableName + "'");
		
		if (l.size()>0){
			return l.get(0);
		}
		
		return null;
	}
}
