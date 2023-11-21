package bpm.vanilla.platform.core.runtime.dao.ged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.core.beans.ged.Lock;
import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DocumentDAO extends HibernateDaoSupport {
	
	private DocumentVersionDAO documentVersionDAO;
	private SecurityDAO securityDao;
	
	private List<DocumentVersion> getVersionsForDocument(GedDocument doc) {
		List<DocumentVersion> versions = documentVersionDAO.getDocumentVersionForDocument(doc);
		if(versions == null) {
			versions = new ArrayList<DocumentVersion>();
		}
		return versions;
	}

	public List<GedDocument> findAll() {
		List<GedDocument> docs = getHibernateTemplate().find("from GedDocument");
		for(GedDocument doc : docs) {
			doc.setDocumentVersions(getVersionsForDocument(doc));
			doc.setLock(getLockForDocument(doc.getId()));
		}
		return docs;
	}

	public GedDocument findByPrimaryKey(int key) {
		List<GedDocument> c = getHibernateTemplate().find("from GedDocument d where d.id=" +  key);
		if (c != null && c.size() > 0){
			GedDocument doc = (GedDocument)c.get(0);
			doc.setDocumentVersions(getVersionsForDocument(doc));
			doc.setLock(getLockForDocument(doc.getId()));
			return doc;
		}
		else{
			return null;
		}
	}

	public int save(GedDocument d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(GedDocument d) {
		getHibernateTemplate().delete(d);
		documentVersionDAO.deleteForDocument(d);
		securityDao.deleteSecurityForDoc(d.getId());
		deleteLock(d.getLock());
	}
	
	public void update(GedDocument d) {
		getHibernateTemplate().update(d);
	}
	
	public GedDocument findForName(String name){
		List<GedDocument> c = getHibernateTemplate().find("from GedDocument where name='" + name + "'");
		if (c != null && c.size() > 0){
			GedDocument doc = (GedDocument)c.get(0);
			doc.setDocumentVersions(getVersionsForDocument(doc));
			doc.setLock(getLockForDocument(doc.getId()));
			return doc;
		}
		else{
			return null;
		}
	}
	
	public List<GedDocument> getDocumentForGroup(int groupId) {
		String query = "from GedDocument d ";
		query += " WHERE d.id in (select s.id from Security s where s.groupId = " + groupId + ")";
		List<GedDocument> c = getHibernateTemplate().find(query);
		for(GedDocument doc : c) {
			doc.setDocumentVersions(getVersionsForDocument(doc));
			doc.setLock(getLockForDocument(doc.getId()));
		}
		return c;
	}

	public List<GedDocument> findDocuments(List<Security> sec) {
		if (sec.isEmpty()) {
			return new ArrayList<GedDocument>();
		}
		else {
			String docs = "(";
			boolean first = true;
			for (Security s : sec) {
				
				if (first) {
					docs += s.getDocumentId();
					first = false;
				}
				else {
					docs += "," + s.getDocumentId();
				}
			}
			
			docs += ")";
			
			List<GedDocument> c = getHibernateTemplate().find("from GedDocument d where d.id in " +  docs); 
			for(GedDocument doc : c) {
				doc.setDocumentVersions(getVersionsForDocument(doc));
				doc.setLock(getLockForDocument(doc.getId()));
			}
			return c;
		}
	}

	public List<GedDocument> findByPrimaryKeys(int[] ids) {
		
		if(ids != null && ids.length > 0) {
			StringBuilder key = new StringBuilder();
			boolean first = true;
			for(int i : ids) {
				if(!first) {
					key.append(",");
				}
				else {
					first = false;
				}
				key.append(i);
			}
			
			List<GedDocument> c = getHibernateTemplate().find("from GedDocument d where d.id in (" +  key.toString() +")");
			if (c != null && c.size() > 0){
				for(GedDocument doc : c) {
					doc.setDocumentVersions(getVersionsForDocument(doc));
					doc.setLock(getLockForDocument(doc.getId()));
				}
				return c;
			}
			else{
				return new ArrayList<GedDocument>();
			}
		}
		return new ArrayList<GedDocument>();
	}

	public List<GedDocument> findWaitingDocs() {
//		return hibernateTemplate.find("from GedDocument d where d.isIndexed=0");
		//TODO don't know...
		return null;
	}

	public void setVersionDao(DocumentVersionDAO documentVersionDAO) {
		this.documentVersionDAO = documentVersionDAO;
	}
 
	public DocumentVersionDAO getDocumentVersionDAO() {
		return documentVersionDAO;
	}
	
	/**
	 * This method only return the document from the given versions which are the last version of the document
	 * 
	 * @param versions
	 * @return Only document with last versions in parameter
	 */
	public List<GedDocument> getDocumentsForVersions(List<DocumentVersion> versions) { 
		HashMap<Integer, GedDocument> map = new HashMap<Integer, GedDocument>();
		
		for(DocumentVersion v : versions) {
			if(map.containsKey(v.getDocumentId())) {
				map.get(v.getDocumentId()).addDocumentVersion(v);
			}
			else {
				GedDocument doc = findByPrimaryKey(v.getDocumentId());
				if(doc.getLastVersion().getId() == v.getId()){
					map.put(doc.getId(), doc);
				}
			}
		}
		
		return new ArrayList<GedDocument>(map.values());
	}
	
	//LOCK Part
	public void saveLock(Lock lock){
		getHibernateTemplate().save(lock);
	}
	
	public void updateLock(Lock lock){
		getHibernateTemplate().update(lock);
	}
	
	public void deleteLock(Lock lock){
		if(lock != null){
			getHibernateTemplate().delete(lock);
		}
	}
	
	public Lock getLockForDocument(int docId){
		List<Lock> locks = getHibernateTemplate().find("from Lock where docId=" + docId);
		if(locks == null || locks.isEmpty()){
			return null;
		}
		return locks.get(0);
	}

	public void setSecurityDao(SecurityDAO securityDao) {
		this.securityDao = securityDao;
	}

	public void delete(DocumentVersion version) {
		getHibernateTemplate().delete(version);
	}
}
