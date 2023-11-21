package bpm.metadata.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.fa.api.olap.OLAPQuery;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.MetaDataException;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.layer.physical.olap.UnitedOlapLevelColumn;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.Prompt;
import bpm.metadata.tools.Log;
import bpm.vanilla.platform.core.IVanillaContext;

public class UnitedOlapQueryGenerator {

	public static EffectiveQuery getQuery(Integer groupFmdtWeight, IVanillaContext vanillaCtx, IBusinessPackage businessPack, UnitedOlapQuery query, String groupName, boolean dynamic, HashMap<Prompt, List<String>> prompts) throws Exception {
		
		checkIfQueryValid(query);
		
		return generateMdxQuery(groupFmdtWeight, vanillaCtx, businessPack, query, groupName, prompts);
	}

	private static EffectiveQuery generateMdxQuery(Integer groupFmdtWeight, IVanillaContext vanillaCtx, IBusinessPackage businessPack, UnitedOlapQuery query, String groupName, HashMap<Prompt, List<String>> prompts) throws Exception {
		
		UnitedOlapDatasource ds = (UnitedOlapDatasource) businessPack.getDataSources(groupName).get(0);
		UnitedOlapConnection con = (UnitedOlapConnection) ds.getConnection();
		
		OLAPQuery faQuery = new OLAPQuery(con.getCube().getDimensions(), con.getCube());
		faQuery.setShowEmpty(!query.isHideNull());
		
		List<IFilter> filters = query.getFilters();
		if(filters == null) {
			filters = new ArrayList<IFilter>();
		}
		
		List<IDataStream> involvedTables = new ArrayList<IDataStream>();
		for(IDataStreamElement elem : query.getSelect()) {
			if(!involvedTables.contains(elem.getDataStream())) {
				involvedTables.add(elem.getDataStream());
			}
		}
		
		//Get back table filters for group
		List<IFilter> selectFilters = new ArrayList<IFilter>();
		for(IBusinessTable table : businessPack.getBusinessTables(groupName)) {
			List<IFilter> tableFilters = table.getFilterFor(groupName);
			for(IFilter f : tableFilters) {
				if(involvedTables.contains(f.getOrigin().getDataStream())) {
					selectFilters.add(f);
				}
				else {
					Log.warning("Security filter : " + f.getName() + " ignored because table : " + table.getName() + " is not used in the query.");
				}
			}
		}

		for(IDataStream table : involvedTables) {
			List<IFilter> tfilters = table.getFilterFor(groupName);
			for(IFilter f : tfilters) {
				if(involvedTables.contains(f.getOrigin().getDataStream())) {
					selectFilters.add(f);
				}
				else {
					Log.warning("Security filter : " + f.getName() + " ignored because table : " + table.getName() + " is not used in the query.");
				}
			}
			tfilters = table.getGenericFilters();
			for(IFilter f : tfilters) {
				if(involvedTables.contains(f.getOrigin().getDataStream())) {
					selectFilters.add(f);
				}
				else {
					Log.warning("Generic filter : " + f.getName() + " ignored because table : " + table.getName() + " is not used in the query.");
				}
			}
		}
		
		List<Prompt> promptFilterSelect = new ArrayList<Prompt>();
		
		//add prompts values
		if(prompts != null && prompts.size() > 0) {
			
			LOOK:for(Prompt pr : prompts.keySet()) {
				
				for(IDataStream str : involvedTables) {
					if(pr.getOrigin().getDataStream() == str) {
						promptFilterSelect.add(pr);
						continue LOOK;
					}
				}
				
				List<String> values = pr.getOrigin().getOrigin().getValues();
				for(String val : prompts.get(pr)) {
					
					for(String v : values) {
						String cleanedUname = cutUselessUnameParts(v, ((UnitedOlapLevelColumn)pr.getOrigin().getOrigin()).getLevelIndex());
						
						if(cleanedUname.equals(val)) { 
						
							if(!faQuery.getWhere().contains(v)) {
								faQuery.addWhere(v);
							}
						}
					}
				}
			}

		}
		
		
		
		
		//filter the select part
		List<IDataStreamElement> selects = new ArrayList<IDataStreamElement>(query.getSelect());
		for(IFilter fi : selectFilters) {
			if(fi instanceof Filter) {
				Filter filter = (Filter) fi;
				
				//look if the datastreamelement is in the query select
				//if it is, remove it
				IDataStreamElement select = null;
				for(IDataStreamElement s : selects) {
					if(s.getName().equals(filter.getOrigin().getName())) {
						select = s;
						break;
					}
				}
				if(select != null) {
					selects.remove(select);
				}
				
				//add the select filter values
				for(String val : filter.getValues()) {
					if(val.startsWith("[Measures]")) {
						faQuery.addrow(val);
					}
					else {
						faQuery.addcol(val);
					}
				}
			}
		}
		
		//filter the select part with prompts
		for(Prompt pr : promptFilterSelect) {
			//look if the datastreamelement is in the query select
			//if it is, remove it
			IDataStreamElement select = null;
			for(IDataStreamElement s : selects) {
				if(s.getName().equals(pr.getOrigin().getName())) {
					select = s;
					break;
				}
			}
			if(select != null) {
				selects.remove(select);
			}
			
			//add the select filter values
			
			List<String> values = pr.getOrigin().getOrigin().getValues();
			for(String val : prompts.get(pr)) {
				
				for(String v : values) {
					String cleanedUname = cutUselessUnameParts(v, ((UnitedOlapLevelColumn)pr.getOrigin().getOrigin()).getLevelIndex());
					
					if(cleanedUname.equals(val)) { 
					
						if(v.startsWith("[Measures]")) {
							faQuery.addrow(v);
						}
						else {
							faQuery.addcol(v);
						}
					}
				}
			}
		}
		
		//Select elements
		for(IDataStreamElement s : selects) {
			String uname = null;
			if(s.getOrigin() != null) {
				uname = s.getOrigin().getName();
			}
			else {
				uname = s.getOriginName();
			}
			if(uname.startsWith("[Measures]")) {
				faQuery.addrow(uname);
			}
			else {
				faQuery.addcol(uname + ".members");
			}
		}
		
		//add where filters
		for(IFilter filter : filters) {
			
			if(filter instanceof Filter) {
				Filter f = (Filter) filter;
				for(String val : f.getValues()) {
					if(!faQuery.getWhere().contains(val)) {
						faQuery.addWhere(val);
					}
				}
			}
			
		}
		

		
		faQuery.setCubeName(con.getCubeName());
		String queryMdx = faQuery.getMDX();
		
		int totalWeight = 0;
		for(IDataStream t : involvedTables){
			totalWeight += t.getWeight();
		}
		if (groupFmdtWeight != null){
			if (groupFmdtWeight < totalWeight){
				throw new FmdtQueryRuntimeException("The FMDT Query Weight is superior to the Vanilla Group Weight. It cannot be executed.\n The VanillaGroup Weight should be at least " + (totalWeight + 1) + " for the Group " + groupName, queryMdx, totalWeight);
			}
		}
		
		return new EffectiveQuery(queryMdx,totalWeight);
	}

	private static String cutUselessUnameParts(String uname, int levelIndex) {
		String[] parts = uname.split("\\]\\.\\[");
		
		String searched = parts[levelIndex + 2].replace("]", "");
		
		return searched;
	}

	private static void checkIfQueryValid(UnitedOlapQuery query) throws MetaDataException {
		if(query.getSelect().size() < 2) {
			throw new MetaDataException("You have to select at least one dimension and one measure for a query");
		}
	}
	
}
