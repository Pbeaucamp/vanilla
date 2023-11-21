package bpm.vanilla.platform.core.repository;

import java.util.List;


public interface IRepository {

	public void add(RepositoryDirectory dir);
	public void remove(RepositoryDirectory dir);
	public void add(RepositoryItem it);
	public void remove(RepositoryItem it);
	
	public List<RepositoryDirectory> getRootDirectories() throws Exception;
	
	public List<RepositoryItem> getItems(RepositoryDirectory directory) throws Exception;
	
	public List<RepositoryDirectory> getChildDirectories(RepositoryDirectory directory) throws Exception;
	
	public RepositoryItem getItem(int id) throws Exception;
	
	public RepositoryDirectory getDirectory(int id) throws Exception;
	
	public List<IRepositoryObject> getDirectoryContent(RepositoryDirectory directory) throws Exception;
	
	public void clear() throws Exception;
	
	public List<RepositoryDirectory> getRepositoryTree() throws Exception;
	
	public List<RepositoryItem> getItems(String search) throws Exception;
}
