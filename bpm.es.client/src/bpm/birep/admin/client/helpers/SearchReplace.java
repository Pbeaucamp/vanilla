package bpm.birep.admin.client.helpers;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class SearchReplace {
	private String oldString;
	private String newString;
	private static SearchReplace instance;
	
	private SearchReplace() {
		super();
	}

	public static SearchReplace getInstance(String oldString, String newString) {
		if (instance == null) {
			instance = new SearchReplace();
		}
		instance.oldString = oldString;
		instance.newString = newString;
		
		return instance;
	}
	
	
	public void run(IRepositoryApi sock) throws Exception{
		Repository r = new Repository(sock);
		for(RepositoryDirectory d : r.getRootDirectories()) {
			searchForChilds(r, d, sock);
		
			for(RepositoryItem it : r.getItems(d)){
				try {
					String xml = sock.getRepositoryService().loadModel(it);
					if (xml.indexOf(oldString) > -1)
						sock.getRepositoryService().updateModel(it, xml.replace(oldString, newString));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		}

	}

	private void searchForChilds(Repository rep, RepositoryDirectory parent, IRepositoryApi sock) throws Exception{
		for(RepositoryDirectory d : rep.getChildDirectories(parent)){
			
			for(RepositoryItem it : rep.getItems(d)){
				try {
					String xml = sock.getRepositoryService().loadModel(it);
					if (xml.indexOf(oldString) > -1)
						sock.getRepositoryService().updateModel(it, xml.replace(oldString, newString));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			searchForChilds(rep, d, sock);	
		
		}
		
	}
}
