package bpm.metadata.query;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;

public class QuerySql implements IQuery {

	private List<IResource> designTimeResources = new ArrayList<IResource>();

	private List<IDataStreamElement> select = new ArrayList<IDataStreamElement>();
	private HashMap<ListOfValue, String> condition = new HashMap<ListOfValue, String>();
	private List<AggregateFormula> aggs = new ArrayList<AggregateFormula>();;
	private List<Ordonable> orderBy = new ArrayList<Ordonable>();

	private List<Prompt> prompts = new ArrayList<Prompt>();

	private List<IFilter> filters = new ArrayList<IFilter>();

	private String groupName = "";
	private boolean dictinct = false;
	private int limit = 0;

	private List<Formula> formulas = new ArrayList<Formula>();
	
	private List<RelationStrategy> relationStrategies = new ArrayList<RelationStrategy>();

	public List<IResource> getDesignTimeResources() {
		return this.designTimeResources;
	}

	public void addDesignTimeResource(IResource r) {
		if (!this.designTimeResources.contains(r)) {
			this.designTimeResources.add(r);
		}

	}

	public void removeDesignTimeResource(IResource r) {
		this.designTimeResources.remove(r);
	}

	public void setOrberBy(List<Ordonable> list) {
		orderBy = list;
	}

	public void setDistinct(boolean value) {
		dictinct = value;
	}

	public void setDistinct(String value) {
		dictinct = Boolean.parseBoolean(value);
	}

	public boolean getDistinct() {
		return dictinct;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	protected void setSelect(List<IDataStreamElement> select) {
		this.select.clear();
		this.select.addAll(select);
	}

	protected void setCondition(HashMap<ListOfValue, String> condition) {
		this.condition = condition;
	}

	protected void setPrompts(List<Prompt> prompts) {

		if (prompts == null) {
			this.prompts.clear();
		}
		else {
			this.prompts = prompts;
		}

	}

	public void addPrompt(Prompt prompt) {
		if (prompts == null) {
			prompts = new ArrayList<Prompt>();
		}
		prompts.add(prompt);
	}

	public void removePrompt(Prompt firstElement) {
		if (prompts != null) {
			prompts.remove(firstElement);
		}
	}

	protected void setAggs(List<AggregateFormula> aggs) {
		this.aggs = aggs;
	}

	protected void setFilters(List<IFilter> filters) {

		if (filters == null) {
			this.filters.clear();
		}
		else {
			this.filters = filters;
		}

	}
	
	public void addFilter(IFilter filter) {
		if(filters == null) {
			filters = new ArrayList<IFilter>();
		}
		filters.add(filter);
	}
	
	public void clearFilter() {
		if(filters != null) {
			filters.clear();
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
		if (orderBy == null) {
			orderBy = new ArrayList<Ordonable>();
		}
		return orderBy;
	}

	public List<IFilter> getFilters() {
		List<IFilter> l = new ArrayList<IFilter>(filters);
		for (IResource r : designTimeResources) {
			if (r instanceof IFilter) {
				l.add((IFilter) r);
			}
		}
		return l;
	}

	public List<Prompt> getPrompts() {
		List<Prompt> l = new ArrayList<Prompt>(prompts);
		for (IResource r : designTimeResources) {
			if (r instanceof Prompt) {
				l.add((Prompt) r);
			}
		}
		return l;
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<freeMetaDataQuery>\n");
		buf.append("     <sqlQuery>\n");
		buf.append("     <distinct>" + dictinct + "</distinct>\n");
		buf.append("     <limit>" + limit + "</limit>\n");
		for (IDataStreamElement e : select) {
			buf.append("         <select>\n");
			buf.append("             <dataStreamElementName>" + e.getName() + "</dataStreamElementName>\n");
			buf.append("             <dataStreamName>" + e.getDataStream().getName() + "</dataStreamName>\n");
			buf.append("         </select>\n");
		}

		if (filters != null) {
			for (IFilter e : filters) {
				if (designTimeResources.contains(e)) {
					continue;
				}
				buf.append("         <filter>" + e.getName() + "</filter>\n");
			}
		}

		if (formulas != null) {
			for (Formula s : formulas) {
				if (designTimeResources.contains(s)) {
					continue;
				}
				buf.append(s.getXml());
			}
		}

		if (prompts != null) {
			for (Prompt e : prompts) {
				if (e == null) {
					continue;
				}
				if (designTimeResources.contains(e)) {
					continue;
				}
				buf.append("         <prompt>" + e.getName() + "</prompt>\n");
			}
		}

		if (aggs != null) {
			for (AggregateFormula a : aggs) {
				buf.append("         <agg>\n");
				buf.append("             <function>" + a.getFunction() + "</function>\n");
				if (a.isBasedOnFormula()) {
					buf.append("             <formula>true</formula>\n");
					buf.append("             <col>" + ((ICalculatedElement) a.getCol()).getFormula().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</col>\n");

				}
				else {
					buf.append("             <formula>false</formula>\n");
					buf.append("             <col>" + a.getCol().getName() + "</col>\n");

				}
				StringBuffer streams = new StringBuffer();
				for (String s : a.getInvolvedDataStreamNames()) {
					if (streams.length() > 0) {
						streams.append(",");
					}
					streams.append(s);
				}
				buf.append("             <dataStreamName>" + streams.toString() + "</dataStreamName>\n");

				buf.append("             <outputName>" + a.getOutputName() + "</outputName>\n");
				buf.append("         </agg>\n");
			}
		}

		if (orderBy != null) {
			for (Ordonable o : orderBy) {

				//Trying to disable this 
				if (o instanceof IDataStreamElement /*&& !(o instanceof ICalculatedElement)*/) {
					buf.append("         <orderBy>\n");
					buf.append("             <dataStreamElementName>" + ((IDataStreamElement) o).getName() + "</dataStreamElementName>\n");
					buf.append("             <dataStreamName>" + ((IDataStreamElement) o).getDataStream().getName() + "</dataStreamName>\n");
					buf.append("          </orderBy>\n");
				}
				else if (o instanceof AggregateFormula) {
					buf.append("         <orderByAgg>\n");
					buf.append("             <agg>" + ((AggregateFormula) o).getOutputName() + "</agg>\n");
					buf.append("             <pos>" + getAggs().indexOf(((AggregateFormula) o)) + "</pos>\n");
					buf.append("         </orderByAgg>\n");
				}
				else if(o instanceof Formula){
					buf.append("         <orderByForm>\n");
					buf.append("<name>" + ((Formula)o).getName() + "</name>\n");
					buf.append("<script>" + ((Formula)o).getFormula().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</script>\n");
					StringBuffer streams = new StringBuffer();
					
					for(String s : ((Formula)o).getDataStreamInvolved()){
						if (streams.length() > 0){
							streams.append(",");
						}
						streams.append(s);	
					}
					buf.append("<dataStreamNames>" + streams.toString() + "</dataStreamNames>\n");
					
					buf.append("         </orderByForm>\n");
				}

			}
		}

		for (IResource r : getDesignTimeResources()) {
			buf.append("         <additionalResource>\n");
			try {
				buf.append(URLEncoder.encode(r.getXml(), "UTF-8") + ">\n");
			} catch (UnsupportedEncodingException e1) {
				
				e1.printStackTrace();
			}
			buf.append("         </additionalResource>\n");
		}
		
		if(relationStrategies != null) {
			for(RelationStrategy strategy : relationStrategies) {
				buf.append("		<relationStrategy>"+strategy.getName()+"</relationStrategy>");
			}
		}

		buf.append("     </sqlQuery>\n");
		buf.append("</freeMetaDataQuery>\n");
		return buf.toString();
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setLimit(String limit) {
		try {
			this.limit = Integer.parseInt(limit);
		} catch (Exception e) {

		}
	}

	public int getLimit() {
		return limit;
	}

	/**
	 * @return the formulas
	 */
	public List<Formula> getFormulas() {
		return formulas;
	}

	/**
	 * @param formulas
	 *            the formulas to set
	 */
	public void setFormulas(List<Formula> formulas) {
		this.formulas = formulas;
	}

	public List<IDataStreamElement> getIndexableDataStreamElement() {
		List<IDataStreamElement> res = new ArrayList<IDataStreamElement>();
		for (IDataStreamElement d : this.select) {
			if (d.isIndexable()) {
				res.add(d);
			}
		}
		return res;
	}

	public void setRelationStrategies(List<RelationStrategy> relationStrategies) {
		this.relationStrategies = relationStrategies;
	}

	public List<RelationStrategy> getRelationStrategies() {
		return relationStrategies;
	}

}
