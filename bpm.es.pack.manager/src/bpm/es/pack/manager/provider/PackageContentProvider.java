package bpm.es.pack.manager.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.VanillaPackage;

public class PackageContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof PlaceImportDirectory) {

			PlaceImportDirectory dir = (PlaceImportDirectory) parentElement;
			if ((dir.getChildsDir() != null && !dir.getChildsDir().isEmpty()) || (dir.getChildsItems() != null && !dir.getChildsItems().isEmpty())) {

				List<Object> l = new ArrayList<Object>();
				if (dir.getChildsDir() != null) {
					l.addAll(dir.getChildsDir());
				}
				if (dir.getChildsItems() != null) {
					l.addAll(dir.getChildsItems());
				}
				return l.toArray(new Object[l.size()]);
			}
		}

		return new Object[0];
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof PlaceImportDirectory) {
			PlaceImportDirectory dir = (PlaceImportDirectory) element;
			if ((dir.getChildsDir() != null && !dir.getChildsDir().isEmpty()) || (dir.getChildsItems() != null && !dir.getChildsItems().isEmpty())) {
				return true;
			}
		}

		return false;
	}

	public Object[] getElements(Object inputElement) {

		if (inputElement instanceof VanillaPackage) {
			VanillaPackage vanillaPack = (VanillaPackage) inputElement;

			List<Object> items = new ArrayList<Object>();
			if (vanillaPack.getDirectories() != null) {
				items.addAll(vanillaPack.getDirectories());
			}
			if (vanillaPack.getItems() != null) {
				items.addAll(vanillaPack.getItems());
			}
			return items.toArray(new Object[items.size()]);
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
