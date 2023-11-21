package bpm.es.gedmanager.views.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RepositoryContentProvider implements ITreeContentProvider {

	private IRepository repository;

	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof RepositoryDirectory) {
			List<RepositoryDirectory> l = new ArrayList<RepositoryDirectory>();
			try {
				l = repository.getChildDirectories(((RepositoryDirectory) parentElement));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (l != null) {
				return l.toArray(new Object[l.size()]);
			}
		}

		return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof RepositoryDirectory) {
			try {
				return repository.getDirectory(((RepositoryDirectory) element).getParentId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (element instanceof RepositoryItem) {
			try {
				return repository.getDirectory(((RepositoryItem) element).getDirectoryId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof RepositoryDirectory) {
			try {
				return repository.getChildDirectories(((RepositoryDirectory) element)).size() + repository.getItems(((RepositoryDirectory) element)).size() > 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof IRepository) {
			try {
				this.repository = (IRepository) inputElement;
				return ((IRepository) inputElement).getRootDirectories().toArray(new Object[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new Object[0];
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
