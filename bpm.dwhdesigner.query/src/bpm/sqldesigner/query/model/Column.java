package bpm.sqldesigner.query.model;

public class Column extends Node {

	private String type;
	private boolean isKey = false;
	private boolean isSelected = false; // Used to save checkbox status when
										// column is removed
	private boolean filtred=false;
	public static final String FILTRED = "ColumnFiltred";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setFiltred(boolean b) {
		filtred = b;
		String s = Boolean.toString(b);
		getListeners().firePropertyChange(FILTRED, null, s);
	}

}
