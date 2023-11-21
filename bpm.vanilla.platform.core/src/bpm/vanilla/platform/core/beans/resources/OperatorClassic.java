package bpm.vanilla.platform.core.beans.resources;

import java.util.HashMap;
import java.util.Map;

public enum OperatorClassic {
	INF(0, "<"),
	INF_OR_EQUAL(1, "<="),
	EQUAL(2, "=="),
	SUP_OR_EQUAL(3, ">="),
	SUP(4, ">"),
	NOT_EQUAL(5, "!="),
	IN(6, "IN"),
	CONTAINS(7, "CONTAINS"),
	REGEX(8, "REGEX");
	
	private int type;
	private String label;

	private static Map<Integer, OperatorClassic> map = new HashMap<Integer, OperatorClassic>();
	static {
        for (OperatorClassic type : OperatorClassic.values()) {
            map.put(type.getType(), type);
        }
    }
	
	private OperatorClassic(int type, String label) {
		this.type = type;
		this.label = label;
	}
	
	public int getType() {
		return type;
	}
	
	public String getlabel() {
		return label;
	}
	
	public static OperatorClassic valueOf(int type) {
        return map.get(type);
    }
	
	@Override
	public String toString() {
		return getlabel();
	}
}