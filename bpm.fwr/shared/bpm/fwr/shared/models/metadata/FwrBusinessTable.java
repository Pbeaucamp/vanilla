package bpm.fwr.shared.models.metadata;

import java.util.ArrayList;
import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FwrBusinessTable extends FwrBusinessObject implements IsSerializable {
	private List<FwrBusinessTable> childs = new ArrayList<FwrBusinessTable>();

	private DataSet parent;
	
	private List<Column> columns;
	
	public FwrBusinessTable(){ 
		super();
	}
	
	public FwrBusinessTable(String name, String description, List<Column> l) {
		super(name, description);
		this.columns = l;
	}

	public List<FwrBusinessTable> getChilds() {
		return childs;
	}

	public void setChilds(List<FwrBusinessTable> childs) {
		this.childs = childs;
	}

	public void addChild(FwrBusinessTable table) {
		this.childs.add(table);	
	}

	public void setParent(DataSet parent) {
		this.parent = parent;
	}

	public DataSet getParent() {
		return parent;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Column> getColumns() {
		return columns;
	}
}
