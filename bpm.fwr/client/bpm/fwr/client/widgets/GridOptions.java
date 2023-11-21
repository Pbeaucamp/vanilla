package bpm.fwr.client.widgets;

public class GridOptions {
	
	private int nbCols;
	private int nbRows;
	
	public GridOptions() { }
	
	public GridOptions(int nbCols, int nbRows) {
		this.setNbCols(nbCols);
		this.setNbRows(nbRows);
	}

	public void setNbCols(int nbCols) {
		this.nbCols = nbCols;
	}

	public int getNbCols() {
		return nbCols;
	}

	public void setNbRows(int nbRows) {
		this.nbRows = nbRows;
	}

	public int getNbRows() {
		return nbRows;
	}
}
