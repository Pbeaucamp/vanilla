package bpm.vanilla.platform.core.components;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IVanillaServerManager;
import bpm.vanilla.platform.core.beans.FileInformations;
import bpm.vanilla.platform.core.beans.User;
import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceHolder;
import bpm.vanilla.platform.core.components.report.IReportRuntimeConfig;
import bpm.vanilla.platform.core.xstream.IXmlActionType;


/**
 * Interface to use a Report Component.
 * Each interaction with a Reportengine should be lacunhe by an implementor of this interface
 * 
 * The getReportParameters and getReportParameterValues should not been used directly, instead of that
 * you should use the same version of those methods from the VanillaParameterComponent {@link VanillaParameterComponent}
 * 
 * @author ludo
 *
 */
public interface ReportingComponent extends IVanillaServerManager {
	
	public static final String REPORTING_SERVLET = "/reportingRuntime/reportingServlet";
	public static final String REPORTING_LOAD_EVALUATOR_SERVLET = "/reportingRuntime/loadEvaluator";
	
	public static enum ActionType implements IXmlActionType{
		PARAMETERS_DEFINITION(Level.DEBUG), PARAMETER_VALUES(Level.DEBUG), PARAMETERS_DEFINITION_FROM_MODEL(Level.DEBUG), PARAMETER_VALUES_FROM_MODEL(Level.DEBUG), 
		RUN(Level.INFO), RUN_FROM_DEFINITION(Level.DEBUG), RUN_ASYNCH(Level.INFO), LOAD_RESULT(Level.DEBUG), LIST_ALTERNATE_DATASOURCES(Level.DEBUG), 
		BUILD_RPT_DESIGN(Level.DEBUG), CHECK_RUN_ASYNCH_STATE(Level.DEBUG), GENERATE_DISCO_PACKAGE(Level.DEBUG), STOCK_REPORT_BACKGROUND(Level.DEBUG);
		
		private Level level;

		ActionType(Level level) {
			this.level = level;
		}
		
		@Override
		public Level getLevel() {
			return level;
		}
	}
	
	/**
	 * run a report from the config
	 * @param runtimeConfig
	 * @return the InputStream for the generated Report
	 * @throws Exception
	 */
	public InputStream runReport(IReportRuntimeConfig runtimeConfig, User user) throws Exception;
	
	/**
	 * run the report from the configuration except that the runtimeConfig.ObjectIdentifier
	 * wont be used to build the report Model, but instead the given InputStream will be used as the model
	 * 
	 * @param runtimeConfig
	 * @param reportModel
	 * @return
	 * @throws Exception
	 */
	public InputStream runReport(IReportRuntimeConfig runtimeConfig, InputStream reportModel, User user, boolean isDisco) throws Exception;

	/**
	 * launch a run for a report, but does not wait for the report to be generated.
	 * The returned identifier may be used to try to load the generated InputStream
	 * 
	 * @param runtimeConfig
	 * @return
	 * @throws Exception
	 */
	public RunIdentifier runReportAsynch(IReportRuntimeConfig runtimeConfig, User user) throws Exception;
	
	/**
	 * load an inputStream from a previously run report.
	 * If the report is not yet generated, it return false.
	 * If a failure happened during the runtime of the report, an Exception is thrown
	 * 
	 * @param runIdentifier
	 * @return
	 * @throws Exception
	 */
	public InputStream loadGeneratedReport(IRunIdentifier runIdentifier) throws Exception;
	
	/**
	 * Check the state of the load from a previously run report.
	 * If the report is not yet generated, it return false.
	 * 
	 * @param runIdentifier
	 * @return
	 * @throws Exception
	 */
	public boolean checkRunAsynchState(IRunIdentifier runIdentifier) throws Exception;
	
	
	/**
	 * To get the possible alternative connection for the DataSources used within the report 
	 * If no alternate Connection available, null is returned
	 * 
	 * TODO : connection might been secured by Groups.... should take care of it to avoid errors)
	 * @param runtimeConfig
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public AlternateDataSourceHolder getAlternateDataSourcesConnections(IReportRuntimeConfig runtimeConfig, User user) throws Exception;

	
	/**
	 * Build a RPTDesign from a FreeWebReport report from the configuration except that the runtimeConfig.ObjectIdentifier
	 * wont be used to build the report Model, but instead the given InputStream will be used as the model
	 * 
	 * @param runtimeConfig
	 * @param reportModel
	 * @return
	 * @throws Exception
	 */
	public InputStream buildRptDesignFromFWR(IReportRuntimeConfig runtimeConfig, InputStream reportModel) throws Exception;
	
	/**
	 * Build a disconnected package
	 * This method executes all the reports previously choose by the user for all parameters, formats and groups he choosed
	 * 
	 * @param objectIdentifier
	 * @param groupId
	 * @param user
	 * @return the generated package (a zip file which contains html and pdf files)
	 * @throws Exception
	 */
	public InputStream generateDiscoPackage(IObjectIdentifier objectIdentifier, int groupId, User user) throws Exception;


	/**
	 * Should be used on parameter that possible values depends from another parameter
	 * 
	 * @param user
	 * @param runtimeConfig
	 * @param parameterName
	 * @param dependanciesValues
	 * @return return the VanillaParameter with the given name and its possible Values defined
	 * @throws Exception
	 */
	public VanillaParameter getReportParameterValues(User user, IReportRuntimeConfig runtimeConfig, String parameterName, List<VanillaParameter> dependanciesValues) throws Exception;

	public VanillaParameter getReportParameterValues(User user, IReportRuntimeConfig runtimeConfig, String parameterName, List<VanillaParameter> dependanciesValues, InputStream model) throws Exception;

	/**
	 * 
	 * This method should be used to fill the Parameters Dialog Box when runnung
	 * a Report with some Parameters
	 * 
	 * @param user
	 * @param runtimeConfig
	 * @return Build parameters and their possible Values(if they can be havent any dependancies) from a RunConfig
	 * @throws Exception
	 */
	public List<VanillaGroupParameter> getReportParameters(User user, IReportRuntimeConfig runtimeConfig) throws Exception;
	
	public List<VanillaGroupParameter> getReportParameters(User user, IReportRuntimeConfig runtimeConfig, InputStream model) throws Exception;

	public FileInformations stockReportBackground(IRunIdentifier runIdentifier) throws Exception;
}
