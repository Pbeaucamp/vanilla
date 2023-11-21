package bpm.vanilla.repository.services.tools;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.workplace.api.datasource.extractor.BIGDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.BIRTDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.BIWDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FASDDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FAVDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FDDicoDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FMDTDatasourceExtractor;
import bpm.vanilla.workplace.api.datasource.extractor.FWRDatasourceExtractor;
import bpm.vanilla.workplace.core.datasource.AbstractDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceExtractor;

public class ImpactDatasourceExtractor {

	public static List<AbstractDatasource> extractDatasources(int type, Integer subtype, String xml, int itemId) throws Exception {
		
		IDatasourceExtractor extractor = null;
	
		switch (type) {
			case IRepositoryApi.FASD_TYPE:
				extractor = new FASDDatasourceExtractor();
				break;
			case IRepositoryApi.FD_TYPE:
				extractor = new FDDatasourceExtractor();
				break;
			case IRepositoryApi.FD_DICO_TYPE:
				extractor = new FDDicoDatasourceExtractor();
				break;
			case IRepositoryApi.FMDT_TYPE:
				extractor = new FMDTDatasourceExtractor();
				break;
			case IRepositoryApi.GTW_TYPE:
				extractor = new BIGDatasourceExtractor();
				break;
			case IRepositoryApi.GED_TYPE:
				break;
			case IRepositoryApi.BIW_TYPE:
				extractor = new BIWDatasourceExtractor();
				break;
			case IRepositoryApi.EXTERNAL_DOCUMENT:
				break;
			case IRepositoryApi.FAV_TYPE:
				extractor = new FAVDatasourceExtractor();
				break;
			case IRepositoryApi.FWR_TYPE:
				extractor = new FWRDatasourceExtractor();
				break;
			case IRepositoryApi.CUST_TYPE:
				if (subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE) {
					extractor = new BIRTDatasourceExtractor();
					break;
				}
		}
		
		if(extractor == null) {
			return new ArrayList<AbstractDatasource>();
		}
		
		 List<IDatasource> datasources = extractor.extractDatasources(xml);
		 
		 //Total bullshit...
		 List<AbstractDatasource> result = new ArrayList<AbstractDatasource>();
		 for(IDatasource ds : datasources) {
			 AbstractDatasource res = (AbstractDatasource) ds;
			 res.setItemId(itemId);
			 result.add(res);
		 }
		 
		 return result;
	}
	
}
