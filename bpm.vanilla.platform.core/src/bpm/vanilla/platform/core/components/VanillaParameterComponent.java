package bpm.vanilla.platform.core.components;

import java.io.InputStream;
import java.util.List;

import bpm.vanilla.platform.core.beans.VanillaLogs.Level;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.parameters.VanillaParameter;
import bpm.vanilla.platform.core.xstream.IXmlActionType;

/**
 * This component is used to provide Parameters informations to run an object.
 * When an object is launched, using this component will allow to gather all
 * information (and interact in case of cascading parameters) to generate a useable 
 * UI with standards widgets(combo, list,.....)
 * 
 * 
 * The parameters information are gathered from the DataProviderService or
 * in case of a BIRT model from the ReportingComponent defined within the BIRT model.
 * 
 * If no definition is available from the BIRT model, or the DataProviderService,
 * default Parameters (as text fields) must be sent.
 * 
 * 
 * @author ludo
 *
 */
public interface VanillaParameterComponent {
	public static enum ActionType implements IXmlActionType{
		PARAMETERS_DEFINITION(Level.DEBUG), PARAMETER_VALUES(Level.DEBUG), PARAMETERS_DEFINITION_FROM_MODEL(Level.DEBUG), PARAMETER_VALUES_FROM_MODEL(Level.DEBUG);
		
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
	 * This method should be used to fill the Parameters Dialog Box when running an item
	 * 
	 * If the item is a Report, the config must be a IReportRuntimeConfig, otherwise
	 * the Parameters will be provided by the DataProvider definitions instead of the
	 * reportEngine using the datas stored in the XML model
	 * 
	 * 
	 * @param runtimeConfig
	 * @return Build parameters and their possible Values(if they can be havent any dependancies) from a RunConfig
	 * @throws Exception
	 */
	public List<VanillaGroupParameter> getParameters(IRuntimeConfig config) throws Exception;

	public List<VanillaGroupParameter> getParameters(IRuntimeConfig config, InputStream model) throws Exception;
	
	/**
	 * Use the runtimeConfig to return the parameterName values.
	 * 
	 * If the item is a Report, the config must be a IReportRuntimeConfig, otherwise
	 * the Parameters will be provided by the DataProvider definitions instead of the
	 * reportEngine using the datas stored in the XML model
	 * 
	 * @param runtimeConfig
	 * @param parameterName
	 * @param dependanciesValues
	 * @return return the VanillaParameter with the given name and its possible Values defined
	 * @throws Exception
	 */
	public VanillaParameter getReportParameterValues(IRuntimeConfig runtimeConfig, String parameterName) throws Exception;

	public VanillaParameter getReportParameterValues(IRuntimeConfig runtimeConfig, String parameterName, InputStream model) throws Exception;

}
