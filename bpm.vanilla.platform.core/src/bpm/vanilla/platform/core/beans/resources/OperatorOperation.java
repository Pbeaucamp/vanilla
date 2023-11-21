package bpm.vanilla.platform.core.beans.resources;

import java.util.HashMap;
import java.util.Map;

public enum OperatorOperation {
	SUM(0, "+"),
	MULTIPLY(1, "*");
//	DIVIDE(2, "%"),
//	SUBTRACT(3, "%");
	
	private int type;
	private String label;

	private static Map<Integer, OperatorOperation> map = new HashMap<Integer, OperatorOperation>();
	static {
        for (OperatorOperation type : OperatorOperation.values()) {
            map.put(type.getType(), type);
        }
    }
	
	private OperatorOperation(int type, String label) {
		this.type = type;
		this.label = label;
	}
	
	public int getType() {
		return type;
	}
	
	public String getlabel() {
		return label;
	}
	
	public static OperatorOperation valueOf(int type) {
        return map.get(type);
    }
	
	@Override
	public String toString() {
		return getlabel();
	}
}