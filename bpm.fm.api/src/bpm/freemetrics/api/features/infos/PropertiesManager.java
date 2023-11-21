package bpm.freemetrics.api.features.infos;

import java.util.List;

public class PropertiesManager {
	private PropertiesDAO dao;

	public PropertiesManager() {
		super();
	}

	public void setDao(PropertiesDAO d) {
		this.dao = d;
	}

	public PropertiesDAO getDao() {
		return dao;
	}

	public List<Property> getProperties() {
		return dao.findAll();
	}

	public Property getPropertyById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addProperty(Property d) throws Exception{

		if ( dao.findForName(d.getName())== null){
			return dao.save(d);
		}else{
			throw new Exception("This Property name is already used");
		}

	}

	public void delAlert(Property d) {
		dao.delete(d);
	}

	public void updateAlert(Property d) throws Exception {

		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
		}else{
			throw new Exception("This Property doesnt exists");
		}

	}

}
