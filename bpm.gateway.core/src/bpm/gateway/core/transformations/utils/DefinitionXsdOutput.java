package bpm.gateway.core.transformations.utils;

public class DefinitionXsdOutput extends DefinitionXSD {

	private String inputColumn;
	private String parentColumn;
	private String childColumn;
	
	public DefinitionXsdOutput() {}

	public DefinitionXsdOutput(DefinitionXSD parent, String name, String propertyName) {
		super(parent, name, propertyName, false);
	}

	public String getInputColumn() {
		return inputColumn;
	}

	public void setInputColumn(String inputColumn) {
		this.inputColumn = inputColumn;
	}

	public String getParentColumn() {
		return parentColumn;
	}

	public void setParentColumn(String parentColumn) {
		this.parentColumn = parentColumn;
	}

	public String getChildColumn() {
		return childColumn;
	}

	public void setChildColumn(String childColumn) {
		this.childColumn = childColumn;
	}
	
	
}
