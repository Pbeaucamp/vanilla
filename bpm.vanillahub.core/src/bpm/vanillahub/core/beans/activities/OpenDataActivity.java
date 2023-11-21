package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.activities.attributes.DataGouv;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class OpenDataActivity extends Activity {

	private static final long serialVersionUID = 1L;
	
	private TypeOpenData typeOpenData;
	private DataGouv dataGouv;
	private CkanPackage ckanPackage;

	private VariableString outputName = new VariableString();
	private String url;
	
	public OpenDataActivity() {
	}

	public OpenDataActivity(String name) {
		super(TypeActivity.OPEN_DATA, name);
		this.typeOpenData = TypeOpenData.DATA_GOUV;
	}
	
	public TypeOpenData getTypeOpenData() {
		return typeOpenData;
	}
	
	public void setTypeOpenData(TypeOpenData typeOpenData) {
		this.typeOpenData = typeOpenData;
	}

	public VariableString getOutputNameVS() {
		return outputName;
	}

	public String getOutputNameDisplay() {
		return outputName.getStringForTextbox();
	}

	public void setOutputName(VariableString outputName) {
		this.outputName = outputName;
	}

	public String getOutputName(List<Parameter> parameters, List<Variable> variables) {
		return outputName.getString(parameters, variables);
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String buildDatasetUrl() {
		return dataGouv != null ? dataGouv.buildDatasetUrl() : null;
	}
	
	public void setDataGouv(DataGouv dataGouv) {
		this.dataGouv = dataGouv;
	}
	
	public DataGouv getDataGouv() {
		return dataGouv;
	}
	
	public void setCkanPackage(CkanPackage ckanPackage) {
		this.ckanPackage = ckanPackage;
	}
	
	public CkanPackage getCkanPackage() {
		return ckanPackage;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		List<Variable> variables = new ArrayList<>();
		variables.addAll(outputName.getVariables());
		return variables;
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		List<Parameter> parameters = new ArrayList<>();
		parameters.addAll(outputName.getParameters());
		return parameters;
	}

	@Override
	public boolean isValid() {
		return dataGouv != null;
	}
}
