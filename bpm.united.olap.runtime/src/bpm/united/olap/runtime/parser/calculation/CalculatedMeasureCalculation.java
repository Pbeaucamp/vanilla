package bpm.united.olap.runtime.parser.calculation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;
import bpm.united.olap.api.runtime.RunResult;
import bpm.united.olap.api.runtime.calculation.ICalculation;
import bpm.united.olap.runtime.formatter.IFormatter;
import bpm.vanilla.platform.logging.IVanillaLogger;

/**
 * Calculation for a calculated measure
 * @author Marc Lanquetin
 *
 */
public class CalculatedMeasureCalculation implements ICalculation {

	private DataCell cell;
	private Measure measure;
	private List<ICalculation> calculations;
	private IVanillaLogger logger;
	private String formula;
	private boolean isPercentile = false;
	private static ScriptEngine mgr;
	
	static {
		mgr = new ScriptEngineManager().getEngineByName("JavaScript");
		
	}
	
	public CalculatedMeasureCalculation(DataCell cell, Measure calcMes, List<ICalculation> calculations, String formula, IVanillaLogger logger) {
		this.cell = cell;
		this.measure = calcMes;
		this.calculations = calculations;
		this.logger = logger;
		this.formula = formula;
	}

	@Override
	public Measure getMeasure() {
		return measure;
	}

	@Override
	public void makeCalcul() {
		for(ICalculation calculation : calculations) {
			
			HashMap<String, List<Double>> values = calculation.getDataCell().getValues();
			if(values == null) {
				values = new HashMap<String, List<Double>>();
			}
			if(cell.getValues() != null) {
				values.putAll(cell.getValues());
			}
			
			calculation.makeCalcul();
			
		}
		
		String formula = this.formula;
		
		if(formula == null) {
			cell.setResultValue(calculations.get(0).getDataCell().getResultValue());
		}
		else {
		
			//XXX replace formula for complex function
			formula = ComplexFunctionHelper.replaceComplexFunctions(formula);
			if(calculations != null && !calculations.isEmpty()) {
				for(ICalculation calculation : calculations) {
					String measName = calculation.getMeasure().getUname();
					Double value = calculation.getDataCell().getResultValue();
					if(value != null) {
						formula = formula.replace(measName, value.toString());
					}
					else {
						formula = formula.replace(measName, "");
					}
				}
			}
			
			String result = "";
			try {
				while(formula.endsWith("+") || formula.endsWith("-") || formula.endsWith("/") || formula.endsWith("*")) {
					formula = formula.substring(0, formula.length() - 1);
				}
				result = mgr.eval(formula).toString();
			} catch (Exception e) {
				logger.warn("Error when parsing expression : " + formula,e);
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
		for(ICalculation calculation : calculations) {
			
			HashMap<String, List<Double>> values = cell.getValues();
			if(values == null) {
				values = new HashMap<String, List<Double>>();
			}
			if(cell.getValues() != null) {
				values.putAll(cell.getValues());
			}
			
			if(calculation.getDataCell().getValues() == null) {
				calculation.getDataCell().setValues(new HashMap<String, List<Double>>());
			}
			calculation.getDataCell().getValues().putAll(values);
			
			calculation.makeCalculDuringQuery(isOnImprovedQuery);
		}
		try {
			cell.getValues().clear();
		} catch (Exception e) {
		}
	}

	@Override
	public DataCell getDataCell() {
		return cell;
	}

	@Override
	public boolean isPercentile() {
		return isPercentile;
	}

	@Override
	public void setIsPercentile(boolean isPercentile) {
		this.isPercentile = isPercentile;
		if(isPercentile) {
			cell.setFormat(IFormatter.PERCENT);
		}
	}

	@Override
	public void setItem(DataObjectItem item) {
		
		
	}

	public List<Measure> getMeasures() {
		List<Measure> mess = new ArrayList<Measure>();
		for(ICalculation calc : calculations) {
			mess.add(calc.getMeasure());
		}
		return mess;
	}
}
