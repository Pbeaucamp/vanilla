package bpm.fd.runtime.model.controler;

import java.util.HashMap;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IQuery;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.datas.QueryHelper;
import bpm.fd.api.core.model.datas.odaconsumer.ConnectionManager;
import bpm.vanilla.platform.core.beans.Group;

public class QueryProvider {
	private HashMap<DataSet, DataSource> datasets = new HashMap<DataSet, DataSource>();
	private FdModel model;
	
	public QueryProvider(FdModel model){
		
		this.model = model;
		
		for(IComponentDefinition d : model.getProject().getComponents().keySet()){
			if (d.getDatas() != null && d.getDatas().getDataSet() != null){
				datasets.put(d.getDatas().getDataSet(), model.getProject().getDictionary().getDatasource(d.getDatas().getDataSet().getDataSourceName()));
			}
		}
		
	}
	
	public DataSource getDatasource(DataSet dataSet) throws Exception{
		try {
			return datasets.get(dataSet);
		} catch(Exception e) {
			return model.getProject().getDictionary().getDatasource(dataSet.getDataSourceName());
		}
	}
	
	public IQuery getQuery(Group group, DataSet dataSet) throws Exception{
		
		if (dataSet == null){
			return null;
		}
		
		
		IQuery query = null;
		try {
			query = QueryHelper.buildquery(datasets.get(dataSet), dataSet, group);
		} catch (Exception e) {
			query = QueryHelper.buildquery(model.getProject().getDictionary().getDatasource(dataSet.getDataSourceName()), dataSet, group);
		}
//		query.setAppContext(group);
//		query.prepare(dataSet.getQueryText());
		return query;
	}

	public IQuery getNewQuery(Group group, DataSet dataSet) throws Exception {
		
		if (dataSet == null){
			return null;
		}
		
		IConnection c = ConnectionManager.openConnection(datasets.get(dataSet)).getOdaConnection();
		c.close();
		
		IQuery query = QueryHelper.buildquery(datasets.get(dataSet), dataSet);
		query.setAppContext(group);
		query.prepare(dataSet.getQueryText());
		return query;
	}
}
