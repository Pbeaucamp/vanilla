package bpm.vanilla.platform.core.runtime.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryManager;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.UserGroup;
import bpm.vanilla.platform.core.beans.UserRep;
import bpm.vanilla.platform.core.runtime.dao.platform.RepositoryDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserDAO;
import bpm.vanilla.platform.core.runtime.dao.security.UserRepDAO;

public class RepositoryManager  extends AbstractVanillaManager implements IRepositoryManager{
	
	private RepositoryDAO repositoryDao;
	private UserRepDAO userRepDao;
	private UserDAO userDao;
	
	@Override
	public List<Repository> getRepositories() {
		return repositoryDao.findAll();
	}
	@Override
	public Repository getRepositoryById(int id) {
		return repositoryDao.findByPrimaryKey(id);
	}
	@Override
	public Repository getRepositoryByName(String name) {
		return repositoryDao.findForName(name);
	}
	@Override
	public void addRepository(Repository d) throws Exception{
		List<Repository> c = repositoryDao.findAll();
		Iterator<Repository> it = c.iterator();
		boolean exist = false;

		while(it.hasNext()){
			if (((Repository)it.next()).getName().equals(d.getName())){
				exist = true;
				break;
			}
		}
		if (!exist){
			repositoryDao.save(d);
		}
		else{
			throw new Exception("<error>This repository name is already used</error>");			
		}
		 
	}
	@Override
	public void deleteRepository(Repository d) {
		for (UserRep ur : userRepDao.findByRepositoryId(d.getId())) {
			userRepDao.delete(ur);
		}
		repositoryDao.delete(d);
	}
	@Override
	public void updateRepository(Repository d) throws Exception{
		List<Repository> c = repositoryDao.findAll();
		Iterator<Repository> it = c.iterator();
		int i = 0;
		while(it.hasNext()){
			if (((Repository)it.next()).getName().equals(d.getName())){
				i++;
			}
		}
		if (i<=1){
			repositoryDao.update(d);
		}
		else{
			throw new Exception("This repository doesnt exists");
		}
		
	}
	
	@Override
	public UserRep getUserRepById(int id) {
		return userRepDao.findByPrimaryKey(id);
	}
	@Override
	public List<UserRep> getUserRepByUserId(int userId){
		return userRepDao.findByUserId(userId);
	}
	@Override
	public List<UserRep> getUserRepByRepositoryId(int repositoryId) {
		return userRepDao.findByRepositoryId(repositoryId);
	}
	@Override
	public void addUserRep(UserRep d) throws Exception {
		UserRep userRep = userRepDao.findByUserRep(d.getUserId(), d.getRepositoryId());
		if (userRep == null)
			userRepDao.save(d);
		 
	}
	@Override
	public void delUserRep(UserRep d) {
		userRepDao.delete(d);
	}
	
	@Override
	public boolean hasUserHaveAccess(int repId, int userId) {
		List<UserRep> l = userRepDao.findIdUserRep(repId, userId);

		return !l.isEmpty();
	}
	@Override
	public List<Repository> getUserRepositories(String login) {
		List<Repository> repositories = new ArrayList<Repository>();
		List<User> users = userDao.findForLogin(login);
		User u = null;
		if (users != null && !users.isEmpty() && users.size() == 1) {
			u = users.get(0);
		}
		else {
			return null;
		}
		
		List<UserRep> joins = userRepDao.findByUserId(u.getId());
		for (UserRep j : joins) {
			Repository r = repositoryDao.findByPrimaryKey(j.getRepositoryId());
			repositories.add(r);
		}
		
		return repositories;
	}
	@Override
	public String getComponentName() {
		return getClass().getName();
	}
	@Override
	protected void init() throws Exception {
		this.repositoryDao = getDao().getRepositoryDao();
		if (this.repositoryDao == null){
			throw new Exception("Missing RepositoryDAO");
		}
		
		this.userDao = getDao().getUserDao();
		if (this.userDao == null){
			throw new Exception("Missing UserDAO");
		}
		
		this.userRepDao = getDao().getUserRepDao();
		if (this.userRepDao == null){
			throw new Exception("Missing UserRepDAO");
		}
		getLogger().info("init done!");
		
	}
	@Override
	public Repository getRepositoryFromUrl(String repositoryUrl)throws Exception {
		String url = new String(repositoryUrl);
		while(url.endsWith("/")){
			url = url.substring(0, url.length() - 1);
		}
		
		for(Repository r : getRepositories()){
			String _url = new String(r.getUrl());
			while(_url.endsWith("/")){
				_url = _url.substring(0, _url.length() - 1);
			}
			if (url.equals(_url)){
				return r;
			}
		}
		return null;
	}
	
	
}
