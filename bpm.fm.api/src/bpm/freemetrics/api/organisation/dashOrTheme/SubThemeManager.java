package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.List;

public class SubThemeManager {
	private SubThemeDAO dao;
	
	public SubThemeManager() {
		super();
	}
	
	
	public void setDao(SubThemeDAO d) {
		this.dao = d;
	}

	public SubThemeDAO getDao() {
		return dao;
	}

	public List<SubTheme> getSubThemes() {
		return dao.findAll();
	}
	
	public SubTheme getSubThemeById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addSubTheme(SubTheme d) throws Exception{
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}
		else{
			throw new Exception("This SubTheme already existe");
		}
		 
	}

	public boolean delSubTheme(SubTheme d) {
		return dao.delete(d);
	}

	public boolean updateSubTheme(SubTheme d) throws Exception {
		
		if (dao.findByPrimaryKey(d.getId()) != null ){
			dao.update(d);
			return true;
		}
		else{
			throw new Exception("This SubTheme doesnt exists");
		}
		
	}


	public SubTheme getSubThemeByName(String name) {
		return dao.findForName(name);
	}


	public List<SubTheme> getSubThemeForThemeId(int themeId) {
		return dao.getSubThemeForThemeId(themeId);
	}

}
