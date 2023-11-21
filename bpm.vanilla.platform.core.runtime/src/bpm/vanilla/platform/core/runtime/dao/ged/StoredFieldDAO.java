package bpm.vanilla.platform.core.runtime.dao.ged;

import java.util.List;

import bpm.vanilla.platform.core.beans.ged.StoredField;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class StoredFieldDAO extends HibernateDaoSupport {

	public List<StoredField> findAll() {
		return getHibernateTemplate().find("from StoredField");
	}

	public StoredField findByPrimaryKey(int key) {
		List<StoredField> c = getHibernateTemplate().find("from StoredField d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return (StoredField)c.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<StoredField> findByDocumentId(int docId) {
		List<StoredField> c = getHibernateTemplate().find("from StoredField d where d.documentId=" +  docId);
		return c;
	}

	public int save(StoredField d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(StoredField d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(StoredField d) {
		getHibernateTemplate().update(d);
	}

}
