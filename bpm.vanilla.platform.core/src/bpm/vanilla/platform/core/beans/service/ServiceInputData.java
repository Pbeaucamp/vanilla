package bpm.vanilla.platform.core.beans.service;

public class ServiceInputData implements IService {

	private int id;
	private String name;
	private int type;
	private String value;
	
	private ServiceTransformationDefinition definition;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public ServiceTransformationDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ServiceTransformationDefinition definition) {
		this.definition = definition;
	}

	@Override
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}


}
