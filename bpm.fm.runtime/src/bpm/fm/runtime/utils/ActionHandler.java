package bpm.fm.runtime.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import bpm.fm.api.model.MetricAction;
import bpm.fm.api.model.utils.ActionResult;
import bpm.fm.api.model.utils.MetricValue;

public class ActionHandler {
	
	private static ScriptEngine mgr;
	
	static {
		mgr = new ScriptEngineManager().getEngineByName("JavaScript");
	}

	public static HashMap<MetricAction, ActionResult> handleAction(MetricValue value) throws Exception {
		HashMap<MetricAction, ActionResult> result = new HashMap<MetricAction, ActionResult>();
		
		List<MetricAction> actions = value.getMetric().getMetricActions();
		
		Date date = value.getDate();
		
		List<MetricAction> toHandle = new ArrayList<MetricAction>();
		for(MetricAction action : actions) {
			if((action.getStartDate().equals(date) || action.getStartDate().before(date)) && ((action.getEndDate().equals(date) || action.getEndDate().after(date)))) {
				toHandle.add(action);
			}
		}
		
		for(MetricAction action : toHandle) {
			ActionResult res = calculateAction(value, action);
			result.put(action, res);
		}
		
		return result;
	}

	private static ActionResult calculateAction(MetricValue value, MetricAction action) throws Exception {
		ActionResult result = new ActionResult();
		
		String formula = action.getFormula();
		formula = formula.replace(MetricAction.ELEMENT_VALUE, value.getValue() + "");
		formula = formula.replace(MetricAction.ELEMENT_MAX, value.getMaximum() + "");
		formula = formula.replace(MetricAction.ELEMENT_MIN, value.getMinimum() + "");
		formula = formula.replace(MetricAction.ELEMENT_OBJECTIVE, value.getObjective() + "");
		
		String res;
		try {
			res = mgr.eval(formula).toString();
		} catch (Exception e) {
			res = "false";
		}
		
		if(res.equalsIgnoreCase("true")) {
			result.setHealth(1);
		}
		else {
			result.setHealth(-1);
		}
		if(action.getStartDate().after(value.getDate())) {
			result.setStatus(ActionResult.STATUS_NOT_STARTED);
		}
		else if(action.getEndDate().before(value.getDate()) || action.getEndDate().equals(value.getDate())) {
			result.setStatus(ActionResult.STATUS_NOT_FINISHED);
		}
		else {
			result.setStatus(ActionResult.STATUS_FINISHED);
		}
		
		return result;
	}
	
}
