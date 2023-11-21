package bpm.vanilla.repository.beans.directory.item;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class ItemDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<RepositoryItem> findAll() {
		return (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem");
	}
	
	@SuppressWarnings("unchecked")
	public List<RepositoryItem> findAllDelete() {
		return (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem where visible = false");
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryItem> findByDirectoryId(int directoryId) {
		return (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem d where d.directoryId="+ directoryId);
	}
	
	@SuppressWarnings("unchecked")
	public List<RepositoryItem> findByType(String type) {
		return (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem where type='" + type + "'");
	}

	@SuppressWarnings("unchecked")
	public RepositoryItem findByPrimaryKey(long key) {
		List<RepositoryItem> l = (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem d where id=" + key);
		if(l == null || l.isEmpty()){
			return null;
		}
		return l.get(0);
	}

	public int save(RepositoryItem d) {
		int i = (Integer)getHibernateTemplate().save(d);
		return i;
	}

	public void delete(RepositoryItem d) {
		d.setVisible(false);
		getHibernateTemplate().update(d);
	}
	
	public void purge(RepositoryItem d) {
		getHibernateTemplate().delete(d);
	}
	
	public void restore(RepositoryItem d) {
		getHibernateTemplate().update(d);
	}

	public void update(RepositoryItem d){
		d.setDateModification(Calendar.getInstance().getTime());
		getHibernateTemplate().update(d);
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryItem> findByUser(Integer id) {
		return  (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem d where ownerId=" + id);
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryItem> findForItemId(int id) {
		return (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem d where id="+ id);
	}
	
	@SuppressWarnings("unchecked")
	public List<RepositoryItem> findForDirectoryId(int id) {
		return (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem d where directoryId="+ id);
	}
	
	@SuppressWarnings("unchecked")
	public Integer getLast() {
		List<Integer> l = getHibernateTemplate().find("select max(id) from RepositoryItem");
		if (l.size() > 0){
			return l.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryItem> getItem(int groupId, Integer dirItemId) {
		if (groupId <= 0){
			 return (List<RepositoryItem>)getHibernateTemplate().find(
					 "from RepositoryItem dirItem" + 
					 " where " +  
					 " dirItem.id=" + dirItemId + 
			 		 " order by dirItem.itemName");
		}
		else{
			 List<Object[]> objects = getHibernateTemplate().find(
					 "from RepositoryItem dirItem, SecuredObject secu" + 
					 " where secu.groupId=" + groupId + 
					 " and dirItem.id=" + dirItemId + 
					 " and secu.directoryItemId=dirItem.id and dirItem.visible=true" +
					 " order by dirItem.itemName");
			 
			 List<RepositoryItem> items = new ArrayList<RepositoryItem>();
			 if(objects != null) {
				 for(Object[] it : objects) {
					 if(it[0] != null && it[0] instanceof RepositoryItem) {
						 items.add((RepositoryItem)it[0]);
					 }
				 }
			 }
			 return items;
		}
	}

	@SuppressWarnings("unchecked")
	public List<RepositoryItem> getItems(int groupId, String search) {
		List<Object[]> objects = getHibernateTemplate().find(
				 "from RepositoryItem dirItem, SecuredObject secu" + 
				 " where secu.groupId=" + groupId + 
				 " and secu.directoryItemId=dirItem.id and dirItem.visible=true" +
				 " and dirItem.itemName LIKE '%" + search + "%'" +
				 " order by dirItem.itemName");
		 
		 List<RepositoryItem> items = new ArrayList<RepositoryItem>();
		 if(objects != null) {
			 for(Object[] it : objects) {
				 if(it[0] != null && it[0] instanceof RepositoryItem) {
					 items.add((RepositoryItem)it[0]);
				 }
			 }
		 }
		 return items;
	}

	public RepositoryItem findByPrimaryKeyNonDeleted(int directoryItemId) {
		List<RepositoryItem> l = (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem d where id=" + directoryItemId + " and visible = true");
		if(l == null || l.isEmpty()){
			return null;
		}
		return l.get(0);
	}

	public List<RepositoryItem> getItems(List<Integer> ids) {
		String inCondition = "";
		
		boolean first = true;
		for(int i : ids) {
			if(first) {
				first = false;
			}
			else {
				inCondition += ",";
			}
			inCondition += i;
		}
		
		return (List<RepositoryItem>)getHibernateTemplate().find("from RepositoryItem d where id in (" + inCondition + ")");
	}
}
