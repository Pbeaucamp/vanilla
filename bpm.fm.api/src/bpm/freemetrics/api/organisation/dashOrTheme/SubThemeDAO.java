package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class SubThemeDAO extends HibernateDaoSupport {
	
	@SuppressWarnings("unchecked")
	public List<SubTheme> findAll() {
		return getHibernateTemplate().find("from SubTheme");
	}
	
	@SuppressWarnings("unchecked")
	public SubTheme findByPrimaryKey(int key) {
		List<SubTheme> c = getHibernateTemplate().find("from SubTheme t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public int save(SubTheme d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from SubTheme");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		
		return id;
	}

	public boolean delete(SubTheme d) {
		getHibernateTemplate().delete(d);
		
		return findByPrimaryKey(d.getId()) == null;
	}
	public void update(SubTheme d) {
		getHibernateTemplate().update(d);
	}
	
	@SuppressWarnings("unchecked")
	public SubTheme findForName(String name){
		List<SubTheme> c = getHibernateTemplate().find("from SubTheme where name='" + name + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<SubTheme> getSubThemeForThemeId(int themeId) {
		return getHibernateTemplate().find("from SubTheme where themeID=" + themeId);
	}


}
