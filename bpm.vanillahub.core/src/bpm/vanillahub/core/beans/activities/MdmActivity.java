package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.activities.attributes.MetaDispatch;
import bpm.vanillahub.core.beans.resources.Source;
import bpm.vanillahub.core.beans.resources.VanillaServer;
import bpm.workflow.commons.beans.TypeActivity;

public class MdmActivity extends ActivityWithResource<VanillaServer> {

	private static final long serialVersionUID = 1L;

	private boolean isFromMeta;
	
	private int contractId;
	private String contractName;
	private boolean validateData;
	
	private List<MetaDispatch> metaDispatch;

	private Source manageFileSource;
	
	public MdmActivity() {
		super();
	}

	public MdmActivity(String name) {
		super(TypeActivity.MDM, name);
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

	public void setManageFileSource(Source manageFileSource) {
		this.manageFileSource = manageFileSource;
	}
	
	public int getManageFileSourceId() {
		return manageFileSource != null ? manageFileSource.getId() : -1;
	}

	public Source getManageFileSource(List<Source> resources) {
		return (Source) (manageFileSource != null ? findResource(manageFileSource, resources) : null);
	}
		
	private Resource findResource(Source resource, List<Source> resources) {
		if (resources != null) {
			for(Resource currentResource : resources) {
				if (resource.getId() == currentResource.getId()) {
					return currentResource;
				}
			}
		}
		return this.manageFileSource;
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
		if (manageFileSource != null) {
			variables.addAll(manageFileSource.getVariables());
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
		if (manageFileSource != null) {
			parameters.addAll(manageFileSource.getParameters());
		}
		parameters.addAll(super.getParameters(resources));
		return parameters;
	}
}
