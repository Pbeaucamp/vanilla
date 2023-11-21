package bpm.fwr.api.beans.components;

import bpm.fwr.api.beans.dataset.DataSet;


/**
 * Interface for all kind of report component.
 * 
 * @author Marco
 *
 */
public interface IReportComponent {
	
	public IReportComponent getComponent();

	/**
	 * 
	 * @return the component start row
	 */
	public int getX();
	
	/**
	 * 
	 * @return the component start column
	 */
	public int getY();
	
	/**
	 * 
	 * @return the number of rows for this component
	 */
	public int getRowCount();
	
	/**
	 * 
	 * @return the number of columns for this component
	 */
	public int getColCount();
	
	/**
	 * 
	 * @param x the component start row
	 */
	public void setX(int x);
	
	/**
	 * 
	 * @param y the component start column
	 */
	public void setY(int y);
	
	/**
	 * 
	 * @param row the number of rows for this component
	 */
	public void setRowCount(int row);
	
	/**
	 * 
	 * @param col the number of columns for this component
	 */
	public void setColCount(int col);
	
	public void setDataset(DataSet dataset);
	
	public DataSet getDataset();
}
