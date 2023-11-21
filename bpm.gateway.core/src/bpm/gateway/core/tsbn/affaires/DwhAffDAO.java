package bpm.gateway.core.tsbn.affaires;

import java.util.List;

import bpm.vanilla.platform.hibernate.HibernateDaoSupport;

public class DwhAffDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public void save(DwhAff app, String idSamu) {
		app.setIdSamu(idSamu);
		
		if(app.getRef() != null && app.getRef().getAgt() != null) {
			for(AGT agt : app.getRef().getAgt()) {
				agt.setAgtstruct(agt.getAgtstruct());
			}
			
			if (app.getAff() != null) {
				for (AFF aff : app.getAff()) {
					if (aff.getEpi() != null) {
						for (EPI epi : aff.getEpi()) {
							if (epi.getPha() != null) {
								for (PHA pha : epi.getPha()) {
									if (pha.getAct() != null) {
										for (ACT act : pha.getAct()) {
											act.setMoyref(act.getMoyref());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		getHibernateTemplate().save(app);
		
		//Delete the data with no value for the table 'moyref'
		List<MOYREF> moyRefs = getHibernateTemplate().find("FROM MOYREF WHERE idAct is null");
		if (moyRefs != null) {
			System.out.println("Delete " + moyRefs.size() + " lines of MoyRef");
			for (MOYREF moyRef : moyRefs) {
				getHibernateTemplate().delete(moyRef);
			}
		}

		//Delete the data with no value for the table 'decref'
		List<DECREF> decRefs = getHibernateTemplate().find("FROM DECREF WHERE idAct is null");
		if (decRefs != null) {
			System.out.println("Delete " + decRefs.size() + " lines of DecRef");
			for (DECREF decRef : decRefs) {
				getHibernateTemplate().delete(decRef);
			}
		}

		//Delete the data with no value for the table 'pecobs'
		List<PECOBS> pecObs = getHibernateTemplate().find("FROM PECOBS WHERE idAct is null");
		if (pecObs != null) {
			System.out.println("Delete " + pecObs.size() + " lines of PecObs");
			for (PECOBS pecOb : pecObs) {
				getHibernateTemplate().delete(pecOb);
			}
		}

		//Delete the data with no value for the table 'epiobs'
		List<EPIOBS> epiObs = getHibernateTemplate().find("FROM EPIOBS WHERE idAct is null");
		if (epiObs != null) {
			System.out.println("Delete " + epiObs.size() + " lines of EpiObs");
			for (EPIOBS epiOb : epiObs) {
				getHibernateTemplate().delete(epiOb);
			}
		}

		//Delete the data with no value for the table 'etabref'
		List<ETABREF> etabRefs = getHibernateTemplate().find("FROM ETABREF WHERE idAct is null");
		if (etabRefs != null) {
			System.out.println("Delete " + etabRefs.size() + " lines of EtabRefs");
			for (ETABREF etabRef : etabRefs) {
				getHibernateTemplate().delete(etabRef);
			}
		}

		//Delete the data with no value for the table 'epiapp'
		List<EPIAPP> epiApps = getHibernateTemplate().find("FROM EPIAPP WHERE idepi is null");
		if (epiApps != null) {
			System.out.println("Delete " + epiApps.size() + " lines of EpiApps");
			for (EPIAPP epiApp : epiApps) {
				getHibernateTemplate().delete(epiApp);
			}
		}

		//Delete the data with no value for the table 'etabtra'
		List<ETABTRA> etabTras = getHibernateTemplate().find("FROM ETABTRA WHERE idtra is null");
		if (etabTras != null) {
			System.out.println("Delete " + etabTras.size() + " lines of EtabTras");
			for (ETABTRA etabTra : etabTras) {
				getHibernateTemplate().delete(etabTra);
			}
		}
	}

//	public void save(REF ref) {
//		getHibernateTemplate().save(ref);
//	}
//
//	public void save(PAT pat) {
//		getHibernateTemplate().save(pat);
//	}
//
//	public void save(CIM10 cim10) {
//		getHibernateTemplate().save(cim10);	
//	}
//
//	public void save(OBS obs) {
//		getHibernateTemplate().save(obs);	
//	}
//
//	public void save(VEC vec) {
//		getHibernateTemplate().save(vec);	
//	}
//
//	public void save(ETAB etab) {
//		getHibernateTemplate().save(etab);	
//	}
//
//	public void save(STRUCT struct) {
//		getHibernateTemplate().save(struct);	
//	}
//
//	public void save(AGT agt) {
//		getHibernateTemplate().save(agt);	
//	}
//
//	public void save(AGTSTRUCT agtStruct) {
//		getHibernateTemplate().saveOrUpdate(agtStruct);	
//	}

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
