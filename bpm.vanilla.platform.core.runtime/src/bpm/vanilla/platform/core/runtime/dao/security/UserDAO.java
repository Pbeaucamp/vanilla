package bpm.vanilla.platform.core.runtime.dao.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.hibernate.Query;
import org.hibernate.Session;

import bpm.vanilla.platform.core.beans.AutoLogin;
import bpm.vanilla.platform.core.beans.PasswordBackup;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.runtime.tools.Security;
import bpm.vanilla.platform.hibernate.DateWithTemporal;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class UserDAO extends HibernateDaoSupport {

	public List<User> findAll() {
		return getHibernateTemplate().find("from User");
	}

	public User findByPrimaryKey(int key) {
		List<User> c = getHibernateTemplate().find("from User d where d.id=" + key);
		if (c != null && c.size() > 0) {
			return c.get(0);
		}
		else {
			return null;
		}
	}

	public int save(User d) {
		if (d.getPassword() != null && !d.isPasswordEncrypted()) {
			d.setPassword(Security.encode(d.getPassword()));
		}
		d.setDatePasswordModification(new Date());

		Integer id = (Integer) getHibernateTemplate().save(d);
		d.setId(id);
		return id;
	}

	public void delete(User d) {
		getHibernateTemplate().delete(d);
	}

	public void update(User d) {
		getHibernateTemplate().update(d);
	}

	/**
	 * @deprecated use findForLogin(String login) instead.
	 * @param name
	 * @return
	 */
	public List<User> findForName(String name) {
		return getHibernateTemplate().find("from User where name='" + name + "'");
	}

	public List<User> getUsers(int begin, int step) {
		String sql = "from User";

		Session session = getHibernateTemplate().getSessionFactory().openSession();
		Query hql = session.createQuery(sql);

		hql.setFirstResult(begin);
		hql.setMaxResults(step);

		List l = hql.list();

		session.close();
		return l;
	}

	public int getUsersNumber() {
		return (Integer) getHibernateTemplate().find("select count(*) from User").get(0);
	}

	public List<User> findForLogin(String login) {
		return getHibernateTemplate().find("from User where login='" + login.replace("'", "''") + "'");
	}
	
	public int save(PasswordBackup passBackup) {
		Integer id = (Integer) getHibernateTemplate().save(passBackup);
		passBackup.setId(id);
		return id;
	}

	public void update(PasswordBackup passBackup) {
		getHibernateTemplate().update(passBackup);
	}

	public void delete(PasswordBackup passBackup) {
		getHibernateTemplate().delete(passBackup);
	}
	
	@SuppressWarnings("unchecked")
	public boolean checkPasswordBackupValidity(String hash) {
		List<PasswordBackup> passBackups = getHibernateTemplate().find("from PasswordBackup where code='" + hash + "'");
		if (!passBackups.isEmpty()) {
			PasswordBackup passBackup = passBackups.get(0);
			Date endDate = passBackup.getEndValidityDate();
			
			return new Date().before(endDate);
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public User getUserByPasswordBackupHash(String hash) {
		List<PasswordBackup> passBackups = getHibernateTemplate().find("from PasswordBackup where code='" + hash + "'");
		if (!passBackups.isEmpty()) {
			return findByPrimaryKey(passBackups.get(0).getUserId());
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<PasswordBackup> getPendingPasswordChangeDemands() {
		List<PasswordBackup> passBackups = getHibernateTemplate().find("from PasswordBackup where accepted=false");
		if (!passBackups.isEmpty()) {
			for (PasswordBackup pass : passBackups) {
				pass.setUser(findByPrimaryKey(pass.getUserId()));
			}
		}
		return passBackups;
	}
	
	public User checkAutoLogin(String keyAutoLogin) {
		List<Object> parameters = new ArrayList<Object>();

		StringBuffer buf = new StringBuffer();
		buf.append("FROM AutoLogin WHERE keyAutoLogin = ?");
		parameters.add(keyAutoLogin);
		buf.append(" AND endValidityDate >= now()");

		List<AutoLogin> autoLogins = getHibernateTemplate().find(buf.toString(), parameters);
		if (!autoLogins.isEmpty()) {
			return findByPrimaryKey(autoLogins.get(0).getUserId());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<User> getUsersByGroup(Integer groupId) {
		return getHibernateTemplate().find("SELECT d FROM User d, UserGroup ug WHERE d.id = ug.userId AND ug.groupId = " + groupId + " ORDER BY d.name ASC");
	}
}
