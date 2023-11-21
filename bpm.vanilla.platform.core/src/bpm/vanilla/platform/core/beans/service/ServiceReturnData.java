package bpm.vanilla.platform.core.beans.service;

public class ServiceReturnData {

	private int id;
	private String name;
	private int type;

	private Object value;
	private String formula;
	private ServiceTransformationDefinition definition;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public ServiceTransformationDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ServiceTransformationDefinition definition) {
		this.definition = definition;
	}

}
