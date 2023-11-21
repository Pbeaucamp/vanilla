package bpm.freemetrics.api.organisation.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.freemetrics.api.organisation.group.Group;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ApplicationDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<Application> findAll() {
		return getHibernateTemplate().find("from Application where endDate is null");
	}


	@SuppressWarnings("unchecked")
	public Application findByPrimaryKey(int key) {
		List<Application> c = getHibernateTemplate().find("from Application d where d.id=" +  key + " and endDate is null");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public int save(Application d) {
		getHibernateTemplate().flush();
		List<Integer> res = getHibernateTemplate().find("select max(id) from Application where endDate is null");
		int i = res != null && !res.isEmpty() && res.get(0) != null ? (((Integer)res.get(0)).intValue() +1) : 1;
		d.setId(i);
		int id = (Integer)getHibernateTemplate().save(d);
		return id;
	}

	public void delete(Application d) {
		
		d.setEndDate(new Date());
		getHibernateTemplate().update(d);
	}
	public void update(Application d) {
		
		getHibernateTemplate().update(d);
		
	}

	@SuppressWarnings("unchecked")
	public Application findForName(String name){
		List<Application> c = getHibernateTemplate().find("from Application where name='" + name.replace("'", "''") + "'" + " and endDate is null");
		if (c != null && c.size() > 0){
			return c.get(0);
		}else{
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Application> getApplicationByTypeTerrId(int typTerId) {
		return getHibernateTemplate().find("from Application d where d.adTypeTerrID=" + typTerId + " and endDate is null");
	}


	public Application getApplicationForHistory(int appId, Date date) {
		
		List<Application> res = getHibernateTemplate().find("from Application a where a.id = " + appId);
		if(res != null && res.size() > 0) {
			Application app = res.get(0);
			if(app.getAdCreationDate().compareTo(date) <= 0) {
				if(app.getEndDate() != null) {
					if(app.getEndDate().compareTo(date) >= 0) {
						return app;
					}
				}
				else {
					return app;
				}
			}
		}
		
		return null;
	}

	private HashMap<Integer, List<Integer>> parentChildIds = new HashMap<Integer, List<Integer>>();
	private HashMap<Integer, List<Integer>> groupChildIds = new HashMap<Integer, List<Integer>>();

	public List<Application> getChildApplicationForGroup(Group parent, int start, int end) throws Exception {
		List<Application> result = new ArrayList<Application>();
 		
		List<Integer> childsIds = null;
		if(groupChildIds.containsKey(parent.getId())) {
			childsIds = groupChildIds.get(parent.getId());
		}
		else {
			childsIds = findRootApps(parent.getId());
			groupChildIds.put(parent.getId(), childsIds);
		}
		
		String inConstraint = "";
		if(childsIds.size() > end) {
			inConstraint = createInConstraint(childsIds.subList(start, end));
		}
		else {
			inConstraint = createInConstraint(childsIds.subList(start, childsIds.size()));
		}
		
		result = getHibernateTemplate().find("from Application where id " + inConstraint + " and endDate is null order by name");
		for(Application ap : result) {
			ap.setParentGroup(parent);
		}
		
		return result;
	}


	public List<Application> getChildApplicationForApp(Application parent, int start, int end) throws Exception {
		
		List<Application> result = new ArrayList<Application>();
 		
		List<Integer> childsIds = null;
		if(parentChildIds.containsKey(parent.getId())) {
			childsIds = parentChildIds.get(parent.getId());
		}
		else {
			childsIds = getHibernateTemplate().find("select h.childId from ApplicationHierarchy h, Application a where h.childId = a.id and h.parentId = " + parent.getId() + " and a.endDate is null order by a.name");
			parentChildIds.put(parent.getId(), childsIds);
		}
		
		String inConstraint = "";
		if(childsIds.size() > end) {
			inConstraint = createInConstraint(childsIds.subList(start, end));
		}
		else {
			inConstraint = createInConstraint(childsIds.subList(start, childsIds.size()));
		}
		
		result = getHibernateTemplate().find("from Application where id " + inConstraint + " and endDate is null order by name");
		
		for(Application ap : result) {
			ap.setParent(parent);
		}
		
		return result;
	}


	private String createInConstraint(List<Integer> childsIds) throws Exception {
		StringBuilder buf = new StringBuilder();
		buf.append(" in (");
		
		boolean first = true;
		
		for(Integer i : childsIds) {
			if(!first) {
				buf.append(",");
			}
			else {
				first = false;
			}
			buf.append(i);
		}
		
		buf.append(")");
		return buf.toString();
	}
	
	public List<Integer> findRootApps(int groupId) throws Exception {
		
		List<Integer> ids = new ArrayList<Integer>();
		
		List<Integer> lst = getHibernateTemplate().find("select g.applicationId from FmApplication_Groups g, Application a where g.applicationId = a.id and g.groupId = " + groupId + " order by a.name");
		
		List<Integer> children = getHibernateTemplate().find("select distinct childId from ApplicationHierarchy");
		
		for(Integer i : lst) {
			if(!children.contains(i)) {
				ids.add(i);
			}
		}
		
		return ids;
	}


	public int getChildrenCount(Object child) throws Exception {
		if(child instanceof Group) {
			Group grp = (Group) child;
			if(groupChildIds.containsKey(grp.getId())) {
				return groupChildIds.get(grp.getId()).size();
			}
		}
		else if(child instanceof Application) {
			Application app = (Application) child;
			if(parentChildIds.containsKey(app.getId())) {
				return parentChildIds.get(app.getId()).size();
			}
		}
		return 0;
	}

	private HashMap<String, List<Integer>> filterChildsIds = new HashMap<String, List<Integer>>();
	
	public List<Application> getApplicationForFilter(Object parent, String filter, int start, int end) throws Exception {
		
		List<Application> result = new ArrayList<Application>();
		
		List<Integer> childsIds = null;
		String key = null;
		if(parent instanceof Group) {
			key =  "grp" + ((Group)parent).getId() + filter;
		}
		else {
			key =  ((Application)parent).getId() + filter;
			
		}
		if(filterChildsIds.containsKey(key)) {
			childsIds = filterChildsIds.get(key);
		}
	
		if(childsIds == null) {
			if(parent instanceof Group) {
				
				childsIds = getHibernateTemplate().find("select id from Application where name like '%" + filter +  "%' and endDate is null order by name" );
				List<Integer> ids2 = findRootApps(((Group)parent).getId());
				
				childsIds.retainAll(ids2);
				
				filterChildsIds.put(key, childsIds);
			}
			else {
				List<Integer> ids2 = getHibernateTemplate().find("select childId from ApplicationHierarchy where parentId = " + ((Application)parent).getId());
				childsIds = getHibernateTemplate().find("select id from Application where name like '%" + filter +  "%' and endDate is null order by name");
				childsIds.retainAll(ids2);
				filterChildsIds.put(key, childsIds);
			}
		}
		
		String inConstraint = "";
		if(childsIds.size() > end) {
			inConstraint = createInConstraint(childsIds.subList(start, end));
		}
		else {
			inConstraint = createInConstraint(childsIds.subList(start, childsIds.size()));
		}
		
		result = getHibernateTemplate().find("from Application where id " + inConstraint + " and endDate is null order by name");
		
		for(Application ap : result) {
			if(parent instanceof Group) {
				ap.setParentGroup((Group)parent);
			}
			else {
				ap.setParent((Application)parent);
			}
			
		}
		
		return result;
	}


	public int getChildrenCount(Object previousParent, String text) {
		List<Integer> childsIds = null;
		String key = null;
		if(previousParent instanceof Group) {
			key =  "grp" + ((Group)previousParent).getId() + text;
		}
		else {
			key =  ((Application)previousParent).getId() + text;
			
		}
		if(filterChildsIds.containsKey(key)) {
			childsIds = filterChildsIds.get(key);
			return childsIds.size();
		}
		return 0;
	}


	public void resetCache() {
		filterChildsIds.clear();
		groupChildIds.clear();
		parentChildIds.clear();
	}
	
	
}
