package bpm.vanilla.platform.core;

import bpm.vanilla.platform.core.repository.services.IDataProviderService;
import bpm.vanilla.platform.core.repository.services.IDocumentationService;
import bpm.vanilla.platform.core.repository.services.IMetaService;
import bpm.vanilla.platform.core.repository.services.IModelVersionningService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAdminService;
import bpm.vanilla.platform.core.repository.services.IRepositoryAlertService;
import bpm.vanilla.platform.core.repository.services.IRepositoryImpactService;
import bpm.vanilla.platform.core.repository.services.IRepositoryLogService;
import bpm.vanilla.platform.core.repository.services.IRepositoryReportHistoricService;
import bpm.vanilla.platform.core.repository.services.IRepositoryService;
import bpm.vanilla.platform.core.repository.services.IWatchListService;

public interface IRepositoryApi {
	public static final String SERVLET_REPOSITORY_SERVICE = "/repositoryService";
	public static final String SERVLET_REPOSITORY_ADMIN_SERVICE = "/repositoryAdmin";
	public static final String SERVLET_REPOSITORY_DOCUMENTATION_SERVICE = "/repositoryDocumentation";
	public static final String SERVLET_REPOSITORY_WATCHLIST_SERVICE = "/repositoryWatchlist";
	public static final String SERVLET_REPOSITORY_IMPACT_SERVICE = "/repositoryImpact";
	public static final String SERVLET_REPOSITORY_LOG_SERVICE = "/repositoryLog";
	public static final String SERVLET_REPOSITORY_DATASPROVIDER_SERVICE = "/repositoryDatasProvider";
	public static final String SERVLET_REPOSITORY_REPORT_HISTO_SERVICE = "/repositoryReportHisto";
	public static final String SERVLET_REPOSITORY_VERSIONING_SERVICE = "/repositoryVersioning";
	public static final String SERVLET_REPOSITORY_ALERTS_SERVICE = "/repositoryAlert";
	
	public static final String HTTP_HEADER_GROUP_ID = "bpm.vanilla.platform.core.repository.groupId";
	public static final String HTTP_HEADER_REPOSITORY_ID = "bpm.vanilla.platform.core.repository.repositoryId";
	public static final String HTTP_HEADER_TARGET_SERVLET = "bpm.vanilla.platform.core.repository.targetServlet";
	
	
	
	
	public static final int MODEL_TYPE_FD_VANILLA_FORM = 0;
	public static final String[] MODEL_TYPE_NAMES = new String[]{
		"bpm.fd.api.core.model.FdVanillaFormModel"
	};
	
	public static final String REPOSITORY_VERSION = "3";

	
	public static final int FASD_TYPE = 0;
	public static final int FD_TYPE = 1;
	public static final int FAR_TYPE = 2;
	public static final int FAV_TYPE = 3;
	public static final int FWR_TYPE = 4;
	public static final int GED_TYPE = 5;
	public static final int FMDT_TYPE = 6;
	public static final int WKB_TYPE = 7;
	public static final int CUST_TYPE = 8;
	public static final int FMUSER_TYPE = 9;
	public static final int FMADMIN_TYPE = 10;
	public static final int FMDESIGNER_TYPE = 11;
	public static final int FD_DICO_TYPE = 12;
	public static final int BIW_TYPE = 13;
	public static final int GTW_TYPE = 14;
	public static final int MAP_TYPE = 15;
	public static final int FMDT_DRILLER_TYPE = 16;
	public static final int EXTERNAL_DOCUMENT = 17;
	public static final int URL = 18;
	public static final int DISCONNECTED_PCKG = 19;
	public static final int TASK_LIST = 20;
	public static final int DELTA_PCKG = 21;
	public static final int GED_ENTRY = 22;
	public static final int DWH_VIEW_TYPE = 23;
	public static final int PROJECTION_TYPE = 24;
	public static final int FA_CUBE_TYPE = 25;
	public static final int REPORTS_GROUP = 26;
	public static final int KPI_THEME = 27;
	public static final int R_MARKDOWN_TYPE = 28;
	
	public static final int ALL_REPORT_TYPE = 29;
	//public static final int REXCEL_TYPE = 30;
	public static final int KPI_MAP = 30;
	public static final int FORM = 31;
	public static final int FMDT_CHART_TYPE = 32;
	
	public static final int XACTION_SUBTYPE = 0;
	public static final int JASPER_REPORT_SUBTYPE = 1;
	public static final int BIRT_REPORT_SUBTYPE = 2;
	public static final int ORBEON_XFORMS = 3;
	public static final int R_SUBTYPE = 5;
	public static final int MARKDOWN_SUBTYPE = 6;
	
	public static final String PORTAL = "PORTAL";
	public static final String FAWEB = "FAWEB";
	public static final String FWR = "FWR";
	public static final String FDWEB = "FDWEB";
	public static final String FMUSERWEB = "FMUSERWEB";
	public static final String FMLOADERWEB = "FMLOADERWEB";
	public static final String ES = "ES";
	public static final String NORPARENA = "NORPARENA";
	public static final String HYPERVISION = "HYPERVISION";
	public static final String DWH = "DWH";
	public static final String FASD = "FASD";
	public static final String FD = "FD";
	public static final String BIG = "GTW";
	public static final String BIW = "BIW";
	public static final String FMADMIN = "FMADMIN";
	public static final String FMDESIGNER = "FMDESIGNER";
	public static final String FMDT = "FMDT";
	public static final String MDM = "MDM";
	public static final String PROFILER = "PROFILER";
	public static final String VIEWER = "VIEWER";
	public static final String SMART_WEB = "SMARTWEB";
	public static final String HUB = "HUB";
	public static final String BIRT_VIEWER = "BIRTVIEWER";
	public static final String AKLABOX = "AKLABOX";
	public static final String AIR = "AIR";
	public static final String FMDT_WEB = "FMDTWEB";
	public static final String FMKPIMAP = "KPIMAP";
	public static final String UPDATE_MANAGER = "UPDATEMANAGER";
	public static final String ADMIN_WEB = "ADMINWEB";
	public static final String ARCHITECT_WEB = "ARCHITECTWEB";
	public static final String DATA_PREPARATION = "DATAPREPARATION";
	
	public static final String[] REPOSITORY_ITEM_TYPES_NAMES = new String[]{FASD, FD, "FAR", "FAV", FWR, "GED", FMDT, "WKB", "CUSTOM", "FMUSER", FMADMIN, FMDESIGNER, 
		"FDDICTIONARY", BIW, BIG, "MAP", "FMDT_DRILLER", "EXT_DOCUMENT", "URL", "DISCONNECTED_PCKG", "TASKLIST", "DELTA_PCKG", "GED_ENTRY", "DWH_VIEW", "PROJECTION",
		"CUBE", "REPORTS_GROUP", "KPI THEME", "R - MARKDOWN", "ALL REPORTS", "KPI MAP", "FORM"/*, "REXCEL"*/, "CHART"};
	
	public static final String[] TYPES_NAMES = new String[]{FASD, FD, "FAR", "FAV", FWR, "GED", FMDT, "WKB", "CUSTOM", "FMUSER", FMADMIN, FMDESIGNER, 
		"FDDICTIONARY", BIW, BIG, "MAP", "FMDT_DRILLER", "EXT_DOCUMENT", "URL", "DISCONNECTED_PCKG", "TASKLIST", "DELTA_PCKG", "GED_ENTRY", "DWH_VIEW", "PROJECTION",
		"CUBE", "REPORTS_GROUP", PORTAL, FAWEB, FDWEB, FMUSERWEB, FMLOADERWEB, ES, NORPARENA, HYPERVISION, DWH, MDM, PROFILER, AIR, "ALL REPORTS", "KPI MAP", "FORM", ARCHITECT_WEB, DATA_PREPARATION, HUB};

	public static final String[] SUBTYPES_NAMES = new String[]{"Xaction", "JRXML", "BIRT", "ORBEON", " KETTLE_KTR", "R", "MARKDOWN"};
	
	public static final String[] AVAILABLE_FORMATS = new String[]{"*.zip", "*.doc", "*.xls", "*.odt", "*.pdf", "*.html", "*.css",
		"*.properties", "*.bmp", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.tif", "*.xml", "*.txt", "*.csv", "*.rpttemplate", "*.js"};
	
	public static final String FORMAT_DOC = ".doc";
	public static final String FORMAT_XLS = ".xls";
	public static final String FORMAT_ODT = ".odt";
	public static final String FORMAT_PDF = ".pdf";
	public static final String FORMAT_HTML = ".html";
	public static final String FORMAT_CSS = ".css";
	public static final String FORMAT_PROPERTIES = ".properties";
	public static final String FORMAT_BMP = ".bmp";
	public static final String FORMAT_JPG = ".jpg";
	public static final String FORMAT_JPEG = ".jpeg";
	public static final String FORMAT_PNG = ".png";
	public static final String FORMAT_GIF = ".gif";
	public static final String FORMAT_TIF = ".tif";
	public static final String FORMAT_XML = ".xml";
	public static final String FORMAT_TXT = ".txt";
	public static final String FORMAT_CSV = ".csv";
	public static final String FORMAT_RPTTEMPLATE = ".rpttemplate";
	
	public static final int GED_FILE = 0;
	public static final int EXTERNAL_FILE = 1;
	public static final int LINKED_FILE = 2;
	
	
	public IRepositoryContext getContext();
	public IModelVersionningService getVersioningService();
	public IRepositoryReportHistoricService getReportHistoricService();
	public IDataProviderService getDatasProviderService();
	public IRepositoryLogService getRepositoryLogService();
	public IRepositoryAdminService getAdminService();
	public IDocumentationService getDocumentationService();
	public IRepositoryService getRepositoryService();
	public IWatchListService getWatchListService();
	public IRepositoryImpactService getImpactDetectionService();
	public IRepositoryAlertService getAlertService();
	public IMetaService getMetaService();
	public void test() throws Exception;
}
