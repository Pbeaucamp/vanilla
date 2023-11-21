package bpm.united.olap.api.runtime.calculation;

import java.io.Serializable;
import java.text.ParseException;

import bpm.united.olap.api.datasource.DataObjectItem;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.runtime.DataCell;


public interface ICalculation extends Serializable {
	
	void makeCalcul();
	
	Measure getMeasure();
	
	void makeCalculDuringQuery(boolean isOnImprovedQuery) throws ParseException;
	
	DataCell getDataCell();
	
	void setIsPercentile(boolean isPercentile);
	
	boolean isPercentile();
	
	void setItem(DataObjectItem item);
}
