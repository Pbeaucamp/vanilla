package bpm.vanilla.platform.core.repository;

import java.util.HashMap;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;

/**
 * helper class to load a repository content and store its content
 * @author ludo
 *
 */
public class RepositoryContent {
	private IRepositoryApi api;
	
	private HashMap<Integer, RepositoryDirectory> directories = new HashMap<Integer, RepositoryDirectory>();
	private HashMap<Integer, RepositoryItem> items = new HashMap<Integer, RepositoryItem>();
	
	public RepositoryContent(IRepositoryApi api){
		this.api = api;
	}
	
	public RepositoryDirectory getDirectory(int id) throws Exception{
		RepositoryDirectory d = directories.get(id);
		if (d == null){
			d = api.getRepositoryService().getDirectory(id);
			if (d == null){
				return null;
			}
			directories.put(id, d);
		}
		return d;
	}
	
	public RepositoryItem getItem(int id) throws Exception{
		RepositoryItem d = items.get(id);
		if (d == null){
			d = api.getRepositoryService().getDirectoryItem(id);
			if (d == null){
				return null;
			}
			items.put(id, d);
		}
		return d;
	}
	
	public void clear(){
		items.clear();
		directories.clear();
	}
}
