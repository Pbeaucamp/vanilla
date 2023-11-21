package bpm.smart.core.model;

public class Constants {
	
	public static final String SERVLET_ADMIN_MANAGER = "/airAdminServlet";
	public static final String SERVLET_EXTERNAL = "/airExternalServlet";
	
	public static final String HTTP_HEADER_SESSION_ID = "bpm.vanillaair.sessionUUID";
	public static final String HTTP_HEADER_LOCALE = "bpm.vanillaair.locale";
	
	public static final String SID = "SID";
	public static final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks in this example.
}
