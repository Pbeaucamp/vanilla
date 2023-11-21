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
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class MigrationContentProvider implements ITreeContentProvider{
	
	private HashMap<Integer, List<AxisDirectoryItemWrapper>> mapObject;
	
	public Object[] getChildren(Object parentElement) {
		List<Object> list = new ArrayList<Object>();
		if (parentElement instanceof RepositoryWrapper) {
			
			try {
				mapObject = ((RepositoryWrapper)parentElement).getMapObject();
				
				for (List<AxisDirectoryItemWrapper> wrapper : mapObject.values()) {
					list.addAll(wrapper);
				}
			} catch (Exception e) {
				OurLogger.error(Messages.MigrationContentProvider_0, e);
			}
			
		}
		else if (parentElement instanceof AxisDirectoryItemWrapper){
			list.addAll(((AxisDirectoryItemWrapper)parentElement).getDependencies());
		}

//		else if (parentElement instanceof AxisDirectory) {
//			List<Object> list = new ArrayList<Object>();
//			
//			AxisDirectory dir = (AxisDirectory)parentElement;
//			
//			try {
//				list.addAll(dir.getChilds());
//				list.addAll(dir.getItems());
//				
//				//add from map object with key directory
//				if (mapObject.get(dir.getId()) != null) {
//					OurLogger.info("Adding " + mapObject.get(dir.getId()).size() 
//							+ " for " + dir.getName());
//					
//					list.addAll(mapObject.get(dir.getId()));
//				}
//				
//			} catch (Exception e) {
//				OurLogger.error("Failed to load directory elements", e);
//			}
//			
//			return list.toArray();
//		}
		return list.toArray(new Object[list.size()]);
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
				return repository.getChildDirectories(((RepositoryDirectory)element)).size() + repository.getItems(((RepositoryDirectory)element)).size() > 0;
			} catch (Exception e) {
				OurLogger.error(Messages.MigrationContentProvider_1, e);
			}
		}
		else if (element instanceof AxisDirectoryItemWrapper){
			return !((AxisDirectoryItemWrapper)element).getDependencies().isEmpty();
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
