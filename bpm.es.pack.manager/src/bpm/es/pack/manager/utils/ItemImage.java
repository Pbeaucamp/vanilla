package bpm.es.pack.manager.utils;

import adminbirep.icons.Icons;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ItemImage {

	public static String getKeyForType(int i, RepositoryItem item) {
		switch (i) {
			case IRepositoryApi.CUST_TYPE:
				return Icons.BIRT_RUN;
			case IRepositoryApi.FAR_TYPE:
				return "far"; //$NON-NLS-1$
			case IRepositoryApi.FASD_TYPE:
				return Icons.FASD;
			case IRepositoryApi.FAV_TYPE:
				return Icons.FAV;
			case IRepositoryApi.FD_TYPE:
				if (item != null && !item.isOn()) {
					return Icons.FD_DEPLOYED_STOP;
				}
				else {
					return Icons.FD_DEPLOYED_RUN;
				}

			case IRepositoryApi.FMDT_TYPE:
				if (item != null && !item.isOn()) {
					return Icons.FMDT_STOP;
				}
				else {
					return Icons.FMDT_RUN;
				}
			case IRepositoryApi.FWR_TYPE:
				if (item != null && !item.isOn()) {
					return Icons.FWR_STOP;
				}
				else {
					return Icons.FWR_RUN;
				}
			case IRepositoryApi.GED_TYPE:
				return Icons.TEMP_GED;
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
				return Icons.GTW;

		}
		return ""; //$NON-NLS-1$
	}
}
