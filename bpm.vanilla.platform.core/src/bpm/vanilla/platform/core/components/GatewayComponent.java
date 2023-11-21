package bpm.vanilla.platform.core.components;

import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.components.gateway.GatewayModelGeneration4Fmdt;
import bpm.vanilla.platform.core.components.gateway.GatewayRuntimeState;
import bpm.vanilla.platform.core.components.gateway.IGatewayRuntimeConfig;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.xstream.IXmlActionType;


public interface GatewayComponent extends IVanillaServerManager {
	
	public static enum ActionType implements IXmlActionType{
		RUN_ASYNCH(Level.INFO), GET_STATE(Level.DEBUG), LIST_ALTERNATE_DATASOURCES(Level.DEBUG), GENERATE_FMDT_TRANSFO(Level.DEBUG), RUN_GATEWAY(Level.INFO);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	public static final String GATEWAY_XTSREAM_SERVLET = "/gatewa/xstreamServlet";
	
	/**
	 * this Servlet path is used to monitor what is happening on the run of a
	 * Gateway. It needs a parameter P_GATEWAY_RUN_IDENTIFIER that as the value
	 * of the Running Gateway id.
	 * 
	 * You may also or not the VanillaLogo to appear, if you do not want it
	 * the URL must contains the parameter P_GATEWAY_MONITOR_INCLUDE_TITLE with
	 * false as value.
	 */
	public static final String GATEWAY_JOB_MONITOR_SERVLET = "/gateway/monitor";
	
	public static final String GATEWAY_LOAD_EVALUATOR_SERVLET = "/gateway/loadEvaluator";
	/**
	 * the parameter used to identify a Gateway when calling the Gateway Monitoring Servlet
	 */
	public static final String P_GATEWAY_RUN_IDENTIFIER = "gatewayRunId";

	public static final String P_GATEWAY_MONITOR_INCLUDE_TITLE = "hasLogo";

	/**
	 * launch a run for a report, but does not wait for the report to be generated.
	 * The returned identifier may be used to try to load the generated InputStream
	 * 
	 * @param runtimeConfig
	 * @return
	 * @throws Exception
	 */
	public RunIdentifier runGatewayAsynch(IGatewayRuntimeConfig runtimeConfig, User user) throws Exception;
	
	/**
	 * launch a run for a gateway, and wait for it to end
	 * The returned identifier may be used to try to load the generated InputStream
	 * 
	 * @param runtimeConfig
	 * @return
	 * @throws Exception
	 */
	public GatewayRuntimeState runGateway(IGatewayRuntimeConfig runtimeConfig, User user) throws Exception;
	
	/**
	 * once runReportAsynch has been run, we can monitor the state of the generated run
	 * of the GatewayRunning instance  within the Gateway engine
	 * 
	 * might throw an Exception if the runIdentifier cannot be found
	 * 
	 * @param runIdentifier
	 * @return the state of the running Gateway matching to the given identifier
	 * @throws Exeption
	 */
	public GatewayRuntimeState getRunState(IRunIdentifier runIdentifier) throws Exception;
	
	
	/**
	 * To get the possible alternative connection for the DataSources used within the report 
	 * If no alternate Connection available, null is returned
	 * 
	 * TODO : connection might been secured by Groups.... should take care of it to avoid errors)
	 * @return
	 * @throws Exception
	 */
	public AlternateDataSourceHolder getAlternateDataSourcesConnections(IReportRuntimeConfig runtimeConfig) throws Exception;

	/**
	 * delegate to a Gateway Component the task to generate a Gateway Transformation Xml 
	 * to perform an extraction from a FMDT Packaged into some outputs.
	 * 
	 * The xml model is available from the DataHandler InputStream
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public byte[] generateFmdtExtractionTransformation(GatewayModelGeneration4Fmdt config, User user) throws Exception;
}
