package bpm.vanilla.platform.core.runtime.dao.widgets;

import java.util.List;

import bpm.vanilla.platform.core.beans.WidgetPosition;
import bpm.vanilla.platform.core.beans.Widgets;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class WidgetPositionDAO extends HibernateDaoSupport{

	public void delete(WidgetPosition wP){
		getHibernateTemplate().delete(wP);
	}
	
	public int save(WidgetPosition wP){
		return (Integer)getHibernateTemplate().save(wP);
	}
	
	public List<WidgetPosition> getAllWidgetsPositionByUser(int user){
		List<WidgetPosition> lst = getHibernateTemplate().find("from WidgetPosition where user="+user);
		for(WidgetPosition w : lst){
			List<Widgets> result =  getHibernateTemplate().find("from Widgets where widgetId="+w.getWidgetId());
			w.setWidgetURL(result.get(0).getWidgetUrl());
		}
		return lst;
	}
	
	public void update(WidgetPosition wP){
		getHibernateTemplate().update(wP);
	}
}
