package bpm.fwr.api.beans.components;

import java.util.List;

import bpm.fwr.api.beans.dataset.Column;
import bpm.fwr.api.beans.dataset.DataSet;

public interface IChart {

	public String getChartTitle();

	public Column getColumnGroup();
	
	public List<Column> getColumnDetails();

	public DataSet getDataset();

	public void setColumnDetails(List<Column> detail);

	public void setChartType(ChartType chartType);

	public void setColumnGroup(Column column);

	public void setDataset(DataSet dataset);
	
	public int getX();

	public int getY();
	
	public void setX(int x);

	public void setY(int y);
}
