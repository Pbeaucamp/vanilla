package bpm.fd.repository.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;



public class RepositoryTreeContentProvider implements ITreeContentProvider{

	private Repository repository;
	private boolean includeItems = true;
	
	public RepositoryTreeContentProvider(boolean includeItems){
		this.includeItems = includeItems;
	}
	
	
	
	public Object[] getChildren(Object parentElement) {
		List<Object> l = new ArrayList<Object>();
		
		if (parentElement instanceof RepositoryDirectory){
			
			try{
				l.addAll(repository.getChildDirectories((RepositoryDirectory)parentElement));
				if (includeItems){
					l.addAll(repository.getItems((RepositoryDirectory)parentElement));
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			
			
		}
		
		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {
		
		try{
			if (element instanceof RepositoryDirectory){
				
				return repository.getDirectory(((RepositoryDirectory)element).getParentId());
			}
			else if (element instanceof RepositoryItem){
				return repository.getDirectory(((RepositoryItem)element).getDirectoryId());
			}
		}catch(Exception ex){
			
		}
		
		return null;
	}

	public boolean hasChildren(Object element) {
		List<Object> l = new ArrayList<Object>();
		
		if (element instanceof RepositoryDirectory){
			
			try{
				l.addAll(repository.getChildDirectories((RepositoryDirectory)element));
				if (includeItems){
					l.addAll(repository.getItems((RepositoryDirectory)element));
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			return l.size() > 0;
			
		}
		return false;
	}

	public Object[] getElements(Object inputElement) {
		
		repository = ((Repository)inputElement);
		List l = new ArrayList();
		try {
			l.addAll(repository.getRootDirectories());
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		
		return l.toArray(new Object[l.size()]);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}
	

}
