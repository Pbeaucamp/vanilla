package bpm.norparena.ui.menu.client.trees;

import bpm.norparena.ui.menu.icons.Icons;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class TreeHelperType {
	public static String getKeyForType(int i, RepositoryItem item){
		switch(i){
		case IRepositoryApi.CUST_TYPE:
			switch(item.getSubtype()){
				case IRepositoryApi.XACTION_SUBTYPE:
					return Icons.XACTION;
				case IRepositoryApi.JASPER_REPORT_SUBTYPE:
					if (!item.isOn()){
						return Icons.JASPER_STOP;
					}
					else{
						return Icons.JASPER_RUN;
					}
					
				case IRepositoryApi.BIRT_REPORT_SUBTYPE:
					if (!item.isOn()){
						return Icons.BIRT_STOP;
					}
					else{
						return Icons.BIRT_RUN;
					}
				case IRepositoryApi.ORBEON_XFORMS:
					return Icons.ORBEON;
					
			}
			
				
			
		case IRepositoryApi.FAR_TYPE:
			return "far"; //$NON-NLS-1$
		case IRepositoryApi.FASD_TYPE:
			if (!item.isOn()){
				return Icons.FASD_STOP;
			}
			else{
				return Icons.FASD_RUN;
			}
//			return "fasd";
		case IRepositoryApi.FAV_TYPE:
			return Icons.FAV;
		case IRepositoryApi.FD_TYPE:
			
			if (!item.isOn()){
				return Icons.FD_DEPLOYED_STOP;
			}
			else{
				return Icons.FD_DEPLOYED_RUN;
			}

		case IRepositoryApi.FMDT_TYPE:
			if (!item.isOn()){
				return Icons.FMDT_STOP;
			}
			else{
				return Icons.FMDT_RUN;
			}
//			return "fmdt";
		case IRepositoryApi.FWR_TYPE:
			if (!item.isOn()){
				return Icons.FWR_STOP;
			}
			else{
				return Icons.FWR_RUN;
			}
			
		case IRepositoryApi.GED_TYPE:
			return Icons.TEMP_GED;
		case IRepositoryApi.GED_ENTRY:
			return Icons.GED_ENTRY;
		case IRepositoryApi.WKB_TYPE:
			return "wkb"; //$NON-NLS-1$
		case IRepositoryApi.FD_DICO_TYPE:
			return Icons.DICO;
		case IRepositoryApi.BIW_TYPE:
			return Icons.BIW;
		case IRepositoryApi.MAP_TYPE:
			return Icons.MD;
		case IRepositoryApi.EXTERNAL_DOCUMENT:
			return Icons.DOC;
		case IRepositoryApi.URL:
			return Icons.LINK;
		case IRepositoryApi.DISCONNECTED_PCKG:
			return Icons.PACKAGE;
		case IRepositoryApi.GTW_TYPE:
			if (!item.isOn()){
				return Icons.GTW_STOP;
			}
			else{
				return Icons.GTW_RUN;
			}

		}
		return ""; //$NON-NLS-1$
	}
}
