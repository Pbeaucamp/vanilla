package bpm.fwr.api.beans.components;

import java.io.Serializable;

import bpm.fwr.api.beans.dataset.DataSet;

public class ReportComponent implements IReportComponent, Serializable {

	private int x;
	private int y;
	private int rowCount;
	private int colCount;
	
	private DataSet dataset;
	
	public int getColCount() {
		return this.colCount;
	}

	public IReportComponent getComponent() {
		return this;
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public void setColCount(int col) {
		this.colCount = col;
	}

	public void setRowCount(int row) {
		this.rowCount = row;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public DataSet getDataset() {
		return dataset;
	}

	public void setDataset(DataSet dataset) {
		this.dataset = dataset;
	}

	public void setX(String x) {
		this.x = Integer.parseInt(x);
	}
	
	public void setY(String y) {
		this.y = Integer.parseInt(y);
	}
	
	public void setRowCount(String rows) {
		this.rowCount = Integer.parseInt(rows);
	}
	
	public void setColCount(String cols) {
		this.colCount = Integer.parseInt(cols);
	}
}
