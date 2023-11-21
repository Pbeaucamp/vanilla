package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;


public class ThemeDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Theme> findAll() {
		return getHibernateTemplate().find("from Theme");
	}

	@SuppressWarnings("unchecked")
	public Theme findByPrimaryKey(int key) {
		List<Theme> c = getHibernateTemplate().find("from Theme t where t.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Theme d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Theme");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(Theme d) {
		getHibernateTemplate().delete(d);
	}
	public void update(Theme d) {
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public Theme findForName(String name){
		List<Theme> c = getHibernateTemplate().find("from Theme where name='" + name.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}
		else{
			return null;
		}
	}


}
