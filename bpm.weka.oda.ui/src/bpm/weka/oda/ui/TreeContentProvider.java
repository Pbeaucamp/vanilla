package bpm.weka.oda.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class TreeContentProvider implements ITreeContentProvider {

	private Repository input;

	public Object[] getChildren(Object parentElement) {

		List l = new ArrayList<Object>();
		try {

			l.addAll(input.getChildDirectories((RepositoryDirectory) parentElement));
			l.addAll(input.getItems((RepositoryDirectory) parentElement));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return l.toArray(new Object[l.size()]);
	}

	public Object getParent(Object element) {
		int dirId = -1;
		if (element instanceof RepositoryDirectory) {

			dirId = ((RepositoryDirectory) element).getParentId();
		}
		else if (element instanceof RepositoryItem) {
			dirId = ((RepositoryItem) element).getDirectoryId();
		}
		try {
			return input.getDirectory(dirId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean hasChildren(Object element) {
		if (element instanceof RepositoryItem) {
			return false;
		}
		else {
			try {

				return !input.getDirectoryContent((RepositoryDirectory) element).isEmpty();
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

		}

	}

	public Object[] getElements(Object inputElement) {
		List l = new ArrayList();

		if (inputElement instanceof Repository) {
			input = (Repository) inputElement;

			try {
				l.addAll(input.getRootDirectories());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		else if (inputElement instanceof List) {
			l = (List) inputElement;
		}

		return l.toArray(new Object[l.size()]);
	}

	public void dispose() {

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

}
