package bpm.vanilla.platform.core.runtime.dao.widgets;

import java.util.List;

import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class WidgetsLogsDAO extends HibernateDaoSupport{

	public void update(Widgets w){
		getHibernateTemplate().update(w);
	}
	
	public List<Widgets> findAll(){
		return getHibernateTemplate().find("from Widgets");
	}
	
	public int save(Widgets w){
		return (Integer)getHibernateTemplate().save(w);
	}
	
	public void delete (Widgets w){
		getHibernateTemplate().delete(w);
	}
	
	public Widgets findByPrimaryKey(int key){
		
		List<Widgets> c = getHibernateTemplate().find("from Widgets where widgetId="+key);
		Widgets cc = new Widgets();

		if (c != null && c.size() > 0){
			return (Widgets) c.get(0);
		}
		else{
			return cc;
		}		
	}
	
	public List<Widgets> findByUserId(int userId) {
		List<Widgets> widgets = getHibernateTemplate().find("from Widgets where user="+userId);
		return widgets;
	}
}
