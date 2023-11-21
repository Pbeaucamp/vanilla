package bpm.vanilla.platform.core.runtime.dao.ged;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.ged.Security;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class SecurityDAO extends HibernateDaoSupport {

	public List<Security> findAll() {
		return getHibernateTemplate().find("from Security");
	}

	public Security findByPrimaryKey(int key) {
		List<Security> c = getHibernateTemplate().find("from Security d where d.id=" +  key);
		if (c != null && c.size() > 0){
			return (Security)c.get(0);
		}
		else{
			return null;
		}
	}
	
	public List<Security> findForGroup(int groupId) {
		return getHibernateTemplate().find("from Security d where d.groupId=" + groupId);
	}
	
	public List<Security> findForDocument(int documentId) {
		List<Security> secus = getHibernateTemplate().find("from Security d where d.documentId=" + documentId);
		if(secus == null) {
			secus = new ArrayList<Security>();
		}
		return secus;
	}

	public int save(Security d) {
		return (Integer) getHibernateTemplate().save(d);
	}

	public void delete(Security d) {
		getHibernateTemplate().delete(d);
	}
	
	public List<Security> findForGroupsAndRepository(Integer userId, List<String> groupId, Integer repositoryId) {
		String gr = "(";
		boolean first = true;
		for (String g : groupId) {
			if (first) {
				gr += g;
				first = false;
			}
			else {
				gr += "," + g;
			}
		}
		gr += ")";
		//if (repositoryId == null || repositoryId == -1) {
		//userId was set at -1 means that search is from super duper user
		if (userId != -1) {
			return getHibernateTemplate().find("from Security d where d.groupId in " + gr + " OR d.userId = " + userId);
		} 
		else {
			//-1 was passed
			return getHibernateTemplate().find("from Security d");
		}
		//}
		//else {
			//i dont thing it s called anymore, ere
			//dont use
			//return hibernateTemplate.find("from Security d where (d.repositoryId = " + repositoryId + " OR d.repositoryId is null) AND d.groupId in " + gr );
		//}
	}

	public Security findByIds(int documentId, int groupId, Integer repositoryId) {
		List<Security> sec = new ArrayList<Security>();
		if (repositoryId == null || repositoryId == -1) {
			sec = getHibernateTemplate().find("from Security d where d.repositoryId is null AND d.groupId = " + groupId + " AND d.documentId=" + documentId);
		}
		else {
			sec = getHibernateTemplate().find("from Security d where d.repositoryId = " + repositoryId + "AND d.groupId = " + groupId + " AND d.documentId=" + documentId);
		}
		
		if (sec.isEmpty()) {
			return null;
		}
		else {
			return sec.get(0);
		}
	}

	public void update(Security s) {
		getHibernateTemplate().update(s);
		
	}

	public List<Integer> findAuthorizedDocumentIds(List<Integer> groupId, int repositoryId) {
		
		String groupIds = " in(";
		boolean first = true;
		for(int i : groupId) {
			if(first) {
				first = false;
			}
			else {
				groupIds = groupIds + ",";
			}
			groupIds = groupIds + i;
		}
		groupIds = groupIds + ")";
		List<Security> sec = null;
		if(!groupId.isEmpty() && groupId.get(0) > 0) {
			sec = getHibernateTemplate().find("from Security d where d.repositoryId = " + repositoryId + " AND d.groupId " + groupIds);	
		}
		else {
			sec = getHibernateTemplate().find("from Security d where d.repositoryId = " + repositoryId);
		}
		
		List<Integer> res = new ArrayList<Integer>();
		if(sec != null && sec.size() > 0) {
			for(Security s : sec) {
				res.add(s.getDocumentId());
			}
		}
		
		return res;
	}

	public void deleteSecurityForDoc(int docId) {
		List<Security> secs = getHibernateTemplate().find("from Security d where d.documentId = " + docId);	
		if(secs != null && !secs.isEmpty()){
			for(Security sec : secs){
				delete(sec);
			}
		}
	}


}
