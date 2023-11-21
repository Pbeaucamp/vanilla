package bpm.document.management.core.model;

import java.util.HashMap;
import java.util.Map;


public enum TypeProcess {
	UPLOAD_DOCUMENTS(0),
	CHECKIN(1),
	UPLOAD_ONE_DOCUMENT(2),
	//We only use checkin for now, so you need to implement it, if you need it
	ADD_NEW_VERSION(3);

	private int type;

	private static Map<Integer, TypeProcess> map = new HashMap<Integer, TypeProcess>();
	static {
		for (TypeProcess processType : TypeProcess.values()) {
			map.put(processType.getType(), processType);
		}
	}

	private TypeProcess(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static TypeProcess valueOf(int processType) {
		return map.get(processType);
	}
}