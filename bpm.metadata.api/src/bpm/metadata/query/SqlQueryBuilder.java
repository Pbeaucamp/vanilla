package bpm.metadata.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;

public class SqlQueryBuilder{

	
	public static IQuery getQuery(String groupName, List<IDataStreamElement> select,
			HashMap<ListOfValue, String> condition,
			List<AggregateFormula> aggs, List<Ordonable> orderBy,
			List<IFilter> filters, List<Prompt> prompts) {
		QuerySql query = new QuerySql();
		
		query.setGroupName(groupName);
		query.setSelect(select);
		query.setAggs(aggs);
		query.setCondition(condition);
		query.setOrderBy(orderBy);
		query.setFilters(filters);
		query.setPrompts(prompts);
		return query;
	}

	
	
	
	
	public static IQuery getQuery(String groupName, List<IDataStreamElement> select,
			HashMap<ListOfValue, String> condition,
			List<AggregateFormula> aggs, List<Ordonable> orderBy,
			List<IFilter> filters, List<Prompt> prompts, List<Formula> formulasMember, List<RelationStrategy> relationStrategies) {
		QuerySql query = new QuerySql();
		
		query.setGroupName(groupName);
		query.setSelect(select);
		query.setAggs(aggs);
		query.setCondition(condition);
		query.setOrderBy(orderBy);
		query.setFilters(filters);
		query.setPrompts(prompts);
		query.setRelationStrategies(relationStrategies);
		
		
//		List<IDataStreamElement> members = new ArrayList<IDataStreamElement>(select);
//		
//		for(String f : formulasMember){
//			members.add(generateCalculatedMember(groupName, f, pack));
//		}
		
		
		query.setFormulas(formulasMember);
		return query;
	}
	

	
//	private static ICalculatedElement generateCalculatedMember(String groupName, String formula, IBusinessPackage pack){
//		List<IDataStreamElement> columns = new ArrayList<IDataStreamElement>();
//		
//		for(IBusinessTable t : pack.getBusinessTables(groupName)){
//			columns.addAll(getAllColumns(groupName,t));
//		}
//		
//
//		String f = new String(formula);
//		for(IDataStreamElement e : columns){
//			if (e instanceof ICalculatedElement){
//				f = f.replace(e.getOuputName(Locale.getDefault()), ((ICalculatedElement)e).getFormula());
//			}
//			else{
//				f = f.replace(e.getOuputName(Locale.getDefault()), ((IDataStreamElement)e).getOrigin().getName());
//			}
//		}
//		
//		ICalculatedElement calc = new ICalculatedElement();
//		calc.setFormula(f);
//		calc.setGranted(groupName, true);
//		calc.setType("STRING");
//		return calc;
//		
//	}
//	
//	private static List<IDataStreamElement> getAllColumns(String groupName, IBusinessTable table){
//		List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
//		l.addAll(table.getColumns(groupName));
//		for(IBusinessTable t : table.getChilds(groupName)){
//			l.addAll(t.getColumns(groupName));
//		}
//		return l;
//		
//	}
}

