package bpm.freemetrics.api.organisation.observatoire;

import java.util.List;

import bpm.freemetrics.api.exception.ThemeObservatoireException;
import bpm.freemetrics.api.exception.UserObservatoireException;

public class ObservatoireManager {

	ObservatoiresUsersDAO assoUserDAO;
	ObservatoiresThemesDAO assoThemeDAO;
	ObservatoireDAO obsDAO;
	
	public ObservatoireManager() {
		super();
	}

	public ObservatoiresUsersDAO getAssoUserDAO() {
		return assoUserDAO;
	}

	public void setAssoUserDAO(ObservatoiresUsersDAO assoUserDAO) {
		this.assoUserDAO = assoUserDAO;
	}
	
	public List<Observatoire> findObservatoireByUserId(int userId) {
		return obsDAO.findByUserId(userId);
	}
	
	public List<ObservatoiresUsers> getObservatoirsUsersByObsId(int id) {
		return assoUserDAO.findByObsId(id);
	}
	
	public List<ObservatoiresThemes> getObservatoiresThemeByObsId(int observatoireId) {
		return assoThemeDAO.findByObservatoireId(observatoireId);
	}
	
	public ObservatoiresThemesDAO getAssoThemeDAO() {
		return assoThemeDAO;
	}

	public void setAssoThemeDAO(ObservatoiresThemesDAO assoThemeDAO) {
		this.assoThemeDAO = assoThemeDAO;
	}

	public ObservatoireDAO getObsDAO() {
		return obsDAO;
	}

	public void setObsDAO(ObservatoireDAO obsDAO) {
		this.obsDAO = obsDAO;
	}

	public Observatoire getObservatoireById(int id) {
		return obsDAO.getById(id);
	}

	public List<Observatoire> getObservatoires() {
		return obsDAO.findAll();
	}

	public Observatoire getObservatoireByName(String name) {
		return obsDAO.finByName(name);
	}
	
	public void update(Observatoire observatoire) {
		obsDAO.update(observatoire);
	}

	public void delete(Observatoire obs) throws UserObservatoireException, ThemeObservatoireException {
		List<ObservatoiresThemes> ot = assoThemeDAO.findByObservatoireId(obs.getId());
		if (ot != null && !ot.isEmpty()) {
			throw new UserObservatoireException();
		}
		List<ObservatoiresUsers> ou = assoUserDAO.findByObsId(obs.getId());
			
		if (ou != null & !ou.isEmpty()) {
			throw new ThemeObservatoireException();
		}
		obsDAO.delete(obs);		
	}

	public int add(Observatoire obs) {
		return obsDAO.add(obs);
	}

	public int addObservatoiresThemes(ObservatoiresThemes ot) {
		return assoThemeDAO.add(ot);
	}

	public int addObservatoiresUsers(ObservatoiresUsers ou) throws Exception {
		List<ObservatoiresUsers> l = assoUserDAO.getByUserAndObservatoire(ou.getUserId(), ou.getObsId());
		if (l.isEmpty())
			return assoUserDAO.add(ou);
		else
			throw new Exception("User allready in this Observatory");
	}

	public void removeObservatoiresThemes(int themeId, int obsId) {
		assoThemeDAO.remove(themeId, obsId);
	}

	public void removeObservatoiresUsers(int userId, int obsId) {
		assoUserDAO.remove(userId, obsId);		
	}
	
}
