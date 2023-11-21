package bpm.freemetrics.api.organisation.group;

import java.util.List;

public class TypeCollectiviteManager {
	private TypeCollectiviteDAO dao;

	public TypeCollectiviteManager() {
		super();
	}


	public void setDao(TypeCollectiviteDAO d) {
		this.dao = d;
	}

	public TypeCollectiviteDAO getDao() {
		return dao;
	}

	public List<TypeCollectivite> getTypeCollectivites() {
		return dao.findAll();
	}

	public TypeCollectivite getTypeCollectiviteById(int id) {
		return dao.findByPrimaryKey(id);
	}

	public int addTypeCollectivite(TypeCollectivite d) throws Exception{
		if (dao.findForName(d.getName()) == null){
			return dao.save(d);
		}else{
			throw new Exception("This TypeCollectivite already exists");
		}

	}

	public boolean delTypeCollectivite(TypeCollectivite d) {
		dao.delete(d);
		return dao.findByPrimaryKey(d.getId()) == null;
	}

	public boolean updateTypeCollectivite(TypeCollectivite d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}else{
			throw new Exception("This TypeCollectivite doesnt exists");
		}

	}


	public TypeCollectivite getTypeCollectiviteByName(String name) {
		return dao.findForName(name) ;
	}

}
