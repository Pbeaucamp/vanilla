package bpm.freemetrics.api.organisation.observatoire;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ObservatoireDAO extends HibernateDaoSupport {

	public List<Observatoire> findByUserId(int userId) {
		return getHibernateTemplate().find("from Observatoire o where o.id in (select a.obsId from ObservatoiresUsers a where a.userId=" + userId + ")");
	}

	public Observatoire getById(int id) {
		List<Observatoire> l = getHibernateTemplate().find("from Observatoire where id=" + id);
		if (!l.isEmpty()) {
			return l.get(0);
		}
		else {
			return null;
		}
	}

	public List<Observatoire> findAll() {
		return getHibernateTemplate().find("from Observatoire");
	}

	public Observatoire finByName(String name) {
		List<Observatoire> l = getHibernateTemplate().find("from Observatoire where name='" + name + "'");
		if (!l.isEmpty()) {
			return l.get(0);
		}
		else {
			return null;
		}
	}
	
	public void update(Observatoire obs) {
		getHibernateTemplate().update(obs);
	}

	public void delete(Observatoire obs) {
		getHibernateTemplate().delete(obs);
	}
	
	public int add(Observatoire obs) {
		return (Integer) getHibernateTemplate().save(obs);
	}
	
}
