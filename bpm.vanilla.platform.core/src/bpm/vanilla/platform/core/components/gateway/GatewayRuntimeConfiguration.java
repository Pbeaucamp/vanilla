package bpm.vanilla.platform.core.components.gateway;

import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.beans.report.AlternateDataSourceConfiguration;

public class GatewayRuntimeConfiguration implements IGatewayRuntimeConfig{
	private AlternateDataSourceConfiguration alternateDataSourceConfiguration = new AlternateDataSourceConfiguration();
	private IObjectIdentifier objectIdentifier;
	private List<VanillaGroupParameter> parameters;
	private Integer vanillaGroupId;
	
	private boolean saveConfig;
	private String saveName;
	private String saveDescription;
	private String runtimeUrl;
	
	public GatewayRuntimeConfiguration(IObjectIdentifier objectIdentifier,
			List<VanillaGroupParameter> parameters, Integer vanillaGroupId) {
		super();
		this.objectIdentifier = objectIdentifier;
		this.parameters = parameters;
		this.vanillaGroupId = vanillaGroupId;
	}
	
	
	public GatewayRuntimeConfiguration() {
	
	}


	@Override
	public AlternateDataSourceConfiguration getAlternateDataSourceConfiguration() {
		return alternateDataSourceConfiguration;
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
	public void setAlternateDataSourceConfiguration(AlternateDataSourceConfiguration alternateDataSourceConfiguration) {
		this.alternateDataSourceConfiguration = alternateDataSourceConfiguration;
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


	public void setRuntimeUrl(String runtimeUrl) {
		this.runtimeUrl = runtimeUrl;
	}


	public String getRuntimeUrl() {
		return runtimeUrl;
	}
	
}
