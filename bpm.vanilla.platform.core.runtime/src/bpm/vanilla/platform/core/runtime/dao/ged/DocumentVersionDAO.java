package bpm.vanilla.platform.core.runtime.dao.ged;

import java.util.List;

import bpm.vanilla.platform.core.beans.ged.DocumentVersion;
import bpm.vanilla.platform.core.beans.ged.GedDocument;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DocumentVersionDAO extends HibernateDaoSupport {
	
	public List<DocumentVersion> getDocumentVersionForDocument(GedDocument document) {
		List<DocumentVersion> versions = getHibernateTemplate().find("from DocumentVersion where documentId = " + document.getId());
		for(DocumentVersion v : versions) {
			v.setParent(document);
		}
		return versions;
	}
	
	public DocumentVersion addVersion(DocumentVersion version) {
		int id = (Integer) getHibernateTemplate().save(version);
		version.setId(id);
		return version;
	}
	
	public DocumentVersion updateVersion(DocumentVersion version) {
		getHibernateTemplate().update(version);
		return version;
	}

	public void deleteForDocument(GedDocument d) {
		List<DocumentVersion> versions = getDocumentVersionForDocument(d);
		getHibernateTemplate().deleteAll(versions);
	}

	public DocumentVersion getDocumentVersionById(Integer id) {
		return (DocumentVersion) getHibernateTemplate().find("from DocumentVersion where id = " + id).get(0);
	}

	public DocumentVersion getDocumentVersionForDocumentAndVersion(GedDocument gdoc, int version) {
		for(DocumentVersion v : getDocumentVersionForDocument(gdoc)) {
			if(v.getVersion() == version) {
				return v;
			}
		}
		return null;
	}
}
