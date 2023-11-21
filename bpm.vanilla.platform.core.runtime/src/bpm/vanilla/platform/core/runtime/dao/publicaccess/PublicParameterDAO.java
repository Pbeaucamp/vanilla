package bpm.vanilla.platform.core.runtime.dao.publicaccess;


import java.util.List;

import bpm.vanilla.platform.core.beans.PublicParameter;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class PublicParameterDAO extends HibernateDaoSupport {


	public List<PublicParameter> findAll() {
		return getHibernateTemplate().find("from PublicParameter");
	}

	public List<PublicParameter> findByPrimaryKey(Integer key) {
		return (List<PublicParameter>) getHibernateTemplate().find("from PublicParameter where id=" + key);
	}
	
	public List<PublicParameter> findByPublicUrlId(Integer publicUrlId) {
		return (List<PublicParameter>) getHibernateTemplate().find("from PublicParameter where publicUrlId=" + publicUrlId);
	}

	public int save(PublicParameter d) {
		return (Integer)getHibernateTemplate().save(d);
	}
	
	public void delete(PublicParameter d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(PublicParameter d) {
		getHibernateTemplate().update(d);
	}

}
