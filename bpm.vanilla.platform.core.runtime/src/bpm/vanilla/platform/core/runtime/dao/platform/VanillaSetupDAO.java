package bpm.vanilla.platform.core.runtime.dao.platform;


import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaSetup;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class VanillaSetupDAO extends HibernateDaoSupport {
	
	public void update(VanillaSetup d) {
		getHibernateTemplate().update(d);
	}
	
	public List<VanillaSetup> findAll() {
		return getHibernateTemplate().find("from VanillaSetup");
	}

	public VanillaSetup findByPrimaryKey(int key) {
		List<VanillaSetup> c = getHibernateTemplate().find("from VanillaSetup d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
}
