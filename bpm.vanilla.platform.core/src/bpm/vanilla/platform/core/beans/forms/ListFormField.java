package bpm.vanilla.platform.core.beans.forms;

import bpm.vanilla.platform.core.beans.data.DataColumn;
import bpm.vanilla.platform.core.beans.data.Dataset;
import bpm.vanilla.platform.core.beans.data.Datasource;

public class ListFormField extends FormField {

	private static final long serialVersionUID = 1L;

	private Datasource datasource;
	private Dataset dataset;
	private DataColumn columnKey;
	private DataColumn columnValue;
	
	public ListFormField() {}

	public ListFormField(TypeField listbox) {
		super(listbox);
	}
	
	public ListFormField(FormField field) {
		super(field);
	}

	public Datasource getDatasource() {
		return datasource;
	}

	public void setDatasource(Datasource datasource) {
		this.datasource = datasource;
	}

	public Dataset getDataset() {
		return dataset;
	}

	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}

	public DataColumn getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(DataColumn columnKey) {
		this.columnKey = columnKey;
	}

	public DataColumn getColumnValue() {
		return columnValue;
	}

	public void setColumnValue(DataColumn columnValue) {
		this.columnValue = columnValue;
	}

}
