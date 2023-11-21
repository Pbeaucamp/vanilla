package bpm.vanilla.repository.beans.directory.item;

import java.util.List;

import bpm.vanilla.platform.core.repository.LinkedDocument;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class LinkedDocumentDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<LinkedDocument> findAll() {
		return (List<LinkedDocument>) getHibernateTemplate().find("from LinkedDocument");
	}

	@SuppressWarnings("unchecked")
	public LinkedDocument findByPrimaryKey(int key) {
		List<LinkedDocument> l = getHibernateTemplate().find("from LinkedDocument d where id=" + key);
		if(l == null || l.isEmpty()){
			return null;
		}
		return l.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<LinkedDocument> getLinkedDocuments(int itemId, int groupId) {
		if (groupId > 0){
			return (List<LinkedDocument>) getHibernateTemplate().find("from LinkedDocument o where o.itemId=" + itemId + " AND o.id in (select a.linkedDocumentId from SecuredLinked a where a.groupId=" + groupId + ")");
		}
		else{
			return (List<LinkedDocument>) getHibernateTemplate().find("from LinkedDocument o where o.itemId=" + itemId );
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<LinkedDocument> getLinkedDocuments(Integer itemId) {
		return (List<LinkedDocument>) getHibernateTemplate().find("from LinkedDocument o where o.itemId=" + itemId);
	}
	
	public int save(LinkedDocument d) {
		int id = (Integer) getHibernateTemplate().save(d);
		d.setId(id);
		return id;
	}

	public void delete(LinkedDocument d) {
		getHibernateTemplate().delete(d);
	}
	public void update(LinkedDocument d) {
		getHibernateTemplate().update(d);
	}



}
