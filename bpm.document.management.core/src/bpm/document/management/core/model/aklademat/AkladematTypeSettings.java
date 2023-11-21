package bpm.document.management.core.model.aklademat;

import java.util.HashMap;
import java.util.Map;

public enum AkladematTypeSettings {
	IPARAPHEUR(0), CHORUS(1);

	private int type;

	private static Map<Integer, AkladematTypeSettings> map = new HashMap<Integer, AkladematTypeSettings>();
	static {
		for (AkladematTypeSettings type : AkladematTypeSettings.values()) {
			map.put(type.getType(), type);
		}
	}

	private AkladematTypeSettings(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static AkladematTypeSettings valueOf(int type) {
		return map.get(type);
	}
}
