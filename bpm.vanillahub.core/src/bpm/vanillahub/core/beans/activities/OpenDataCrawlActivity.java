package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.activities.attributes.IOpenDataProperties;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.resources.Cible;

public class OpenDataCrawlActivity extends ActivityWithResource<Cible> {

	private static final long serialVersionUID = 1L;
	
	private TypeOpenData typeOpenData;
	
	private String url;
	private boolean addPrefix;
	private boolean updateDataset;
	private boolean manageResource = true;
	private CkanPackage selectedPackage;
	
	private VariableString lastHarvestDate = new VariableString();
	
	private IOpenDataProperties properties;

	public OpenDataCrawlActivity() {
	}

	public OpenDataCrawlActivity(String name) {
		super(TypeActivity.OPENDATA_CRAWL, name);
		this.typeOpenData = TypeOpenData.D4C;
	}
	
	public TypeOpenData getTypeOpenData() {
		return typeOpenData;
	}
	
	public void setTypeOpenData(TypeOpenData typeOpenData) {
		this.typeOpenData = typeOpenData;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean addPrefix() {
		return addPrefix;
	}
	
	public void setAddPrefix(boolean addPrefix) {
		this.addPrefix = addPrefix;
	}
	
	public boolean isUpdateDataset() {
		return updateDataset;
	}
	
	public void setUpdateDataset(boolean updateDataset) {
		this.updateDataset = updateDataset;
	}
	
	public boolean isManageResource() {
		return manageResource;
	}
	
	public void setManageResource(boolean manageResource) {
		this.manageResource = manageResource;
	}
	
	public CkanPackage getSelectedPackage() {
		return selectedPackage;
	}
	
	public void setSelectedPackage(CkanPackage selectedPackage) {
		this.selectedPackage = selectedPackage;
	}
	
	public IOpenDataProperties getProperties() {
		return properties;
	}
	
	public void setProperties(IOpenDataProperties properties) {
		this.properties = properties;
	}
	
	public VariableString getLastHarvestDateVS() {
		return lastHarvestDate;
	}
	
	public String getLastHarvestDateDisplay() {
		return lastHarvestDate.getStringForTextbox();
	}
	
	public void setLastHarvestDate(VariableString lastHarvestDate) {
		this.lastHarvestDate = lastHarvestDate;
	}

	public String getLastHarvestDate(List<Parameter> parameters, List<Variable> variables) {
		return lastHarvestDate != null ? lastHarvestDate.getString(parameters, variables) : null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = new ArrayList<Variable>();
		variables.addAll(lastHarvestDate != null ? lastHarvestDate.getVariables() : new ArrayList<Variable>());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.addAll(lastHarvestDate != null ? lastHarvestDate.getParameters() : new ArrayList<Parameter>());
		return parameters;
	}
}
