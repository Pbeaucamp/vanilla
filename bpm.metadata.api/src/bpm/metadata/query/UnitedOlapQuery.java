package bpm.metadata.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;

public class UnitedOlapQuery implements IQuery {

	private List<IDataStreamElement> select = new ArrayList<IDataStreamElement>();
	private HashMap<ListOfValue, String> condition = new HashMap<ListOfValue, String>();
	private List<AggregateFormula> aggs  = new ArrayList<AggregateFormula>();;
	private List<Ordonable> orderBy  = new ArrayList<Ordonable>();
	
	private List<Prompt> prompts = new ArrayList<Prompt>();
	
	private List<IFilter> filters = new ArrayList<IFilter>();

	private String groupName = "";
	private boolean dictinct = false;
	private int limit = 0; 
	
	private List<Formula> formulas = new ArrayList<Formula>();
	
	private boolean hideNull;
	
	public UnitedOlapQuery(){}
	
	public UnitedOlapQuery(List<IDataStreamElement> selects, List<IFilter> filters, List<Prompt> prompts, String groupName, boolean hideNull) {
		this.select = selects;
		this.filters = filters;
		this.groupName = groupName;
		this.prompts = prompts;
		this.hideNull = hideNull;
	}
	
	public void setOrberBy(List<Ordonable> list){
		orderBy = list;
	}
	
	public void setDistinct(boolean value){
		dictinct = value;
	}
	
	public void setDistinct(String value){
		dictinct = Boolean.parseBoolean(value);
	}
	
	public boolean getDistinct(){
		return dictinct;
	}
	
	public String getGroupName() {
		return groupName;
	}


	protected void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	protected void setSelect(List<IDataStreamElement> select){
		this.select.clear();
		this.select.addAll(select);
	}


	protected void setCondition(HashMap<ListOfValue, String> condition) {
		this.condition = condition;
	}
	
	protected void setPrompts(List<Prompt> prompts) {
		
		if (prompts == null){
			this.prompts.clear();
		}
		else{
			this.prompts = prompts;
		}

	}


	protected void setAggs(List<AggregateFormula> aggs) {
		this.aggs = aggs;
	}

	protected void setFilters(List<IFilter> filters) {
		
		if (filters == null){
			this.filters.clear();
		}
		else{
			this.filters = filters;
		}
		
	}
	
	protected void setOrderBy(List<Ordonable> orderBy) {
		this.orderBy = orderBy;
	}


	public List<IDataStreamElement> getSelect() {
		return select;
	}


	public HashMap<ListOfValue, String> getCondition() {
		return condition;
	}


	public List<AggregateFormula> getAggs() {
		return aggs;
	}


	public List<Ordonable> getOrderBy() {
		return orderBy;
	}


	public List<IFilter> getFilters() {
		return filters;
	}


	public List<Prompt> getPrompts() {
		if(prompts == null) {
			prompts = new ArrayList<Prompt>();
		}
		return prompts;
	}
	
	public void setLimit(int limit){
		this.limit = limit;
	}
	
	public void setLimit(String limit){
		try{
			this.limit = Integer.parseInt(limit);
		}catch(Exception e){
			
		}
	}
	
	public int getLimit(){
		return limit;
	}

	/**
	 * @return the formulas
	 */
	public List<Formula> getFormulas() {
		return formulas;
	}

	/**
	 * @param formulas the formulas to set
	 */
	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
	}
	
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<freeMetaDataQuery>\n");
		buf.append("     <sqlQuery>\n");
		buf.append("     <distinct>" + dictinct + "</distinct>\n");
		buf.append("     <hideNull>" + hideNull + "</hideNull>\n");
		buf.append("     <limit>" + limit + "</limit>\n");
		for(IDataStreamElement e : select){
			buf.append("         <select>\n");
			buf.append("             <dataStreamElementName>" + e.getName() + "</dataStreamElementName>\n");
			buf.append("             <dataStreamName>" + e.getDataStream().getName() + "</dataStreamName>\n");
			buf.append("         </select>\n");
		}
		
		if (filters != null){
			for(IFilter e : filters){
				buf.append("         <filter>" + e.getName()+"</filter>\n");
			}
		}
		
		
		if (formulas != null){
			for(Formula s : formulas){
				buf.append(s.getXml());
			}
		}
		
		if (prompts != null){
			for(Prompt e : prompts){
				if (e == null){
					continue;
				}
				buf.append("         <prompt>" + e.getName()+"</prompt>\n");
			}
		}
		
		if (aggs != null){
			for(AggregateFormula a : aggs){
				buf.append("         <agg>\n");
				buf.append("             <function>" + a.getFunction() + "</function>\n");
				buf.append("             <col>" + a.getCol().getName() + "</col>\n");
				buf.append("             <dataStreamName>" + a.getCol().getDataStream().getName() + "</dataStreamName>\n");
				buf.append("             <outputName>" + a.getOutputName() + "</outputName>\n");
				
//				buf.append("             <groups>\n");
//				for(IDataStreamElement e : a.getGroupBy()){
//					buf.append("                 <groupBy>\n");
//					buf.append("                     <col>" + e.getName() + "</col>\n");
//					buf.append("                     <dataStreamName>" + e.getDataStream().getName() + "</dataStreamName>\n");
//					buf.append("                 </groupBy>\n");
//				}
//				buf.append("             </groups>\n");
				
				buf.append("         </agg>\n");
			}
		}
		
		
		
		if (orderBy != null){
			for(Ordonable o : orderBy){
				
				if (o instanceof IDataStreamElement){
					buf.append("         <orderBy>\n");
					buf.append("             <dataStreamElementName>" + ((IDataStreamElement)o).getName() + "</dataStreamElementName>\n");
					buf.append("             <dataStreamName>" + ((IDataStreamElement)o).getDataStream().getName() + "</dataStreamName>\n");
					buf.append("          </orderBy>\n");
				}
				else if (o instanceof AggregateFormula){
					buf.append("         <orderByAgg>\n");
					buf.append("             <agg>" + ((AggregateFormula)o).getOutputName() + "</agg>\n");
					buf.append("             <pos>" + getAggs().indexOf(((AggregateFormula)o)) + "</pos>\n");
					buf.append("         </orderByAgg>\n");
				}
				
			}
		}
		buf.append("     </sqlQuery>\n");
		buf.append("</freeMetaDataQuery>\n");
		return buf.toString();
	}

	public void setHideNull(boolean hideNull) {
		this.hideNull = hideNull;
	}

	public boolean isHideNull() {
		return hideNull;
	}
	
	public void setHideNull(String hideNull) {
		this.hideNull = Boolean.parseBoolean(hideNull);
	}

	@Override
	public void addPrompt(Prompt prompt) {
		prompts.add(prompt);
	}

	@Override
	public void removePrompt(Prompt firstElement) {
		prompts.remove(firstElement);
	}
	
}
