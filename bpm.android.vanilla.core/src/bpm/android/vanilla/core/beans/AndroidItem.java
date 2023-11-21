package bpm.android.vanilla.core.beans;

import java.util.List;

public class AndroidItem extends AndroidObject {

	private static final long serialVersionUID = 1L;
	
	private List<Parameter> parameters;
	
	public AndroidItem() { }
	
	public AndroidItem(int id, String name, int type, int subType, List<Parameter> parameters) {
		super(id, name, type, subType);
		this.parameters = parameters;
	}

	public boolean hasParameters() {
		return parameters != null && !parameters.isEmpty();
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
}
