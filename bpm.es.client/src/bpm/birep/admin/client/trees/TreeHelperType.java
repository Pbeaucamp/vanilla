package bpm.birep.admin.client.trees;

import adminbirep.icons.Icons;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class TreeHelperType {
	public static String getKeyForType(int i, RepositoryItem item){
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
					if (!item.isOn()){
						return "birt_stop"; //$NON-NLS-1$
					}
					else{
						return "birt_run"; //$NON-NLS-1$
					}
				case IRepositoryApi.ORBEON_XFORMS:
					return "orbeon_16"; //$NON-NLS-1$
					
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
			return "temp_ged"; //$NON-NLS-1$
		case IRepositoryApi.GED_ENTRY:
			return "ged_entry"; //$NON-NLS-1$
		case IRepositoryApi.WKB_TYPE:
			return "wkb"; //$NON-NLS-1$
		case IRepositoryApi.FD_DICO_TYPE:
			return "dico"; //$NON-NLS-1$
		case IRepositoryApi.BIW_TYPE:
			if (!item.isOn()){
				return Icons.BIW_STOP;
			}
			else{
				return Icons.BIW_RUN;
			}
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
		
		case IRepositoryApi.DWH_VIEW_TYPE:
			return "dwhview"; //$NON-NLS-1$
		case IRepositoryApi.TASK_LIST:
			return "tasklist"; //$NON-NLS-1$
		case IRepositoryApi.REPORTS_GROUP:
			return Icons.REPORTS_GROUP;
		case IRepositoryApi.KPI_THEME:
			return Icons.KPI_THEME;
		case IRepositoryApi.DELTA_PCKG:	

		}
		return ""; //$NON-NLS-1$
	}
}
