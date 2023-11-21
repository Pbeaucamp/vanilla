package bpm.vanilla.platform.core.runtime.dao.platform;

import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLocale;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class VanillaLocaleDAO extends HibernateDaoSupport {

	public void delete(VanillaLocale vanillaLocale){
		getHibernateTemplate().delete(vanillaLocale);
	}
	
	public int save(VanillaLocale vanillaLocale){
		return (Integer)getHibernateTemplate().save(vanillaLocale);
	}
	
	public void update(VanillaLocale vanillaLocale){
		getHibernateTemplate().update(vanillaLocale);
	}
	
	@SuppressWarnings("unchecked")
	public List<VanillaLocale> findAll() {
		return getHibernateTemplate().find("from VanillaLocale");
	}

}
