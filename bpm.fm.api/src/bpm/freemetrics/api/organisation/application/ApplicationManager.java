package bpm.freemetrics.api.organisation.application;

import java.util.Date;
import java.util.List;

public class ApplicationManager {
	private ApplicationDAO dao;

	public ApplicationManager() {
		super();
	}

	public void setDao(ApplicationDAO d) {
		this.dao = d;
	}

	public ApplicationDAO getDao() {
		return dao;
	}

	public List<Application> getApplications() {
		return dao.findAll();
	}

	public Application getApplicationById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addApplication(Application d) throws Exception{
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			return dao.save(d);
//			throw new Exception("This application name is already used");
		}
	}

	public void delApplication(Application d) {
		if(d != null)
			dao.delete(d);
	}

	public boolean updateApplication(Application d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This application doesnt exists");
		}
	}

	public Application getApplicationByName(String name) {
		return dao.findForName(name);
	}

	public List<Application> getApplicationByTypeTerrId(int typTerId) {
		return dao.getApplicationByTypeTerrId(typTerId) ;
	}
	
	public Application getApplicationForHistory(int appId, Date date) {
		return dao.getApplicationForHistory(appId, date);
	}

}
