package bpm.freemetrics.api.security;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class FmUserDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<FmUser> findAll() {
		getHibernateTemplate().flush();
		return getHibernateTemplate().find("from FmUser");
	}

	@SuppressWarnings("unchecked")
	public FmUser findByPrimaryKey(int key) {
		getHibernateTemplate().flush();
		List<FmUser> c = getHibernateTemplate().find("from FmUser d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(FmUser d) {
		String tmp = MD5.encode(d.getPassword());
		d.setPassword(tmp);

		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from FmUser");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public boolean delete(FmUser d) {
		
		getHibernateTemplate().flush();
		
		getHibernateTemplate().delete(d);

		return findByPrimaryKey(d.getId()) == null;
	}

	public void update(FmUser d) {

		getHibernateTemplate().flush();
		
		FmUser u = findByPrimaryKey(d.getId());

		if(d.getPassword().length() != 32 || !d.getPassword().equals(u.getPassword())){
			String tmp = MD5.encode(d.getPassword());
			d.setPassword(tmp);
		}

		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public FmUser getUserByLogin(String login){
		getHibernateTemplate().flush();
		List<FmUser> c =  getHibernateTemplate().find("from FmUser where login='" + login.replace("'", "''") + "'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public FmUser findForNameAndPass(String name, String password) {
		getHibernateTemplate().flush();
		String pass = "";
		
		
		if (password.matches("[0-9a-f]{32}")) {
			pass = password;
		}
		else {
			pass = MD5.encode(password); 
		}
		
		List<FmUser> c =  getHibernateTemplate().find("from FmUser where login='" + name.replace("'", "''") + "' AND password='" + pass +"'");
//		List<FmUser> c =  getHibernateTemplate().find("from FmUser where name='" + name.replace("'", "''") + "' AND password='" + password +"'");

		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}
	
	public FmUser findForNameAndPass(String username, String password, boolean isEncrypted) {
		getHibernateTemplate().flush();
		String pass = null;
		if(isEncrypted) {
			pass = password;
		}
		else {
			pass = MD5.encode(password);
		}
		List<FmUser> c =  getHibernateTemplate().find("from FmUser where login='" + username.replace("'", "''") + "' AND password='" + pass +"'");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

}
