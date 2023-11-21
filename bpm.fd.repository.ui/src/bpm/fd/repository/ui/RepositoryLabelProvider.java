package bpm.fd.repository.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.fd.repository.ui.icons.Icons;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class RepositoryLabelProvider extends LabelProvider{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if (element instanceof RepositoryDirectory){
			return Activator.getDefault().getImageRegistry().get(Icons.folder);
		}
		else if (element instanceof RepositoryItem){
			if (((RepositoryItem)element).getType() == IRepositoryApi.FD_TYPE){
				return bpm.fd.design.ui.Activator.getDefault().getImageRegistry().get(bpm.fd.design.ui.icons.Icons.fd_16);
			}
			if (((RepositoryItem)element).getType() == IRepositoryApi.FWR_TYPE || ((RepositoryItem)element).getType() == IRepositoryApi.CUST_TYPE){
				return bpm.fd.design.ui.Activator.getDefault().getImageRegistry().get(bpm.fd.design.ui.icons.Icons.report);
			}
			
			
		}
		return super.getImage(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof RepositoryDirectory){
			return ((RepositoryDirectory)element).getName();
		}
		else if (element instanceof RepositoryItem){
			return ((RepositoryItem)element).getItemName();
		}
		return super.getText(element);
	}

}
