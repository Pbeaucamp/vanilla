package bpm.freemetrics.api.organisation.dashOrTheme;

import java.util.List;

public class ThemeManager {
	private ThemeDAO dao;

	public ThemeManager() {
		super();
	}


	public void setDao(ThemeDAO d) {
		this.dao = d;
	}

	public ThemeDAO getDao() {
		return dao;
	}

	public List<Theme> getThemes() {
		return dao.findAll();
	}

	public Theme getThemeById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addTheme(Theme d) throws Exception{

		if (dao.findForName(d.getName()) == null ){
			return dao.save(d);
		}else{
			throw new Exception("This Theme already exists");
		}
	}

	public boolean delTheme(Theme d) {
		dao.delete(d);
		return getThemeById(d.getId()) == null;
	}

	public boolean updateTheme(Theme d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This Theme doesnt exists");
		}
	}

	public Theme getThemeByName(String name) {
		return dao.findForName(name);
	}

}
