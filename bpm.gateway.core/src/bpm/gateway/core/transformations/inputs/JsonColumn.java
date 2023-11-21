package bpm.gateway.core.transformations.inputs;

import com.fasterxml.jackson.databind.node.JsonNodeType;

public class JsonColumn {
	
	private String name;
	private int depth;
	private JsonNodeType type;
	
	public JsonColumn(String name, int depth, JsonNodeType type) {
		this.name = name;
		this.depth = depth;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public JsonNodeType getType() {
		return type;
	}

	public void verifyType(JsonColumn newColumn) {
		//If the type is different, we put a string as value to avoid errors
		if (!getType().equals(newColumn.getType())) {
			this.type = JsonNodeType.STRING;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JsonColumn) {
			return ((JsonColumn) obj).getName().equals(getName()) 
					&& ((JsonColumn) obj).getDepth() == getDepth();
		}
		return super.equals(obj);
	}

	public String getGatewayType() {
		switch (type) {
		case BOOLEAN:
			return Boolean.class.getName();
		case NUMBER:
			return Double.class.getName();
		case STRING:
		case NULL:
		case MISSING:
		case OBJECT:
		case POJO:
		case ARRAY:
		case BINARY:
			return String.class.getName();
		default:
			break;
		}
		return String.class.getName();
	}
}