package bpm.freemetrics.api.security;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FmRoleManager {
	
	private FmRoleDAO dao;
	
	public FmRoleManager() {
		super();
	}
	
	public void setDao(FmRoleDAO d) {
		this.dao = d;
	}

	public FmRoleDAO getDao() {
		return dao;
	}

	public List<FmRole> getRoles() {
		return dao.findAll();
	}
	
	public FmRole getRoleById(int id) {
		return dao.findByPrimaryKey(id);
	}

	@SuppressWarnings("unchecked")
	public int addRole(FmRole d) throws Exception{
		Collection c = dao.findAll();
		Iterator it = c.iterator();
		boolean exist = false;
		
		while(it.hasNext()){
			if (((FmRole)it.next()).getName().equals(d.getName())){
				exist = true;
				break;
			}
		}
		if (!exist){
			return dao.save(d);
		}
		else{
			throw new Exception("This role is already used");
		}
		 
	}

	public boolean delRole(FmRole d) {
		dao.delete(d);
		return dao.findByPrimaryKey(d.getId())== null;
	}

	public boolean updateRole(FmRole d) throws Exception {
		if (dao.findByPrimaryKey(d.getId()) != null){
			dao.update(d);
			return true;
		}
		else{
			throw new Exception("This role doesnt exists");
		}
	}

	public boolean isRoleSuperAdmin(int roleId) {
		FmRole rol = getRoleById(roleId);
		
		if(rol != null && rol.getGrants() != null && rol.getGrants().contains("A")
				&& rol.getGrants().contains("D") && rol.getGrants().contains("W")
				&& rol.getGrants().contains("U")&& rol.getGrants().contains("L")){
			
			return true;
		}else{
			return false;
		}
	}

	public FmRole getRoleByName(String name) {
		return dao.findForName(name);
	}
	

}
