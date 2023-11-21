package bpm.profiling.database.dao;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

import bpm.profiling.runtime.core.Connection;

public class ConnectionDao extends HibernateDaoSupport {

	public List<Connection> getAll(){
		return getHibernateTemplate().find("from Connection");
	}
	
	public void add(Connection c){
		int id = (Integer)getHibernateTemplate().save(c);
		c.setId(id);
	}
	
	public void delete(Connection c){
		getHibernateTemplate().delete(c);
	
	}
	
	public void update(Connection c){
		getHibernateTemplate().update(c);
	
	}

	public Connection getById(int id) {
		List<Connection> l = getHibernateTemplate().find("from Connection where id=" + id);
		
		if (l.size() > 0){
			return l.get(0);
		}
		return null;
	}

}


