package bpm.vanilla.platform.core.beans.resources;

import java.util.HashMap;
import java.util.Map;

public enum OperatorListe {
	IN(0), OUT(1);

	private int type;

	private static Map<Integer, OperatorListe> map = new HashMap<Integer, OperatorListe>();
	static {
		for (OperatorListe type : OperatorListe.values()) {
			map.put(type.getType(), type);
		}
	}

	private OperatorListe(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static OperatorListe valueOf(int type) {
		return map.get(type);
	}
}