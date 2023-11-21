package bpm.vanillahub.core.beans.activities;

import java.util.HashMap;
import java.util.Map;

public enum TypeOpenData {
	D4C(0),
	CKAN(1),
	DATA_GOUV(2),
	ODS(3);

	private int type;

	private static Map<Integer, TypeOpenData> map = new HashMap<Integer, TypeOpenData>();
	static {
		for (TypeOpenData type : TypeOpenData.values()) {
			map.put(type.getType(), type);
		}
	}

	private TypeOpenData(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static TypeOpenData valueOf(int type) {
		return map.get(type);
	}
}