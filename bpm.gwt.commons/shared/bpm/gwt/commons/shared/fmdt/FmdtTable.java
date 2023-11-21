package bpm.gwt.commons.shared.fmdt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.vanilla.platform.core.beans.FmdtDrillerFilter;
import bpm.vanilla.platform.core.beans.fmdt.FmdtColumn;
import bpm.vanilla.platform.core.beans.fmdt.FmdtData;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FmdtTable extends FmdtObject implements IsSerializable {

	private List<FmdtRow> datas;
	private FmdtDatabaseInfoRow databaseColumns;
	private String databaseTable;
	
	private HashMap<String, List<String>> relatedTables;

	private String query;
	private String queryXml;

	private int nbDatas;
	private int actualPage = 1;

	private boolean group = false;

	private List<String> groupNames = new ArrayList<String>();
	private List<String> orderNames = new ArrayList<String>();
	private List<FmdtDrillerFilter> filters = new ArrayList<FmdtDrillerFilter>();
	
	private boolean editable;
	
	private List<FmdtData> columns = new ArrayList<FmdtData>();

	public FmdtTable(){
		
	}
	
	public FmdtTable(FmdtTable table){
		this.datas=new ArrayList<FmdtRow>(table.getDatas());
		this.databaseColumns=new FmdtDatabaseInfoRow();
		if(table.getDatabaseColumns().getColumnType()!=null)
				this.databaseColumns.setColumnType(new ArrayList<String>(table.getDatabaseColumns().getColumnType()));
		if(table.getDatabaseColumns().getOriginalValues()!=null)
			this.databaseColumns.setOriginalValues(new ArrayList<String>(table.getDatabaseColumns().getOriginalValues()));
		if(table.getDatabaseColumns().getValues()!=null)
			this.databaseColumns.setValues(new ArrayList<String>(table.getDatabaseColumns().getValues()));
		this.databaseColumns.setLevel(table.getDatabaseColumns().getLevel());
		this.databaseColumns.setType(table.getDatabaseColumns().getType());
		this.databaseColumns.setTypeRow(table.getDatabaseColumns().getTypeRow());
		this.databaseTable=new String(table.getDatabaseTable());
		if(table.getRelatedTables()!=null)
			this.relatedTables=new HashMap<String, List<String>>(table.getRelatedTables());
		else
			this.relatedTables=new HashMap<String, List<String>>();
		this.query=new String(table.getQuery());
		this.queryXml=new String(table.getQueryXml());
		this.nbDatas=table.getNbDatas();
		
		this.group=table.isGroup();
		if(table.getGroupNames()!=null)
			this.groupNames=new ArrayList<String>(table.getGroupNames());
		if(table.getOrderNames()!=null)
			this.orderNames=new ArrayList<String>(table.getOrderNames());
		if(table.getFilters()!=null)
			this.filters= new ArrayList<FmdtDrillerFilter>(table.getFilters());
		this.editable=table.isEditable();
		if(table.getColumns()!=null)
			this.columns=new ArrayList<FmdtData>(table.getColumns());
		
		this.setName(table.getName());
		
	}
	
	public List<String> getGroupNames() {
		return groupNames;
	}

	public void setGroupNames(List<String> groupNames) {
		this.groupNames = groupNames;
	}

	public boolean isGroup() {
		return group;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public List<FmdtRow> getDatas() {
		return datas;
	}

	public void setDatas(List<FmdtRow> datas) {
		this.datas = datas;
		this.nbDatas = datas.size();
	}

	public HashMap<String, List<String>> getRelatedTables() {
		return relatedTables;
	}

	public void setRelatedTables(HashMap<String, List<String>> relatedTables) {
		this.relatedTables = relatedTables;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQueryXml() {
		return queryXml;
	}

	public void setQueryXml(String queryXml) {
		this.queryXml = queryXml;
	}

	public int getNbDatas() {
		return nbDatas;
	}

	public int getActualPage() {
		return actualPage;
	}

	public void setActualPage(int actualPage) {
		this.actualPage = actualPage;
	}

	public void setOrderNames(List<String> orderNames) {
		this.orderNames = orderNames;
	}

	public List<String> getOrderNames() {
		return orderNames;
	}

	public void setFilters(List<FmdtDrillerFilter> filters) {
		this.filters = filters;
	}

	public List<FmdtDrillerFilter> getFilters() {
		return filters;
	}

	public void addFilter(FmdtDrillerFilter filter) {
		this.filters.add(filter);
	}

	public void removeFilter(FmdtDrillerFilter filter) {
		this.filters.remove(filter);
	}

	public void removeFilter(String name) {
		FmdtDrillerFilter toRm = null;
		for (FmdtDrillerFilter fil : this.filters) {
			if (fil.getName().equals(name)) {
				toRm = fil;
			}
		}
		this.filters.remove(toRm);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public FmdtDatabaseInfoRow getDatabaseColumns() {
		return databaseColumns;
	}

	public void setDatabaseColumns(FmdtDatabaseInfoRow databaseColumns) {
		this.databaseColumns = databaseColumns;
	}

	public String getDatabaseTable() {
		return databaseTable;
	}

	public void setDatabaseTable(String databaseTable) {
		this.databaseTable = databaseTable;
	}

	public List<FmdtData> getColumns() {
		return columns;
	}

	public void setColumns(List<FmdtData> columns) {
		this.columns = columns;
	}
	
	public void addColumn(FmdtColumn column){
		this.columns.add(column);
	}
	public void removeColumn(FmdtColumn column){
		this.columns.remove(column);
	}

}
