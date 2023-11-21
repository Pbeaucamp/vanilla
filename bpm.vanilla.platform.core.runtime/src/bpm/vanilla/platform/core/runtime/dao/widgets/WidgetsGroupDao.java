package bpm.vanilla.platform.core.runtime.dao.widgets;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.WidgetsGroups;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class WidgetsGroupDao extends HibernateDaoSupport{

	public void update(WidgetsGroups w){
		getHibernateTemplate().update(w);
	}
	
	public List<WidgetsGroups> findAll(){
		return getHibernateTemplate().find("from WidgetsGroups");
	}
	
	public int save(WidgetsGroups w){
		return (Integer) getHibernateTemplate().save(w);
	}
	
	public void delete(WidgetsGroups w){
		getHibernateTemplate().delete(w);
	}
	
	public WidgetsGroups findByPrimaryKey(int key){
		WidgetsGroups w = (WidgetsGroups) getHibernateTemplate().find("from WidgetsGroups where idWidgetGroup="+key);
		WidgetsGroups ww = new WidgetsGroups();
		
		if (w != null ){
			return w;
		}
		else{
			return ww;
		}
		
	}
	
	public List<WidgetsGroups> findByGroup(int group){
		List<WidgetsGroups> w = getHibernateTemplate().find("from WidgetsGroups where idGroup="+group);
		List<WidgetsGroups> ww = new ArrayList<WidgetsGroups>();
		
		if (w != null && w.size() > 0){
			return w;
		}
		else{
			return ww;
		}		
	}
	
	public List<WidgetsGroups> findByWidgetId(int id){
		List<WidgetsGroups> w = getHibernateTemplate().find("from WidgetsGroups where widgetId="+id);
		List<WidgetsGroups> ww = new ArrayList<WidgetsGroups>();
	
		if (w != null && w.size() > 0){
			return w;
		}
		else{
			return ww;
		}	
	}
	
	public WidgetsGroups findIfExist(WidgetsGroups w){
		List c = getHibernateTemplate().find("from WidgetsGroups where idGroup="+w.getIdGroup()+" and widgetId="+w.getWidgetId());
		if(c.isEmpty()){
			return new WidgetsGroups();
		}else{
			return (WidgetsGroups) c.get(0);
		}
	}
}
