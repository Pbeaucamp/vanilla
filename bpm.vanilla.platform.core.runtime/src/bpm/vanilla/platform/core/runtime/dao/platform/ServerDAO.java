package bpm.vanilla.platform.core.runtime.dao.platform;


import java.util.List;

import bpm.vanilla.platform.core.beans.Server;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
/**
 * @deprecated user RUntimeComponentDAO instead
 * @author ludo
 *
 */
public class ServerDAO extends HibernateDaoSupport{

	public void delete(Server server){
		getHibernateTemplate().delete(server);
	}
	
	public int add(Server server){
		return (Integer)getHibernateTemplate().save(server);
	}
	
	public void update(Server server){
		getHibernateTemplate().update(server);
	}
	
	public List<Server> getAll(){
		return getHibernateTemplate().find("from Server");
	}
	
	public List<Server> getByType(int type){
		return getHibernateTemplate().find("from Server where type=" + type);
	}
}
