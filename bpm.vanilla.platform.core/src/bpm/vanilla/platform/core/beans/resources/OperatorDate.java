package bpm.vanilla.platform.core.beans.resources;

import java.util.HashMap;
import java.util.Map;

public enum OperatorDate {
	BEFORE(0),
	EQUAL(1),
	AFTER(2),
	BEFORE_OR_EQUAL(3),
	AFTER_OR_EQUAL(4);
	
	private int type;

	private static Map<Integer, OperatorDate> map = new HashMap<Integer, OperatorDate>();
	static {
        for (OperatorDate type : OperatorDate.values()) {
            map.put(type.getType(), type);
        }
    }
	
	private OperatorDate(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	public static OperatorDate valueOf(int type) {
        return map.get(type);
    }
}