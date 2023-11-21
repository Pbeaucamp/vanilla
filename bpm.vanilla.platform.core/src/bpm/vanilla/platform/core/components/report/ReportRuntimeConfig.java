package bpm.vanilla.platform.core.components.report;

import java.util.List;
import java.util.Locale;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;

public class ReportRuntimeConfig implements IReportRuntimeConfig{

	private AlternateDataSourceConfiguration alternateDataSourceConfiguration;
	private Locale locale;
	private String outputFormat;
	private IObjectIdentifier objectIdentifier;
	private List<VanillaGroupParameter> parameters;
	private Integer vanillaGroupId;
	private Integer maxRowsPerQuery;
	
	private boolean saveConfig;
	private String saveName;
	private String saveDescription;
	private String runtimeUrl;
	
	private boolean displayComments;
	
	public ReportRuntimeConfig(){}
	
	
	public ReportRuntimeConfig(IObjectIdentifier objectIdentifier,
			List<VanillaGroupParameter> parameters, Integer vanillaGroupId) {
		super();
		this.objectIdentifier = objectIdentifier;
		this.parameters = parameters;
		this.vanillaGroupId = vanillaGroupId;
	}


	@Override
	public AlternateDataSourceConfiguration getAlternateConnectionsConfiguration() {
		return alternateDataSourceConfiguration;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public String getOutputFormat() {
		return outputFormat;
	}

	@Override
	public IObjectIdentifier getObjectIdentifier() {
		return objectIdentifier;
	}

	@Override
	public List<VanillaGroupParameter> getParametersValues() {
		return parameters;
	}

	@Override
	public Integer getVanillaGroupId() {
		return vanillaGroupId;
	}

	/**
	 * @param alternateDataSourceConfiguration the alternateDataSourceConfiguration to set
	 */
	public void setAlternateDataSourceConfiguration(
			AlternateDataSourceConfiguration alternateDataSourceConfiguration) {
		this.alternateDataSourceConfiguration = alternateDataSourceConfiguration;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * @param outputFormat the outputFormat to set
	 */
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	/**
	 * @param objectIdentifier the objectIdentifier to set
	 */
	public void setObjectIdentifier(IObjectIdentifier objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(List<VanillaGroupParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * @param vanillaGroupId the vanillaGroupId to set
	 */
	public void setVanillaGroupId(Integer vanillaGroupId) {
		this.vanillaGroupId = vanillaGroupId;
	}

	public boolean isSaveConfig() {
		return saveConfig;
	}


	public void setSaveConfig(boolean saveConfig) {
		this.saveConfig = saveConfig;
	}


	public String getSaveName() {
		return saveName;
	}


	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}


	public String getSaveDescription() {
		return saveDescription;
	}


	public void setSaveDescription(String saveDescription) {
		this.saveDescription = saveDescription;
	}


	/**
	 * Used to run the report on a selected cluster
	 * @param runtimeUrl
	 */
	public void setRuntimeUrl(String runtimeUrl) {
		this.runtimeUrl = runtimeUrl;
	}


	public String getRuntimeUrl() {
		return runtimeUrl;
	}


	public void setMaxRowsPerQuery(Integer maxRowsPerQuery) {
		this.maxRowsPerQuery = maxRowsPerQuery;
	}


	public Integer getMaxRowsPerQuery() {
		return maxRowsPerQuery;
	}
	
	public void setDisplayComments(boolean displayComments) {
		this.displayComments = displayComments;
	}
	
	public boolean displayComments() {
		return displayComments;
	}

}
