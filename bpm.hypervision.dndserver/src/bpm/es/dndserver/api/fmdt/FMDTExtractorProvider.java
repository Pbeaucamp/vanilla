package bpm.es.dndserver.api.fmdt;

import bpm.es.dndserver.api.fmdt.extractors.BirtFmdtExtractor;
import bpm.es.dndserver.api.fmdt.extractors.FdDictionaryExtractor;
import bpm.es.dndserver.api.fmdt.extractors.FwrExtractor;
import bpm.es.dndserver.api.fmdt.extractors.GtwExtractor;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FMDTExtractorProvider {

	public static IFMDTExtractor getExtractor(RepositoryItem item ) throws Exception{
		switch(item.getType()){
		case IRepositoryApi.FD_DICO_TYPE:
			
			return new FdDictionaryExtractor();
		case IRepositoryApi.FWR_TYPE:
			return new FwrExtractor();
		case IRepositoryApi.GTW_TYPE:
			return new GtwExtractor();
		case IRepositoryApi.CUST_TYPE:
			if (item.getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE){
				return new BirtFmdtExtractor();
			}
			break;
		}
		
		return null;
	}
}
