package bpm.vanilla.repository.beans.alert;

import java.util.Collection;

import bpm.vanilla.platform.core.beans.alerts.Smtp;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SmtpDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public Collection<Smtp> findAll() {
		return getHibernateTemplate().find("from Smtp");
	}

	public Smtp findByPrimaryKey(int key) throws Exception {
		return (Smtp) getHibernateTemplate().find("from Smtp where id=" + key);
	}

	public int save(Smtp d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(Smtp d) {
		getHibernateTemplate().delete(d);
	}

	public void update(Smtp d) {
		getHibernateTemplate().update(d);
	}

}
