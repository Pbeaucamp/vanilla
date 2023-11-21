package bpm.vanilla.platform.core.runtime.dao.ged;

import java.util.List;

import bpm.vanilla.platform.core.beans.ged.DocCat;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DocCatDAO extends HibernateDaoSupport {

	public List<DocCat> findAll() {
		return getHibernateTemplate().find("from DocCat");
	}

	public DocCat findByPrimaryKey(int key) {
		List<DocCat> c = getHibernateTemplate().find("from DocCat d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return (DocCat)c.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<DocCat> findForCategory(int catId) {
		return getHibernateTemplate().find("from DocCat d where d.categoryId=" + catId);
	}
	
	public List<DocCat> findForDocument(int documentId) {
		return getHibernateTemplate().find("from DocCat d where d.documentId=" + documentId);
	}

	public List<DocCat> findByIds(int catId, int documentId) {
		return getHibernateTemplate().find("from DocCat d where d.documentId=" + documentId + " and d.categoryId=" + catId);
	}
	
	
	public int save(DocCat d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(DocCat d) {
		getHibernateTemplate().delete(d);
	}
}
