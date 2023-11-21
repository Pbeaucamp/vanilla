package bpm.vanillahub.core.beans.activities;

import java.util.List;

import bpm.vanilla.platform.core.beans.resources.LimeSurveyResponseFormat;
import bpm.vanilla.platform.core.beans.resources.LimeSurveyType;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Resource;
import bpm.vanilla.platform.core.beans.resources.Variable;
import bpm.vanilla.platform.core.beans.resources.VariableString;
import bpm.vanillahub.core.beans.resources.LimeSurveyServer;
import bpm.workflow.commons.beans.TypeActivity;

public class LimeSurveyInputActivity extends ActivityWithResource<LimeSurveyServer> {

	private static final long serialVersionUID = 1L;

	private VariableString limeSurveyId = new VariableString();
	private VariableString outputName = new VariableString();
	
	private LimeSurveyType limeSurveyType = LimeSurveyType.LIMESURVEY;
	private LimeSurveyResponseFormat format = LimeSurveyResponseFormat.CSV;
	
	public LimeSurveyInputActivity() {
		super();
	}

	public LimeSurveyInputActivity(String name) {
		super(TypeActivity.LIMESURVEY_INPUT, name);
	}

	@Override
	public boolean isValid() {
		return getResourceId() > 0 && limeSurveyId != null;
	}
	
	public VariableString getLimeSurveyIdVS() {
		return limeSurveyId;
	}
	
	public String getLimeSurveyIdDisplay() {
		return limeSurveyId.getStringForTextbox();
	}
	
	public void setLimeSurveyId(VariableString limeSurveyId) {
		this.limeSurveyId = limeSurveyId;
	}
	
	public String getLimeSurveyId(List<Parameter> parameters, List<Variable> variables) {
		return limeSurveyId.getString(parameters, variables);
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
	
	public LimeSurveyType getLimeSurveyType() {
		return limeSurveyType;
	}
	
	public void setLimeSurveyType(LimeSurveyType limeSurveyType) {
		this.limeSurveyType = limeSurveyType;
	}
	
	public LimeSurveyResponseFormat getFormat() {
		return format;
	}
	
	public void setFormat(LimeSurveyResponseFormat format) {
		this.format = format;
	}

	@Override
	public List<Variable> getVariables(List<? extends Resource> resources) {
		return limeSurveyId.getVariables();
	}

	@Override
	public List<Parameter> getParameters(List<? extends Resource> resources) {
		return limeSurveyId.getParameters();
	}
}
