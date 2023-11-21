package bpm.vanillahub.core.beans.activities;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.TypeActivity;
import bpm.workflow.commons.resources.Cible;

public class PreClusterGeoDataActivity extends ActivityWithResource<Cible> {

	private static final long serialVersionUID = 1L;
	
	private VariableString urlServerNode = new VariableString("http://localhost:1337/");
	
	public PreClusterGeoDataActivity() { }
	
	public PreClusterGeoDataActivity(String name) {
		super(TypeActivity.PRECLUSTER, name);
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0;
	}

	public VariableString getUrlServerNode() {
		return urlServerNode;
	}

	public void setUrlServerNode(VariableString urlServerNode) {
		this.urlServerNode = urlServerNode;
	}
	
	public String getUrlServerNodeDisplay() {
		return urlServerNode.getStringForTextbox();
	}

	public String getUrlServerNode(List<Parameter> parameters, List<Variable> variables) {
		return urlServerNode.getString(parameters, variables);
	}

}
