package bpm.es.dndserver.api.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.tools.OurLogger;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class RepositoryWrapper {

	private IRepositoryApi sock;
	private String repositoryName;
	private String groupName;
	
	private int repositoryId;
	
	private HashMap<Integer, List<AxisDirectoryItemWrapper>> mapObject 
			= new HashMap<Integer, List<AxisDirectoryItemWrapper>>();
	
	private HashMap<Integer, RepositoryDirectory> directories 
			= new HashMap<Integer, RepositoryDirectory>();
	
	public RepositoryWrapper(IRepositoryApi sock, Repository repDef) {
		this.sock = sock;
		
		this.repositoryName = repDef.getName();
		this.repositoryId = repDef.getId();
	}

	public String getRepositoryName() {
		return repositoryName;
	}
	
	public int getRepositoryId(){
		return repositoryId;
	}

	public void refreshRepository() throws Exception {
		IRepository rep =new bpm.vanilla.platform.core.repository.Repository(sock);
		List<RepositoryDirectory> dirs = 
			rep.getRootDirectories();
		//sock.getr
		for (RepositoryDirectory dir : dirs) {
			OurLogger.info(Messages.RepositoryWrapper_0 + dir.getName() +"'"); //$NON-NLS-1$
			//XXX : WHAT THE FUCK IS THIS ?????????????????????????????
//			((AxisDirectory)dir).loadContent();
		}
	}
	
	public IRepositoryApi getRepositoryClient() {
		return sock;
	}


	public IRepositoryApi getSock() {
		return sock;
	}
	
	/**
	 * Used to add temp objects that will be visible but not committed
	 * @param toAdd
	 * @param targetDirectory
	 */
	public void addTemporaryElements(List<AxisDirectoryItemWrapper> toAdd, 
			RepositoryDirectory targetDirectory) {
		//XXX special case for targetdir == null, no dad, root folder
		if (targetDirectory == null) {
			if (mapObject.containsKey(0)) {
				mapObject.get(0).addAll(toAdd);
			}
			else {
				List<AxisDirectoryItemWrapper> items = 
					new ArrayList<AxisDirectoryItemWrapper>();
				
				items.addAll(toAdd);
				
				mapObject.put(0, items);
			}
		}
		else if (mapObject.containsKey(targetDirectory.getId())) {
			mapObject.get(targetDirectory.getId()).addAll(toAdd);
		}
		else {
			List<AxisDirectoryItemWrapper> items = 
				new ArrayList<AxisDirectoryItemWrapper>();
			
			items.addAll(toAdd);
			
			directories.put(targetDirectory.getId(), targetDirectory);
			mapObject.put(targetDirectory.getId(), items);
		}
	}
	
	public void deleteTemporaryElement(AxisDirectoryItemWrapper toDelete) {
		List<AxisDirectoryItemWrapper> temps = 
			mapObject.get(toDelete.getDirectory().getId());
		
		List<AxisDirectoryItemWrapper> items = new ArrayList<AxisDirectoryItemWrapper>();
		
		if (temps == null) {
			return;
		}
		
		for (AxisDirectoryItemWrapper itm : temps) {
			if (itm.getAxisItem().getId() != toDelete.getAxisItem().getId())
				items.add(itm);
			else 
				;//fake delete
		}
		
		mapObject.put(toDelete.getDirectory().getId(), items);
	}
	
	public HashMap<Integer, List<AxisDirectoryItemWrapper>> getMapObject() {
		return mapObject;
	}
	
	public List<AxisDirectoryItemWrapper> getFlatItems() {
		List<AxisDirectoryItemWrapper> items = new ArrayList<AxisDirectoryItemWrapper>();
		
		for (List<AxisDirectoryItemWrapper> wraps : 
			mapObject.values()) {
			
			items.addAll(wraps);
		}
		
		return items;
	}
	
	public void clearMapObjects() {
		mapObject.clear();
	}
	
	public void loadDependencies() {
		
	}

	public void setGroupName(String group) {
		groupName = group;
		
	}

	public String getGroupName() {
		return groupName;
	}
}
