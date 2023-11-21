package bpm.vanilla.workplace.server.db;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import bpm.vanilla.workplace.shared.model.PlaceWebLog;

public class LogDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<PlaceWebLog> getAllLogs() {
		return (List<PlaceWebLog>) getHibernateTemplate().find("from PlaceWebLog");
	}
	
	@SuppressWarnings("unchecked")
	public PlaceWebLog findByPrimaryKey(int key) {
		List<PlaceWebLog> l = (List<PlaceWebLog>) getHibernateTemplate().find("from PlaceWebLog where id=" + key);
		if (l.isEmpty()){
			return null;
		}
		return l.get(0);
	}
	
	public int save(PlaceWebLog d) {
		d.setId((Integer)getHibernateTemplate().save(d));
		return d.getId();
	}

	public void delete(PlaceWebLog d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(PlaceWebLog d) {
		getHibernateTemplate().update(d);
	}
}
