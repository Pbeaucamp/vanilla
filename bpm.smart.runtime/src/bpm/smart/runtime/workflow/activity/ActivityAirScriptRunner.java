package bpm.smart.runtime.workflow.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bpm.smart.core.model.RScriptModel;
import bpm.smart.core.model.workflow.activity.AirScriptActivity;
import bpm.smart.runtime.i18n.Labels;
import bpm.smart.runtime.workflow.ActivityRunner;
import bpm.smart.runtime.workflow.ResourceManager;
import bpm.smart.runtime.workflow.WorkflowRunInstance;
import bpm.vanilla.platform.core.beans.resources.Parameter;
import bpm.vanilla.platform.core.beans.resources.Parameter.TypeParameter;
import bpm.workflow.commons.beans.ActivityLog;
import bpm.workflow.commons.beans.Result;

public class ActivityAirScriptRunner extends ActivityRunner<AirScriptActivity> {

	public ActivityAirScriptRunner(AirScriptActivity activity, WorkflowRunInstance instance, ResourceManager resourceManager) {
		super(activity, instance, resourceManager);
	}

	private List<Parameter> getActivityParameters(List<Parameter> paramsWithoutValues) {
		List<Parameter> paramsWithValues = new ArrayList<>();
		for(Parameter workflowParam : instance.getCurrentParameters()){
			for(Parameter paramWithoutValue : paramsWithoutValues){
				if(workflowParam.getId() == paramWithoutValue.getId()){
					paramsWithValues.add(workflowParam);
				}
			}
		}
		return paramsWithValues;
	}

	@Override
	public ActivityLog executeActivity() throws Exception {
		ActivityLog log = new ActivityLog(this.getActivity().getName());
		log.setStartDate(new Date());
		
		log.addInfo(Labels.getLabel(instance.getCurrentLocale(), Labels.StartActivity) + activity.getName());
		
		int scriptId = ((AirScriptActivity)activity).getrScriptId();
		
		RScriptModel model = instance.getManager().getLastModelbyScript(scriptId);
		model.setUserREnv(instance.getManager().getUser().getLogin() + instance.getManager().getUser().getId().intValue());
		
		// get script params
		List<Parameter> paramsWithoutValue = instance.getManager().getScriptParameters(model.getScript());
		
		// get script params to replace in code or lov params to send
		List<Parameter> paramsWithValue = getActivityParameters(paramsWithoutValue);
			
		
		//change script with params values
		model.setScript(changeTextParameters(model.getScript(), paramsWithValue));
		
		model = instance.getManager().executeScriptR(model, getLovParams(paramsWithValue));
		
		log.setEndDate(new Date());
		log.setScriptR(model.getScript());
		
		if(model.isScriptError()) {
			log.addError(model.getOutputLog());
			log.setResult(Result.ERROR);
		}
		
		else {
			log.addInfo(model.getOutputLog());
			log.setResult(Result.SUCCESS);
//			executeChildActivities();
		}
		
		return log;
	}

	@Override
	public List<Parameter> getParameters() {
		return activity.getParameters(null);
	}

	private String changeTextParameters(String script, List<Parameter> params) {
//		String script = getText();
		for(Parameter param : params){
			if(param.getParameterType().equals(TypeParameter.RANGE)){
				StringBuilder buf = new StringBuilder();
				boolean first = true;
				int rowCount = 0;
				int colCount = 1;
				for(String elem : param.getValueRange()) {
					if(first) {
						first = false;
					}
					else {
						buf.append(",");
					}
					
					try {
						Double.parseDouble(elem);
						buf.append(elem);
					} catch(Exception e) {
						buf.append("\""+elem+"\"");
					}
						
					rowCount++;
				}
				
				buf.append("),nrow = " + rowCount + ",ncol = " + colCount + ", byrow = TRUE");
				
				script = script.replace(param.getParameterName(), "matrix(c(" + buf.toString() + ")");
			} else if(param.getParameterType().equals(TypeParameter.LOV)){
				
			} else {
				script = script.replace(param.getParameterName(), param.getValue());
			}
			
		}
		return script;
	}
	
	public List<Parameter> getLovParams(List<Parameter> parameters) {
		List<Parameter> lovParams = new ArrayList<>();
		for(Parameter param : parameters){
			if(param.getParameterType().equals(TypeParameter.LOV)){
				lovParams.add(param);
			}
		}
		return lovParams;
	}
}
