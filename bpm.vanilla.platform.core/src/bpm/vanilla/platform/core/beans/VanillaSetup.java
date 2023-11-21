package bpm.vanilla.platform.core.beans;


public class VanillaSetup {

	private int id;
	private Integer portalLanguage = 0;
	private Boolean authentification = true;
	private String birtViewerPath;
	private String freeAnalysisServer;
	private String freeDashboardServer;
	private String freeWebReportServer;
	private String freeMetricsServer;
	private String fasdWebServer;
	private String birtViewer;
	private String workbook;
	private String securityServer;
	private String schedulerServer;
	private String vanillaServer;
	private String path;
	private String orbeonPath;
	private String orbeonUrl;
	private String gatewayServerUrl;
	private String fmdtServerUrl;
	private String reportingServerUrl;
	private String googleKey;
	private String reportEngine;
	private String vanillaFiles;
	private String vanillaRuntimeServersUrl;
	
	
	public  String getGatewayServerUrl() {
		return gatewayServerUrl;
	}
	public void setGatewayServerUrl(String gatewayServerUrl) {
		this.gatewayServerUrl = gatewayServerUrl;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getVanillaServer() {
		return vanillaServer;
	}
	public void setVanillaServer(String vanillaServer) {
		this.vanillaServer = vanillaServer;
	}
	public Boolean getAuthentification() {
		return authentification;
	}
	public void setAuthentification(Boolean authentification) {
		this.authentification = authentification;
	}
	public void setAuthentification(String authentification) {
		if ("false".equalsIgnoreCase(authentification)) {
			this.authentification = false;
		}
		else {
			this.authentification = true;
		}
	}
	
	
	public String getBirtViewerPath() {
		return birtViewerPath;
	}
	public void setBirtViewerPath(String birtViewerPath) {
		this.birtViewerPath = birtViewerPath;
	}
	public String getFreeAnalysisServer() {
		return freeAnalysisServer;
	}
	public void setFreeAnalysisServer(String freeAnalysisServer) {
		this.freeAnalysisServer = freeAnalysisServer;
	}
	public String getVanillaRuntimeServersUrl() {
		return vanillaRuntimeServersUrl;
	}
	public void setVanillaRuntimeServersUrl(String vanillaRuntimeServersUrl) {
		this.vanillaRuntimeServersUrl = vanillaRuntimeServersUrl;
	}
	
	public String getFreeWebReportServer() {
		return freeWebReportServer;
	}
	public void setFreeWebReportServer(String freeWebReportServer) {
		this.freeWebReportServer = freeWebReportServer;
	}
	public String getFreeMetricsServer() {
		return freeMetricsServer;
	}
	public void setFreeMetricsServer(String freeMetricsServer) {
		this.freeMetricsServer = freeMetricsServer;
	}
	public String getFasdWebServer() {
		return fasdWebServer;
	}
	public void setFasdWebServer(String fasdWebServer) {
		this.fasdWebServer = fasdWebServer;
	}
	public String getBirtViewer() {
		return birtViewer;
	}
	public void setBirtViewer(String birtViewer) {
		this.birtViewer = birtViewer;
	}
	public String getWorkbook() {
		return workbook;
	}
	public void setWorkbook(String workbook) {
		this.workbook = workbook;
	}
	public String getSchedulerServer() {
		return schedulerServer;
	}
	public void setSchedulerServer(String schedulerServer) {
		this.schedulerServer = schedulerServer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id){
		try {
			this.id = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getSecurityServer() {
		return securityServer;
	}
	public void setSecurityServer(String securityServer) {
		this.securityServer = securityServer;
	}
	public String getOrbeonPath() {
		return orbeonPath;
	}
	public void setOrbeonPath(String orbeonPath) {
		this.orbeonPath = orbeonPath;
	}
	public String getOrbeonUrl() {
		return orbeonUrl;
	}
	public void setOrbeonUrl(String orbeonUrl) {
		this.orbeonUrl = orbeonUrl;
	}
	public  String getFmdtServerUrl() {
		return fmdtServerUrl;
	}
	public void setFmdtServerUrl(String fmdtServerUrl) {
		this.fmdtServerUrl = fmdtServerUrl;
	}
	public String getReportingServerUrl() {
		return reportingServerUrl;
	}
	public void setReportingServerUrl(String reportingServerUrl) {
		this.reportingServerUrl = reportingServerUrl;
	}
	public String getGoogleKey() {
		return googleKey;
	}
	public void setGoogleKey(String googleKey) {
		this.googleKey = googleKey;
	}
	
	
	public String getReportEngine() {
		return reportEngine;
	}
	public void setReportEngine(String reportEngine) {
		this.reportEngine = reportEngine;
	}
	public String getVanillaFiles() {
		return vanillaFiles;
	}
	public void setVanillaFiles(String vanillaFiles) {
		this.vanillaFiles = vanillaFiles;
	}
	public Integer getPortalLanguage() {
		return portalLanguage;
	}
	public void setPortalLanguage(Integer portalLanguage) {
		this.portalLanguage = portalLanguage;
	}
	public void setPortalLanguage(String portalLanguage) {
		this.portalLanguage = new Integer(portalLanguage);
	}
	public String getFreeDashboardServer() {
		return freeDashboardServer;
	}
	public void setFreeDashboardServer(String freeDashboardServer) {
		this.freeDashboardServer = freeDashboardServer;
	}
	
	public String getXml(){
		StringBuffer buf = new StringBuffer();
		
		buf.append("    <setup>\n");
		buf.append("         <id>" +  this.id + "</id>\n");
		if (portalLanguage != null)
			buf.append("         <language>" +  this.portalLanguage + "</language>\n");
		
		buf.append("         <authentification>" + this.authentification + "</authentification>\n");
		buf.append("         <birtViewerPath>" + this.birtViewerPath + "</birtViewerPath>\n");
		buf.append("         <birtViewer>" + this.birtViewer + "</birtViewer>\n");
		buf.append("         <fasdWebServer>" + this.fasdWebServer + "</fasdWebServer>\n");
		buf.append("         <freeAnalysisServer>" + this.freeAnalysisServer + "</freeAnalysisServer>\n");
		buf.append("         <freeDashboardServer>" + this.freeDashboardServer + "</freeDashboardServer>\n");
		buf.append("         <freeMetricsServer>" + this.freeMetricsServer + "</freeMetricsServer>\n");
		buf.append("         <freeWebReportServer>" + this.freeWebReportServer + "</freeWebReportServer>\n");
		buf.append("         <securityServer>" + this.securityServer + "</securityServer>\n");
		buf.append("         <workbook>" + this.workbook + "</workbook>\n");
		buf.append("         <vanillaServer>" + this.vanillaServer + "</vanillaServer>\n");
		buf.append("         <path>" + this.path + "</path>\n");
		buf.append("         <orbeonpath>" + this.orbeonPath + "</orbeonpath>\n");
		buf.append("         <orbeonurl>" + this.orbeonUrl + "</orbeonurl>\n");
		buf.append("         <schedulerServerUrl>" + this.schedulerServer + "</schedulerServerUrl>\n");
		buf.append("         <gatewayServerUrl>" + this.gatewayServerUrl + "</gatewayServerUrl>\n");
		buf.append("         <fmdtServerUrl>" + this.fmdtServerUrl + "</fmdtServerUrl>\n");
		buf.append("         <reportingServerUrl>" + this.reportingServerUrl + "</reportingServerUrl>\n");
		buf.append("         <googleKey>" + this.googleKey + "</googleKey>\n");
		buf.append("         <reportengine>" + this.reportEngine + "</reportengine>\n");
		buf.append("         <vanillafiles>" + this.vanillaFiles + "</vanillafiles>\n");
		buf.append("         <vanillaruntimeserverurl>" + this.vanillaRuntimeServersUrl + "</vanillaruntimeserverurl>\n");
		buf.append("    </setup>\n");
		
		return buf.toString();
	}


	
}
