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


public class MetaDataContentProvider implements ITreeContentProvider{
	
	private HashMap<Integer, List<AxisDirectoryItemWrapper>> mapObject;
	
	public Object[] getChildren(Object parentElement) {
//		if (parentElement == null){
//			return new Object[0];
//		}
		if (parentElement instanceof RepositoryWrapper) {
			List<Object> list = new ArrayList<Object>();
			try {
				mapObject = ((RepositoryWrapper)parentElement).getMapObject();
				
				IRepositoryApi client = 
					((RepositoryWrapper)parentElement).getRepositoryClient();
				
				IRepository rep = new Repository(client);
				
				list.addAll(rep.getRootDirectories());
				
				//add from map object with key null (not associated with a directory)
				if (mapObject.get(0) != null) {
					OurLogger.info(Messages.MetaDataContentProvider_0 + mapObject.get(0).size() 
							+ Messages.MetaDataContentProvider_1);
					list.addAll(mapObject.get(0));
				}
				
			} catch (Exception e) {
				OurLogger.error(Messages.MetaDataContentProvider_2, e);
			}
			return list.toArray();
		}

		else if (parentElement instanceof RepositoryDirectory) {
			List<Object> list = new ArrayList<Object>();
			
			RepositoryDirectory dir = (RepositoryDirectory)parentElement;
			
			try {
				list.addAll(repository.getChildDirectories(dir));
				
				for (RepositoryItem itm : repository.getItems(dir)) {
					if (itm.getType() == IRepositoryApi.FMDT_TYPE)
						list.add(itm);
				}
				
			} catch (Exception e) {
				OurLogger.error(Messages.MetaDataContentProvider_3, e);
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
				return repository.getChildDirectories(((RepositoryDirectory)element)).size() + repository.getItems(((RepositoryDirectory)element)).size() > 0;
			} catch (Exception e) {
				OurLogger.error(Messages.MetaDataContentProvider_4, e);
			}
		}
		
		return false;
	}

	private IRepository repository;
	
	public Object[] getElements(Object inputElement) {
		if (inputElement == null){
			return new Object[0];
		}
		else if(inputElement instanceof RepositoryWrapper) {
			repository = new Repository(((RepositoryWrapper)inputElement).getRepositoryClient());
		}
		return getChildren(inputElement);
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

}
