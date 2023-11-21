package bpm.metadata.misc;

import java.util.ArrayList;
import java.util.List;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.Formula;
import bpm.metadata.query.Ordonable;

public class AggregateFormula implements Ordonable{
	public static final String SUM = "SUM";
	public static final String AVG = "AVG";
	public static final String MIN = "MIN";
	public static final String MAX = "MAX";
	public static final String COUNT = "COUNT";
	public static final String COUNT_DISTINCT = "DISTINCT COUNT";
	private String function;
	private IDataStreamElement col;
//	private List<IDataStreamElement> groupBy = new ArrayList<IDataStreamElement>();
	private String outputName;
	private boolean basedOnFormula = false;
	
	private List<String> formulaDataStreamInvolved = new ArrayList<String>();
	
	public AggregateFormula(String function, IDataStreamElement col/*, List<IDataStreamElement> groupBy*/, String outputName){
		this.function = function;
		this.col = col;
//		this.groupBy = groupBy;
		this.outputName = outputName;
	}

	public AggregateFormula(String function, Formula formula, String outputName) {
		this.function = function;
		this.outputName = outputName;
		this.col = new ICalculatedElement(formula.getFormula());
		this.col.setName(formula.getName());
		this.formulaDataStreamInvolved.addAll(formula.getDataStreamInvolved());
		this.basedOnFormula = true;
	}
	public List<String> getInvolvedDataStreamNames(){
		if (this.col != null && this.col.getDataStream() != null){
			List<String> l = new ArrayList<String>();
			l.add(this.col.getDataStream().getName());
			return l;
		}
		else{
			
			return  new ArrayList<String>(formulaDataStreamInvolved);
		}
	}
	
	public boolean isBasedOnFormula(){
		return basedOnFormula;
	}

	public String getFunction() {
		return function;
	}

	public String getSqlFunction(){
		if (function.equals(COUNT_DISTINCT)){
			return " COUNT(DISTINCT";
		}
		else{
			return getFunction();
		}
	}
	public IDataStreamElement getCol() {
		return col;
	}
	
	public void setCol(IDataStreamElement col) {
		this.col = col;
	}

//	public List<IDataStreamElement> getGroupBy() {
//		return groupBy;
//	}
//	
//	public void addToGroupBy(IDataStreamElement el){
//		if (!groupBy.contains(el)){
//			groupBy.add(el);
//		}
//	}
//	
//	public void removeToGroupBy(IDataStreamElement el){
//		groupBy.remove(el);
//	}
	
	public String getOutputName(){
		return outputName;
	}

	public void setFunction(String function) {
		this.function = function;
		
	}
	
	public void setOutputName(String name) {
		this.outputName = name;
	}
}

