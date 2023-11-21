package bpm.es.dndserver.views.providers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.es.dndserver.Activator;
import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class MigrationLabelProvider extends LabelProvider{

	@Override
	public Image getImage(Object obj) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		ImageRegistry dndReg = bpm.es.dndserver.Activator.getDefault().getImageRegistry();
		
		if (obj instanceof RepositoryItem) {
			RepositoryItem it = (RepositoryItem)obj;
			String key = getKeyForType(it.getType(), it);
			return reg.get(key);
			
		}
		else if (obj instanceof RepositoryDirectory){
			return reg.get("folder"); //$NON-NLS-1$
		}
		else if (obj instanceof AxisDirectoryItemWrapper) {
			RepositoryItem it = ((AxisDirectoryItemWrapper) obj).getAxisItem();
			String key = getKeyForType(it.getType(), it);
			return reg.get(key);
			//return dndReg.get(IconsName.ADD);
		}
		
		return reg.get("default"); //$NON-NLS-1$
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
			return ((RepositoryItem)element).getItemName() ;
		}
		else if (element instanceof AxisDirectoryItemWrapper){
			return ((AxisDirectoryItemWrapper)element).getAxisItem().getItemName();
		}
		
		return super.getText(element);
	}

	private String getKeyForType(int i, RepositoryItem item){
		switch(i){
		case IRepositoryApi.CUST_TYPE:
			switch(item.getSubtype()){
				case IRepositoryApi.XACTION_SUBTYPE:
					return "xaction"; //$NON-NLS-1$
				case IRepositoryApi.JASPER_REPORT_SUBTYPE:
					if (!item.isOn()){
						return "jasper_stop"; //$NON-NLS-1$
					}
					else{
						return "jasper_run"; //$NON-NLS-1$
					}
					
				case IRepositoryApi.BIRT_REPORT_SUBTYPE:
					return "birt_run"; //$NON-NLS-1$
			}
			
				
			
		case IRepositoryApi.FAR_TYPE:
			return "far"; //$NON-NLS-1$
		case IRepositoryApi.FASD_TYPE:
			if (!item.isOn()){
				return "fasd_stop"; //$NON-NLS-1$
			}
			else{
				return "fasd_run"; //$NON-NLS-1$
			}
//			return "fasd";
		case IRepositoryApi.FAV_TYPE:
			return "fav"; //$NON-NLS-1$
		case IRepositoryApi.FD_TYPE:
			
			if (!item.isOn()){
				return "fd_deployed_stop"; //$NON-NLS-1$
			}
			else{
				return "fd_deployed_run"; //$NON-NLS-1$
			}

		case IRepositoryApi.FMDT_TYPE:
			if (!item.isOn()){
				return "fmdt_stop"; //$NON-NLS-1$
			}
			else{
				return "fmdt_run"; //$NON-NLS-1$
			}
//			return "fmdt";
		case IRepositoryApi.FWR_TYPE:
			if (!item.isOn()){
				return "fwr_stop"; //$NON-NLS-1$
			}
			else{
				return "fwr_run"; //$NON-NLS-1$
			}
			
		case IRepositoryApi.GED_TYPE:
			return "ged"; //$NON-NLS-1$
		case IRepositoryApi.WKB_TYPE:
			return "wkb"; //$NON-NLS-1$
		case IRepositoryApi.FD_DICO_TYPE:
			return "dico"; //$NON-NLS-1$
		case IRepositoryApi.BIW_TYPE:
			return "biw"; //$NON-NLS-1$
		case IRepositoryApi.MAP_TYPE:
			return "md"; //$NON-NLS-1$
		case IRepositoryApi.EXTERNAL_DOCUMENT:
			return "doc"; //$NON-NLS-1$
		case IRepositoryApi.URL:
			return "link"; //$NON-NLS-1$
		case IRepositoryApi.DISCONNECTED_PCKG:
			return "package"; //$NON-NLS-1$
		case IRepositoryApi.GTW_TYPE:
			if (!item.isOn()){
				return "gtw_stop"; //$NON-NLS-1$
			}
			else{
				return "gtw_run"; //$NON-NLS-1$
			}

		}
		return ""; //$NON-NLS-1$
	}
}
