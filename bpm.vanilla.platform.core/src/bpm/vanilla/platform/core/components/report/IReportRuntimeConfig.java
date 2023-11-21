package bpm.vanilla.platform.core.components.report;

import java.util.Locale;

import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;
import bpm.vanilla.platform.core.components.IRuntimeConfig;

/**
 * a specific to Report RuntimeConfiguration
 * 
 *  - if no Locale is given, the report wont use one
 *  - if no AlternateDataSourceConfiguration is set, the default connections will be used
 *  - if a DataSource has no Connnection define, the default one will be used
 * 
 * 
 * @author ludo
 *
 */
public interface IReportRuntimeConfig extends IRuntimeConfig{

	/**
	 * the Locale to be used to run a report
	 * @return
	 */
	public Locale getLocale();
	
	/**
	 * the configuration to use for the runtime(if null 
	 * @return
	 */
	public AlternateDataSourceConfiguration getAlternateConnectionsConfiguration();
	
	/**
	 * the outputFormat too use for the report(PDF,HTML, ....)
	 * @return
	 */
	public String getOutputFormat();
	
	/**
	 * The max size of the data use for this run
	 * @return
	 */
	public Integer getMaxRowsPerQuery();
	
	/**
	 * If we display the comments
	 * 
	 * @return
	 */
	public boolean displayComments();
	
}
