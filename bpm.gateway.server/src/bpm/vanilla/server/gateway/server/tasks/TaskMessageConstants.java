package bpm.vanilla.server.gateway.server.tasks;

public interface TaskMessageConstants {
	public static final String RUN_GATEWAY_TASK = "bpm.vanilla.server.gateway.server.tasks.TaskRunGateway";
	public static final String GET_GATEWAY_STEPS_INFOS = "bpm.vanilla.server.gateway.server.stepsInfos";
	
	
	/*
	 * Steps Info properties
	 */
	final public static  String PROP_STEP_NAME = "stepName";
	final public static  String PROP_stepState = "stepState";
	final public static  String PROP_stepDuration = "stepDuration";
	final public static  String PROP_stepStartTime = "stepStartTime";
	final public static  String PROP_stepStopTime = "stepStopTime";
	final public static  String PROP_stepBufferedRows = "stepBufferedRows";
	final public static  String PROP_stepReadedRows = "stepReadedRows";
	final public static  String PROP_stepProcessedRows = "stepProcessedRows";
	final public static  String PROP_stepErrorsNumber = "stepErrorsNumber";
	final public static  String PROP_stepWarningsNumber = "stepWarningsNumber";
	final public static  String PROP_logs = "stepLogs";
}
