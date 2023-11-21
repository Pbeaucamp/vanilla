package bpm.vanilla.workplace.server.db;

import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import bpm.vanilla.workplace.server.config.PlaceConfiguration;
import bpm.vanilla.workplace.shared.model.PlaceWebLog;
import bpm.vanilla.workplace.shared.model.PlaceWebProject;
import bpm.vanilla.workplace.shared.model.PlaceWebLog.LogType;

public class ProjectDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<PlaceWebProject> getAllProjects() {
		return (List<PlaceWebProject>) getHibernateTemplate().find("from PlaceWebProject");
	}
	
	@SuppressWarnings("unchecked")
	public PlaceWebProject findByPrimaryKey(int key) {
		List<PlaceWebProject> l = (List<PlaceWebProject>) getHibernateTemplate().find("from PlaceWebProject where id=" + key);
		if (l.isEmpty()){
			return null;
		}
		return l.get(0);
	}
	
	public int save(int userId, PlaceWebProject d, PlaceConfiguration config) {
		d.setId((Integer)getHibernateTemplate().save(d));
		
		PlaceWebLog log = new PlaceWebLog(LogType.CREATE_PROJECT, userId, new Date(), d.getId(), null);
		config.getLogDao().save(log);
		
		return d.getId();
	}

	public void delete(PlaceWebProject d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(PlaceWebProject d) {
		getHibernateTemplate().update(d);
	}
}
