package bpm.es.dndserver.views.providers;

import java.util.ArrayList;
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


public class DependentContentProvider implements ITreeContentProvider{
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AxisDirectoryItemWrapper) {
			List<AxisDirectoryItemWrapper> list = new ArrayList<AxisDirectoryItemWrapper>();
			try {
				list = ((AxisDirectoryItemWrapper)parentElement).getDependencies();
				
			} catch (Exception e) {
				OurLogger.error(Messages.DependentContentProvider_0, e);
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

		if (element instanceof AxisDirectoryItemWrapper){
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
