package bpm.vanilla.repository.ui.viewers;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.icons.IconsRegistry;

public class RepositoryLabelProvider extends LabelProvider{

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		ImageRegistry reg = Activator.getDefault().getImageRegistry();
		if (element instanceof RepositoryItem){
			RepositoryItem it = (RepositoryItem)element;
			
			switch(it.getType()){
			case  IRepositoryApi.FWR_TYPE:
				return reg.get(IconsRegistry.FWR);
			case IRepositoryApi.CUST_TYPE:
				if (it.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE){
					return reg.get(IconsRegistry.BIRT);
				}
				else if (it.getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE){
					return reg.get(IconsRegistry.JASPER);
				}
			case IRepositoryApi.GTW_TYPE:
				return reg.get(IconsRegistry.GTW);
			case IRepositoryApi.FMDT_TYPE:
				return reg.get(IconsRegistry.FMDT);
			case IRepositoryApi.FD_DICO_TYPE:
				return reg.get(IconsRegistry.DICO);
			case IRepositoryApi.FD_TYPE:
				return reg.get(IconsRegistry.FD);
			case IRepositoryApi.FASD_TYPE:
				return reg.get(IconsRegistry.FASD);
			case IRepositoryApi.EXTERNAL_DOCUMENT:
				return reg.get(IconsRegistry.DOC);
			case IRepositoryApi.BIW_TYPE:
				return reg.get(IconsRegistry.BIW);
			case IRepositoryApi.DWH_VIEW_TYPE:
				return reg.get(IconsRegistry.DWH_VIEW);
			case IRepositoryApi.TASK_LIST:
				return reg.get(IconsRegistry.TASK_LIST);
			case IRepositoryApi.FAR_TYPE:
				return reg.get(IconsRegistry.FAR);
			
			case IRepositoryApi.DISCONNECTED_PCKG:
				return reg.get(IconsRegistry.DISCO_PACK);
			case IRepositoryApi.FAV_TYPE:
				return reg.get(IconsRegistry.FAV);
			case IRepositoryApi.GED_ENTRY:
				return reg.get(IconsRegistry.GED_ENTRY);
			case IRepositoryApi.GED_TYPE:
				return reg.get(IconsRegistry.GED_PENDING);
			case IRepositoryApi.MAP_TYPE:
				return reg.get(IconsRegistry.MAP_TYPE);
			case IRepositoryApi.URL:
				return reg.get(IconsRegistry.URL_TYPE);
			case IRepositoryApi.FMDT_DRILLER_TYPE:
				return reg.get(IconsRegistry.FMDT_DRILLER);
			case IRepositoryApi.PROJECTION_TYPE:
				return reg.get(IconsRegistry.PROJECTION);
			case IRepositoryApi.REPORTS_GROUP:
				return reg.get(IconsRegistry.REPORTS_GROUP);
			case IRepositoryApi.KPI_THEME:
				return reg.get(IconsRegistry.KPI_THEME);
//			case IRepositoryApi.WKB_TYPE:
//				return reg.get(IconsRegistry.WKB_TYPE);
//			case IRepositoryApi.DELTA_PCKG:
//				return reg.get(IconsRegistry.DELTA_PACK);
			case IRepositoryApi.R_MARKDOWN_TYPE:
				return reg.get(IconsRegistry.MARKDOWN);
			}

		}
		else if (element instanceof RepositoryDirectory){
			return reg.get(IconsRegistry.FOLDER);
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
		else if (element instanceof Repository){
			return ((Repository)element).getName();
		}
		return super.getText(element);
	}

}
