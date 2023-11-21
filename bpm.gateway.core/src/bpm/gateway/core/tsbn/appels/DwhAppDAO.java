package bpm.gateway.core.tsbn.appels;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DwhAppDAO extends HibernateDaoSupport {

	public void save(DwhApp app, String idSamu) {
		app.setIdSamu(idSamu);
		
		if(app.getReferentiels() != null && app.getReferentiels().getAgents() != null) {
			for(Agents agent : app.getReferentiels().getAgents()) {
				agent.setAgtStruct(agent.getAgtStruct());
			}
		}
		
		getHibernateTemplate().save(app);
	}

//	public void save(Referentiels referentiels) {
//		getHibernateTemplate().save(referentiels);	
//	}
//
//	public void save(AgtWrs agt) {
//		getHibernateTemplate().save(agt);	
//	}
//
//	public void save(Agents agt) {
//		getHibernateTemplate().saveOrUpdate(agt);	
//	}
//
//	public void save(AgtStruct agtStr) {
//		getHibernateTemplate().saveOrUpdate(agtStr);	
//	}
//
//	public void save(Appel appel) {
//		getHibernateTemplate().save(appel);	
//	}
//
//	public void save(AppEntrant appelEntrant) {
//		getHibernateTemplate().save(appelEntrant);	
//	}
//
//	public void save(AppInt appInt) {
//		getHibernateTemplate().save(appInt);	
//	}
//
//	public void save(AppSortant appSortant) {
//		getHibernateTemplate().save(appSortant);	
//	}
//
//	public void save(AppTrsf appTrsf) {
//		getHibernateTemplate().save(appTrsf);	
//	}
//
//	public void save(Communes communes) {
//		getHibernateTemplate().save(communes);	
//	}
//
//	public void save(FilesAppels filesAppels) {
//		getHibernateTemplate().save(filesAppels);	
//	}
//
//	public void save(Secteurs secteurs) {
//		getHibernateTemplate().save(secteurs);	
//	}
//
//	public void save(Structures structures) {
//		getHibernateTemplate().save(structures);	
//	}
}
