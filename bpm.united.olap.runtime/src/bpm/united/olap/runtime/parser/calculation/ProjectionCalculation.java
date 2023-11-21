package bpm.united.olap.runtime.parser.calculation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.ProjectionMeasure;
import bpm.united.olap.api.model.impl.ProjectionMeasureCondition;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class ProjectionCalculation implements ICalculation {

	private DataCell cell;
	private ICalculation originalCalcul;
	private IVanillaLogger logger;
	private ProjectionMeasure measure;
	private static ScriptEngine mgr = new ScriptEngineManager().getEngineByName("JavaScript");
	private String formula;
	
	public ProjectionCalculation(DataCell cell, ProjectionMeasure mes, ICalculation calc, IVanillaLogger logger2) {
		this.cell = cell;
		this.measure = mes;
		this.originalCalcul = calc;
		this.logger = logger2;
	}

	@Override
	public DataCell getDataCell() {
		return cell;
	}

	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public boolean isPercentile() {
		return false;
	}

	@Override
	public void makeCalcul() {
			
		HashMap<String, List<Double>> values = originalCalcul.getDataCell().getValues();
		if(values == null) {
			values = new HashMap<String, List<Double>>();
		}
		if(cell.getValues() != null) {
			values.putAll(cell.getValues());
		}
		
		originalCalcul.makeCalcul();
			
		String formula = null;
		
		if(formula == null) {
			cell.setResultValue(originalCalcul.getDataCell().getResultValue());
		}
		else {
		
			String measName = originalCalcul.getMeasure().getUname();
			Double value = originalCalcul.getDataCell().getResultValue();
			if(value != null) {
				formula = formula.replace(measName, value.toString());
			}
			else {
				formula = formula.replace(measName, "");
			}
			
			String result = "";
			try {
				while(formula.endsWith("+") || formula.endsWith("-") || formula.endsWith("/") || formula.endsWith("*")) {
					formula = formula.substring(0, formula.length() - 1);
				}
				result = mgr.eval(formula).toString();
			} catch (Exception e) {
			}
			
			if(result != null && !result.equals("")) {
				cell.setResultValue(Double.parseDouble(result));
			}
			else {
				cell.setResultValue(null);
			}
		}
	}

	@Override
	public void makeCalculDuringQuery(boolean isOnImprovedQuery) throws ParseException {
		HashMap<String, List<Double>> values = cell.getValues();
		if(values == null) {
			values = new HashMap<String, List<Double>>();
		}
		if(cell.getValues() != null) {
			values.putAll(cell.getValues());
		}
		
		if(originalCalcul.getDataCell().getValues() == null) {
			originalCalcul.getDataCell().setValues(new HashMap<String, List<Double>>());
		}

		if(formula != null) {
			
			List<Double> originalValues = values.get(originalCalcul.getMeasure().getUname());
			if(originalValues != null && originalValues.size() > 0) {
				HashMap<String, List<Double>> projectionValues = new HashMap<String, List<Double>>();
				List<Double> projVals = new ArrayList<Double>();
				for(Double val : originalValues) {
					String newFormula = formula.replace(originalCalcul.getMeasure().getUname(), val.toString());
					String result = "";
					try {
						result = mgr.eval(newFormula).toString();
					} catch (Exception e) {
					}
					if(result != null && !result.equals("")) {
						projVals.add(Double.parseDouble(result));
					}
					else {
						projVals.add(null);
					}
				}
				projectionValues.put(originalCalcul.getMeasure().getUname(), projVals);
				values = projectionValues;
			}
		}
		
		originalCalcul.getDataCell().getValues().putAll(values);
		
		originalCalcul.makeCalculDuringQuery(isOnImprovedQuery);
		cell.getValues().clear();
	}

	@Override
	public void setIsPercentile(boolean isPercentile) {
		
	}

	@Override
	public void setItem(DataObjectItem item) {
		
	}

	public void lookForFormula(List<Member> members) {
		
		int minimum = -1;
		ProjectionMeasureCondition actualCondition = null;
		if(measure.getConditions() != null) {
			LOOK:for(ProjectionMeasureCondition cond : measure.getConditions()) {
				int ecart = 0;
				for(Member mem : cond.getMembers()) {
					boolean ok = false;
					
					String[] condPart = mem.getUname().split("\\]\\.\\[");
					int actualEcart = -1;
					for(Member m : members) {
						String[] actualPart = m.getUname().split("\\]\\.\\[");
						
						if(!condPart[0].equals(actualPart[0])) {
							continue;
						}
						
						if(condPart.length < actualPart.length) {
							if(m.getUname().startsWith(mem.getUname())) {
								ecart += actualPart.length - condPart.length;
								actualEcart = ecart;
								ok = true;
								break;
							}
						}
						else {
							if(mem.getUname().startsWith(m.getUname())) {
								ecart += condPart.length - actualPart.length;
								actualEcart = ecart;
								ok = true;
								break;
							}
						}
					}
					
					if(!ok) {
						continue LOOK;
					}
					
					if(actualEcart == - 1) {
						ecart += mem.getParentHierarchy().getLevels().size();
					}
					 
				}
				
				if(minimum >= 0) {
					if(minimum > ecart) {
						minimum = ecart;
						actualCondition = cond;
					}
				}
				else {
					minimum = ecart;
					actualCondition = cond;
				}
				
			}
		}
		
		if(actualCondition != null) {
			formula = actualCondition.getFormula();
		}
		else {
			formula = measure.getFormula();
		}
	}
	
}
