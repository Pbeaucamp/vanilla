package bpm.freemetrics.api.security;

import java.util.List;

import bpm.freemetrics.api.utils.IReturnCode;

public class FmUserManager {

		private FmUserDAO dao;
		
		public FmUserManager() {
			super();
		}
		
		public void setDao(FmUserDAO d) {
			this.dao = d;
		}

		public FmUserDAO getDao() {
			return dao;
		}

		public List<FmUser> getUsers() {
			return dao.findAll();
		}
		
		public FmUser getUserById(int id) {
			return dao.findByPrimaryKey(id);
		}

		public int addUser(FmUser d) throws Exception{
			if (dao.getUserByLogin(d.getLogin()) == null){
				return dao.save(d);
			}
			else{
				throw new Exception("This username is already used");
			}
			 
		}

		public boolean delUser(FmUser d) {
			dao.delete(d);
			return dao.findByPrimaryKey(d.getId()) == null;
		}

		public int updateUser(FmUser d) throws Exception {
			if (dao.findByPrimaryKey(d.getId()) != null){
				dao.update(d);
				return IReturnCode.OPERATION_DONE_SUCCESFULLY; 
			}
			else{
				throw new Exception("This user doesnt exists");
			}
			
		}

		public FmUser getUserByNameAndPass(String name, String password) {
			return dao.findForNameAndPass(name, password);
		}

		public FmUser getUserByLogin(String name) {			
			return dao.getUserByLogin(name);
		}

		public boolean deleteUserById(int id) {
			FmUser g = dao.findByPrimaryKey(id);
			if(g == null)
			return true;
			
			return dao.delete(g);
		}
		
		public FmUser getUserByNameAndPass(String username, String password, boolean isEncrypted) {
			return dao.findForNameAndPass(username, password, isEncrypted);
		}
	}