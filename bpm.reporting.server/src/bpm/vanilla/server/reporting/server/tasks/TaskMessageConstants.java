package bpm.vanilla.server.reporting.server.tasks;

public interface TaskMessageConstants {
	public static final String GENERATE_REPORT_TASK = "bpm.vanilla.server.reporting.server.tasks.TaskGenerateFwrReport";
	
	public static final String GENERATE_REPORT_FROM_MODEL_TASK = "bpm.vanilla.server.reporting.server.GenerateReportFromModelCommand";
	
	//BIRT Message Constant
	public static final String BIRT_VIEWER_COMMAND = "bpm.vanilla.server.reporting.server.BirtViewerCommand";
	
	public static final String BIRT_MESSAGE_SUBTYPE = "bpm.vanilla.server.reporting.server.BirtViewerCommand.SubType";
	public static final String BIRT_GET_PAREMETERS = "getParameters";
	public static final String GET_PARAMETERS_FOR_CASCADING_GROUP = "getParametersForCascadingGroup";
	
	public static final String BIRT_RUN_REPORT_HTML = "runReportHtml";
	public static final String BIRT_RUN_REPORT_PDF = "runReportPdf";
	public static final String BIRT_RUN_REPORT_XLS = "runReportXls";
	public static final String BIRT_RUN_REPORT_PPT = "runReportPpt";
	public static final String BIRT_RUN_REPORT_POSCRIPT = "runReportPoscript";
	public static final String BIRT_RUN_REPORT_DOC = "runReportDoc";
	public static final String BIRT_RUN_REPORT_ODS = "runReportOds";
	public static final String BIRT_RUN_REPORT_ODT = "runReportOdt";
	
	public static final String BIRT_PARAMETER_NAME = "birtParameterName";
	public static final String BIRT_PARAMETERS = "birtParameters";
	public static final String BIRT_LOCALE = "birtLocale";
	
	/*type*/
	public final static String VIEWER_TYPE = "vanilla.viewer.type";
	/*possible value*/
	public final static String VIEWER_TYPE_FWR = "vanilla.viewer.type.fwr";
	public final static String VIEWER_TYPE_BIRT = "vanilla.viewer.type.birt";
	public final static String VIEWER_TYPE_JASPER = "vanilla.viewer.type.jasper";
	
	public final static String SAVE_FWR_AS_BIRT = "bpm.vanilla.server.reporting.server.SaveFwrAsBirt";
}
