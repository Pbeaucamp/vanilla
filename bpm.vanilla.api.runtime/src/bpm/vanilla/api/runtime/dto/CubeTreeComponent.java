package bpm.vanilla.api.runtime.dto;

import java.util.List;

public abstract class CubeTreeComponent {

	protected String id;
	protected String name;
	protected String type;
	protected List<CubeTreeComponent> children;

	public CubeTreeComponent(String _id, String _name, String _type, Object olapObj) {
		id = _id;
		name = _name;
		type = _type;
		children = loadChildren(olapObj);
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public List<CubeTreeComponent> getChildren() {
		return children;
	}

	public abstract List<CubeTreeComponent> loadChildren(Object obj);

}
