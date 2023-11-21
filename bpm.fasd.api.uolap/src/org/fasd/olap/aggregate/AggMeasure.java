package org.fasd.olap.aggregate;

import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.OLAPMeasure;

public class AggMeasure{
	private OLAPMeasure mes;
	private DataObjectItem column;
	private String measureId;
	private String columnId;
	
	public DataObjectItem getColumn() {
		return column;
	}
	public void setColumn(DataObjectItem column) {
		this.column = column;
	}
	public String getMeasureId() {
		return measureId;
	}
	public void setMeasureId(String measureId) {
		this.measureId = measureId;
	}
	public OLAPMeasure getMes() {
		return mes;
	}
	public void setMes(OLAPMeasure mes) {
		this.mes = mes;
	}
	public String getColumnId() {
		return columnId;
	}
	
	public void setColumnId(String id){
		columnId = id;
	}
	
}
