package bpm.norparena.ui.menu.client.helpers;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
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
	
	
	public void run(IRepositoryApi sock, IRepository r) throws Exception{
		
		for(RepositoryDirectory d : r.getRootDirectories()) {
			searchForChilds(d, sock, r);
		
			for(RepositoryItem it : r.getItems(d)){
				if (it.getType() == IRepositoryApi.CUST_TYPE) {
					if (!(it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE)) { //$NON-NLS-1$
						try {
							String xml = sock.getRepositoryService().loadModel(it);
							if (xml.indexOf(oldString) > -1)
								sock.getRepositoryService().updateModel(it, xml.replace(oldString, newString));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if ((it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE)) { //$NON-NLS-1$
						try {
							String xml = sock.getRepositoryService().loadModel(it);
							if (xml.indexOf(oldString) > -1)
								sock.getRepositoryService().updateModel(it, xml.replace(oldString, newString));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else {
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

	}

	private void searchForChilds(RepositoryDirectory parent, IRepositoryApi sock, IRepository rep) throws Exception{
		for(RepositoryDirectory d : rep.getChildDirectories(parent)){
			
			for(RepositoryItem it : rep.getItems(d)){
				if (it.getType() == IRepositoryApi.CUST_TYPE) {
					if (!(it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE)) { //$NON-NLS-1$
						try {
							String xml = sock.getRepositoryService().loadModel(it);
							if (xml.indexOf(oldString) > -1)
								sock.getRepositoryService().updateModel(it, xml.replace(oldString, newString));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else if ((it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE)) { //$NON-NLS-1$
						try {
							String xml = sock.getRepositoryService().loadModel(it);
							if (xml.indexOf(oldString) > -1)
								sock.getRepositoryService().updateModel(it, xml.replace(oldString, newString));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				else {
					try {
						String xml = sock.getRepositoryService().loadModel(it);
						if (xml.indexOf(oldString) > -1)
							sock.getRepositoryService().updateModel(it, xml.replace(oldString, newString));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			searchForChilds(d, sock,rep);	
		
		}
		
	}
}
