package bpm.es.pack.manager.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.workplace.api.datasource.replacement.BIGDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIRTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIWDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FASDDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FAVDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FDDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FDDicoDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FMDTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FWRDatasourceReplacement;

public class RepositoryContentProvider implements ITreeContentProvider {

	private IRepository repository;

	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof RepositoryDirectory) {

			if (repository != null) {
				List<RepositoryDirectory> dirs = new ArrayList<RepositoryDirectory>();
				try {
					dirs = repository.getChildDirectories(((RepositoryDirectory) parentElement));
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<RepositoryItem> items = new ArrayList<RepositoryItem>();
				try {
					items = repository.getItems((RepositoryDirectory) parentElement);
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<RepositoryItem> supportedItems = new ArrayList<RepositoryItem>();
				for (RepositoryItem item : items) {
					if (isSupportedForPackage(item)) {
						supportedItems.add(item);
					}
				}

				List<Object> l = new ArrayList<Object>();
				l.addAll(dirs);
				l.addAll(supportedItems);
				return l.toArray(new Object[l.size()]);
			}
		}

		return new Object[0];
	}

	public Object getParent(Object element) {
		if (repository != null) {
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
		}
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof RepositoryDirectory) {

			if (repository != null) {
				try {
					return repository.getChildDirectories(((RepositoryDirectory) element)).size() + repository.getItems(((RepositoryDirectory) element)).size() > 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
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

	private boolean isSupportedForPackage(RepositoryItem item) {
		return item.getType() == IRepositoryApi.FASD_TYPE || item.getType() == IRepositoryApi.FD_TYPE || item.getType() == IRepositoryApi.FD_DICO_TYPE || item.getType() == IRepositoryApi.FMDT_TYPE || item.getType() == IRepositoryApi.GTW_TYPE || item.getType() == IRepositoryApi.GED_TYPE || item.getType() == IRepositoryApi.BIW_TYPE || item.getType() == IRepositoryApi.EXTERNAL_DOCUMENT || item.getType() == IRepositoryApi.FAV_TYPE || item.getType() == IRepositoryApi.FWR_TYPE || (item.getType() == IRepositoryApi.CUST_TYPE && item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE);
	}
}
