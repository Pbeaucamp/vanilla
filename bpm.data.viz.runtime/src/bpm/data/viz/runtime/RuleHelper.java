package bpm.data.viz.runtime;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import bpm.data.viz.core.preparation.DataPreparation;
import bpm.data.viz.core.preparation.PreparationRule;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;
import bpm.data.viz.core.preparation.PreparationRuleAddChar;
import bpm.data.viz.core.preparation.PreparationRuleAffecter;
import bpm.data.viz.core.preparation.PreparationRuleAffecter.Plage;
import bpm.data.viz.core.preparation.PreparationRuleCalc;
import bpm.data.viz.core.preparation.PreparationRuleFilter;
import bpm.data.viz.core.preparation.PreparationRuleFormat;
import bpm.data.viz.core.preparation.PreparationRuleGroup;
import bpm.data.viz.core.preparation.PreparationRuleMinMax;
import bpm.data.viz.core.preparation.PreparationRuleRecode;
import bpm.data.viz.core.preparation.PreparationRuleSort;
import bpm.data.viz.core.preparation.PreparationRuleSort.SortType;
import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.DatasetResultQuery;

public class RuleHelper {

	public static void applyRule(DatasetResultQuery resQuery, PreparationRule rule, DataPreparation dataPrep) {
//		if(rule.getColumn() != null) {
			switch(rule.getType()) {
				case LOWER_CASE:
				case UPPER_CASE:
					List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
					for(int i = 0; i < res.size(); i++) {
						try {
							if(rule.getType() == RuleType.LOWER_CASE) {
								res.set(i, String.valueOf(res.get(i)).toLowerCase());
							}
							else {
								res.set(i, String.valueOf(res.get(i)).toUpperCase());
							}
						} catch(Exception e) {}
					}
					setNewValues(resQuery, res, rule);
					break;
				case RECODE:
					recode(resQuery, (PreparationRuleRecode) rule);
					break;
				case SORT:
					sort(resQuery, (PreparationRuleSort) rule, dataPrep);
					break;
				case ADD_CHAR:
					addChar(resQuery, (PreparationRuleAddChar) rule);
					break;
				case NORMALIZE:
					normalize(resQuery, rule);
					break;
				case FORMAT_NUMBER:
					format(resQuery, (PreparationRuleFormat) rule);
					break;
				case MIN:
				case MAX:
					minMax(resQuery, (PreparationRuleMinMax) rule);
					break;
				case ROUND:
					round(resQuery, rule);
					break;
				case DATE_TO_AGE:
					dateToAge(resQuery, rule);
					break;
				case CALC:
					calc(resQuery, (PreparationRuleCalc) rule);
					break;
				case DEDOUBLON:
					dedoublon(resQuery, rule);
					break;
				case FILTER:
					filter(resQuery, (PreparationRuleFilter)rule);
					break;
				case GROUP:
					group(resQuery, (PreparationRuleGroup)rule);
					break;
					
				case AFFECTER:
					affecter(resQuery, (PreparationRuleAffecter)rule);
					break;
					
			
			}
//		}
	}
	
	private static void group(DatasetResultQuery resQuery, PreparationRuleGroup rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(resQuery.getResult().keySet().iterator().next()));
		for(int i = 0; i < res.size(); i++) {
			try {

				res.set(i, rule.getMappings().get(res.get(i)));

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}

	public static List<Map<DataColumn, Serializable>> convertResult(DataPreparation dataPrep, Map<String, List<Serializable>> toConvert) {
		List<Map<DataColumn, Serializable>> result = new ArrayList<>();
		
		int rowNumber = findLineNumber(toConvert);
		for(int i = 0 ; i < rowNumber; i++) {
			Map<DataColumn, Serializable> line = new LinkedHashMap<>();
			
			for(String key : toConvert.keySet()) {
				Serializable value = null;
				
				List<Serializable> values = toConvert.get(key);
				if (values != null && values.size() > i) {
					value = values.get(i);
				}
				
				line.put(findColumn(dataPrep.getDataset(), key), value);
			}
			
			result.add(line);
		}
		
		return result;
	}
	
	private static int findLineNumber(Map<String, List<Serializable>> data) {
		int lineNumber = 0;
		if (data != null) {
			for (String column : data.keySet()) {
				List<Serializable> values = data.get(column);
				if (values != null && lineNumber < values.size()) {
					lineNumber = values.size();
				}
			}
		}
		return lineNumber;
	}
	
	private static DataColumn findColumn(Dataset dataset, String key) {
		for(DataColumn col : dataset.getMetacolumns()) {
			if(col.getColumnName().endsWith(key)) {
				return col;
			}
		}
		DataColumn col = new DataColumn();
		col.setColumnName(key);
		col.setColumnLabel(key);
		return col;
	}

	private static void filter(DatasetResultQuery resQuery, PreparationRuleFilter rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(resQuery.getResult().keySet().iterator().next()));
		int removed = 0;
		for(int i = 0; i < res.size(); i++) {
			try {

				String formula = rule.getFilter();
				
				for(String key : resQuery.getResult().keySet()) {
					try {
						formula = formula.replace(key, resQuery.getResult().get(key).get(i - removed).toString());
					} catch(Exception e) {
					}
				}
				
				Context cx = ContextFactory.getGlobal().enterContext();
				
				Object result = null;
				try {
					Scriptable scope = cx.initStandardObjects();
//					System.out.println(formula);
					result = cx.evaluateString(scope, formula, "<cmd>", 1, null);
//					System.out.println(result);
					if(!Boolean.parseBoolean(result.toString())) {
//						String rm = "";
//						String rm2 = "";
						for(String col : resQuery.getResult().keySet()) {
//							rm += " - " + resQuery.getResult().get(col).get(i-removed);
//							rm2 += " - " + resQuery.getResult().get(col).get(i);
							
							resQuery.getResult().get(col).remove(i - removed);
							
							
						}
//						System.out.println(rm + " - > " + (i-removed) + resQuery.getResult().values().iterator().next().size()) ;
//						System.out.println(rm2 + " - > " + (i) + resQuery.getResult().values().iterator().next().size()) ;
						removed++; 
					}
					
				} catch(Exception e) {
//					System.out.println("catch");
					for(String col : resQuery.getResult().keySet()) {
						resQuery.getResult().get(col).remove(i - removed);
						
					}
					removed++;
//					e.printStackTrace();
				} finally {
					Context.exit();
				}

			} catch(Exception e) {}
		}
	}

	private static void dedoublon(DatasetResultQuery resQuery, PreparationRule rule) {
		int size = resQuery.getResult().values().iterator().next().size();
		List<List<Serializable>> existing = new ArrayList<>();
		int removed = 0;
		for(int i = 0; i < size; i++) {
			try {
				List<Serializable> line = new ArrayList<>();
				for(DataColumn col : rule.getColumns()) {
					line.add(resQuery.getResult().get(col.getColumnName()).get(i - removed));
				}
				
				if(existing.contains(line)) {
					for(String col : resQuery.getResult().keySet()) {
						resQuery.getResult().get(col).remove(i - removed);
						
					}
					removed++;
				}
				else {
					existing.add(line);
				}

			} catch(Exception e) {}
		}
	}

	private static void calc(DatasetResultQuery resQuery, PreparationRuleCalc rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(resQuery.getResult().keySet().iterator().next()));
		for(int i = 0; i < res.size(); i++) {
			try {

				String formula = rule.getFormula();
				
				for(String key : resQuery.getResult().keySet()) {
					try {
						formula = formula.replace(key, resQuery.getResult().get(key).get(i).toString());
					} catch(Exception e) {
					}
				}
				
				Context cx = ContextFactory.getGlobal().enterContext();
				
				Object result = null;
				try {
					Scriptable scope = cx.initStandardObjects();
					result = cx.evaluateString(scope, formula, "<cmd>", 1, null);
					res.set(i, result.toString());
				} finally {
					Context.exit();
				}

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}

	private static void dateToAge(DatasetResultQuery resQuery, PreparationRule rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {

				Date val = (Date) res.get(i);

				Calendar curr = Calendar.getInstance();
				Calendar birth = Calendar.getInstance();
				birth.setTime(val);
				int yeardiff = curr.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
				curr.add(Calendar.YEAR, -yeardiff);
				if(birth.after(curr)) {
					yeardiff = yeardiff - 1;
				}

				res.set(i, yeardiff);

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}

	private static void setNewValues(DatasetResultQuery resQuery, List<Serializable> res, PreparationRule rule) {
		if(rule.isNewColumn()) {
			resQuery.getResult().put(rule.getNewColumnName() != null && !rule.getNewColumnName().isEmpty() ? rule.getNewColumnName() : rule.getColumn().getColumnName() + "_" + rule.getType(), res);
		}
		else {
			resQuery.getResult().put(rule.getColumn().getColumnName(), res);
		}
		
	}

	private static void round(DatasetResultQuery resQuery, PreparationRule rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {

				double val = Double.valueOf(res.get(i).toString());

				res.set(i, Math.round(val));

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}

	private static void minMax(DatasetResultQuery resQuery, PreparationRuleMinMax rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {

				double val = Double.valueOf(res.get(i).toString());

				if(rule.getType() == RuleType.MIN && val < rule.getValue()) {
					val = rule.getValue();
				}
				else if(rule.getType() == RuleType.MAX && val > rule.getValue()) {
					val = rule.getValue();
				}

				res.set(i, val);

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}

	private static void format(DatasetResultQuery resQuery, PreparationRuleFormat rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {

				double val = Double.valueOf(res.get(i).toString());
				DecimalFormat f = new DecimalFormat(rule.getPattern());
				res.set(i, f.format(val));

			} catch(Exception e) {
				// e.printStackTrace();
			}
		}
		setNewValues(resQuery, res, rule);
	}

	private static void normalize(DatasetResultQuery resQuery, PreparationRule rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {
				String val = Normalizer.normalize(String.valueOf(res.get(i)), Normalizer.Form.NFD);
				val = val.replaceAll("[^\\p{ASCII}]", "");
				res.set(i, val);

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}

	private static void addChar(DatasetResultQuery resQuery, PreparationRuleAddChar rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {

				String val = String.valueOf(res.get(i)) + rule.getCharToAdd();

				res.set(i, val);

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}

	private static void sort(DatasetResultQuery resQuery, final PreparationRuleSort rule, DataPreparation dp) {
		
		List<Map<DataColumn, Serializable>> values = convertResult(dp, resQuery.getResult());
		
		Collections.sort(values, new Comparator<Map<DataColumn, Serializable>>() {
			@Override
			public int compare(Map<DataColumn, Serializable> o1, Map<DataColumn, Serializable> o2) {
				for(DataColumn col : rule.getColumns()) {
					int res = 0;
					if(o1.get(col) == null) {
						res = -1;
					}
					else if(o2.get(col) == null) {
						res = 1;
					}
					else {
						if(o1.get(col) instanceof String || o2.get(col) instanceof String) {
							res = (o1.get(col).toString()).compareTo(o2.get(col).toString());
						}
						else {
							res = ((Comparable)o1.get(col)).compareTo(((Comparable)o2.get(col)));
						}
						
					}
					if(rule.getSortType() == SortType.DESC) {
						res = - res;
					}
					if(res != 0) {
						return res;
					}
				}
				return 0;
			}
		});
		
		Map<String, List<Serializable>> result = new LinkedHashMap<String, List<Serializable>>();
		
		for(Map<DataColumn, Serializable> line : values) {
			for(DataColumn col : rule.getColumns()) {
				if(result.get(col.getColumnName()) == null) {
					result.put(col.getColumnName(), new ArrayList<Serializable>());
				}
				result.get(col.getColumnName()).add(line.get(col));
			}
			for(DataColumn col : line.keySet()) {
				if(!rule.getColumns().contains(col)) {
					if(result.get(col.getColumnName()) == null) {
						result.put(col.getColumnName(), new ArrayList<Serializable>());
					}
					result.get(col.getColumnName()).add(line.get(col));
				}
			}
		}
		
		resQuery.setResult(result);
		
//		List<Serializable> res = resQuery.getResult().get(rule.getColumn().getColumnName());
////		for(int i = 0; i < res.size(); i++) {
//			try {
//
//				RuleUtils.keySort(res, rule.getSortType(), resQuery.getResult().values());
//
//			} catch(Exception e) {}
////		}
//		setNewValues(resQuery, res, rule);
	}

	private static void recode(DatasetResultQuery resQuery, PreparationRuleRecode rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {

				String val = String.valueOf(res.get(i)).replaceAll(rule.getOriginFormula(), rule.getResultFormula());

				res.set(i, val);

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
	}
	
	
	private static void affecter(DatasetResultQuery resQuery, PreparationRuleAffecter rule) {
		List<Serializable> res = new ArrayList<>(resQuery.getResult().get(rule.getColumn().getColumnName()));
		for(int i = 0; i < res.size(); i++) {
			try {

				double val = Double.parseDouble(String.valueOf(res.get(i))) ;
				Serializable original = res.get(i);
				for(Plage p :  rule.getGroups() ) {
					if(val >= p.getMin() &&  val <= p.getMax() ){
						
						
						original =  p.getMin() + "-" + p.getMax();
						
					}
					
				}

				res.set(i, original);

			} catch(Exception e) {}
		}
		setNewValues(resQuery, res, rule);
}
	
}
