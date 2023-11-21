package bpm.vanilla.platform.core.runtime.dao.preferences;


import java.util.Iterator;
import java.util.List;

import bpm.vanilla.platform.core.beans.OpenPreference;



public class OpenPreferenceManager {
	private OpenPreferenceDAO dao;
	
	public OpenPreferenceManager() {
		super();
	}
	
	
	public void setDao(OpenPreferenceDAO d) {
		this.dao = d;
	}

	public OpenPreferenceDAO getDao() {
		return dao;
	}

	public List<OpenPreference> getOpenPreferences() {
		return dao.findAll();
	}
	
	public OpenPreference getOpenPreferenceById(int id) {
		return dao.findByPrimaryKey(id);
	}
	
	public List<OpenPreference> getUserOpenPreferences(int userId) {
		return dao.findOpenPreferencesByUserId(userId);
	}

	public int  addOpenPreference(OpenPreference d) {
		return dao.save(d);	 
	}

	public void delOpenPreference(OpenPreference d) {
		dao.delete(d);
	}

	public void updateOpenPreference(OpenPreference d) throws Exception {
		List<OpenPreference> c = dao.findAll();
		Iterator<OpenPreference> it = c.iterator();
		int i = 0;
		while(it.hasNext()){
			if (((OpenPreference)it.next()).getId() == d.getId()){
				i++;
			}
		}
		if (i<=1){
			dao.update(d);
		}
		else{
			throw new Exception("This OpenPreference doesnt exists");
		}
		
	}
	
	
}


