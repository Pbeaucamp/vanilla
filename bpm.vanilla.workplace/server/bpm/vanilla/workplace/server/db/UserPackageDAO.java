package bpm.vanilla.workplace.server.db;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import bpm.vanilla.workplace.shared.model.PlaceWebUserPackage;

public class UserPackageDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<PlaceWebUserPackage> getAllUserPackages() {
		return (List<PlaceWebUserPackage>) getHibernateTemplate().find("from PlaceWebUserPackage");
	}
	
	@SuppressWarnings("unchecked")
	public PlaceWebUserPackage findByPrimaryKey(int key) {
		List<PlaceWebUserPackage> l = (List<PlaceWebUserPackage>) getHibernateTemplate().find("from PlaceWebUserPackage where id=" + key);
		if (l.isEmpty()){
			return null;
		}
		return l.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public void deleteAllPackageForUser(int userId){
		List<PlaceWebUserPackage> l = (List<PlaceWebUserPackage>) getHibernateTemplate().find("from PlaceWebUserPackage where userId=" + userId);
		if (!l.isEmpty()){
			for(PlaceWebUserPackage userPack : l){
				getHibernateTemplate().delete(userPack);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deletePackageForUser(int userId, int packageId){
		List<PlaceWebUserPackage> l = (List<PlaceWebUserPackage>) getHibernateTemplate().find("from PlaceWebUserPackage where userId=" + userId + " and packageId=" + packageId);
		if (!l.isEmpty()){
			for(PlaceWebUserPackage userPack : l){
				getHibernateTemplate().delete(userPack);
			}
		}
	}
	
	public int save(PlaceWebUserPackage d) {
		d.setId((Integer)getHibernateTemplate().save(d));
		return d.getId();
	}

	public void delete(PlaceWebUserPackage d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(PlaceWebUserPackage d) {
		getHibernateTemplate().update(d);
	}
}
