package bpm.metadata.misc;

import java.util.HashMap;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.physical.IColumn;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;

public class MetaDataChecker {

	public static HashMap<Object, String> checkModel(MetaData model){
		HashMap<Object, String> errorItems = new HashMap<Object, String>();
		
		for(AbstractDataSource ds  : model.getDataSources()){
			if (! (ds instanceof SQLDataSource)){
				continue;
			}
			
			for(IDataStream st : ((SQLDataSource)ds).getDataStreams()){
				if (st.getOrigin() == null){
					
					errorItems.put(st, "The physical table " + st.getOriginName() + " is not found in the Database");
				}
				for(IDataStreamElement el : st.getElements()){
					if (!(el instanceof ICalculatedElement)){
						if (el.getOrigin() == null){
							errorItems.put(el, "The physical column " + el.getOriginName() + " is not found in the Physical Table");
						}
					}
				}
			}
			
			for(Relation r : ((SQLDataSource)ds).getRelations()){
				String s = checkRelation(r);
				if (s != null){
					errorItems.put(r, s);
				}
			}
			
			for(IBusinessModel b : model.getBusinessModels()){
				for(IBusinessTable t :  ((BusinessModel)b).getBusinessTables()){
					for(IDataStreamElement e : t.getColumns("none")){
						if (e.getDataStream() == null){
							errorItems.put(t, "DataStreamElement " + e.getName() + " is orphan");
						}
						else if (!(e instanceof ICalculatedElement) && e.getOrigin() == null){
							errorItems.put(t, "DataStreamElement " + e.getName() + " has no origin found in the DataSource");
						}
						else {
							
							try {
								IDataStream datas = ds.getDataStreamNamed(e.getDataStream().getName());
								boolean finded = false;
								for(IColumn col : datas.getOrigin().getColumns()) {
									if(col.getName().equals(e.getOrigin().getName())) {
										finded = true;
										break;
										
									}
								}
								if(!finded) {
									errorItems.put(t, "DataStreamElement " + e.getName() + " has no origin found in the DataSource");
								}
							} catch(Exception e1) {
								errorItems.put(t, "DataStreamElement " + e.getName() + " has no origin found in the DataSource");
							}
						}
					}
				}
				for(Relation r : ((BusinessModel)b).getRelations()){
					String s = checkRelation(r);
					if (s != null){
						errorItems.put(r, s);
					}
				}
			}
			
			for(IResource r : model.getResources()){
				if (r instanceof IFilter){
					if (((IFilter)r).getOrigin() == null){
						errorItems.put(r, "Resource " + r.getName() + " has no DataStreamElement Target");
					}
					else if (((IFilter)r).getOrigin().getOrigin() == null){
						errorItems.put(r, "Resource " + r.getName() + " has no origin for its DataStreamElement Target");
					}
					else {
						IDataStream datas = ds.getDataStreamNamed(((IFilter)r).getOrigin().getDataStream().getName());
						
						boolean finded = false;
						for(IColumn col : datas.getOrigin().getColumns()) {
							if(col.getName().equals(((IFilter)r).getOrigin().getName())) {
								finded = true;
								break;
								
							}
						}
						if(!finded) {
							errorItems.put(r, "DataStreamElement " + r.getName() + " has no origin for its DataStreamElement Target");
						}
					}
				}
			}
		}
		
		return errorItems;
		
	}
	
	private static String checkRelation(Relation r){
		if (r.getLeftTable() == null){
			return "No Left Table defined";
		}
		
		if (r.getRightTable() == null){
			return "No Right Table defined";
		}
		
		for(Join j : r.getJoins()){
			if (j.getLeftElement() == null){
				return "A join as no left Element defined";
			}
			if (j.getRightElement() == null){
				return "A join as no right Element defined";
			}
		}
		return null;
		
	}
}
