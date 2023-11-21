package bpm.vanilla.platform.core.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;

public class Repository implements IRepository {

	private IRepositoryApi api;
	
	private HashMap<Integer, RepositoryDirectory> directories = new HashMap<Integer, RepositoryDirectory>();
	private HashMap<Integer, RepositoryItem> items = new HashMap<Integer, RepositoryItem>();

	private int type = -1;
	private int subtype;
	private String modeltype;
	
	public Repository(IRepositoryApi api) {
		this.api = api;
	}
	
	public Repository(IRepositoryApi api, int type) {
		this.api = api;
		this.type = type;
	}
	
	public Repository(IRepositoryApi api, int type, int subtype) {
		this.api = api;
		this.type = type;
		this.subtype = subtype;
	}
	
	public Repository(IRepositoryApi api, int type, int subtype, String modelType) {
		this.api = api;
		this.type = type;
		this.subtype = subtype;
		this.modeltype = modelType;
	}
	
	@Override
	public List<RepositoryDirectory> getChildDirectories(RepositoryDirectory directory) throws Exception {
		
		getDirectoryContent(directory);
		
		List<IRepositoryObject> objects = api.getRepositoryService().getDirectoryContent(directory, IRepositoryService.ONLY_DIRECTORY);
//		List<IRepositoryObject> objects = api.getRepositoryService().getDirectoryContent(directory);
		List<RepositoryDirectory> res = new ArrayList<RepositoryDirectory>();
		for(Object obj : objects) {
			if(obj instanceof RepositoryDirectory) {
				RepositoryDirectory dir = (RepositoryDirectory) obj;
				res.add(dir);
				if(directories.get(dir.getId()) == null) {
					directories.put(dir.getId(), dir);
				}
			}
		}
		return res;
	}

	@Override
	public RepositoryDirectory getDirectory(int id) throws Exception {
		if(directories.get(id) != null) {
			return directories.get(id);
		}
		RepositoryDirectory dir = api.getRepositoryService().getDirectory(id);
		directories.put(id, dir);
		return dir;
	}
	
	public void add(RepositoryDirectory dir){
		if (dir != null && dir.getId() > 0){
			directories.put(dir.getId(), dir);
		}
		
	}
	
	public void add(RepositoryItem it){
		if (it!= null && it.getId() > 0){
			items.put(it.getId() , it);
		}
	}
	
	public void remove(RepositoryDirectory dir){
		directories.remove(dir.getId());
	}
	public void remove(RepositoryItem it){
		items.remove(it.getId());
	}

	@Override
	public RepositoryItem getItem(int id) throws Exception {
		if(items.get(id) != null) {
			return items.get(id);
		}
		RepositoryItem it = api.getRepositoryService().getDirectoryItem(id);
		items.put(id, it);
		return it;
	}

	@Override
	public List<RepositoryItem> getItems(RepositoryDirectory directory) throws Exception {
		List<IRepositoryObject> objects = api.getRepositoryService().getDirectoryContent(directory, IRepositoryService.ONLY_ITEM);
//		List<IRepositoryObject> objects = api.getRepositoryService().getDirectoryContent(directory);
		List<RepositoryItem> res = new ArrayList<RepositoryItem>();
		for(Object obj : objects) {
			if(obj instanceof RepositoryItem) {
				RepositoryItem it = (RepositoryItem) obj;
				if(isValid(it)) {
					res.add(it);
					if(items.get(it.getId()) == null) {
						items.put(it.getId(), it);
					}
				}
			}
		}
		return res;
	}

	/**
	 * Check the type, subType and modelType
	 * @param it
	 * @return
	 */
	private boolean isValid(RepositoryItem it) {
		if(type > -1) {
			return testType(it);
		}
		return true;
	}

	/**
	 * Test the item type
	 * @param it
	 * @return
	 */
	private boolean testType(RepositoryItem it) {
		if(it.getType() == type) {
			boolean isOk = true;
			if (it.getSubtype() == IRepositoryApi.CUST_TYPE){
				if(subtype > -1) {
					isOk = testSubtype(it);
				}
				
			}
			if(modeltype != null && !modeltype.equals("")) {
				if(!isOk) {
					return false;
				}
				isOk = testModelType(it);
			}
			return isOk;
		}
		else if(type == IRepositoryApi.ALL_REPORT_TYPE) {
			if(it.getType() == IRepositoryApi.FWR_TYPE || it.getType() == IRepositoryApi.CUST_TYPE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test the item subType
	 * @param it
	 * @return
	 */
	private boolean testSubtype(RepositoryItem it) {
		return it.getSubtype() == subtype;
	}

	/**
	 * Test the item modelType
	 * @param it
	 * @return
	 */
	private boolean testModelType(RepositoryItem it) {
		return it.getModelType().equals(modeltype);
	}

	@Override
	public List<RepositoryDirectory> getRootDirectories() throws Exception {
		List<IRepositoryObject> objects = api.getRepositoryService().getDirectoryContent(null, IRepositoryService.ONLY_DIRECTORY);
//		List<IRepositoryObject> objects = api.getRepositoryService().getDirectoryContent(null);
		List<RepositoryDirectory> res = new ArrayList<RepositoryDirectory>();
		for(Object obj : objects) {
			if(obj instanceof RepositoryDirectory) {
				RepositoryDirectory dir = (RepositoryDirectory) obj;
				res.add(dir);
				if(directories.get(dir.getId()) == null) {
					directories.put(dir.getId(), dir);
				}
			}
		}
		return res;
	}

	@Override
	public List<IRepositoryObject> getDirectoryContent(RepositoryDirectory directory) throws Exception {
		List<IRepositoryObject> l = new ArrayList<IRepositoryObject>();
		for(Integer k : directories.keySet()){
			if (directories.get(k) != null && directories.get(k).getParentId() == directory.getId()){
				l.add(directories.get(k));
			}
		}
		for(Integer k : items.keySet()){
			if (items.get(k) != null && items.get(k).getDirectoryId() == directory.getId()){
				l.add(items.get(k));
			}
		}
		if (l.isEmpty()){
			
			l.addAll(api.getRepositoryService().getDirectoryContent(directory, IRepositoryService.BOTH));
//			l.addAll(api.getRepositoryService().getDirectoryContent(directory));
			for(Object o : l){
				if (o instanceof RepositoryItem){
					add((RepositoryItem)o);
				}
				else if (o instanceof RepositoryDirectory){
					add((RepositoryDirectory)o);
				}
			}
			
		}
		return l;
	}

	@Override
	public void clear() throws Exception {
		directories.clear();
		items.clear();
	}

	public Collection<RepositoryItem> getAllItems()  throws Exception {
		Collection<RepositoryItem> l = new ArrayList<RepositoryItem>();
		for(RepositoryDirectory d : getRootDirectories()){
			l.addAll(getAllItems(d));
			
		}
		return l;
	}
	
	private List<RepositoryItem> getAllItems(RepositoryDirectory r) throws Exception{
		 List<RepositoryItem> l = new ArrayList<RepositoryItem>();
		 l.addAll(getItems(r));
		 for(RepositoryDirectory d : getChildDirectories(r)){
			 l.addAll(getAllItems(d));
		 }
		 return l;
	}

	@Override
	public List<RepositoryDirectory> getRepositoryTree() throws Exception {
		List<RepositoryDirectory> rootDirs = getRootDirectories();
		if(rootDirs != null) {
			for(RepositoryDirectory dir : rootDirs) {
				buildTree(dir);
			}
			return rootDirs;
		}
		else {
			return new ArrayList<RepositoryDirectory>();
		}
	}
	
	private void buildTree(RepositoryDirectory dir) throws Exception {
		List<IRepositoryObject> result = new ArrayList<IRepositoryObject>();
		
		List<IRepositoryObject> childs = getDirectoryContent(dir);
		if(childs != null) {
			for(IRepositoryObject obj : childs) {
				if(obj instanceof RepositoryDirectory) {
					buildTree((RepositoryDirectory)obj);
					result.add(obj);
				}
				else if(obj instanceof RepositoryItem) {
					if(isValid((RepositoryItem)obj)) {
						result.add(obj);
					}
				}
			}
		}
		dir.setChilds(result);
	}

	@Override
	public List<RepositoryItem> getItems(String search) throws Exception {
		return api.getRepositoryService().getItems(search);
	}

}
