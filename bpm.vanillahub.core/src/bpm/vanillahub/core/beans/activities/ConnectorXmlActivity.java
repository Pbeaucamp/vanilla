package bpm.vanillahub.core.beans.activities;

import java.util.ArrayList;
import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanillahub.core.beans.resources.Connector;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class ConnectorXmlActivity extends Activity {

	private Connector connector;
	
	public ConnectorXmlActivity() { }
	
	public ConnectorXmlActivity(String name) {
		super(TypeActivity.CONNECTOR, name);
	}

	@Override
	public boolean isValid() {
		return getConnector() != null;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}
	
	public Connector getConnector() {
		return connector;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return new ArrayList<Parameter>();
	}
}
