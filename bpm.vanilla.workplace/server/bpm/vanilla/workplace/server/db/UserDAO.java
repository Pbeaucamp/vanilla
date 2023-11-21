package bpm.vanilla.workplace.server.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import bpm.vanilla.workplace.server.config.PlaceConfiguration;
import bpm.vanilla.workplace.shared.model.PlaceWebLog;
import bpm.vanilla.workplace.shared.model.PlaceWebUser;
import bpm.vanilla.workplace.shared.model.PlaceWebUserPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebLog.LogType;

public class UserDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<PlaceWebUser> getAllUsers() {
		return (List<PlaceWebUser>) getHibernateTemplate().find("from PlaceWebUser");
	}
	
	@SuppressWarnings("unchecked")
	public PlaceWebUser findByPrimaryKey(int key) {
		List<PlaceWebUser> l = (List<PlaceWebUser>) getHibernateTemplate().find("from PlaceWebUser where id=" + key);
		if (l.isEmpty()){
			return null;
		}
		return l.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public PlaceWebUser findByNameAndPassword(String name, String password) {
		List<PlaceWebUser> l = (List<PlaceWebUser>) getHibernateTemplate().find("from PlaceWebUser where name='" + name 
				+ "' and password='" + password + "' and valid=1");
		if(l.isEmpty()){
			return null;
		}
		return l.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<PlaceWebUser> getUsersForPackage(int packageId) {
		List<PlaceWebUserPackage> userPackages = (List<PlaceWebUserPackage>) 
			getHibernateTemplate().find("from PlaceWebUserPackage where packageId = " + packageId);
	
		List<PlaceWebUser> users = new ArrayList<PlaceWebUser>();
		for(PlaceWebUserPackage userPack : userPackages){
			List<PlaceWebUser> usersTmp = (List<PlaceWebUser>) 
				getHibernateTemplate().find("from PlaceWebUser where id = " + userPack.getUserId());
			
			if(usersTmp != null && !usersTmp.isEmpty()){
				users.add(usersTmp.get(0));
			}
		}
		
		return users;
	}
	
	@SuppressWarnings("unchecked")
	public boolean userAlreadyExist(String name) {
		List<PlaceWebUser> l = (List<PlaceWebUser>) getHibernateTemplate().find("from PlaceWebUser where name='" + name + "'");
		if(l.isEmpty()){
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean validateUser(String name, String hashCode, PlaceConfiguration config) {
		List<PlaceWebUser> l = (List<PlaceWebUser>) getHibernateTemplate().find("from PlaceWebUser where name='" + name + "'" +
				" and hashCode='" + hashCode + "'");
		if(l.isEmpty()){
			return false;
		}
		else {
			PlaceWebUser user = l.get(0);
			user.setHashCode(null);
			user.setValid(true);
			update(user, config);
		}
		return true;
	}
	
	public int save(PlaceWebUser d, PlaceConfiguration config) {
		d.setId((Integer)getHibernateTemplate().save(d));
		
		PlaceWebLog log = new PlaceWebLog(LogType.CREATION_USER, d.getId(), new Date(), null, null);
		config.getLogDao().save(log);
		
		return d.getId();
	}

	public void delete(PlaceWebUser d, PlaceConfiguration config) {
		getHibernateTemplate().delete(d);
		
		PlaceWebLog log = new PlaceWebLog(LogType.DELETE_USER, d.getId(), new Date(), null, null);
		config.getLogDao().save(log);
	}
	
	public void update(PlaceWebUser d, PlaceConfiguration config) {
		getHibernateTemplate().update(d);
		
		PlaceWebLog log = new PlaceWebLog(LogType.UPDATE_USER, d.getId(), new Date(), null, null);
		config.getLogDao().save(log);
	}
}
