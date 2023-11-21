package bpm.freemetrics.api.features.forum;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ForumDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<Forum> findAll() {
		return getHibernateTemplate().find("from Forum");
	}
	
	@SuppressWarnings("unchecked")
	public Forum findByPrimaryKey(int key) {
		List<Forum> c = getHibernateTemplate().find("from Forum d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(Forum d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Forum");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(Forum d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Forum d) {
		getHibernateTemplate().update(d);
	}


	@SuppressWarnings("unchecked")
	public List<Forum> getForumForAppMetrAndMetrValId(int appId, int metrId,
			int metrValId) {
	
		return getHibernateTemplate().find("from Forum d where d.mfApplicationId=" +appId+" AND d.mfMetricsId = "+metrId+" AND d.mfMetricsValueId = "+metrValId);
	}


	@SuppressWarnings("unchecked")
	public List<Forum> getForumForAppAndMetrId(int appId, int metrId) {
		
		return getHibernateTemplate().find("from Forum d where d.mfApplicationId=" +appId+" AND d.mfMetricsId="+metrId);
	}	

	@SuppressWarnings("unchecked")
	public List<Forum> getForumByValueId(int valueId) {
		return getHibernateTemplate().find("from Forum d where d.mfMetricsValueId = "+valueId);
	}	
}
