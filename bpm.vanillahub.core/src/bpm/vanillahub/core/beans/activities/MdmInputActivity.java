package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.MetaDispatch;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.workflow.commons.beans.TypeActivity;

public class MdmInputActivity extends ActivityWithResource<VanillaServer> {

	private static final long serialVersionUID = 1L;

	private boolean isFromMeta;
	
	private int contractId;
	private String contractName;
	private boolean validateData;
	
	private List<MetaDispatch> metaDispatch;
	
	public MdmInputActivity() {
		super();
	}

	public MdmInputActivity(String name) {
		super(TypeActivity.MDM_INPUT, name);
	}

	@Override
	public boolean isValid() {
		return !isFromMeta ? (getResourceId() > 0 && contractId > 0) : true;
	}
	
	public boolean isFromMeta() {
		return isFromMeta;
	}
	
	public void setFromMeta(boolean isFromMeta) {
		this.isFromMeta = isFromMeta;
	}
	
	public int getContractId() {
		return contractId;
	}
	
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	
	public String getContractName() {
		return contractName;
	}
	
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	public List<MetaDispatch> getMetaDispatch() {
		return metaDispatch;
	}
	
	public void setMetaDispatch(List<MetaDispatch> metaDispatch) {
		this.metaDispatch = metaDispatch;
	}
	
	public boolean validateData() {
		return validateData;
	}
	
	public void setValidateData(boolean validateData) {
		this.validateData = validateData;
	}
	
	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = new ArrayList<Variable>();
		if (metaDispatch != null) {
			for (MetaDispatch meta : metaDispatch) {
				variables.addAll(meta.getValue() != null ? meta.getValue().getVariables() : new ArrayList<Variable>());
			}
		}
		variables.addAll(super.getVariables(resources));
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		if (metaDispatch != null) {
			for (MetaDispatch meta : metaDispatch) {
				parameters.addAll(meta.getValue() != null ? meta.getValue().getParameters() : new ArrayList<Parameter>());
			}
		}
		parameters.addAll(super.getParameters(resources));
		return parameters;
	}
}
