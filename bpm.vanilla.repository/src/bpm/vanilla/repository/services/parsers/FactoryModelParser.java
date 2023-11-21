package bpm.vanilla.repository.services.parsers;

import bpm.vanilla.platform.core.IRepositoryApi;

public class FactoryModelParser {
	public static final int DWH_VIEW_TYPE = 23;
	
	public static IModelParser getModelParser(int repositoryObjectType, Integer repositoryObjectSubtype, String xmlModel) throws Exception{
		switch(repositoryObjectType){
		case IRepositoryApi.FD_DICO_TYPE:
			return new FdDictionaryParser(xmlModel);
		case IRepositoryApi.BIW_TYPE:
			return new BiwParser(xmlModel);
		case IRepositoryApi.CUST_TYPE:
			if (repositoryObjectSubtype == IRepositoryApi.BIRT_REPORT_SUBTYPE){
				return new BIRTParser(xmlModel);
			}
			else if (repositoryObjectSubtype == IRepositoryApi.JASPER_REPORT_SUBTYPE){
				return new JASPERParser(xmlModel);
			}
			else if (repositoryObjectSubtype == IRepositoryApi.XACTION_SUBTYPE){
				throw new Exception("Xactions are no more supported");
			}
			else if (repositoryObjectSubtype == IRepositoryApi.ORBEON_XFORMS){
				return new ORBEONParser(xmlModel);
			}
			break;
		case IRepositoryApi.DELTA_PCKG:
			return new DeltaParser(xmlModel);
		case IRepositoryApi.DISCONNECTED_PCKG:
			return new DisconnectedParser(xmlModel);
		case IRepositoryApi.EXTERNAL_DOCUMENT:
			return new ExternalDocumentParser(xmlModel);
		case IRepositoryApi.FASD_TYPE:
			return new FASDParser(xmlModel);
		case IRepositoryApi.FAV_TYPE:
			return new FAVParser(xmlModel);
		case IRepositoryApi.FD_TYPE:			
			return new FdParser(xmlModel);
			
		case IRepositoryApi.FMDT_DRILLER_TYPE:
			return new FmdtDrillerParser(xmlModel);
		case IRepositoryApi.FMDT_TYPE:
			return new FmdtParser(xmlModel);
		case IRepositoryApi.FWR_TYPE:
			return new FWRParser(xmlModel);
		
		case IRepositoryApi.GTW_TYPE:
			return new GTWParser(xmlModel);
		case IRepositoryApi.MAP_TYPE:
			return new MAPParser(xmlModel);
		case IRepositoryApi.FORM:
			return new FormParser(xmlModel);
		case IRepositoryApi.TASK_LIST:
			return new TaskListParser(xmlModel);
		case IRepositoryApi.WKB_TYPE:
			return new WKBParser(xmlModel);			
		case IRepositoryApi.URL:
			return new URLParser(xmlModel);
		case IRepositoryApi.GED_ENTRY:
			return new GEDParser(xmlModel);
		case IRepositoryApi.GED_TYPE:
			return new GEDParser(xmlModel);
		case DWH_VIEW_TYPE:
			return new DWHParser(xmlModel);
		case IRepositoryApi.PROJECTION_TYPE:
			return new ProjectionParser(xmlModel);
		case IRepositoryApi.REPORTS_GROUP:
			return new ReportsGroupParser(xmlModel);
		case IRepositoryApi.KPI_THEME:
			return new KpiThemeParser(xmlModel);
		case IRepositoryApi.R_MARKDOWN_TYPE:
			return new MarkdownTypeParser(xmlModel);
		case IRepositoryApi.KPI_MAP:
			return new KpiMapParser(xmlModel);
		}
		
		throw new Exception("Model Parser for type=" + repositoryObjectType + " subtype=" + repositoryObjectSubtype + " do not exists");
	}
}
