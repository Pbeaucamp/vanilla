package bpm.es.dndserver.views.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.es.dndserver.Messages;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.es.dndserver.api.repository.RepositoryWrapper;
import bpm.es.dndserver.tools.OurLogger;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class RepositoryContentProvider implements ITreeContentProvider{
	
	private HashMap<Integer, List<AxisDirectoryItemWrapper>> mapObject;
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof RepositoryWrapper) {
			List<Object> list = new ArrayList<Object>();
			try {
				mapObject = ((RepositoryWrapper)parentElement).getMapObject();
				
				IRepositoryApi client = 
					((RepositoryWrapper)parentElement).getRepositoryClient();
				
				bpm.vanilla.platform.core.repository.IRepository repo = new Repository(client);
				
				list.addAll(repo.getRootDirectories());
				
				//add from map object with key null (not associated with a directory)
				if (mapObject.get(0) != null) {
					OurLogger.info(Messages.RepositoryContentProvider_0 + mapObject.get(0).size() 
							+ Messages.RepositoryContentProvider_1);
					list.addAll(mapObject.get(0));
				}
				
			} catch (Exception e) {
				OurLogger.error(Messages.RepositoryContentProvider_2, e);
			}
			return list.toArray();
		}

		else if (parentElement instanceof RepositoryDirectory) {
			List<Object> list = new ArrayList<Object>();
			
			RepositoryDirectory dir = (RepositoryDirectory)parentElement;
			
			try {
				list.addAll(repository.getChildDirectories(dir));
				list.addAll(repository.getItems(dir));
				
				//add from map object with key directory
				if (mapObject.get(dir.getId()) != null) {
					OurLogger.info(Messages.RepositoryContentProvider_3 + mapObject.get(dir.getId()).size() 
							+ Messages.RepositoryContentProvider_4 + dir.getName());
					
					list.addAll(mapObject.get(dir.getId()));
				}
				
			} catch (Exception e) {
				OurLogger.error(Messages.RepositoryContentProvider_5, e);
			}
			
			return list.toArray();
		}
		else
			return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof RepositoryDirectory){
			try {
				return repository.getDirectory(((RepositoryDirectory)element).getParentId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (element instanceof RepositoryItem){
			try {
				return repository.getDirectory(((RepositoryItem)element).getDirectoryId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasChildren(Object element) {

		if (element instanceof RepositoryDirectory){
			//return true;
			try {
				boolean hasChildren= repository.getChildDirectories(((RepositoryDirectory)element)).size() + repository.getItems(((RepositoryDirectory)element)).size() > 0;
				boolean hasMapping = mapObject.get(((RepositoryDirectory)element).getId()) != null;
				
				return hasChildren || hasMapping;
			} catch (Exception e) {
				OurLogger.error(Messages.RepositoryContentProvider_6, e);
			}
		}
		
		return false;
	}

	private IRepository repository;
	
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof RepositoryWrapper) {
			repository = new Repository(((RepositoryWrapper)inputElement).getRepositoryClient());
		}
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

}
