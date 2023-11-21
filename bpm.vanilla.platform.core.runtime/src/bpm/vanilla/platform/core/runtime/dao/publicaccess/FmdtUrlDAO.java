package bpm.vanilla.platform.core.runtime.dao.publicaccess;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.FmdtUrl;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmdtUrlDAO extends HibernateDaoSupport {

	public List<FmdtUrl> findAll() {
		return getHibernateTemplate().find("from FmdtUrl");
	}

	public FmdtUrl findByName(String name) throws Exception {
		List<FmdtUrl> c = getHibernateTemplate().find("from FmdtUrl d where d.name='" + name + "'");
		
		if (c == null || c.isEmpty()){
			throw new Exception("The FmtUrl " + name + " doesn't exist");
		}
		else {
			return c.get(0);
		}
	}

	public FmdtUrl findByPrimaryKey(long key) {
		return (FmdtUrl) getHibernateTemplate().load(FmdtUrl.class,new Long(key));
	}

	public int save(FmdtUrl d) throws Exception {
		for (FmdtUrl fu : findAll()) {
			if (fu.getName().equals(d.getName()))
				throw new Exception("The name : " + d.getName() + " is allready used");
		}
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(FmdtUrl d) {
		getHibernateTemplate().delete(d);
	}

	public void update(FmdtUrl d) {
		getHibernateTemplate().update(d);
	}

	public List<FmdtUrl> findByItemId(int itemId) throws Exception {
		List<FmdtUrl> c = getHibernateTemplate().find("from FmdtUrl d where d.itemId=" + itemId);
		
		if (c == null || c.isEmpty()){
			return new ArrayList<FmdtUrl>();
		}
		else {
			return c;
		}
	
	}

	public List<FmdtUrl> findByGroupName(String groupName) throws Exception {
		List<FmdtUrl> c = getHibernateTemplate().find("from FmdtUrl where groupName='" + groupName + "'");
		
		if (c == null || c.isEmpty()){
			return new ArrayList<FmdtUrl>();
		}
		else {
			return c;
		}
	
	}
}