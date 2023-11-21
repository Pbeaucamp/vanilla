package bpm.vanilla.platform.core.impl;

import java.util.List;

import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.parameters.VanillaGroupParameter;
import bpm.vanilla.platform.core.components.IRuntimeConfig;

public class RuntimeConfiguration implements IRuntimeConfig{
	private int groupId;
	private IObjectIdentifier identifier;
	private List<VanillaGroupParameter> parameters;
	
	public RuntimeConfiguration() {
	}
	
	public RuntimeConfiguration(int groupId, IObjectIdentifier identifier, List<VanillaGroupParameter> parameters){
		this.groupId = groupId;
		this.parameters = parameters;
		this.identifier = identifier;
	}

	@Override
	public IObjectIdentifier getObjectIdentifier() {
		return identifier;
	}
	@Override
	public List<VanillaGroupParameter> getParametersValues() {
		return parameters;
	}
	@Override
	public Integer getVanillaGroupId() {
		return groupId;
	}
	
	
}
