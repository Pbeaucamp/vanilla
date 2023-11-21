package bpm.vanilla.workplace.server.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import bpm.vanilla.workplace.server.config.PlaceConfiguration;
import bpm.vanilla.workplace.shared.model.PlaceWebLog;
import bpm.vanilla.workplace.shared.model.PlaceWebPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebUserPackage;
import bpm.vanilla.workplace.shared.model.PlaceWebLog.LogType;

public class PackageDAO extends HibernateDaoSupport {

	@SuppressWarnings("unchecked")
	public List<PlaceWebPackage> getAllPackages() {
		return (List<PlaceWebPackage>) getHibernateTemplate().find("from PlaceWebPackage");
	}
	
	@SuppressWarnings("unchecked")
	public PlaceWebPackage findByPrimaryKey(int key) {
		List<PlaceWebPackage> l = (List<PlaceWebPackage>) getHibernateTemplate().find("from PlaceWebPackage where id=" + key);
		if (l.isEmpty()){
			return null;
		}
		return l.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<PlaceWebPackage> getPackagesByUserAndProjectId(int userId, int projectId){
		List<PlaceWebUserPackage> userPackages = (List<PlaceWebUserPackage>) 
			getHibernateTemplate().find("from PlaceWebUserPackage where userId = " + userId);
		
		List<PlaceWebPackage> packagesForProject = new ArrayList<PlaceWebPackage>();
		for(PlaceWebUserPackage userPack : userPackages){
			List<PlaceWebPackage> packages = (List<PlaceWebPackage>) 
				getHibernateTemplate().find("from PlaceWebPackage where id = " + userPack.getPackageId() + " and projectId = " + projectId);
			
			if(packages != null && !packages.isEmpty()){
				packagesForProject.add(packages.get(0));
			}
		}
		
		return packagesForProject;
	}
	
	@SuppressWarnings("unchecked")
	public List<PlaceWebPackage> getPackagesByProjectId(int projectId){
		List<PlaceWebPackage> packages = (List<PlaceWebPackage>) 
			getHibernateTemplate().find("from PlaceWebPackage where projectId = " + projectId);
		if(packages == null){
			return new ArrayList<PlaceWebPackage>();
		}
		return packages;
	}
	
	@SuppressWarnings("unchecked")
	public List<PlaceWebPackage> getPackagesByUserId(int userId){
		List<PlaceWebUserPackage> userPackages = (List<PlaceWebUserPackage>) 
			getHibernateTemplate().find("from PlaceWebUserPackage where userId = " + userId);
		
		List<PlaceWebPackage> packagesForProject = new ArrayList<PlaceWebPackage>();
		for(PlaceWebUserPackage userPack : userPackages){
			List<PlaceWebPackage> packages = (List<PlaceWebPackage>) 
				getHibernateTemplate().find("from PlaceWebPackage where id = " + userPack.getPackageId());
			
			if(packages != null && !packages.isEmpty()){
				packagesForProject.add(packages.get(0));
			}
		}
		
		return packagesForProject;
	}
	
	public int save(int userId, PlaceWebPackage d, PlaceConfiguration config) {
		d.setId((Integer)getHibernateTemplate().save(d));
		
		PlaceWebLog log = new PlaceWebLog(LogType.EXPORT_PACKAGE, userId, new Date(), 
				d.getProjectId(), d.getId());
		config.getLogDao().save(log);
		
		PlaceWebUserPackage userPack = new PlaceWebUserPackage(userId, d.getId());
		config.getUserPackageDao().save(userPack);
		
		return d.getId();
	}
	
	@SuppressWarnings("unchecked")
	public void deletePackage(int packageId){
		List<PlaceWebUserPackage> userPackages = (List<PlaceWebUserPackage>) 
			getHibernateTemplate().find("from PlaceWebUserPackage where packageId = " + packageId);
	
		for(PlaceWebUserPackage userPack : userPackages){
			getHibernateTemplate().delete(userPack);
		}
		
		PlaceWebPackage pack = new PlaceWebPackage();
		pack.setId(packageId);
		getHibernateTemplate().delete(pack);
	}

	public void delete(PlaceWebPackage d) {
		getHibernateTemplate().delete(d);
	}
	
	public void update(PlaceWebPackage d) {
		getHibernateTemplate().update(d);
	}
}
