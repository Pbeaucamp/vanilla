package bpm.vanillahub.core.beans.activities;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.workflow.commons.beans.Activity;
import bpm.workflow.commons.beans.TypeActivity;

public class CompressionActivity extends Activity {
	
	private VariableString outputFile = new VariableString();

	public CompressionActivity() { }
	
	public CompressionActivity(String name) {
		super(TypeActivity.COMPRESSION, name);
	}
	
	public VariableString getOutputFileVS() {
		return outputFile;
	}
	
	public String getOutputFileDisplay() {
		return outputFile.getStringForTextbox();
	}
	
	public void setOutputFile(VariableString outputFile) {
		this.outputFile = outputFile;
	}

	public String getOutputFile(List<Parameter> parameters, List<Variable> variables) {
		return outputFile.getString(parameters, variables);
	}

	@Override
	public boolean isValid() {
		return outputFile != null && !outputFile.getStringForTextbox().isEmpty();
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return outputFile.getVariables();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return outputFile.getParameters();
	}

}
