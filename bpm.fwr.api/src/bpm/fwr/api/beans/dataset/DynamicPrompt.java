package bpm.fwr.api.beans.dataset;

@SuppressWarnings("serial")
public class DynamicPrompt extends FwrPrompt {
	
	private Column column;
	
	public DynamicPrompt() {
		super();
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	public Column getColumn() {
		return column;
	}
}
