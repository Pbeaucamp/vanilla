package bpm.vanilla.repository.ui.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;
/**
 * This class is a TreeContentProvider used for a TreeViewer wich input is an object Repository
 * @author ludo
 *
 */
public class RepositoryContentProvider implements ITreeContentProvider{
	private Repository input;
	
	public Object[] getChildren(Object parentElement) {
		
		List l = new ArrayList<Object>();
		try{
			
			l.addAll(input.getChildDirectories((RepositoryDirectory)parentElement));
			l.addAll(input.getItems((RepositoryDirectory)parentElement));
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.RepositoryContentProvider_0, Messages.RepositoryContentProvider_1 + ((RepositoryDirectory)parentElement).getName() + ":" + ex.getMessage()); //$NON-NLS-3$
		}
		
		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {
		int dirId = -1;
		if (element instanceof RepositoryDirectory){
			
			dirId = ((RepositoryDirectory)element).getParentId();
		}
		else if(element instanceof RepositoryItem){
			dirId = ((RepositoryItem)element).getDirectoryId();
		}
		try {
			return input.getDirectory(dirId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean hasChildren(Object element) {
		if (element instanceof RepositoryItem){
			return false;
		}
		else{
			try{
				
				return !input.getDirectoryContent((RepositoryDirectory)element).isEmpty();
			}catch(Exception ex){
				ex.printStackTrace();
				return false;
			}
			
		}
		
	}

	public Object[] getElements(Object inputElement) {
		List l = new ArrayList();
		
		if (inputElement instanceof Repository){
			input = (Repository)inputElement;
			
			try {
				l.addAll(input.getRootDirectories());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		else if (inputElement instanceof List){
			l =(List)inputElement;
		}
		
		return l.toArray(new Object[l.size()]);
	}

	public void dispose() {
		
		
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		
	}

}
