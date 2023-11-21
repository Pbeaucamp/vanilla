package bpm.freemetrics.api.organisation.observatoire;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ObservatoiresUsersDAO extends HibernateDaoSupport {

	public List<ObservatoiresUsers> findAll() {
		return getHibernateTemplate().find("from ObservatoiresUsers");
	}
	
	public ObservatoiresUsers findByPrimaryKey(Integer key) {
		List<ObservatoiresUsers> l = getHibernateTemplate().find("from ObservatoiresUsers where assoId=" + key);
		if (!l.isEmpty()) {
			return l.get(0);
		}
		else {
			return null;
		}
	}
	public List<ObservatoiresUsers> findByObsId(int id) {
		return getHibernateTemplate().find("from ObservatoiresUsers where obsId=" + id);
	}
	
	public List<ObservatoiresUsers> findByUserId(int id) {
		return getHibernateTemplate().find("from ObservatoiresUsers where userId=" + id);
	}
	
	public void delete(ObservatoiresUsers ou) {
		getHibernateTemplate().delete(ou);
	}
	
	public int add(ObservatoiresUsers ou) {
		return (Integer) getHibernateTemplate().save(ou);
	}

	public List<ObservatoiresUsers> getByUserAndObservatoire(int userId, int observatoireId){
		return getHibernateTemplate().find("from ObservatoiresUsers where userId=" + userId + " and obsId = " + observatoireId);
	}
	
	public void remove(int userId, int obsId) {
		for (ObservatoiresUsers ou : getByUserAndObservatoire(userId, obsId)) {
			delete(ou);
		}
		
	}
	
}
