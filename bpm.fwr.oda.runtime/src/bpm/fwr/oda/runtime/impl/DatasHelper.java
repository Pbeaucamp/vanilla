package bpm.fwr.oda.runtime.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.metadata.layer.business.GrantException;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IRepositoryApi;


public class DatasHelper {

	private static DatasHelper instance;
	
	private DatasHelper() {
		super();
	}
	
	public static DatasHelper getInstance() {
		if (instance == null) {
			instance = new DatasHelper();
		}
		return instance;
	}
	
	public IBusinessTable lookUnder(IBusinessTable table, String tableName, String groupName) {		
		IBusinessTable res = null;
		
		List<IBusinessTable> childs = table.getChilds(groupName);
		
		for (Object o : childs) {
			IBusinessTable t = (IBusinessTable) o;
			if (t.getName().equalsIgnoreCase(tableName)) {
				res = t;
			}
			if (res == null){
				return lookUnder(t, tableName, groupName);
			}
		}
		
		
		return res;
	}
	
	public String buildQuery(DataSet ds, IRepositoryApi sock) {
		IBusinessPackage pckg = null;
		try {
			IBusinessModel model = bpm.fwr.api.birt.DatasHelper.getSelectedModel(sock, ds.getDatasource());
			pckg = model.getBusinessPackage(ds.getDatasource().getBusinessPackage(), ds.getDatasource().getGroup());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//find all the IDataStreamElements
		List<Column> columns = ds.getColumns();
		
		List<IDataStreamElement> iDataStreamElements = new ArrayList<IDataStreamElement>();
		for(Column col : columns) {
			if(!col.isCalculated()) {
				String tName = col.getBusinessTableParent();
				IBusinessTable t = pckg.getBusinessTable(ds.getDatasource().getGroup(), tName);
				if (t == null) {
					Collection<IBusinessTable> tables = pckg.getBusinessTables(ds.getDatasource().getGroup());
					Iterator<IBusinessTable> it = tables.iterator();
					while (it.hasNext() && t == null) {
						IBusinessTable table = it.next();
						t = lookUnder(table, tName, ds.getDatasource().getGroup());
					}
				}
				IDataStreamElement d;
				try {
					d = (IDataStreamElement) t.getColumn(ds.getDatasource().getGroup(), col.getBasicName());
					iDataStreamElements.add(d);
				} catch (GrantException e) {
					e.printStackTrace();
				}
			}
		}
		
		//add filters
		List<IFilter> filters = new ArrayList<IFilter>();
		filters = null;
		
		//add prompts
		List<Prompt> prompts = new ArrayList<Prompt>();
		prompts = null;
		
		//get the query
		IQuery iq = SqlQueryBuilder.getQuery(ds.getDatasource().getGroup(), iDataStreamElements, null, null, null, filters, prompts);
		if (ds.getDatasource().getConnectionName() == null || "Default".equalsIgnoreCase(ds.getDatasource().getConnectionName()) || ds.getDatasource().getConnectionName().trim().equalsIgnoreCase("")) {
			ds.getDatasource().setConnectionName(pckg.getConnectionsNames(ds.getDatasource().getGroup()).get(0));
		}
		return ((QuerySql) iq).getXml();
	}
}
