package bpm.vanilla.platform.core.beans.resources;

import java.util.ArrayList;
import java.util.List;

public class ListOfValues extends Resource {

	private List<String> values;

	public ListOfValues() {
		super("", TypeResource.LOV);
	}

	public ListOfValues(String name) {
		super(name, TypeResource.LOV);
	}

	public List<String> getValues() {
		return values;
	}

//	public void addValue(String value) {
//		if (values == null) {
//			this.values = new ArrayList<>();
//		}
//		this.values.add(value);
//	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}

	public void updateInfo(String name, List<String> values) {
		setName(name);
		this.values = values;
	}

	@Override
	public List<Variable> getVariables() {
		return new ArrayList<Variable>();
	}

	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}
}
