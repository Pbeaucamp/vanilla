package bpm.es.dndserver.tools;

import bpm.es.dndserver.api.repository.AxisDirectoryItemWrapper;
import bpm.vanilla.platform.core.IRepositoryApi;

public class RepositoryObjectTypeAuthorizer {

	public static boolean isSupported(AxisDirectoryItemWrapper wrap){
		
		
		switch(wrap.getAxisItem().getType()){
		case IRepositoryApi.DWH_VIEW_TYPE:
		case IRepositoryApi.BIW_TYPE:
		case IRepositoryApi.FAR_TYPE:
		case IRepositoryApi.FAV_TYPE:
		case IRepositoryApi.FD_TYPE:
		case IRepositoryApi.FD_DICO_TYPE:
		case IRepositoryApi.FWR_TYPE:
		case IRepositoryApi.GTW_TYPE:
		case IRepositoryApi.TASK_LIST:
		case IRepositoryApi.EXTERNAL_DOCUMENT:
		case IRepositoryApi.FASD_TYPE:
		case IRepositoryApi.GED_TYPE:
		case IRepositoryApi.GED_ENTRY:
		case IRepositoryApi.FMDT_TYPE:
		
			return true;
		case IRepositoryApi.CUST_TYPE:
			if (wrap.getAxisItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE){
				return true;
			}
		
		
	
			return false;

		case IRepositoryApi.DELTA_PCKG:
		case IRepositoryApi.DISCONNECTED_PCKG:
		
		case IRepositoryApi.MAP_TYPE:
		
			return false;
		}
		
		return false;
	}
}
