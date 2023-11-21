package bpm.fd.core.component;

import bpm.fd.core.IComponentOption;

public class DataVizOption implements IComponentOption {

	private static final long serialVersionUID = 1L;

	private boolean showMap;
	private boolean showViz;
	private boolean showTable;

	public boolean isShowMap() {
		return showMap;
	}

	public void setShowMap(boolean showMap) {
		this.showMap = showMap;
	}

	public boolean isShowViz() {
		return showViz;
	}

	public void setShowViz(boolean showViz) {
		this.showViz = showViz;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

}
