package bpm.gateway.core.tsbn.referentiels;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DwhRefDAO extends HibernateDaoSupport {

	public void save(DwhRef ref) {
		getHibernateTemplate().save(ref);
	}
}
