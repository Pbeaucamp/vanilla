package bpm.vanilla.platform.core.beans.resources;

import java.util.HashMap;
import java.util.Map;

public enum LimeSurveyType {
	LIMESURVEY(0),
	LIMESURVEY_SHAPES(1),
	LIMESURVEY_VMAP(2);

	private int type;

	private static Map<Integer, LimeSurveyType> map = new HashMap<Integer, LimeSurveyType>();
	static {
		for (LimeSurveyType limeSurveyType : LimeSurveyType.values()) {
			map.put(limeSurveyType.getType(), limeSurveyType);
		}
	}

	private LimeSurveyType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public static LimeSurveyType valueOf(int limeSurveyType) {
		return map.get(limeSurveyType);
	}
}

